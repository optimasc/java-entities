package com.optimasc.entity.parser;

import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;

import junit.framework.TestCase;

import com.optimasc.entity.EntityName;

/** JUnit 3 test suite for {@link DefaultEntityNameParser}.
 *
 *  <p>Covers each {@link EntityNameSyntax} preset, the most
 *  important corner cases (empty input, root names, trailing
 *  separators, escape sequences), error handling (bad prefix,
 *  missing required leading separator, unrepresentable
 *  components), and interoperation with the
 *  {@link java.text.Format} API.</p>
 *
 *  @author Carl Eric Codere
 */
public class DefaultEntityNameParserTest extends TestCase
{
  public DefaultEntityNameParserTest(String name)
  {
    super(name);
  }

  // ======================================================================
  // POSIX_PATH
  // ======================================================================

  public void testPosixSimplePath() throws ParseException
  {
    DefaultEntityNameParser p = new DefaultEntityNameParser(
        EntityNameSyntax.POSIX_PATH);
    EntityName n = p.parse("/home/alice");
    assertEquals(2, n.size());
    assertEquals("home", n.get(0));
    assertEquals("alice", n.get(1));
  }

  public void testPosixWithoutLeadingSlash() throws ParseException
  {
    DefaultEntityNameParser p = new DefaultEntityNameParser(
        EntityNameSyntax.POSIX_PATH);
    EntityName n = p.parse("home/alice");
    assertEquals(2, n.size());
    assertEquals("home", n.get(0));
    assertEquals("alice", n.get(1));
  }

  public void testPosixEmptyIsRoot() throws ParseException
  {
    DefaultEntityNameParser p = new DefaultEntityNameParser(
        EntityNameSyntax.POSIX_PATH);
    EntityName n = p.parse("");
    assertTrue(n.isEmpty());
    assertSame(EntityName.EMPTY, n);
  }

  public void testPosixSlashIsRoot() throws ParseException
  {
    DefaultEntityNameParser p = new DefaultEntityNameParser(
        EntityNameSyntax.POSIX_PATH);
    EntityName n = p.parse("/");
    assertTrue(n.isEmpty());
  }

  public void testPosixTrailingSlashNormalized() throws ParseException
  {
    DefaultEntityNameParser p = new DefaultEntityNameParser(
        EntityNameSyntax.POSIX_PATH);
    EntityName a = p.parse("/a/b/");
    EntityName b = p.parse("/a/b");
    assertEquals(a, b);
  }

  public void testPosixPreserveEmptyMiddleComponent()
      throws ParseException
  {
    // a//b → ["a", "", "b"] — we preserve empty components.
    DefaultEntityNameParser p = new DefaultEntityNameParser(
        EntityNameSyntax.POSIX_PATH);
    EntityName n = p.parse("/a//b");
    assertEquals(3, n.size());
    assertEquals("a", n.get(0));
    assertEquals("",  n.get(1));
    assertEquals("b", n.get(2));
  }

  public void testPosixFormatRoot()
  {
    DefaultEntityNameParser p = new DefaultEntityNameParser(
        EntityNameSyntax.POSIX_PATH);
    assertEquals("", p.format(EntityName.EMPTY));
  }

  public void testPosixFormatSimple()
  {
    DefaultEntityNameParser p = new DefaultEntityNameParser(
        EntityNameSyntax.POSIX_PATH);
    EntityName n = new EntityName(new String[] { "home", "alice" });
    // POSIX_PATH does not require leading separator → no leading "/"
    assertEquals("home/alice", p.format(n));
  }

  public void testPosixRoundTrip() throws ParseException
  {
    DefaultEntityNameParser p = new DefaultEntityNameParser(
        EntityNameSyntax.POSIX_PATH);
    EntityName n = new EntityName(new String[] { "a", "b", "c" });
    assertEquals(n, p.parse(p.format(n)));
  }

  // ======================================================================
  // DOM_XPATH (requires leading separator)
  // ======================================================================

  public void testXPathSimple() throws ParseException
  {
    DefaultEntityNameParser p = new DefaultEntityNameParser(
        EntityNameSyntax.DOM_XPATH);
    EntityName n = p.parse("/library/book/title");
    assertEquals(3, n.size());
    assertEquals("library", n.get(0));
    assertEquals("title",   n.get(2));
  }

  public void testXPathSlashIsRoot() throws ParseException
  {
    DefaultEntityNameParser p = new DefaultEntityNameParser(
        EntityNameSyntax.DOM_XPATH);
    assertTrue(p.parse("/").isEmpty());
  }

  public void testXPathBareNameRejected()
  {
    DefaultEntityNameParser p = new DefaultEntityNameParser(
        EntityNameSyntax.DOM_XPATH);
    try
    {
      p.parse("library/book");
      fail("Expected ParseException for missing leading separator.");
    }
    catch (ParseException e)
    {
      assertEquals(0, e.getErrorOffset());
    }
  }

  public void testXPathEmptyRejected()
  {
    DefaultEntityNameParser p = new DefaultEntityNameParser(
        EntityNameSyntax.DOM_XPATH);
    try
    {
      p.parse("");
      fail("Expected ParseException for empty input "
          + "(requireLeadingSeparator=true).");
    }
    catch (ParseException e)
    {
      // expected
    }
  }

  public void testXPathFormatRootHasSlash()
  {
    DefaultEntityNameParser p = new DefaultEntityNameParser(
        EntityNameSyntax.DOM_XPATH);
    assertEquals("/", p.format(EntityName.EMPTY));
  }

  public void testXPathFormatHasLeadingSlash()
  {
    DefaultEntityNameParser p = new DefaultEntityNameParser(
        EntityNameSyntax.DOM_XPATH);
    EntityName n = new EntityName(new String[] { "a", "b" });
    assertEquals("/a/b", p.format(n));
  }

  public void testXPathRoundTrip() throws ParseException
  {
    DefaultEntityNameParser p = new DefaultEntityNameParser(
        EntityNameSyntax.DOM_XPATH);
    String[] inputs = { "/", "/a", "/a/b", "/a/b/c" };
    for (int i = 0; i < inputs.length; i++)
    {
      EntityName n = p.parse(inputs[i]);
      assertEquals("round-trip failed for: " + inputs[i],
          inputs[i], p.format(n));
    }
  }

  // ======================================================================
  // LDAP_DN (right-to-left, escape, case-insensitive)
  // ======================================================================

  public void testLdapBasic() throws ParseException
  {
    DefaultEntityNameParser p = new DefaultEntityNameParser(
        EntityNameSyntax.LDAP_DN);
    EntityName n = p.parse(
        "cn=alice,ou=people,dc=example,dc=com");
    assertEquals(4, n.size());
    // Right-to-left: leaf-of-external becomes leaf-of-internal,
    // root-of-external (dc=com) becomes root-of-internal.
    assertEquals("dc=com",     n.get(0));
    assertEquals("dc=example", n.get(1));
    assertEquals("ou=people",  n.get(2));
    assertEquals("cn=alice",   n.get(3));
  }

  public void testLdapCaseFolding() throws ParseException
  {
    DefaultEntityNameParser p = new DefaultEntityNameParser(
        EntityNameSyntax.LDAP_DN);
    EntityName lower = p.parse("CN=Alice,DC=EXAMPLE");
    EntityName upper = p.parse("cn=alice,dc=example");
    assertEquals(lower, upper);
  }

  public void testLdapEscape() throws ParseException
  {
    DefaultEntityNameParser p = new DefaultEntityNameParser(
        EntityNameSyntax.LDAP_DN);
    // "Smith, John" inside a value — comma escaped with backslash.
    EntityName n = p.parse("cn=smith\\, john,ou=people");
    assertEquals(2, n.size());
    // Root-most is "ou=people" (right-to-left ordering).
    assertEquals("ou=people",      n.get(0));
    assertEquals("cn=smith, john", n.get(1));
  }

  public void testLdapEscapeRoundTrip() throws ParseException
  {
    DefaultEntityNameParser p = new DefaultEntityNameParser(
        EntityNameSyntax.LDAP_DN);
    String s = "cn=smith\\, john,ou=people";
    EntityName n = p.parse(s);
    String formatted = p.format(n);
    assertEquals(n, p.parse(formatted));
  }

  public void testLdapFormatPlain()
  {
    DefaultEntityNameParser p = new DefaultEntityNameParser(
        EntityNameSyntax.LDAP_DN);
    // Construct internally root-first; format emits leaf-first.
    EntityName n = new EntityName(new String[] {
        "dc=com", "dc=example", "ou=people", "cn=alice" });
    assertEquals("cn=alice,ou=people,dc=example,dc=com",
        p.format(n));
  }

  public void testLdapEmpty() throws ParseException
  {
    DefaultEntityNameParser p = new DefaultEntityNameParser(
        EntityNameSyntax.LDAP_DN);
    EntityName n = p.parse("");
    assertTrue(n.isEmpty());
  }

  // ======================================================================
  // FLAT
  // ======================================================================

  public void testFlatSimple() throws ParseException
  {
    DefaultEntityNameParser p = new DefaultEntityNameParser(
        EntityNameSyntax.FLAT);
    EntityName n = p.parse("hello-world");
    assertEquals(1, n.size());
    assertEquals("hello-world", n.get(0));
  }

  public void testFlatIgnoresSeparators() throws ParseException
  {
    // FLAT direction with a configured separator must still
    // treat the whole input as one component.
    EntityNameSyntax syntax = EntityNameSyntax.builder()
        .direction(EntityNameSyntax.Direction.FLAT)
        .separator('/')
        .build();
    DefaultEntityNameParser p = new DefaultEntityNameParser(syntax);
    EntityName n = p.parse("foo/bar/baz");
    assertEquals(1, n.size());
    assertEquals("foo/bar/baz", n.get(0));
  }

  public void testFlatEmptyIsRoot() throws ParseException
  {
    DefaultEntityNameParser p = new DefaultEntityNameParser(
        EntityNameSyntax.FLAT);
    assertTrue(p.parse("").isEmpty());
  }

  public void testFlatWithPrefix() throws ParseException
  {
    // URN-style: prefix strips literally, remainder is one
    // opaque identifier.
    EntityNameSyntax urnIsbn = EntityNameSyntax.builder()
        .direction(EntityNameSyntax.Direction.FLAT)
        .prefix("urn:isbn:")
        .build();
    DefaultEntityNameParser p = new DefaultEntityNameParser(urnIsbn);
    EntityName n = p.parse("urn:isbn:0451450523");
    assertEquals(1, n.size());
    assertEquals("0451450523", n.get(0));
    // Round-trip.
    assertEquals("urn:isbn:0451450523", p.format(n));
  }

  public void testFlatFormatMultiComponentRejected()
  {
    DefaultEntityNameParser p = new DefaultEntityNameParser(
        EntityNameSyntax.FLAT);
    EntityName n = new EntityName(new String[] { "a", "b" });
    try
    {
      p.format(n);
      fail("Expected IllegalArgumentException — FLAT cannot "
          + "represent a multi-component name.");
    }
    catch (IllegalArgumentException expected)
    {
      // expected
    }
  }

  // ======================================================================
  // FILE / HTTP
  // ======================================================================

  public void testFilePosixPath() throws ParseException
  {
    DefaultEntityNameParser p = new DefaultEntityNameParser(
        EntityNameSyntax.FILE);
    EntityName n = p.parse("file:///home/alice");
    assertEquals(2, n.size());
    assertEquals("home",  n.get(0));
    assertEquals("alice", n.get(1));
  }

  public void testFileWindowsPath() throws ParseException
  {
    DefaultEntityNameParser p = new DefaultEntityNameParser(
        EntityNameSyntax.FILE);
    EntityName n = p.parse("file:///C:/Users/alice");
    assertEquals(3, n.size());
    assertEquals("C:",    n.get(0));
    assertEquals("Users", n.get(1));
    assertEquals("alice", n.get(2));
  }

  public void testFileRoot() throws ParseException
  {
    DefaultEntityNameParser p = new DefaultEntityNameParser(
        EntityNameSyntax.FILE);
    EntityName n = p.parse("file:///");
    assertTrue(n.isEmpty());
    assertEquals("file:///", p.format(EntityName.EMPTY));
  }

  public void testFileWrongPrefixRejected()
  {
    DefaultEntityNameParser p = new DefaultEntityNameParser(
        EntityNameSyntax.FILE);
    try
    {
      p.parse("http://example.com/a");
      fail("Expected ParseException for non-file prefix input.");
    }
    catch (ParseException expected)
    {
      assertEquals(0, expected.getErrorOffset());
    }
  }

  public void testFileTwoSlashFormRejected()
  {
    // FILE is the RFC 8089 three-slash form. The nonstandard
    // two-slash forms (file://host/share and file://path) must
    // be rejected.
    DefaultEntityNameParser p = new DefaultEntityNameParser(
        EntityNameSyntax.FILE);
    try
    {
      p.parse("file://host/share");
      fail("Expected ParseException — two-slash form is not"
          + " accepted by the FILE preset.");
    }
    catch (ParseException expected)
    {
      // expected
    }
  }

  public void testFileRoundTrip() throws ParseException
  {
    DefaultEntityNameParser p = new DefaultEntityNameParser(
        EntityNameSyntax.FILE);
    String[] cases = {
        "file:///",
        "file:///home",
        "file:///home/alice",
        "file:///C:/Users/alice/letter.txt"
    };
    for (int i = 0; i < cases.length; i++)
    {
      EntityName n = p.parse(cases[i]);
      assertEquals("round-trip failed: " + cases[i],
          cases[i], p.format(n));
    }
  }

  public void testHttpBasic() throws ParseException
  {
    DefaultEntityNameParser p = new DefaultEntityNameParser(
        EntityNameSyntax.HTTP);
    EntityName n = p.parse("http://example.com/a/b");
    assertEquals(3, n.size());
    assertEquals("example.com", n.get(0));
    assertEquals("a",           n.get(1));
    assertEquals("b",           n.get(2));
  }

  // ======================================================================
  // Error handling and edge cases
  // ======================================================================

  public void testWrongPrefixRejected()
  {
    DefaultEntityNameParser p = new DefaultEntityNameParser(
        EntityNameSyntax.FILE);
    try
    {
      p.parse("ftp://example.com/a");
      fail("Expected ParseException.");
    }
    catch (ParseException expected)
    {
      // expected
    }
  }

  public void testFormatNullRejected()
  {
    DefaultEntityNameParser p = new DefaultEntityNameParser(
        EntityNameSyntax.POSIX_PATH);
    try
    {
      p.format((EntityName) null);
      fail("Expected IllegalArgumentException for null name.");
    }
    catch (IllegalArgumentException expected)
    {
      // expected
    }
  }

  public void testParseNullRejected() throws ParseException
  {
    DefaultEntityNameParser p = new DefaultEntityNameParser(
        EntityNameSyntax.POSIX_PATH);
    try
    {
      p.parse(null);
      fail("Expected IllegalArgumentException for null input.");
    }
    catch (IllegalArgumentException expected)
    {
      // expected
    }
  }

  public void testFormatUnrepresentableComponentRejected()
  {
    // POSIX_PATH has no escape. A component containing "/"
    // cannot be safely formatted because round-trip would split
    // it into two components.
    DefaultEntityNameParser p = new DefaultEntityNameParser(
        EntityNameSyntax.POSIX_PATH);
    EntityName n = new EntityName(new String[] { "a/b" });
    try
    {
      p.format(n);
      fail("Expected IllegalArgumentException — component "
          + "contains separator with no escape mechanism.");
    }
    catch (IllegalArgumentException expected)
    {
      // expected
    }
  }

  public void testConstructorRejectsNullSyntax()
  {
    try
    {
      new DefaultEntityNameParser(null);
      fail("Expected IllegalArgumentException for null syntax.");
    }
    catch (IllegalArgumentException expected)
    {
      // expected
    }
  }

  // ======================================================================
  // Format API integration
  // ======================================================================

  public void testFormatApiParseObject()
  {
    DefaultEntityNameParser p = new DefaultEntityNameParser(
        EntityNameSyntax.POSIX_PATH);
    ParsePosition pos = new ParsePosition(0);
    Object result = p.parseObject("/a/b", pos);
    assertTrue(result instanceof EntityName);
    EntityName n = (EntityName) result;
    assertEquals(2, n.size());
    assertEquals(4, pos.getIndex());
    assertEquals(-1, pos.getErrorIndex());
  }

  public void testFormatApiParseObjectFailureSetsErrorIndex()
  {
    DefaultEntityNameParser p = new DefaultEntityNameParser(
        EntityNameSyntax.FILE);
    ParsePosition pos = new ParsePosition(0);
    Object result = p.parseObject("ftp://x", pos);
    assertNull(result);
    assertTrue("errorIndex should have been set",
        pos.getErrorIndex() >= 0);
  }

  public void testFormatApiFormat()
  {
    DefaultEntityNameParser p = new DefaultEntityNameParser(
        EntityNameSyntax.POSIX_PATH);
    EntityName n = new EntityName(new String[] { "a", "b" });
    StringBuffer sb = new StringBuffer();
    StringBuffer ret = p.format(n, sb, new FieldPosition(0));
    assertSame(sb, ret);
    assertEquals("a/b", sb.toString());
  }

  public void testFormatApiFormatWrongTypeRejected()
  {
    DefaultEntityNameParser p = new DefaultEntityNameParser(
        EntityNameSyntax.POSIX_PATH);
    try
    {
      p.format("not an EntityName",
          new StringBuffer(), new FieldPosition(0));
      fail("Expected IllegalArgumentException for non-"
          + "EntityName argument.");
    }
    catch (IllegalArgumentException expected)
    {
      // expected
    }
  }

  // ======================================================================
  // Builder behaviour
  // ======================================================================

  public void testBuilderNullDirectionRejected()
  {
    try
    {
      EntityNameSyntax.builder().direction(null);
      fail("Expected IllegalArgumentException for null direction.");
    }
    catch (IllegalArgumentException expected)
    {
      // expected
    }
  }

  public void testBuilderEmptyPrefixBecomesNull()
  {
    EntityNameSyntax s =
        EntityNameSyntax.builder().prefix("").build();
    assertNull(s.getPrefix());
  }
}
