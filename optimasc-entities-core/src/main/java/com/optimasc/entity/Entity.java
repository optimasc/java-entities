package com.optimasc.entity;

import java.util.List;

import com.optimasc.attribute.AttributeModification;
import com.optimasc.attribute.Attributes;

/** A generic, read-oriented view of an entity, object or node in a 
 *  hierarchical, named, attributed data structure.
 *
 *  <p>Implementations of this interface adapt heterogeneous backends
 *  &mdash; such as a JNDI {@link javax.naming.directory.DirContext}, a
 *  DOM XML element, a Java bean, a filesystem directory, or a RIFF chunk
 *  hierarchy &mdash; to a common abstraction suitable for presentation in
 *  a tree-based user interface.</p>
 *
 *  <p>Each entity exposes:</p>
 *  <ul>
 *    <li>a display label;</li>
 *    <li>an object class indicating the entity's type;</li>
 *    <li>a snapshot of the entity's attributes;</li>
 *  </ul>
 *
 *  <p>This interface is deliberately read-oriented. Implementations that
 *  support modification do so by also implementing
 *  {@link ModifiableEntity}; a consumer may query for this capability
 *  using {@code instanceof}.</p>
 *
 *  <p>The {@link Attributes} object returned by
 *  {@link #getAttributes(String[])} is a detached snapshot. Mutations to
 *  the snapshot, if permitted, do not affect the entity; conversely,
 *  subsequent modifications to the entity are not reflected in a
 *  previously-returned snapshot. All modifications to a node's attributes
 *  must be performed through
 *  {@link ModifiableEntity#modifyAttributes(AttributeModification[])}.</p>
 *
 *  <p>Implementations are not required to be thread-safe.</p>
 *
 *  <p>Methods that may involve I/O or remote calls declare
 *  {@code throws} {@link EntityException}. Although {@link EntityException}
 *  is an unchecked exception ({@link RuntimeException}), the explicit
 *  {@code throws} declaration serves as documentation that the method
 *  is failure-prone and that callers should consider handling it.</p>
 *
 *  @see ModifiableEntity
 *  @see Attributes
 *  @see EntityException
 *  @author Carl Eric Codere
 */
public interface Entity
{

  /** Well-known object class name for a generic <em>container</em>
   *  &mdash; an entity that exists primarily to group child
   *  entities and carries no domain payload of its own.
   *
   *  <p>This is a portable, backend-neutral object class name that
   *  adapters may report from {@link #getObjectClass()} for grouping
   *  nodes that have no more specific type. The name is the bare
   *  token {@code "container"}, aligned in spirit with the UPnP
   *  ContentDirectory container concept but without that
   *  specification's dotted class prefix, so that it may be reused
   *  uniformly across adapters (filesystem directories, schema-tree
   *  grouping nodes, synthetic roots, and so on). Adapters that need
   *  to interoperate with a specific external vocabulary are
   *  expected to map that vocabulary's container class onto this
   *  value at their boundary.</p>
   *
   *  <p>Reporting this object class is independent of the
   *  {@link ContainerEntity} capability: whether an entity can hold
   *  children is signalled structurally by
   *  {@code instanceof ContainerEntity}, while this constant merely
   *  names a type. An entity may report {@code "container"} and
   *  implement {@link ContainerEntity}; the two are complementary.</p>
   */
  String OBJECT_CLASS_CONTAINER = "container";

  /** Returns the name of the object class to which this entity
   *  belongs. This value identifies the entity's type within the schema
   *  and corresponds to the LDAP {@code objectClass} or an equivalent
   *  type identifier for other providers.
   *
   *  <p>For linked entities, this should return the object class of the
   *  link itself, not the object class of the link target.</p>
   *
   *  <p>Consumers may call this method frequently, for example each time
   *  a user interface renders the entity to choose its icon.
   *  Implementations should therefore capture the value when the
   *  instance is created &mdash; typically from data already returned by
   *  the enumeration or lookup that produced the entity &mdash; and
   *  return it without contacting the backend. A backend call should
   *  only be made if the value was not available at creation, in which
   *  case the retrieved value should be kept for subsequent calls.</p>
   *
   *  <p>The returned value may therefore reflect the state of the backend
   *  at the time the instance was obtained. Consumers that require the
   *  current value must obtain a new instance, for example through
   *  {@link ContainerEntity#getChildren()} or
   *  {@link Namespace#resolve(EntityName)}.</p>
   *
   *  @return A non-null object class identifier.
   *    Implementations must not return {@code null}.
   *  @throws EntityException with error code
   *    {@link EntityException#ACTION_FAILED} if the value cannot be
   *    retrieved due to a backend failure, or
   *    {@link EntityException#NO_SUCH_OBJECT} if the entity no longer
   *    exists in the backend.
   */
  String getObjectClass() throws EntityException;
  
  /** Returns a human-readable label identifying this entity within its
   *  parent. This value is intended for display and is
   *  not required to be unique, stable across sessions, or suitable for
   *  use as an identifier.
   *
   *  <p>The value returned by this method may be backed by a named
   *  attribute in the underlying system. Typical attribute sources
   *  include:</p>
   *  <ul>
   *    <li>a {@code displayName} attribute, for backends that
   *        distinguish between a technical identifier and a
   *        human-readable label (e.g. Active Directory, WebDAV);</li>
   *    <li>the {@code cn} (commonName) attribute, for LDAP and
   *        X.500-backed adapters;</li>
   *    <li>the value of a {@code name} or {@code title} attribute,
   *        for XML or bean-backed adapters;</li>
   *    <li>the file name, for filesystem-backed adapters;</li>
   *    <li>the element local name, for DOM-backed adapters where no
   *        distinguishing attribute exists.</li>
   *  </ul>
   *
   *  <p>This value is intended purely for display. It is not required
   *  to be unique, stable across sessions, or suitable for use as an
   *  identifier or lookup key. For a stable unique identifier use
   *  {@link #getNameInNamespace()} instead.</p>
   *
   *  <p>Consumers may call this method frequently, for example each time
   *  a user interface renders the entity. Implementations should
   *  therefore capture the value when the instance is created &mdash;
   *  typically from data already returned by the enumeration or lookup
   *  that produced the entity &mdash; and return it without contacting
   *  the backend. A backend call should only be made if the value was
   *  not available at creation, in which case the retrieved value should
   *  be kept for subsequent calls.</p>
   *
   *  <p>The returned value may therefore reflect the state of the backend
   *  at the time the instance was obtained. Consumers that require the
   *  current value must obtain a new instance, for example through
   *  {@link ContainerEntity#getChildren()} or
   *  {@link Namespace#resolve(EntityName)}.</p>
   *
   *  @return A non-null, non-empty string suitable for display.
   *    Implementations must not return {@code null}; if no meaningful
   *    name can be derived, a synthesized placeholder must be returned.
   *  @throws EntityException with error code
   *    {@link EntityException#ACTION_FAILED} if the value cannot be
   *    retrieved due to a backend failure, or
   *    {@link EntityException#NO_SUCH_OBJECT} if the entity no longer
   *    exists in the backend.
   */
  String getDisplayName() throws EntityException;  

  /** Returns a snapshot of this entity's attributes.
   *
   *  <p>The returned set is a detached snapshot, independent of the
   *  entity's internal state. Subsequent modifications to the entity are
   *  not reflected in a previously-returned snapshot, and mutations to
   *  the snapshot (if the returned set is mutable) do not affect the
   *  entity. To apply changes back, callers must construct an
   *  {@link AttributeModification} array describing the desired changes and
   *  invoke
   *  {@link ModifiableEntity#modifyAttributes(AttributeModification[])}
   *  on a {@link ModifiableEntity}.</p>
   *
   *  <p>Child entities are not represented in the attributes; they are
   *  obtained through {@link #getChildren()}. The distinction between an
   *  attribute and a child is system-defined, but typically follows the
   *  rule that scalar values and short homogeneous lists of scalar values
   *  are attributes, while composite sub-objects and collections of
   *  composite sub-objects are children.</p>
   *
   *  <p>If this is a link entity, this method returns the attributes of
   *  the link itself, not of the link target.</p>
   *
   *  @param attrIds [in] The expanded names or OIDs of the attributes to
   *    retrieve, or {@code null} to retrieve all attributes.
   *  @return A non-null snapshot of this entity's attributes. If the
   *    entity has no attributes (or none matching {@code attrIds}), an
   *    empty set is returned.
   *  @throws EntityException with error code
   *    {@link EntityException#ACTION_FAILED} if the attributes cannot be
   *    retrieved due to a backend failure, or
   *    {@link EntityException#NO_SUCH_OBJECT} if the entity no longer
   *    exists in the backend.
   */
  Attributes getAttributes(String[] attrIds) throws EntityException;


  /** Retrieves the full name of this entity within its own namespace.
   *
   *  <p>This uniquely identifies the entity's position in the namespace
   *  and is the portable equivalent of an X.500 Distinguished Name (DN),
   *  a filesystem absolute path, or a DOM node's XPath address. It is
   *  suitable for use as a stable key for caching or logging, but must
   *  not be assumed to be human-readable.</p>
   *
   *  @return The non-null fully qualified name path of this entity within its
   *    namespace. Implementations must not return {@code null}.
   *  @throws EntityException with error code
   *    {@link EntityException#ACTION_FAILED} if the name cannot be
   *    retrieved due to a backend failure.
   */
  EntityName getNameInNamespace() throws EntityException;

  /** Returns the {@link Namespace} that owns this entity.
   *
   *  <p>Every entity belongs to exactly one namespace &mdash; the
   *  rooted, navigable collection through which it was obtained.
   *  This back-reference is the entity's route to namespace-level
   *  services without having to thread a {@link Namespace} handle
   *  through every call site. In particular, it is how a consumer
   *  reaches capability introspection: schema validation, for
   *  example, is obtained with
   *  {@code getNamespace().getCapability(}{@link
   *  com.optimasc.entity.schema.Schema}{@code .class)} given any
   *  entity drawn from that namespace.</p>
   *
   *  <p>The returned namespace is the one whose
   *  {@link Namespace#resolve(EntityName)} accepts this entity's
   *  {@link #getNameInNamespace()}, and whose
   *  {@link Namespace#getNameParser()} governs the syntax of that
   *  name. For entities that form the schema-introspection tree
   *  reached through {@link com.optimasc.entity.schema.Schema#getRoot()},
   *  the owning namespace is that derived schema namespace (whose
   *  {@link Namespace#getScheme()} is {@code "schema"}), not the
   *  data namespace the schema describes.</p>
   *
   *  <p>This method does not perform I/O and does not contact the
   *  backend; it returns an already-established association.</p>
   *
   *  @return The non-null {@link Namespace} that owns this entity.
   *    Implementations must not return {@code null}.
   */
  Namespace getNamespace();

  /** Returns this entity's parent, or {@code null} if this entity is the
   *  root of the backend's namespace.
   *
   *  @return The parent {@link Entity}, or {@code null} if this entity
   *    is the root.
   *  @throws EntityException with error code
   *    {@link EntityException#ACTION_FAILED} if the parent cannot be
   *    retrieved due to a backend failure, or
   *    {@link EntityException#NO_SUCH_OBJECT} if the parent no longer
   *    exists in the backend.
   */
  Entity getParent() throws EntityException;

  /** Returns the name-in-namespace of the entity this one is a symbolic
   *  link to, or {@code null} if this entity is not a link.
   *
   *  <p>A non-null return indicates that this entity is a reference to
   *  another entity within the namespace, analogous to a JNDI
   *  {@link javax.naming.LinkRef}, a filesystem symbolic link, or an
   *  X.500 alias. The returned name identifies the target; consumers may
   *  resolve it by feeding it back to the appropriate lookup mechanism,
   *  or may interact with this entity as a link without dereferencing.</p>
   *
   *  <p>The link itself is an entity in its own right: it has a display
   *  name, an object class (indicating "link" or a backend equivalent),
   *  and a name-in-namespace distinct from its target. Links typically
   *  have no attributes of their own beyond the target pointer, and
   *  typically have no children &mdash; their children, if any, are
   *  obtained by resolving the target and asking that entity.</p>
   *
   *  <p>Implementations that support link semantics must ensure that:</p>
   *  <ul>
   *    <li>{@link #getNameInNamespace()} returns the link's own name,
   *        not the target's;</li>
   *    <li>{@link #getDisplayName()} returns a label for the link
   *        (often the same as the target's last name segment);</li>
   *    <li>{@link #getObjectClass()} returns an identifier that the UI
   *        can use to render the link distinctively (e.g., with a
   *        shortcut overlay icon).</li>
   *  </ul>
   *
   *  <p>The returned name identifies the target and may be passed
   *    directly to {@link com.optimasc.entity.Namespace#resolve(EntityName)}
   *      to obtain the target entity, or the link may be used as-is
   *      without dereferencing.</p>
   *
   *  @return The {@link EntityName} identifying the link target within
   *    this namespace, or {@code null} if this entity is not a link.
   *    The returned name is directly resolvable via
   *    {@link com.optimasc.entity.Namespace#resolve(EntityName)}.
   *  @throws EntityException with error code
   *    {@link EntityException#ACTION_FAILED} if the target name cannot
   *    be retrieved due to a backend failure.
   */
  EntityName getLinkTarget() throws EntityException;
  
  /** Returns a human-readable label identifying this entity. This
   *  calls {@link #getDisplayName()} to return the human-readable
   *  string representation.
   *  
   *  <p>Since {@link EntityException} is an unchecked exception,
   *  implementations that retrieve this value from a backend may throw
   *  it to signal a retrieval failure, even though {@code toString()}
   *  does not declare it. Callers that display this value in a UI
   *  should be prepared to catch {@link EntityException} and substitute
   *  a fallback label.</p>
   *
   *
   *  @return A non-null, non-empty string suitable for display.
   *    Implementations must not return {@code null}; if no meaningful
   *    name can be derived, a synthesized placeholder must be returned.
   *  @throws EntityException with error code
   *    {@link EntityException#ACTION_FAILED} if the value cannot be
   *    retrieved due to a backend failure, or
   *    {@link EntityException#NO_SUCH_OBJECT} if the entity no longer
   *    exists in the backend.
   */
  String toString();  
  
  /** Indicates whether some other object is an entity representing the
   *  same backend object as this entity.
   *
   *  <p>Consumers rely on equality to recognize an entity across successive
   *  retrievals.</p>
   *
   *  <p>Implementations must satisfy the following rules:</p>
   *  <ul>
   *    <li>Entities obtained from different {@link Namespace} instances are
   *        never equal, even if both namespaces are bound to the same
   *        backend.</li>
   *    <li>Within a namespace, two entities are equal if and only if they
   *        represent the same backend object. The recommended basis is the
   *        name returned by {@link #getNameInNamespace()}, compared with
   *        {@link EntityName#equals(Object)}; since the namespace's
   *        {@link EntityNameParser} normalizes components according to its
   *        syntax, equal names denote the same entity. Adapters whose names
   *        are not stable identifiers, such as positional DOM addresses,
   *        must instead base equality on the identity of the underlying
   *        backend object.</li>          
   *    <li>A link entity is equal only to entities representing the same
   *        link, never to its target.</li>
   *    <li>The result must remain constant for the lifetime of the
   *        instance. If the backend object is renamed or moved, instances
   *        obtained before the change keep their original identity, and
   *        are not equal to instances obtained after the change.</li>
   *    <li>This method must not perform I/O, contact the backend, or throw
   *        an exception. The values on which equality is based must
   *        therefore be captured when the instance is created.</li>
   *    <li>The general contract of {@link Object#equals(Object)} applies:
   *        the relation is reflexive, symmetric and transitive, and
   *        {@code equals(null)} returns {@code false}. All entities of a
   *        namespace must use the same equality implementation, so that
   *        symmetry holds between them.</li>
   *  </ul>
   *
   *  @param obj [in] The object to compare with this entity, may be
   *    {@code null}.
   *  @return {@code true} if {@code obj} is an entity of the same namespace
   *    representing the same backend object, {@code false} otherwise.
   *  @see #hashCode()
   *  @see AbstractEntity
   */
  boolean equals(Object obj);

  /** Returns a hash code value for this entity, consistent with
   *  {@link #equals(Object)}.
   *
   *  <p>Entities that are equal must return the same hash code. As with
   *  {@link #equals(Object)}, this method must not perform I/O, contact the
   *  backend, or throw an exception, and its result must remain constant
   *  for the lifetime of the instance, so that entities can be used as
   *  keys of hash-based collections.</p>
   *
   *  @return The hash code of this entity.
   *  @see #equals(Object)
   */
  int hashCode();  
}
