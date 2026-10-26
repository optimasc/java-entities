package com.optimasc.entity;

import java.util.Properties;

/** A factory that opens {@link Namespace} instances from
 *  configuration.
 *
 *  <p>A {@code NamespaceProvider} encapsulates the
 *  backend-specific logic required to connect to, authenticate
 *  with, and initialise a namespace &mdash; LDAP bind parameters,
 *  filesystem root paths, JDBC connection URLs, UPnP device UUIDs,
 *  and so on. Once a provider has produced a {@link Namespace},
 *  that namespace handle is fully configured and authenticated;
 *  its own methods take no further connection parameters.</p>
 *
 *  <p>This split between provider and namespace mirrors the
 *  factory/handle pattern used by JNDI ({@code InitialContextFactory}
 *  versus {@code Context}), JDBC ({@code Driver} versus
 *  {@code Connection}), and most pluggable backend SDKs. It keeps
 *  the per-call surface of {@link Namespace} clean and free of
 *  configuration plumbing, while consolidating backend-specific
 *  configuration in one well-known place.</p>
 *
 *  <h3>Configuration</h3>
 *
 *  <p>Configuration is passed to {@link #open(Properties)} as a
 *  {@link Properties} bag, following the JNDI convention. Each
 *  concrete provider documents its own set of recognised property
 *  keys and defines {@code String} constants for them so that
 *  callers may reference them without hard-coding string literals.
 *  Standard keys common to many providers (provider URL, security
 *  principal, security credentials) follow the JNDI naming
 *  conventions where applicable, to ease integration with code
 *  that already manages JNDI environments.</p>
 *
 *  <p>Providers should treat the input {@link Properties} as
 *  read-only and must not retain a reference to it after
 *  {@link #open(Properties)} returns; callers are free to mutate
 *  or discard the {@link Properties} object afterwards.</p>
 *
 *  <h3>Lifecycle</h3>
 *
 *  <p>Each successful call to {@link #open(Properties)} returns a
 *  fresh {@link Namespace} instance whose backend resources
 *  (network connections, file handles, watcher threads) remain
 *  open until the caller invokes {@link Namespace#close()}.
 *  Providers themselves are typically stateless and may be safely
 *  reused across multiple {@code open} calls.</p>
 *
 *  <h3>Discovery</h3>
 *
 *  <p>Consumers that need to choose a provider at runtime (for
 *  example, based on a URL scheme or a user-selected backend type)
 *  may use the standard Java {@link java.util.ServiceLoader}
 *  mechanism: providers declare themselves in
 *  {@code META-INF/services/com.optimasc.entity.NamespaceProvider}
 *  and consumers iterate the loaded providers to find one whose
 *  {@link #accepts(Properties)} method returns {@code true} for
 *  the supplied configuration. This is optional &mdash; consumers
 *  that statically know which provider they want may instantiate
 *  it directly.</p>
 *
 *  @see Namespace
 *  @see Properties
 *  @author Carl Eric Codere
 */
public interface NamespaceProvider
{
  // -----------------------------------------------------------------------
  // Standard property keys
  // -----------------------------------------------------------------------

  /** Standard configuration key naming the URI of the system
   *  to be opened.
   *
   *  <p>The value is a {@link String} containing a URI that
   *  identifies both the system type and its location. The URI
   *  scheme typically indicates which provider should be selected
   *  (for example, {@code ldap://} for an LDAP directory,
   *  {@code file:///} for a local filesystem, {@code http://}
   *  for a web server, {@code jdbc:} for a database-backed
   *  store), and the remainder of the URI carries
   *  provider-specific addressing detail such as host, port,
   *  search base, or root path.</p>
   *
   *  <p>The general URI form is accepted. This includes not only
   *  hierarchical URLs naming a network endpoint or local
   *  resource, but also schemes whose scheme-specific part is
   *  opaque or layered rather than a conventional authority and
   *  path &mdash; for example {@code jdbc:} connection strings or
   *  {@code jar:}-style layered locators. Providers document which
   *  schemes they recognise.</p>
   *
   *  <p>Examples of values for various provider types:</p>
   *  <ul>
   *    <li>{@code "ldap://directory.example.com:389/dc=example,dc=com"}</li>
   *    <li>{@code "file:///var/data/inventory"}</li>
   *    <li>{@code "http://api.example.com/v2"}</li>
   *    <li>{@code "jdbc:postgresql://db.example.com/catalogue"}</li>
   *  </ul>
   *
   *  <p>This property is recognised by virtually every provider
   *  and is typically the first key inspected by
   *  {@link #accepts(Properties)} to decide whether the
   *  configuration is one it should handle. Providers that
   *  require additional configuration (credentials, timeouts,
   *  search parameters) document their own property keys
   *  alongside this one.</p>
   *
   *  <p>Although the historical JNDI convention uses the key
   *  name {@code "java.naming.provider.url"}, this kit names the
   *  property {@code "provider.uri"} to reflect the URI (rather
   *  than URL) generality: a value need not name a retrievable
   *  network or local resource. Schemes whose scheme-specific
   *  part is opaque or layered &mdash; such as {@code jdbc:} or a
   *  {@code jar:}-style locator &mdash; are equally valid, and the
   *  more general {@code URI} terminology covers them where
   *  {@code URL} would not.</p>
   */
  String PROVIDER_URI = "com.optimasc.entity.provider.uri";  

  /** Property key for the security principal &mdash; the identity
   *  used to authenticate with the backend.
   *
   *  <p>The exact value format is backend-specific: an LDAP bind
   *  DN, a filesystem user name, a database user name, etc.
   *  Providers that connect anonymously or to authentication-free
   *  backends may ignore this key.</p>
   *
   *  <p>Value: {@code "com.optimasc.entity.security.principal"}.</p>
   *
   *  <p>This key intentionally mirrors the role of
   *  {@code java.naming.security.principal} from JNDI.</p>
   */
  String SECURITY_PRINCIPAL = "com.optimasc.entity.security.principal";

  /** Property key for the security credentials &mdash; the secret
   *  used in conjunction with {@link #SECURITY_PRINCIPAL} to
   *  authenticate with the backend.
   *
   *  <p>The exact value format is backend-specific: a password
   *  string, an encoded credential, a token, etc. Providers should
   *  not log this value and should clear any in-memory copy as
   *  soon as authentication completes.</p>
   *
   *  <p>Value: {@code "com.optimasc.entity.security.credentials"}.</p>
   *
   *  <p>This key intentionally mirrors the role of
   *  {@code java.naming.security.credentials} from JNDI.</p>
   */
  String SECURITY_CREDENTIALS = "com.optimasc.entity.security.credentials";

  // -----------------------------------------------------------------------
  // Provider identification
  // -----------------------------------------------------------------------

  /** Returns a short, stable identifier for this provider.
   *
   *  <p>The identifier is intended for logging, configuration
   *  selection, and human-readable display. It should be a short
   *  ASCII string in lowercase hyphen-separated form, such as
   *  {@code "ldap"}, {@code "filesystem"}, {@code "jndi-bridge"},
   *  or {@code "upnp-content-directory"}. The value should be
   *  stable across versions so that configuration files referring
   *  to a provider by name continue to work.</p>
   *
   * @return The non-null, non-empty provider identifier.
   */
  String getProviderName();

  /** Returns a human-readable description of this provider.
   *
   *  <p>Intended for display in user interfaces that let an end
   *  user select among installed providers (e.g. a connection
   *  dialog with a backend-type combo box). The description
   *  should be a single short sentence in English; localised
   *  variants are out of scope for this interface.</p>
   *
   * @return The non-null, non-empty provider description.
   */
  String getDescription();

  /** Returns the URI schemes this provider recognises.
   *
   *  <p>Each returned value is a bare scheme &mdash; lowercase,
   *  with no trailing colon or separator &mdash; such as
   *  {@code "ldap"}, {@code "ldaps"}, {@code "file"}, or
   *  {@code "jdbc"}. The array enumerates every scheme for which
   *  {@link #accepts(Properties)} may return {@code true} on the
   *  basis of the {@link #PROVIDER_URI} scheme alone.</p>
   *
   *  <p>This method is a cheap, declarative routing hint, intended
   *  for consumers that maintain a registry of providers and need
   *  to select one for a given URI without constructing a trial
   *  configuration: a consumer may index providers by the schemes
   *  they return here and dispatch a URI to the matching provider.
   *  It complements {@link #accepts(Properties)}, which remains the
   *  authoritative test &mdash; {@code accepts} may consider more
   *  than the scheme (presence of required keys, credential form),
   *  so a scheme appearing in this array indicates only that the
   *  provider is a candidate for that scheme, not that any
   *  particular configuration will be accepted.</p>
   *
   *  <p>Every scheme returned here that a live {@link Namespace}
   *  reports from {@link Namespace#getScheme()} must appear in this
   *  array; the converse does not hold, since a provider may
   *  recognise schemes it has not yet been asked to open. Schemes
   *  belonging to internally-derived namespaces that no provider
   *  opens (for example, the schema-introspection namespace) are
   *  not provider-openable and do not appear here.</p>
   *
   * @return A non-null, non-empty array of bare scheme names. The
   *   array is owned by the caller; implementations should return a
   *   fresh array or an unmodifiable view so that callers cannot
   *   mutate provider state.
   */
  String[] getSupportedSchemes();

  // -----------------------------------------------------------------------
  // Capability negotiation
  // -----------------------------------------------------------------------

  /** Returns {@code true} if this provider can open a namespace
   *  from the given configuration.
   *
   *  <p>This is a cheap, side-effect-free test used by consumers
   *  that iterate available providers (for example, via
   *  {@link java.util.ServiceLoader}) to find one that handles a
   *  given configuration. Typical implementations inspect the
   *  scheme of {@link #PROVIDER_URI}, or check for the presence
   *  of provider-specific required keys.</p>
   *
   *  <p>A {@code true} return does <em>not</em> guarantee that
   *  {@link #open(Properties)} will succeed &mdash; the backend
   *  may still be unreachable, the credentials may still be
   *  rejected, and so on. It is only a syntactic match.</p>
   *
   * @param config [in] The configuration to inspect. Must not be
   *   {@code null}.
   * @return {@code true} if this provider recognises the
   *   configuration as one it can attempt to open, {@code false}
   *   otherwise.
   */
  boolean accepts(Properties config);

  // -----------------------------------------------------------------------
  // Namespace instantiation
  // -----------------------------------------------------------------------

  /** Opens a {@link Namespace} backed by the configured backend.
   *
   *  <p>This is the connection-establishing call: it interprets
   *  the configuration, contacts the backend if necessary,
   *  authenticates, and returns a fully-initialised
   *  {@link Namespace} handle. Subsequent operations on the
   *  returned namespace require no further configuration; the
   *  authentication and connection state are encapsulated within
   *  the namespace instance.</p>
   *
   *  <p>Each call to this method returns a fresh namespace
   *  instance with its own backend resources. The caller is
   *  responsible for invoking {@link Namespace#close()} on the
   *  returned instance once it is no longer needed; failure to
   *  do so may leak network connections, file handles, or
   *  watcher threads.</p>
   *
   *  <p>Providers should validate the configuration before
   *  contacting the backend and throw an {@link EntityException}
   *  with error code {@link EntityException#INVALID_SYNTAX} if
   *  required keys are missing or malformed. Backend errors
   *  encountered during the connection or authentication phase
   *  should be reported with error code
   *  {@link EntityException#ACTION_FAILED} and a chained cause
   *  describing the underlying failure.</p>
   *
   * @param config [in] The configuration describing the backend
   *   to open. Must not be {@code null}. The provider treats this
   *   argument as read-only.
   * @return A non-null, fully-initialised {@link Namespace}
   *   handle.
   * @throws EntityException with error code
   *   {@link EntityException#ACTION_FAILED} if the backend cannot
   *   be contacted or authentication fails;
   *   {@link EntityException#ACCESS_DENIED} if the credentials
   *   are valid but lack the rights required to open the
   *   namespace.
   * @throws IllegalArgumentException when there are 
   *   missing properties or if they are malformed.
   *   
   */
  Namespace open(Properties config) throws EntityException;
  
}
