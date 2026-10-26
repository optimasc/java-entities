package com.optimasc.attribute;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

/**
 * Manages attribute key mappings between attribute formats and the
 * internal hub vocabulary, using a hub-and-spoke model.
 *
 * <h3>Hub-and-spoke model</h3>
 * <p>Each external format (ID3v2, X.500, PREMIS, …) is described by a
 * properties file mapping its own expanded names to internal hub names.
 * The hub specification name is {@link #STANDARD_SPEC}.  Converting between
 * two arbitrary formats is performed by composing two spokes through the
 * hub at runtime — no per-pair mapping files are needed.</p>
 *
 * <h3>Properties file format</h3>
 * <p>Each line in a spoke file has the form:</p>
 * <pre>
 *   externalExpandedName = relationshipChar internalName [ @contextKey=contextValue ]
 * </pre>
 * <p>where {@code relationshipChar} is one of the {@link MappingEntry}
 * {@code RELATIONSHIP_*} constants ({@code =}, {@code >}, {@code <},
 * {@code !}), and the optional {@code @contextKey=contextValue} suffix pins
 * a fixed context on the destination attribute.  Example:</p>
 * <pre>
 *   TPE3==contributor@role=conductor
 *   TCON=!genre
 * </pre>
 *
 * <h3>Thread safety</h3>
 * <p>Instances of this class are <strong>not</strong> thread-safe.  External
 * synchronization is required if an instance is shared across threads.</p>
 *
 * @author Carl Eric Codere
 */
public class AttributeMapper
{
  // -------------------------------------------------------------------------
  // Constants
  // -------------------------------------------------------------------------

  /** The specification name that identifies the internal hub vocabulary.
   *  Passing this as {@code srcSpec} or {@code dstSpec} to
   *  {@link #getMapping(String, String)} short-circuits the three-stage
   *  composition to a single spoke load or inversion. */
  public static final String STANDARD_SPEC = "standard";

  /** Relationship precedence order for weakest-wins composition (ascending). */
  private static final char[] RELATIONSHIP_PRECEDENCE = {
      MappingEntry.RELATIONSHIP_EXACT,
      MappingEntry.RELATIONSHIP_NARROWER,
      MappingEntry.RELATIONSHIP_BROADER,
      MappingEntry.RELATIONSHIP_MANUAL
  };

  // -------------------------------------------------------------------------
  // Inner class — MappingEntry
  // -------------------------------------------------------------------------

  /**
   * Represents a single parsed mapping token from a spoke properties file.
   *
   * <p>The token grammar is:</p>
   * <pre>
   *   token        ::= relationship internalName contextSuffix?
   *   relationship ::= '=' | '&gt;' | '&lt;' | '!'
   *   contextSuffix ::= '@' contextKey '=' contextValue
   * </pre>
   */
  public static class MappingEntry
  {
    /** Exact match — no processing required. */
    public static final char RELATIONSHIP_EXACT    = '=';
    /** Narrower scope than the source attribute. */
    public static final char RELATIONSHIP_NARROWER = '>';
    /** Broader scope than the source attribute. */
    public static final char RELATIONSHIP_BROADER  = '<';
    /** Match but requires manual processing / conversion. */
    public static final char RELATIONSHIP_MANUAL   = '!';

    /** Collection type suffix indicating an unordered bag. */
    public static final String COLLECTION_BAG = "{}";
    /** Collection type suffix indicating an ordered sequence. */
    public static final String COLLECTION_SEQ = "()";

    /** The relationship character between this mapping and the source
     *  attribute. */
    private final char   relationship;

    /**
     * The expanded name of the mapped term (never {@code null}).
     */
    private final String expandedName;

    /**
     * The collection type suffix ({@link #COLLECTION_BAG} or
     * {@link #COLLECTION_SEQ}), or {@code null} if the term is a scalar.
     */
    private final String collectionType;

    /**
     * The pinned context value in {@code key=value} form, or {@code null}
     * if no context is pinned on this mapping entry.
     */
    private final String contextValue;

    /**
     * Constructs a {@code MappingEntry} with all fields supplied explicitly.
     *
     * @param relationship   one of the {@code RELATIONSHIP_*} constants
     * @param expandedName   the expanded name (must not be {@code null})
     * @param collectionType {@link #COLLECTION_BAG}, {@link #COLLECTION_SEQ},
     *                       or {@code null}
     * @param contextValue   pinned context as {@code key=value}, or
     *                       {@code null} if none
     */
    public MappingEntry(char relationship, String expandedName,
        String collectionType, String contextValue)
    {
      this.relationship   = relationship;
      this.expandedName   = expandedName;
      this.collectionType = collectionType;
      this.contextValue   = contextValue;
    }

    /**
     * Returns the relationship character for this mapping entry.
     *
     * @return one of {@link #RELATIONSHIP_EXACT}, {@link #RELATIONSHIP_NARROWER},
     *         {@link #RELATIONSHIP_BROADER}, or {@link #RELATIONSHIP_MANUAL}
     */
    public char getRelationship()
    {
      return relationship;
    }

    /**
     * Returns the expanded name of the mapped term.
     *
     * @return the expanded name (never {@code null})
     */
    public String getExpandedName()
    {
      return expandedName;
    }

    /**
     * Returns the collection type suffix of the mapped term.
     *
     * @return {@link #COLLECTION_BAG}, {@link #COLLECTION_SEQ}, or
     *         {@code null} if scalar
     */
    public String getCollectionType()
    {
      return collectionType;
    }

    /**
     * Returns the pinned context value as a {@code key=value} string, or
     * {@code null} if no context is pinned on this entry.
     *
     * @return the context string or {@code null}
     */
    public String getContextValue()
    {
      return contextValue;
    }

    /** {@inheritDoc} */
    public String toString()
    {
      return "MappingEntry[rel=" + relationship
          + ", name=" + expandedName
          + ", col=" + collectionType
          + ", ctx=" + contextValue + "]";
    }
  }

  // -------------------------------------------------------------------------
  // Instance state
  // -------------------------------------------------------------------------

  /**
   * Cache of composed mappings keyed by {@code "srcSpec-dstSpec"}.
   * Populated on demand by {@link #getMapping(String, String)}.
   */
  private static Hashtable<String, Hashtable<String, MappingEntry>> cache;

  /**
   * Manually registered mappings that bypass spoke-file loading.
   * Keyed by {@code "srcSpec-dstSpec"}.
   */
  private static Hashtable<String, Hashtable<String, MappingEntry>> registered;

  // -------------------------------------------------------------------------
  // Constructor
  // -------------------------------------------------------------------------

  /** Creates a new, empty {@code AttributeMapper} with no registered
   *  mappings and an empty composition cache. */
  static 
  {
    cache      = new Hashtable<String, Hashtable<String, MappingEntry>>();
    registered = new Hashtable<String, Hashtable<String, MappingEntry>>();
    
  }

  // -------------------------------------------------------------------------
  // Public static utilities — pure transformations, no instance state
  // -------------------------------------------------------------------------

  /**
   * Parses a spoke properties stream into a typed mapping table.
   *
   * <p>Each entry in the stream must follow the format:</p>
   * <pre>
   *   externalExpandedName = relationshipChar internalName [ @contextKey=contextValue ]
   * </pre>
   * <p>Lines whose value does not begin with a recognised relationship
   * character ({@code =}, {@code >}, {@code <}, {@code !}) are silently
   * skipped.</p>
   *
   * @param source [in] The {@link InputStream} to read from.  The stream
   *               is read as a standard Java {@link Properties} file
   *               (ISO-8859-1, {@code \} escaping).  The caller is
   *               responsible for closing the stream.  Must not be
   *               {@code null}.
   * @return A non-null {@code Hashtable} mapping each external expanded
   *         name to its parsed {@link MappingEntry}.  Returns an empty
   *         table if the stream contains no valid entries.
   * @throws IOException              if an I/O error occurs reading the
   *                                  stream.
   * @throws IllegalArgumentException if {@code source} is {@code null}.
   */
  public static Hashtable<String, MappingEntry> loadMapping(InputStream source)
      throws IOException
  {
    if (source == null)
      throw new IllegalArgumentException("'source' must not be null.");

    Properties props = new Properties();
    props.load(source);

    Hashtable<String, MappingEntry> result =
        new Hashtable<String, MappingEntry>();

    Enumeration<?> keys = props.propertyNames();
    while (keys.hasMoreElements())
    {
      String externalName = (String) keys.nextElement();
      String raw          = props.getProperty(externalName).trim();

      if (raw.length() == 0)
        continue;

      // --- Parse relationship character -----------------------------------
      char rel = raw.charAt(0);
      if (rel != MappingEntry.RELATIONSHIP_EXACT
          && rel != MappingEntry.RELATIONSHIP_NARROWER
          && rel != MappingEntry.RELATIONSHIP_BROADER
          && rel != MappingEntry.RELATIONSHIP_MANUAL)
        // No recognised relationship prefix — skip silently
        continue;

      String remainder = raw.substring(1);

      // --- Parse optional @contextKey=contextValue suffix -----------------
      String contextValue  = null;
      String nameAndCol    = remainder;
      int atIdx = remainder.indexOf('@');
      if (atIdx >= 0)
      {
        contextValue = remainder.substring(atIdx + 1);
        nameAndCol   = remainder.substring(0, atIdx);
      }

      // --- Parse optional collection-type suffix --------------------------
      String collectionType = null;
      String internalName   = nameAndCol;
      if (nameAndCol.endsWith(MappingEntry.COLLECTION_BAG))
      {
        collectionType = MappingEntry.COLLECTION_BAG;
        internalName   = nameAndCol.substring(
            0, nameAndCol.length() - MappingEntry.COLLECTION_BAG.length());
      }
      else if (nameAndCol.endsWith(MappingEntry.COLLECTION_SEQ))
      {
        collectionType = MappingEntry.COLLECTION_SEQ;
        internalName   = nameAndCol.substring(
            0, nameAndCol.length() - MappingEntry.COLLECTION_SEQ.length());
      }

      if (internalName.length() == 0)
        continue;

      result.put(externalName,
          new MappingEntry(rel, internalName, collectionType, contextValue));
    }
    return result;
  }

  /**
   * Inverts a spoke mapping from {@code external → internal} to
   * {@code internal → external}.
   *
   * <p>This is the destination-side step of the hub-and-spoke composition:
   * given a spoke loaded in its natural forward direction, this method
   * produces the reverse map needed to translate internal names back to the
   * destination format's names.</p>
   *
   * <p>If two external names in {@code spoke} map to the same internal name,
   * only one survives in the inverted map; which one is retained is
   * implementation-defined (determined by {@link Hashtable} enumeration
   * order).</p>
   *
   * @param spoke [in] The forward spoke map ({@code external → internal})
   *              as returned by {@link #loadMapping}.  Must not be
   *              {@code null}.
   * @return A non-null {@code Hashtable} mapping each internal expanded name
   *         to its corresponding {@link MappingEntry} carrying the external
   *         name and the original relationship and context information.
   * @throws IllegalArgumentException if {@code spoke} is {@code null}.
   */
  public static Hashtable<String, MappingEntry> invertMapping(
      Hashtable<String, MappingEntry> spoke)
  {
    if (spoke == null)
      throw new IllegalArgumentException("'spoke' must not be null.");

    Hashtable<String, MappingEntry> result =
        new Hashtable<String, MappingEntry>();

    Enumeration<String> keys = spoke.keys();
    while (keys.hasMoreElements())
    {
      String        externalName = keys.nextElement();
      MappingEntry  entry        = (MappingEntry) spoke.get(externalName);
      // The internal name becomes the key; the external name becomes the
      // destination expanded name in the inverted entry.
      result.put(entry.getExpandedName(),
          new MappingEntry(
              entry.getRelationship(),
              externalName,
              entry.getCollectionType(),
              entry.getContextValue()));
    }
    return result;
  }

  // -------------------------------------------------------------------------
  // Private helpers
  // -------------------------------------------------------------------------

  /**
   * Composes two relationship characters using weakest-wins precedence.
   *
   * <p>Precedence order (weakest last):
   * {@code =} &lt; {@code >} = {@code <} &lt; {@code !}.</p>
   *
   * @param srcRel relationship from the source spoke hop
   * @param dstRel relationship from the destination spoke hop
   * @return the weaker of the two relationship characters
   */
  private static char composeRelationship(char srcRel, char dstRel)
  {
    return (precedenceOf(srcRel) >= precedenceOf(dstRel)) ? srcRel : dstRel;
  }

  /** Returns the precedence rank of a relationship character (higher = weaker). */
  private static int precedenceOf(char rel)
  {
    for (int i = 0; i < RELATIONSHIP_PRECEDENCE.length; i++)
    {
      if (RELATIONSHIP_PRECEDENCE[i] == rel)
        return i;
    }
    // Unknown character treated as weakest
    return RELATIONSHIP_PRECEDENCE.length;
  }

  /**
   * Returns a cache/registry key for the given source/destination pair.
   *
   * @param srcSpec source specification name
   * @param dstSpec destination specification name
   * @return a non-null string key
   */
  private static String cacheKey(String srcSpec, String dstSpec)
  {
    return srcSpec + "-" + dstSpec;
  }

  // -------------------------------------------------------------------------
  // Instance methods — mapping registry and composition
  // -------------------------------------------------------------------------

  /**
   * Registers a pre-built mapping table for the given source/destination
   * pair, bypassing spoke-file loading entirely.
   *
   * <p>If a composed or previously registered mapping already exists for
   * this pair in the cache, it is replaced.</p>
   *
   * @param srcSpec [in] The source specification name.  Must not be
   *                {@code null}.
   * @param dstSpec [in] The destination specification name.  Must not be
   *                {@code null}.
   * @param map     [in] The pre-built mapping table to register.  Must not
   *                be {@code null}.
   * @throws IllegalArgumentException if any argument is {@code null}.
   */
  public static void addMapping(String srcSpec, String dstSpec,
      Hashtable<String, MappingEntry> map)
  {
    if (srcSpec == null)
      throw new IllegalArgumentException("'srcSpec' must not be null.");
    if (dstSpec == null)
      throw new IllegalArgumentException("'dstSpec' must not be null.");
    if (map == null)
      throw new IllegalArgumentException("'map' must not be null.");
    registered.put(cacheKey(srcSpec, dstSpec), map);
    // Invalidate any cached composition for this pair
    cache.remove(cacheKey(srcSpec, dstSpec));
  }

  /**
   * Removes the manually registered mapping for the given source/destination
   * pair.  If no such registration exists, this method is a no-op.
   *
   * @param srcSpec [in] The source specification name.  Must not be
   *                {@code null}.
   * @param dstSpec [in] The destination specification name.  Must not be
   *                {@code null}.
   */
  public static void removeMapping(String srcSpec, String dstSpec)
  {
    if (srcSpec == null || dstSpec == null)
      return;
    registered.remove(cacheKey(srcSpec, dstSpec));
    cache.remove(cacheKey(srcSpec, dstSpec));
  }

  /**
   * Returns the composed mapping table for the given source/destination pair
   * as a {@code Hashtable} from source expanded names to destination
   * {@link MappingEntry} instances.
   *
   * <h3>Resolution order</h3>
   * <ol>
   *   <li>A manually registered mapping (see
   *       {@link #addMapping(String, String, Hashtable)}) takes
   *       priority.</li>
   *   <li>A previously composed and cached result is returned
   *       directly.</li>
   *   <li>Otherwise the mapping is composed on demand via the
   *       hub-and-spoke pipeline and cached for future calls.</li>
   * </ol>
   *
   * <h3>Hub-and-spoke pipeline</h3>
   * <p>Spoke files are loaded from the classpath at the path
   * {@code /res/<spec>.properties} via the context class loader.  The
   * three-stage pipeline is:</p>
   * <ol>
   *   <li><strong>Load source spoke</strong> — {@code srcExpandedName →
   *       internalName}.</li>
   *   <li><strong>Load and invert destination spoke</strong> —
   *       {@code internalName → dstExpandedName}.</li>
   *   <li><strong>Join on internal name</strong> — for each source entry
   *       whose internal name has a counterpart in the inverted destination
   *       spoke, emit {@code srcExpandedName → MappingEntry(dstExpandedName,
   *       composedRelationship, …)}.  The composed relationship follows
   *       weakest-wins precedence
   *       ({@code =} &lt; {@code >} = {@code <} &lt; {@code !}).
   *       The pinned context of the destination hop takes precedence over
   *       that of the source hop when both are non-null.</li>
   * </ol>
   *
   * <h3>Short-circuit cases</h3>
   * <ul>
   *   <li>If {@code srcSpec} equals {@link #STANDARD_SPEC}, only the
   *       destination spoke is loaded and inverted (Stage 2 only).</li>
   *   <li>If {@code dstSpec} equals {@link #STANDARD_SPEC}, only the source
   *       spoke is loaded (Stage 1 only).</li>
   *   <li>If {@code srcSpec} equals {@code dstSpec}, an identity mapping
   *       (every source name maps to itself with
   *       {@link MappingEntry#RELATIONSHIP_EXACT}) is returned.</li>
   * </ul>
   *
   * @param srcSpec [in] The source specification name.  Must not be
   *                {@code null}.
   * @param dstSpec [in] The destination specification name.  Must not be
   *                {@code null}.
   * @return A non-null {@code Hashtable} mapping source expanded names to
   *         destination {@link MappingEntry} instances, or {@code null} if
   *         one or both spoke files could not be located on the classpath.
   * @throws IOException              if an I/O error occurs while reading a
   *                                  spoke file.
   * @throws IllegalArgumentException if {@code srcSpec} or {@code dstSpec}
   *                                  is {@code null}.
   */
  public static Hashtable<String, MappingEntry> getMapping(
      String srcSpec, String dstSpec) throws IOException
  {
    if (srcSpec == null)
      throw new IllegalArgumentException("'srcSpec' must not be null.");
    if (dstSpec == null)
      throw new IllegalArgumentException("'dstSpec' must not be null.");

    String key = cacheKey(srcSpec, dstSpec);

    // --- 1. Manually registered mapping takes priority ------------------
    Hashtable<String, MappingEntry> manual =
        (Hashtable<String, MappingEntry>) registered.get(key);
    if (manual != null)
      return manual;

    // --- 2. Return cached composition if available ----------------------
    Hashtable<String, MappingEntry> cached =
        (Hashtable<String, MappingEntry>) cache.get(key);
    if (cached != null)
      return cached;

    // --- 3. Identity short-circuit --------------------------------------
    // Handled implicitly: if both spokes are the same, the join will
    // produce identity entries naturally. No special case needed.

    Hashtable<String, MappingEntry> result;

    if (srcSpec.equals(dstSpec))
    {
      // Identity mapping — load the single spoke and map each external
      // name to itself with RELATIONSHIP_EXACT.
      Hashtable<String, MappingEntry> spoke = loadSpoke(srcSpec);
      if (spoke == null)
        return null;
      result = new Hashtable<String, MappingEntry>();
      Enumeration<String> keys = spoke.keys();
      while (keys.hasMoreElements())
      {
        String externalName = keys.nextElement();
        result.put(externalName, new MappingEntry(
            MappingEntry.RELATIONSHIP_EXACT, externalName, null, null));
      }
    }
    else if (dstSpec.equals(STANDARD_SPEC))
    {
      // Stage 1 only — source spoke is the answer as-is
      result = loadSpoke(srcSpec);
      if (result == null)
        return null;
    }
    else if (srcSpec.equals(STANDARD_SPEC))
    {
      // Stage 2 only — load and invert the destination spoke
      Hashtable<String, MappingEntry> dstSpoke = loadSpoke(dstSpec);
      if (dstSpoke == null)
        return null;
      result = invertMapping(dstSpoke);
    }
    else
    {
      // Full three-stage composition
      Hashtable<String, MappingEntry> srcSpoke = loadSpoke(srcSpec);
      if (srcSpoke == null)
        return null;
      Hashtable<String, MappingEntry> dstSpoke = loadSpoke(dstSpec);
      if (dstSpoke == null)
        return null;
      Hashtable<String, MappingEntry> invertedDst = invertMapping(dstSpoke);

      result = new Hashtable<String, MappingEntry>();
      Enumeration<String> srcKeys = srcSpoke.keys();
      while (srcKeys.hasMoreElements())
      {
        String       srcExternalName = srcKeys.nextElement();
        MappingEntry srcEntry        =
            (MappingEntry) srcSpoke.get(srcExternalName);
        String       internalName    = srcEntry.getExpandedName();

        // Look up the internal name in the inverted destination spoke
        MappingEntry dstEntry =
            (MappingEntry) invertedDst.get(internalName);
        if (dstEntry == null)
          // No path through the hub for this source attribute — skip it
          continue;

        // Compose relationship: weakest wins
        char composedRel = composeRelationship(
            srcEntry.getRelationship(), dstEntry.getRelationship());

        // Composed collection type: destination wins
        String composedCollection = dstEntry.getCollectionType() != null
            ? dstEntry.getCollectionType()
            : srcEntry.getCollectionType();

        // Composed context: destination pin wins over source pin
        String composedContext = dstEntry.getContextValue() != null
            ? dstEntry.getContextValue()
            : srcEntry.getContextValue();

        result.put(srcExternalName, new MappingEntry(
            composedRel,
            dstEntry.getExpandedName(),
            composedCollection,
            composedContext));
      }
    }

    // Cache the composed result for future calls
    if (result != null)
      cache.put(key, result);

    return result;
  }

  // -------------------------------------------------------------------------
  // Private spoke loader
  // -------------------------------------------------------------------------

  /**
   * Loads a spoke file for the given specification from the classpath at
   * {@code /res/<spec>.properties} and parses it via
   * {@link #loadMapping(InputStream)}.
   *
   * @param spec the specification name (e.g. {@code "ID3V2"})
   * @return the parsed spoke map, or {@code null} if the resource was not
   *         found on the classpath
   * @throws IOException if an I/O error occurs while reading the resource
   */
  private static Hashtable<String, MappingEntry> loadSpoke(String spec)
      throws IOException
  {
    InputStream is = AttributeMapper.class.getResourceAsStream(
        "/res/" + spec + ".properties");
    if (is == null)
      return null;
    try
    {
      return loadMapping(is);
    }
    finally
    {
      try { is.close(); } catch (IOException e) { /* best effort */ }
    }
  }
}