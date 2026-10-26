package com.optimasc.entity.schema;

/**
 * The global, read-only schema that owns every pre-defined standard
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
 * <h3>Read-only</h3>
 * <p>{@link #getInstance()} returns the populated catalog typed as
 * {@link Schema}, which exposes no mutators. The underlying store is populated
 * exactly once in a static initializer and is never modified thereafter.</p>
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
  private static final Schema INSTANCE = build();

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
  public static Schema getInstance()
  {
    return INSTANCE;
  }

  /**
   * Builds and populates the backing {@link BasicSchema} once. Each
   * registration also binds the corresponding standard definition constant's
   * namespace back-reference to the returned schema.
   */
  private static Schema build()
  {
    BasicSchema schema = new BasicSchema();

    // --- Type definitions (syntaxes) ------------------------------------
    schema.add(StandardTypes.BOOLEAN);
    schema.add(StandardTypes.NCNAME);
    schema.add(StandardTypes.STRING);
    schema.add(StandardTypes.INTEGER);
    schema.add(StandardTypes.OBJECT_IDENTIFIER);
    schema.add(StandardTypes.OCTET_STRING);
    schema.add(StandardTypes.JAVA_CLASS);
    schema.add(StandardTypes.GENERALIZEDTIME);

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
