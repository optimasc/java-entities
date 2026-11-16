package com.optimasc.entity.schema;

/**
 * The global catalog of the standard definitions: the syntaxes of
 * {@link StandardTypes}, the attribute definitions of
 * {@link StandardAttributes}, and the object-class definitions of
 * {@link StandardClasses}.
 *
 * <p>The type definitions of {@link ExtendedTypes} and {@link XMLSchemaTypes}
 * are not registered by default, so that they are only loaded when used. An
 * application that needs to retrieve them by name or OID adds them to its own
 * schema, or to this one, with {@link BasicSchema#add(com.optimasc.schema.Definition)}.</p>
 *
 * <h2>Shared definitions</h2>
 * <p>The constants of {@link StandardTypes}, {@link ExtendedTypes},
 * {@link XMLSchemaTypes}, {@link StandardAttributes} and
 * {@link StandardClasses} are {@link DefaultDefinition#isShared() shared}
 * definitions: this schema lists them but does not own them, and their
 * {@link com.optimasc.entity.Entity#getNamespace() namespace} is always
 * {@link UnboundSchemaNamespace#INSTANCE}. Any other schema, such as the
 * schema of a domain namespace, can therefore add the subset of standard
 * definitions it uses, alongside the definitions it owns. A domain schema can
 * also resolve every standard definition by name, without adding it, by
 * delegating to this schema with a {@link DelegatingSchema}.</p>
 *
 * <p>Every standard definition must have a unique expanded name and OID:
 * {@link #getInstance()} fails with an {@code IllegalStateException} when this
 * class is loaded if two of them collide.</p>
 *
 * <h2>Concurrency</h2>
 * <p>The schema is a thread-safe {@link BasicSchema}: definitions can be added
 * while other threads use it.</p>
 *
 * <p>This class is not instantiable.</p>
 *
 * @see StandardTypes
 * @see ExtendedTypes
 * @see XMLSchemaTypes
 * @see StandardAttributes
 * @see StandardClasses
 * @see DelegatingSchema
 * @author Carl Eric Codere
 */
public final class StandardSchema
{
  /**
   * The singleton instance, populated once at class-load time.
   */
  private static final BasicSchema INSTANCE = build();

  /** Not instantiable. */
  private StandardSchema()
  {
  }

  /**
   * Returns the global schema containing the standard type, attribute, and
   * class definitions.
   *
   * @return The non-null global schema singleton.
   */
  public static BasicSchema getInstance()
  {
    return INSTANCE;
  }

  /**
   * Builds and populates the backing {@link BasicSchema} once.
   *
   * @throws IllegalStateException if two standard definitions have the same
   *   expanded name or OID.
   */
  private static BasicSchema build()
  {
    BasicSchema schema = new BasicSchema();

    // --- Type definitions (syntaxes) ------------------------------------
    
    // Base ASN.1 equivalent syntaxes
    register(schema, StandardTypes.BOOLEAN);
    register(schema, StandardTypes.STRING);
    register(schema, StandardTypes.INTEGER);
    register(schema, StandardTypes.OBJECT_IDENTIFIER);
    register(schema, StandardTypes.OCTET_STRING);
    register(schema, StandardTypes.JAVA_CLASS);
    register(schema, StandardTypes.GENERALIZEDTIME);
    register(schema, StandardTypes.IA5STRING);
    register(schema, StandardTypes.PRINTABLESTRING);
    register(schema, StandardTypes.DATE);
    register(schema, StandardTypes.DATETIME);
    register(schema, StandardTypes.DURATION);
    register(schema, StandardTypes.VISIBLE_STRING);
    register(schema, StandardTypes.REAL);
    register(schema, StandardTypes.TIME_OF_DAY);
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
    register(schema, StandardAttributes.ATTRIBUTE_NAME);
    register(schema, StandardAttributes.ATTRIBUTE_OBJECT_CLASS);
    register(schema, StandardAttributes.ATTRIBUTE_NAME_NS_URI);
    register(schema, StandardAttributes.ATTRIBUTE_OBSOLETE);
    register(schema, StandardAttributes.ATTRIBUTE_ORIGIN);
    register(schema, StandardAttributes.ATTRIBUTE_DESCRIPTION);
    register(schema, StandardAttributes.ATTRIBUTE_OBJECT_IDENTIFIER);
    register(schema, StandardAttributes.ATTRIBUTE_DISPLAY_NAME);
    register(schema, StandardAttributes.ATTRIBUTE_DATATYPE_NAME);
    register(schema, StandardAttributes.ATTRIBUTE_TYPE_JAVA_CLASS_NAME);
    register(schema, StandardAttributes.ATTRIBUTE_READ_ONLY);
    register(schema, StandardAttributes.ATTRIBUTE_TYPE_NAME);
    register(schema, StandardAttributes.ATTRIBUTE_MAX_VALUE_LENGTH);
    register(schema, StandardAttributes.ATTRIBUTE_VALUE_TYPE);

    // --- Class (object-class) definitions -------------------------------
    register(schema, StandardClasses.CLASS_TOP);
    register(schema, StandardClasses.CLASS_DEFINITION);
    register(schema, StandardClasses.ATTRIBUTE_DEFINITION);
    register(schema, StandardClasses.TYPE_DEFINITION);

    return schema;
  }

  /**
   * Adds a standard definition to the schema being built.
   *
   * @param schema     [in] The schema being built.
   * @param definition [in] The standard definition to add.
   * @throws IllegalStateException if the schema already contains a definition
   *   with the same expanded name or OID.
   */
  private static void register(BasicSchema schema,
      com.optimasc.schema.Definition definition)
  {
    if (schema.add(definition) == false)
      throw new IllegalStateException("Duplicate name or OID in the standard "
          + "schema: '" + definition.getExpandedName() + "'"
          + ((definition.getOID() != null) ? " (" + definition.getOID() + ")" : "")
          + ".");
  }
}
