package com.optimasc.attribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


/**
 * Mutable, in-memory implementation of {@link Attributes}.
 *
 * <p>Internally uses a {@link HashMap} keyed by expanded name (RDF IRI
 * format: {@code namespaceURI + localName}, or just {@code localName} when
 * no namespace is present) as the primary index. OID-based lookup is
 * performed by iterating the map values, which is acceptable given the
 * typically small number of attributes on any one entity.</p>
 *
 * <p>Case-sensitivity of local-name lookup is fixed at construction time
 * via the {@code caseIgnored} constructor parameter and exposed through
 * {@link #isCaseIgnored()}. When {@code caseIgnored} is {@code false},
 * expanded-name lookup delegates directly to {@link HashMap#get} for O(1)
 * performance. When {@code caseIgnored} is {@code true}, lookup iterates
 * the map values and applies case-insensitive matching on the local-name
 * portion only; the namespace portion is always matched exactly.</p>
 *
 * <p>The {@link #put(Attribute)} method validates that the
 * attribute's namespace URI, when present, ends with {@code '/'} or
 * {@code '#'} as required for unambiguous expanded-name construction and
 * splitting (see {@link Attribute#getNamespaceURI()}).</p>
 *
 * <p>This class is not synchronized against concurrent multithreaded
 * access.</p>
 *
 * @see Attribute
 * @see Attributes
 * @see AttributeModification
 * @author Carl Eric Codere
 */
public class DefaultAttributes implements Attributes
{
  // -----------------------------------------------------------------------
  // Internal storage
  // -----------------------------------------------------------------------

  /** Ordered list of {@link Attribute} instances. */
  protected HashMap<String, Attribute> attributes;

  /** Whether local-name lookup is case-insensitive. */
  protected final boolean caseIgnored;

  // -----------------------------------------------------------------------
  // Constructors
  // -----------------------------------------------------------------------

  /**
   * Constructs an empty attribute set.
   *
   * @param caseIgnored
   *          [in] {@code true} for case-insensitive local-name lookup (LDAP /
   *          X.500 style); {@code false} for exact matching (XML / RDF /
   *          filesystem style).
   */
  public DefaultAttributes(boolean caseIgnored)
  {
    this.caseIgnored = caseIgnored;
    this.attributes = new HashMap<String,Attribute>();
  }

  /**
   * Constructs an empty, case-sensitive attribute set. Equivalent to
   * {@code DefaultAttributes(false)}.
   */
  public DefaultAttributes()
  {
    this(false);
  }

  /**
   * Copy constructor. Creates a new set with the same {@link #isCaseIgnored()}
   * setting and a shallow copy of all attributes from {@code source}.
   *
   * @param source
   *          [in] The set to copy. Must not be {@code null}.
   */
  public DefaultAttributes(Attributes source)
  {
    this(source.isCaseIgnored());
    Iterator<Attribute> it = source.iterator();
    while (it.hasNext())
      put((Attribute) it.next());
  }

  // -----------------------------------------------------------------------
  // Attributes — read operations
  // -----------------------------------------------------------------------

  /**
   * Returns the attribute whose expanded name or OID matches {@code key}, or
   * {@code null} if absent.
   *
   * <p>
   * Expanded-name matching: the namespace portion (everything up to and
   * including the last {@code /} or {@code #} that precedes the local name) is
   * always compared exactly; the local-name portion follows
   * {@link #isCaseIgnored()}. For unnamespaced keys the entire string is the
   * local name and case-sensitivity applies to the whole string.
   * </p>
   *
   * <p>
   * OID matching is always exact.
   * </p>
   */
  public Attribute get(String key)
  {
    Attribute attribute;
    if (key == null)
      throw new IllegalArgumentException("'key' must not be null.");

    // 1. Try expanded-name index first with case sensitivity
    if (caseIgnored == false)
    {
      attribute = attributes.get(key);
      if (attribute != null)
        return attribute;
    }
    // 2. Fall back to OID index (always exact) and search with case ignored search
    Iterator<Attribute> it = attributes.values().iterator();
    while (it.hasNext())
    {
       attribute = it.next();
       if (expandedNameMatches(attribute.getExpandedName(), key))
         return attribute;
       if (key.equals(attribute.getOID()))
         return attribute;
    }
    
    return null;
  }

  public boolean contains(String key)
  {
    return get(key) != null;
  }

  public int size()
  {
    return attributes.size();
  }

  public boolean isEmpty()
  {
    return attributes.isEmpty();
  }

  public boolean isCaseIgnored()
  {
    return caseIgnored;
  }

  public Iterator<Attribute> iterator()
  {
    return attributes.values().iterator();
  }

  public Iterator<String> getExpandedNames()
  {
    return attributes.keySet().iterator();
  }

  // -----------------------------------------------------------------------
  // Mutation — not on the interface
  // -----------------------------------------------------------------------

  /**
   * Adds or replaces an attribute in this set.
   *
   * <p>
   * If an attribute with the same expanded name (subject to
   * {@link #isCaseIgnored()}) already exists it is replaced in-place,
   * preserving insertion order. If the incoming attribute has an OID and an
   * existing attribute is registered under that OID, the old OID registration
   * is removed before the new one is added.
   * </p>
   * 

   *
   * @param attr
   *          [in] The attribute to add or replace. Must not be {@code null}.
   *          {@link Attribute#getLocalName()} must not be {@code null}.
   * @return The previous attribute with the same expanded name, or {@code null}
   *         if none existed.
   *  @throws IllegalArgumentException Thrown if if the namespaceURI of the
   *   attribute is not null and does not end with the '/' or '#' character.
   *         
   */
  public Attribute put(Attribute attr)
  {
    if (attr == null)
      throw new IllegalArgumentException("'attr' must not be null.");
    String ns = attr.getNamespaceURI();
    if (ns != null && ns.length() > 0)
    {
      char last = ns.charAt(ns.length() - 1);
      if (last != '/' && last != '#')
        throw new IllegalArgumentException(
            "Attribute namespace URI must end with '/' or '#': '" + ns + "'.");
    }

    String expandedName = attr.getExpandedName();
    Attribute previous = null;
    
    // 1. Try expanded-name index first with case sensitivity
    if (caseIgnored == false)
    {
      previous = attributes.get(expandedName);
    } else
    // 2. Try expanded-name index first without case sensitivity
    {
      Iterator<Attribute> it = attributes.values().iterator();
      Attribute foundAttribute;
      while (it.hasNext())
      {
         foundAttribute = it.next();
         if (expandedNameMatches((String) foundAttribute.getExpandedName(), expandedName))
         {
           previous = foundAttribute;
           if (previous != null)
             attributes.remove(previous.getExpandedName());
           break;
         }
      }
      
    }
    attributes.put(expandedName,attr);
    return previous;
  }

  /**
   * Removes the attribute with the given expanded name or OID.
   *
   * @param key
   *          [in] Expanded name or OID. Must not be {@code null}.
   * @return The removed attribute, or {@code null} if not found.
   */
  public Attribute remove(String key)
  {
    Attribute removed;
    if (key == null)
      throw new IllegalArgumentException("'key' must not be null.");
    
    // 1. Try expanded-name index first with case sensitivity
    if (caseIgnored == false)
    {
      removed = attributes.remove(key);
      if (removed != null)
        return removed;
    }
    // 2. Fall back to OID index (always exact) and search with case ignored search
    Iterator<Attribute> it = attributes.values().iterator();
    Attribute attribute;
    while (it.hasNext())
    {
       attribute = it.next();
       if (expandedNameMatches((String) attribute.getExpandedName(), key))
       {
         removed = attributes.remove(attribute.getExpandedName());
         if (removed != null)
           return removed;
       }
       if (key.equals(attribute.getOID()))
       {
         removed = attributes.remove(attribute.getExpandedName());
         if (removed != null)
           return removed;
       }
    }
    
    return null;
  }

  /** Removes all attributes from this set. */
  public void clear()
  {
    attributes.clear();
  }

  /**
   * Returns a diff of this set against {@code original} as an array of
   * {@link AttributeModification} instances, suitable for passing directly to
   * methods that need to modify attributes.
   *
   * <p>
   * The diff produces:
   * </p>
   * <ul>
   * <li>{@link AttributeModification#ADD_ATTRIBUTE} for every attribute
   * present in {@code this} but absent in {@code original}.</li>
   * <li>{@link AttributeModification#REMOVE_ATTRIBUTE} for every attribute
   * present in {@code original} but absent in {@code this}.</li>
   * <li>{@link AttributeModification#REPLACE_ATTRIBUTE} for every attribute
   * present in both whose value set has changed.</li>
   * </ul>
   *
   * @param original
   *          [in] The baseline snapshot to diff against. Must not be
   *          {@code null}.
   * @return A non-null, possibly empty array of modifications in the order:
   *         removals, replacements, additions.
   */
  public AttributeModification[] diffAgainst(Attributes original)
  {
    List<AttributeModification> mods = new ArrayList<AttributeModification>();

    // Removals and replacements — iterate original, check this
    Iterator<Attribute> origIt = original.iterator();
    while (origIt.hasNext())
    {
      Attribute origAttr = (Attribute) origIt.next();
      Attribute thisAttr = get(origAttr.getExpandedName());
      if (thisAttr == null)
      {
        mods.add(new AttributeModification(
            AttributeModification.REMOVE_ATTRIBUTE, origAttr));
      } else if (!attributeValuesEqual(origAttr, thisAttr))
      {
        mods.add(new AttributeModification(
            AttributeModification.REPLACE_ATTRIBUTE, thisAttr));
      }
    }

    // Additions — iterate this, check original
    Iterator<Attribute> thisIt = iterator();
    while (thisIt.hasNext())
    {
      Attribute thisAttr = (Attribute) thisIt.next();
      if (original.get(thisAttr.getExpandedName()) == null)
      {
        mods.add(new AttributeModification(
            AttributeModification.ADD_ATTRIBUTE, thisAttr));
      }
    }

    return (AttributeModification[]) mods
        .toArray(new AttributeModification[mods.size()]);
  }

  // -----------------------------------------------------------------------
  // Object overrides
  // -----------------------------------------------------------------------

  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    sb.append("DefaultAttributes[");
    sb.append(attributes.size());
    sb.append("]{");
    Iterator<Attribute> it = iterator();
    boolean first = true;
    while (it.hasNext())
    {
      if (!first)
        sb.append(", ");
      sb.append(it.next().toString());
      first = false;
    }
    sb.append("}");
    return sb.toString();
  }

  // -----------------------------------------------------------------------
  // Private helpers
  // -----------------------------------------------------------------------

  /**
   * Returns {@code true} if {@code stored} (an expanded name already in the
   * index) matches {@code key} (the lookup key) according to the
   * case-sensitivity rules.
   *
   * <p>
   * For namespaced expanded names the namespace portion is split off using the
   * last {@code /} or {@code #} character; only the local-name tail is subject
   * to {@link #caseIgnored}. For unnamespaced names the whole string is treated
   * as the local name.
   * </p>
   */
  private boolean expandedNameMatches(String stored, String key)
  {
    if (!caseIgnored)
      return stored.equals(key);

    // Find the split point between namespace and local name
    // Namespace ends at the last '/' or '#' in the IRI
    int splitStored = namespaceSplitIndex(stored);
    int splitKey = namespaceSplitIndex(key);

    // If both are unnamespaced (no split point) compare whole string
    if (splitStored < 0 && splitKey < 0)
      return stored.equalsIgnoreCase(key);

    // If split points differ, the namespace portions differ in length —
    // they can only match if both namespace substrings are equal (exact)
    // and the local-name substrings match under case rules.
    String nsStored = splitStored >= 0 ? stored.substring(0, splitStored + 1)
        : "";
    String localStored = splitStored >= 0 ? stored.substring(splitStored + 1)
        : stored;
    String nsKey = splitKey >= 0 ? key.substring(0, splitKey + 1) : "";
    String localKey = splitKey >= 0 ? key.substring(splitKey + 1) : key;

    // Namespace portion: always exact
    if (!nsStored.equals(nsKey))
      return false;

    // Local-name portion: case-insensitive when caseIgnored
    return localStored.equalsIgnoreCase(localKey);
  }

  /**
   * Returns the index of the last {@code /} or {@code #} in {@code iri}, which
   * marks the end of the namespace portion, or {@code -1} if neither character
   * is present (unnamespaced name).
   */
  private static int namespaceSplitIndex(String iri)
  {
    int lastSlash = iri.lastIndexOf('/');
    int lastHash = iri.lastIndexOf('#');
    return lastSlash > lastHash ? lastSlash : lastHash;
  }



  /**
   * Returns {@code true} if two attributes have identical value sets. Values
   * are compared using {@link Object#equals}, null-safe, in index order.
   */
  private static boolean attributeValuesEqual(Attribute a,
      Attribute b)
  {
    if (a.size() != b.size())
      return false;
    int size = a.size();
    for (int i = 0; i < size; i++)
    {
      Object va = a.get(i);
      Object vb = b.get(i);
      if (va == null ? vb != null : !va.equals(vb))
        return false;
    }
    return true;
  }
}
