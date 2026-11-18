package com.optimasc.entity.schema;

import java.util.Iterator;

import com.optimasc.entity.Entity;
import com.optimasc.entity.EntityException;
import com.optimasc.entity.Namespace;
import com.optimasc.schema.ClassDefinition;
import com.optimasc.schema.ItemDefinition;

/** Typed entry point for schema introspection, and the
 *  {@link Namespace} through which schema definitions are
 *  browsable as an entity tree.
 *
 *  <h3>Typed API</h3>
 *  <p>A {@code Schema} provides a type-safe API for querying the
 *  three kinds of schema definition that describe a namespace's
 *  structure:</p>
 *  <ul>
 *    <li>{@link ItemDefinition} &mdash; describes a single named
 *        attribute, property, or column (its data type, cardinality,
 *        ordering, read-only status, etc.).</li>
 *    <li>{@link EntityTypeDefinition} &mdash; describes a data type or
 *        syntax (the Java class used to represent values and an
 *        optional string formatter).</li>
 *    <li>{@link ClassDefinition} &mdash; describes an object class,
 *        table, or document type (mandatory and optional items,
 *        parent class, kind).</li>
 *  </ul>
 *
 *  <h3>Tree view via Namespace</h3>
 *  <p>{@code Schema} extends {@link Namespace}, so the same
 *  definitions are also browsable as a live entity tree through the
 *  standard {@link Namespace#getRoot()} / {@link Entity#getChildren()}
 *  traversal API.  Generic tree consumers (Swing {@code JTree},
 *  CLI walkers, generic exporters) can use this path without any
 *  schema-specific code; schema-aware consumers should prefer the
 *  typed lookup methods below.</p>
 *
 *  <h3>Required tree structure</h3>
 *  <p>Implementations of this interface <strong>must</strong> expose
 *  the following virtual tree from {@link #getRoot()}:</p>
 *  <pre>
 *    (root)                      objectClass = "schema"
 *      &#x251C;&#x2500;&#x2500; AttributeDefinitions/   objectClass = {@link Entity#OBJECT_CLASS_CONTAINER "container"}
 *      &#x2502;     &#x251C;&#x2500;&#x2500; cn                  objectClass = {@link ItemDefinition#OBJECT_CLASS_NAME "AttributeDefinition"}
 *      &#x2502;     &#x251C;&#x2500;&#x2500; mail
 *      &#x2502;     &#x2514;&#x2500;&#x2500; ...
 *      &#x251C;&#x2500;&#x2500; TypeDefinitions/        objectClass = {@link Entity#OBJECT_CLASS_CONTAINER "container"}
 *      &#x2502;     &#x251C;&#x2500;&#x2500; DirectoryString
 *      &#x2502;     &#x2514;&#x2500;&#x2500; ...
 *      &#x2514;&#x2500;&#x2500; ClassDefinitions/       objectClass = {@link Entity#OBJECT_CLASS_CONTAINER "container"}
 *            &#x251C;&#x2500;&#x2500; person
 *            &#x2514;&#x2500;&#x2500; ...
 *  </pre>
 *  <ul>
 *    <li>The root entity's {@link Entity#getObjectClass()} returns
 *        {@link #OBJECT_CLASS_SCHEMA}.</li>
 *    <li>The three container nodes use
 *        {@link Entity#OBJECT_CLASS_CONTAINER} and are named
 *        {@link #CONTAINER_ATTRIBUTE_DEFINITIONS},
 *        {@link #CONTAINER_TYPE_DEFINITIONS}, and
 *        {@link #CONTAINER_CLASS_DEFINITIONS}.</li>
 *    <li>Leaf nodes under each container have the object class of
 *        their definition type: {@link ItemDefinition#OBJECT_CLASS_NAME},
 *        {@link EntityTypeDefinition#OBJECT_CLASS_NAME}, or
 *        {@link ClassDefinition#OBJECT_CLASS_NAME} respectively.</li>
 *    <li>The tree is <em>live</em>: definitions added to or removed
 *        from the schema after construction must be immediately
 *        visible through {@link Entity#getChildren()} on the
 *        appropriate container node.</li>
 *  </ul>
 *
 *  <h3>Scheme</h3>
 *  <p>{@link #getScheme()} returns {@link #SCHEME} ({@value #SCHEME}).
 *  This is an <em>internally derived</em> scheme; it does not appear
 *  in any {@link com.optimasc.entity.NamespaceProvider}'s
 *  supported-scheme list and cannot be opened via a provider URI.</p>
 *
 *  <h3>Lookup semantics</h3>
 *  <p>Lookup by OID is always exact.  Lookup by name follows the
 *  case-sensitivity rules of the underlying implementation
 *  (typically case-insensitive for LDAP-backed schemas,
 *  case-sensitive for XML / RDF-backed schemas).</p>
 *
 *  <p>Implementations are not required to support all three
 *  definition types.  A minimal read-only in-memory schema may
 *  implement only {@link #getItemDefinition(String)} and return
 *  empty iterators for the other two.  Methods that are not
 *  supported should return {@code null} (single-result lookups) or
 *  an empty iterator (collection results) rather than throwing.</p>
 *
 *  <p>This interface is not synchronized against concurrent
 *  multithreaded access.</p>
 *
 *  @see Definition
 *  @see ItemDefinition
 *  @see EntityTypeDefinition
 *  @see ClassDefinition
 *  @author Carl Eric Codere
 */
public interface Schema extends Namespace
{
  // -----------------------------------------------------------------------
  // Scheme and object-class constants
  // -----------------------------------------------------------------------

  /** The URI scheme returned by {@link #getScheme()} for all
   *  {@code Schema} namespaces.
   *
   *  <p>This scheme ({@value}) identifies the schema-introspection
   *  namespace as internally derived. It is not openable through a
   *  {@link com.optimasc.entity.NamespaceProvider} and will not
   *  appear in any provider's supported-scheme list.</p>
   */
  String SCHEME = "schema";

  /** Object-class name reported by the root entity of the schema
   *  tree (the entity returned by {@link #getRoot()}).
   *
   *  <p>A generic tree consumer can use this value to identify
   *  the schema root when the tree is mounted alongside data-tree
   *  nodes.</p>
   */
  String OBJECT_CLASS_SCHEMA = "schema";

  // -----------------------------------------------------------------------
  // Container-node name constants
  // -----------------------------------------------------------------------

  /** Name of the container node that groups all
   *  {@link ItemDefinition} leaf entities under the schema root.
   *
   *  <p>Its {@link Entity#getObjectClass()} returns
   *  {@link Entity#OBJECT_CLASS_CONTAINER}.</p>
   */
  String CONTAINER_ATTRIBUTE_DEFINITIONS = "AttributeDefinitions";

  /** Name of the container node that groups all
   *  {@link EntityTypeDefinition} leaf entities under the schema root.
   *
   *  <p>Its {@link Entity#getObjectClass()} returns
   *  {@link Entity#OBJECT_CLASS_CONTAINER}.</p>
   */
  String CONTAINER_TYPE_DEFINITIONS = "TypeDefinitions";

  /** Name of the container node that groups all
   *  {@link ClassDefinition} leaf entities under the schema root.
   *
   *  <p>Its {@link Entity#getObjectClass()} returns
   *  {@link Entity#OBJECT_CLASS_CONTAINER}.</p>
   */
  String CONTAINER_CLASS_DEFINITIONS = "ClassDefinitions";

  // -----------------------------------------------------------------------
  // Item (attribute) definitions
  // -----------------------------------------------------------------------

  /** Returns the item definition whose name or OID matches
   *  {@code key}, or {@code null} if no such definition exists.
   *
   *  <p>The key may be either the expanded name
   *  (e.g. {@code "http://purl.org/dc/elements/1.1/title"}) or the
   *  OID in dotted-decimal notation (e.g. {@code "2.5.4.3"}).
   *  For definitions registered without a namespace, the expanded
   *  name equals the local name. OID lookup is always exact; name
   *  lookup follows the case-sensitivity rules of the
   *  implementation when it is present.</p>
   *
   * @param key [in] Expanded name, local name (for names with no
   *   namespace), or OID. Must not be {@code null}.
   * @return The matching {@link ItemDefinition}, or {@code null}
   *   if absent.
   */
  ItemDefinition getItemDefinition(String key);

  /** Returns an {@link Iterator} over all item definitions in
   *  this schema, in an implementation-defined order.
   *
   *  <p>Each call returns a fresh, independent iterator. The
   *  iterator does not support {@code remove()}.</p>
   *
   * @return A non-null iterator; may be empty if no item
   *   definitions are registered.
   */
  Iterator<ItemDefinition> getItemDefinitions();

  // -----------------------------------------------------------------------
  // Type (syntax) definitions
  // -----------------------------------------------------------------------

  /** Returns the type definition whose name or OID matches
   *  {@code key}, or {@code null} if no such definition exists.
   *
   * @param key [in] Expanded name, local name (for names with no
   *   namespace), or OID. Must not be {@code null}.
   * @return The matching {@link EntityTypeDefinition}, or {@code null}
   *   if absent.
   */
  EntityTypeDefinition getTypeDefinition(String key);

  /** Returns an {@link Iterator} over all type definitions in
   *  this schema, in an implementation-defined order.
   *
   * @return A non-null iterator; may be empty if no type
   *   definitions are registered.
   */
  Iterator<EntityTypeDefinition> getTypeDefinitions();

  // -----------------------------------------------------------------------
  // Class (object-class) definitions
  // -----------------------------------------------------------------------

  /** Returns the class definition whose name or OID matches
   *  {@code key}, or {@code null} if no such definition exists.
   *
   * @param key [in] Expanded name, local name (for names with no
   *   namespace), or OID. Must not be {@code null}.
   * @return The matching {@link ClassDefinition}, or {@code null}
   *   if absent.
   */
  ClassDefinition getClassDefinition(String key);

  /** Returns an {@link Iterator} over all class definitions in
   *  this schema, in an implementation-defined order.
   *
   * @return A non-null iterator; may be empty if no class
   *   definitions are registered.
   */
  Iterator<ClassDefinition> getClassDefinitions();
}
