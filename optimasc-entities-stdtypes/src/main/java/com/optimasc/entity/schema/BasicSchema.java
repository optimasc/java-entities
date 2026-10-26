package com.optimasc.entity.schema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.optimasc.attribute.Attributes;
import com.optimasc.attribute.DefaultAttributes;
import com.optimasc.entity.ContainerEntity;
import com.optimasc.entity.Entity;
import com.optimasc.entity.EntityException;
import com.optimasc.entity.EntityName;
import com.optimasc.entity.EntityNameParser;
import com.optimasc.entity.Namespace;
import com.optimasc.schema.ClassDefinition;
import com.optimasc.schema.Definition;
import com.optimasc.schema.ItemDefinition;

/**
 * Mutable, in-memory implementation of {@link Schema}.
 *
 * <p>
 * Definitions are indexed by both their expanded name and their OID (when
 * present), allowing O(1) lookup by either key. A separate insertion-ordered
 * set of values ensures that {@link #size()} and the typed iterators always
 * reflect the true count of registered definitions rather than the number of
 * index entries.
 * </p>
 *
 * <p>
 * If a definition with the same expanded name or the same OID already exists in
 * the schema, {@link #add(Definition)} refuses the addition and returns
 * {@code false}, preserving the existing entry. Use {@link #remove(Definition)}
 * followed by {@link #add(Definition)} to replace an entry.
 * </p>
 *
 * <h3>Namespace / tree view</h3>
 * <p>
 * This class also implements {@link Schema} (which extends {@link Namespace}),
 * so the schema is browsable as a live entity tree. The tree structure is:
 * </p>
 * 
 * <pre>
 *    (root)                      objectClass = {@link Schema#OBJECT_CLASS_SCHEMA}
 *      &#x251C;&#x2500;&#x2500; AttributeDefinitions/   objectClass = {@link Entity#OBJECT_CLASS_CONTAINER}
 *      &#x2502;     &#x2514;&#x2500;&#x2500; &lt;ItemDefinition leaves&gt;
 *      &#x251C;&#x2500;&#x2500; TypeDefinitions/        objectClass = {@link Entity#OBJECT_CLASS_CONTAINER}
 *      &#x2502;     &#x2514;&#x2500;&#x2500; &lt;TypeDefinition leaves&gt;
 *      &#x2514;&#x2500;&#x2500; ClassDefinitions/       objectClass = {@link Entity#OBJECT_CLASS_CONTAINER}
 *            &#x2514;&#x2500;&#x2500; &lt;ClassDefinition leaves&gt;
 * </pre>
 * <p>
 * The tree is <em>live</em>: definitions added via {@link #add(Definition)} or
 * removed via {@link #remove(Definition)} are immediately visible through
 * {@link ContainerEntity#getChildren()} on the relevant container node. Every
 * definition's {@link Entity#getNamespace()} returns this {@code BasicSchema}
 * instance.
 * </p>
 *
 * <p>
 * {@link #getScheme()} returns {@link Schema#SCHEME}. {@link #getNameParser()},
 * {@link #resolve(String)}, and {@link #resolve(EntityName)} are not yet
 * implemented and throw {@link UnsupportedOperationException}; they will be
 * wired up in a future pass.
 * </p>
 *
 * <p>
 * This class is not synchronized against concurrent multithreaded access.
 * </p>
 *
 * @see Schema
 * @see Definition
 * @author Carl Eric Codere
 */
public class BasicSchema implements Schema
{
  // -----------------------------------------------------------------------
  // Internal storage
  // -----------------------------------------------------------------------

  /**
   * Primary index: expanded name and OID (when present) to Definition. Never
   * {@code null}.
   */
  protected final HashMap<String, Definition> index;

  /**
   * Insertion-ordered set of all registered definitions, used for typed
   * iteration and accurate {@link #size()} reporting.
   */
  protected final Set<Definition> values;

  // -----------------------------------------------------------------------
  // Virtual tree nodes (created once, live views over backing store)
  // -----------------------------------------------------------------------

  /**
   * Root entity of the schema tree. Cached; returned by {@link #getRoot()}.
   */
  private final SchemaRootEntity root;

  /** Container node for {@link ItemDefinition} leaves. */
  private final DefinitionContainerEntity attributeContainer;

  /** Container node for {@link EntityTypeDefinition} leaves. */
  private final DefinitionContainerEntity typeContainer;

  /** Container node for {@link ClassDefinition} leaves. */
  private final DefinitionContainerEntity classContainer;

  // -----------------------------------------------------------------------
  // Constructor
  // -----------------------------------------------------------------------

  /** Constructs an empty schema with an empty virtual tree. */
  public BasicSchema()
  {
    this.index = new HashMap<String, Definition>();
    this.values = new HashSet<Definition>();

    // Build the three container nodes
    EntityName attrName = EntityName.EMPTY
        .append(CONTAINER_ATTRIBUTE_DEFINITIONS);
    EntityName typeName = EntityName.EMPTY.append(CONTAINER_TYPE_DEFINITIONS);
    EntityName className = EntityName.EMPTY.append(CONTAINER_CLASS_DEFINITIONS);

    this.attributeContainer = new DefinitionContainerEntity(this, null,
        attrName, CONTAINER_ATTRIBUTE_DEFINITIONS, ItemDefinition.class);
    this.typeContainer = new DefinitionContainerEntity(this, null, typeName,
        CONTAINER_TYPE_DEFINITIONS, EntityTypeDefinition.class);
    this.classContainer = new DefinitionContainerEntity(this, null, className,
        CONTAINER_CLASS_DEFINITIONS, ClassDefinition.class);

    // Root holds the three containers as its fixed children
    List<Entity> rootChildren = new ArrayList<Entity>();
    rootChildren.add(attributeContainer);
    rootChildren.add(typeContainer);
    rootChildren.add(classContainer);
    this.root = new SchemaRootEntity(this, rootChildren);
  }

  // -----------------------------------------------------------------------
  // Mutation
  // -----------------------------------------------------------------------

  /**
   * Adds a definition to this schema.
   *
   * <p>
   * Both the expanded name and the OID (when non-null) are registered as lookup
   * keys. If a definition with the same expanded name or the same OID already
   * exists, the addition is refused and {@code false} is returned.
   * </p>
   *
   * <p>
   * When the definition is a {@link DefaultDefinition}, its
   * {@link Entity#getNamespace()} is set to this schema so that
   * {@code entity.getNamespace().getCapability(Schema.class)} round-trips back
   * to this instance.
   * </p>
   *
   * @param definition
   *          [in] The definition to add. Must not be {@code null}.
   * @return {@code true} if the definition was added; {@code false} if a
   *         definition with the same expanded name or OID already exists.
   * @throws IllegalArgumentException
   *           if {@code definition} is {@code null}.
   */
  public boolean add(Definition definition)
  {
    if (definition == null)
      throw new IllegalArgumentException("'definition' must not be null.");
    String expandedName = definition.getExpandedName();
    if (index.containsKey(expandedName))
      return false;
    String id = definition.getOID();
    if (id != null && index.containsKey(id))
      return false;
    index.put(expandedName, definition);
    if (id != null)
      index.put(id, definition);
    values.add(definition);
    // Wire the namespace back-reference so the round-trip works.
    if (definition instanceof DefaultDefinition)
      ((DefaultDefinition) definition).setNamespace(this);
    return true;
  }

  /**
   * Removes a definition from this schema.
   *
   * <p>
   * Both the expanded name and the OID index entries are removed. If the
   * definition is a {@link DefaultDefinition} whose namespace is currently this
   * schema, its namespace is reset to the unbound placeholder so it remains
   * non-null.
   * </p>
   *
   * @param definition
   *          [in] The definition to remove. Must not be {@code null}.
   * @return {@code true} if the definition was present and removed;
   *         {@code false} if it was not found.
   */
  public boolean remove(Definition definition)
  {
    if (definition == null)
      throw new IllegalArgumentException("'definition' must not be null.");
    boolean removed = values.remove(definition);
    if (removed)
    {
      index.remove(definition.getExpandedName());
      String id = definition.getOID();
      if (id != null)
        index.remove(id);
      // Reset the namespace back-reference.
      if (definition instanceof DefaultDefinition)
      {
        DefaultDefinition dd = (DefaultDefinition) definition;
        if (dd.getNamespace() == this)
          dd.setNamespace(UnboundSchemaNamespace.INSTANCE);
      }
    }
    return removed;
  }

  /**
   * Removes all definitions from this schema.
   *
   * <p>
   * Resets the namespace back-reference on any removed
   * {@link DefaultDefinition} instances that currently point to this schema.
   * </p>
   */
  public void clear()
  {
    for (Iterator it = values.iterator(); it.hasNext();)
    {
      Object d = it.next();
      if (d instanceof DefaultDefinition)
      {
        DefaultDefinition dd = (DefaultDefinition) d;
        if (dd.getNamespace() == this)
          dd.setNamespace(UnboundSchemaNamespace.INSTANCE);
      }
    }
    index.clear();
    values.clear();
  }

  /**
   * Returns the total number of definitions registered in this schema.
   *
   * @return The definition count. Never negative.
   */
  public int size()
  {
    return values.size();
  }

  /**
   * @return {@code true} if this schema contains no definitions.
   */
  public boolean isEmpty()
  {
    return values.isEmpty();
  }

  // -----------------------------------------------------------------------
  // Schema — item definitions
  // -----------------------------------------------------------------------

  /** {@inheritDoc} */
  public ItemDefinition getItemDefinition(String key)
  {
    Definition d = lookup(key);
    if (d instanceof ItemDefinition)
      return (ItemDefinition) d;
    return null;
  }

  /** {@inheritDoc} */
  public Iterator<ItemDefinition> getItemDefinitions()
  {
    return new TypedIterator<ItemDefinition>(values.iterator(),
        ItemDefinition.class);
  }

  // -----------------------------------------------------------------------
  // Schema — type definitions
  // -----------------------------------------------------------------------

  /** {@inheritDoc} */
  public EntityTypeDefinition getTypeDefinition(String key)
  {
    Definition d = lookup(key);
    if (d instanceof EntityTypeDefinition)
      return (EntityTypeDefinition) d;
    return null;
  }

  /** {@inheritDoc} */
  public Iterator<EntityTypeDefinition> getTypeDefinitions()
  {
    return new TypedIterator<EntityTypeDefinition>(values.iterator(),
        EntityTypeDefinition.class);
  }

  // -----------------------------------------------------------------------
  // Schema — class definitions
  // -----------------------------------------------------------------------

  /** {@inheritDoc} */
  public ClassDefinition getClassDefinition(String key)
  {
    Definition d = lookup(key);
    if (d instanceof ClassDefinition)
      return (ClassDefinition) d;
    return null;
  }

  /** {@inheritDoc} */
  public Iterator<ClassDefinition> getClassDefinitions()
  {
    return new TypedIterator<ClassDefinition>(values.iterator(),
        ClassDefinition.class);
  }

  // -----------------------------------------------------------------------
  // Namespace — scheme and tree access
  // -----------------------------------------------------------------------

  /**
   * {@inheritDoc}
   *
   * @return {@link Schema#SCHEME} ({@value Schema#SCHEME}).
   */
  public String getScheme()
  {
    return SCHEME;
  }

  /**
   * Returns the root entity of the schema tree.
   *
   * <p>
   * The returned entity has object class {@link Schema#OBJECT_CLASS_SCHEMA} and
   * three fixed children: the {@link Schema#CONTAINER_ATTRIBUTE_DEFINITIONS},
   * {@link Schema#CONTAINER_TYPE_DEFINITIONS}, and
   * {@link Schema#CONTAINER_CLASS_DEFINITIONS} container nodes.
   * </p>
   *
   * @return The non-null root {@link Entity}.
   */
  public Entity getRoot() throws EntityException
  {
    return root;
  }

  // -----------------------------------------------------------------------
  // Namespace — capability introspection
  // -----------------------------------------------------------------------

  /**
   * Returns {@code this} when {@code capabilityType} is
   * {@link Schema}{@code .class}; returns {@code null} for all other types.
   *
   * {@inheritDoc}
   */
  public <T> T getCapability(Class<T> capabilityType)
  {
    if (capabilityType != null
        && capabilityType.isAssignableFrom(this.getClass()))
      return capabilityType.cast(this);
    return null;
  }

  /**
   * Returns {@code true} only for {@link com.optimasc.entity.Features#SCHEMA}.
   *
   * {@inheritDoc}
   */
  public boolean hasFeature(String featureName)
  {
    return com.optimasc.entity.Features.SCHEMA.equals(featureName);
  }

  // -----------------------------------------------------------------------
  // Namespace — deferred operations
  // -----------------------------------------------------------------------

  /**
   * Not yet implemented.
   *
   * @throws UnsupportedOperationException
   *           always.
   */
  public EntityNameParser getNameParser()
  {
    throw new UnsupportedOperationException(
        "BasicSchema.getNameParser() is not yet implemented.");
  }

  /**
   * Not yet implemented.
   *
   * @throws UnsupportedOperationException
   *           always.
   */
  public Entity resolve(String nameInNamespace)
      throws java.text.ParseException, EntityException
  {
    throw new UnsupportedOperationException(
        "BasicSchema.resolve(String) is not yet implemented.");
  }

  /**
   * Not yet implemented.
   *
   * @throws UnsupportedOperationException
   *           always.
   */
  public Entity resolve(EntityName name) throws EntityException
  {
    throw new UnsupportedOperationException(
        "BasicSchema.resolve(EntityName) is not yet implemented.");
  }

  // -----------------------------------------------------------------------
  // Closeable
  // -----------------------------------------------------------------------

  /** No-op for this in-memory implementation. */
  public void close()
  {
    // nothing to release
  }

  // -----------------------------------------------------------------------
  // Object overrides
  // -----------------------------------------------------------------------

  /**
   * Returns a string representation listing the number of definitions of each
   * type. Intended for debugging only.
   */
  public String toString()
  {
    int items = 0;
    int types = 0;
    int classes = 0;
    Iterator it = values.iterator();
    while (it.hasNext())
    {
      Object d = it.next();
      if (d instanceof ClassDefinition)
        classes++;
      else if (d instanceof EntityTypeDefinition)
        types++;
      else if (d instanceof ItemDefinition)
        items++;
    }
    return "BasicSchema[items=" + items + ", types=" + types + ", classes="
        + classes + "]";
  }

  // -----------------------------------------------------------------------
  // Private helpers
  // -----------------------------------------------------------------------

  private Definition lookup(String key)
  {
    if (key == null)
      throw new IllegalArgumentException("'key' must not be null.");
    return (Definition) index.get(key);
  }

  // -----------------------------------------------------------------------
  // Inner class — typed filtering iterator
  // -----------------------------------------------------------------------

  private static final class TypedIterator<T> implements Iterator<T>
  {
    private final Iterator<?> source;
    private final Class<T> type;
    private T next;
    private boolean hasNext;

    TypedIterator(Iterator<?> source, Class<T> type)
    {
      this.source = source;
      this.type = type;
      advance();
    }

    private void advance()
    {
      while (source.hasNext())
      {
        Object candidate = source.next();
        if (type.isInstance(candidate))
        {
          next = type.cast(candidate);
          hasNext = true;
          return;
        }
      }
      next = null;
      hasNext = false;
    }

    public boolean hasNext()
    {
      return hasNext;
    }

    public T next()
    {
      if (!hasNext)
        throw new java.util.NoSuchElementException();
      T result = next;
      advance();
      return result;
    }

    public void remove()
    {
      throw new UnsupportedOperationException(
          "Remove via iterator is not supported.");
    }
  }

  // -----------------------------------------------------------------------
  // Inner class — schema root entity
  // -----------------------------------------------------------------------

  /**
   * The root entity of the schema tree. Has three fixed children (the three
   * container nodes) and no parent.
   */
  private static final class SchemaRootEntity implements ContainerEntity
  {
    private final BasicSchema owner;
    private final List<Entity> children;
    private final DefaultAttributes attributes;

    SchemaRootEntity(BasicSchema owner, List<Entity> children)
    {
      this.owner = owner;
      this.children = Collections.unmodifiableList(children);
      this.attributes = new DefaultAttributes(false);
    }

    public String getObjectClass()
    {
      return OBJECT_CLASS_SCHEMA;
    }

    public String getDisplayName()
    {
      return SCHEME;
    }

    public String toString()
    {
      return SCHEME;
    }

    public Attributes getAttributes(String[] attrIds)
    {
      return attributes;
    }

    public EntityName getNameInNamespace()
    {
      return EntityName.EMPTY;
    }

    public Namespace getNamespace()
    {
      return owner;
    }

    public Entity getParent()
    {
      return null;
    }

    public EntityName getLinkTarget()
    {
      return null;
    }

    public List<? extends Entity> getChildren()
    {
      return children;
    }

    public int getChildCountHint()
    {
      return children.size();
    }
  }

  // -----------------------------------------------------------------------
  // Inner class — definition container entity
  // -----------------------------------------------------------------------

  /**
   * A live container node whose children are drawn dynamically from the owning
   * schema's backing store, filtered to one definition type. Adding or removing
   * definitions from the schema is immediately reflected in
   * {@link #getChildren()}.
   */
  private static final class DefinitionContainerEntity
      implements ContainerEntity
  {
    private final BasicSchema owner;
    private final Entity parent;
    private final EntityName nameInNamespace;
    private final String displayName;
    private final Class<?> definitionType;
    private final DefaultAttributes attributes;

    DefinitionContainerEntity(BasicSchema owner, Entity parent,
        EntityName nameInNamespace, String displayName, Class<?> definitionType)
    {
      this.owner = owner;
      this.parent = parent;
      this.nameInNamespace = nameInNamespace;
      this.displayName = displayName;
      this.definitionType = definitionType;
      this.attributes = new DefaultAttributes(false);
    }

    public String getObjectClass()
    {
      return OBJECT_CLASS_CONTAINER;
    }

    public String getDisplayName()
    {
      return displayName;
    }

    public String toString()
    {
      return displayName;
    }

    public Attributes getAttributes(String[] attrIds)
    {
      return attributes;
    }

    public EntityName getNameInNamespace()
    {
      return nameInNamespace;
    }

    public Namespace getNamespace()
    {
      return owner;
    }

    public Entity getParent()
    {
      return parent;
    }

    public EntityName getLinkTarget()
    {
      return null;
    }

    /**
     * Returns a live, unmodifiable snapshot of all definitions of this
     * container's type currently registered in the schema. Each call produces a
     * fresh list; the list itself is immutable but reflects the schema state at
     * call time.
     */
    public List<? extends Entity> getChildren()
    {
      List<Entity> result = new ArrayList<Entity>();
      for (Iterator it = owner.values.iterator(); it.hasNext();)
      {
        Object d = it.next();
        if (definitionType.isInstance(d))
          result.add((Entity) d);
      }
      return Collections.unmodifiableList(result);
    }

    public int getChildCountHint()
    {
      int count = 0;
      for (Iterator it = owner.values.iterator(); it.hasNext();)
      {
        if (definitionType.isInstance(it.next()))
          count++;
      }
      return count;
    }
  }
}
