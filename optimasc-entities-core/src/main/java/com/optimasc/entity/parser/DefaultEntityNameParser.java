package com.optimasc.entity.parser;

import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.List;

import com.optimasc.entity.EntityName;

/** Configurable, property-driven implementation of
 *  {@link com.optimasc.entity.EntityNameParser}.
 *
 *  <p>The parser's behaviour is fixed at construction time by an
 *  {@link EntityNameSyntax} configuration object. A single
 *  instance is stateless and safe for concurrent use by multiple
 *  threads.</p>
 *
 *  <h2>Round-trip contract</h2>
 *
 *  <p>For any {@link EntityName} {@code n} this parser produces,
 *  {@code parse(format(n)).equals(n)}. For any input string the
 *  parser accepts, {@code format(parse(s))} is equal to {@code s}
 *  up to normalization (case-folding when
 *  {@link EntityNameSyntax#isIgnoreCase()} is set, and removal of
 *  a trailing separator).</p>
 *
 *  @see EntityNameSyntax
 *  @see com.optimasc.entity.AbstractEntityNameParser
 *  @author Carl Eric Codere
 */
public class DefaultEntityNameParser extends AbstractEntityNameParser
{
  /** Configuration; never {@code null}. */
  protected final EntityNameSyntax syntax;

  /** Constructs a parser with the given syntax configuration.
   *
   * @param syntax [in] The syntax to use. Must not be
   *   {@code null}.
   * @throws IllegalArgumentException if {@code syntax} is
   *   {@code null}.
   */
  public DefaultEntityNameParser(EntityNameSyntax syntax)
  {
    if (syntax == null)
      throw new IllegalArgumentException(
          "'syntax' must not be null.");
    this.syntax = syntax;
  }

  /** Returns the syntax configuration this parser was constructed
   *  with. Never {@code null}. */
  public EntityNameSyntax getSyntax()
  {
    return syntax;
  }

  // -----------------------------------------------------------------------
  // Parsing
  // -----------------------------------------------------------------------

  protected EntityName doParse(String source, ParsePosition pos)
  {
    int start = pos.getIndex();
    int end   = source.length();

    // 1. Strip the configured prefix if any.
    if (syntax.getPrefix() != null)
    {
      String prefix = syntax.getPrefix();
      if (!source.regionMatches(start, prefix, 0, prefix.length()))
      {
        pos.setErrorIndex(start);
        return null;
      }
      start += prefix.length();
    }

    // 2. FLAT direction: the rest is a single component.
    if (syntax.getDirection() == EntityNameSyntax.Direction.FLAT)
    {
      if (start == end)
      {
        pos.setIndex(end);
        return EntityName.EMPTY;
      }
      String component = source.substring(start, end);
      if (syntax.isIgnoreCase())
        component = component.toLowerCase();
      pos.setIndex(end);
      return new EntityName(new String[] { component });
    }

    // 3. Empty after prefix strip → EMPTY (the root), unless a
    //    leading separator is required (in which case the empty
    //    string carries no separator and is a syntax error).
    if (start == end)
    {
      if (syntax.isRequireLeadingSeparator())
      {
        pos.setErrorIndex(start);
        return null;
      }
      pos.setIndex(end);
      return EntityName.EMPTY;
    }

    // 4. Leading separator handling.
    char sep = syntax.getSeparator();
    boolean hasLeading = (source.charAt(start) == sep);
    if (syntax.isRequireLeadingSeparator() && !hasLeading)
    {
      pos.setErrorIndex(start);
      return null;
    }
    if (hasLeading)
    {
      start++;
      // A bare separator after the prefix is the root.
      if (start == end)
      {
        pos.setIndex(end);
        return EntityName.EMPTY;
      }
    }

    // 5. Split on separator, honouring the escape character.
    List<String> components = new ArrayList<String>();
    char escape = syntax.getEscape();
    boolean hasEscape = (escape != 0);
    StringBuffer current = new StringBuffer();
    int i = start;
    while (i < end)
    {
      char c = source.charAt(i);
      if (hasEscape && c == escape && i + 1 < end)
      {
        // Escape sequence: next character is taken literally.
        current.append(source.charAt(i + 1));
        i += 2;
        continue;
      }
      if (c == sep)
      {
        components.add(finishComponent(current));
        current.setLength(0);
        i++;
        continue;
      }
      current.append(c);
      i++;
    }
    // Trailing separator → don't add a trailing empty component
    // (normalize away). Otherwise add the last buffered one.
    boolean trailingSeparator =
        (end > start) && (source.charAt(end - 1) == sep)
            && !(hasEscape && end >= 2
                 && source.charAt(end - 2) == escape);
    if (!trailingSeparator)
    {
      components.add(finishComponent(current));
    }

    // 6. Direction reversal for RIGHT_TO_LEFT.
    if (syntax.getDirection() == EntityNameSyntax.Direction.RIGHT_TO_LEFT)
    {
      int n = components.size();
      String[] reversed = new String[n];
      for (int k = 0; k < n; k++)
        reversed[k] = (String) components.get(n - 1 - k);
      pos.setIndex(end);
      return new EntityName(reversed);
    }

    String[] arr = (String[]) components.toArray(new String[components.size()]);
    pos.setIndex(end);
    return new EntityName(arr);
  }

  /** Finalizes a component currently being assembled in
   *  {@code buf}, applying case-folding if configured, and
   *  returns the resulting string. */
  private String finishComponent(StringBuffer buf)
  {
    String s = buf.toString();
    if (syntax.isIgnoreCase())
      s = s.toLowerCase();
    return s;
  }

  // -----------------------------------------------------------------------
  // Formatting
  // -----------------------------------------------------------------------

  public String format(EntityName name)
  {
    if (name == null)
      throw new IllegalArgumentException(
          "'name' must not be null.");

    StringBuffer sb = new StringBuffer();

    // Prefix.
    if (syntax.getPrefix() != null)
      sb.append(syntax.getPrefix());

    // FLAT direction.
    if (syntax.getDirection() == EntityNameSyntax.Direction.FLAT)
    {
      if (name.size() == 0)
      {
        return sb.toString();
      }
      if (name.size() > 1)
      {
        throw new IllegalArgumentException(
            "FLAT syntax cannot represent a multi-component name: "
                + name);
      }
      sb.append(name.get(0));
      return sb.toString();
    }

    // Empty name handling for hierarchical syntaxes.
    if (name.size() == 0)
    {
      if (syntax.isRequireLeadingSeparator())
        sb.append(syntax.getSeparator());
      return sb.toString();
    }

    // Determine emit order. RIGHT_TO_LEFT walks components
    // leaf-first regardless of the requireLeadingSeparator flag
    // — leaf-first syntaxes (e.g. LDAP DNs) never start with a
    // separator.
    boolean rightToLeft =
        (syntax.getDirection() == EntityNameSyntax.Direction.RIGHT_TO_LEFT);
    int n = name.size();

    if (!rightToLeft && syntax.isRequireLeadingSeparator())
      sb.append(syntax.getSeparator());

    char sep    = syntax.getSeparator();
    char escape = syntax.getEscape();
    boolean hasEscape = (escape != 0);

    for (int k = 0; k < n; k++)
    {
      if (k > 0)
        sb.append(sep);
      String component = rightToLeft
          ? name.get(n - 1 - k)
          : name.get(k);
      appendEscaped(sb, component, sep, escape, hasEscape);
    }
    return sb.toString();
  }

  /** Appends {@code component} to {@code sb}, escaping the
   *  separator and the escape character itself when an escape
   *  mechanism is configured. Throws
   *  {@link IllegalArgumentException} if the component contains
   *  the separator with no escape mechanism available, since the
   *  round-trip contract cannot be honoured.
   */
  private void appendEscaped(StringBuffer sb, String component,
      char sep, char escape, boolean hasEscape)
  {
    int len = component.length();
    for (int i = 0; i < len; i++)
    {
      char c = component.charAt(i);
      if (c == sep || (hasEscape && c == escape))
      {
        if (!hasEscape)
        {
          throw new IllegalArgumentException(
              "Component contains separator character '" + sep
                  + "' but syntax provides no escape mechanism: \""
                  + component + "\"");
        }
        sb.append(escape);
      }
      sb.append(c);
    }
  }
}
