package com.optimasc.entity.schema;

import com.optimasc.schema.ClassDefinition;
import com.optimasc.schema.Definition;
import com.optimasc.schema.ItemDefinition;

/**
 * Basic Attribute definitions. This includes and defines the attributes that
 * are used and defined in the different definition classes defined in this
 * package such as {@link Entity}, {@link Definition}, {@link ClassDefinition}
 * and {@link EntityTypeDefinition}.
 * 
 * <p>
 * The following attribute definitions are pre-defined and available for use:
 * </p>
 * <table>
 * <tr>
 * <th>Java Name</th>
 * <th>Name</th>
 * <th>ID</th>
 * <th>Description</th>
 * </tr>
 * <tr>
 * <td>{@link #ATTRIBUTE_NAME}</td>
 * <td>{@link Definition#KEY_NAME}</td>
 * <td>{@link #OID_NAME}</td>
 * <td>Unique qualified name attribute, descriptor in LDAP identifying the
 * definition</td>
 * </tr>
 * <tr>
 * <td>{@link #ATTRIBUTE_DESCRIPTION}</td>
 * <td>{@link Definition#KEY_DESCRIPTION}</td>
 * <td>{@link #OID_DESCRIPTION}</td>
 * <td>Description attribute, describes the definition</td>
 * </tr>
 * <tr>
 * <td>{@link #ATTRIBUTE_OBJECT_IDENTIFIER}</td>
 * <td>{@link Definition#KEY_OID}</td>
 * <td>{@link #OID_OBJECT_IDENTIFIER}</td>
 * <td>Object identifier, unique identification of the definition</td>
 * </tr>
 * <tr>
 * <td>{@link #ATTRIBUTE_DISPLAY_NAME}</td>
 * <td>{@link Definition#KEY_DISPLAY_NAME}</td>
 * <td>{@link #OID_DISPLAY_NAME}</td>
 * <td>Display name attribute, display label associated with the definition</td>
 * </tr>
 * <tr>
 * <td>{@link #ATTRIBUTE_NAME_NS_URI}</td>
 * <td>{@link Definition#KEY_NAME_NS_URI}</td>
 * <td></td>
 * <td>Namespace URI attribute, defines the namespace URI used associated with
 * the name.</td>
 * </tr>
 * <tr>
 * <td>{@link #ATTRIBUTE_OBSOLETE}</td>
 * <td>{@link Definition#KEY_OBSOLETE}</td>
 * <td></td>
 * <td>Obsolete attribute, indicates if the definition is obsolete or not.</td>
 * </tr>
 * </table>
 * 
 * 
 * @author Carl Eric Codere
 *
 */
public class StandardAttributes
{
  /** Object Identifier for the 'commonName' attribute */
  public static final String OID_NAME = "2.5.4.3";
  /** Object Identifier for the 'description' attribute */
  public static final String OID_DESCRIPTION = "2.5.4.13";
  /** Object Identifier for the 'Object Identifier' attribute */
  public static final String OID_OBJECT_IDENTIFIER = "2.5.4.106";
  /** Object Identifier for the 'displayName' attribute */
  public static final String OID_DISPLAY_NAME = "2.16.840.1.113730.3.1.241";
  /** Object Identifier for the 'objectClass' attribute */
  public static final String OID_OBJECT_CLASS_NAME = "2.5.4.0";

  /**
   * Attribute definition for the 'commonName' ITU-T X.520 attribute. This defines
   * the {@link Definition#KEY_NAME} value.
   */
  public static final DefaultItemDefinition ATTRIBUTE_NAME = new DefaultItemDefinition(
      // Unique Identifier, namespaceURI, qualifiedName, description, typeName, multipleValueType,
      // contextType, readOnly
      OID_NAME, null, Definition.KEY_NAME, "commonName", StandardTypes.OID_STRING,
      DefaultItemDefinition.VALUE_TYPE_SINGLE, null, false);

  /** The definition of the "objectClass" attribute */
  public static final DefaultItemDefinition ATTRIBUTE_OBJECT_CLASS = new DefaultItemDefinition(
      // Unique Identifier, namespaceURI, qualifiedName, description, typeName, multipleValueType,
      // contextType, readOnly
      OID_OBJECT_CLASS_NAME, null, Definition.KEY_OBJECT_CLASS_NAME,
      "Object class of this object", StandardTypes.OID_STRING,
      DefaultItemDefinition.VALUE_TYPE_SINGLE, null, false);

  /**
   * Attribute definition for the 'namespaceURI' attribute. This defines the
   * {@link Definition#KEY_NAME_NS_URI} value.
   */
  public static final DefaultItemDefinition ATTRIBUTE_NAME_NS_URI = new DefaultItemDefinition(
      // Unique Identifier, namespaceURI, qualifiedName, description, typeName, multipleValueType,
      // contextType, readOnly
      null, null, Definition.KEY_NAME_NS_URI, "Name Namespace URI", StandardTypes.OID_STRING,
      DefaultItemDefinition.VALUE_TYPE_SINGLE, null, false);

  /**
   * Attribute definition for the 'obsolete' attribute. This defines the
   * {@link Definition#KEY_OBSOLETE} value.
   */
  public static final DefaultItemDefinition ATTRIBUTE_OBSOLETE = new DefaultItemDefinition(
      // Unique Identifier, namespaceURI, qualifiedName, description, typeName, multipleValueType,
      // contextType, readOnly
      null, null, Definition.KEY_OBSOLETE, "Definition is obsolete",
      StandardTypes.OID_BOOLEAN, DefaultItemDefinition.VALUE_TYPE_SINGLE, null, false);

  /**
   * Attribute definition for the 'x-origin' attribute. This defines the
   * {@link Definition#KEY_ORIGIN} value.
   */
  public static final DefaultItemDefinition ATTRIBUTE_ORIGIN = new DefaultItemDefinition(
      // Unique Identifier, namespaceURI, qualifiedName, description, typeName, multipleValueType,
      // contextType, readOnly
      null, null, Definition.KEY_ORIGIN, "Origin of this definition",
      StandardTypes.OID_STRING, DefaultItemDefinition.VALUE_TYPE_SINGLE, null, false);

  /**
   * Attribute definition for the 'description' ITU-T X.520 attribute. This
   * defines the {@link Definition#KEY_DESCRIPTION} value.
   */
  public static final DefaultItemDefinition ATTRIBUTE_DESCRIPTION = new DefaultItemDefinition(
      // Unique Identifier, namespaceURI, qualifiedName, description, typeName, multipleValueType,
      // contextType, readOnly
      OID_DESCRIPTION, null, Definition.KEY_DESCRIPTION, "Description",
      StandardTypes.OID_STRING + "{" + Integer.toString(Definition.DESC_MAX_LENGTH) + "}",
      DefaultItemDefinition.VALUE_TYPE_SINGLE, null, false);

  /**
   * Attribute definition for the 'Object Identifier' ITU-T X.520 attribute.
   * This defines the {@link Definition#KEY_OID} value.
   */
  public static final DefaultItemDefinition ATTRIBUTE_OBJECT_IDENTIFIER = new DefaultItemDefinition(
      // Unique Identifier, namespaceURI, qualifiedName, description, typeName, multipleValueType,
      // contextType, readOnly
      OID_OBJECT_IDENTIFIER, null, Definition.KEY_OID, "Object Identifier",
      StandardTypes.OID_OBJECT_IDENTIFIER, DefaultItemDefinition.VALUE_TYPE_SINGLE, null,
      false);

  /**
   * Attribute definition for the 'displayName' IETF RFC 2798 used as a
   * friendly name to display to the user. This defines the
   * {@link Definition#KEY_DISPLAY_NAME} value.
   */
  public static final DefaultItemDefinition ATTRIBUTE_DISPLAY_NAME = new DefaultItemDefinition(
      // Unique Identifier, namespaceURI, qualifiedName, description, typeName, multipleValueType,
      // contextType, readOnly
      OID_DISPLAY_NAME, null, Definition.KEY_DISPLAY_NAME, "Display name",
      StandardTypes.OID_STRING, DefaultItemDefinition.VALUE_TYPE_SINGLE, null, false);

  /*------------------ Type definitions attributes. ---------------------*/

  /**
   * Attribute definition for the 'Datatype' used to represent the 
   * datatype. java object instance. This is a fully qualified name of a class.
   * {@link EntityTypeDefinition#KEY_DATATYPE} value.
   */
  public static final DefaultItemDefinition ATTRIBUTE_DATATYPE_NAME = new DefaultItemDefinition(
      // Unique Identifier, namespaceURI, qualifiedName, description, typeName, multipleValueType,
      // contextType, readOnly
      null, null, EntityTypeDefinition.KEY_DATATYPE, "datatype",
      StandardTypes.NAME_JAVA_CLASS, DefaultItemDefinition.VALUE_TYPE_SINGLE, null, false);

  /**
   * Attribute definition for the 'Class' used to indicate the fully qualify
   * named value to represent a value for this type. See
   * {@link EntityTypeDefinition#KEY_TYPE_JAVA_CLASS_NAME} value.
   */
  public static final DefaultItemDefinition ATTRIBUTE_TYPE_JAVA_CLASS_NAME = new DefaultItemDefinition(
      // Unique Identifier, namespaceURI, qualifiedName, description, typeName, multipleValueType,
      // contextType, readOnly
      null, null, ClassDefinition.KEY_TYPE_JAVA_CLASS_NAME,
      "Type definition associated class name for value", StandardTypes.OID_STRING,
      DefaultItemDefinition.VALUE_TYPE_SINGLE, null, false);

  /*------------------ Item definitions attributes. ---------------------*/

  /**
   * Attribute definition to be used to indicate that the item's definition
   * values are read-only. See {@link ItemDefinition#KEY_READONLY} value.
   */
  public static final DefaultItemDefinition ATTRIBUTE_READ_ONLY = new DefaultItemDefinition(
      // Unique Identifier, namespaceURI, qualifiedName, description, typeName, multipleValueType,
      // contextType, readOnly
      null, null, ItemDefinition.KEY_READONLY, "Read-only attribute value",
      StandardTypes.OID_BOOLEAN, DefaultItemDefinition.VALUE_TYPE_SINGLE, null, false);

  /**
   * Attribute definition to be used to indicate that the item's definition
   * values's types. See {@link ItemDefinition#KEY_TYPE_NAME} value.
   */
  public static final DefaultItemDefinition ATTRIBUTE_TYPE_NAME = new DefaultItemDefinition(
      // Unique Identifier, namespaceURI, qualifiedName, description, typeName, multipleValueType,
      // contextType, readOnly
      null, null, ItemDefinition.KEY_TYPE_NAME, "Type name of values",
      StandardTypes.OID_STRING, DefaultItemDefinition.VALUE_TYPE_SINGLE, null, false);

  /**
   * Attribute definition to be used to indicate that the maximum allowed
   * length of these values. This is only applicable to sequences (character
   * strings or octet strings). See {@link ItemDefinition#KEY_MAX_VALUE_LENGTH}
   * value.
   */
  public static final DefaultItemDefinition ATTRIBUTE_MAX_VALUE_LENGTH = new DefaultItemDefinition(
      // Unique Identifier, namespaceURI, qualifiedName, description, typeName, multipleValueType,
      // contextType, readOnly
      null, null, ItemDefinition.KEY_MAX_VALUE_LENGTH, "The maximum length allowed",
      StandardTypes.OID_INTEGER, DefaultItemDefinition.VALUE_TYPE_SINGLE, null, false);

  /**
   * Attribute definition to be used to indicate the allowed value syntaxes.
   * See {@link ItemDefinition#KEY_VALUE_TYPE} value.
   */
  public static final DefaultItemDefinition ATTRIBUTE_VALUE_TYPE = new DefaultItemDefinition(
      // Unique Identifier, namespaceURI, qualifiedName, description, typeName, multipleValueType,
      // contextType, readOnly
      null, null, ItemDefinition.KEY_VALUE_TYPE, "The value type",
      StandardTypes.OID_STRING, DefaultItemDefinition.VALUE_TYPE_SINGLE, null, false);

}
