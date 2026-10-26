package com.optimasc.entity.schema;

import com.optimasc.schema.ClassDefinition.ObjectClassKind;
import com.optimasc.schema.ClassDefinition;
import com.optimasc.schema.Definition;
import com.optimasc.schema.ItemDefinition;

/**
 * Pre-defined class definitions for the three core schema definition
 * types: {@link ClassDefinition}, {@link ItemDefinition}, and
 * {@link EntityTypeDefinition}.
 *
 * <p>All definitions form a hierarchy rooted at {@link #CLASS_TOP},
 * which corresponds to the {@code top} abstract object class defined
 * in ITU-T X.501 (OID {@link #OID_TOP}). The remaining definitions
 * are structural classes that extend {@code top}.</p>
 *
 * <p>The following class definitions are pre-defined:</p>
 *
 * <table border="1">
 *   <tr>
 *     <th>Field</th>
 *     <th>Name</th>
 *     <th>OID</th>
 *     <th>Description</th>
 *   </tr>
 *   <tr>
 *     <td>{@link #CLASS_TOP}</td>
 *     <td>top</td>
 *     <td>{@link #OID_TOP}</td>
 *     <td>Root abstract class (ITU-T X.501)</td>
 *   </tr>
 *   <tr>
 *     <td>{@link #CLASS_DEFINITION}</td>
 *     <td>ClassDefinition</td>
 *     <td>none</td>
 *     <td>Schema definition of an object class</td>
 *   </tr>
 *   <tr>
 *     <td>{@link #ATTRIBUTE_DEFINITION}</td>
 *     <td>AttributeDefinition</td>
 *     <td>none</td>
 *     <td>Schema definition of a named attribute or property</td>
 *   </tr>
 *   <tr>
 *     <td>{@link #TYPE_DEFINITION}</td>
 *     <td>TypeDefinition</td>
 *     <td>none</td>
 *     <td>Schema definition of a data type or syntax</td>
 *   </tr>
 * </table>
 *
 * @see ClassDefinition
 * @see ItemDefinition
 * @see EntityTypeDefinition
 * @see DefaultClassDefinition
 * @author Carl Eric Codere
 */
public class StandardClasses
{
  /** OID of the {@code top} abstract class as defined in ITU-T X.501. */
  public static final String OID_TOP = "2.5.6.0";

  /** Local name of the root abstract class. */
  public static final String NAME_TOP = "top";

  /** Root abstract class definition as defined in ITU-T X.501.
   *  Every structural class in this schema ultimately derives from
   *  {@code top}. The only mandatory attribute is
   *  {@link Definition#KEY_OBJECT_CLASS_NAME}, consistent with the
   *  LDAP {@code objectClass} requirement. */
  public static final DefaultClassDefinition CLASS_TOP = new DefaultClassDefinition(
      OID_TOP,                                        // oid
      null,                                           // namespaceURI
      NAME_TOP,                                       // localName
      "Root abstract class (ITU-T X.501)",            // description
      null,                                           // parentClass
      ObjectClassKind.abstractClass,                  // classKind
      new String[] { Definition.KEY_OBJECT_CLASS_NAME } // mandatoryAttributes
  );

  /** Local name of the class definition object class. */
  public static final String NAME_CLASS_DEFINITION = "ClassDefinition";

  /** Class definition for schema definitions that describe object
   *  classes. Instances must carry at minimum a local name and an
   *  object class identifier. */
  public static final DefaultClassDefinition CLASS_DEFINITION = new DefaultClassDefinition(
      null,                                           // oid
      null,                                           // namespaceURI
      NAME_CLASS_DEFINITION,                          // localName
      "Schema definition of an object class",         // description
      NAME_TOP,                                       // parentClass
      ObjectClassKind.structuralClass,                // classKind
      new String[] { Definition.KEY_NAME,
                     Definition.KEY_OBJECT_CLASS_NAME } // mandatoryAttributes
  );

  /** Local name of the attribute definition object class. */
  public static final String NAME_ATTRIBUTE_DEFINITION = "AttributeDefinition";

  /** Class definition for schema definitions that describe named
   *  attributes or properties. Instances must carry at minimum a
   *  local name, an object class identifier, and a type name
   *  identifying the syntax of the attribute's values. */
  public static final DefaultClassDefinition ATTRIBUTE_DEFINITION = new DefaultClassDefinition(
      null,                                           // oid
      null,                                           // namespaceURI
      NAME_ATTRIBUTE_DEFINITION,                      // localName
      "Schema definition of a named attribute",       // description
      NAME_TOP,                                       // parentClass
      ObjectClassKind.structuralClass,                // classKind
      new String[] { Definition.KEY_NAME,
                     Definition.KEY_OBJECT_CLASS_NAME,
                     ItemDefinition.KEY_TYPE_NAME }   // mandatoryAttributes
  );

  /** Local name of the type definition object class. */
  public static final String NAME_TYPE_DEFINITION = "TypeDefinition";

  /** Class definition for schema definitions that describe data types
   *  or syntaxes. Instances must carry at minimum a local name, an
   *  object class identifier, and a Java class name identifying the
   *  runtime representation of values of this type. */
  public static final DefaultClassDefinition TYPE_DEFINITION = new DefaultClassDefinition(
      null,                                           // oid
      null,                                           // namespaceURI
      NAME_TYPE_DEFINITION,                           // localName
      "Schema definition of a data type or syntax",   // description
      NAME_TOP,                                       // parentClass
      ObjectClassKind.structuralClass,                // classKind
      new String[] { Definition.KEY_NAME,
                     Definition.KEY_OBJECT_CLASS_NAME,
                     ClassDefinition.KEY_TYPE_JAVA_CLASS_NAME } // mandatoryAttributes
  );
}