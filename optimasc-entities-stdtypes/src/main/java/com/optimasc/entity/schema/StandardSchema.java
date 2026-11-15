package com.optimasc.entity.schema;

/**
 * The global, schema that owns every pre-defined standard
 * definition: the syntaxes of {@link StandardTypes}, the attribute
 * definitions of {@link StandardAttributes}, and the object-class
 * definitions of {@link StandardClasses}.
 *
 * <h3>Ownership of the standard constants</h3>
 * <p>The {@code StandardTypes.*}, {@code StandardAttributes.*}, and
 * {@code StandardClasses.*} constants are singletons. Registering a
 * {@link DefaultDefinition} with a {@link BasicSchema} binds that definition's
 * {@link com.optimasc.entity.Entity#getNamespace() namespace} back-reference
 * to the owning schema. Because this schema registers all of those constants,
 * <strong>their owning namespace is this schema</strong>.</p>
 *
 * <p><strong>Do not add the standard definition constants to any other
 * schema.</strong> Doing so would re-point the shared constant's namespace
 * back-reference to that other schema and corrupt this global catalog. Domain
 * schemas never need to: a {@link DelegatingSchema} resolves standard
 * definitions <em>by name</em> through delegation, without holding the
 * constant instances itself.</p>
 *
 * <p>This class is not instantiable.</p>
 *
 * @see StandardTypes
 * @see StandardAttributes
 * @see StandardClasses
 * @see DelegatingSchema
 * @author Carl Eric Codere
 */
public final class StandardSchema
{
  /**
   * The singleton instance, populated once at class-load time and exposed
   * only through the read-only {@link Schema} interface.
   */
  private static final BasicSchema INSTANCE = build();

  /** Not instantiable. */
  private StandardSchema()
  {
  }

  /**
   * Returns the shared, read-only global schema containing every standard
   * type, attribute, and class definition.
   *
   * @return The non-null global {@link Schema} singleton.
   */
  public static BasicSchema getInstance()
  {
    return INSTANCE;
  }

  /**
   * Builds and populates the backing {@link BasicSchema} once. Each
   * registration also binds the corresponding standard definition constant's
   * namespace back-reference to the returned schema.
   */
  private static BasicSchema build()
  {
    BasicSchema schema = new BasicSchema();

    // --- Type definitions (syntaxes) ------------------------------------
    
    // Base ASN.1 equivalent syntaxes
    schema.add(StandardTypes.BOOLEAN);
    schema.add(StandardTypes.STRING);
    schema.add(StandardTypes.INTEGER);
    schema.add(StandardTypes.OBJECT_IDENTIFIER);
    schema.add(StandardTypes.OCTET_STRING);
    schema.add(StandardTypes.JAVA_CLASS);
    schema.add(StandardTypes.GENERALIZEDTIME);
    schema.add(StandardTypes.IA5STRING);
    schema.add(StandardTypes.PRINTABLESTRING);
    schema.add(StandardTypes.DATE);
    schema.add(StandardTypes.DATETIME);
    schema.add(StandardTypes.DURATION);
    schema.add(StandardTypes.VISIBLE_STRING);
    schema.add(StandardTypes.REAL);
    schema.add(StandardTypes.TIME_OF_DAY);
    // Extended equivalent syntaxes - removed to save memory/load time
/*    
    schema.add(StandardTypes.FLOAT32);
    schema.add(StandardTypes.FLOAT64);
    schema.add(StandardTypes.INT8);
    schema.add(StandardTypes.INT16);
    schema.add(StandardTypes.INT32);
    schema.add(StandardTypes.INT64);
    schema.add(StandardTypes.LANGUAGE);
    schema.add(StandardTypes.MEDIA_TYPE);
    schema.add(StandardTypes.NCNAME);
    schema.add(StandardTypes.NORMALIZED_STRING);
    schema.add(StandardTypes.OPENDATE);
    schema.add(StandardTypes.PERCENTAGE);
    schema.add(StandardTypes.QUALIFIED_NAME);
    schema.add(StandardTypes.TEXT);
    schema.add(StandardTypes.TOKEN);
    schema.add(StandardTypes.TIME_OF_DAY_TZ);
    schema.add(StandardTypes.UINT8);
    schema.add(StandardTypes.UINT16);
    schema.add(StandardTypes.UINT32);
    schema.add(StandardTypes.UINT64);
    schema.add(StandardTypes.URI);
*/    

    // --- XMLSchema 1.1 type definitions ---------------------------------
    // Removed to save memory/load-time
/*    schema.add(StandardTypes.XSD_BOOLEAN);
    schema.add(StandardTypes.XSD_BASE64BINARY);
    schema.add(StandardTypes.XSD_HEXBINARY);
    schema.add(StandardTypes.XSD_LANGUAGE);
    schema.add(StandardTypes.XSD_TIMESTAMP);
    schema.add(StandardTypes.XSD_DATE);
    schema.add(StandardTypes.XSD_YEAR);
    schema.add(StandardTypes.XSD_DECIMAL);
    schema.add(StandardTypes.XSD_INTEGER);
    schema.add(StandardTypes.XSD_INT8);
    schema.add(StandardTypes.XSD_INT16);
    schema.add(StandardTypes.XSD_INT);
    schema.add(StandardTypes.XSD_LONG);
    schema.add(StandardTypes.XSD_UINT8);
    schema.add(StandardTypes.XSD_UINT16);
    schema.add(StandardTypes.XSD_UINT32);
    schema.add(StandardTypes.XSD_NON_NEGATIVE_INTEGER);
    schema.add(StandardTypes.XSD_FLOAT);
    schema.add(StandardTypes.XSD_DOUBLE);
    schema.add(StandardTypes.XSD_STRING);
    schema.add(StandardTypes.XSD_NORMALIZED_STRING);
    schema.add(StandardTypes.XSD_TOKEN);
    schema.add(StandardTypes.XSD_UINT64);
    schema.add(StandardTypes.XSD_ANYURI);
    schema.add(StandardTypes.XSD_DATETIME);
    schema.add(StandardTypes.XSD_TIME);
    schema.add(StandardTypes.XSD_DURATION);
    schema.add(StandardTypes.XSD_NAME);
    schema.add(StandardTypes.XSD_NCNAME);
    schema.add(StandardTypes.XSD_NMTOKEN);*/

    // --- Attribute (item) definitions -----------------------------------
    schema.add(StandardAttributes.ATTRIBUTE_NAME);
    schema.add(StandardAttributes.ATTRIBUTE_OBJECT_CLASS);
    schema.add(StandardAttributes.ATTRIBUTE_NAME_NS_URI);
    schema.add(StandardAttributes.ATTRIBUTE_OBSOLETE);
    schema.add(StandardAttributes.ATTRIBUTE_ORIGIN);
    schema.add(StandardAttributes.ATTRIBUTE_DESCRIPTION);
    schema.add(StandardAttributes.ATTRIBUTE_OBJECT_IDENTIFIER);
    schema.add(StandardAttributes.ATTRIBUTE_DISPLAY_NAME);
    schema.add(StandardAttributes.ATTRIBUTE_DATATYPE_NAME);
    schema.add(StandardAttributes.ATTRIBUTE_TYPE_JAVA_CLASS_NAME);
    schema.add(StandardAttributes.ATTRIBUTE_READ_ONLY);
    schema.add(StandardAttributes.ATTRIBUTE_TYPE_NAME);
    schema.add(StandardAttributes.ATTRIBUTE_MAX_VALUE_LENGTH);
    schema.add(StandardAttributes.ATTRIBUTE_VALUE_TYPE);

    // --- Class (object-class) definitions -------------------------------
    schema.add(StandardClasses.CLASS_TOP);
    schema.add(StandardClasses.CLASS_DEFINITION);
    schema.add(StandardClasses.ATTRIBUTE_DEFINITION);
    schema.add(StandardClasses.TYPE_DEFINITION);

    return schema;
  }
}
