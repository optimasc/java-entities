package com.optimasc.entity.schema;

import com.optimasc.datatypes.TypeFactory;
import com.optimasc.datatypes.aggregate.ClassType;
import com.optimasc.datatypes.defined.BinaryType;
import com.optimasc.datatypes.defined.ObjectIdentifierType;
import com.optimasc.datatypes.defined.StringType;
import com.optimasc.datatypes.defined.UCS2CharType;
import com.optimasc.datatypes.defined.UCS2StringType;
import com.optimasc.datatypes.primitives.BooleanType;
import com.optimasc.datatypes.primitives.DateTimeType;
import com.optimasc.datatypes.primitives.IntegralType;
import com.optimasc.text.StandardFormatters;


/**
 * Basic type definitions. This includes and defines the most important and
 * minimum type definitions that can be useful.
 * 
 * <p>
 * The following type definitions are pre-defined and available for use:
 * </p>
 * <table>
 * <tr>
 * <th>Java Name</th>
 * <th>ExpandedName</th>
 * <th>ID</th>
 * <th>Java Object representation</th>
 * <th>Description</th>
 * </tr>
 * <tr>
 * <td>{@link #OCTET_STRING}</td>
 * <td>http://www.w3.org/2001/XMLSchema#hexBinary</td>
 * <td>{@link #OID_OCTET_STRING}</td>
 * <td>byte[]</td>
 * <td>Binary data, represented as hexBinary</td>
 * </tr>
 * <tr>
 * <td>{@link #BOOLEAN}</td>
 * <td>http://www.w3.org/2001/XMLSchema#boolean</td>
 * <td>{@link #OID_BOOLEAN}</td>
 * <td>java.lang.Boolean</td>
 * <td>A boolean value</td>
 * </tr>
 * <tr>
 * <td>{@link #INTEGER}</td>
 * <td>http://www.w3.org/2001/XMLSchema#integer</td>
 * <td>{@link #OID_INTEGER}</td>
 * <td>java.math.BigInteger</td>
 * <td>An integer value</td>
 * </tr>
 * <tr>
 * <td>{@link #OBJECT_IDENTIFIER}</td>
 * <td>objectIdentifier</td>
 * <td>{@link #OID_OBJECT_IDENTIFIER}</td>
 * <td>java.lang.String</td>
 * <td>An object identifier value, where components are digits</td>
 * </tr>
 * <tr>
 * <td>{@link #STRING}</td>
 * <td>http://www.w3.org/2001/XMLSchema#string</td>
 * <td>{@link #OID_STRING}</td>
 * <td>java.lang.String</td>
 * <td>A string value</td>
 * </tr>
 * <tr>
 * <td>{@link #NCNAME}</td>
 * <td>http://www.w3.org/2001/XMLSchema#NCName</td>
 * <td>{@link #OID_NCNAME}</td>
 * <td>java.lang.String</td>
 * <td>A string value compatible with a subset of the XMLSchema NCName syntax</td>
 * </tr>
 * <tr>
 * <td>{@link #JAVA_CLASS}</td>
 * <td>javaClass</td>
 * <td></td>
 * <td>java.lang.Class</td>
 * <td>A java class representation</td>
 * </tr>
 * </table>
 * 
 *  <h2>Lookup</h2>
 *  <p>Each type in this catalog has two stable identifiers: its expanded
 *  name, exposed as a {@code NAME_*} constant, and its OID, exposed
 *  as an {@code OID_*} constant. Either may be passed to
 *  {@link Schema#getTypeDefinition(String)} to retrieve the type. The sole
 *  exception is {@link #JAVA_CLASS}, which has no OID and can be
 *  retrieved only by its name {@code "javaClass"}.</p>
 * 
 * */
public class StandardTypes
{
  /** Object Identifier for syntax of a BOOLEAN */
  public static final String OID_BOOLEAN = "1.3.6.1.4.1.1466.115.121.1.7";
  public static final String NAME_BOOLEAN = "http://www.w3.org/2001/XMLSchema#boolean";
  
  /**
   * Type definition for BOOLEAN type. A Boolean type is represented by a
   * <code>java.lang.Boolean</code> java object instance. 
   */
  public static final EntityTypeDefinition BOOLEAN =
  /* OID, NamespaceURI, name, Description, datatype */
    new DefaultTypeDefinition(OID_BOOLEAN, null,
          "BOOLEAN", "Boolean", new BooleanType());

  /** Object Identifier for syntax of a NCName */
  public static final String OID_NCNAME = "1.3.6.1.4.1.61799.5.40.26.6";
  public static final String NAME_NCNAME = "http://www.w3.org/2001/XMLSchema#NCName";
  
  /**
   * Type definition for NCName type. A NCName type is represented by a
   * <code>java.lang.String</code> java object instance. It is a subset of the
   * <code>NCName</code> type in XMLSchema.
   */
  public static final EntityTypeDefinition NCNAME =
  /* OID, NamespaceURI, name, Description, datatype */
  new DefaultTypeDefinition(OID_NCNAME, 
      null, "NCName", "NCName",
      new StringType(new StandardFormatters.NCNameConverter(),TypeFactory.getDefaultInstance(UCS2CharType.class)));

  /**
   * Object Identifier for syntax of a string, equivalent to
   * UnboundedDirectoryString in X.520
   */
  public static final String OID_STRING = "1.3.6.1.4.1.1466.115.121.1.15";
  public static final String NAME_STRING = "http://www.w3.org/2001/XMLSchema#string";
  /**
   * Type definition for generic string type. This is a generic type
   * representing any unicode string. This is represented by a
   * <code>java.lang.String</code> java object.
   */
  public static final EntityTypeDefinition STRING =
  /* OID, NamespaceURI, name, Description, datatype */
  new DefaultTypeDefinition(OID_STRING, null,
          "BMPString", "UnboundedDirectoryString", new UCS2StringType());
  
  /** Object Identifier for syntax of INTEGER */
  public static final String OID_INTEGER = "1.3.6.1.4.1.1466.115.121.1.27";
  public static final String NAME_INTEGER = "http://www.w3.org/2001/XMLSchema#integer";
  
  /**
   * Type definition for INTEGER type. This is a generic type representing an
   * integer numeric value. This is represented by a
   * <code>java.math.BigInteger</code> java object.
   */
  public static final EntityTypeDefinition INTEGER =
  /* OID, NamespaceURI, name, Description, datatype */
  new DefaultTypeDefinition(
  OID_INTEGER, null, "INTEGER", "INTEGER", new IntegralType());

  /** Object Identifier for syntax of an OBJECT IDENTIFIER */
  public static final String OID_OBJECT_IDENTIFIER = "1.3.6.1.4.1.1466.115.121.1.38";
  public static final String NAME_OBJECT_IDENTIFIER = "objectIdentifier";
  
  /** Type definition for OBJECT IDENTIFIER type. */
  public static final EntityTypeDefinition OBJECT_IDENTIFIER =
  /* OID, NamespaceURI, name, Description, Class, formatter, allowedValues */
  new DefaultTypeDefinition(OID_OBJECT_IDENTIFIER, null, "objectIdentifier", "OBJECT IDENTIFIER",
      new ObjectIdentifierType());

  /** Object Identifier for syntax of an array of bytes / OCTET STRING */
  public static final String OID_OCTET_STRING = "1.3.6.1.4.1.1466.115.121.1.40";
  public static final String NAME_BINARY = "http://www.w3.org/2001/XMLSchema#hexBinary";
  /**
   * Type definition for an OCTET STRING type. This is a generic type
   * representing binary data. This is represented by a <code>byte[]</code>
   * java object.
   */
  public static final EntityTypeDefinition OCTET_STRING =
  /* OID, NamespaceURI, name, Description, Class, formatter, allowedValues */
  new DefaultTypeDefinition(OID_OCTET_STRING, 
      null, "binary",
      "OCTET STRING", new BinaryType());

  /** Object Identifier for syntax of a class type */
  public static final String NAME_JAVA_CLASS = "javaClass";
  /**
   * Type definition for a Java Class type. This is a generic type
   * representing a fully qualified java class name. This is represented by a
   * <code>Class</code> java object.
   */
  public static final EntityTypeDefinition JAVA_CLASS =
  /* OID, NamespaceURI, name, Description, Class, formatter, allowedValues */
  new DefaultTypeDefinition(null, null, "javaClass", "Java Class",
      new ClassType());  

  /** Object Identifier for syntax of a GeneralizedTime */
  public static final String OID_GENERALIZEDTIME = "1.3.6.1.4.1.1466.115.121.1.24";
  /** Object Identifier for syntax of a GeneralizedTime */
  public static final String NAME_GENERALIZEDTIME = "GeneralizedTime";
  /**
   * Type definition for an ASN1 GeneralizedTime. This is a generic type
   * representing a date/time. This is represented by a
   * <code>java.util.GregorianCalendar</code> java object.
   */
  public static final EntityTypeDefinition GENERALIZEDTIME =
  /* OID, NamespaceURI, name, Description, Class, formatter, allowedValues */
  new DefaultTypeDefinition(OID_GENERALIZEDTIME, null, "GeneralizedTime",
      "Generalized Time", new DateTimeType());

}
