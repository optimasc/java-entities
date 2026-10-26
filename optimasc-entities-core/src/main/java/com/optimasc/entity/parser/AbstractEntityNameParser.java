package com.optimasc.entity.parser;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;

import com.optimasc.entity.EntityName;
import com.optimasc.entity.EntityNameParser;

/** Convenience base class for {@link EntityNameParser}
 *  implementations that wish to interoperate with the
 *  {@link java.text.Format} API.
 *
 *  <p>This class implements both {@link EntityNameParser} and
 *  the abstract methods of {@link Format} on top of a single
 *  parsing primitive that subclasses provide:
 *  {@link #doParse(String, ParsePosition)}. The same code path
 *  is shared between {@link #parse(String)} (the entity API) and
 *  {@link #parseObject(String, ParsePosition)} (the
 *  {@code Format} API), so subclasses write the syntax logic
 *  exactly once.</p>
 *
 *  <p>Subclasses must implement two methods:</p>
 *  <ul>
 *    <li>{@link #doParse(String, ParsePosition)} &mdash; advances
 *        the parse position through the input string and returns
 *        the parsed {@link EntityName}, or signals failure by
 *        calling {@link ParsePosition#setErrorIndex(int)} and
 *        returning {@code null}.</li>
 *    <li>{@link #format(EntityName)} &mdash; renders an
 *        {@code EntityName} into its canonical external form.</li>
 *  </ul>
 *
 *  <p>Once these two are in place, all four entry points
 *  ({@link #parse(String)}, {@link #parseObject(String)},
 *  {@link #parseObject(String, ParsePosition)}, and
 *  {@link #format(Object, StringBuffer, FieldPosition)}) work
 *  consistently.</p>
 *
 *  <h2>{@code Serializable} and {@code Cloneable}</h2>
 *
 *  <p>{@link Format} declares itself {@code Serializable} and
 *  {@code Cloneable}. This class does not add a
 *  {@code serialVersionUID} and does not override {@code clone()};
 *  subclasses that genuinely need either capability must take
 *  responsibility for them. Most subclasses will not: a parser
 *  is typically a stateless singleton obtained from a
 *  {@link Namespace}.</p>
 *
 *  @see EntityNameParser
 *  @see Format
 *  @author Carl Eric Codere
 */
public abstract class AbstractEntityNameParser extends Format
    implements EntityNameParser
{
  // -----------------------------------------------------------------------
  // EntityNameParser — the strongly-typed entry point
  // -----------------------------------------------------------------------

  /** {@inheritDoc}
   *
   *  <p>This implementation delegates to
   *  {@link #doParse(String, ParsePosition)} and converts a
   *  {@code null} return (with {@code errorIndex} set on the
   *  position) into a {@link ParseException}. It additionally
   *  treats a successful parse that does not consume the entire
   *  input as a syntax error, reporting the first unconsumed
   *  character as the error offset.</p>
   */
  @Override
  public EntityName parse(String nameInNamespace) throws ParseException
  {
    if (nameInNamespace == null)
      throw new IllegalArgumentException(
          "'nameInNamespace' must not be null.");

    ParsePosition pos = new ParsePosition(0);
    EntityName result = doParse(nameInNamespace, pos);

    if (result == null)
    {
      int errorOffset = pos.getErrorIndex();
      if (errorOffset < 0)
        errorOffset = pos.getIndex();
      throw new ParseException(
          "Unparseable name: \"" + nameInNamespace + "\"",
          errorOffset);
    }

    if (pos.getIndex() < nameInNamespace.length())
      throw new ParseException(
          "Unexpected trailing content in name: \""
              + nameInNamespace + "\"",
          pos.getIndex());

    return result;
  }

  // -----------------------------------------------------------------------
  // Format — the JDK-integration entry points
  // -----------------------------------------------------------------------

  /** Implements {@link Format#parseObject(String, ParsePosition)}
   *  by delegating to {@link #doParse(String, ParsePosition)}.
   *
   *  <p>Returns the parsed {@link EntityName} on success, or
   *  {@code null} on failure (in which case {@code pos.errorIndex}
   *  will have been set). Unlike {@link #parse(String)}, this
   *  method does not require the entire input to be consumed;
   *  this is the {@code Format} contract, which allows a parser
   *  to be used as a sub-parser within a larger string.</p>
   *
   * @param source [in] The input string. Must not be
   *   {@code null}.
   * @param pos    [in/out] The parse position; advanced to the
   *   first unconsumed character on success, or has its
   *   {@code errorIndex} set on failure.
   * @return The parsed {@link EntityName}, or {@code null} on
   *   failure.
   */
  @Override
  public Object parseObject(String source, ParsePosition pos)
  {
    if (source == null)
      throw new IllegalArgumentException("'source' must not be null.");
    if (pos == null)
      throw new IllegalArgumentException("'pos' must not be null.");
    return doParse(source, pos);
  }

  /** Implements
   *  {@link Format#format(Object, StringBuffer, FieldPosition)}
   *  by delegating to {@link #format(EntityName)}.
   *
   *  <p>The {@link FieldPosition} argument is accepted for
   *  {@code Format} compatibility but is not used &mdash; entity
   *  names have no fields in the {@code DateFormat} /
   *  {@code NumberFormat} sense.</p>
   *
   * @param obj      [in] Must be an {@link EntityName} instance.
   * @param toAppend [in/out] The buffer to which the formatted
   *   output is appended.
   * @param pos      [in/out] Ignored. Pass any non-null instance.
   * @return The same {@link StringBuffer} passed in
   *   {@code toAppend}, with the formatted name appended.
   * @throws IllegalArgumentException if {@code obj} is not an
   *   {@link EntityName}, or if any required argument is
   *   {@code null}.
   */
  @Override
  public StringBuffer format(Object obj, StringBuffer toAppend,
      FieldPosition pos)
  {
    if (obj == null)
      throw new IllegalArgumentException("'obj' must not be null.");
    if (toAppend == null)
      throw new IllegalArgumentException(
          "'toAppend' must not be null.");
    if (!(obj instanceof EntityName))
      throw new IllegalArgumentException(
          "Argument must be an EntityName, got "
              + obj.getClass().getName() + ".");
    toAppend.append(format((EntityName) obj));
    return toAppend;
  }

  // -----------------------------------------------------------------------
  // Subclass contract
  // -----------------------------------------------------------------------

  /** Parses an {@link EntityName} from {@code source} starting at
   *  {@code pos.getIndex()}, advancing {@code pos} past the
   *  consumed characters.
   *
   *  <p>This is the single parsing primitive that subclasses
   *  must implement. Both {@link #parse(String)} and
   *  {@link #parseObject(String, ParsePosition)} delegate to it.</p>
   *
   *  <p>On success, the implementation returns the parsed name
   *  and sets {@code pos.index} to the first unconsumed character.
   *  On failure, the implementation returns {@code null} and sets
   *  {@code pos.errorIndex} to the character position where
   *  parsing failed. The implementation should not throw on
   *  syntactic failure; failure is signalled solely through the
   *  return value and {@code errorIndex}.</p>
   *
   *  <p>Implementations are free to consume only a prefix of the
   *  input &mdash; the {@code Format} contract permits a parser
   *  to be a sub-parser within a larger string, and
   *  {@link #parse(String)} is responsible for enforcing
   *  full-input consumption on top of this primitive.</p>
   *
   * @param source [in] The input string. Will not be {@code null}.
   * @param pos    [in/out] The parse position; {@code pos.index}
   *   is updated on success, {@code pos.errorIndex} is set on
   *   failure. Will not be {@code null}.
   * @return The parsed {@link EntityName} on success, or
   *   {@code null} on failure.
   */
  protected abstract EntityName doParse(String source, ParsePosition pos);

  /** Formats an {@link EntityName} into its canonical external
   *  string form. This method is declared by
   *  {@link EntityNameParser} and must be implemented by
   *  subclasses; it is repeated here for documentation purposes
   *  to make the subclass contract explicit alongside
   *  {@link #doParse}.
   */
  public abstract String format(EntityName name);
}
