package com.optimasc.entity.schema;

import com.optimasc.datatypes.defined.BinaryType;
import com.optimasc.datatypes.defined.ByteType;
import com.optimasc.datatypes.defined.DateType;
import com.optimasc.datatypes.defined.DoubleType;
import com.optimasc.datatypes.defined.IntType;
import com.optimasc.datatypes.defined.LongType;
import com.optimasc.datatypes.defined.NonNegativeIntegerType;
import com.optimasc.datatypes.defined.ShortType;
import com.optimasc.datatypes.defined.SingleType;
import com.optimasc.datatypes.defined.StringType;
import com.optimasc.datatypes.defined.UnsignedByteType;
import com.optimasc.datatypes.defined.UnsignedIntType;
import com.optimasc.datatypes.defined.UnsignedLongType;
import com.optimasc.datatypes.defined.UnsignedShortType;
import com.optimasc.datatypes.defined.YearType;
import com.optimasc.datatypes.derived.UnicodeCharType;
import com.optimasc.datatypes.generated.LanguageType;
import com.optimasc.datatypes.primitives.BooleanType;
import com.optimasc.datatypes.primitives.CalendarDurationType;
import com.optimasc.datatypes.primitives.DateTimeType;
import com.optimasc.datatypes.primitives.DecimalType;
import com.optimasc.datatypes.primitives.IntegralType;
import com.optimasc.datatypes.primitives.TimeType;
import com.optimasc.datatypes.primitives.URIType;
import com.optimasc.date.DateTime;
import com.optimasc.text.CalendarDurationConverter;
import com.optimasc.text.NumericFormatters;
import com.optimasc.text.StandardDateFormatters;
import com.optimasc.text.StandardFormatters;

import omg.org.astm.type.UnnamedTypeReference;

/**
 * XML Schema 1.1 type definitions: a subset of the built-in datatypes of W3C
 * XML Schema Definition Language (XSD) 1.1 Part 2.
 *
 * <p>
 * These type definitions are not registered in {@link StandardSchema}, so
 * that they are only loaded when used: they should be registered if
 * needed.
 * </p>
 *
 * <table>
 * <caption>XML Schema 1.1 type definitions</caption>
 * <tr>
 * <th>Java Name</th>
 * <th>Local Name</th>
 * <th>Java Object representation</th>
 * <th>Description</th>
 * </tr>
 * <tr>
 * <td>{@link #XSD_BOOLEAN}</td>
 * <td>{@link #XSD_NAME_BOOLEAN}: <code>boolean</code></td>
 * <td><code>java.lang.Boolean</code></td>
 * <td>A boolean value</td>
 * </tr>
 * <tr>
 * <td>{@link #XSD_BASE64BINARY}</td>
 * <td>{@link #XSD_NAME_BASE64}: <code>base64Binary</code></td>
 * <td><code>byte[]</code></td>
 * <td>Binary data, represented in Base64</td>
 * </tr>
 * <tr>
 * <td>{@link #XSD_HEXBINARY}</td>
 * <td>{@link #XSD_NAME_HEXBINARY}: <code>hexBinary</code></td>
 * <td><code>byte[]</code></td>
 * <td>Binary data, represented in hexadecimal</td>
 * </tr>
 * <tr>
 * <td>{@link #XSD_LANGUAGE}</td>
 * <td>{@link #XSD_NAME_LANGUAGE}: <code>language</code></td>
 * <td><code>java.util.Locale</code></td>
 * <td>A language tag as defined by IETF BCP 47</td>
 * </tr>
 * <tr>
 * <td>{@link #XSD_TIMESTAMP}</td>
 * <td>{@link #XSD_NAME_TIMESTAMP}: <code>dateTimeStamp</code></td>
 * <td><code>com.optimasc.lang.GregorianDatetimeCalendar</code></td>
 * <td>A date and time with millisecond precision and a mandatory timezone</td>
 * </tr>
 * <tr>
 * <td>{@link #XSD_DATE}</td>
 * <td>{@link #XSD_NAME_DATE}: <code>date</code></td>
 * <td><code>com.optimasc.lang.GregorianDatetimeCalendar</code></td>
 * <td>A date</td>
 * </tr>
 * <tr>
 * <td>{@link #XSD_YEAR}</td>
 * <td>{@link #XSD_NAME_YEAR}: <code>gYear</code></td>
 * <td><code>com.optimasc.lang.GregorianDatetimeCalendar</code></td>
 * <td>A Gregorian year</td>
 * </tr>
 * <tr>
 * <td>{@link #XSD_DECIMAL}</td>
 * <td>{@link #XSD_NAME_DECIMAL}: <code>decimal</code></td>
 * <td><code>java.math.BigDecimal</code></td>
 * <td>A decimal number of unlimited precision</td>
 * </tr>
 * <tr>
 * <td>{@link #XSD_INTEGER}</td>
 * <td>{@link #XSD_NAME_INTEGER}: <code>integer</code></td>
 * <td><code>java.math.BigInteger</code></td>
 * <td>An integer value of unlimited range</td>
 * </tr>
 * <tr>
 * <td>{@link #XSD_INT8}</td>
 * <td>{@link #XSD_NAME_INT8}: <code>byte</code></td>
 * <td><code>java.lang.Integer</code></td>
 * <td>A signed integer within the range -128 to 127</td>
 * </tr>
 * <tr>
 * <td>{@link #XSD_INT16}</td>
 * <td>{@link #XSD_NAME_INT16}: <code>short</code></td>
 * <td><code>java.lang.Integer</code></td>
 * <td>A signed integer within the range -32768 to 32767</td>
 * </tr>
 * <tr>
 * <td>{@link #XSD_INT}</td>
 * <td>{@link #XSD_NAME_INT}: <code>int</code></td>
 * <td><code>java.lang.Integer</code></td>
 * <td>A signed integer within the range -2147483648 to 2147483647</td>
 * </tr>
 * <tr>
 * <td>{@link #XSD_LONG}</td>
 * <td>{@link #XSD_NAME_LONG}: <code>long</code></td>
 * <td><code>java.lang.Long</code></td>
 * <td>A signed integer within the range -9223372036854775808 to 9223372036854775807</td>
 * </tr>
 * <tr>
 * <td>{@link #XSD_UINT8}</td>
 * <td>{@link #XSD_NAME_UINT8}: <code>unsignedByte</code></td>
 * <td><code>java.lang.Integer</code></td>
 * <td>An unsigned integer within the range 0 to 255</td>
 * </tr>
 * <tr>
 * <td>{@link #XSD_UINT16}</td>
 * <td>{@link #XSD_NAME_UINT16}: <code>unsignedShort</code></td>
 * <td><code>java.lang.Integer</code></td>
 * <td>An unsigned integer within the range 0 to 65535</td>
 * </tr>
 * <tr>
 * <td>{@link #XSD_UINT32}</td>
 * <td>{@link #XSD_NAME_UINT32}: <code>unsignedInt</code></td>
 * <td><code>java.lang.Long</code></td>
 * <td>An unsigned integer within the range 0 to 4294967295</td>
 * </tr>
 * <tr>
 * <td>{@link #XSD_NON_NEGATIVE_INTEGER}</td>
 * <td>{@link #XSD_NAME_NON_NEGATIVE_INTEGER}: <code>nonNegativeInteger</code></td>
 * <td><code>java.math.BigInteger</code></td>
 * <td>A non-negative integer of unlimited range</td>
 * </tr>
 * <tr>
 * <td>{@link #XSD_FLOAT}</td>
 * <td>{@link #XSD_NAME_FLOAT}: <code>float</code></td>
 * <td><code>java.lang.Float</code></td>
 * <td>An IEEE 754 single precision floating point value</td>
 * </tr>
 * <tr>
 * <td>{@link #XSD_DOUBLE}</td>
 * <td>{@link #XSD_NAME_DOUBLE}: <code>double</code></td>
 * <td><code>java.lang.Double</code></td>
 * <td>An IEEE 754 double precision floating point value</td>
 * </tr>
 * <tr>
 * <td>{@link #XSD_STRING}</td>
 * <td>{@link #XSD_NAME_STRING}: <code>string</code></td>
 * <td><code>java.lang.String</code></td>
 * <td>A string of XML characters</td>
 * </tr>
 * <tr>
 * <td>{@link #XSD_NORMALIZED_STRING}</td>
 * <td>{@link #XSD_NAME_NORMALIZED_STRING}: <code>normalizedString</code></td>
 * <td><code>java.lang.String</code></td>
 * <td>A string of XML characters with no TAB, LF or CR</td>
 * </tr>
 * <tr>
 * <td>{@link #XSD_TOKEN}</td>
 * <td>{@link #XSD_NAME_TOKEN}: <code>token</code></td>
 * <td><code>java.lang.String</code></td>
 * <td>A normalized string with no leading, trailing or consecutive spaces</td>
 * </tr>
 * <tr>
 * <td>{@link #XSD_UINT64}</td>
 * <td>{@link #XSD_NAME_UINT64}: <code>unsignedLong</code></td>
 * <td><code>java.math.BigInteger</code></td>
 * <td>An unsigned integer within the range 0 to 18446744073709551615</td>
 * </tr>
 * <tr>
 * <td>{@link #XSD_ANYURI}</td>
 * <td>{@link #XSD_NAME_ANYURI}: <code>anyURI</code></td>
 * <td><code>java.net.URI</code></td>
 * <td>A URI reference as defined by IETF RFC 3986</td>
 * </tr>
 * <tr>
 * <td>{@link #XSD_DATETIME}</td>
 * <td>{@link #XSD_NAME_DATETIME}: <code>dateTime</code></td>
 * <td><code>com.optimasc.lang.GregorianDatetimeCalendar</code></td>
 * <td>A date and local time with second precision and no timezone</td>
 * </tr>
 * <tr>
 * <td>{@link #XSD_TIME}</td>
 * <td>{@link #XSD_NAME_TIME}: <code>time</code></td>
 * <td><code>com.optimasc.lang.GregorianDatetimeCalendar</code></td>
 * <td>A local time of day with second precision and no timezone</td>
 * </tr>
 * <tr>
 * <td>{@link #XSD_DURATION}</td>
 * <td>{@link #XSD_NAME_DURATION}: <code>duration</code></td>
 * <td><code>javax.xml.datatype.Duration</code></td>
 * <td>A non-negative duration with a month component and a day-time component, with millisecond precision</td>
 * </tr>
 * <tr>
 * <td>{@link #XSD_NAME}</td>
 * <td>{@link #XSD_NAME_NAME}: <code>Name</code></td>
 * <td><code>java.lang.String</code></td>
 * <td>An XML name</td>
 * </tr>
 * <tr>
 * <td>{@link #XSD_NCNAME}</td>
 * <td>{@link #XSD_NAME_NCNAME}: <code>NCName</code></td>
 * <td><code>java.lang.String</code></td>
 * <td>An XML non-colonized name</td>
 * </tr>
 * <tr>
 * <td>{@link #XSD_NMTOKEN}</td>
 * <td>{@link #XSD_NAME_NMTOKEN}: <code>NMTOKEN</code></td>
 * <td><code>java.lang.String</code></td>
 * <td>An XML name token</td>
 * </tr>
 * </table>
 *
 * <h2>Identifiers</h2>
 * <p>These types have no OID. Their expanded name is
 * {@link #XSD_NAMESPACE_URI} followed by their local name, which is exposed
 * as an {@code XSD_NAME_*} constant.</p>
 *
 * @see StandardTypes
 * @see ExtendedTypes
 */
public class XMLSchemaTypes
{
  
  /*---------------------- XMLSchemaa 1.1 datatypes ----------------------*/
  
  public static final String XSD_NAMESPACE_URI = "http://www.w3.org/2001/XMLSchema#";
  public static final String XSD_NAME_BOOLEAN = "boolean";
  /**
   * Type definition for XMLSchema boolean type. A boolean type is represented by a
   * <code>java.lang.Boolean</code> java object instance. 
   */
  public static final EntityTypeDefinition XSD_BOOLEAN =
  /* OID, NamespaceURI, name, Description, datatype */
    DefaultDefinition.shared(new DefaultTypeDefinition(null, XSD_NAMESPACE_URI,
          XSD_NAME_BOOLEAN, "XMLSchema boolean", new BooleanType(), StandardFormatters.XSDBooleanConverter.getInstance()));
  
  
  public static final String XSD_NAME_BASE64 = "base64Binary";
  /**
   * Type definition for XMLSchema base64Binary type. A base64Binary type is represented by a
   * <code>byte[]</code> java object. 
   */
  public static final EntityTypeDefinition XSD_BASE64BINARY =
  /* OID, NamespaceURI, name, Description, datatype */
    DefaultDefinition.shared(new DefaultTypeDefinition(null, XSD_NAMESPACE_URI,
          XSD_NAME_BASE64, "XMLSchema base64Binary", new BinaryType(), StandardFormatters.XSDBase64BinaryConverter.getInstance()));
  
  public static final String XSD_NAME_HEXBINARY = "hexBinary";
  /**
   * Type definition for XMLSchema hexBinary type. A hexBinary type is represented by a
   * <code>byte[]</code> java object. 
   */
  public static final EntityTypeDefinition XSD_HEXBINARY =
  /* OID, NamespaceURI, name, Description, datatype */
    DefaultDefinition.shared(new DefaultTypeDefinition(null, XSD_NAMESPACE_URI,
          XSD_NAME_HEXBINARY, "XMLSchema hexBinary", new BinaryType(), StandardFormatters.XSDHexBinaryConverter.getInstance()));
  
  
  public static final String XSD_NAME_LANGUAGE = "language";
  /**
   * Type definition for XMLSchema language type. A language type is represented by a
   * <code>java.util.Locale</code> java object. 
   */
  public static final EntityTypeDefinition XSD_LANGUAGE =
  /* OID, NamespaceURI, name, Description, datatype */
    DefaultDefinition.shared(new DefaultTypeDefinition(null, XSD_NAMESPACE_URI,
          XSD_NAME_LANGUAGE, "XMLSchema language", new LanguageType(), StandardFormatters.LocaleTypeConverter.getInstance()));
  
  
  public static final String XSD_NAME_TIMESTAMP = "dateTimeStamp";
  /**
   * Type definition for XMLSchema dateTimeStamp type. A dateTimeStamp type is represented by a
   * <code>com.optimasc.lang.GregorianDatetimeCalendar</code> java object. 
   */
  public static final EntityTypeDefinition XSD_TIMESTAMP =
  /* OID, NamespaceURI, name, Description, datatype */
    DefaultDefinition.shared(new DefaultTypeDefinition(null, XSD_NAMESPACE_URI,
          XSD_NAME_TIMESTAMP, "XMLSchema dateTimeStamp", new DateTimeType(DateTime.TimeAccuracy.MILLISECOND, false), StandardDateFormatters.XSDDateTimeStampConverter.getInstance()));
  
  public static final String XSD_NAME_DATE = "date";
  /**
   * Type definition for XMLSchema date type. A date type is represented by a
   * <code>com.optimasc.lang.GregorianDatetimeCalendar</code> java object. 
   */
  public static final EntityTypeDefinition XSD_DATE =
  /* OID, NamespaceURI, name, Description, datatype */
    DefaultDefinition.shared(new DefaultTypeDefinition(null, XSD_NAMESPACE_URI,
          XSD_NAME_DATE, "XMLSchema date", new DateType(), StandardDateFormatters.XSDDateConverter.getInstance()));
  
  
  public static final String XSD_NAME_YEAR = "gYear";
  /**
   * Type definition for XMLSchema gYear type. A gYear
   * type is represented by a
   * <code>com.optimasc.lang.GregorianDatetimeCalendar</code> java object. 
   */
  public static final EntityTypeDefinition XSD_YEAR =
  /* OID, NamespaceURI, name, Description, datatype */
    DefaultDefinition.shared(new DefaultTypeDefinition(null, XSD_NAMESPACE_URI,
        XSD_NAME_YEAR, "XMLSchema gYear", new YearType(), StandardDateFormatters.XSDGYearConverter.getInstance()));
  
  
  
  public static final String XSD_NAME_DECIMAL = "decimal";
  /**
   * Type definition for XMLSchema decimal type. A decimal type is represented by a
   * <code>java.math.BigDecimal</code> java object. 
   */
  public static final EntityTypeDefinition XSD_DECIMAL =
  /* OID, NamespaceURI, name, Description, datatype */
    DefaultDefinition.shared(new DefaultTypeDefinition(null, XSD_NAMESPACE_URI,
          XSD_NAME_DECIMAL, "XMLSchema decimal", new DecimalType(), NumericFormatters.XSDDecimalConverter.getInstance()));

  public static final String XSD_NAME_INTEGER = "integer";
  /**
   * Type definition for XMLSchema integer type. An integer type is represented by a
   * <code>java.math.BigInteger</code> java object. 
   */
  public static final EntityTypeDefinition XSD_INTEGER =
  /* OID, NamespaceURI, name, Description, datatype */
    DefaultDefinition.shared(new DefaultTypeDefinition(null, XSD_NAMESPACE_URI,
          XSD_NAME_INTEGER, "XMLSchema integer", new IntegralType(), NumericFormatters.XSDIntegerConverter.getInstance()));
  
  
  public static final String XSD_NAME_INT8 = "byte";
  /**
   * Type definition for XMLSchema byte type. A byte type is represented by a
   * <code>java.lang.Integer</code> java object. 
   */
  public static final EntityTypeDefinition XSD_INT8 =
  /* OID, NamespaceURI, name, Description, datatype */
    DefaultDefinition.shared(new DefaultTypeDefinition(null, XSD_NAMESPACE_URI,
          XSD_NAME_INT8, "XMLSchema byte", new ByteType(), NumericFormatters.XSDByteConverter.getInstance()));
  
  
  public static final String XSD_NAME_INT16 = "short";
  /**
   * Type definition for XMLSchema short type. A short type is represented by a
   * <code>java.lang.Integer</code> java object. 
   */
  public static final EntityTypeDefinition XSD_INT16 =
  /* OID, NamespaceURI, name, Description, datatype */
    DefaultDefinition.shared(new DefaultTypeDefinition(null, XSD_NAMESPACE_URI,
          XSD_NAME_INT16, "XMLSchema short", new ShortType(), NumericFormatters.XSDShortConverter.getInstance()));
  
  
  
  public static final String XSD_NAME_INT = "int";
  /**
   * Type definition for XMLSchema int type. An int type is represented by a
   * <code>java.lang.Integer</code> java object. 
   */
  public static final EntityTypeDefinition XSD_INT =
  /* OID, NamespaceURI, name, Description, datatype */
    DefaultDefinition.shared(new DefaultTypeDefinition(null, XSD_NAMESPACE_URI,
          XSD_NAME_INT, "XMLSchema int", new IntType(), NumericFormatters.XSDIntConverter.getInstance()));
  
  public static final String XSD_NAME_LONG = "long";
  /**
   * Type definition for XMLSchema long type. A long type is represented by a
   * <code>java.lang.Long</code> java object. 
   */
  public static final EntityTypeDefinition XSD_LONG =
  /* OID, NamespaceURI, name, Description, datatype */
    DefaultDefinition.shared(new DefaultTypeDefinition(null, XSD_NAMESPACE_URI,
          XSD_NAME_LONG, "XMLSchema long", new LongType(), NumericFormatters.XSDLongConverter.getInstance()));
  
  public static final String XSD_NAME_UINT8 = "unsignedByte";
  /**
   * Type definition for XMLSchema unsignedByte type. An unsignedByte type is represented by a
   * <code>java.lang.Integer</code> java object. 
   */
  public static final EntityTypeDefinition XSD_UINT8 =
  /* OID, NamespaceURI, name, Description, datatype */
    DefaultDefinition.shared(new DefaultTypeDefinition(null, XSD_NAMESPACE_URI,
          XSD_NAME_UINT8, "XMLSchema unsignedByte", new UnsignedByteType(), NumericFormatters.XSDUnsignedByteConverter.getInstance()));
  
  public static final String XSD_NAME_UINT16 = "unsignedShort";
  /**
   * Type definition for XMLSchema unsignedShort type. An unsignedShort type is represented by a
   * <code>java.lang.Integer</code> java object. 
   */
  public static final EntityTypeDefinition XSD_UINT16 =
  /* OID, NamespaceURI, name, Description, datatype */
    DefaultDefinition.shared(new DefaultTypeDefinition(null, XSD_NAMESPACE_URI,
          XSD_NAME_UINT16, "XMLSchema unsignedShort", new UnsignedShortType(), NumericFormatters.XSDUnsignedShortConverter.getInstance()));

  public static final String XSD_NAME_UINT32 = "unsignedInt";
  /**
   * Type definition for XMLSchema unsignedInt type. An unsignedShort type is represented by a
   * <code>java.lang.Long</code> java object. 
   */
  public static final EntityTypeDefinition XSD_UINT32 =
  /* OID, NamespaceURI, name, Description, datatype */
    DefaultDefinition.shared(new DefaultTypeDefinition(null, XSD_NAMESPACE_URI,
          XSD_NAME_UINT32, "XMLSchema unsignedInt", new UnsignedIntType(), NumericFormatters.XSDUnsignedIntConverter.getInstance()));
  
  
  public static final String XSD_NAME_NON_NEGATIVE_INTEGER = "nonNegativeInteger";
  /**
   * Type definition for XMLSchema nonNegativeInteger type. A nonNegativeInteger
   * type is represented by a
   * <code>java.math.BigInteger</code> java object. 
   */
  public static final EntityTypeDefinition XSD_NON_NEGATIVE_INTEGER =
  /* OID, NamespaceURI, name, Description, datatype */
    DefaultDefinition.shared(new DefaultTypeDefinition(null, XSD_NAMESPACE_URI,
        XSD_NAME_NON_NEGATIVE_INTEGER, "XMLSchema nonNegativeInteger", new NonNegativeIntegerType(), NumericFormatters.XSDNonNegativeIntegerConverter.getInstance()));
  
  
  public static final String XSD_NAME_FLOAT = "float";
  /**
   * Type definition for XMLSchema float type. A float type is represented by a
   * <code>java.lang.Float</code> java object. 
   */
  public static final EntityTypeDefinition XSD_FLOAT =
  /* OID, NamespaceURI, name, Description, datatype */
    DefaultDefinition.shared(new DefaultTypeDefinition(null, XSD_NAMESPACE_URI,
          XSD_NAME_FLOAT, "XMLSchema float", new SingleType(), NumericFormatters.XSDFloatConverter.getInstance()));
  
  public static final String XSD_NAME_DOUBLE = "double";
  /**
   * Type definition for XMLSchema double type. A double type is represented by a
   * <code>java.lang.Double</code> java object. 
   */
  public static final EntityTypeDefinition XSD_DOUBLE =
  /* OID, NamespaceURI, name, Description, datatype */
    DefaultDefinition.shared(new DefaultTypeDefinition(null, XSD_NAMESPACE_URI,
          XSD_NAME_DOUBLE, "XMLSchema double", new DoubleType(), NumericFormatters.XSDDoubleConverter.getInstance()));
  
  
  public static final String XSD_NAME_STRING = "string";
  /**
   * Type definition for XMLSchema string type. A string type is represented by a
   * <code>java.lang.String</code> java object. 
   */
  public static final EntityTypeDefinition XSD_STRING =
  /* OID, NamespaceURI, name, Description, datatype */
    DefaultDefinition.shared(new DefaultTypeDefinition(null, XSD_NAMESPACE_URI,
          XSD_NAME_STRING, "XMLSchema string", new StringType(new UnnamedTypeReference(new UnicodeCharType())), StandardFormatters.XSDStringConverter.getInstance()));
  
  
  
  
  
  public static final String XSD_NAME_NORMALIZED_STRING = "normalizedString";
  /**
   * Type definition for XMLSchema normalizedString type. A normaliedString type is represented by a
   * <code>java.lang.String</code> java object. 
   */
  public static final EntityTypeDefinition XSD_NORMALIZED_STRING =
  /* OID, NamespaceURI, name, Description, datatype */
    DefaultDefinition.shared(new DefaultTypeDefinition(null, XSD_NAMESPACE_URI,
          XSD_NAME_NORMALIZED_STRING, "XMLSchema normalizedString", new StringType(new UnnamedTypeReference(new UnicodeCharType())), StandardFormatters.XSDNormalizedStringConverter.getInstance()));
  
  public static final String XSD_NAME_TOKEN = "token";
  /**
   * Type definition for XMLSchema token type. A token type is represented by a
   * <code>java.lang.String</code> java object. 
   */
  public static final EntityTypeDefinition XSD_TOKEN =
  /* OID, NamespaceURI, name, Description, datatype */
    DefaultDefinition.shared(new DefaultTypeDefinition(null, XSD_NAMESPACE_URI,
          XSD_NAME_TOKEN, "XMLSchema token", new StringType(new UnnamedTypeReference(new UnicodeCharType())), StandardFormatters.XSDTokenConverter.getInstance()));
  
  
  public static final String XSD_NAME_UINT64 = "unsignedLong";
  /**
   * Type definition for XMLSchema unsignedLong type. An unsignedLong type is represented by a
   * <code>java.math.BigInteger</code> java object.
   */
  public static final EntityTypeDefinition XSD_UINT64 =
  /* OID, NamespaceURI, name, Description, datatype */
    DefaultDefinition.shared(new DefaultTypeDefinition(null, XSD_NAMESPACE_URI,
          XSD_NAME_UINT64, "XMLSchema unsignedLong", new UnsignedLongType(), NumericFormatters.XSDUnsignedLongConverter.getInstance()));


  public static final String XSD_NAME_ANYURI = "anyURI";
  /**
   * Type definition for XMLSchema anyURI type. An anyURI type is represented by a
   * <code>java.net.URI</code> java object. Only non-empty URI references conforming
   * to IETF RFC 3986 are accepted; IRIs are rejected.
   */
  public static final EntityTypeDefinition XSD_ANYURI =
  /* OID, NamespaceURI, name, Description, datatype */
    DefaultDefinition.shared(new DefaultTypeDefinition(null, XSD_NAMESPACE_URI,
          XSD_NAME_ANYURI, "XMLSchema anyURI", new URIType(), StandardFormatters.URIConverter.getInstance()));


  public static final String XSD_NAME_DATETIME = "dateTime";
  /**
   * Type definition for XMLSchema dateTime type. A dateTime type is represented by a
   * <code>com.optimasc.lang.GregorianDatetimeCalendar</code> java object, with
   * second precision and no timezone. Values with a timezone are rejected.
   */
  public static final EntityTypeDefinition XSD_DATETIME =
  /* OID, NamespaceURI, name, Description, datatype */
    DefaultDefinition.shared(new DefaultTypeDefinition(null, XSD_NAMESPACE_URI,
          XSD_NAME_DATETIME, "XMLSchema dateTime", new DateTimeType(), StandardDateFormatters.XSDDateTimeConverter.getInstance()));


  public static final String XSD_NAME_TIME = "time";
  /**
   * Type definition for XMLSchema time type. A time type is represented by a
   * <code>com.optimasc.lang.GregorianDatetimeCalendar</code> java object, with
   * second precision and no timezone. Values with a timezone are rejected.
   */
  public static final EntityTypeDefinition XSD_TIME =
  /* OID, NamespaceURI, name, Description, datatype */
    DefaultDefinition.shared(new DefaultTypeDefinition(null, XSD_NAMESPACE_URI,
          XSD_NAME_TIME, "XMLSchema time", new TimeType(), StandardDateFormatters.XSDTimeConverter.getInstance()));


  public static final String XSD_NAME_DURATION = "duration";
  /**
   * Type definition for XMLSchema duration type. A duration type is represented by a
   * <code>javax.xml.datatype.Duration</code> java object. Negative durations are
   * rejected.
   */
  public static final EntityTypeDefinition XSD_DURATION =
  /* OID, NamespaceURI, name, Description, datatype */
    DefaultDefinition.shared(new DefaultTypeDefinition(null, XSD_NAMESPACE_URI,
          XSD_NAME_DURATION, "XMLSchema duration", new CalendarDurationType(), CalendarDurationConverter.getInstance()));


  public static final String XSD_NAME_NAME = "Name";
  /**
   * Type definition for XMLSchema Name type. A Name type is represented by a
   * <code>java.lang.String</code> java object.
   */
  public static final EntityTypeDefinition XSD_NAME =
  /* OID, NamespaceURI, name, Description, datatype */
    DefaultDefinition.shared(new DefaultTypeDefinition(null, XSD_NAMESPACE_URI,
          XSD_NAME_NAME, "XMLSchema Name", new StringType(new UnnamedTypeReference(new UnicodeCharType())), StandardFormatters.XSDNameConverter.getInstance()));


  public static final String XSD_NAME_NCNAME = "NCName";
  /**
   * Type definition for XMLSchema NCName type. A NCName type is represented by a
   * <code>java.lang.String</code> java object. This is distinct from the
   * draft-codere-ldapsyntax NCName syntax, which has a smaller repertoire.
   */
  public static final EntityTypeDefinition XSD_NCNAME =
  /* OID, NamespaceURI, name, Description, datatype */
    DefaultDefinition.shared(new DefaultTypeDefinition(null, XSD_NAMESPACE_URI,
          XSD_NAME_NCNAME, "XMLSchema NCName", new StringType(new UnnamedTypeReference(new UnicodeCharType())), StandardFormatters.XSDNCNameConverter.getInstance()));


  public static final String XSD_NAME_NMTOKEN = "NMTOKEN";
  /**
   * Type definition for XMLSchema NMTOKEN type. A NMTOKEN type is represented by a
   * <code>java.lang.String</code> java object.
   */
  public static final EntityTypeDefinition XSD_NMTOKEN =
  /* OID, NamespaceURI, name, Description, datatype */
    DefaultDefinition.shared(new DefaultTypeDefinition(null, XSD_NAMESPACE_URI,
          XSD_NAME_NMTOKEN, "XMLSchema NMTOKEN", new StringType(new UnnamedTypeReference(new UnicodeCharType())), StandardFormatters.XSDNMTOKENConverter.getInstance()));


}
