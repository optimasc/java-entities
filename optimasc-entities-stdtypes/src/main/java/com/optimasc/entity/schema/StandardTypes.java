package com.optimasc.entity.schema;

import com.optimasc.datatypes.TypeFactory;
import com.optimasc.datatypes.aggregate.ClassType;
import com.optimasc.datatypes.defined.BinaryType;
import com.optimasc.datatypes.defined.ByteType;
import com.optimasc.datatypes.defined.DateType;
import com.optimasc.datatypes.defined.DoubleType;
import com.optimasc.datatypes.defined.IntType;
import com.optimasc.datatypes.defined.LongType;
import com.optimasc.datatypes.defined.NonNegativeIntegerType;
import com.optimasc.datatypes.defined.ObjectIdentifierType;
import com.optimasc.datatypes.defined.ShortType;
import com.optimasc.datatypes.defined.SingleType;
import com.optimasc.datatypes.defined.StringType;
import com.optimasc.datatypes.defined.UCS2CharType;
import com.optimasc.datatypes.defined.UCS2StringType;
import com.optimasc.datatypes.defined.UnsignedByteType;
import com.optimasc.datatypes.defined.UnsignedIntType;
import com.optimasc.datatypes.defined.UnsignedShortType;
import com.optimasc.datatypes.defined.YearType;
import com.optimasc.datatypes.derived.UnicodeCharType;
import com.optimasc.datatypes.generated.LanguageType;
import com.optimasc.datatypes.primitives.BooleanType;
import com.optimasc.datatypes.primitives.DateTimeType;
import com.optimasc.datatypes.primitives.DecimalType;
import com.optimasc.datatypes.primitives.IntegralType;
import com.optimasc.datatypes.primitives.RealType;
import com.optimasc.text.NumericFormatters;
import com.optimasc.text.StandardDateFormatters;
import com.optimasc.text.StandardFormatters;

import omg.org.astm.type.TypeReference;
import omg.org.astm.type.UnnamedTypeReference;


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
  /** Object Identifier for syntax of an LDAP BOOLEAN */
  public static final String OID_BOOLEAN = "1.3.6.1.4.1.1466.115.121.1.7";
  
  /**
   * Type definition for LDAP BOOLEAN type. A Boolean type is represented by a
   * <code>java.lang.Boolean</code> java object instance. 
   */
  public static final EntityTypeDefinition BOOLEAN =
  /* OID, NamespaceURI, name, Description, datatype */
    new DefaultTypeDefinition(OID_BOOLEAN, null,
          "BOOLEAN", "Boolean", new BooleanType(), StandardFormatters.BooleanConverter.getInstance());

  
  

  /** Object Identifier for syntax of a NCName */
  public static final String OID_NCNAME = "1.3.6.1.4.1.61799.5.40.26.6";
  public static final String NAME_NCNAME = "http://www.w3.org/2001/XMLSchema#NCName";
  

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
          "UnboundedDirectoryString", "UnboundedDirectoryString", new StringType(new UnnamedTypeReference(new UnicodeCharType())), StandardFormatters.StringConverter.getInstance());
  
  /** Object Identifier for syntax of INTEGER */
  public static final String OID_INTEGER = "1.3.6.1.4.1.1466.115.121.1.27";
  
  /**
   * Type definition for INTEGER type. This is a generic type representing an
   * integer numeric value. This is represented by a
   * <code>java.math.BigInteger</code> java object.
   */
  public static final EntityTypeDefinition INTEGER =
  /* OID, NamespaceURI, name, Description, datatype */
  new DefaultTypeDefinition(
  OID_INTEGER, null, "INTEGER", "INTEGER", new IntegralType(),NumericFormatters.IntegerCanonicalConverter.getLDAPInstance());

  /** Object Identifier for syntax of an OBJECT IDENTIFIER */
  public static final String OID_OBJECT_IDENTIFIER = "1.3.6.1.4.1.1466.115.121.1.38";
  public static final String NAME_OBJECT_IDENTIFIER = "objectIdentifier";
  
  /** Type definition for OBJECT IDENTIFIER type. */
  public static final EntityTypeDefinition OBJECT_IDENTIFIER =
  /* OID, NamespaceURI, name, Description, Class, formatter, allowedValues */
  new DefaultTypeDefinition(OID_OBJECT_IDENTIFIER, null, "objectIdentifier", "OBJECT IDENTIFIER",
      new ObjectIdentifierType(), StandardFormatters.OIDConverter.getInstance());

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
      null, "OCTET STRING",
      "OCTET STRING", new BinaryType(),null);

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
      new ClassType(),null);  

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
      "Generalized Time", new DateTimeType(), StandardDateFormatters.GeneralizedTimeConverter.getInstance());

  
  /*---------------------- XMLSchemaa 1.1 datatypes ----------------------*/
  
  public static final String XSD_NAMESPACE_URI = "http://www.w3.org/2001/XMLSchema#";
  public static final String XSD_NAME_BOOLEAN = "boolean";
  /**
   * Type definition for XMLSchema boolean type. A boolean type is represented by a
   * <code>java.lang.Boolean</code> java object instance. 
   */
  public static final EntityTypeDefinition XSD_BOOLEAN =
  /* OID, NamespaceURI, name, Description, datatype */
    new DefaultTypeDefinition(null, XSD_NAMESPACE_URI,
          XSD_NAME_BOOLEAN, "XMLSchema boolean", new BooleanType(), StandardFormatters.XSDBooleanConverter.getInstance());
  
  
  public static final String XSD_NAME_BASE64 = "base64Binary";
  /**
   * Type definition for XMLSchema base64Binary type. A base64Binary type is represented by a
   * <code>byte[]</code> java object. 
   */
  public static final EntityTypeDefinition XSD_BASE64BINARY =
  /* OID, NamespaceURI, name, Description, datatype */
    new DefaultTypeDefinition(null, XSD_NAMESPACE_URI,
          XSD_NAME_BASE64, "XMLSchema base64Binary", new BinaryType(), StandardFormatters.XSDBase64BinaryConverter.getInstance());
  
  public static final String XSD_NAME_HEXBINARY = "hexBinary";
  /**
   * Type definition for XMLSchema hexBinary type. A hexBinary type is represented by a
   * <code>byte[]</code> java object. 
   */
  public static final EntityTypeDefinition XSD_HEXBINARY =
  /* OID, NamespaceURI, name, Description, datatype */
    new DefaultTypeDefinition(null, XSD_NAMESPACE_URI,
          XSD_NAME_HEXBINARY, "XMLSchema hexBinary", new BinaryType(), StandardFormatters.XSDHexBinaryConverter.getInstance());
  
  
  public static final String XSD_NAME_LANGUAGE = "language";
  /**
   * Type definition for XMLSchema language type. A language type is represented by a
   * <code>java.util.Locale</code> java object. 
   */
  public static final EntityTypeDefinition XSD_LANGUAGE =
  /* OID, NamespaceURI, name, Description, datatype */
    new DefaultTypeDefinition(null, XSD_NAMESPACE_URI,
          XSD_NAME_LANGUAGE, "XMLSchema language", new LanguageType(), StandardFormatters.LocaleTypeConverter.getInstance());
  
  
  public static final String XSD_NAME_TIMESTAMP = "dateTimeStamp";
  /**
   * Type definition for XMLSchema dateTimeStamp type. A dateTimeStamp type is represented by a
   * <code>com.optimasc.lang.GregorianDatetimeCalendar</code> java object. 
   */
  public static final EntityTypeDefinition XSD_TIMESTAMP =
  /* OID, NamespaceURI, name, Description, datatype */
    new DefaultTypeDefinition(null, XSD_NAMESPACE_URI,
          XSD_NAME_TIMESTAMP, "XMLSchema dateTimeStamp", new DateTimeType(), StandardDateFormatters.XSDDateTimeStampConverter.getInstance());
  
  public static final String XSD_NAME_DATE = "date";
  /**
   * Type definition for XMLSchema date type. A date type is represented by a
   * <code>com.optimasc.lang.GregorianDatetimeCalendar</code> java object. 
   */
  public static final EntityTypeDefinition XSD_DATE =
  /* OID, NamespaceURI, name, Description, datatype */
    new DefaultTypeDefinition(null, XSD_NAMESPACE_URI,
          XSD_NAME_DATE, "XMLSchema date", new DateType(), StandardDateFormatters.XSDDateConverter.getInstance());
  
  
  public static final String XSD_NAME_YEAR = "gYear";
  /**
   * Type definition for XMLSchema gYear type. A gYear
   * type is represented by a
   * <code>com.optimasc.lang.GregorianDatetimeCalendar</code> java object. 
   */
  public static final EntityTypeDefinition XSD_YEAR =
  /* OID, NamespaceURI, name, Description, datatype */
    new DefaultTypeDefinition(null, XSD_NAMESPACE_URI,
        XSD_NAME_YEAR, "XMLSchema gYear", new YearType(), StandardDateFormatters.XSDGYearConverter.getInstance());
  
  
  
  public static final String XSD_NAME_DECIMAL = "decimal";
  /**
   * Type definition for XMLSchema decimal type. A decimal type is represented by a
   * <code>java.math.BigDecimal</code> java object. 
   */
  public static final EntityTypeDefinition XSD_DECIMAL =
  /* OID, NamespaceURI, name, Description, datatype */
    new DefaultTypeDefinition(null, XSD_NAMESPACE_URI,
          XSD_NAME_DECIMAL, "XMLSchema decimal", new DecimalType(), NumericFormatters.XSDDecimalConverter.getInstance());

  public static final String XSD_NAME_INTEGER = "integer";
  /**
   * Type definition for XMLSchema integer type. An integer type is represented by a
   * <code>java.math.BigInteger</code> java object. 
   */
  public static final EntityTypeDefinition XSD_INTEGER =
  /* OID, NamespaceURI, name, Description, datatype */
    new DefaultTypeDefinition(null, XSD_NAMESPACE_URI,
          XSD_NAME_INTEGER, "XMLSchema integer", new IntegralType(), NumericFormatters.XSDIntegerConverter.getInstance());
  
  
  public static final String XSD_NAME_INT8 = "byte";
  /**
   * Type definition for XMLSchema byte type. A byte type is represented by a
   * <code>java.lang.Integer</code> java object. 
   */
  public static final EntityTypeDefinition XSD_INT8 =
  /* OID, NamespaceURI, name, Description, datatype */
    new DefaultTypeDefinition(null, XSD_NAMESPACE_URI,
          XSD_NAME_INT8, "XMLSchema byte", new ByteType(), NumericFormatters.XSDByteConverter.getInstance());
  
  
  public static final String XSD_NAME_INT16 = "short";
  /**
   * Type definition for XMLSchema short type. A short type is represented by a
   * <code>java.lang.Integer</code> java object. 
   */
  public static final EntityTypeDefinition XSD_INT16 =
  /* OID, NamespaceURI, name, Description, datatype */
    new DefaultTypeDefinition(null, XSD_NAMESPACE_URI,
          XSD_NAME_INT16, "XMLSchema short", new ShortType(), NumericFormatters.XSDShortConverter.getInstance());
  
  
  
  public static final String XSD_NAME_INT = "int";
  /**
   * Type definition for XMLSchema int type. An int type is represented by a
   * <code>java.lang.Integer</code> java object. 
   */
  public static final EntityTypeDefinition XSD_INT =
  /* OID, NamespaceURI, name, Description, datatype */
    new DefaultTypeDefinition(null, XSD_NAMESPACE_URI,
          XSD_NAME_INT, "XMLSchema int", new IntType(), NumericFormatters.XSDIntConverter.getInstance());
  
  public static final String XSD_NAME_LONG = "long";
  /**
   * Type definition for XMLSchema long type. A long type is represented by a
   * <code>java.lang.Long</code> java object. 
   */
  public static final EntityTypeDefinition XSD_LONG =
  /* OID, NamespaceURI, name, Description, datatype */
    new DefaultTypeDefinition(null, XSD_NAMESPACE_URI,
          XSD_NAME_LONG, "XMLSchema long", new LongType(), NumericFormatters.XSDLongConverter.getInstance());
  
  public static final String XSD_NAME_UINT8 = "unsignedByte";
  /**
   * Type definition for XMLSchema unsignedByte type. An unsignedByte type is represented by a
   * <code>java.lang.Integer</code> java object. 
   */
  public static final EntityTypeDefinition XSD_UINT8 =
  /* OID, NamespaceURI, name, Description, datatype */
    new DefaultTypeDefinition(null, XSD_NAMESPACE_URI,
          XSD_NAME_UINT8, "XMLSchema unsignedByte", new UnsignedByteType(), NumericFormatters.XSDUnsignedByteConverter.getInstance());
  
  public static final String XSD_NAME_UINT16 = "unsignedShort";
  /**
   * Type definition for XMLSchema unsignedShort type. An unsignedShort type is represented by a
   * <code>java.lang.Integer</code> java object. 
   */
  public static final EntityTypeDefinition XSD_UINT16 =
  /* OID, NamespaceURI, name, Description, datatype */
    new DefaultTypeDefinition(null, XSD_NAMESPACE_URI,
          XSD_NAME_UINT16, "XMLSchema unsignedShort", new UnsignedShortType(), NumericFormatters.XSDUnsignedShortConverter.getInstance());

  public static final String XSD_NAME_UINT32 = "unsignedInt";
  /**
   * Type definition for XMLSchema unsignedInt type. An unsignedShort type is represented by a
   * <code>java.lang.Long</code> java object. 
   */
  public static final EntityTypeDefinition XSD_UINT32 =
  /* OID, NamespaceURI, name, Description, datatype */
    new DefaultTypeDefinition(null, XSD_NAMESPACE_URI,
          XSD_NAME_UINT32, "XMLSchema unsignedInt", new UnsignedIntType(), NumericFormatters.XSDUnsignedIntConverter.getInstance());
  
  
  public static final String XSD_NAME_NON_NEGATIVE_INTEGER = "nonNegativeInteger";
  /**
   * Type definition for XMLSchema nonNegativeInteger type. A nonNegativeInteger
   * type is represented by a
   * <code>java.math.BigInteger</code> java object. 
   */
  public static final EntityTypeDefinition XSD_NON_NEGATIVE_INTEGER =
  /* OID, NamespaceURI, name, Description, datatype */
    new DefaultTypeDefinition(null, XSD_NAMESPACE_URI,
        XSD_NAME_NON_NEGATIVE_INTEGER, "XMLSchema nonNegativeInteger", new NonNegativeIntegerType(), NumericFormatters.XSDNonNegativeIntegerConverter.getInstance());
  
  
  public static final String XSD_NAME_FLOAT = "float";
  /**
   * Type definition for XMLSchema float type. A float type is represented by a
   * <code>java.lang.Float</code> java object. 
   */
  public static final EntityTypeDefinition XSD_FLOAT =
  /* OID, NamespaceURI, name, Description, datatype */
    new DefaultTypeDefinition(null, XSD_NAMESPACE_URI,
          XSD_NAME_FLOAT, "XMLSchema float", new SingleType(), NumericFormatters.XSDFloatConverter.getInstance());
  
  public static final String XSD_NAME_DOUBLE = "double";
  /**
   * Type definition for XMLSchema double type. A double type is represented by a
   * <code>java.lang.Double</code> java object. 
   */
  public static final EntityTypeDefinition XSD_DOUBLE =
  /* OID, NamespaceURI, name, Description, datatype */
    new DefaultTypeDefinition(null, XSD_NAMESPACE_URI,
          XSD_NAME_DOUBLE, "XMLSchema double", new DoubleType(), NumericFormatters.XSDDoubleConverter.getInstance());
  
  
  public static final String XSD_NAME_STRING = "string";
  /**
   * Type definition for XMLSchema string type. A string type is represented by a
   * <code>java.lang.String</code> java object. 
   */
  public static final EntityTypeDefinition XSD_STRING =
  /* OID, NamespaceURI, name, Description, datatype */
    new DefaultTypeDefinition(null, XSD_NAMESPACE_URI,
          XSD_NAME_STRING, "XMLSchema string", new StringType(new UnnamedTypeReference(new UnicodeCharType())), StandardFormatters.XSDStringConverter.getInstance());
  
  
  
  
  
  public static final String XSD_NAME_NORMALIZED_STRING = "normalizedString";
  /**
   * Type definition for XMLSchema normalizedString type. A normaliedString type is represented by a
   * <code>java.lang.String</code> java object. 
   */
  public static final EntityTypeDefinition XSD_NORMALIZED_STRING =
  /* OID, NamespaceURI, name, Description, datatype */
    new DefaultTypeDefinition(null, XSD_NAMESPACE_URI,
          XSD_NAME_NORMALIZED_STRING, "XMLSchema normalizedString", new StringType(new UnnamedTypeReference(new UnicodeCharType())), StandardFormatters.XSDNormalizedStringConverter.getInstance());
  
  public static final String XSD_NAME_TOKEN = "token";
  /**
   * Type definition for XMLSchema token type. A token type is represented by a
   * <code>java.lang.String</code> java object. 
   */
  public static final EntityTypeDefinition XSD_TOKEN =
  /* OID, NamespaceURI, name, Description, datatype */
    new DefaultTypeDefinition(null, XSD_NAMESPACE_URI,
          XSD_NAME_TOKEN, "XMLSchema token", new StringType(new UnnamedTypeReference(new UnicodeCharType())), StandardFormatters.XSDTokenConverter.getInstance());
  
  
//  public static final String XSD_NAME_UINT64 = "unsignedLong";
  /**
   * Type definition for XMLSchema unsignedLong type. An unsignedLong type is represented by a
   * <code>java.math.BigInteger</code> java object. 
   */
//  public static final EntityTypeDefinition XSD_UINT64 =
//  /* OID, NamespaceURI, name, Description, datatype */
//    new DefaultTypeDefinition(null, XSD_NAMESPACE_URI,
//          XSD_NAME_UINT64, "XMLSchema unsignedLong", new UnsignedLongType(), NumericFormatters.XSDUnsignedLongConverter.getInstance());

}
