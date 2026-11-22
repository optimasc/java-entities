package com.optimasc.attribute;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/** Tests of {@link AttributeUtilities#sameAttributeValues(Attribute, Attribute)}
 *  and of {@link DefaultAttribute#equals(Object)} / {@code hashCode()}. */
public class AttributeEqualityTest
{
  private static DefaultAttribute attr(String name, boolean ordered,
      Object... values)
  {
    DefaultAttribute a = new DefaultAttribute(null, null, name, Object.class,
        null, ordered, null);
    for (int i = 0; i < values.length; i++)
      a.add(values[i]);
    return a;
  }

  /** A context-aware attribute: values and keys alternate. */
  private static DefaultAttribute localized(String name, boolean ordered,
      String... valuesAndKeys)
  {
    DefaultAttribute a = new DefaultAttribute(null, null, name, Object.class,
        null, ordered, "Lang");
    for (int i = 0; i < valuesAndKeys.length; i += 2)
      a.add(valuesAndKeys[i], valuesAndKeys[i + 1]);
    return a;
  }

  // ---- sameAttributeValues ----

  @Test
  public void nullsAndIdentity()
  {
    DefaultAttribute a = attr("a", false, "x");
    assertTrue(AttributeUtilities.sameAttributeValues(null, null));
    assertFalse(AttributeUtilities.sameAttributeValues(a, null));
    assertFalse(AttributeUtilities.sameAttributeValues(null, a));
    assertTrue(AttributeUtilities.sameAttributeValues(a, a));
  }

  @Test
  public void emptyAttributesAreTheSame()
  {
    assertTrue(AttributeUtilities.sameAttributeValues(attr("a", false),
        attr("b", true)));
  }

  @Test
  public void differentSizesDiffer()
  {
    assertFalse(AttributeUtilities.sameAttributeValues(attr("a", false, "x"),
        attr("a", false, "x", "y")));
  }

  @Test
  public void unorderedIgnoresTheOrder()
  {
    assertTrue(AttributeUtilities.sameAttributeValues(
        attr("a", false, "x", "y", "z"), attr("a", false, "z", "x", "y")));
  }

  @Test
  public void unorderedCountsDuplicates()
  {
    assertFalse(AttributeUtilities.sameAttributeValues(
        attr("a", false, "a", "a", "b"), attr("a", false, "a", "b", "b")));
    assertTrue(AttributeUtilities.sameAttributeValues(
        attr("a", false, "a", "b", "a"), attr("a", false, "a", "a", "b")));
  }

  @Test
  public void unorderedDifferentValuesDiffer()
  {
    assertFalse(AttributeUtilities.sameAttributeValues(
        attr("a", false, "x", "y"), attr("a", false, "x", "z")));
  }

  @Test
  public void orderedIsPositional()
  {
    assertTrue(AttributeUtilities.sameAttributeValues(
        attr("a", true, "x", "y"), attr("a", true, "x", "y")));
    assertFalse(AttributeUtilities.sameAttributeValues(
        attr("a", true, "x", "y"), attr("a", true, "y", "x")));
  }

  @Test
  public void eitherOrderedMakesItPositional()
  {
    assertFalse(AttributeUtilities.sameAttributeValues(
        attr("a", true, "x", "y"), attr("a", false, "y", "x")));
    assertFalse(AttributeUtilities.sameAttributeValues(
        attr("a", false, "x", "y"), attr("a", true, "y", "x")));
    assertTrue(AttributeUtilities.sameAttributeValues(
        attr("a", true, "x", "y"), attr("a", false, "x", "y")));
  }

  @Test
  public void namesAndClassesAreIgnored()
  {
    DefaultAttribute a = new DefaultAttribute(null, null, "one",
        Integer.class, null, false, null);
    a.add(Integer.valueOf(1));
    DefaultAttribute b = new DefaultAttribute(null, null, "two",
        Object.class, null, false, null);
    b.add(Integer.valueOf(1));
    assertTrue(AttributeUtilities.sameAttributeValues(a, b));
  }

  @Test
  public void contextKeysTakePart()
  {
    assertTrue(AttributeUtilities.sameAttributeValues(
        localized("t", false, "Hello", "en", "Bonjour", "fr"),
        localized("t", false, "Bonjour", "fr", "Hello", "en")));
    assertFalse(AttributeUtilities.sameAttributeValues(
        localized("t", false, "Hello", "en"),
        localized("t", false, "Hello", "fr")));
    assertFalse(AttributeUtilities.sameAttributeValues(
        localized("t", false, "Hello", "en", "Bonjour", "fr"),
        localized("t", false, "Hello", "fr", "Bonjour", "en")));
    // A value with a context key is not the same as one without.
    assertFalse(AttributeUtilities.sameAttributeValues(
        localized("t", false, "Hello", "en"), attr("t", false, "Hello")));
  }

  // ---- DefaultAttribute.equals / hashCode ----

  @Test
  public void equalsIgnoresTheOrderOfAnUnorderedAttribute()
  {
    DefaultAttribute a = attr("a", false, "x", "y", "z");
    DefaultAttribute b = attr("a", false, "z", "y", "x");
    assertEquals(a, b);
    assertEquals(b, a);
    assertEquals(a.hashCode(), b.hashCode());
  }

  @Test
  public void equalsUsesTheOrderOfAnOrderedAttribute()
  {
    assertEquals(attr("a", true, "x", "y"), attr("a", true, "x", "y"));
    assertEquals(attr("a", true, "x", "y").hashCode(),
        attr("a", true, "x", "y").hashCode());
    assertFalse(attr("a", true, "x", "y").equals(attr("a", true, "y", "x")));
  }

  @Test
  public void equalsCountsDuplicates()
  {
    assertFalse(attr("a", false, "a", "a", "b").equals(
        attr("a", false, "a", "b", "b")));
  }

  @Test
  public void orderedFlagIsPartOfEquals()
  {
    assertFalse(attr("a", true, "x", "y").equals(attr("a", false, "x", "y")));
    assertFalse(attr("a", false, "x", "y").equals(attr("a", true, "x", "y")));
  }

  @Test
  public void equalsIsTransitiveAcrossTheOrderedFlag()
  {
    DefaultAttribute orderedXY = attr("a", true, "x", "y");
    DefaultAttribute orderedYX = attr("a", true, "y", "x");
    DefaultAttribute unorderedYX = attr("a", false, "y", "x");
    // The case that breaks transitivity if the flag were ignored.
    assertFalse(orderedXY.equals(unorderedYX));
    assertFalse(unorderedYX.equals(orderedYX));
    assertFalse(orderedXY.equals(orderedYX));
  }

  @Test
  public void equalsStillComparesNameAndClass()
  {
    assertFalse(attr("a", false, "x").equals(attr("b", false, "x")));
    DefaultAttribute strings = new DefaultAttribute(null, null, "a",
        String.class, null, false, null);
    strings.add("x");
    assertFalse(strings.equals(attr("a", false, "x")));
  }

  @Test
  public void equalsUsesTheContextKeys()
  {
    DefaultAttribute a = localized("t", false, "Hello", "en", "Bonjour", "fr");
    DefaultAttribute b = localized("t", false, "Bonjour", "fr", "Hello", "en");
    assertEquals(a, b);
    assertEquals(a.hashCode(), b.hashCode());
    assertFalse(a.equals(localized("t", false, "Hello", "fr", "Bonjour", "en")));
  }

  @Test
  public void equalsRejectsNullAndOtherTypes()
  {
    DefaultAttribute a = attr("a", false, "x");
    assertFalse(a.equals(null));
    assertFalse(a.equals("x"));
    assertTrue(a.equals(a));
  }
}
