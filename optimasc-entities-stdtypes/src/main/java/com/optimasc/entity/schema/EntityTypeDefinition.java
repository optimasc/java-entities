package com.optimasc.entity.schema;

import com.optimasc.datatypes.Datatype;
import com.optimasc.schema.Definition;
import com.optimasc.schema.TypeDefinition;

/** Entity-aware extension of {@link TypeDefinition} that additionally
 *  exposes the full {@link Datatype} facet model.
 *
 *  <p>While {@link TypeDefinition} provides only the minimal contract
 *  needed to identify a type and validate values, this interface
 *  integrates with the entity-tree infrastructure of
 *  {@code com.optimasc.entity} and binds the type definition to a
 *  {@link Datatype} instance that carries the complete value-space
 *  description: representation class, numeric range, length, pattern,
 *  and enumeration facets.</p>
 *
 *  <p>Implementations of this interface are nodes in the schema entity
 *  tree exposed by {@link Schema}: they appear under the
 *  {@code TypeDefinitions} container returned by the schema root, and
 *  may be browsed generically through the {@link com.optimasc.entity.Entity}
 *  API.  The object-class name that identifies such nodes is
 *  {@link #OBJECT_CLASS_NAME}.</p>
 *
 *  <p>The {@link Datatype} is mandatory for a valid instance; its
 *  {@link Datatype#getClassType()} method supersedes
 *  {@link TypeDefinition#getValueClass()}, and its facets are the
 *  authoritative source for {@link TypeDefinition#isValid(Object)}.
 *  Serialisation of values to and from any wire format (ASN.1, XML
 *  Schema, LDAP, SQL, …) remains outside the scope of this interface
 *  and is the responsibility of the consuming backend adapter.</p>
 *
 *  <p>For the human-readable description of the type, consumers must
 *  use {@link Definition#getDescription()}, which is authoritative;
 *  any comment carried by the {@link Datatype} itself is an internal
 *  note and is not intended for catalog display.</p>
 *
 *  @see TypeDefinition
 *  @see Datatype
 *  @see Schema
 *  @author Carl Eric Codere
 */
public interface EntityTypeDefinition extends TypeDefinition
{
  /** Object-class name that identifies type-definition nodes in the
   *  schema entity tree.
   *
   *  <p>Generic tree consumers can compare
   *  {@link com.optimasc.entity.Entity#getObjectClass()} against this
   *  constant to distinguish type-definition nodes from
   *  attribute-definition ({@link EntityItemDefinition#OBJECT_CLASS_NAME})
   *  or class-definition ({@link EntityClassDefinition#OBJECT_CLASS_NAME})
   *  nodes when traversing the schema tree exposed by {@link Schema}.</p>
   */
  String OBJECT_CLASS_NAME = "TypeDefinition";

  /** Attribute key under which the {@link Datatype} payload is stored.
   *
   *  <p>Exposed as a constant so that generic attribute-bag consumers
   *  can retrieve the datatype without going through
   *  {@link #getDatatype()}.  Prefer {@link #getDatatype()} in typed
   *  code.</p>
   */
  String KEY_DATATYPE = "DATATYPE";

  /** Returns the {@link Datatype} that defines the value space of
   *  this type.
   *
   *  <p>The datatype carries the representation class
   *  ({@link Datatype#getClassType()}) and all constraining facets.
   *  It is the single authoritative source for value-space semantics;
   *  {@link #getValueClass()} and {@link #isValid(Object)} must
   *  delegate to it.</p>
   *
   *  <p>For catalog display, prefer {@link Definition#getDescription()}
   *  over any comment carried by the returned {@link Datatype}.</p>
   *
   * @return Non-null {@link Datatype} for a validly constructed
   *         instance.
   */
  Datatype getDatatype();
}