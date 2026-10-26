package com.optimasc.entity;

import java.text.ParseException;

/** Parses and formats {@link EntityName} instances according to
 *  the syntax of a particular {@link Namespace}.
 *
 *  <p>An {@code EntityNameParser} is the authority on the textual
 *  syntax of names within a namespace. It encapsulates everything
 *  that varies between naming systems &mdash; separator
 *  characters, escaping rules, component order, scheme and
 *  authority prefixes, case-folding, Unicode normalization
 *  &mdash; so that the rest of the API can work with
 *  {@link EntityName} as a uniform value type independent of
 *  syntax.</p>
 *
 *  <p>Examples of distinct parser syntaxes:</p>
 *  <ul>
 *    <li><strong>LDAP DN syntax:</strong>
 *        {@code "cn=John Smith,ou=People,dc=example,dc=com"}
 *        &mdash; comma-separated, leaf-first component order,
 *        backslash escaping, RDN attribute-type/value pairs.</li>
 *    <li><strong>POSIX path syntax:</strong>
 *        {@code "/home/alice/letter.txt"} &mdash; slash-separated,
 *        root-first order, no escaping in typical use.</li>
 *    <li><strong>Windows file URI syntax:</strong>
 *        {@code "file://C:/Users/alice/letter.txt"} &mdash;
 *        scheme-prefixed, drive letter as first component,
 *        percent-encoding for reserved characters.</li>
 *    <li><strong>XPath-like syntax:</strong>
 *        {@code "/library/book[3]/title"} &mdash; slash-separated
 *        with bracketed positional predicates.</li>
 *    <li><strong>RIFF chunk-path syntax:</strong>
 *        {@code "RIFF/LIST/INFO/INAM"} &mdash; slash-separated
 *        four-character chunk identifiers.</li>
 *  </ul>
 *
 *  <p>A parser is obtained from its namespace through
 *  {@link Namespace#getNameParser()}. Within a single
 *  namespace every name follows the same syntax, so a single
 *  parser instance is shared across all entities in that
 *  namespace.</p>
 *
 *  <h2>Round-trip contract</h2>
 *
 *  <p>For any {@code EntityName n} produced by this parser:</p>
 *  <pre>
 *  parser.parse(parser.format(n)).equals(n)        // always true
 *  </pre>
 *
 *  <p>For any syntactically valid input string {@code s} accepted
 *  by this parser:</p>
 *  <pre>
 *  parser.format(parser.parse(s))                  // equivalent to s
 *  </pre>
 *
 *  <p>The second round trip is equivalent rather than identical
 *  because the parser may normalize the input during parsing:
 *  collapse redundant whitespace, fold case in LDAP attribute
 *  types, apply Unicode NFC to filesystem path components, or
 *  remove a trailing separator that the syntax treats as
 *  insignificant. Any such normalization is the parser's
 *  responsibility, and the resulting components are exactly the
 *  form that {@link EntityName#equals} will compare.</p>
 *
 *  <h2>Threading</h2>
 *
 *  <p>Implementations are expected to be stateless and safe for
 *  concurrent use by multiple threads without external
 *  synchronization. A namespace may return the same parser
 *  instance to every caller.</p>
 *
 *  @see EntityName
 *  @see Namespace
 *  @see AbstractEntityNameParser
 *  @author Carl Eric Codere
 */
public interface EntityNameParser
{
  /** Parses an external name string into an {@link EntityName}.
   *
   *  <p>The input is interpreted according to the syntax this
   *  parser represents. Scheme, authority, separators, escaping,
   *  and any other syntactic decoration are stripped; the
   *  resulting {@code EntityName} contains only the unescaped
   *  path components in root-first order, regardless of the
   *  component order used by the external syntax. (LDAP, in
   *  particular, writes leaf-first externally but the returned
   *  name is always root-first.)</p>
   *
   *  <p>Any normalization required by the syntax &mdash; case
   *  folding, Unicode normalization, whitespace canonicalization,
   *  removal of insignificant trailing separators &mdash; is
   *  applied during parsing. The resulting components are in the
   *  exact form that {@link EntityName#equals} will compare.</p>
   *
   *  <p>The empty name is returned for inputs that denote the
   *  root of the namespace. Which external strings denote the
   *  root is syntax-dependent: the empty string, a single
   *  separator character, the scheme followed by an empty
   *  authority, and so on.</p>
   *
   * @param nameInNamespace [in] The external name string to
   *   parse. Must not be {@code null}.
   * @return A non-null {@link EntityName} containing the
   *   unescaped, normalized, root-first components of the parsed
   *   name. Returns {@link EntityName#EMPTY} (or an equal
   *   instance) if the input denotes the root of the namespace.
   * @throws IllegalArgumentException if {@code nameInNamespace}
   *   is {@code null}.
   * @throws ParseException if the input is not syntactically
   *   valid for this parser's syntax. The exception's
   *   {@link ParseException#getErrorOffset() errorOffset}
   *   indicates the character position at which parsing failed,
   *   or {@code 0} if no specific position is available.
   */
  EntityName parse(String nameInNamespace) throws ParseException;

  /** Formats an {@link EntityName} into its canonical external
   *  string form.
   *
   *  <p>This is the inverse of {@link #parse}: scheme, authority,
   *  separators, and escaping are re-applied to produce a string
   *  that, when fed back through {@link #parse}, yields a name
   *  equal to the input. Component order is converted from the
   *  internal root-first order to whatever order the external
   *  syntax requires.</p>
   *
   *  <p>The empty name is formatted to the syntax's canonical
   *  representation of the namespace root &mdash; the empty
   *  string, a single separator, a bare scheme, or whatever the
   *  parser considers canonical.</p>
   *
   *  <p>Components that contain characters reserved by the syntax
   *  (separators, escape characters, etc.) are escaped during
   *  formatting. A parser must not produce output that, when
   *  re-parsed, splits a single component into two.</p>
   *
   * @param name [in] The name to format. Must not be {@code null}.
   *   Should be a name previously produced by this parser, or one
   *   constructed with components that satisfy this parser's
   *   syntactic constraints. The behaviour is undefined for names
   *   whose components contain characters this parser cannot
   *   represent.
   * @return The non-null canonical external form of {@code name}.
   * @throws IllegalArgumentException if {@code name} is
   *   {@code null}, or if a component of {@code name} cannot be
   *   represented in this parser's syntax (for example, a
   *   filesystem-path parser asked to format a component
   *   containing the path separator character with no escaping
   *   mechanism available).
   */
  String format(EntityName name);
}
