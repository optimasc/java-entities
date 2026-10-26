package com.optimasc.entity;

import com.optimasc.attribute.Attributes;

/** Standard feature names for use with
 *  {@link Namespace#hasFeature(String)}.
 *
 *  <p>A <em>feature</em> is a fine-grained, binary property of a
 *  {@link Namespace} (i.e. of the backend adapter as a whole) that
 *  callers may interrogate at runtime to determine which optional
 *  parts of the entity contract are meaningfully implemented.
 *  Features describe backend-wide capabilities, not per-entity
 *  capabilities; for per-entity capability detection use
 *  {@code instanceof} against the relevant sub-interface (e.g.
 *  {@link ModifiableEntity}).</p>
 *
 *  <p>This class follows the same pattern as the error-code constants
 *  on {@link EntityException}: a single non-instantiable holder of
 *  {@code String} constants. Adapters and consumers should reference
 *  these constants rather than embed literal strings, so that
 *  misspellings are caught at compile time.</p>
 *
 *  <p>Feature names are case-sensitive ASCII identifiers using a
 *  lowercase, hyphen-separated convention to align with the
 *  conventions used by LDAP {@code supportedFeatures} OIDs (in their
 *  English-language descriptions), UPnP capability strings, and DOM
 *  feature names. Future feature names should follow the same
 *  convention.</p>
 *
 *  <p>Unknown feature names passed to
 *  {@link Namespace#hasFeature(String)} must return {@code false},
 *  never throw. This allows consumers to interrogate features that
 *  may have been added in a newer version of the library without
 *  needing to defensively check first.</p>
 *  
 *  <p>Some features pair with a typed capability retrievable
 *  via {@link Namespace#getCapability(Class)}. Where this is the
 *  case, the constant's documentation specifies the capability
 *  type and guarantees that {@code hasFeature} returning
 *  {@code true} implies {@code getCapability} returns a non-
 *  {@code null} value of that type. The two methods are
 *  contractually consistent: an adapter must not advertise a
 *  feature through {@code hasFeature} while withholding the
 *  corresponding capability through {@code getCapability}, or
 *  vice versa.</p> *  
 *
 *  <p>The set of features defined here is intentionally small.
 *  Features should be added only when there is a concrete consumer
 *  use-case for branching on the capability; speculative features
 *  that no consumer queries are dead weight.</p>
 *
 *  @see Namespace#hasFeature(String)
 *  @see Namespace#getCapability(Class)
 *  @author Carl Eric Codere
 */
public final class Features
{
  // -----------------------------------------------------------------------
  // Standard feature names
  // -----------------------------------------------------------------------

  /** Indicates that this namespace exposes schema information through
   *  {@link com.optimasc.entity.schema.Schema} and related schema-aware
   *  operations.
   *
   *  <p>When this feature is present:</p>
   *  <ul>
   *    <li>{@link Entity#getObjectClass()} returns a name that is
   *        meaningful within a declared schema (as opposed to a
   *        synthesized placeholder);</li>
   *    <li>Mandatory-attribute checks may be performed by the backend at
   *        creation and modification time.</li>
   *  </ul>
   *
   *  <p>When this feature is absent:</p>
   *  <ul>
   *    <li>{@link Entity#getObjectClass()} may return an adapter-defined
   *        placeholder such as {@code "entity"};</li>
   *    <li>The consumer is responsible for any client-side validation it
   *        wishes to enforce.</li>
   *  </ul>
   *  
   *  <p>When this feature is present, the corresponding
   *  {@link Schema} object describing the backend's type system
   *  may be retrieved via
   *  {@link Namespace#getCapability(Class) getCapability(Schema.class)},
   *  which is guaranteed to return a non-{@code null} value. When
   *  this feature is absent, {@code getCapability(Schema.class)}
   *  returns {@code null}.</p>     
   *
   *  <p>Backends that typically have this feature: LDAP/X.500 (subschema
   *  subentry), XML Schema-validated XML documents, UPnP
   *  ContentDirectory.</p>
   *
   *  <p>Backends that typically lack this feature: plain DOM without an
   *  associated schema, generic filesystem adapters, RIFF/IFF chunk
   *  hierarchies, ad-hoc JavaBean adapters.</p>
   *
   *  <p>Value: {@code "schema"}.</p>
   */
  public static final String SCHEMA = "schema";

  /** Indicates that this namespace supports symbolic-link entities
   *  &mdash; entities that act as references to other entities within
   *  the namespace.
   *
   *  <p>When this feature is present:</p>
   *  <ul>
   *    <li>{@link Entity#getLinkTarget()} may return a non-null
   *        name-in-namespace, indicating that the entity is a link;</li>
   *    <li>A non-null return from {@link Entity#getLinkTarget()} can be
   *        passed to {@link Namespace#resolve(String)} to dereference
   *        the link.</li>
   *  </ul>
   *
   *  <p>When this feature is absent, {@link Entity#getLinkTarget()}
   *  always returns {@code null} and consumers may skip link-handling
   *  code paths entirely (e.g. cycle-detection during tree traversal,
   *  shortcut overlay icons).</p>
   *
   *  <p>Backends that typically have this feature: filesystem adapters
   *  on POSIX-like filesystems (symbolic links), X.500 directories
   *  (aliases), JNDI contexts containing
   *  {@link javax.naming.LinkRef} entries.</p>
   *
   *  <p>Backends that typically lack this feature: DOM XML documents,
   *  RIFF/IFF chunk hierarchies, in-memory JavaBean adapters, SQL-row
   *  adapters.</p>
   *
   *  <p>Value: {@code "links"}.</p>
   */
  public static final String LINKS = "links";
  
  /** Content replacement. When declared, the entities of the namespace
   *  that implement {@link ContentEntity} support
   *  {@link ContentEntity#openContentWriter()}, subject to the access
   *  rights of each item. When not declared,
   *  {@link ContentEntity#openContentWriter()} always fails with
   *  {@link EntityException#NOT_IMPLEMENTED}.
   *
   *  <p>Support is a property of the backend as a whole: it is
   *  determined when the namespace is opened and does not change for
   *  the lifetime of the namespace.</p>
   */
  public static final String CONTENT_WRITE = "content-write";  

  /** Indicates that entities in this namespace meaningfully carry
   *  attributes accessible through
   *  {@link Entity#getAttributes(String[])}.
   *
   *  <p>When this feature is present, {@link Entity#getAttributes}
   *  returns a populated {@link Attributes} snapshot for entities
   *  that have attributes (it may still be empty for entities that
   *  legitimately have none).</p>
   *
   *  <p>When this feature is absent, {@link Entity#getAttributes}
   *  always returns an empty {@link Attributes} snapshot. The
   *  namespace is purely hierarchical: entities have names, parents,
   *  and children, but no attached metadata. Consumers can use this
   *  signal to suppress attribute-display panels in their user
   *  interface entirely.</p>
   *
   *  <p>This feature reports only whether attributes are
   *  <em>readable</em>. Whether they are <em>writable</em> is a
   *  per-entity question answered by
   *  {@code instanceof ModifiableEntity}.</p>
   *
   *  <p>Backends that typically have this feature: LDAP/X.500, DOM
   *  XML (element attributes), filesystem adapters exposing extended
   *  attributes or {@code stat} metadata, UPnP ContentDirectory.</p>
   *
   *  <p>Backends that typically lack this feature: pure tree-of-names
   *  adapters such as a minimal filesystem view that exposes only
   *  names and hierarchy.</p>
   *
   *  <p>Value: {@code "attributes"}.</p>
   */
  public static final String ATTRIBUTES = "attributes";
  
  /** Indicates that this namespace removes an entity together with
   *  its entire subtree in a single atomic operation.
   *
   *  <p>This feature selects between the two behaviours that
   *  {@link ModifiableEntity#removeChild(Entity)} may have when
   *  the child to remove still has children of its own. The choice
   *  applies to the namespace as a whole and is strict in both
   *  directions.</p>
   *
   *  <p>When this feature is present:</p>
   *  <ul>
   *    <li>{@link ModifiableEntity#removeChild(Entity)} accepts a
   *        child that has children and removes the child and all
   *        of its descendants in one atomic step: other observers
   *        see either the whole subtree or none of it;</li>
   *    <li>{@link EntityUtilities#removeTree} makes a single call
   *        to the backend and reports a single progress event.</li>
   *  </ul>
   *
   *  <p>When this feature is absent:</p>
   *  <ul>
   *    <li>{@link ModifiableEntity#removeChild(Entity)} removes
   *        only a child that has no children, and refuses any
   *        other with
   *        {@link EntityException#CONTAINER_NOT_EMPTY};</li>
   *    <li>{@link EntityUtilities#removeTree} removes the subtree
   *        entity by entity, reporting progress for each one and
   *        allowing the removal to be cancelled between two
   *        entities.</li>
   *  </ul>
   *
   *  <p>An adapter that can remove some subtrees atomically but
   *  not others must not advertise this feature, and must refuse
   *  every non-empty child. Consumers rely on the feature to know,
   *  before they start a removal, whether it will be a single
   *  indeterminate step or a sequence of reported steps.</p>
   *
   *  <p>Backends that typically have this feature: DOM XML
   *  documents, RIFF/IFF chunk hierarchies, in-memory JavaBean
   *  adapters, SQL adapters whose tables declare cascading
   *  deletes.</p>
   *
   *  <p>Backends that typically lack this feature: filesystem
   *  adapters, LDAP/X.500 directories (whose delete operation
   *  applies to leaf entries only).</p>
   *
   *  <p>Value: {@code "subtree-remove"}.</p>
   */
  public static final String SUBTREE_REMOVE = "subtree-remove";  

  // -----------------------------------------------------------------------
  // Construction
  // -----------------------------------------------------------------------

  /** Not instantiable. */
  private Features()
  {
  }
}
