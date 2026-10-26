package com.optimasc.entity.parser;

/** Immutable configuration for {@link DefaultEntityNameParser}.
 *
 *  <p>An {@code EntityNameSyntax} bundles every knob the default,
 *  property-driven parser supports: parsing direction, separator
 *  character, optional escape character, optional literal prefix,
 *  case-folding, and leading-separator handling. Instances are
 *  immutable, safe for concurrent use, and constructed via the
 *  fluent {@link Builder} returned from {@link #builder()}.</p>
 *
 *  <p>Convenience constants are provided for the most common
 *  syntaxes; for anything else, build one with
 *  {@link #builder()}.</p>
 *
 *  <h2>Direction</h2>
 *
 *  <p>The {@link #getDirection() direction} controls how external
 *  components are mapped to the internal root-first order used by
 *  {@link com.optimasc.entity.EntityName}:</p>
 *  <ul>
 *    <li>{@link Direction#LEFT_TO_RIGHT} &mdash; the first
 *        separator-delimited token in the input is the root-most
 *        component. Used by filesystems, URIs, RIFF, DOM XPath.</li>
 *    <li>{@link Direction#RIGHT_TO_LEFT} &mdash; the first
 *        separator-delimited token is the leaf-most component;
 *        the parser reverses on parse and reverses on format.
 *        Used by LDAP DNs.</li>
 *    <li>{@link Direction#FLAT} &mdash; no separator processing
 *        is performed; the entire prefix-stripped input is a
 *        single component. Used by URN-style and other
 *        opaque-identifier namespaces.</li>
 *  </ul>
 *
 *  <h2>Prefix</h2>
 *
 *  <p>The {@link #getPrefix() prefix}, if non-null, is a literal
 *  string that the parser strips from the start of the input
 *  before splitting, and re-prepends when formatting. The parser
 *  does no interpretation of what the prefix means: it does not
 *  validate URI scheme syntax, does not parse authority
 *  components, does not perform percent-decoding. The prefix is
 *  simply a fixed leading string that must be present.</p>
 *
 *  <p>Typical uses include URI schemes, namespace tags, and any
 *  other fixed leading-string convention:</p>
 *  <ul>
 *    <li>{@code prefix = "file:///"} parses
 *        {@code "file:///home/alice"} to components
 *        {@code ["home", "alice"]} and
 *        {@code "file:///C:/Users/alice"} to components
 *        {@code ["C:", "Users", "alice"]}.</li>
 *    <li>{@code prefix = "http://"} parses
 *        {@code "http://example.com/a/b"} to components
 *        {@code ["example.com", "a", "b"]}.</li>
 *    <li>{@code prefix = "urn:isbn:"} with
 *        {@link Direction#FLAT} parses
 *        {@code "urn:isbn:0451450523"} to the single-component
 *        name {@code ["0451450523"]}.</li>
 *    <li>{@code prefix = "dn:"} could be used by an LDAP
 *        adapter whose name-in-namespace strings are written as
 *        {@code "dn:cn=alice,..."}.</li>
 *  </ul>
 *
 *  <p>For full URI parsing (authority, query, fragment,
 *  percent-encoding), use {@link java.net.URI} and build a
 *  custom {@link com.optimasc.entity.EntityNameParser} on top
 *  of it.</p>
 *
 *  @see DefaultEntityNameParser
 *  @author Carl Eric Codere
 */
public final class EntityNameSyntax
{
  // -----------------------------------------------------------------------
  // Direction nested type
  // -----------------------------------------------------------------------

  /** Parsing-direction discriminator. Implemented as a class with
   *  three named constants rather than a Java 5 {@code enum} for
   *  toolchain compatibility. Instances are compared by identity. */
  public static final class Direction
  {
    /** First token in the external string is the root-most
     *  component. */
    public static final Direction LEFT_TO_RIGHT =
        new Direction("LEFT_TO_RIGHT");

    /** First token in the external string is the leaf-most
     *  component. */
    public static final Direction RIGHT_TO_LEFT =
        new Direction("RIGHT_TO_LEFT");

    /** No separator processing; the entire prefix-stripped input
     *  is a single component. */
    public static final Direction FLAT =
        new Direction("FLAT");

    private final String label;
    private Direction(String label) { this.label = label; }
    public String toString() { return label; }
  }

  // -----------------------------------------------------------------------
  // Preset constants
  // -----------------------------------------------------------------------

  /** POSIX filesystem path syntax: separator {@code '/'}, no
   *  escape, no prefix, case-sensitive. Parses
   *  {@code "/home/alice"} to components
   *  {@code ["home", "alice"]} and the empty string or
   *  {@code "/"} to {@link com.optimasc.entity.EntityName#EMPTY}. */
  public static final EntityNameSyntax POSIX_PATH = builder()
      .direction(Direction.LEFT_TO_RIGHT)
      .separator('/')
      .build();

  /** LDAP DN syntax: separator {@code ','}, escape {@code '\\'},
   *  right-to-left direction, case-insensitive component
   *  matching. Parses
   *  {@code "cn=alice,ou=people,dc=example,dc=com"} to components
   *  {@code ["dc=com", "dc=example", "ou=people", "cn=alice"]}.
   *
   *  <p>The parser does not validate RFC 4514 DN syntax beyond
   *  separator splitting; in particular, a leading comma in the
   *  external input is accepted (producing a trailing empty
   *  component after reversal) rather than rejected. Adapters
   *  requiring strict DN validation should pre-validate input
   *  before parsing, or use a dedicated LDAP DN parser.</p> */
  public static final EntityNameSyntax LDAP_DN = builder()
      .direction(Direction.RIGHT_TO_LEFT)
      .separator(',')
      .escape('\\')
      .ignoreCase(true)
      .build();

  /** DOM-style XPath absolute-path syntax: separator {@code '/'},
   *  leading separator required (rooted absolute paths only),
   *  case-sensitive. {@code "/"} parses to
   *  {@link com.optimasc.entity.EntityName#EMPTY}; bare
   *  {@code "a/b"} (without leading slash) is rejected as
   *  syntactically invalid. */
  public static final EntityNameSyntax DOM_XPATH = builder()
      .direction(Direction.LEFT_TO_RIGHT)
      .separator('/')
      .requireLeadingSeparator(true)
      .build();

  /** Flat opaque-identifier syntax: no separator processing, no
   *  prefix, no escape, case-sensitive. The entire input is a
   *  single component. The empty string parses to
   *  {@link com.optimasc.entity.EntityName#EMPTY}. */
  public static final EntityNameSyntax FLAT = builder()
      .direction(Direction.FLAT)
      .build();

  /** {@code file://} URI syntax in its RFC 8089 canonical
   *  three-slash form: prefix {@code "file:///"}, separator
   *  {@code '/'}, no escape, case-sensitive, left-to-right.
   *  Parses {@code "file:///home/alice"} to components
   *  {@code ["home", "alice"]} and
   *  {@code "file:///C:/Users/alice"} to components
   *  {@code ["C:", "Users", "alice"]}.
   *
   *  <p>This preset rejects nonstandard two-slash forms
   *  ({@code "file://path"}) and remote-host forms
   *  ({@code "file://host/share"}); users who need these should
   *  build a custom syntax with the appropriate prefix.</p> */
  public static final EntityNameSyntax FILE = builder()
      .direction(Direction.LEFT_TO_RIGHT)
      .separator('/')
      .prefix("file:///")
      .build();

  /** {@code http://} URI syntax: prefix {@code "http://"},
   *  separator {@code '/'}, no escape, case-sensitive,
   *  left-to-right. Parses {@code "http://example.com/a/b"} to
   *  components {@code ["example.com", "a", "b"]} &mdash; the
   *  authority is treated as the first path component, with no
   *  special interpretation. */
  public static final EntityNameSyntax HTTP = builder()
      .direction(Direction.LEFT_TO_RIGHT)
      .separator('/')
      .prefix("http://")
      .build();

  // -----------------------------------------------------------------------
  // Fields
  // -----------------------------------------------------------------------

  private final Direction direction;
  private final char      separator;
  private final char      escape;            // 0 means none
  private final String    prefix;            // null means none
  private final boolean   ignoreCase;
  private final boolean   requireLeadingSeparator;

  // -----------------------------------------------------------------------
  // Constructor (private — use the builder)
  // -----------------------------------------------------------------------

  private EntityNameSyntax(Builder b)
  {
    this.direction               = b.direction;
    this.separator               = b.separator;
    this.escape                  = b.escape;
    this.prefix                  = b.prefix;
    this.ignoreCase              = b.ignoreCase;
    this.requireLeadingSeparator = b.requireLeadingSeparator;
  }

  // -----------------------------------------------------------------------
  // Accessors
  // -----------------------------------------------------------------------

  /** Returns the parsing direction. Never {@code null}. */
  public Direction getDirection() { return direction; }

  /** Returns the separator character. Significant only when
   *  {@link #getDirection()} is {@link Direction#LEFT_TO_RIGHT}
   *  or {@link Direction#RIGHT_TO_LEFT}; ignored in
   *  {@link Direction#FLAT}. */
  public char getSeparator() { return separator; }

  /** Returns the escape character, or {@code 0} (the NUL
   *  character) if no escape mechanism is configured. When set,
   *  {@code escape + any-char} in the external string represents
   *  a literal occurrence of {@code any-char} within a component
   *  (allowing the separator itself to appear in a component
   *  value); during formatting, the parser escapes the separator
   *  and the escape character. */
  public char getEscape() { return escape; }

  /** Returns the literal prefix string to strip on parse and
   *  prepend on format, or {@code null} if no prefix is
   *  configured. The prefix is treated as a fixed leading string
   *  with no semantic interpretation. */
  public String getPrefix() { return prefix; }

  /** Returns {@code true} if component matching is
   *  case-insensitive. When set, the parser lowercases each
   *  component during {@code parse} so that
   *  {@link com.optimasc.entity.EntityName} equality remains
   *  simple string equality. */
  public boolean isIgnoreCase() { return ignoreCase; }

  /** Returns {@code true} if a leading separator character is
   *  required (after any configured prefix). When set, inputs
   *  that do not begin with the separator are rejected as
   *  syntactically invalid, and {@code format(EntityName.EMPTY)}
   *  emits the separator (preceded by the prefix, if configured)
   *  rather than the empty string &mdash; so that the root
   *  round-trips correctly. */
  public boolean isRequireLeadingSeparator()
  {
    return requireLeadingSeparator;
  }

  // -----------------------------------------------------------------------
  // Builder
  // -----------------------------------------------------------------------

  /** Returns a new {@link Builder} with default settings:
   *  {@link Direction#LEFT_TO_RIGHT}, separator {@code '/'}, no
   *  escape, no prefix, case-sensitive, leading separator not
   *  required. */
  public static Builder builder()
  {
    return new Builder();
  }

  /** Fluent builder for {@link EntityNameSyntax}. */
  public static final class Builder
  {
    private Direction direction               = Direction.LEFT_TO_RIGHT;
    private char      separator               = '/';
    private char      escape                  = 0;
    private String    prefix                  = null;
    private boolean   ignoreCase              = false;
    private boolean   requireLeadingSeparator = false;

    private Builder() { }

    /** Sets the parsing direction. Must not be {@code null}. */
    public Builder direction(Direction d)
    {
      if (d == null)
        throw new IllegalArgumentException(
            "'direction' must not be null.");
      this.direction = d;
      return this;
    }

    /** Sets the separator character. Significant only for
     *  {@link Direction#LEFT_TO_RIGHT} and
     *  {@link Direction#RIGHT_TO_LEFT}. */
    public Builder separator(char c)
    {
      this.separator = c;
      return this;
    }

    /** Sets the escape character, or {@code 0} to disable
     *  escaping. */
    public Builder escape(char c)
    {
      this.escape = c;
      return this;
    }

    /** Sets the literal prefix string to strip on parse and
     *  prepend on format. Pass {@code null} (or the empty string)
     *  to disable. */
    public Builder prefix(String s)
    {
      this.prefix = (s != null && s.length() == 0) ? null : s;
      return this;
    }

    /** Enables case-insensitive component matching by
     *  lowercasing components during parse. */
    public Builder ignoreCase(boolean b)
    {
      this.ignoreCase = b;
      return this;
    }

    /** Requires a leading separator after any configured prefix.
     *  Inputs without one are rejected on parse, and the
     *  separator is emitted on format (after the prefix). */
    public Builder requireLeadingSeparator(boolean b)
    {
      this.requireLeadingSeparator = b;
      return this;
    }

    /** Builds the immutable {@link EntityNameSyntax}. */
    public EntityNameSyntax build()
    {
      return new EntityNameSyntax(this);
    }
  }
}
