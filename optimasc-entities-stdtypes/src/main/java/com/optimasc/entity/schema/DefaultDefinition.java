package com.optimasc.entity.schema;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;

import com.optimasc.attribute.Attributes;
import com.optimasc.attribute.DefaultAttribute;
import com.optimasc.attribute.DefaultAttributes;
import com.optimasc.entity.Entity;
import com.optimasc.entity.EntityException;
import com.optimasc.entity.EntityName;
import com.optimasc.entity.Namespace;
import com.optimasc.entity.Syntax;
import com.optimasc.schema.Definition;

/** Default, mutable, in-memory implementation of {@link Definition}.
 *
 *  <p>Attribute storage is backed by a {@link DefaultAttributes}
 *  instance. The typed setters on this class validate their inputs and
 *  store values into that backing store. The typed getters project
 *  values back out of it, so there is only one source of truth.</p>
 *
 *  <p>The {@link com.optimasc.entity.Entity} methods required by the
 *  inherited {@link Definition} contract are implemented as follows:</p>
 *  <ul>
 *    <li>{@link #getDisplayName()} delegates to {@link #getLocalName()}.</li>
 *    <li>{@link #getNameInNamespace()} delegates to
 *        {@link #getExpandedName()}.</li>
 *    <li>{@link #getObjectClass()} reads {@link Definition#KEY_OBJECT_CLASS_NAME}
 *        from the backing store.</li>
 *    <li>{@link #getAttributes(String[])} returns a snapshot of the
 *        backing store, filtered by {@code attrIds} when non-null.</li>
 *    <li>{@link #getChildren()} returns an empty, unmodifiable list —
 *        schema definitions are leaves in the entity tree.</li>
 *    <li>{@link #getChildCountHint()} returns {@code 0}.</li>
 *    <li>{@link #getAllowedChildClasses()} returns an empty array —
 *        no children are permitted beneath a leaf definition.</li>
 *    <li>{@link #getParent()} returns {@code null} — parent tracking
 *        is not managed by this class.</li>
 *    <li>{@link #getLinkTarget()} returns {@code null} — definitions
 *        are never link entities.</li>
 *  </ul>
 *
 *  <h2>Shared definitions</h2>
 *  <p>A definition can be marked as <em>shared</em> with
 *  {@link #markShared()}, or created through {@link #shared(DefaultDefinition)}.
 *  This is meant for the constants of definition catalogs, such as
 *  {@link StandardTypes}, which many schemas use at the same time. A shared
 *  definition:</p>
 *  <ul>
 *    <li>is never owned by a schema: {@link #getNamespace()} always returns
 *        {@link UnboundSchemaNamespace#INSTANCE}, and
 *        {@link #setNamespace(Namespace)} throws an
 *        {@code IllegalStateException};</li>
 *    <li>can be added to any number of schemas with
 *        {@link BasicSchema#add(com.optimasc.schema.Definition)};</li>
 *    <li>cannot be modified: its setters throw an
 *        {@code UnsupportedOperationException}.</li>
 *  </ul>
 *  <p>Marking a definition as shared cannot be undone.</p>
 *
 *  <p>This class is not synchronized against concurrent multithreaded
 *  access, except for the shared flag and the namespace back-reference, which
 *  are visible to all threads once set. A shared definition is never modified,
 *  so it can be used by several threads.</p>
 *
 *  @see Definition
 *  @author Carl Eric Codere
 */
public abstract class DefaultDefinition implements EntityDefinition
{
  // -----------------------------------------------------------------------
  // Internal storage
  // -----------------------------------------------------------------------

  /** Backing attribute store. Never {@code null}. */
  protected final DefaultAttributes attributes;

  /** The namespace that owns this definition.
   *
   *  <p>Defaults to {@link UnboundSchemaNamespace#INSTANCE} for standalone
   *  in-memory definitions. May be replaced via
   *  {@link #setNamespace(Namespace)} once the owning namespace is
   *  known — for example when a definition is attached to a live
   *  {@link Schema#asEntity()} tree backed by a real backend.</p>
   */
  private volatile Namespace namespace = UnboundSchemaNamespace.INSTANCE;

  /** {@code true} once this definition is shared: see {@link #markShared()}. */
  private volatile boolean shared;
  
  // -----------------------------------------------------------------------
  // Constructors
  // -----------------------------------------------------------------------

  /** Constructs a definition with a local name and an auto-generated
   *  identifier.
   *
   *  <p>Defaults applied:</p>
   *  <ul>
   *    <li>{@link Definition#KEY_OBSOLETE}: {@code false}</li>
   *  </ul>
   *
   * @param namespaceURI  [in] Namespace URI of this definition, or
   *                      {@code null} if not associated
   *                      with a namespace. When non-null, must 
   *                      end with {@code '/'} or {@code '#'}.
   * @param localName [in] Name of this definition.
   *                      Must not be {@code null} or empty, and must
   *                      not exceed {@link Definition#NAME_MAX_LENGTH}
   *                      characters.
   * @throws IllegalArgumentException if {@code localName} is
   *   {@code null}, empty, or exceeds {@link Definition#NAME_MAX_LENGTH}
   *   characters, or if {@code namespaceURI} is non-null and does not
   *   end with {@code '/'} or {@code '#'}.
   */
  public DefaultDefinition(String namespaceURI, String localName)
  {
    this.attributes = new DefaultAttributes(false);
    setLocalName(localName);
    setNamespaceURI(namespaceURI);
    setObsolete(false);
  }

  /** Constructs a definition with a local name and an explicit
   *  identifier.
   *
   *  <p>Defaults applied:</p>
   *  <ul>
   *    <li>{@link Definition#KEY_OBSOLETE}: {@code false}</li>
   *  </ul>
   *
   * @param id            [in] Unique identifier for this definition,
   *                      an OBJECT IDENTIFIER in dotted-decimal
   *                      notation (e.g. {@code "2.5.4.3"}). May be
   *                      {@code null}.
   * @param namespaceURI  [in] Namespace URI, or {@code null}. When
   *                      non-null, must end with {@code '/'} or
   *                      {@code '#'}.
   * @param localName [in] local name. Must not be {@code null}
   *                      or empty, and must not exceed
   *                      {@link Definition#NAME_MAX_LENGTH} characters.
   * @throws IllegalArgumentException if any argument violates the
   *   constraints above.
   */
  public DefaultDefinition(String id, String namespaceURI, String localName)
  {
    this(namespaceURI, localName);
    if (id != null) setOID(id);
  }

  /** Constructs a definition with a local name, an explicit
   *  identifier, and a description.
   *
   *  <p>Defaults applied:</p>
   *  <ul>
   *    <li>{@link Definition#KEY_OBSOLETE}: {@code false}</li>
   *  </ul>
   *
   * @param id            [in] Unique identifier. Mayt be {@code null}.
   * @param namespaceURI  [in] Namespace URI, or {@code null}.
   * @param localName [in] Name. Must not be {@code null}
   *                      or empty, and must not exceed
   *                      {@link Definition#NAME_MAX_LENGTH} characters.
   * @param description   [in] Human-readable description, or {@code null}.
   *                      When non-null, must not exceed
   *                      {@link Definition#DESC_MAX_LENGTH} characters.
   * @throws IllegalArgumentException if any argument violates the
   *   constraints above.
   */
  public DefaultDefinition(String id, String namespaceURI, String localName,
      String description)
  {
    this(id, namespaceURI, localName);
    if (description != null)
      setDescription(description);
  }

  // -----------------------------------------------------------------------
  // Shared definitions
  // -----------------------------------------------------------------------

  /** Marks a definition as shared and returns it. This is a convenience for
   *  declaring the constants of a definition catalog:
   *
   *  <pre>
   *  public static final EntityTypeDefinition BOOLEAN =
   *      DefaultDefinition.shared(new DefaultTypeDefinition(...));
   *  </pre>
   *
   * @param definition [in] The definition to mark as shared. Must not be
   *                   {@code null}.
   * @return {@code definition}.
   * @throws IllegalArgumentException if {@code definition} is {@code null}.
   * @throws IllegalStateException if {@code definition} is already owned by
   *   a schema.
   * @see #markShared()
   */
  public static <T extends DefaultDefinition> T shared(T definition)
  {
    if (definition == null)
      throw new IllegalArgumentException("Definition must not be null.");
    definition.markShared();
    return definition;
  }

  /** Marks this definition as shared. A shared definition is owned by no
   *  schema, can be added to any number of schemas, and can no longer be
   *  modified. This cannot be undone.
   *
   *  <p>Marking an already shared definition has no effect.</p>
   *
   * @throws IllegalStateException if this definition is owned by a schema,
   *   that is if {@link #getNamespace()} is not
   *   {@link UnboundSchemaNamespace#INSTANCE}.
   */
  public final void markShared()
  {
    if (namespace != UnboundSchemaNamespace.INSTANCE)
      throw new IllegalStateException("Definition '" + getExpandedName()
          + "' is owned by a schema and cannot be shared.");
    shared = true;
  }

  /** Returns {@code true} if this definition is shared.
   *
   * @return {@code true} if {@link #markShared()} was called.
   */
  public final boolean isShared()
  {
    return shared;
  }

  /** Verifies that this definition can be modified. Every method that
   *  modifies a definition calls this method first.
   *
   * @throws UnsupportedOperationException if this definition is shared.
   */
  protected final void checkModifiable()
  {
    if (shared)
      throw new UnsupportedOperationException("Definition '"
          + getExpandedName() + "' is shared and cannot be modified.");
  }

  // -----------------------------------------------------------------------
  // Definition — typed setters
  // -----------------------------------------------------------------------

  /** Sets the local local name of this definition.
   *
   * @param localName [in] Must not be {@code null} or empty, and
   *                      must not exceed {@link Definition#NAME_MAX_LENGTH}
   *                      characters.
   * @throws IllegalArgumentException if {@code localName} is
   *   {@code null}, empty, or exceeds {@link #NAME_MAX_LENGTH} characters.
   */
  public void setLocalName(String localName)
  {
    checkModifiable();
    if (localName == null || localName.length() == 0)
      throw new IllegalArgumentException("Local name must not be null or empty.");
    if (localName.length() > NAME_MAX_LENGTH)
      throw new IllegalArgumentException(
          "Local name must not exceed " + NAME_MAX_LENGTH
          + " characters, but has " + localName.length() + ".");
    if (localName.indexOf(':') >= 0)
      throw new IllegalArgumentException(
          "Name must be a local name without namespace prefix; "
          + "use setNamespaceURI() for namespace context. Got: '"
          + localName + "'.");
    try
    {
      Syntax.verifyLocalName(localName);
    } catch (ParseException e)
    {
      IllegalArgumentException e1 = new IllegalArgumentException(e.getMessage());
      e1.initCause(e);
      throw e1;
    }
    DefaultAttribute attr =
        new DefaultAttribute(KEY_NAME, String.class);
    attr.add(localName);
    attributes.put(attr);
  }

  /** Sets the namespace URI of this definition.
   *
   * @param namespaceURI [in] Namespace URI, or {@code null} to clear.
   *                     When non-null and non-empty, must end with
   *                     {@code '/'} or {@code '#'}.
   * @throws IllegalArgumentException if {@code namespaceURI} is
   *   non-null, non-empty, and does not end with {@code '/'} or
   *   {@code '#'}.
   */
  public void setNamespaceURI(String namespaceURI)
  {
    checkModifiable();
    if (namespaceURI != null && namespaceURI.length() > 0)
    {
      char last = namespaceURI.charAt(namespaceURI.length() - 1);
      if (last != '/' && last != '#')
        throw new IllegalArgumentException(
            "Namespace URI must end with '/' or '#', but got: '"
            + namespaceURI + "'.");
    }
    if (namespaceURI == null)
    {
      attributes.remove(KEY_NAME_NS_URI);
      return;
    }
    DefaultAttribute attr =
        new DefaultAttribute(KEY_NAME_NS_URI, String.class);
    attr.add(namespaceURI);
    attributes.put(attr);
  }

  /** Sets the unique identifier (OID) of this definition.
   *
   * @param id [in] Must not be {@code null} or empty.
   * @throws IllegalArgumentException if {@code id} is {@code null}
   *   or empty is not a valid dotted notation OBJECT IDENTIFIER.
   */
  public void setOID(String id)
  {
    checkModifiable();
    if (id == null || id.length() == 0)
      throw new IllegalArgumentException(
          "ID must not be null or empty.");
    
    try
    {
      Syntax.verifyObjectIdentifier(id,false);
    } catch (ParseException e)
    {
      IllegalArgumentException e1 = new IllegalArgumentException(e.getMessage());
      e1.initCause(e);
      throw e1;
    }
    
    
    DefaultAttribute attr =
        new DefaultAttribute(KEY_OID, String.class);
    attr.add(id);
    attributes.put(attr);
  }

  /** Sets the human-readable description of this definition.
   *
   * @param description [in] Description string, or {@code null} to
   *                    clear. When non-null, must not exceed
   *                    {@link Definition#DESC_MAX_LENGTH} characters.
   * @throws IllegalArgumentException if {@code description} exceeds
   *   {@link Definition#DESC_MAX_LENGTH} characters.
   */
  public void setDescription(String description)
  {
    checkModifiable();
    if (description != null && description.length() > DESC_MAX_LENGTH)
      throw new IllegalArgumentException(
          "Description must not exceed " + DESC_MAX_LENGTH
          + " characters, but has " + description.length() + ".");
    if (description == null)
    {
      attributes.remove(KEY_DESCRIPTION);
      return;
    }
    DefaultAttribute attr =
        new DefaultAttribute(KEY_DESCRIPTION, String.class);
    attr.add(description);
    attributes.put(attr);
  }

  /** Sets the user-friendly display label of this definition.
   *
   * @param displayName [in] Display label, or {@code null} to clear.
   *                    When absent, {@link #getDisplayName()} falls
   *                    back to {@link #getLocalName()}.
   */
  public void setDisplayName(String displayName)
  {
    checkModifiable();
    if (displayName == null)
    {
      attributes.remove(KEY_DISPLAY_NAME);
      return;
    }
    DefaultAttribute attr =
        new DefaultAttribute(KEY_DISPLAY_NAME, String.class);
    attr.add(displayName);
    attributes.put(attr);
  }

  /** Sets the obsolete flag of this definition.
   *
   * @param obsolete [in] {@code true} if this definition is obsolete
   *                 and should no longer be used in new schema designs.
   */
  public void setObsolete(boolean obsolete)
  {
    checkModifiable();
    DefaultAttribute attr =
        new DefaultAttribute(KEY_OBSOLETE, Boolean.class);
    attr.add(Boolean.valueOf(obsolete));
    attributes.put(attr);
  }

  /** Sets the origin (standard or specification) of this definition.
   *
   * @param origin [in] Origin string (e.g. {@code "RFC 4519"}), or
   *               {@code null} to clear.
   */
  public void setOrigin(String origin)
  {
    checkModifiable();
    if (origin == null)
    {
      attributes.remove(KEY_ORIGIN);
      return;
    }
    DefaultAttribute attr =
        new DefaultAttribute(KEY_ORIGIN, String.class);
    attr.add(origin);
    attributes.put(attr);
  }

  /** Sets the object class name of this definition.
   *
   * @param objectClass [in] Object class name (e.g.
   *                    {@code "AttributeDefinition"}), or {@code null}
   *                    to clear.
   */
  public void setObjectClass(String objectClass)
  {
    checkModifiable();
    if (objectClass == null)
    {
      attributes.remove(KEY_OBJECT_CLASS_NAME);
      return;
    }
    DefaultAttribute attr =
        new DefaultAttribute(KEY_OBJECT_CLASS_NAME, String.class);
    attr.add(objectClass);
    attributes.put(attr);
  }

  // -----------------------------------------------------------------------
  // Definition — typed getters
  // -----------------------------------------------------------------------

  /** {@inheritDoc}
   */
  public String getLocalName()
  {
    com.optimasc.attribute.Attribute attr = attributes.get(KEY_NAME);
    return attr != null ? (String) attr.get(0) : null;
  }

  /** {@inheritDoc} */
  public String getNamespaceURI()
  {
    com.optimasc.attribute.Attribute attr =
        attributes.get(KEY_NAME_NS_URI);
    return attr != null && attr.size() > 0
        ? (String) attr.get(0) : null;
  }

  /** {@inheritDoc} */
  public String getExpandedName()
  {
    String ns    = getNamespaceURI();
    String local = getLocalName();
    return ns != null ? ns + local : local;
  }

  /** {@inheritDoc} */
  public String getOID()
  {
    com.optimasc.attribute.Attribute attr = attributes.get(KEY_OID);
    return attr != null && attr.size() > 0
        ? (String) attr.get(0) : null;
  }

  /** {@inheritDoc} */
  public String getDescription()
  {
    com.optimasc.attribute.Attribute attr =
        attributes.get(KEY_DESCRIPTION);
    return attr != null && attr.size() > 0
        ? (String) attr.get(0) : null;
  }

  /** {@inheritDoc} */
  public boolean isObsolete()
  {
    com.optimasc.attribute.Attribute attr =
        attributes.get(KEY_OBSOLETE);
    if (attr == null || attr.size() == 0)
      return false;
    Boolean v = (Boolean) attr.get(0);
    return v != null && v.booleanValue();
  }

  /** {@inheritDoc} */
  public String getOrigin()
  {
    com.optimasc.attribute.Attribute attr =
        attributes.get(KEY_ORIGIN);
    return attr != null && attr.size() > 0
        ? (String) attr.get(0) : null;
  }

  // -----------------------------------------------------------------------
  // Entity — runtime contract
  // -----------------------------------------------------------------------

  /** Returns the display name of this definition for use in a tree UI.
   *
   *  <p>Returns the value of {@link Definition#KEY_DISPLAY_NAME} when
   *  set; otherwise falls back to {@link #getLocalName()}.</p>
   *
   * @return A non-null display label for this definition.
   */
  public String toString()
  {
    return getDisplayName();
  }

  /** Returns the object class of this definition, or {@code null} if
   *  not set.
   *
   * @return The object class name, or {@code null}.
   */
  public String getObjectClass() throws EntityException
  {
    com.optimasc.attribute.Attribute attr =
        attributes.get(KEY_OBJECT_CLASS_NAME);
    return attr != null && attr.size() > 0
        ? (String) attr.get(0) : null;
  }


  /** Returns the structural name of this definition.
  *
  *  <p>When a namespace URI is set, returns a two-component
  *  {@link EntityName} of the form {@code [namespaceURI, localName]}:
  *  the URI is the structural parent, the local name is the leaf.
  *  When no namespace URI is set, returns a one-component name
  *  {@code [localName]}.</p>
  *
  *  <p>The leaf component is always the local name, matching the
  *  identifier callers use to refer to the definition within its
  *  namespace. The string concatenation of the two components
  *  &mdash; equivalent to {@link #getExpandedName()} &mdash; is
  *  available separately for callers that want the canonical
  *  string form.</p>
  *
  * @return A non-null {@link EntityName} of one or two components.
  */
 public EntityName getNameInNamespace() throws EntityException
 {
   String ns    = getNamespaceURI();
   String local = getLocalName();
   if (ns == null)
     return new EntityName(new String[] { local });
   return new EntityName(new String[] { ns, local });
 }  

  /** Returns a snapshot of this definition's attributes, optionally
   *  filtered to the requested attribute IDs.
   *
   *  <p>The returned set is a shallow copy of the internal backing
   *  store. Mutations to the snapshot do not affect this definition,
   *  and subsequent changes to this definition are not reflected in a
   *  previously-returned snapshot.</p>
   *
   * @param attrIds [in] Expanded names or OIDs of the attributes to
   *                retrieve, or {@code null} to retrieve all attributes.
   * @return A non-null snapshot of this definition's attributes.
   */
  public Attributes getAttributes(String[] attrIds)
      throws EntityException
  {
    DefaultAttributes result = new DefaultAttributes(false);
    if (attrIds == null)
    {
      java.util.Iterator<com.optimasc.attribute.Attribute> it =
          attributes.iterator();
      while (it.hasNext())
        result.put(copyOf(it.next()));
      return result;
    }
    for (int i = 0; i < attrIds.length; i++)
    {
      com.optimasc.attribute.Attribute attr =
          attributes.get(attrIds[i]);
      if (attr != null)
        result.put(copyOf(attr));
    }
    return result;
  }

  /** Returns a copy of an attribute, with the same identity, values, context
   *  keys and default context key, so that modifying the copy does not modify
   *  this definition.
   *
   * @param attr [in] The attribute to copy. Must not be {@code null}.
   * @return A new attribute equal to {@code attr}.
   */
  private static com.optimasc.attribute.Attribute copyOf(
      com.optimasc.attribute.Attribute attr)
  {
    DefaultAttribute copy = new DefaultAttribute(attr.getOID(),
        attr.getNamespaceURI(), attr.getLocalName(), attr.getValueClass(),
        attr.getValueTypeName(), attr.isOrdered(), attr.getContextType());
    int size = attr.size();
    for (int i = 0; i < size; i++)
      copy.add(attr.get(i), attr.getContextKey(i));
    String defaultKey = attr.getDefaultContextKey();
    if (defaultKey != null)
      copy.markDefault(defaultKey);
    return copy;
  }

  /** Returns an empty, unmodifiable list.
   *
   *  <p>Schema definitions are leaves in the entity tree and have no
   *  children.</p>
   *
   * @return An empty, unmodifiable list.
   */
  public List getChildren() throws EntityException
  {
    return Collections.EMPTY_LIST;
  }

  /** Returns {@code 0}.
   *
   *  <p>Schema definitions are leaves and definitively have no
   *  children.</p>
   *
   * @return {@code 0}.
   */
  public int getChildCountHint()
  {
    return 0;
  }

  /** Returns an empty array.
   *
   *  <p>No child classes may be created beneath a schema definition
   *  leaf.</p>
   *
   * @return An empty {@code String} array.
   */
  public String[] getAllowedChildClasses() throws EntityException
  {
    return new String[0];
  }

  /** Returns {@code null}.
   *
   *  <p>Parent tracking in the entity tree is not managed by this
   *  class. A container ({@link Schema}) holds the definitions, not
   *  a parent entity.</p>
   *
   * @return {@code null}.
   */
  public Entity getParent() throws EntityException
  {
    return null;
  }

  /** Returns {@code null}.
   *
   *  <p>Schema definitions are never link entities.</p>
   *
   * @return {@code null}.
   */
  public EntityName getLinkTarget() throws EntityException
  {
    return null;
  }

  // -----------------------------------------------------------------------
  // Object overrides
  // -----------------------------------------------------------------------

  /** Two definitions are equal if and only if they have the same
   *  identifier ({@link #getOID()}, case-insensitive) and the same
   *  expanded name ({@link #getExpandedName()}, case-sensitive).
   */
  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (!(obj instanceof Definition))
      return false;
    Definition other = (Definition) obj;
    String thisID  = getOID();
    String otherID = other.getOID();
    if (thisID == null ? otherID != null
                       : !thisID.equalsIgnoreCase(otherID))
      return false;
    String thisName  = getExpandedName();
    String otherName = other.getExpandedName();
    if (thisName == null ? otherName != null
                         : !thisName.equals(otherName))
      return false;
    return true;
  }

  /** Returns a hash code consistent with {@link #equals(Object)},
   *  based on the lower-cased ID and the expanded name.
   */
  public int hashCode()
  {
    int result = 17;
    String id = getOID();
    result = 31 * result + (id != null ? id.toLowerCase().hashCode() : 0);
    String name = getExpandedName();
    result = 31 * result + (name != null ? name.hashCode() : 0);
    return result;
  }
  
  

  /** {@inheritDoc}
   *
   *  <p>Returns {@link UnboundSchemaNamespace#INSTANCE} by default, and
   *  always for a shared definition. Call {@link #setNamespace(Namespace)}
   *  to associate this definition with a different namespace once the
   *  owning namespace is available.</p>
   *
   * @return The non-null {@link Namespace} that owns this definition.
   *         Never {@code null}.
   */
  public Namespace getNamespace()
  {
    return namespace;
  }

  /** Sets the namespace that owns this definition.
   *
   *  <p>In normal usage this method does not need to be called:
   *  standalone in-memory definitions automatically belong to
   *  {@link UnboundSchemaNamespace#INSTANCE}. Use this setter only when
   *  attaching a definition to a live entity tree backed by a real
   *  backend namespace.</p>
   *
   * @param namespace [in] Must not be {@code null}.
   * @throws IllegalArgumentException if {@code namespace} is
   *   {@code null}.
   * @throws IllegalStateException if this definition is shared: a shared
   *   definition is owned by no namespace.
   */
  public void setNamespace(Namespace namespace)
  {
    if (namespace == null)
      throw new IllegalArgumentException("Namespace must not be null.");
    if (shared)
      throw new IllegalStateException("Definition '" + getExpandedName()
          + "' is shared and cannot be owned by a namespace.");
    this.namespace = namespace;
  }

  @Override
  public String getDisplayName() throws EntityException
  {
    com.optimasc.attribute.Attribute attr =
        attributes.get(KEY_DISPLAY_NAME);
    if (attr != null && attr.size() > 0)
      return (String) attr.get(0);
    return getLocalName();
  }

}
