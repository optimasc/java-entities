package com.optimasc.entity.schema;

import com.optimasc.datatypes.aggregate.ClassType;
import com.optimasc.datatypes.defined.ASCIICharType;
import com.optimasc.datatypes.defined.BinaryType;
import com.optimasc.datatypes.defined.DateType;
import com.optimasc.datatypes.defined.DoubleType;
import com.optimasc.datatypes.defined.ObjectIdentifierType;
import com.optimasc.datatypes.defined.StringType;
import com.optimasc.datatypes.defined.VisibleCharType;
import com.optimasc.datatypes.derived.UnicodeCharType;
import com.optimasc.datatypes.primitives.BooleanType;
import com.optimasc.datatypes.primitives.CalendarDurationType;
import com.optimasc.datatypes.primitives.DateTimeType;
import com.optimasc.datatypes.primitives.IntegralType;
import com.optimasc.datatypes.primitives.TimeType;
import com.optimasc.date.DateTime;
import com.optimasc.text.CalendarDurationConverter;
import com.optimasc.text.NumericFormatters;
import com.optimasc.text.StandardDateFormatters;
import com.optimasc.text.StandardFormatters;

import omg.org.astm.type.UnnamedTypeReference;

/**
 * Base type definitions: the syntaxes that are equivalent to an ASN.1
 * built-in type (ITU-T X.680), defined by IETF RFC 4517 and IETF
 * draft-codere-ldapsyntax. {@link #JAVA_CLASS} is the only definition that
 * has no ASN.1 equivalent.
 *
 * <p>
 * These are the only type definitions registered in {@link StandardSchema}.
 * Syntaxes of IETF draft-codere-ldapsyntax with no ASN.1 built-in equivalent
 * are defined by {@link ExtendedTypes}, and the XML Schema 1.1 datatypes by
 * {@link XMLSchemaTypes}.
 * </p>
 *
 * <table>
 * <caption>Base type definitions</caption>
 * <tr>
 * <th>Java Name</th>
 * <th>Expanded Name</th>
 * <th>ID</th>
 * <th>ASN.1 type</th>
 * <th>Java Object representation</th>
 * <th>Description</th>
 * </tr>
 * <tr>
 * <td>{@link #BOOLEAN}</td>
 * <td>BOOLEAN</td>
 * <td>{@link #OID_BOOLEAN}</td>
 * <td>BOOLEAN</td>
 * <td><code>java.lang.Boolean</code></td>
 * <td>A boolean value</td>
 * </tr>
 * <tr>
 * <td>{@link #STRING}</td>
 * <td>DirectoryString</td>
 * <td>{@link #OID_STRING}</td>
 * <td>DirectoryString (X.520)</td>
 * <td><code>java.lang.String</code></td>
 * <td>A Unicode string</td>
 * </tr>
 * <tr>
 * <td>{@link #INTEGER}</td>
 * <td>INTEGER</td>
 * <td>{@link #OID_INTEGER}</td>
 * <td>INTEGER</td>
 * <td><code>java.math.BigInteger</code></td>
 * <td>An integer value of unlimited range</td>
 * </tr>
 * <tr>
 * <td>{@link #OBJECT_IDENTIFIER}</td>
 * <td>OBJECT IDENTIFIER</td>
 * <td>{@link #OID_OBJECT_IDENTIFIER}</td>
 * <td>OBJECT IDENTIFIER</td>
 * <td><code>java.lang.String</code></td>
 * <td>An object identifier in dotted-decimal notation</td>
 * </tr>
 * <tr>
 * <td>{@link #OCTET_STRING}</td>
 * <td>OCTET STRING</td>
 * <td>{@link #OID_OCTET_STRING}</td>
 * <td>OCTET STRING</td>
 * <td><code>byte[]</code></td>
 * <td>Binary data, exchanged with the backend as raw octets</td>
 * </tr>
 * <tr>
 * <td>{@link #JAVA_CLASS}</td>
 * <td>javaClass</td>
 * <td></td>
 * <td>(none)</td>
 * <td><code>java.lang.Class</code></td>
 * <td>A Java class</td>
 * </tr>
 * <tr>
 * <td>{@link #GENERALIZEDTIME}</td>
 * <td>GeneralizedTime</td>
 * <td>{@link #OID_GENERALIZEDTIME}</td>
 * <td>GeneralizedTime</td>
 * <td><code>com.optimasc.lang.GregorianDatetimeCalendar</code></td>
 * <td>A date and time with millisecond precision and timezone</td>
 * </tr>
 * <tr>
 * <td>{@link #IA5STRING}</td>
 * <td>IA5String</td>
 * <td>{@link #OID_IA5STRING}</td>
 * <td>IA5String</td>
 * <td><code>java.lang.String</code></td>
 * <td>A string of characters from U+0000 to U+007F</td>
 * </tr>
 * <tr>
 * <td>{@link #PRINTABLESTRING}</td>
 * <td>PrintableString</td>
 * <td>{@link #OID_PRINTABLESTRING}</td>
 * <td>PrintableString</td>
 * <td><code>java.lang.String</code></td>
 * <td>A string of characters from the PrintableString repertoire</td>
 * </tr>
 * <tr>
 * <td>{@link #DATE}</td>
 * <td>Date</td>
 * <td>{@link #OID_DATE}</td>
 * <td>DATE</td>
 * <td><code>com.optimasc.lang.GregorianDatetimeCalendar</code></td>
 * <td>A date with day precision, with a year from 1582 to 9999</td>
 * </tr>
 * <tr>
 * <td>{@link #DATETIME}</td>
 * <td>Date-Time</td>
 * <td>{@link #OID_DATETIME}</td>
 * <td>DATE-TIME</td>
 * <td><code>com.optimasc.lang.GregorianDatetimeCalendar</code></td>
 * <td>A date and local time with second precision and no timezone, with a year from 1582 to 9999</td>
 * </tr>
 * <tr>
 * <td>{@link #DURATION}</td>
 * <td>Duration</td>
 * <td>{@link #OID_DURATION}</td>
 * <td>DURATION</td>
 * <td><code>javax.xml.datatype.Duration</code></td>
 * <td>A duration with a month component and a day-time component, with millisecond precision</td>
 * </tr>
 * <tr>
 * <td>{@link #VISIBLE_STRING}</td>
 * <td>VisibleString</td>
 * <td>{@link #OID_VISIBLE_STRING}</td>
 * <td>VisibleString</td>
 * <td><code>java.lang.String</code></td>
 * <td>A string of visible ASCII characters (U+0020 to U+007E)</td>
 * </tr>
 * <tr>
 * <td>{@link #REAL}</td>
 * <td>Real</td>
 * <td>{@link #OID_REAL}</td>
 * <td>REAL</td>
 * <td><code>java.lang.Double</code></td>
 * <td>A real number, rounded to double precision</td>
 * </tr>
 * <tr>
 * <td>{@link #TIME_OF_DAY}</td>
 * <td>TimeOfDay</td>
 * <td>{@link #OID_TIME_OF_DAY}</td>
 * <td>TIME-OF-DAY</td>
 * <td><code>com.optimasc.lang.GregorianDatetimeCalendar</code></td>
 * <td>A local time of day with second precision and no timezone</td>
 * </tr>
 * </table>
 *
 * <h2>Lookup</h2>
 * <p>Each type in this catalog has two stable identifiers: its expanded
 * name, exposed as a {@code NAME_*} constant, and its OID, exposed
 * as an {@code OID_*} constant. Either may be passed to
 * {@link Schema#getTypeDefinition(String)} on {@link StandardSchema} to
 * retrieve the type. The sole exception is {@link #JAVA_CLASS}, which has no
 * OID and can be retrieved only by its name {@code "javaClass"}. These types
 * have no namespace, so their expanded name is their local name.</p>
 *
 * @see ExtendedTypes
 * @see XMLSchemaTypes
 * @see StandardSchema
 */
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
  public static final String NAME_STRING = "DirectoryString";
  /**
   * Type definition for generic string type. This is a generic type
   * representing any unicode string. This is represented by a
   * <code>java.lang.String</code> java object.
   */
  public static final EntityTypeDefinition STRING =
  /* OID, NamespaceURI, name, Description, datatype */
  new DefaultTypeDefinition(OID_STRING, null,
          NAME_STRING, "Directory String", new StringType(new UnnamedTypeReference(new UnicodeCharType())), StandardFormatters.StringConverter.getInstance());
  
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
  OID_INTEGER, null, NAME_INTEGER, "INTEGER", new IntegralType(),NumericFormatters.IntegerCanonicalConverter.getInstance());

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
  public static final String NAME_OCTET_STRING = "BINARY";
  /**
   * Type definition for an OCTET STRING type. This is a generic type
   * representing binary data. This is represented by a <code>byte[]</code>
   * java object.
   */
  public static final EntityTypeDefinition OCTET_STRING =
  /* OID, NamespaceURI, name, Description, Class, formatter, allowedValues */
  new DefaultTypeDefinition(OID_OCTET_STRING, 
      null, NAME_OCTET_STRING,
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

  /*---------------- Base ASN.1 IETF draft-codere-ldapsyntax syntaxes ----------------*/
  
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
  
}
