package com.optimasc.entity;

import java.io.Closeable;

/** A rooted, navigable collection of {@link Entity} instances
 *  sharing a single name syntax.
 *
 *  <p>A {@code Namespace} is the entry point to a tree of
 *  entities backed by some underlying system &mdash; an LDAP
 *  directory, a filesystem, a DOM document, a RIFF file, a SQL
 *  table, a UPnP ContentDirectory, or any other hierarchical,
 *  named, attributed store. It exposes four complementary views:</p>
 *
 *  <ul>
 *    <li><strong>Root access</strong> through {@link #getRoot()},
 *        which returns the entity at the top of the namespace's
 *        tree and is the natural starting point for traversal.</li>
 *    <li><strong>Name-to-entity resolution</strong> through
 *        {@link #resolve(String)} and {@link #resolve(EntityName)},
 *        which look up entities by their name-in-namespace.</li>
 *    <li><strong>Syntax access</strong> through
 *        {@link #getNameParser()}, which exposes the parser
 *        that converts between external name strings and
 *        structured {@link EntityName} instances.</li>
 *    <li><strong>Capability introspection</strong> through
 *        {@link #hasFeature(String)} and
 *        {@link #getCapability(Class)}, which let consumers
 *        interrogate which optional parts of the entity contract
 *        are meaningfully implemented by the backend.</li>
 *  </ul>
 *
 *  <p>Within a single namespace every name follows the same
 *  syntax; the namespace returns a single shared
 *  {@link EntityNameParser} instance. Adapters that need to
 *  expose multiple syntaxes (federation, mounted sub-namespaces)
 *  should do so by composing multiple {@code Namespace}
 *  instances, each with its own parser, rather than varying the
 *  parser within a single namespace.</p>
 *
 *  <p>The root of the namespace is obtained directly through
 *  {@link #getRoot()}, or equivalently by resolving the empty
 *  name &mdash; {@code resolve(EntityName.EMPTY)}.</p>
 *
 *  <p>Capability introspection is split across three patterns,
 *  each matched to its grain:</p>
 *
 *  <ul>
 *    <li><strong>Backend-wide behavioural promises</strong>
 *        (e.g. whether attributes are meaningfully populated,
 *        whether symbolic links are honoured) are queried through
 *        {@link #hasFeature(String)} with constants from
 *        {@link Features}.</li>
 *    <li><strong>Backend-specific extension capabilities</strong>
 *        (e.g. obtaining the underlying JNDI {@code Context} or
 *        UPnP {@code Service} handle) are queried through
 *        {@link #getCapability(Class)}.</li>
 *    <li><strong>Per-entity structural capabilities</strong>
 *        (e.g. whether a particular entity is modifiable or
 *        observable) are detected by {@code instanceof} against
 *        the relevant sub-interface such as
 *        {@link ModifiableEntity}, not through this namespace.</li>
 *  </ul>
 *
 *  <p>Implementations are not required to be thread-safe. Long-
 *  running resolution operations (remote LDAP lookups, filesystem
 *  traversal across slow storage) should be invoked from worker
 *  threads in user-interface consumers.</p>
 *
 *  @see Entity
 *  @see EntityName
 *  @see EntityNameParser
 *  @see Features
 *  @author Carl Eric Codere
 */
public interface Namespace extends Closeable
{
  /** Returns the parser used to convert between external name
   *  strings and {@link EntityName} instances within this
   *  namespace.
   *
   *  <p>The returned parser is the syntax authority for this
   *  namespace: every name-in-namespace string returned by an
   *  {@link Entity#getNameInNamespace()} call on an entity from
   *  this namespace is parseable by this parser, and every
   *  {@link EntityName} produced by this parser is resolvable
   *  through {@link #resolve(EntityName)}.</p>
   *
   *  <p>Implementations typically return the same parser instance
   *  on every call; the parser is stateless and may be safely
   *  cached by callers.</p>
   *
   * @return The non-null {@link EntityNameParser} for this
   *   namespace.
   */
  EntityNameParser getNameParser();

  /** Returns the bare URI scheme identifying the kind of backend
   *  this namespace is bound to.
   *
   *  <p>The returned value is a single lowercase scheme token with
   *  no trailing colon or separator &mdash; for example
   *  {@code "ldap"}, {@code "file"}, or {@code "jdbc"}. Whereas a
   *  {@link NamespaceProvider} may recognise several schemes (see
   *  {@link NamespaceProvider#getSupportedSchemes()}), a live
   *  namespace is bound to exactly one concrete backend and so
   *  reports exactly one scheme.</p>
   *
   *  <p>This is the scheme-level counterpart to the full locator
   *  supplied at open time through
   *  {@link NamespaceProvider#PROVIDER_URI}: it identifies the
   *  provider family and parser syntax in use, and is sufficient to
   *  route a name back to a compatible provider, without exposing
   *  the authority or path of the underlying connection.</p>
   *
   *  <p>For namespaces opened through a {@link NamespaceProvider},
   *  the returned scheme is one of the schemes that provider
   *  declares from {@link NamespaceProvider#getSupportedSchemes()}.
   *  Some namespaces are <em>internally derived</em> rather than
   *  opened from a locator &mdash; most notably the
   *  schema-introspection namespace reached through
   *  {@link com.optimasc.entity.schema.Schema#getRoot()}, whose
   *  scheme is {@code "schema"}.
   *  Such schemes are not provider-openable and therefore do not
   *  appear in any provider's supported-scheme list; they are
   *  nonetheless valid return values of this method.</p>
   *
   * @return The non-null, non-empty bare scheme of this namespace.
   */
  String getScheme();

  /** Returns the root entity of this namespace.
   *
   *  <p>The root is the unique entity that has no parent within
   *  this namespace. It is the natural starting point for tree
   *  traversal, breadcrumb construction, and any algorithm that
   *  walks the namespace from the top.</p>
   *
   *  <p>This method is equivalent to {@link #resolve(EntityName)
   *  resolve(EntityName.EMPTY)} and is provided as a direct
   *  accessor because root retrieval is by far the most common
   *  entry point into a namespace. Implementations typically
   *  return a cached reference and the call is cheap; consumers
   *  may invoke it without concern for repeated cost.</p>
   *
   * @return The non-null root {@link Entity} of this namespace.
   * @throws EntityException with error code
   *   {@link EntityException#ACTION_FAILED} if the root cannot
   *   be obtained due to a backend error.
   */
  Entity getRoot() throws EntityException;

  /** Resolves an external name string to the {@link Entity} it
   *  identifies.
   *
   *  <p>This is a convenience equivalent to:</p>
   *  <pre>
   *  resolve(getNameParser().parse(nameInNamespace))
   *  </pre>
   *
   *  <p>The input string is interpreted according to the syntax
   *  of this namespace's parser. The empty string (or whatever
   *  the parser considers the canonical root representation)
   *  resolves to the same entity returned by {@link #getRoot()}.</p>
   *
   * @param nameInNamespace [in] The external name string. Must
   *   not be {@code null}.
   * @return The {@link Entity} identified by the given name, or
   *   {@code null} if no such entity exists in this namespace.
   * @throws java.text.ParseException if {@code nameInNamespace}
   *   is not syntactically valid for this namespace's parser.
   * @throws EntityException with error code
   *   {@link EntityException#ACTION_FAILED} if the resolution
   *   fails due to a backend error.
   */
  Entity resolve(String nameInNamespace)
      throws java.text.ParseException, EntityException;

  /** Resolves a structured name to the {@link Entity} it
   *  identifies.
   *
   *  <p>This is the generic resolution method: it allows callers
   *  to construct or manipulate names structurally
   *  (concatenating components, taking prefixes, etc.) without
   *  ever round-tripping through the parser. Generic algorithms
   *  &mdash; breadcrumb builders, parent navigators, caching
   *  layers &mdash; can use this method without knowing the
   *  parser's syntax.</p>
   *
   *  <p>Resolution is conceptually equivalent to a component-by-
   *  component walk from the root: starting from the root entity,
   *  for each component {@code name.get(i)} the implementation
   *  finds the child of the current entity whose naming-attribute
   *  value matches the component, and descends into it.
   *  Implementations may use a more efficient direct lookup where
   *  the backend supports it (LDAP, for example, can resolve a
   *  DN in a single bind), but the observable result must match
   *  the component-walk semantics.</p>
   *
   *  <p>The empty name resolves to the same entity returned by
   *  {@link #getRoot()}.</p>
   *
   * @param name [in] The structured name to resolve. Must not be
   *   {@code null}.
   * @return The {@link Entity} identified by {@code name}, or
   *   {@code null} if no such entity exists in this namespace.
   * @throws IllegalArgumentException if {@code name} is
   *   {@code null}.
   * @throws EntityException with error code
   *   {@link EntityException#ACTION_FAILED} if the resolution
   *   fails due to a backend error.
   */
  Entity resolve(EntityName name) throws EntityException;

  /** Returns {@code true} if this namespace supports the named
   *  feature.
   *
   *  <p>Features describe backend-wide behavioural properties of
   *  the adapter behind this namespace &mdash; whether attributes
   *  are meaningfully populated, whether symbolic links are
   *  honoured, whether schema information is exposed, and so on.
   *  See {@link Features} for the standard feature names and
   *  their contractual meaning.</p>
   *
   *  <p>Unknown feature names return {@code false} rather than
   *  throwing, so that callers may safely interrogate features
   *  that may have been added in a newer version of the library
   *  than the adapter was built against.</p>
   *
   *  <p>This method is intended for fine-grained binary
   *  properties of the backend that affect how existing methods
   *  on {@link Entity} behave (whether they return real data or
   *  empty/null placeholders). For per-entity structural
   *  capabilities that add new methods &mdash; modifiability,
   *  observability &mdash; use {@code instanceof} against the
   *  relevant sub-interface such as {@link ModifiableEntity}
   *  instead. For backend-specific extension handles, use
   *  {@link #getCapability(Class)}.</p>
   *
   * @param featureName [in] The feature name to query. Must not
   *   be {@code null}.
   * @return {@code true} if the named feature is supported,
   *   {@code false} otherwise (including for unrecognized
   *   feature names).
   * @see Features
   */
  boolean hasFeature(String featureName);

  /** Returns an implementation of the requested capability type,
   *  or {@code null} if this namespace does not provide it.
   *
   *  <p>This method serves two related roles:</p>
   *
   *  <ul>
   *    <li><strong>Standard typed capabilities paired with
   *        features.</strong> Some entries in {@link Features}
   *        pair with a typed capability retrievable through this
   *        method &mdash; for example, {@link Features#SCHEMA}
   *        pairs with {@code getCapability(Schema.class)}. Where
   *        such a pairing exists, the feature constant's
   *        documentation specifies the capability type, and the
   *        two methods are contractually consistent: if
   *        {@link #hasFeature(String)} returns {@code true} for
   *        the feature, this method returns a non-{@code null}
   *        instance of the paired type; if it returns
   *        {@code false}, this method returns {@code null} for
   *        that type. These capabilities are part of the
   *        portable contract and may be relied upon across all
   *        compliant adapters.</li>
   *    <li><strong>Backend-specific extension handles.</strong>
   *        This method is also an escape hatch for capabilities
   *        that do not warrant a place in the core
   *        {@code Namespace} interface &mdash; the underlying
   *        JNDI {@link javax.naming.Context}, the raw UPnP
   *        {@code Service}, an LDAP connection handle, and so on.
   *        These are not portable: consumers must treat a
   *        {@code null} return as the normal "not supported"
   *        signal and degrade gracefully, since a capability
   *        available on one namespace implementation will not
   *        generally be available on another.</li>
   *  </ul>
   *
   *  <p>For coarse-grained capabilities that are part of the
   *  portable contract and vary per entity &mdash; modifiability,
   *  observability &mdash; use {@code instanceof} against the
   *  relevant entity sub-interface instead. For fine-grained
   *  behavioural properties of the backend, use
   *  {@link #hasFeature(String)}.</p>
   *
   * @param <T> The capability type.
   * @param capabilityType [in] The interface or class of the
   *   capability to retrieve. Must not be {@code null}.
   * @return An instance of {@code capabilityType}, or
   *   {@code null} if this namespace does not provide the
   *   requested capability.
   */
  <T> T getCapability(Class<T> capabilityType);  
}
