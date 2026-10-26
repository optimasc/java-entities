package com.optimasc.attribute;

import java.util.Iterator;

/** A read-only, named collection of {@link Attribute} instances.
 *
 *  <p>Attribute identity within this set is based on the
 *  <em>expanded name</em> (RDF IRI format: {@code namespaceURI + localName},
 *  or just {@code localName} when no namespace is present). Expanded names
 *  are always unique within a set. OIDs are optional secondary identifiers;
 *  not every attribute has one.</p>
 *
 *  <p>Whether attribute name lookup is case-sensitive is determined by
 *  {@link #isCaseIgnored()}. When {@code true}, the local-name portion of
 *  an expanded name is matched case-insensitively (as in LDAP, where
 *  {@code CN} and {@code cn} are the same attribute). When {@code false},
 *  matching is exact. OID lookup is always exact regardless of this flag.</p>
 *
 *  <p>Lookup via {@link #get(String)} and {@link #contains(String)} accepts
 *  either an expanded name or an OID as the key.</p>
 *
 *  @author Carl Eric Codere
 */
public interface Attributes extends Iterable<Attribute>
{
  /** Returns the attribute whose expanded name or OID equals {@code key},
   *  or {@code null} if no such attribute exists.
   *
   *  <p>When {@link #isCaseIgnored()} returns {@code true}, the local-name
   *  portion of an expanded name is compared case-insensitively.
   *  The namespace portion of an expanded name, when present, is always
   *  compared exactly (namespace IRIs are case-sensitive by definition).
   *  OID lookup is always exact.</p>
   *
   * @param key [in] An expanded name (e.g.
   *            {@code "http://purl.org/dc/elements/1.1/title"} or
   *            {@code "title"}) or an OID (e.g. {@code "2.5.4.3"}).
   *            Must not be {@code null}.
   * @return The matching {@link Attribute}, or {@code null} if absent.
   */
  Attribute get(String key);

  /** Returns {@code true} if an attribute with the given expanded name
   *  or OID exists in this set.
   *
   *  <p>Case-sensitivity follows {@link #isCaseIgnored()}: when {@code true},
   *  the local-name portion of an expanded name is matched
   *  case-insensitively. The namespace portion and OIDs are always matched
   *  exactly.</p>
   *
   * @param key [in] Expanded name or OID. Must not be {@code null}.
   * @return {@code true} if a matching attribute exists, {@code false}
   *         otherwise.
   */
  boolean contains(String key);

  /** Returns the number of attributes in this set. */
  int size();

  /** Returns {@code true} if this set contains no attributes. */
  boolean isEmpty();

  /** Returns {@code true} if attribute name lookup in this set is
   *  case-insensitive for the local-name portion of expanded names.
   *
   *  <p>When {@code true}, {@link #get(String)} and {@link #contains(String)}
   *  treat the local-name portion of an expanded name as case-insensitive
   *  (e.g. {@code "CN"}, {@code "cn"}, and {@code "Cn"} all resolve to the
   *  same attribute). This matches the behaviour of LDAP / X.500 attribute
   *  sets.</p>
   *
   *  <p>When {@code false}, all name comparisons are exact. This matches
   *  the behaviour of XML attribute sets, RDF property sets, and most
   *  filesystem extended-attribute stores.</p>
   *
   *  <p>The namespace portion of an expanded name and OID lookups are
   *  always exact, regardless of this flag.</p>
   *
   * @return {@code true} if local-name lookup is case-insensitive,
   *         {@code false} if it is case-sensitive.
   */
  boolean isCaseIgnored();

  /** Returns an {@link Iterator} over all {@link Attribute} instances
   *  in this set. Each call returns a fresh, independent iterator.
   *  The iteration order is insertion order.
   */
  Iterator<Attribute> iterator();

  /** Returns an {@link Iterator} over the expanded names of all attributes
   *  in this set, in insertion order.
   *
   *  <p>The expanded name is the primary unique key:
   *  {@code namespaceURI + localName} when a namespace is present,
   *  otherwise just {@code localName}. Every attribute in the set has
   *  an expanded name; this iterator always produces exactly
   *  {@link #size()} elements.</p>
   *
   *  <p>The casing of the returned names reflects the casing used when
   *  the attributes were added, regardless of {@link #isCaseIgnored()}.</p>
   */
  Iterator<String> getExpandedNames();

}
