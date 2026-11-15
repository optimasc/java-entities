package com.optimasc.entity.schema;

import com.optimasc.datatypes.defined.ByteType;
import com.optimasc.datatypes.defined.DoubleType;
import com.optimasc.datatypes.defined.IntType;
import com.optimasc.datatypes.defined.LongType;
import com.optimasc.datatypes.defined.PercentageType;
import com.optimasc.datatypes.defined.ShortType;
import com.optimasc.datatypes.defined.SingleType;
import com.optimasc.datatypes.defined.StringType;
import com.optimasc.datatypes.defined.UnsignedByteType;
import com.optimasc.datatypes.defined.UnsignedIntType;
import com.optimasc.datatypes.defined.UnsignedLongType;
import com.optimasc.datatypes.defined.UnsignedShortType;
import com.optimasc.datatypes.derived.UnicodeCharType;
import com.optimasc.datatypes.generated.LanguageType;
import com.optimasc.datatypes.primitives.DateTimeType;
import com.optimasc.datatypes.primitives.MediaTypeType;
import com.optimasc.datatypes.primitives.TimeType;
import com.optimasc.datatypes.primitives.URIType;
import com.optimasc.date.DateTime;
import com.optimasc.text.NumericFormatters;
import com.optimasc.text.StandardDateFormatters;
import com.optimasc.text.StandardFormatters;

import omg.org.astm.type.UnnamedTypeReference;

/**
 * Extended type definitions: the syntaxes of IETF draft-codere-ldapsyntax that
 * have no ASN.1 built-in equivalent. Most are subtypes of an ASN.1 built-in
 * type, such as a constrained <code>INTEGER</code> or a string with a
 * restricted character repertoire.
 *
 * <p>
 * These type definitions are not automatically  registered in 
 * {@link  StandardSchema}, they should be registereed manually 
 * if needed. The base syntaxes are defined by {@link StandardTypes}.
 * </p>
 *
 * <table>
 * <caption>Extended type definitions</caption>
 * <tr>
 * <th>Java Name</th>
 * <th>Expanded Name</th>
 * <th>ID</th>
 * <th>Java Object representation</th>
 * <th>Description</th>
 * </tr>
 * <tr>
 * <td>{@link #FLOAT32}</td>
 * <td>Float32</td>
 * <td>{@link #OID_FLOAT32}</td>
 * <td><code>java.lang.Float</code></td>
 * <td>An IEEE 754 single precision floating point value</td>
 * </tr>
 * <tr>
 * <td>{@link #FLOAT64}</td>
 * <td>Float64</td>
 * <td>{@link #OID_FLOAT64}</td>
 * <td><code>java.lang.Double</code></td>
 * <td>An IEEE 754 double precision floating point value</td>
 * </tr>
 * <tr>
 * <td>{@link #INT8}</td>
 * <td>Int8</td>
 * <td>{@link #OID_INT8}</td>
 * <td><code>java.lang.Integer</code></td>
 * <td>A signed integer within the range -128 to 127</td>
 * </tr>
 * <tr>
 * <td>{@link #INT16}</td>
 * <td>Int16</td>
 * <td>{@link #OID_INT16}</td>
 * <td><code>java.lang.Integer</code></td>
 * <td>A signed integer within the range -32768 to 32767</td>
 * </tr>
 * <tr>
 * <td>{@link #INT32}</td>
 * <td>Int32</td>
 * <td>{@link #OID_INT32}</td>
 * <td><code>java.lang.Integer</code></td>
 * <td>A signed integer within the range -2147483648 to 2147483647</td>
 * </tr>
 * <tr>
 * <td>{@link #INT64}</td>
 * <td>Int64</td>
 * <td>{@link #OID_INT64}</td>
 * <td><code>java.lang.Long</code></td>
 * <td>A signed integer within the range -9223372036854775808 to 9223372036854775807</td>
 * </tr>
 * <tr>
 * <td>{@link #LANGUAGE}</td>
 * <td>Language</td>
 * <td>{@link #OID_LANGUAGE}</td>
 * <td><code>java.util.Locale</code></td>
 * <td>A language tag as defined by IETF BCP 47</td>
 * </tr>
 * <tr>
 * <td>{@link #MEDIA_TYPE}</td>
 * <td>MediaType</td>
 * <td>{@link #OID_MEDIA_TYPE}</td>
 * <td><code>com.optimasc.lang.MediaType</code></td>
 * <td>An Internet media type as defined by IETF RFC 6838</td>
 * </tr>
 * <tr>
 * <td>{@link #NCNAME}</td>
 * <td>NCName</td>
 * <td>{@link #OID_NCNAME}</td>
 * <td><code>java.lang.String</code></td>
 * <td>An identifier that is a subset of the XML NCName</td>
 * </tr>
 * <tr>
 * <td>{@link #NORMALIZED_STRING}</td>
 * <td>NormalizedString</td>
 * <td>{@link #OID_NORMALIZED_STRING}</td>
 * <td><code>java.lang.String</code></td>
 * <td>A whitespace-normalized Unicode string</td>
 * </tr>
 * <tr>
 * <td>{@link #OPENDATE}</td>
 * <td>OpenDate</td>
 * <td>{@link #OID_OPENDATE}</td>
 * <td><code>com.optimasc.lang.GregorianDatetimeCalendar</code></td>
 * <td>A date with year, month or day precision, or a date and time with timezone, with a year from 1582 to 9999</td>
 * </tr>
 * <tr>
 * <td>{@link #PERCENTAGE}</td>
 * <td>Percentage</td>
 * <td>{@link #OID_PERCENTAGE}</td>
 * <td><code>java.lang.Integer</code></td>
 * <td>An integer within the range 0 to 100</td>
 * </tr>
 * <tr>
 * <td>{@link #QUALIFIED_NAME}</td>
 * <td>QualifiedName</td>
 * <td>{@link #OID_QUALIFIED_NAME}</td>
 * <td><code>java.lang.String</code></td>
 * <td>An identifier with an optional namespace prefix, a subset of the XML QName</td>
 * </tr>
 * <tr>
 * <td>{@link #TEXT}</td>
 * <td>Text</td>
 * <td>{@link #OID_TEXT}</td>
 * <td><code>java.lang.String</code></td>
 * <td>A Unicode string with a restricted character repertoire</td>
 * </tr>
 * <tr>
 * <td>{@link #TOKEN}</td>
 * <td>Token</td>
 * <td>{@link #OID_TOKEN}</td>
 * <td><code>java.lang.String</code></td>
 * <td>A whitespace-normalized Unicode string with no leading, trailing or consecutive spaces</td>
 * </tr>
 * <tr>
 * <td>{@link #TIME_OF_DAY_TZ}</td>
 * <td>TimeOfDayWithTimezone</td>
 * <td>{@link #OID_TIME_OF_DAY_TZ}</td>
 * <td><code>com.optimasc.lang.GregorianDatetimeCalendar</code></td>
 * <td>A time of day with second precision and timezone</td>
 * </tr>
 * <tr>
 * <td>{@link #UINT8}</td>
 * <td>UInt8</td>
 * <td>{@link #OID_UINT8}</td>
 * <td><code>java.lang.Integer</code></td>
 * <td>An unsigned integer within the range 0 to 255</td>
 * </tr>
 * <tr>
 * <td>{@link #UINT16}</td>
 * <td>UInt16</td>
 * <td>{@link #OID_UINT16}</td>
 * <td><code>java.lang.Integer</code></td>
 * <td>An unsigned integer within the range 0 to 65535</td>
 * </tr>
 * <tr>
 * <td>{@link #UINT32}</td>
 * <td>UInt32</td>
 * <td>{@link #OID_UINT32}</td>
 * <td><code>java.lang.Long</code></td>
 * <td>An unsigned integer within the range 0 to 4294967295</td>
 * </tr>
 * <tr>
 * <td>{@link #UINT64}</td>
 * <td>UInt64</td>
 * <td>{@link #OID_UINT64}</td>
 * <td><code>java.math.BigInteger</code></td>
 * <td>An unsigned integer within the range 0 to 18446744073709551615</td>
 * </tr>
 * <tr>
 * <td>{@link #URI}</td>
 * <td>URI</td>
 * <td>{@link #OID_URI}</td>
 * <td><code>java.net.URI</code></td>
 * <td>A Uniform Resource Identifier as defined by IETF RFC 3986</td>
 * </tr>
 * </table>
 *
 * <h2>Identifiers</h2>
 * <p>Each type has two stable identifiers: its expanded name, exposed as a
 * {@code NAME_*} constant, and its OID, exposed as an {@code OID_*}
 * constant. These types have no namespace, so their expanded name is their
 * local name.</p>
 *
 * @see StandardTypes
 * @see XMLSchemaTypes
 */
public class ExtendedTypes
{
  /*---------------- Non-Base ASN.1 IETF draft-codere-ldapsyntax syntaxes ----------------*/
  
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

}
