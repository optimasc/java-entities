package com.optimasc.entity;

import java.text.ParseException;

/**
 * Static utility methods for validating the syntactic correctness of string
 * forms used in the entity model — attribute local names, object identifiers
 * (OIDs), and similar.
 *
 * <p>
 * This class enforces only universal floor invariants. Stricter syntactic rules
 * (NCName conformance, ASCII-only restrictions, length limits) are the
 * responsibility of the schema or backend adapter that consumes the value.
 * </p>
 *
 * <p>
 * This class cannot be instantiated.
 * </p>
 *
 * @author Carl Eric Codere
 */
public final class Syntax
{

  /**
   * Returns {@code true} if {@code ch} is a Unicode non-character codepoint in
   * the BMP (U+FDD0–U+FDEF or U+FFFE–U+FFFF).
   */
  private static boolean isNonCharacter(char ch)
  {
    return (ch >= 0xFDD0 && ch <= 0xFDEF) || (ch == 0xFFFE) || (ch == 0xFFFF);
  }

  /**
   * Checks for prohibited characters in an identifier such as an attribute
   * <em>localName</em>. This is the universal floor enforced by the entity data
   * model; it deliberately does not impose a particular identifier
   * <em>syntax</em> such as NCName.
   *
   * <p>
   * Verification fails if {@code value} is {@code null}, empty, or contains any
   * character with one of the following properties:
   * </p>
   *
   * <ul>
   * <li>General_Category of Cc (Control);</li>
   * <li>General_Category of Cs (Surrogate);</li>
   * <li>General_Category of Co (Private Use);</li>
   * <li>General_Category of Cf (Format) — excluded to prevent confusable
   * identifiers built from bidi controls, zero-width joiners, the byte order
   * mark, and similar invisible characters. This exclusion is motivated by the
   * identifier-safety reasoning in Unicode Standard Annex #31;</li>
   * <li>General_Category of Zs, Zl, or Zp (any Separator) — identifiers contain
   * no whitespace of any kind, including U+00A0 NO-BREAK SPACE and similar code
   * points;</li>
   * <li>Codepoint is a Unicode non-character (U+FDD0–U+FDEF or U+FFFE–U+FFFF in
   * the BMP);</li>
   * <li>Character is {@code ':'}, {@code '/'}, or {@code '#'} — these are
   * reserved as namespace structure delimiters: {@code ':'} is the XML
   * namespace prefix separator, and {@code '/'} and {@code '#'} terminate the
   * namespace IRI in the {@code namespaceURI + localName} concatenation used to
   * build expanded names. Allowing them inside an identifier would prevent
   * unambiguous decomposition of the expanded name.</li>
   * </ul>
   *
   * <p>
   * Limitations: unassigned codepoints are not rejected, since they may be
   * assigned in future Unicode versions. Only the BMP (legacy UCS-2) range is
   * verified.
   * </p>
   *
   * @param value
   *          [in] The identifier string to check.
   * @throws ParseException
   *           if {@code value} is {@code null}, empty, or contains a prohibited
   *           character.
   */
  public static void verifyLocalName(CharSequence value) throws ParseException
  {
    if (value == null)
    {
      throw new ParseException("Identifier must not be null", 0);
    }
    int maxLength = value.length();
    if (maxLength == 0)
    {
      throw new ParseException("Identifier must not be empty", 0);
    }

    for (int i = 0; i < maxLength; i++)
    {
      char ch = value.charAt(i);

      /* Architectural restriction: namespace structure delimiters.
       * Checked first so the error message names the specific character. */
      if (ch == ':' || ch == '/' || ch == '#')
      {
        throw new ParseException(
            "Identifier contains a prohibited reserved character ('" + ch
                + "') at position",
            i);
      }

      int category = Character.getType(ch);
      switch (category)
      {
        case Character.SURROGATE:
          throw new ParseException(
              "Identifier contains a prohibited character (General category 'Cs') at position",
              i);
        case Character.PRIVATE_USE:
          throw new ParseException(
              "Identifier contains a prohibited character (General category 'Co') at position",
              i);
        case Character.FORMAT:
          throw new ParseException(
              "Identifier contains a prohibited character (General category 'Cf') at position",
              i);
        case Character.LINE_SEPARATOR:
          throw new ParseException(
              "Identifier contains a prohibited character (General category 'Zl') at position",
              i);
        case Character.PARAGRAPH_SEPARATOR:
          throw new ParseException(
              "Identifier contains a prohibited character (General category 'Zp') at position",
              i);
        case Character.SPACE_SEPARATOR:
          throw new ParseException(
              "Identifier contains a prohibited character (General category 'Zs') at position",
              i);
        case Character.CONTROL:
          throw new ParseException(
              "Identifier contains a prohibited character (General category 'Cc') at position",
              i);
        default:
          break;
      }

      if (isNonCharacter(ch))
      {
        throw new ParseException(
            "Identifier contains a prohibited non-character codepoint at position",
            i);
      }
    }
  }
  
  
  /**
   * Validates a fully numeric OBJECT IDENTIFIER syntax, and optionally checks
   * the length braces and returns the noidlen specifier if present. 
   * 
   * @param oid
   *          [in] The OBJECT IDENTIFIER value to validate.
   * @param allowLength
   *          [in] Allow length specifier in braces as defined in IETF RFC 4512.
   * @return The length of the value or -1 if there is no length.
   * @throws ParseException
   *           if {@code value} is {@code null}, empty, or contains a prohibited
   *           character.
   */
  public static int verifyObjectIdentifier(String oid, boolean allowLength) throws
    ParseException
  {
    final int EXPECT_DIGIT = 0;
    final int EXPECT_DOT = 1;
    final int EXPECT_END = 2;
    int nextState = EXPECT_DIGIT;
    if (oid == null)
    {
      throw new ParseException("oid must not be null", 0);
    }
    
    int count = oid.length();
    int i = 0;
    
    if (count == 0)
    {
      throw new ParseException("oid must not be empty", 0);
    }
    
    StringBuffer buffer = new StringBuffer();
    while (i < count)
    {
      if (nextState == EXPECT_DIGIT)
      {
        while ((i < count) && ((oid.charAt(i) >= '0') && (oid.charAt(i) <= '9')))
        {
          i++;
          nextState = EXPECT_DOT;
        }
      }
      if (i >= count)
      {
        break;
      }
      if (allowLength == true)
      {
        if (oid.charAt(i) == '{')
        {
          if (oid.charAt(oid.length()-1)!='}')
          {
            throw new ParseException("Illegal OBJECT IDENTIFIER syntax: Missing '}' to specify the length",i);
          }
          i++;
          while ((i < count) && (oid.charAt(i) != '}'))
          {
            buffer.append(oid.charAt(i));
            i++;
          }
          i++;
          nextState = EXPECT_END;
          if (i>=count)
              break;
        }
      }
      if ((oid.charAt(i) == '.') && (nextState == EXPECT_DOT))
      {
        i++;
        nextState = EXPECT_DIGIT;
        continue;
      }
      throw new ParseException("Illegal OBJECT IDENTIFIER syntax",i);
    }
    if (nextState == EXPECT_DIGIT)
    {
      throw new ParseException("Illegal OBJECT IDENTIFIER syntax",i);
    }
    if (buffer.length()==0)
    {
      return -1;
    }
    return Integer.parseInt(buffer.toString());
  }


}