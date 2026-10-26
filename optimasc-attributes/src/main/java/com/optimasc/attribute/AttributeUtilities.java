package com.optimasc.attribute;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.optimasc.attribute.AttributeMapper.MappingEntry;

public class AttributeUtilities
{
  /**
   * Copies attributes from {@code source} into {@code destination}, with
   * optional overwrite and deletion behaviour.
   *
   * <p>For each attribute in {@code source}:</p>
   * <ul>
   *   <li>If {@code overwrite} is {@code false} and an attribute with the
   *       same expanded name already exists in {@code destination}, it is
   *       left unchanged.</li>
   *   <li>Otherwise the attribute from {@code source} is added or replaces
   *       the existing one in {@code destination}.</li>
   * </ul>
   *
   * <p>If {@code removeDeleted} is {@code true}, any attribute present in
   * {@code destination} but absent in {@code source} is removed from
   * {@code destination} after the copy pass.</p>
   *
   * <p>If {@code source} is {@code null} the method returns immediately
   * without modifying {@code destination}.</p>
   *
   * @param source        [in]  The attribute set to copy from. May be
   *                      {@code null}, in which case the method is a no-op.
   * @param destination   [in,out] The attribute set to copy into. Must not
   *                      be {@code null}.
   * @param overwrite     [in]  {@code true} to replace existing attributes
   *                      in {@code destination}; {@code false} to skip
   *                      attributes that are already present.
   * @param removeDeleted [in]  {@code true} to remove from {@code destination}
   *                      any attribute whose expanded name is absent in
   *                      {@code source}.
   */
  public static void copyAttributes(Attributes source, DefaultAttributes destination,
      boolean overwrite, boolean removeDeleted)
  {
    if (source == null)
      return;

    // First pass — copy attributes from source into destination
    Iterator<Attribute> sourceIt = source.iterator();
    while (sourceIt.hasNext())
    {
      Attribute attr = sourceIt.next();
      String key = attr.getExpandedName();
      if ((overwrite == false) && (destination.get(key) != null))
      {
        // attribute already exists and overwrite is disabled — skip
      }
      else
        // always overwrite entire attribute
      {
        destination.put(attr);
      }
    }

    // Second pass — remove from destination anything absent in source
    if (removeDeleted)
    {
      List<String> toRemove = new ArrayList<String>();
      Iterator<Attribute> destIt = destination.iterator();
      while (destIt.hasNext())
      {
        Attribute destAttr = destIt.next();
        if (source.get(destAttr.getExpandedName()) == null)
        {
          toRemove.add(destAttr.getExpandedName());
        }
      }
      for (int i = 0; i < toRemove.size(); i++)
        destination.remove((String) toRemove.get(i));
    }
  }  
  
  public static Map<String,Object[]> attributesToMap(Attributes source) 
  {
    Map<String,Object[]> dest = new LinkedHashMap<String,Object[]>();
    attributesToMap(source, dest);
    return dest;
  }
  
  
  /**
   * Copies every attribute from {@code source} into {@code destination} as a
   * flat {@code String → Object[]} mapping.
   *
   * <p>Each entry in the destination map has:</p>
   * <ul>
   *   <li><strong>Key</strong> — the attribute's
   *       {@link Attribute#getExpandedName() expanded name}
   *       ({@code namespaceURI + localName}, or just {@code localName} when no
   *       namespace is present).</li>
   *   <li><strong>Value</strong> — a non-null {@code Object[]} snapshot of the
   *       attribute's values in index order, as returned by
   *       {@link Attribute#toArray()}.  The array may be empty when the
   *       attribute exists but carries no values.  The component type of the
   *       array matches {@link Attribute#getValueClass()}.</li>
   * </ul>
   *
   * <p>Context information (context type, context keys, default context key)
   * is intentionally not preserved: this method produces a simple positional
   * snapshot suitable for serialisation, display, or schema validation.  Use
   * the {@link Attribute} API directly when context keys must be retained.</p>
   *
   * <p>If {@code source} is {@code null} the method returns immediately
   * without modifying {@code destination}.  If {@code destination} already
   * contains an entry for an expanded name that is also present in
   * {@code source}, the existing entry is silently replaced.</p>
   *
   * @param source      [in]  The attribute set to read from.  May be
   *                    {@code null}, in which case the method is a no-op.
   * @param destination [in/out] The map to populate.  Must not be
   *                    {@code null}.
   * @throws IllegalArgumentException if {@code destination} is {@code null}.
   */
  public static void attributesToMap(Attributes source, Map<String, Object[]> destination)
  {
    if (destination == null)
      throw new IllegalArgumentException("'destination' must not be null.");
    if (source == null)
      return;

    Iterator<Attribute> it = source.iterator();
    while (it.hasNext())
    {
      Attribute attr = it.next();
      destination.put(attr.getExpandedName(), attr.toArray());
    }
  }  
  
  /**
   * Converts a flat {@code String, Object} map into a case-sensitive
   * {@link Attributes} set.
   *
   * <p>Each map entry is interpreted as follows:</p>
   * <ul>
   *   <li><strong>Key</strong> — used verbatim as the attribute's local name
   *       (or expanded name when the string contains a namespace IRI prefix
   *       ending with {@code '/'} or {@code '#'}).  Must not be {@code null}.</li>
   *   <li><strong>Value is an {@code Object[]}</strong> — each element becomes
   *       one attribute value.  All non-null elements must be instances of the
   *       same class; that class becomes the attribute's
   *       {@linkplain Attribute#getValueClass() value class}.  If every element
   *       is {@code null}, {@code Object.class} is used.  An empty array
   *       produces an attribute with no values whose value class is
   *       {@code Object.class}.</li>
   *   <li><strong>Value is any other {@code Object}</strong> (scalar) — wrapped
   *       into a single-element array automatically; the value's runtime class
   *       becomes the attribute's value class.</li>
   *   <li><strong>Value is {@code null}</strong> — produces an attribute with
   *       no values and value class {@code Object.class}.</li>
   * </ul>
   *
   * <p>No context information (context type, context keys, default context key)
   * is set on any produced attribute; all attributes are non-context-aware.</p>
   *
   * <p>The returned {@link Attributes} instance is a freshly created,
   * case-sensitive {@link DefaultAttributes}.  It is independent of
   * {@code source}: subsequent changes to the map do not affect the returned
   * set.</p>
   *
   * @param source [in] The map to convert.  May be {@code null}, in which case
   *               an empty {@link Attributes} set is returned.
   * @return A non-null {@link Attributes} set containing one attribute per
   *         map entry, in map iteration order.
   * @throws IllegalArgumentException if any map key is {@code null}, if an
   *         {@code Object[]} value contains elements of more than one non-null
   *         class (mixed-type array), or if any key contains an invalid
   *         namespace URI (as enforced by {@link DefaultAttribute}).  
   */
  public static Attributes mapToAttributes(Map<String, Object> source)
  {
    DefaultAttributes result = new DefaultAttributes(false);
    if (source == null)
      return result;

    Iterator<Map.Entry<String, Object>> it = source.entrySet().iterator();
    while (it.hasNext())
    {
      Map.Entry<String, Object> entry = (Map.Entry<String, Object>) it.next();
      String key = entry.getKey();
      if (key == null)
        throw new IllegalArgumentException(
            "Map contains a null key; attribute names must not be null.");

      Object raw = entry.getValue();

      // --- Normalise to Object[] -----------------------------------------
      Object[] values;
      if (raw == null)
      {
        values = new Object[0];
      }
      else if (raw instanceof Object[])
      {
        values = (Object[]) raw;
      }
      else
      {
        // scalar — wrap it
        values = new Object[] { raw };
      }

      // --- Determine value class and validate homogeneity -----------------
      Class<?> valueClass = Object.class;
      for (int i = 0; i < values.length; i++)
      {
        Object v = values[i];
        if (v == null)
          continue;
        if (valueClass == Object.class)
        {
          valueClass = v.getClass();
        }
        else if (!valueClass.equals(v.getClass()))
        {
          throw new IllegalArgumentException(
              "Array value for key '" + key + "' contains mixed types: '"
              + valueClass.getName() + "' and '" + v.getClass().getName()
              + "' (all non-null elements must be the same class).");
        }
      }

      // --- Build the attribute --------------------------------------------
      DefaultAttribute attr = new DefaultAttribute(key, valueClass);
      for (int i = 0; i < values.length; i++)
      {
        attr.add(values[i]);
      }

      result.put(attr);
    }

    return result;
  }  

/**
 * Remaps attributes from {@code source} into {@code destination} using an
 * optional key-translation map, with overwrite and deletion control.
 *
 * <h3>Key translation</h3>
 * <p>When {@code keyMapping} is non-null each source attribute whose
 * expanded name appears as a key in the map is translated to the destination
 * expanded name carried by the corresponding {@link MappingEntry}.  Source
 * attributes whose expanded name has no entry in {@code keyMapping} are
 * silently skipped.  When {@code keyMapping} is {@code null} no translation
 * is performed and every source attribute is copied verbatim, using its own
 * expanded name as the destination key (equivalent to
 * {@link AttributeUtilities#copyAttributes}).</p>
 *
 * <h3>Relationship handling</h3>
 * <p>The {@link MappingEntry#getRelationship()} character controls whether a
 * mapped entry is copied:</p>
 * <ul>
 *   <li>{@link MappingEntry#RELATIONSHIP_EXACT} ({@code =}),
 *       {@link MappingEntry#RELATIONSHIP_NARROWER} ({@code >}), and
 *       {@link MappingEntry#RELATIONSHIP_BROADER} ({@code <}) — the source
 *       attribute is copied to the destination key.  The relationship
 *       character is informational only; no value transformation is
 *       applied.</li>
 *   <li>{@link MappingEntry#RELATIONSHIP_MANUAL} ({@code !}) — the entry
 *       requires manual processing and is skipped entirely.  The
 *       corresponding destination attribute is neither written nor
 *       removed.</li>
 * </ul>
 *
 * <h3>Collection-type and scalar narrowing</h3>
 * <p>The {@link MappingEntry#getCollectionType()} of the destination entry
 * determines how multi-valued source attributes are handled:</p>
 * <ul>
 *   <li>If the destination entry has a non-null collection type, or if the
 *       source attribute is not context-aware, all values are copied
 *       as-is.</li>
 *   <li>If the destination entry collection type is {@code null} (scalar)
 *       but the source attribute is context-aware (multi-valued by context
 *       key), only a single value is extracted using the following
 *       precedence:
 *       <ol>
 *         <li>The value whose context key equals
 *             {@link Attribute#getDefaultContextKey()}, when a default
 *             is designated.</li>
 *         <li>Otherwise {@link Attribute#get(int) get(0)}, regardless of
 *             whether the source attribute is ordered.  For ordered
 *             attributes this is the positionally first value; for
 *             unordered attributes this is the first value returned by
 *             the implementation, which is implementation-defined and
 *             may vary between calls.</li>
 *         <li>If the source attribute is empty, it is skipped entirely —
 *             no destination attribute is written.</li>
 *       </ol>
 *   </li>
 * </ul>
 *
 * <h3>Overwrite semantics</h3>
 * <p>Overwrite is evaluated per attribute (not per context-key).  When
 * {@code overwrite} is {@code false} and an attribute with the destination
 * expanded name already exists in {@code destination}, that attribute is
 * left completely unchanged.  When {@code overwrite} is {@code true} the
 * destination attribute is replaced in full by the incoming attribute.</p>
 *
 * <h3>Deletion semantics</h3>
 * <p>When {@code removeDeleted} is {@code true}, after the copy pass any
 * attribute in {@code destination} whose expanded name was not produced by
 * this call (i.e. it has no corresponding mapped source attribute, or its
 * mapping entry has relationship {@link MappingEntry#RELATIONSHIP_MANUAL})
 * is removed.  Because this removes <em>everything</em> not contributed by
 * this single source, {@code removeDeleted} should only be set to
 * {@code true} when {@code destination} is populated from a single source.
 * Using it in a multi-source accumulation scenario will silently delete
 * attributes contributed by other sources.</p>
 *
 * <p>If {@code source} is {@code null} the method returns immediately
 * without modifying {@code destination}.</p>
 *
 * @param source        [in]     The attribute set to read from.  May be
 *                               {@code null}, in which case the method is a
 *                               no-op.
 * @param destination   [in/out] The attribute set to write into.  Must not
 *                               be {@code null}.
 * @param keyMapping    [in]     A map whose keys are source expanded names
 *                               and whose values are {@link MappingEntry}
 *                               instances describing the destination expanded
 *                               name and relationship.  {@code null} means no
 *                               remapping: all source attributes are copied
 *                               verbatim.
 * @param overwrite     [in]     {@code true} to replace an existing
 *                               destination attribute; {@code false} to leave
 *                               it unchanged on collision.
 * @param removeDeleted [in]     {@code true} to remove from
 *                               {@code destination} any attribute not
 *                               produced by this call.  Safe only when
 *                               {@code destination} is fed from a single
 *                               source.
 * @return The {@code destination} set, to allow chained calls.
 * @throws IllegalArgumentException if {@code destination} is {@code null}.
 */
public static DefaultAttributes remapAttributes(
    Attributes source,
    DefaultAttributes destination,
    Map<String, MappingEntry> keyMapping,
    boolean overwrite,
    boolean removeDeleted)
    throws IllegalArgumentException
{
  if (destination == null)
    throw new IllegalArgumentException("'destination' must not be null.");
  if (source == null)
    return destination;

  // Track destination expanded names written in this call for removeDeleted.
  List<String> written = removeDeleted ? new ArrayList<String>() : null;

  Iterator<Attribute> it = source.iterator();
  while (it.hasNext())
  {
    Attribute srcAttr = it.next();
    String srcKey = srcAttr.getExpandedName();

    // --- Resolve destination key, relationship and collectionType -------
    String destKey;
    String collectionType = null;
    if (keyMapping == null)
    {
      // Plain copy — no remapping, no collection-type narrowing
      destKey = srcKey;
    }
    else
    {
      MappingEntry entry = (MappingEntry) keyMapping.get(srcKey);
      // No mapping entry for this source attribute — skip it
      if (entry == null)
        continue;
      // Manual relationship — skip, preserve any existing destination value
      if (entry.getRelationship() == MappingEntry.RELATIONSHIP_MANUAL)
        continue;
      destKey        = entry.getExpandedName();
      collectionType = entry.getCollectionType();
    }

    // --- Apply overwrite rule -------------------------------------------
    if ((overwrite == false) && (destination.get(destKey) != null))
    {
      if (written != null)
        written.add(destKey);
      continue;
    }

    // --- Build destination attribute ------------------------------------
    DefaultAttribute destAttr = new DefaultAttribute(
        null,
        null,
        destKey,
        srcAttr.getValueClass(),
        null,
        srcAttr.isOrdered(),
        srcAttr.getContextType()
        );

    // --- Copy values, applying scalar narrowing if required -------------
    boolean scalarDest = (collectionType == null);
    boolean contextAware = (srcAttr.getContextType() != null);

    if (scalarDest && contextAware)
    {
      // Destination is scalar but source is context-aware: extract one value
      if (srcAttr.isEmpty())
        // Nothing to copy — skip this attribute entirely
        continue;

      Object singleValue;
      String defaultKey = srcAttr.getDefaultContextKey();
      if (defaultKey != null)
      {
        // Prefer the value designated as default
        singleValue = srcAttr.getByContext(defaultKey);
      }
      else
      {
        // No default designated — fall back to index 0 (implementation-defined
        // for unordered attributes; positionally meaningful for ordered ones)
        singleValue = srcAttr.get(0);
      }
      destAttr.add(singleValue);
    }
    else
    {
      // Straight copy — preserve all values and context keys
      int size = srcAttr.size();
      for (int i = 0; i < size; i++)
        destAttr.add(srcAttr.get(i), srcAttr.getContextKey(i));
    }

    destination.put(destAttr);
    if (written != null)
      written.add(destKey);
  }

  // --- removeDeleted pass ---------------------------------------------
  if (removeDeleted)
  {
    // Collect keys first to avoid ConcurrentModificationException
    List<String> toRemove = new ArrayList<String>();
    Iterator<Attribute> destIt = destination.iterator();
    while (destIt.hasNext())
    {
      Attribute destAttr = destIt.next();
      if (!written.contains(destAttr.getExpandedName()))
        toRemove.add(destAttr.getExpandedName());
    }
    for (int i = 0; i < toRemove.size(); i++)
      destination.remove((String) toRemove.get(i));
  }

  return destination;
}}
