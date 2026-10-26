package com.optimasc.entity.schema;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.xml.namespace.QName;

import com.optimasc.attribute.DefaultAttribute;
import com.optimasc.schema.AttributeUsage;
import com.optimasc.schema.ClassDefinition;
import com.optimasc.schema.Definition;
import com.optimasc.schema.ItemDefinition;

/** Default, mutable, in-memory implementation of {@link ItemDefinition}.
 *
 *  <p>Extends {@link DefaultDefinition} and adds typed setters and
 *  getters for the attributes specific to an item definition: its
 *  type name, value type, context type, read-only flag, maximum value
 *  length, attribute usage, and optional choice constraints.</p>
 *
 *  <p>The type name setter ({@link #setTypeName(String)}) accepts an
 *  optional inline length constraint in the form
 *  {@code "typeName{n}"} — for example
 *  {@code "1.3.6.1.4.1.1466.115.121.1.15{64}"}
 *  or {@code "DirectoryString{128}"}. When present, the {@code {n}}
 *  suffix is stripped from the stored type name and the value
 *  {@code n} is stored separately as the maximum value length
 *  (see {@link ItemDefinition#KEY_MAX_VALUE_LENGTH}).</p>
 *
 *  <p>The following defaults are applied at construction time:</p>
 *  <ul>
 *    <li>{@link ItemDefinition#KEY_READONLY}: {@code false}</li>
 *    <li>{@link ItemDefinition#KEY_USAGE}:
 *        {@link AttributeUsage#userApplication}</li>
 *    <li>{@link ItemDefinition#KEY_VALUE_TYPE}:
 *        {@link ItemDefinition#VALUE_TYPE_BAG} (minimal constructors)</li>
 *  </ul>
 *
 *  <p>This class is not synchronized against concurrent multithreaded
 *  access.</p>
 *
 *  @see ItemDefinition
 *  @see DefaultDefinition
 *  @author Carl Eric Codere
 */
public class DefaultItemDefinition extends DefaultDefinition
    implements ItemDefinition
{
  // -----------------------------------------------------------------------
  // Constructors
  // -----------------------------------------------------------------------

  /** Constructs an item definition with only the required name fields.
   *
   *  <p>Defaults applied:</p>
   *  <ul>
   *    <li>{@link ItemDefinition#KEY_VALUE_TYPE}:
   *        {@link ItemDefinition#VALUE_TYPE_BAG}</li>
   *    <li>{@link ItemDefinition#KEY_READONLY}: {@code false}</li>
   *    <li>{@link ItemDefinition#KEY_USAGE}:
   *        {@link AttributeUsage#userApplication}</li>
   *    <li>Object class: {@link ItemDefinition#OBJECT_CLASS_NAME}</li>
   *  </ul>
   *
   * @param namespaceURI [in] Namespace URI, or {@code null}. When
   *                     non-null, must end with {@code '/'} or
   *                     {@code '#'}.
   * @param localName    [in] Local name. Must not be {@code null} or
   *                     empty, and must not exceed
   *                     {@link com.optimasc.schema.Definition#NAME_MAX_LENGTH}
   *                     characters.
   * @throws IllegalArgumentException if any argument violates its
   *   constraints.
   */
  public DefaultItemDefinition(String namespaceURI, String localName)
  {
    super(namespaceURI, localName);
    setValueType(VALUE_TYPE_BAG, null);
    setReadOnly(false);
    setObjectClass(ItemDefinition.OBJECT_CLASS_NAME);
    setUsage(AttributeUsage.userApplication);
  }

  /** Constructs an item definition from a {@link QName}.
   *
   *  <p>The namespace URI and local part are extracted from the
   *  {@code QName}; the prefix is discarded since it is not part of
   *  the definition's identity. A {@code QName} with an empty namespace
   *  URI (as returned by {@link QName#getNamespaceURI()} when no
   *  namespace was specified) is treated as having no namespace
   *  ({@code null}).</p>
   *
   *  <p>Defaults applied: same as
   *  {@link #DefaultItemDefinition(String, String)}.</p>
   *
   * @param qname [in] Must not be {@code null}.
   * @throws IllegalArgumentException if {@code qname} is {@code null},
   *   or if its local part violates the constraints of
   *   {@link #DefaultItemDefinition(String, String)}.
   */
  public DefaultItemDefinition(QName qname)
  {
    this(normalizeNamespaceURI(qname.getNamespaceURI()),
        qname.getLocalPart());
  }

  /** Constructs a fully-specified item definition.
   *
   *  <p>Defaults applied:</p>
   *  <ul>
   *    <li>{@link ItemDefinition#KEY_USAGE}:
   *        {@link AttributeUsage#userApplication}</li>
   *    <li>Object class: {@link ItemDefinition#OBJECT_CLASS_NAME}</li>
   *  </ul>
   *
   * @param id          [in] Unique identifier (OID), or {@code null}.
   * @param namespaceURI [in] Namespace URI, or {@code null}. When
   *                     non-null, must end with {@code '/'} or
   *                     {@code '#'}.
   * @param localName    [in] Local name. Must not be {@code null} or
   *                     empty, and must not exceed
   *                     {@link com.optimasc.schema.Definition#NAME_MAX_LENGTH}
   *                     characters.
   * @param description  [in] Human-readable description, or
   *                     {@code null}. When non-null, must not exceed
   *                     {@link com.optimasc.schema.Definition#DESC_MAX_LENGTH}
   *                     characters.
   * @param typeName     [in] Type name or OID. Must not be {@code null}
   *                     or empty. May include an inline length constraint
   *                     suffix (e.g. {@code "DirectoryString{64}"}).
   * @param valueType    [in] One of {@link ItemDefinition#VALUE_TYPE_SINGLE},
   *                     {@link ItemDefinition#VALUE_TYPE_BAG},
   *                     {@link ItemDefinition#VALUE_TYPE_SEQ}, or
   *                     {@link ItemDefinition#VALUE_TYPE_ALT}.
   * @param contextType  [in] Context type identifier. Required when
   *                     {@code valueType} is
   *                     {@link ItemDefinition#VALUE_TYPE_ALT};
   *                     must be {@code null} for all other value types.
   * @param readOnly     [in] {@code true} if values may only be
   *                     modified by the system.
   * @throws IllegalArgumentException if any argument violates its
   *   constraints.
   */
  public DefaultItemDefinition(String id, String namespaceURI,
      String localName, String description, String typeName,
      String valueType, String contextType, boolean readOnly)
  {
    super(id, namespaceURI, localName, description);
    setTypeName(typeName);
    setValueType(valueType, contextType);
    setReadOnly(readOnly);
    setObjectClass(ItemDefinition.OBJECT_CLASS_NAME);
    setUsage(AttributeUsage.userApplication);
  }

  /** Constructs a fully-specified item definition from a {@link QName}.
   *
   *  <p>The namespace URI and local part are extracted from the
   *  {@code QName}; the prefix is discarded. A {@code QName} with an
   *  empty namespace URI is treated as having no namespace
   *  ({@code null}).</p>
   *
   *  <p>Defaults applied: same as
   *  {@link #DefaultItemDefinition(String, String, String, String,
   *  String, String, String, boolean)}.</p>
   *
   * @param id          [in] Unique identifier (OID), or {@code null}.
   * @param qname       [in] Must not be {@code null}.
   * @param description [in] Human-readable description, or {@code null}.
   * @param typeName    [in] Type name or OID. Must not be {@code null}
   *                    or empty.
   * @param valueType   [in] Value multiplicity and ordering constant.
   * @param contextType [in] Context type; required for ALT, null
   *                    otherwise.
   * @param readOnly    [in] {@code true} if values are system-only.
   * @throws IllegalArgumentException if any argument violates its
   *   constraints.
   */
  public DefaultItemDefinition(String id, QName qname,
      String description, String typeName,
      String valueType, String contextType, boolean readOnly)
  {
    this(id,
        normalizeNamespaceURI(qname.getNamespaceURI()),
        qname.getLocalPart(),
        description, typeName, valueType, contextType, readOnly);
  }

  /** Constructs a fully-specified item definition with choice
   *  constraints.
   *
   *  <p>Defaults applied: same as
   *  {@link #DefaultItemDefinition(String, String, String, String,
   *  String, String, String, boolean)}.</p>
   *
   * @param id          [in] Unique identifier (OID), or {@code null}.
   * @param namespaceURI [in] Namespace URI, or {@code null}.
   * @param localName    [in] Local name. Must not be {@code null} or
   *                     empty.
   * @param description  [in] Human-readable description, or
   *                     {@code null}.
   * @param typeName     [in] Type name or OID. Must not be {@code null}
   *                     or empty.
   * @param valueType    [in] Value multiplicity and ordering constant.
   * @param contextType  [in] Context type; required for ALT, null
   *                     otherwise.
   * @param readOnly     [in] {@code true} if values are system-only.
   * @param choices      [in] Array of permitted or suggested values,
   *                     or {@code null} if no choice constraint is
   *                     declared. When non-null, must be non-empty and
   *                     all elements must be non-null and
   *                     assignment-compatible with each other.
   * @param choiceType   [in] {@link ItemDefinition#CHOICE_TYPE_OPEN} or
   *                     {@link ItemDefinition#CHOICE_TYPE_CLOSED}.
   *                     Required when {@code choices} is non-null;
   *                     must be {@code null} when {@code choices} is
   *                     {@code null}.
   * @throws IllegalArgumentException if any argument violates its
   *   constraints.
   */
  public DefaultItemDefinition(String id, String namespaceURI,
      String localName, String description, String typeName,
      String valueType, String contextType, boolean readOnly,
      Object[] choices, String choiceType)
  {
    this(id, namespaceURI, localName, description,
        typeName, valueType, contextType, readOnly);
    if (choices != null)
      setChoices(choices, choiceType);
  }

  /** Constructs a fully-specified item definition with choice
   *  constraints, using a {@link QName} for the name.
   *
   *  <p>The namespace URI and local part are extracted from the
   *  {@code QName}; the prefix is discarded. A {@code QName} with an
   *  empty namespace URI is treated as having no namespace
   *  ({@code null}).</p>
   *
   * @param id          [in] Unique identifier (OID), or {@code null}.
   * @param qname       [in] Must not be {@code null}.
   * @param description [in] Human-readable description, or {@code null}.
   * @param typeName    [in] Type name or OID. Must not be {@code null}
   *                    or empty.
   * @param valueType   [in] Value multiplicity and ordering constant.
   * @param contextType [in] Context type; required for ALT, null
   *                    otherwise.
   * @param readOnly    [in] {@code true} if values are system-only.
   * @param choices     [in] Array of permitted or suggested values,
   *                    or {@code null}.
   * @param choiceType  [in] {@link ItemDefinition#CHOICE_TYPE_OPEN} or
   *                    {@link ItemDefinition#CHOICE_TYPE_CLOSED}, or
   *                    {@code null} when {@code choices} is {@code null}.
   * @throws IllegalArgumentException if any argument violates its
   *   constraints.
   */
  public DefaultItemDefinition(String id, QName qname,
      String description, String typeName,
      String valueType, String contextType, boolean readOnly,
      Object[] choices, String choiceType)
  {
    this(id,
        normalizeNamespaceURI(qname.getNamespaceURI()),
        qname.getLocalPart(),
        description, typeName, valueType, contextType, readOnly,
        choices, choiceType);
  }

  /** Copy constructor. Constructs a new {@code DefaultItemDefinition}
   *  whose fields are initialised from the public getters of
   *  {@code source}.
   *
   *  <p>This constructor is useful for converting any {@link ItemDefinition}
   *  implementation — in particular one that is not entity-aware — into
   *  this entity-backed implementation.</p>
   *
   *  <p>The object class of the new instance is always set to
   *  {@link ItemDefinition#OBJECT_CLASS_NAME}, regardless of the
   *  source's object class (if any).</p>
   *
   *  <p>The source's choice list (if any) is copied by iterating
   *  {@link ItemDefinition#getChoices()} and converting the result to
   *  an array; the choice type is copied from
   *  {@link ItemDefinition#getChoiceType()}.</p>
   *
   * @param source [in] Must not be {@code null}.
   * @throws IllegalArgumentException if {@code source} is {@code null},
   *   or if any value returned by its getters violates the constraints
   *   of this class.
   */
  public DefaultItemDefinition(ItemDefinition source)
  {
    super(source.getOID(),
        source.getNamespaceURI(),
        source.getLocalName(),
        source.getDescription());
    setTypeName(source.getTypeName());
    setValueType(source.getValueType(), source.getContextType());
    setReadOnly(source.isReadOnly());
    setObjectClass(ItemDefinition.OBJECT_CLASS_NAME);
    setUsage(source.getUsage());
    if (source.getMaxValueLength() != -1)
      setMaxValueLength(source.getMaxValueLength());
    if (source.isObsolete())
      setObsolete(true);
    Set sourceChoices = source.getChoices();
    if (sourceChoices != null)
    {
      Object[] choiceArray = sourceChoices.toArray(new Object[sourceChoices.size()]);
      setChoices(choiceArray, source.getChoiceType());
    }
  }

  // -----------------------------------------------------------------------
  // Private helpers
  // -----------------------------------------------------------------------

  /** Normalises a {@link QName} namespace URI to {@code null} when
   *  the QName carries no namespace (empty string).
   *
   * @param namespaceURI [in] Value from {@link QName#getNamespaceURI()}.
   * @return {@code null} if {@code namespaceURI} is null or empty;
   *         otherwise the original value.
   */
  private static String normalizeNamespaceURI(String namespaceURI)
  {
    if (namespaceURI == null || namespaceURI.length() == 0)
      return null;
    return namespaceURI;
  }

  // -----------------------------------------------------------------------
  // ItemDefinition — typed setters
  // -----------------------------------------------------------------------

  /** Sets the type name or syntax identifier for values of this item.
   *
   *  <p>Accepts an optional inline length constraint suffix in the
   *  form {@code "typeName{n}"}, where {@code n} is a non-negative
   *  integer. When present:</p>
   *  <ul>
   *    <li>The {@code {n}} suffix is stripped and the bare type name
   *        is stored under {@link ItemDefinition#KEY_TYPE_NAME}.</li>
   *    <li>The value {@code n} is stored as the maximum value length
   *        under {@link ItemDefinition#KEY_MAX_VALUE_LENGTH}.</li>
   *  </ul>
   *
   * @param typeName [in] Type name or OID, optionally suffixed with
   *                 {@code {n}}. Must not be {@code null} or empty.
   * @throws IllegalArgumentException if {@code typeName} is
   *   {@code null} or empty, or if a {@code {n}} suffix is present
   *   but {@code n} is not a valid non-negative integer.
   */
  public void setTypeName(String typeName)
  {
    if (typeName == null || typeName.length() == 0)
      throw new IllegalArgumentException(
          "Type name must not be null or empty.");

    int braceIndex = typeName.indexOf('{');
    if (braceIndex != -1)
    {
      int closingBrace = typeName.indexOf('}', braceIndex);
      if (closingBrace == -1)
        throw new IllegalArgumentException(
            "Type name '" + typeName
            + "' contains an opening '{' with no matching '}'.");
      String lengthStr =
          typeName.substring(braceIndex + 1, closingBrace);
      long maxLength;
      try
      {
        maxLength = Long.parseLong(lengthStr);
      }
      catch (NumberFormatException e)
      {
        throw new IllegalArgumentException(
            "Type name '" + typeName
            + "' contains an invalid length constraint '{"
            + lengthStr + "}': must be a non-negative integer.");
      }
      if (maxLength < 0)
        throw new IllegalArgumentException(
            "Type name '" + typeName
            + "' contains a negative length constraint, which is "
            + "not permitted.");
      setMaxValueLength(maxLength);
      typeName = typeName.substring(0, braceIndex);
    }

    if (typeName.length() > 0
        && Character.isDigit(typeName.charAt(0)))
      validateOID(typeName);

    DefaultAttribute attr =
        new DefaultAttribute(KEY_TYPE_NAME, String.class);
    attr.add(typeName);
    attributes.put(attr);
  }

  /** Sets the value type (multiplicity and ordering) and, when
   *  applicable, the context type for alternative values.
   *
   *  <p>The following rules apply to the {@code contextType}
   *  parameter:</p>
   *  <ul>
   *    <li>When {@code valueType} is
   *        {@link ItemDefinition#VALUE_TYPE_ALT},
   *        {@code contextType} must be non-null.</li>
   *    <li>For all other value types, {@code contextType} must be
   *        {@code null}.</li>
   *  </ul>
   *
   * @param valueType   [in] One of
   *                    {@link ItemDefinition#VALUE_TYPE_SINGLE},
   *                    {@link ItemDefinition#VALUE_TYPE_BAG},
   *                    {@link ItemDefinition#VALUE_TYPE_SEQ}, or
   *                    {@link ItemDefinition#VALUE_TYPE_ALT}. Must
   *                    not be {@code null}.
   * @param contextType [in] Context type identifier (e.g.
   *                    {@link ItemDefinition#CONTEXT_LANGUAGE}),
   *                    required for ALT, must be {@code null}
   *                    otherwise.
   * @throws IllegalArgumentException if {@code valueType} is not one
   *   of the defined constants, if {@code contextType} is {@code null}
   *   for an ALT value type, or if {@code contextType} is non-null
   *   for a non-ALT value type.
   */
  public void setValueType(String valueType, String contextType)
  {
    if (valueType == null)
      throw new IllegalArgumentException(
          "Value type must not be null.");
    if (valueType.equals(VALUE_TYPE_SINGLE)
        || valueType.equals(VALUE_TYPE_BAG)
        || valueType.equals(VALUE_TYPE_SEQ))
    {
      if (contextType != null)
        throw new IllegalArgumentException(
            "Context type must be null for value type '"
            + valueType + "'.");
      attributes.remove(KEY_CONTEXT_TYPE);
    }
    else if (valueType.equals(VALUE_TYPE_ALT))
    {
      if (contextType == null)
        throw new IllegalArgumentException(
            "Context type is required for value type '"
            + VALUE_TYPE_ALT + "'.");
      DefaultAttribute ctxAttr =
          new DefaultAttribute(KEY_CONTEXT_TYPE, String.class);
      ctxAttr.add(contextType);
      attributes.put(ctxAttr);
    }
    else
    {
      throw new IllegalArgumentException(
          "Unknown value type '" + valueType
          + "'. Must be one of: VALUE_TYPE_SINGLE, VALUE_TYPE_BAG,"
          + " VALUE_TYPE_SEQ, VALUE_TYPE_ALT.");
    }
    DefaultAttribute attr =
        new DefaultAttribute(KEY_VALUE_TYPE, String.class);
    attr.add(valueType);
    attributes.put(attr);
  }

  /** Sets the read-only flag for this item definition.
   *
   * @param readOnly [in] {@code true} if values of this item may only
   *                 be modified by the system; {@code false} if user
   *                 applications may modify them.
   */
  public void setReadOnly(boolean readOnly)
  {
    DefaultAttribute attr =
        new DefaultAttribute(KEY_READONLY, Boolean.class);
    attr.add(Boolean.valueOf(readOnly));
    attributes.put(attr);
  }

  /** Sets the intended usage of this item definition.
   *
   * @param usage [in] Must not be {@code null}.
   * @throws IllegalArgumentException if {@code usage} is {@code null}.
   */
  public void setUsage(AttributeUsage usage)
  {
    if (usage == null)
      throw new IllegalArgumentException(
          "Attribute usage must not be null.");
    DefaultAttribute attr =
        new DefaultAttribute(KEY_USAGE, AttributeUsage.class);
    attr.add(usage);
    attributes.put(attr);
  }

  /** Sets the maximum permitted length for values of this item.
   *
   *  <p>This setter is also called internally by
   *  {@link #setTypeName(String)} when an inline {@code {n}} length
   *  constraint is detected.</p>
   *
   * @param maxLength [in] Maximum length in characters (strings) or
   *                  octets (binary). Must be non-negative, or
   *                  {@code -1} to clear any existing constraint.
   * @throws IllegalArgumentException if {@code maxLength} is less
   *   than {@code -1}.
   */
  public void setMaxValueLength(long maxLength)
  {
    if (maxLength < -1)
      throw new IllegalArgumentException(
          "Maximum value length must be -1 (unrestricted) or a "
          + "non-negative integer, but got: " + maxLength + ".");
    if (maxLength == -1)
    {
      attributes.remove(KEY_MAX_VALUE_LENGTH);
      return;
    }
    DefaultAttribute attr =
        new DefaultAttribute(KEY_MAX_VALUE_LENGTH, Long.class);
    attr.add(new Long(maxLength));
    attributes.put(attr);
  }

  /** Removes any maximum value length constraint from this item
   *  definition.
   *
   *  <p>After calling this method, {@link #getMaxValueLength()}
   *  returns {@code -1}.</p>
   */
  public void clearMaxValueLength()
  {
    attributes.remove(KEY_MAX_VALUE_LENGTH);
  }

  /** Sets the choice values and choice type for this item definition,
   *  replacing any previously set choice constraint.
   *
   *  <p>All elements in {@code choices} must be non-null and
   *  assignment-compatible with each other: every element must be an
   *  instance of the class of the first element. This is consistent
   *  with the contract of {@link #getChoices()}, whose returned set
   *  contains values whose runtime class corresponds to the value
   *  representation of this item's {@link #getTypeName()}.</p>
   *
   *  <p>Duplicate values (per {@link Object#equals}) are silently
   *  collapsed: only the first occurrence is retained, so the stored
   *  set is always distinct.</p>
   *
   * @param choices    [in] Array of permitted or suggested values.
   *                   Must not be {@code null} or empty. All elements
   *                   must be non-null and assignment-compatible with
   *                   the first element.
   * @param choiceType [in] {@link ItemDefinition#CHOICE_TYPE_OPEN} or
   *                   {@link ItemDefinition#CHOICE_TYPE_CLOSED}. Must
   *                   not be {@code null}.
   * @throws IllegalArgumentException if {@code choices} is
   *   {@code null} or empty, if any element is {@code null}, if
   *   elements are not mutually assignment-compatible, or if
   *   {@code choiceType} is not one of the defined constants.
   */
  public void setChoices(Object[] choices, String choiceType)
  {
    if (choices == null || choices.length == 0)
      throw new IllegalArgumentException(
          "Choices must not be null or empty.");
    if (!CHOICE_TYPE_OPEN.equals(choiceType)
        && !CHOICE_TYPE_CLOSED.equals(choiceType))
      throw new IllegalArgumentException(
          "Choice type must be CHOICE_TYPE_OPEN or "
          + "CHOICE_TYPE_CLOSED, but got: '" + choiceType + "'.");

    // Determine the reference class from the first element and
    // validate homogeneity of the whole array.
    if (choices[0] == null)
      throw new IllegalArgumentException(
          "Choice element at index 0 must not be null.");
    Class referenceClass = choices[0].getClass();
    for (int i = 1; i < choices.length; i++)
    {
      if (choices[i] == null)
        throw new IllegalArgumentException(
            "Choice element at index " + i + " must not be null.");
      if (!referenceClass.isInstance(choices[i]))
        throw new IllegalArgumentException(
            "Choice element at index " + i + " ("
            + choices[i].getClass().getName()
            + ") is not assignment-compatible with the first "
            + "element's class ("
            + referenceClass.getName() + ").");
    }

    // Store choices as a multi-valued attribute using the reference
    // class as the element type. Duplicates are silently collapsed
    // via LinkedHashSet insertion-order semantics.
    LinkedHashSet seen = new LinkedHashSet();
    DefaultAttribute attr =
        new DefaultAttribute(KEY_CHOICES, referenceClass);
    for (int i = 0; i < choices.length; i++)
    {
      if (seen.add(choices[i]))
        attr.add(choices[i]);
    }
    attributes.put(attr);

    // Store the choice type.
    DefaultAttribute typeAttr =
        new DefaultAttribute(KEY_CHOICE_TYPE, String.class);
    typeAttr.add(choiceType);
    attributes.put(typeAttr);
  }

  /** Removes any choice constraint from this item definition.
   *
   *  <p>After calling this method, both {@link #getChoices()} and
   *  {@link #getChoiceType()} return {@code null}.</p>
   */
  public void clearChoices()
  {
    attributes.remove(KEY_CHOICES);
    attributes.remove(KEY_CHOICE_TYPE);
  }

  // -----------------------------------------------------------------------
  // ItemDefinition — typed getters
  // -----------------------------------------------------------------------

  /** {@inheritDoc} */
  public String getTypeName()
  {
    com.optimasc.attribute.Attribute attr =
        attributes.get(KEY_TYPE_NAME);
    return attr != null && attr.size() > 0
        ? (String) attr.get(0) : null;
  }

  /** {@inheritDoc} */
  public String getValueType()
  {
    com.optimasc.attribute.Attribute attr =
        attributes.get(KEY_VALUE_TYPE);
    return attr != null && attr.size() > 0
        ? (String) attr.get(0) : VALUE_TYPE_SINGLE;
  }

  /** {@inheritDoc} */
  public String getContextType()
  {
    com.optimasc.attribute.Attribute attr =
        attributes.get(KEY_CONTEXT_TYPE);
    return attr != null && attr.size() > 0
        ? (String) attr.get(0) : null;
  }

  /** {@inheritDoc} */
  public AttributeUsage getUsage()
  {
    com.optimasc.attribute.Attribute attr =
        attributes.get(KEY_USAGE);
    if (attr == null || attr.size() == 0)
      return AttributeUsage.userApplication;
    return (AttributeUsage) attr.get(0);
  }

  /** {@inheritDoc} */
  public boolean isSingleValued()
  {
    return VALUE_TYPE_SINGLE.equals(getValueType());
  }

  /** {@inheritDoc} */
  public boolean isOrdered()
  {
    return VALUE_TYPE_SEQ.equals(getValueType());
  }

  /** {@inheritDoc} */
  public boolean isReadOnly()
  {
    com.optimasc.attribute.Attribute attr =
        attributes.get(KEY_READONLY);
    if (attr == null || attr.size() == 0)
      return false;
    Boolean v = (Boolean) attr.get(0);
    return v != null && v.booleanValue();
  }

  /** {@inheritDoc}
   *
   * @return The maximum value length as a positive {@code long}, or
   *         {@code -1} if no length restriction has been set.
   */
  public long getMaxValueLength()
  {
    com.optimasc.attribute.Attribute attr =
        attributes.get(KEY_MAX_VALUE_LENGTH);
    if (attr == null || attr.size() == 0)
      return -1L;
    Long v = (Long) attr.get(0);
    return v != null ? v.longValue() : -1L;
  }

  /** {@inheritDoc}
   *
   *  <p>The returned set is an unmodifiable, insertion-order-preserving
   *  snapshot built from the backing attribute store. The runtime class
   *  of each element matches the class of the first element supplied to
   *  {@link #setChoices(Object[], String)}.</p>
   */
  public Set getChoices()
  {
    com.optimasc.attribute.Attribute attr =
        attributes.get(KEY_CHOICES);
    if (attr == null || attr.size() == 0)
      return null;
    LinkedHashSet result = new LinkedHashSet();
    for (int i = 0; i < attr.size(); i++)
      result.add(attr.get(i));
    return Collections.unmodifiableSet(result);
  }

  /** {@inheritDoc} */
  public String getChoiceType()
  {
    com.optimasc.attribute.Attribute attr =
        attributes.get(KEY_CHOICE_TYPE);
    return attr != null && attr.size() > 0
        ? (String) attr.get(0) : null;
  }

  // -----------------------------------------------------------------------
  // Private helpers
  // -----------------------------------------------------------------------

  /** Validates that {@code oid} is a syntactically correct OID in
   *  dotted-decimal notation.
   *
   * @param oid [in] The OID string to validate. Must not be
   *            {@code null}.
   * @throws IllegalArgumentException if {@code oid} is not a valid
   *   dotted-decimal OID.
   */
  private static void validateOID(String oid)
  {
    if (oid == null || oid.length() == 0)
      throw new IllegalArgumentException(
          "OID must not be null or empty.");
    int start = 0;
    int len   = oid.length();
    while (start <= len)
    {
      int dot = oid.indexOf('.', start);
      int end = (dot == -1) ? len : dot;
      if (end == start)
        throw new IllegalArgumentException(
            "OID '" + oid + "' contains an empty arc.");
      String arc = oid.substring(start, end);
      for (int i = 0; i < arc.length(); i++)
      {
        if (!Character.isDigit(arc.charAt(i)))
          throw new IllegalArgumentException(
              "OID '" + oid + "' contains a non-numeric arc: '"
              + arc + "'.");
      }
      if (arc.length() > 1 && arc.charAt(0) == '0')
        throw new IllegalArgumentException(
            "OID '" + oid
            + "' contains an arc with a leading zero: '"
            + arc + "'.");
      if (dot == -1)
        break;
      start = dot + 1;
    }
  }
}
