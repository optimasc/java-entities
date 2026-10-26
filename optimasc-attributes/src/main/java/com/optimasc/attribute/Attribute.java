package com.optimasc.attribute;

import java.util.Iterator;

/** A named, multi-valued property.
 *
 *  <p>An attribute carries:</p>
 *  <ul>
 *    <li>An <em>identity</em>: a mandatory local name, an optional namespace
 *        IRI, and an optional Object Identifier (OID) for directory-style
 *        backends.</li>
 *    <li>A <em>type</em>: a mandatory Java value class and an optional
 *        semantic type name (XSD IRI, LDAP syntax OID, etc.).</li>
 *    <li>Zero or more <em>values</em>, accessed positionally via
 *        {@link #get(int)} or as a sequence via {@link #iterator()}.
 *        When {@link #isOrdered()} returns {@code true} the position is
 *        semantically significant; otherwise it is merely the order in
 *        which values happen to be enumerated.</li>
 *    <li>An optional <em>context</em> associated with each value - see
 *        the section below.</li>
 *  </ul>
 *
 *  <h2>Contexts</h2>
 *
 *  <p>A <strong>context</strong> is a secondary label attached to each
 *  individual value that disambiguates it from sibling values carrying the
 *  same attribute name. It answers the question <em>"under what
 *  qualification does this particular value apply?"</em> - for example,
 *  <em>"in which language is this title written?"</em> or <em>"for which
 *  audience does this description apply?"</em></p>
 *
 *  <p>All values of a context-aware attribute share a single
 *  <strong>context type</strong> (returned by {@link #getContextType()}),
 *  which names the qualification dimension - for example {@code "Lang"}
 *  for language tags. Each value then carries its own
 *  <strong>context key</strong> (returned by {@link #getContextKey(int)}),
 *  which is the specific value along that dimension — for example
 *  {@code "en"}, {@code "fr"}, {@code "de"}.</p>
 *
 *  <p>Within a single context-aware attribute, context keys are unique:
 *  there is at most one value per key. One key may be designated as the
 *  default (returned by {@link #getDefaultContextKey()}), to be used when
 *  no specific context is requested.</p>
 *
 *  <p>For example, this model captures language-tagged values uniformly across the
 *  systems that motivate this API:</p>
 *  <ul>
 *    <li><strong>XML / XHTML</strong> - the {@code xml:lang} attribute
 *        on sibling elements (e.g. {@code <title xml:lang="en">…</title>}
 *        and {@code <title xml:lang="fr">…</title>}).</li>
 *    <li><strong>RDF</strong> - language-tagged literals such as
 *        {@code "Hello"@en} and {@code "Bonjour"@fr}.</li>
 *    <li><strong>LDAP / X.500</strong> - attribute language tags
 *        (RFC 3866), e.g. {@code cn;lang-en} and {@code cn;lang-fr}.</li>
 *    <li><strong>UPnP ContentDirectory</strong> - multiple
 *        {@code dc:title} elements distinguished by {@code xml:lang}.</li>
 *  </ul>
 *
 *  <p>The context mechanism is deliberately generic: although language
 *  tagging is the canonical use case, the same machinery accommodates
 *  other per-value qualifications (audience, region, phone type, 
 *  encoding variant, etc.) without introducing new interfaces.</p>
 *
 *  <p>An attribute is <strong>context-aware</strong> when
 *  {@link #getContextType()} returns a non-null value. When it returns
 *  {@code null}, the attribute carries no per-value qualification and
 *  {@link #getContextKey(int)} and {@link #getDefaultContextKey()} also
 *  return {@code null}; {@link #getByContext(String)} returns {@code null}
 *  for every key.</p>
 *
 *  @author Carl Eric Codere
 */
public interface Attribute extends Iterable<Object>
{
  // ---------------------------------------------------------------------
  // Identity
  // ---------------------------------------------------------------------

  /** Returns the Object Identifier of this attribute, or {@code null} if
   *  no OID is associated. */
  String getOID();

  /** Returns the local name of this attribute - the portion of the
   *  expanded name that follows the namespace URI, or the entire name
   *  when no namespace is present. This is the analogue of DOM's
   *  {@code Node.getLocalName()} and {@code QName.getLocalPart()}.
   *
   *  <p>To obtain the full namespace-qualified form, use
   *  {@link #getExpandedName()}, which returns
   *  {@code namespaceURI + localName} in RDF IRI style. Note that this
   *  interface does not model XML-style prefix:localName qualified names;
   *  there is no prefix concept here.</p>
   *
   *  @return The non-null local name.
   */
  String getLocalName();

  /** Returns the namespace IRI associated with this attribute, or
   *  {@code null} if this attribute has no namespace.
   *
   *  <p>The namespace IRI, when present, forms the first part of the
   *  attribute's expanded name. The expanded name is constructed by
   *  simple concatenation: {@code namespaceURI + localName} (see
   *  {@link #getExpandedName()}). To ensure that the expanded name can
   *  always be split back unambiguously into its namespace and local-name
   *  components, the following constraint is enforced:</p>
   *
   *  <p><strong>The namespace IRI must end with either {@code '/'} or
   *  {@code '#'}.</strong></p>
   *
   *  <p>Examples of valid namespace IRIs:</p>
   *  <ul>
   *    <li>{@code "http://purl.org/dc/elements/1.1/"} — Dublin Core,
   *        slash-delimited</li>
   *    <li>{@code "http://www.w3.org/1999/02/22-rdf-syntax-ns#"} — RDF,
   *        hash-delimited</li>
   *    <li>{@code "http://www.w3.org/2001/XMLSchema#"} — XML Schema
   *        datatypes</li>
   *  </ul>
   *
   *  <p>Examples of invalid namespace IRIs (missing trailing delimiter):</p>
   *  <ul>
   *    <li>{@code "http://purl.org/dc/elements/1.1"}</li>
   *    <li>{@code "http://www.w3.org/1999/02/22-rdf-syntax-ns"}</li>
   *  </ul>
   *
   *  <p>This constraint follows the namespace split convention used by
   *  RDF serializers, XML parsers, and XPath processors, where the
   *  last {@code '/'} or {@code '#'} in an IRI marks the boundary
   *  between the namespace and the local name. Implementations that
   *  violate this constraint will produce expanded names that cannot
   *  be reliably decomposed.</p>
   *
   *  <p>An empty string is treated the same as {@code null} — both
   *  indicate the absence of a namespace. Implementations should
   *  return {@code null} rather than an empty string for consistency.</p>
   *
   * @return A non-empty namespace IRI ending with {@code '/'} or
   *         {@code '#'}, or {@code null} if this attribute has no
   *         namespace.
   */
  String getNamespaceURI();

  /** Returns the expanded name of this attribute in RDF IRI form:
   *  {@code getNamespaceURI() + getLocalName()} when a namespace is
   *  present, or simply {@code getLocalName()} otherwise.
   *
   *  <p>Because {@link #getNamespaceURI()} is required to end with
   *  {@code '/'} or {@code '#'}, the expanded name can always be
   *  decomposed back into its components.</p>
   *
   *  @return The non-null expanded name.
   */
  String getExpandedName();

  // ---------------------------------------------------------------------
  // Type
  // ---------------------------------------------------------------------

  /** Returns the Java class representing the type of values in this
   *  attribute. Never {@code null}; implementations should use
   *  {@code Object.class} for untyped attributes. */
  Class<?> getValueClass();

  /** Returns the semantic type name of values in this attribute, or
   *  {@code null} if unknown. Typical forms include XSD datatype IRIs
   *  and LDAP syntax OIDs, e.g.
   *  <ul>
   *    <li>{@code "http://www.w3.org/2001/XMLSchema#unsignedByte"}</li>
   *    <li>{@code "1.3.6.1.4.1.1466.115.121.1.27"} (LDAP Integer syntax)</li>
   *  </ul>
   */
  String getValueTypeName();

  // ---------------------------------------------------------------------
  // Values
  // ---------------------------------------------------------------------

  /** Returns the number of values held by this attribute. */
  int size();

  /** Returns {@code true} if this attribute has no values. */
  boolean isEmpty();

  /** Returns the value at the given index. Position is semantically
   *  significant only when {@link #isOrdered()} returns {@code true};
   *  otherwise it merely reflects the order in which values are
   *  enumerated.
   *
   *  @throws IndexOutOfBoundsException if {@code index} is out of range.
   */
  Object get(int index);

  /** Returns {@code true} if this attribute contains the given value,
   *  using {@link Object#equals} (null-safe). */
  boolean contains(Object value);

  /** Returns {@code true} if the order of this attribute's values is
   *  semantically significant. */
  boolean isOrdered();

  // ---------------------------------------------------------------------
  // Context — see class-level "Contexts" section for the full model
  // ---------------------------------------------------------------------

  /** Returns the context type of this attribute - the name of the
   *  qualification dimension shared by all its values - or {@code null}
   *  if this attribute is not context-aware.
   *
   *  <p>The canonical context type is {@code "Lang"} for language-tagged
   *  values. See the class-level documentation for a full discussion of
   *  the context model.</p>
   *
   *  @return The context type label, or {@code null} when this attribute
   *          carries no per-value qualification.
   */
  String getContextType();

  /** Returns the context key associated with the value at the given
   *  index, or {@code null} when this attribute is not context-aware.
   *
   *  <p>For a language-tagged attribute, this is the language tag of
   *  the value (e.g. {@code "en"}, {@code "fr"}).</p>
   *
   *  @throws IndexOutOfBoundsException if {@code index} is out of range.
   */
  String getContextKey(int index);

  /** Returns the value associated with the given context key, or
   *  {@code null} if no such value is present or this attribute is not
   *  context-aware.
   *
   *  <p>Because context keys are unique within a single attribute, this
   *  lookup is unambiguous.</p>
   */
  Object getByContext(String contextKey);

  /** Returns the default context key for this attribute - the key whose
   *  associated value should be used when no specific context is
   *  requested - or {@code null} if no default is designated or this
   *  attribute is not context-aware.
   *
   *  <p>For a language-tagged attribute this typically corresponds to
   *  the {@code xml:lang} value of the parent document, the LDAP
   *  attribute with no {@code lang-} option, or a similar fallback.</p>
   */
  String getDefaultContextKey();
  
  /** Returns the values of this attribute as a newly allocated typed
   *  array whose component type is determined by {@link #getValueClass()}.
   *
   *  <p>The returned array is a snapshot: subsequent changes to this
   *  attribute do not affect it, and changes to the returned array do
   *  not affect this attribute.</p>
   *
   *  <p>Example:</p>
   *  <pre>
   *    String[] names = (String[]) attr.toArray();
   *  </pre>
   *
   * @return A non-null, possibly empty array of length {@link #size()},
   *         whose component type is {@link #getValueClass()}. Each
   *         element is the value at the corresponding index, in
   *         iteration order.
   */
  Object[] toArray();
  
  /** Returns an iterator over this attribute's values, in index order
   *  (the same order as {@link #get(int)} and {@link #toArray()}). The
   *  iterator does not support {@link Iterator#remove()}; this is a
   *  read-only view. */
  Iterator<Object> iterator();  
}