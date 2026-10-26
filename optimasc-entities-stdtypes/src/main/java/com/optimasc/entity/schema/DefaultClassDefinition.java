package com.optimasc.entity.schema;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import com.optimasc.attribute.Attribute;
import com.optimasc.attribute.DefaultAttribute;
import com.optimasc.schema.ClassDefinition;
import com.optimasc.schema.Definition;

/** Default, mutable, in-memory implementation of {@link ClassDefinition}.
 *
 *  <p>Extends {@link DefaultDefinition} and adds typed setters and
 *  getters for the attributes specific to a class definition: its
 *  kind, superclass, mandatory and optional item names, allowed parent
 *  class names, restricted flag, and optional Java instantiation
 *  class.</p>
 *
 *  <p>The following defaults are applied at construction time when not
 *  explicitly provided:</p>
 *  <ul>
 *    <li>{@link ClassDefinition#KEY_CLASS_KIND}:
 *        {@link ClassDefinition.ObjectClassKind#structuralClass}</li>
 *    <li>{@link ClassDefinition#KEY_RESTRICTED}: {@code false}</li>
 *    <li>{@link ClassDefinition#KEY_MANDATORY_ATTRIBUTES}: empty
 *        (iterator yields no elements)</li>
 *  </ul>
 *
 *  <p>This class is not synchronized against concurrent multithreaded
 *  access.</p>
 *
 *  @see ClassDefinition
 *  @see DefaultDefinition
 *  @author Carl Eric Codere
 */
public class DefaultClassDefinition extends DefaultDefinition
    implements ClassDefinition
{
  // -----------------------------------------------------------------------
  // Constructors
  // -----------------------------------------------------------------------

  /** Constructs a structural class definition with a local name
   *  and an auto-generated identifier.
   *
   *  <p>Defaults applied:</p>
   *  <ul>
   *    <li>{@link Definition#KEY_OID}:
   *        {@code localName + KEY_ID_SUFFIX}</li>
   *    <li>{@link Definition#KEY_OBSOLETE}: {@code false}</li>
   *    <li>{@link ClassDefinition#KEY_CLASS_KIND}:
   *        {@link ObjectClassKind#structuralClass}</li>
   *    <li>{@link ClassDefinition#KEY_RESTRICTED}: {@code false}</li>
   *    <li>{@link com.optimasc.entity.Entity#getObjectClass()}: set to
   *        {@link ClassDefinition#OBJECT_CLASS_NAME}</li>
   *  </ul>
   *
   * @param namespaceURI  [in] Namespace URI, or {@code null}.
   * @param localName [in] Name. Must not be {@code null}
   *                      or empty, and must not exceed
   *                      {@link Definition#NAME_MAX_LENGTH} characters.
   * @throws IllegalArgumentException if {@code localName} violates
   *   its constraints.
   */
  public DefaultClassDefinition(String namespaceURI, String localName)
  {
    super(namespaceURI, localName);
    setKind(ObjectClassKind.structuralClass);
    setRestricted(false);
    setObjectClass(ClassDefinition.OBJECT_CLASS_NAME);
  }

  /** Constructs a class definition with a name, an explicit
   *  identifier, and a description.
   *
   *  <p>Defaults applied:</p>
   *  <ul>
   *    <li>{@link Definition#KEY_OBSOLETE}: {@code false}</li>
   *    <li>{@link ClassDefinition#KEY_CLASS_KIND}:
   *        {@link ObjectClassKind#structuralClass}</li>
   *    <li>{@link ClassDefinition#KEY_RESTRICTED}: {@code false}</li>
   *    <li>{@link com.optimasc.entity.Entity#getObjectClass()}: set to
   *        {@link ClassDefinition#OBJECT_CLASS_NAME}</li>
   *  </ul>
   *
   * @param id            [in] Unique identifier. Must not be
   *                      {@code null} or empty.
   * @param namespaceURI  [in] Namespace URI, or {@code null}.
   * @param localName [in] Name. Must not be {@code null}
   *                      or empty, and must not exceed
   *                      {@link Definition#NAME_MAX_LENGTH} characters.
   * @param description   [in] Human-readable description, or
   *                      {@code null}. When non-null, must not exceed
   *                      {@link Definition#DESC_MAX_LENGTH} characters.
   * @throws IllegalArgumentException if any argument violates its
   *   constraints.
   */
  public DefaultClassDefinition(String id, String namespaceURI,
      String localName, String description)
  {
    super(id, namespaceURI, localName, description);
    setKind(ObjectClassKind.structuralClass);
    setRestricted(false);
    setObjectClass(ClassDefinition.OBJECT_CLASS_NAME);
  }
  
  
  /** Constructs a class definition with all core schema parameters
   *  explicitly specified.
   *
   *  <p>Defaults applied:</p>
   *  <ul>
   *    <li>{@link Definition#KEY_OBSOLETE}: {@code false}</li>
   *    <li>{@link ClassDefinition#KEY_RESTRICTED}: {@code false}</li>
   *    <li>{@link ClassDefinition#KEY_ALLOWED_PARENTS}: empty (any
   *        parent is permitted). Use {@link #setAllowedParents(String[])}
   *        after construction if restriction is required.</li>
   *    <li>{@link ClassDefinition#KEY_TYPE_JAVA_CLASS_NAME}: none. Use
   *        {@link #setClz(Class)} after construction if a specific
   *        instantiation class is required.</li>
   *    <li>{@link com.optimasc.entity.Entity#getObjectClass()}: set to
   *        {@link ClassDefinition#OBJECT_CLASS_NAME}</li>
   *  </ul>
   *
   * @param oid               [in] Unique identifier in dotted-decimal OID
   *                          notation (e.g. {@code "2.5.6.0"}), or
   *                          {@code null} if no OID is assigned.
   * @param namespaceURI      [in] Namespace URI, or {@code null}. When
   *                          non-null, must end with {@code '/'} or
   *                          {@code '#'}.
   * @param localName         [in] Name. Must not be {@code null} or empty,
   *                          and must not exceed
   *                          {@link Definition#NAME_MAX_LENGTH} characters.
   * @param description       [in] Human-readable description, or
   *                          {@code null}. When non-null, must not exceed
   *                          {@link Definition#DESC_MAX_LENGTH} characters.
   * @param parentClass       [in] Expanded name or OID of the superclass,
   *                          or {@code null} if this class has no parent.
   *                          Equivalent to SUP in ITU-T X.501.
   * @param classKind         [in] Whether this class is abstract,
   *                          structural, or auxiliary. Must not be
   *                          {@code null}. Equivalent to KIND in
   *                          ITU-T X.501.
   * @param mandatoryAttributes [in] Expanded names or OIDs of items that
   *                          must be present on every instance of this
   *                          class, or {@code null} for none. Equivalent
   *                          to MUST CONTAIN in ITU-T X.501. Attributes
   *                          not listed here are implicitly permitted
   *                          (open-world assumption).
   * @throws IllegalArgumentException if any argument violates its
   *   constraints.
   */
  public DefaultClassDefinition(String oid, String namespaceURI,
      String localName, String description, String parentClass,
      ObjectClassKind classKind, String[] mandatoryAttributes)
  {
      super(oid, namespaceURI, localName, description);
      setKind(classKind);
      setRestricted(false);
      if (parentClass != null)
          setSuperClass(parentClass);
      if (mandatoryAttributes != null)
          setMandatoryAttributes(mandatoryAttributes);
      setObjectClass(ClassDefinition.OBJECT_CLASS_NAME);
  }  

  // -----------------------------------------------------------------------
  // ClassDefinition — typed setters
  // -----------------------------------------------------------------------

  /** Sets the kind of this object class.
   *
   * @param kind [in] Must not be {@code null}.
   * @throws IllegalArgumentException if {@code kind} is {@code null}.
   */
  public void setKind(ObjectClassKind kind)
  {
    if (kind == null)
      throw new IllegalArgumentException(
          "Object class kind must not be null.");
    DefaultAttribute attr =
        new DefaultAttribute(KEY_CLASS_KIND, ObjectClassKind.class);
    attr.add(kind);
    attributes.put(attr);
  }

  /** Sets the kind of this object class from its RFC 4512 string
   *  representation.
   *
   *  <p>Accepted values: {@code "abstract"}, {@code "structural"},
   *  {@code "auxiliary"}.</p>
   *
   * @param kind [in] RFC 4512 kind string. Must not be {@code null}.
   * @throws IllegalArgumentException if {@code kind} is {@code null}
   *   or does not match a known kind value.
   */
  public void setKindByName(String kind)
  {
    if (kind == null)
      throw new IllegalArgumentException(
          "Object class kind string must not be null.");
    setKind(ObjectClassKind.valueOf(kind));
  }

  /** Sets the superclass of this class definition.
   *
   * @param superClass [in] Qualified name or OID of the superclass,
   *                   or {@code null} to clear.
   */
  public void setSuperClass(String superClass)
  {
    if (superClass == null)
    {
      attributes.remove(KEY_SUPERCLASS);
      return;
    }
    DefaultAttribute attr =
        new DefaultAttribute(KEY_SUPERCLASS, String.class);
    attr.add(superClass);
    attributes.put(attr);
  }

  /** Sets the mandatory item names for this class definition,
   *  replacing any previously set values.
   *
   *  <p>Each element must be a non-null qualified item name.
   *  Passing {@code null} clears the mandatory attribute list.</p>
   *
   * @param names [in] Array of qualified item names, or {@code null}
   *              to clear.
   * @throws IllegalArgumentException if any element in {@code names}
   *   is {@code null}.
   */
  public void setMandatoryAttributes(String[] names)
  {
    if (names == null)
    {
      attributes.remove(KEY_MANDATORY_ATTRIBUTES);
      return;
    }
    DefaultAttribute attr =
        new DefaultAttribute(KEY_MANDATORY_ATTRIBUTES, String.class);
    for (int i = 0; i < names.length; i++)
    {
      if (names[i] == null)
        throw new IllegalArgumentException(
            "Mandatory attribute name at index " + i
            + " must not be null.");
      attr.add(names[i]);
    }
    attributes.put(attr);
  }

  /** Sets the allowed parent class names for this class definition,
   *  replacing any previously set values.
   *
   *  <p>Each element must be a non-null qualified class name.
   *  Passing {@code null} clears the allowed parents list, meaning
   *  no parent constraint is declared.</p>
   *
   * @param names [in] Array of permitted parent class names, or
   *              {@code null} to clear.
   * @throws IllegalArgumentException if any element in {@code names}
   *   is {@code null}.
   */
  public void setAllowedParents(String[] names)
  {
    if (names == null)
    {
      attributes.remove(KEY_ALLOWED_PARENTS);
      return;
    }
    DefaultAttribute attr =
        new DefaultAttribute(KEY_ALLOWED_PARENTS, String.class);
    for (int i = 0; i < names.length; i++)
    {
      if (names[i] == null)
        throw new IllegalArgumentException(
            "Allowed parent name at index " + i
            + " must not be null.");
      attr.add(names[i]);
    }
    attributes.put(attr);
  }

  /** Sets the restricted flag for this class definition.
   *
   * @param restricted [in] {@code true} if instances of this class
   *                   may not be modified by user applications.
   */
  public void setRestricted(boolean restricted)
  {
    DefaultAttribute attr =
        new DefaultAttribute(KEY_RESTRICTED, Boolean.class);
    attr.add(Boolean.valueOf(restricted));
    attributes.put(attr);
  }

  /** Sets the Java class used to instantiate entities belonging to
   *  this class.
   *
   * @param clz [in] A class implementing
   *            {@link com.optimasc.entity.Entity} with a public
   *            two-parameter constructor
   *            {@code (String namespaceURI, String name)}, or
   *            {@code null} to clear.
   */
  public void setClz(Class<?> clz)
  {
    if (clz == null)
    {
      attributes.remove(KEY_TYPE_JAVA_CLASS_NAME);
      return;
    }
    DefaultAttribute attr =
        new DefaultAttribute(KEY_TYPE_JAVA_CLASS_NAME, String.class);
    attr.add(clz.getName());
    attributes.put(attr);
  }

  /** Sets the Java class used to instantiate entities belonging to
   *  this class, looked up by fully-qualified class name.
   *
   *  <p>This convenience method is intended for cases where only the
   *  class name is available as a string (e.g. when loading
   *  definitions from a configuration file or database). It wraps
   *  {@link ClassNotFoundException} in {@link IllegalArgumentException}
   *  so callers are not burdened with a checked exception.</p>
   *
   * @param className [in] Fully-qualified Java class name. Must not
   *                  be {@code null} or empty.
   * @throws IllegalArgumentException if {@code className} is
   *   {@code null}, empty, or does not identify a loadable class.
   */
  public void setClzByName(String className)
  {
    if (className == null || className.length() == 0)
      throw new IllegalArgumentException(
          "Class name must not be null or empty.");
    try
    {
      Class.forName(className);
    }
    catch (ClassNotFoundException e)
    {
      IllegalArgumentException ex = new IllegalArgumentException(
          "Class '" + className + "' not found.");
      ex.initCause(e);
      throw ex;
    }
    DefaultAttribute attr =
        new DefaultAttribute(KEY_TYPE_JAVA_CLASS_NAME, String.class);
    attr.add(className);
    attributes.put(attr);
  }

  // -----------------------------------------------------------------------
  // ClassDefinition — typed getters
  // -----------------------------------------------------------------------

  /** {@inheritDoc} */
  public ObjectClassKind getKind()
  {
    com.optimasc.attribute.Attribute attr =
        attributes.get(KEY_CLASS_KIND);
    if (attr == null || attr.size() == 0)
      return ObjectClassKind.structuralClass;
    return (ObjectClassKind) attr.get(0);
  }

  /** {@inheritDoc} */
  public boolean isAbstract()
  {
    return getKind() == ObjectClassKind.abstractClass;
  }

  /** {@inheritDoc} */
  public String getSuperClass()
  {
    com.optimasc.attribute.Attribute attr =
        attributes.get(KEY_SUPERCLASS);
    return attr != null && attr.size() > 0
        ? (String) attr.get(0) : null;
  }

  /** {@inheritDoc}
   *
   *  <p>The returned set is a fresh, unmodifiable snapshot built from
   *  the values stored under
   *  {@link ClassDefinition#KEY_MANDATORY_ATTRIBUTES}, preserving
   *  declaration order.</p>
   */
  public Set<String> getMandatoryAttributes()
  {
    return projectNames(KEY_MANDATORY_ATTRIBUTES);
  }

  /** {@inheritDoc}
   *
   *  <p>The returned set is a fresh, unmodifiable snapshot built from
   *  the values stored under {@link ClassDefinition#KEY_ALLOWED_PARENTS},
   *  preserving declaration order.</p>
   */
  public Set<String> getAllowedParents()
  {
    return projectNames(KEY_ALLOWED_PARENTS);
  }

  /** Projects the string values stored under {@code key} into a fresh,
   *  unmodifiable, insertion-ordered {@link Set}.
   *
   *  <p>Returns an empty set when no attribute is stored under
   *  {@code key}. Values are read through the generic
   *  {@link com.optimasc.attribute.Attribute} contract
   *  ({@code size()} / {@code get(int)}) rather than {@code toArray()},
   *  so no assumption is made about the backing array component
   *  type.</p>
   */
  private Set<String> projectNames(String key)
  {
    com.optimasc.attribute.Attribute attr = attributes.get(key);
    if (attr == null || attr.size() == 0)
      return Collections.emptySet();
    LinkedHashSet<String> set = new LinkedHashSet<String>();
    for (int i = 0; i < attr.size(); i++)
      set.add((String) attr.get(i));
    return Collections.unmodifiableSet(set);
  }

  /** {@inheritDoc} */
  public boolean isRestricted()
  {
    com.optimasc.attribute.Attribute attr =
        attributes.get(KEY_RESTRICTED);
    if (attr == null || attr.size() == 0)
      return false;
    Boolean v = (Boolean) attr.get(0);
    return v != null && v.booleanValue();
  }

  /** {@inheritDoc}
   *
   * @throws IllegalArgumentException if the stored class name can no
   *   longer be resolved on the classpath.
   */
  public Class<?> getClz()
  {
    com.optimasc.attribute.Attribute attr =
        attributes.get(KEY_TYPE_JAVA_CLASS_NAME);
    if (attr == null || attr.size() == 0)
      return null;
    String className = (String) attr.get(0);
    try
    {
      return Class.forName(className);
    }
    catch (ClassNotFoundException e)
    {
      IllegalArgumentException ex = new IllegalArgumentException(
          "Class '" + className
          + "' can no longer be found on the classpath.");
      ex.initCause(e);
      throw ex;
    }
  }
}
