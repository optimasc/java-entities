package com.optimasc.entity.schema;

import com.optimasc.datatypes.aggregate.ClassType;
import com.optimasc.datatypes.defined.ASCIICharType;
import com.optimasc.datatypes.defined.BinaryType;
import com.optimasc.datatypes.defined.ByteType;
import com.optimasc.datatypes.defined.DateType;
import com.optimasc.datatypes.defined.DoubleType;
import com.optimasc.datatypes.defined.IntType;
import com.optimasc.datatypes.defined.LongType;
import com.optimasc.datatypes.defined.ObjectIdentifierType;
import com.optimasc.datatypes.defined.PercentageType;
import com.optimasc.datatypes.defined.ShortType;
import com.optimasc.datatypes.defined.SingleType;
import com.optimasc.datatypes.defined.StringType;
import com.optimasc.datatypes.defined.UnsignedByteType;
import com.optimasc.datatypes.defined.UnsignedIntType;
import com.optimasc.datatypes.defined.UnsignedLongType;
import com.optimasc.datatypes.defined.UnsignedShortType;
import com.optimasc.datatypes.defined.VisibleCharType;
import com.optimasc.datatypes.derived.UnicodeCharType;
import com.optimasc.datatypes.generated.LanguageType;
import com.optimasc.datatypes.primitives.BooleanType;
import com.optimasc.datatypes.primitives.CalendarDurationType;
import com.optimasc.datatypes.primitives.DateTimeType;
import com.optimasc.datatypes.primitives.IntegralType;
import com.optimasc.datatypes.primitives.MediaTypeType;
import com.optimasc.datatypes.primitives.TimeType;
import com.optimasc.datatypes.primitives.URIType;
import com.optimasc.date.DateTime;
import com.optimasc.text.CalendarDurationConverter;
import com.optimasc.text.NumericFormatters;
import com.optimasc.text.StandardDateFormatters;
import com.optimasc.text.StandardFormatters;

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
 * <td>binary</td>
 * <td>{@link #OID_OCTET_STRING}</td>
 * <td>byte[]</td>
 * <td>Binary data, exchanged with the backend as raw octets</td>
 * </tr>
 * <tr>
 * <td>{@link #BOOLEAN}</td>
 * <td>BOOLEAN</td>
 * <td>{@link #OID_BOOLEAN}</td>
 * <td>java.lang.Boolean</td>
 * <td>A boolean value</td>
 * </tr>
 * <tr>
 * <td>{@link #INTEGER}</td>
 * <td>INTEGER</td>
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
 * <td>UnboundedDirectoryString</td>
 * <td>{@link #OID_STRING}</td>
 * <td>java.lang.String</td>
 * <td>A string value</td>
 * </tr>
 * <tr>
 * <td>{@link #GENERALIZEDTIME}</td>
 * <td>GeneralizedTime</td>
 * <td>{@link #OID_GENERALIZEDTIME}</td>
 * <td>com.optimasc.lang.GregorianDatetimeCalendar</td>
 * <td>A date and time with millisecond precision and timezone</td>
 * </tr>
 * <tr>
 * <td>{@link #IA5STRING}</td>
 * <td>IA5String</td>
 * <td>{@link #OID_IA5STRING}</td>
 * <td>java.lang.String</td>
 * <td>A string of characters from U+0000 to U+007F</td>
 * </tr>
 * <tr>
 * <td>{@link #PRINTABLESTRING}</td>
 * <td>PrintableString</td>
 * <td>{@link #OID_PRINTABLESTRING}</td>
 * <td>java.lang.String</td>
 * <td>A string of characters from the PrintableString repertoire</td>
 * </tr>
 * <tr>
 * <td>{@link #DATE}</td>
 * <td>Date</td>
 * <td>{@link #OID_DATE}</td>
 * <td>com.optimasc.lang.GregorianDatetimeCalendar</td>
 * <td>A date with day precision, with a year from 1582 to 9999</td>
 * </tr>
 * <tr>
 * <td>{@link #DATETIME}</td>
 * <td>Date-Time</td>
 * <td>{@link #OID_DATETIME}</td>
 * <td>com.optimasc.lang.GregorianDatetimeCalendar</td>
 * <td>A date and local time with second precision and no timezone, with a year from 1582 to 9999</td>
 * </tr>
 * <tr>
 * <td>{@link #DURATION}</td>
 * <td>Duration</td>
 * <td>{@link #OID_DURATION}</td>
 * <td>javax.xml.datatype.Duration</td>
 * <td>An elapsed time with millisecond precision</td>
 * </tr>
 * <tr>
 * <td>{@link #FLOAT32}</td>
 * <td>Float32</td>
 * <td>{@link #OID_FLOAT32}</td>
 * <td>java.lang.Float</td>
 * <td>An IEEE 754 single precision floating point value</td>
 * </tr>
 * <tr>
 * <td>{@link #FLOAT64}</td>
 * <td>Float64</td>
 * <td>{@link #OID_FLOAT64}</td>
 * <td>java.lang.Double</td>
 * <td>An IEEE 754 double precision floating point value</td>
 * </tr>
 * <tr>
 * <td>{@link #INT8}</td>
 * <td>Int8</td>
 * <td>{@link #OID_INT8}</td>
 * <td>java.lang.Integer</td>
 * <td>A signed integer within the range -128 to 127</td>
 * </tr>
 * <tr>
 * <td>{@link #INT16}</td>
 * <td>Int16</td>
 * <td>{@link #OID_INT16}</td>
 * <td>java.lang.Integer</td>
 * <td>A signed integer within the range -32768 to 32767</td>
 * </tr>
 * <tr>
 * <td>{@link #INT32}</td>
 * <td>Int32</td>
 * <td>{@link #OID_INT32}</td>
 * <td>java.lang.Integer</td>
 * <td>A signed integer within the range -2147483648 to 2147483647</td>
 * </tr>
 * <tr>
 * <td>{@link #INT64}</td>
 * <td>Int64</td>
 * <td>{@link #OID_INT64}</td>
 * <td>java.lang.Long</td>
 * <td>A signed integer within the range -9223372036854775808 to 9223372036854775807</td>
 * </tr>
 * <tr>
 * <td>{@link #LANGUAGE}</td>
 * <td>Language</td>
 * <td>{@link #OID_LANGUAGE}</td>
 * <td>java.util.Locale</td>
 * <td>A language tag as defined by IETF BCP 47</td>
 * </tr>
 * <tr>
 * <td>{@link #MEDIA_TYPE}</td>
 * <td>MediaType</td>
 * <td>{@link #OID_MEDIA_TYPE}</td>
 * <td>com.optimasc.lang.MediaType</td>
 * <td>An Internet media type as defined by IETF RFC 6838</td>
 * </tr>
 * <tr>
 * <td>{@link #NCNAME}</td>
 * <td>NCName</td>
 * <td>{@link #OID_NCNAME}</td>
 * <td>java.lang.String</td>
 * <td>An identifier that is a subset of the XML NCName</td>
 * </tr>
 * <tr>
 * <td>{@link #NORMALIZED_STRING}</td>
 * <td>NormalizedString</td>
 * <td>{@link #OID_NORMALIZED_STRING}</td>
 * <td>java.lang.String</td>
 * <td>A whitespace-normalized Unicode string</td>
 * </tr>
 * <tr>
 * <td>{@link #OPENDATE}</td>
 * <td>OpenDate</td>
 * <td>{@link #OID_OPENDATE}</td>
 * <td>com.optimasc.lang.GregorianDatetimeCalendar</td>
 * <td>A date with year, month or day precision, or a date and time with timezone, with a year from 1582 to 9999</td>
 * </tr>
 * <tr>
 * <td>{@link #PERCENTAGE}</td>
 * <td>Percentage</td>
 * <td>{@link #OID_PERCENTAGE}</td>
 * <td>java.lang.Integer</td>
 * <td>An integer within the range 0 to 100</td>
 * </tr>
 * <tr>
 * <td>{@link #QUALIFIED_NAME}</td>
 * <td>QualifiedName</td>
 * <td>{@link #OID_QUALIFIED_NAME}</td>
 * <td>java.lang.String</td>
 * <td>An identifier using namespaces, that is a subset of the XML QName</td>
 * </tr>
 * <tr>
 * <td>{@link #REAL}</td>
 * <td>Real</td>
 * <td>{@link #OID_REAL}</td>
 * <td>java.lang.Double</td>
 * <td>A real number, which is rounded to double precision</td>
 * </tr>
 * <tr>
 * <td>{@link #TEXT}</td>
 * <td>Text</td>
 * <td>{@link #OID_TEXT}</td>
 * <td>java.lang.String</td>
 * <td>A Unicode string with a restricted character repertoire</td>
 * </tr>
 * <tr>
 * <td>{@link #TOKEN}</td>
 * <td>Token</td>
 * <td>{@link #OID_TOKEN}</td>
 * <td>java.lang.String</td>
 * <td>A whitespace-normalized Unicode string with no leading, trailing or consecutive spaces</td>
 * </tr>
 * <tr>
 * <td>{@link #TIME_OF_DAY}</td>
 * <td>TimeOfDay</td>
 * <td>{@link #OID_TIME_OF_DAY}</td>
 * <td>com.optimasc.lang.GregorianDatetimeCalendar</td>
 * <td>A local time of day with second precision and no timezone</td>
 * </tr>
 * <tr>
 * <td>{@link #TIME_OF_DAY_TZ}</td>
 * <td>TimeOfDayWithTimezone</td>
 * <td>{@link #OID_TIME_OF_DAY_TZ}</td>
 * <td>com.optimasc.lang.GregorianDatetimeCalendar</td>
 * <td>A time of day with second precision and timezone</td>
 * </tr>
 * <tr>
 * <td>{@link #UINT8}</td>
 * <td>UInt8</td>
 * <td>{@link #OID_UINT8}</td>
 * <td>java.lang.Integer</td>
 * <td>An unsigned integer within the range 0 to 255</td>
 * </tr>
 * <tr>
 * <td>{@link #UINT16}</td>
 * <td>UInt16</td>
 * <td>{@link #OID_UINT16}</td>
 * <td>java.lang.Integer</td>
 * <td>An unsigned integer within the range 0 to 65535</td>
 * </tr>
 * <tr>
 * <td>{@link #UINT32}</td>
 * <td>UInt32</td>
 * <td>{@link #OID_UINT32}</td>
 * <td>java.lang.Long</td>
 * <td>An unsigned integer within the range 0 to 4294967295</td>
 * </tr>
 * <tr>
 * <td>{@link #UINT64}</td>
 * <td>UInt64</td>
 * <td>{@link #OID_UINT64}</td>
 * <td>java.math.BigInteger</td>
 * <td>An unsigned integer within the range 0 to 18446744073709551615</td>
 * </tr>
 * <tr>
 * <td>{@link #URI}</td>
 * <td>URI</td>
 * <td>{@link #OID_URI}</td>
 * <td>java.net.URI</td>
 * <td>A Uniform Resource Identifier as defined by IETF RFC 3986</td>
 * </tr>
 * <tr>
 * <td>{@link #VISIBLE_STRING}</td>
 * <td>VisibleString</td>
 * <td>{@link #OID_VISIBLE_STRING}</td>
 * <td>java.lang.String</td>
 * <td>A string of printable ASCII characters (U+0020 to U+007E)</td>
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
 *  retrieved only by its name {@code "javaClass"}. These types have no
 *  namespace, so their expanded name is their local name.</p>
 *
 *  <p>The XMLSchema 1.1 types ({@code XSD_*} constants) have no OID. Their
 *  expanded name is {@link #XSD_NAMESPACE_URI} followed by their local name,
 *  exposed as an {@code XSD_NAME_*} constant.</p>
 * 
 * */
public class StandardTypes
{
  /** Object Identifier for syntax of an LDAP BOOLEAN */
  public static final String OID_BOOLEAN = "1.3.6.1.4.1.1466.115.121.1.7";
  public static final String NAME_BOOLEAN = "BOOLEAN";
  
  /**
   * Type definition for LDAP BOOLEAN type. A Boolean type is represented by a
   * <code>java.lang.Boolean</code> java object instance. 
   */
  public static final EntityTypeDefinition BOOLEAN =
  /* OID, NamespaceURI, name, Description, datatype */
    new DefaultTypeDefinition(OID_BOOLEAN, null,
          NAME_BOOLEAN, "Boolean", new BooleanType(), StandardFormatters.BooleanConverter.getInstance());

  /**
   * Object Identifier for syntax of a string, equivalent to
   * UnboundedDirectoryString in X.520
   */
  public static final String OID_STRING = "1.3.6.1.4.1.1466.115.121.1.15";
  public static final String NAME_STRING = "UnboundedDirectoryString";
  /**
   * Type definition for generic string type. This is a generic type
   * representing any unicode string. This is represented by a
   * <code>java.lang.String</code> java object.
   */
  public static final EntityTypeDefinition STRING =
  /* OID, NamespaceURI, name, Description, datatype */
  new DefaultTypeDefinition(OID_STRING, null,
          NAME_STRING, "UnboundedDirectoryString", new StringType(new UnnamedTypeReference(new UnicodeCharType())), StandardFormatters.StringConverter.getInstance());
  
  /** Object Identifier for syntax of INTEGER */
  public static final String OID_INTEGER = "1.3.6.1.4.1.1466.115.121.1.27";
  public static final String NAME_INTEGER = "INTEGER";
  
  /**
   * Type definition for INTEGER type. This is a generic type representing an
   * integer numeric value. This is represented by a
   * <code>java.math.BigInteger</code> java object.
   */
  public static final EntityTypeDefinition INTEGER =
  /* OID, NamespaceURI, name, Description, datatype */
  new DefaultTypeDefinition(
  OID_INTEGER, null, NAME_INTEGER, "INTEGER", new IntegralType(),NumericFormatters.IntegerCanonicalConverter.getLDAPInstance());

  /** Object Identifier for syntax of an OBJECT IDENTIFIER */
  public static final String OID_OBJECT_IDENTIFIER = "1.3.6.1.4.1.1466.115.121.1.38";
  public static final String NAME_OBJECT_IDENTIFIER = "objectIdentifier";
  
  /** Type definition for OBJECT IDENTIFIER type. */
  public static final EntityTypeDefinition OBJECT_IDENTIFIER =
  /* OID, NamespaceURI, name, Description, Class, formatter, allowedValues */
  new DefaultTypeDefinition(OID_OBJECT_IDENTIFIER, null, NAME_OBJECT_IDENTIFIER, "OBJECT IDENTIFIER",
      new ObjectIdentifierType(), StandardFormatters.OIDConverter.getInstance());

  /** Object Identifier for syntax of an array of bytes / OCTET STRING */
  public static final String OID_OCTET_STRING = "1.3.6.1.4.1.1466.115.121.1.40";
  public static final String NAME_BINARY = "binary";
  /**
   * Type definition for an OCTET STRING type. This is a generic type
   * representing binary data. This is represented by a <code>byte[]</code>
   * java object.
   */
  public static final EntityTypeDefinition OCTET_STRING =
  /* OID, NamespaceURI, name, Description, Class, formatter, allowedValues */
  new DefaultTypeDefinition(OID_OCTET_STRING, 
      null, NAME_BINARY,
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
  new DefaultTypeDefinition(null, null, NAME_JAVA_CLASS, "Java Class",
      new ClassType(),null);  

  /** Object Identifier for syntax of a GeneralizedTime */
  public static final String OID_GENERALIZEDTIME = "1.3.6.1.4.1.1466.115.121.1.24";
  /** Object Identifier for syntax of a GeneralizedTime */
  public static final String NAME_GENERALIZEDTIME = "GeneralizedTime";
  /**
   * Type definition for an ASN1 GeneralizedTime. This is a generic type
   * representing a date/time with millisecond precision and timezone. This is represented by a
   * <code>com.optimasc.lang.GregorianDatetimeCalendar</code> java object.
   */
  public static final EntityTypeDefinition GENERALIZEDTIME =
  /* OID, NamespaceURI, name, Description, Class, formatter, allowedValues */
  new DefaultTypeDefinition(OID_GENERALIZEDTIME, null, NAME_GENERALIZEDTIME,
      "Generalized Time", new DateTimeType(DateTime.TimeAccuracy.MILLISECOND, false), StandardDateFormatters.GeneralizedTimeConverter.getInstance());

  /** Object Identifier for syntax of an IA5String */
  public static final String OID_IA5STRING = "1.3.6.1.4.1.1466.115.121.1.26";
  public static final String NAME_IA5STRING = "IA5String";
  /**
   * Type definition for LDAP IA5String type, a string of characters from U+0000 to U+007F.
   * An IA5String type is represented by a <code>java.lang.String</code> java object.
   */
  public static final EntityTypeDefinition IA5STRING =
  /* OID, NamespaceURI, name, Description, datatype */
    new DefaultTypeDefinition(OID_IA5STRING, null,
          NAME_IA5STRING, "IA5 String", new StringType(new UnnamedTypeReference(new ASCIICharType())), StandardFormatters.IA5StringConverter.getInstance());

  /** Object Identifier for syntax of a PrintableString */
  public static final String OID_PRINTABLESTRING = "1.3.6.1.4.1.1466.115.121.1.44";
  public static final String NAME_PRINTABLESTRING = "PrintableString";
  /**
   * Type definition for LDAP PrintableString type. A PrintableString type is represented by a
   * <code>java.lang.String</code> java object. The datatype uses the visible ASCII
   * characters, a superset of the PrintableString repertoire, which is enforced by the format.
   */
  public static final EntityTypeDefinition PRINTABLESTRING =
  /* OID, NamespaceURI, name, Description, datatype */
    new DefaultTypeDefinition(OID_PRINTABLESTRING, null,
          NAME_PRINTABLESTRING, "Printable String", new StringType(new UnnamedTypeReference(new VisibleCharType())), StandardFormatters.PrintableStringConverter.getInstance());

  /*---------------- IETF draft-codere-ldapsyntax syntaxes ----------------*/
  
  /** Object Identifier for syntax of a Date */
  public static final String OID_DATE = "1.3.6.1.4.1.61799.5.40.31";
  public static final String NAME_DATE = "Date";
  /**
   * Type definition for LDAP Date syntax, a date with day precision, with a year from 1582 to 9999. A Date type is represented by a
   * <code>com.optimasc.lang.GregorianDatetimeCalendar</code> java object.
   */
  public static final EntityTypeDefinition DATE =
  /* OID, NamespaceURI, name, Description, datatype */
    new DefaultTypeDefinition(OID_DATE, null,
          NAME_DATE, "Date", new DateType(), StandardDateFormatters.LDAPDateConverter.getInstance());
  
  
  /** Object Identifier for syntax of a Date-Time */
  public static final String OID_DATETIME = "1.3.6.1.4.1.61799.5.40.33";
  public static final String NAME_DATETIME = "Date-Time";
  /**
   * Type definition for LDAP Date-Time syntax, a date and local time with second precision and no timezone, with a year from 1582 to 9999. A Date-Time type is represented by a
   * <code>com.optimasc.lang.GregorianDatetimeCalendar</code> java object.
   */
  public static final EntityTypeDefinition DATETIME =
  /* OID, NamespaceURI, name, Description, datatype */
    new DefaultTypeDefinition(OID_DATETIME, null,
          NAME_DATETIME, "Date-Time", new DateTimeType(DateTime.TimeAccuracy.SECOND, true), StandardDateFormatters.LDAPDateTimeConverter.getInstance());
  
  
  /** Object Identifier for syntax of a Duration */
  public static final String OID_DURATION = "1.3.6.1.4.1.61799.5.40.34";
  public static final String NAME_DURATION = "Duration";
  /**
   * Type definition for LDAP Duration syntax, an elapsed time with millisecond precision. A Duration type is represented by a
   * <code>javax.xml.datatype.Duration</code> java object.
   */
  public static final EntityTypeDefinition DURATION =
  /* OID, NamespaceURI, name, Description, datatype */
    new DefaultTypeDefinition(OID_DURATION, null,
          NAME_DURATION, "Duration", new CalendarDurationType(), CalendarDurationConverter.getInstance());
  
  
  /** Object Identifier for syntax of a Float32 */
  public static final String OID_FLOAT32 = "1.3.6.1.4.1.61799.5.40.9.4";
  public static final String NAME_FLOAT32 = "Float32";
  /**
   * Type definition for LDAP Float32 syntax, an IEEE 754 single precision floating point value. A Float32 type is represented by a
   * <code>java.lang.Float</code> java object.
   */
  public static final EntityTypeDefinition FLOAT32 =
  /* OID, NamespaceURI, name, Description, datatype */
    new DefaultTypeDefinition(OID_FLOAT32, null,
          NAME_FLOAT32, "Float32", new SingleType(), NumericFormatters.Float32Converter.getInstance());
  
  
  /** Object Identifier for syntax of a Float64 */
  public static final String OID_FLOAT64 = "1.3.6.1.4.1.61799.5.40.9.8";
  public static final String NAME_FLOAT64 = "Float64";
  /**
   * Type definition for LDAP Float64 syntax, an IEEE 754 double precision floating point value. A Float64 type is represented by a
   * <code>java.lang.Double</code> java object.
   */
  public static final EntityTypeDefinition FLOAT64 =
  /* OID, NamespaceURI, name, Description, datatype */
    new DefaultTypeDefinition(OID_FLOAT64, null,
          NAME_FLOAT64, "Float64", new DoubleType(), NumericFormatters.Float64Converter.getInstance());
  
  
  /** Object Identifier for syntax of a Int8 */
  public static final String OID_INT8 = "1.3.6.1.4.1.61799.5.40.2.1";
  public static final String NAME_INT8 = "Int8";
  /**
   * Type definition for LDAP Int8 syntax, a signed integer within the range -128 to 127. A Int8 type is represented by a
   * <code>java.lang.Integer</code> java object.
   */
  public static final EntityTypeDefinition INT8 =
  /* OID, NamespaceURI, name, Description, datatype */
    new DefaultTypeDefinition(OID_INT8, null,
          NAME_INT8, "Int8", new ByteType(), NumericFormatters.ByteConverter.getInstance());
  
  
  /** Object Identifier for syntax of a Int16 */
  public static final String OID_INT16 = "1.3.6.1.4.1.61799.5.40.2.2";
  public static final String NAME_INT16 = "Int16";
  /**
   * Type definition for LDAP Int16 syntax, a signed integer within the range -32768 to 32767. A Int16 type is represented by a
   * <code>java.lang.Integer</code> java object.
   */
  public static final EntityTypeDefinition INT16 =
  /* OID, NamespaceURI, name, Description, datatype */
    new DefaultTypeDefinition(OID_INT16, null,
          NAME_INT16, "Int16", new ShortType(), NumericFormatters.ShortConverter.getInstance());
  
  
  /** Object Identifier for syntax of a Int32 */
  public static final String OID_INT32 = "1.3.6.1.4.1.61799.5.40.2.4";
  public static final String NAME_INT32 = "Int32";
  /**
   * Type definition for LDAP Int32 syntax, a signed integer within the range -2147483648 to 2147483647. A Int32 type is represented by a
   * <code>java.lang.Integer</code> java object.
   */
  public static final EntityTypeDefinition INT32 =
  /* OID, NamespaceURI, name, Description, datatype */
    new DefaultTypeDefinition(OID_INT32, null,
          NAME_INT32, "Int32", new IntType(), NumericFormatters.IntConverter.getInstance());
  
  
  /** Object Identifier for syntax of a Int64 */
  public static final String OID_INT64 = "1.3.6.1.4.1.61799.5.40.2.8";
  public static final String NAME_INT64 = "Int64";
  /**
   * Type definition for LDAP Int64 syntax, a signed integer within the range -9223372036854775808 to 9223372036854775807. A Int64 type is represented by a
   * <code>java.lang.Long</code> java object.
   */
  public static final EntityTypeDefinition INT64 =
  /* OID, NamespaceURI, name, Description, datatype */
    new DefaultTypeDefinition(OID_INT64, null,
          NAME_INT64, "Int64", new LongType(), NumericFormatters.LongConverter.getInstance());
  
  
  /** Object Identifier for syntax of a Language */
  public static final String OID_LANGUAGE = "1.3.6.1.4.1.61799.5.40.19.1";
  public static final String NAME_LANGUAGE = "Language";
  /**
   * Type definition for LDAP Language syntax, a language tag as defined by IETF BCP 47. A Language type is represented by a
   * <code>java.util.Locale</code> java object.
   */
  public static final EntityTypeDefinition LANGUAGE =
  /* OID, NamespaceURI, name, Description, datatype */
    new DefaultTypeDefinition(OID_LANGUAGE, null,
          NAME_LANGUAGE, "Language", new LanguageType(), StandardFormatters.LocaleTypeConverter.getInstance());
  
  
  /** Object Identifier for syntax of a Media Type */
  public static final String OID_MEDIA_TYPE = "1.3.6.1.4.1.61799.5.40.26.5";
  public static final String NAME_MEDIA_TYPE = "MediaType";
  /**
   * Type definition for LDAP Media Type syntax, an Internet media type as defined by IETF RFC 6838. A Media Type type is represented by a
   * <code>com.optimasc.lang.MediaType</code> java object.
   */
  public static final EntityTypeDefinition MEDIA_TYPE =
  /* OID, NamespaceURI, name, Description, datatype */
    new DefaultTypeDefinition(OID_MEDIA_TYPE, null,
          NAME_MEDIA_TYPE, "Media Type", new MediaTypeType(), StandardFormatters.MediaTypeConverter.getInstance());
  
  
  /** Object Identifier for syntax of a NCName */
  public static final String OID_NCNAME = "1.3.6.1.4.1.61799.5.40.12.6";
  public static final String NAME_NCNAME = "NCName";
  /**
   * Type definition for LDAP NCName syntax, an identifier that is a subset of the XML NCName. A NCName type is represented by a
   * <code>java.lang.String</code> java object.
   */
  public static final EntityTypeDefinition NCNAME =
  /* OID, NamespaceURI, name, Description, datatype */
    new DefaultTypeDefinition(OID_NCNAME, null,
          NAME_NCNAME, "NCName", new StringType(new UnnamedTypeReference(new UnicodeCharType())), StandardFormatters.NCNameConverter.getInstance());
  
  
  /** Object Identifier for syntax of a Normalized String */
  public static final String OID_NORMALIZED_STRING = "1.3.6.1.4.1.61799.5.40.12.4";
  public static final String NAME_NORMALIZED_STRING = "NormalizedString";
  /**
   * Type definition for LDAP Normalized String syntax, a whitespace-normalized Unicode string. A Normalized String type is represented by a
   * <code>java.lang.String</code> java object.
   */
  public static final EntityTypeDefinition NORMALIZED_STRING =
  /* OID, NamespaceURI, name, Description, datatype */
    new DefaultTypeDefinition(OID_NORMALIZED_STRING, null,
          NAME_NORMALIZED_STRING, "Normalized String", new StringType(new UnnamedTypeReference(new UnicodeCharType())), StandardFormatters.NormalizedStringConverter.getInstance());
  
  
  /** Object Identifier for syntax of a OpenDate */
  public static final String OID_OPENDATE = "1.3.6.1.4.1.61799.5.40.14.1";
  public static final String NAME_OPENDATE = "OpenDate";
  /**
   * Type definition for LDAP OpenDate syntax, a date with year, month or day precision, or a date and time with timezone, with a year from 1582 to 9999. A OpenDate type is represented by a
   * <code>com.optimasc.lang.GregorianDatetimeCalendar</code> java object.
   */
  public static final EntityTypeDefinition OPENDATE =
  /* OID, NamespaceURI, name, Description, datatype */
    new DefaultTypeDefinition(OID_OPENDATE, null,
          NAME_OPENDATE, "OpenDate", new DateTimeType(DateTime.TimeAccuracy.MILLISECOND, false), StandardDateFormatters.OpenDateConverter.getInstance());
  
  
  /** Object Identifier for syntax of a Percentage */
  public static final String OID_PERCENTAGE = "1.3.6.1.4.1.61799.5.40.2.20";
  public static final String NAME_PERCENTAGE = "Percentage";
  /**
   * Type definition for LDAP Percentage syntax, an integer within the range 0 to 100. A Percentage type is represented by a
   * <code>java.lang.Integer</code> java object.
   */
  public static final EntityTypeDefinition PERCENTAGE =
  /* OID, NamespaceURI, name, Description, datatype */
    new DefaultTypeDefinition(OID_PERCENTAGE, null,
          NAME_PERCENTAGE, "Percentage", new PercentageType(), NumericFormatters.PercentageConverter.getInstance());
  
  
  /** Object Identifier for syntax of a QualifiedName */
  public static final String OID_QUALIFIED_NAME = "1.3.6.1.4.1.61799.5.40.12.7";
  public static final String NAME_QUALIFIED_NAME = "QualifiedName";
  /**
   * Type definition for LDAP QualifiedName syntax, an identifier using namespaces, that is a subset of the XML QName. A QualifiedName type is represented by a
   * <code>java.lang.String</code> java object.
   */
  public static final EntityTypeDefinition QUALIFIED_NAME =
  /* OID, NamespaceURI, name, Description, datatype */
    new DefaultTypeDefinition(OID_QUALIFIED_NAME, null,
          NAME_QUALIFIED_NAME, "QualifiedName", new StringType(new UnnamedTypeReference(new UnicodeCharType())), StandardFormatters.QualifiedNameConverter.getInstance());
  
  
  /** Object Identifier for syntax of a Real */
  public static final String OID_REAL = "1.3.6.1.4.1.61799.5.40.9";
  public static final String NAME_REAL = "Real";
  /**
   * Type definition for LDAP Real syntax, a real number, which is rounded to double precision. A Real type is represented by a
   * <code>java.lang.Double</code> java object.
   */
  public static final EntityTypeDefinition REAL =
  /* OID, NamespaceURI, name, Description, datatype */
    new DefaultTypeDefinition(OID_REAL, null,
          NAME_REAL, "Real", new DoubleType(), NumericFormatters.RealConverter.getInstance());
  
  
  /** Object Identifier for syntax of a Text */
  public static final String OID_TEXT = "1.3.6.1.4.1.61799.5.40.12.3";
  public static final String NAME_TEXT = "Text";
  /**
   * Type definition for LDAP Text syntax, a Unicode string with a restricted character repertoire. A Text type is represented by a
   * <code>java.lang.String</code> java object.
   */
  public static final EntityTypeDefinition TEXT =
  /* OID, NamespaceURI, name, Description, datatype */
    new DefaultTypeDefinition(OID_TEXT, null,
          NAME_TEXT, "Text", new StringType(new UnnamedTypeReference(new UnicodeCharType())), StandardFormatters.TextConverter.getInstance());
  
  
  /** Object Identifier for syntax of a Token */
  public static final String OID_TOKEN = "1.3.6.1.4.1.61799.5.40.12.5";
  public static final String NAME_TOKEN = "Token";
  /**
   * Type definition for LDAP Token syntax, a whitespace-normalized Unicode string with no leading, trailing or consecutive spaces. A Token type is represented by a
   * <code>java.lang.String</code> java object.
   */
  public static final EntityTypeDefinition TOKEN =
  /* OID, NamespaceURI, name, Description, datatype */
    new DefaultTypeDefinition(OID_TOKEN, null,
          NAME_TOKEN, "Token", new StringType(new UnnamedTypeReference(new UnicodeCharType())), StandardFormatters.TokenConverter.getInstance());
  
  
  /** Object Identifier for syntax of a Time Of Day */
  public static final String OID_TIME_OF_DAY = "1.3.6.1.4.1.61799.5.40.32";
  public static final String NAME_TIME_OF_DAY = "TimeOfDay";
  /**
   * Type definition for LDAP Time Of Day syntax, a local time of day with second precision and no timezone. A Time Of Day type is represented by a
   * <code>com.optimasc.lang.GregorianDatetimeCalendar</code> java object.
   */
  public static final EntityTypeDefinition TIME_OF_DAY =
  /* OID, NamespaceURI, name, Description, datatype */
    new DefaultTypeDefinition(OID_TIME_OF_DAY, null,
          NAME_TIME_OF_DAY, "Time Of Day", new TimeType(DateTime.TimeAccuracy.SECOND, true), StandardDateFormatters.TimeOfDayConverter.getInstance());
  
  
  /** Object Identifier for syntax of a Time Of Day with Timezone */
  public static final String OID_TIME_OF_DAY_TZ = "1.3.6.1.4.1.61799.5.40.35";
  public static final String NAME_TIME_OF_DAY_TZ = "TimeOfDayWithTimezone";
  /**
   * Type definition for LDAP Time Of Day with Timezone syntax, a time of day with second precision and timezone. A Time Of Day with Timezone type is represented by a
   * <code>com.optimasc.lang.GregorianDatetimeCalendar</code> java object.
   */
  public static final EntityTypeDefinition TIME_OF_DAY_TZ =
  /* OID, NamespaceURI, name, Description, datatype */
    new DefaultTypeDefinition(OID_TIME_OF_DAY_TZ, null,
          NAME_TIME_OF_DAY_TZ, "Time Of Day with Timezone", new TimeType(DateTime.TimeAccuracy.SECOND, false), StandardDateFormatters.TimeOfDayWithTimezoneConverter.getInstance());
  
  
  /** Object Identifier for syntax of a UInt8 */
  public static final String OID_UINT8 = "1.3.6.1.4.1.61799.5.40.2.21";
  public static final String NAME_UINT8 = "UInt8";
  /**
   * Type definition for LDAP UInt8 syntax, an unsigned integer within the range 0 to 255. A UInt8 type is represented by a
   * <code>java.lang.Integer</code> java object.
   */
  public static final EntityTypeDefinition UINT8 =
  /* OID, NamespaceURI, name, Description, datatype */
    new DefaultTypeDefinition(OID_UINT8, null,
          NAME_UINT8, "UInt8", new UnsignedByteType(), NumericFormatters.unsignedByteConverter.getInstance());
  
  
  /** Object Identifier for syntax of a UInt16 */
  public static final String OID_UINT16 = "1.3.6.1.4.1.61799.5.40.2.22";
  public static final String NAME_UINT16 = "UInt16";
  /**
   * Type definition for LDAP UInt16 syntax, an unsigned integer within the range 0 to 65535. A UInt16 type is represented by a
   * <code>java.lang.Integer</code> java object.
   */
  public static final EntityTypeDefinition UINT16 =
  /* OID, NamespaceURI, name, Description, datatype */
    new DefaultTypeDefinition(OID_UINT16, null,
          NAME_UINT16, "UInt16", new UnsignedShortType(), NumericFormatters.unsignedShortConverter.getInstance());
  
  
  /** Object Identifier for syntax of a UInt32 */
  public static final String OID_UINT32 = "1.3.6.1.4.1.61799.5.40.2.24";
  public static final String NAME_UINT32 = "UInt32";
  /**
   * Type definition for LDAP UInt32 syntax, an unsigned integer within the range 0 to 4294967295. A UInt32 type is represented by a
   * <code>java.lang.Long</code> java object.
   */
  public static final EntityTypeDefinition UINT32 =
  /* OID, NamespaceURI, name, Description, datatype */
    new DefaultTypeDefinition(OID_UINT32, null,
          NAME_UINT32, "UInt32", new UnsignedIntType(), NumericFormatters.unsignedIntConverter.getInstance());
  
  
  /** Object Identifier for syntax of a UInt64 */
  public static final String OID_UINT64 = "1.3.6.1.4.1.61799.5.40.2.28";
  public static final String NAME_UINT64 = "UInt64";
  /**
   * Type definition for LDAP UInt64 syntax, an unsigned integer within the range 0 to 18446744073709551615. A UInt64 type is represented by a
   * <code>java.math.BigInteger</code> java object.
   */
  public static final EntityTypeDefinition UINT64 =
  /* OID, NamespaceURI, name, Description, datatype */
    new DefaultTypeDefinition(OID_UINT64, null,
          NAME_UINT64, "UInt64", new UnsignedLongType(), NumericFormatters.unsignedLongConverter.getInstance());
  
  
  /** Object Identifier for syntax of a URI */
  public static final String OID_URI = "1.3.6.1.4.1.61799.5.40.26.4";
  public static final String NAME_URI = "URI";
  /**
   * Type definition for LDAP URI syntax, a Uniform Resource Identifier as defined by IETF RFC 3986. A URI type is represented by a
   * <code>java.net.URI</code> java object.
   */
  public static final EntityTypeDefinition URI =
  /* OID, NamespaceURI, name, Description, datatype */
    new DefaultTypeDefinition(OID_URI, null,
          NAME_URI, "URI", new URIType(), StandardFormatters.URIConverter.getInstance());
  
  
  /** Object Identifier for syntax of a Visible String */
  public static final String OID_VISIBLE_STRING = "1.3.6.1.4.1.61799.5.40.26";
  public static final String NAME_VISIBLE_STRING = "VisibleString";
  /**
   * Type definition for LDAP Visible String syntax, a string of printable ASCII characters (U+0020 to U+007E). A Visible String type is represented by a
   * <code>java.lang.String</code> java object.
   */
  public static final EntityTypeDefinition VISIBLE_STRING =
  /* OID, NamespaceURI, name, Description, datatype */
    new DefaultTypeDefinition(OID_VISIBLE_STRING, null,
          NAME_VISIBLE_STRING, "Visible String", new StringType(new UnnamedTypeReference(new VisibleCharType())), StandardFormatters.VisibleStringConverter.getInstance());
  
  

}
