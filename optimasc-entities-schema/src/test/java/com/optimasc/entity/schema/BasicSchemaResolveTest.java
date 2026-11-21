package com.optimasc.entity.schema;

import junit.framework.TestCase;

import com.optimasc.entity.Entity;
import com.optimasc.entity.EntityName;
import com.optimasc.schema.ClassDefinition;
import com.optimasc.schema.ItemDefinition;

/** Tests of {@link BasicSchema#resolve(EntityName)},
 *  {@link BasicSchema#resolve(String)} and
 *  {@link BasicSchema#getNameParser()}. */
public class BasicSchemaResolveTest extends TestCase
{
  private static final String DC = "http://purl.org/dc/elements/1.1/";

  private BasicSchema schema;
  private ItemDefinition plain;
  private ItemDefinition namespaced;
  private ClassDefinition clazz;

  protected void setUp() throws Exception
  {
    schema = new BasicSchema();
    plain = new DefaultItemDefinition(null, null, "cn", "Common name",
        StandardTypes.OID_STRING, ItemDefinition.VALUE_TYPE_SINGLE, null,
        false);
    namespaced = new DefaultItemDefinition(null, DC, "title", "Title",
        StandardTypes.OID_STRING, ItemDefinition.VALUE_TYPE_SINGLE, null,
        false);
    clazz = new DefaultClassDefinition(null, null, "person", "A class",
        null, ClassDefinition.ObjectClassKind.structuralClass,
        new String[] { "cn" });
    assertTrue(schema.add(plain));
    assertTrue(schema.add(namespaced));
    assertTrue(schema.add(clazz));
    assertTrue(schema.add(StandardTypes.STRING));
  }

  private static EntityName name(String... components)
  {
    return new EntityName(components);
  }

  public void testEmptyNameIsTheRoot() throws Exception
  {
    assertSame(schema.getRoot(), schema.resolve(EntityName.EMPTY));
    assertSame(schema.getRoot(), schema.resolve(""));
  }

  public void testContainers() throws Exception
  {
    String[] names = { Schema.CONTAINER_ATTRIBUTE_DEFINITIONS,
        Schema.CONTAINER_TYPE_DEFINITIONS, Schema.CONTAINER_CLASS_DEFINITIONS };
    for (int i = 0; i < names.length; i++)
    {
      Entity e = schema.resolve(name(names[i]));
      assertNotNull(names[i], e);
      assertEquals(Entity.OBJECT_CLASS_CONTAINER, e.getObjectClass());
      assertEquals(names[i], e.getDisplayName());
      assertSame(e, schema.resolve(names[i]));
    }
  }

  public void testDefinitionWithoutNamespace() throws Exception
  {
    Entity e = schema.resolve(name("AttributeDefinitions", "cn"));
    assertSame(plain, e);
    assertEquals(ItemDefinition.OBJECT_CLASS_NAME, e.getObjectClass());
  }

  public void testDefinitionWithNamespace() throws Exception
  {
    Entity e = schema.resolve(name("AttributeDefinitions", DC, "title"));
    assertSame(namespaced, e);
  }

  public void testDefinitionInAnotherContainerIsNotFound() throws Exception
  {
    assertSame(clazz, schema.resolve(name("ClassDefinitions", "person")));
    assertNull(schema.resolve(name("AttributeDefinitions", "person")));
    assertNull(schema.resolve(name("TypeDefinitions", "cn")));
    assertNull(schema.resolve(name("person")));
  }

  public void testNamesAreUniqueAcrossKinds() throws Exception
  {
    // The index of a schema is shared by attributes, types and classes.
    ClassDefinition same = new DefaultClassDefinition(null, null, "cn", "d",
        null, ClassDefinition.ObjectClassKind.structuralClass,
        new String[] { "cn" });
    assertFalse(schema.add(same));
  }

  public void testTypeDefinition() throws Exception
  {
    Entity type = (Entity) StandardTypes.STRING;
    EntityName own = type.getNameInNamespace();
    String[] parts = new String[own.size() + 1];
    parts[0] = "TypeDefinitions";
    for (int i = 0; i < own.size(); i++)
      parts[i + 1] = own.get(i);
    assertSame(type, schema.resolve(new EntityName(parts)));
  }

  public void testUnknownNamesResolveToNull() throws Exception
  {
    assertNull(schema.resolve(name("Unknown")));
    assertNull(schema.resolve(name("AttributeDefinitions", "nope")));
    assertNull(schema.resolve(name("AttributeDefinitions", "title")));
    assertNull(schema.resolve(name("AttributeDefinitions", "cn", "extra")));
    assertNull(schema.resolve(name("attributedefinitions", "cn")));
    assertNull(schema.resolve(name("AttributeDefinitions", "CN")));
    assertNull(schema.resolve("AttributeDefinitions/nope"));
  }

  public void testNullArguments() throws Exception
  {
    try
    {
      schema.resolve((EntityName) null);
      fail();
    } catch (IllegalArgumentException e)
    {
      // Expected.
    }
    try
    {
      schema.resolve((String) null);
      fail();
    } catch (IllegalArgumentException e)
    {
      // Expected.
    }
  }

  public void testTreeIsLive() throws Exception
  {
    EntityName n = name("AttributeDefinitions", "late");
    assertNull(schema.resolve(n));
    ItemDefinition late = new DefaultItemDefinition(null, null, "late", "d",
        StandardTypes.OID_STRING, ItemDefinition.VALUE_TYPE_SINGLE, null,
        false);
    schema.add(late);
    assertSame(late, schema.resolve(n));
    schema.remove(late);
    assertNull(schema.resolve(n));
  }

  public void testEveryEntityOfTheTreeIsReachable() throws Exception
  {
    java.util.List<? extends Entity> containers =
        ((com.optimasc.entity.ContainerEntity) schema.getRoot()).getChildren();
    assertEquals(3, containers.size());
    for (int i = 0; i < containers.size(); i++)
    {
      Entity container = containers.get(i);
      assertSame(container, schema.resolve(container.getNameInNamespace()));
      java.util.List<? extends Entity> leaves =
          ((com.optimasc.entity.ContainerEntity) container).getChildren();
      for (int j = 0; j < leaves.size(); j++)
      {
        Entity leaf = leaves.get(j);
        EntityName own = leaf.getNameInNamespace();
        String[] parts = new String[own.size() + 1];
        parts[0] = container.getDisplayName();
        for (int k = 0; k < own.size(); k++)
          parts[k + 1] = own.get(k);
        assertSame(leaf, schema.resolve(new EntityName(parts)));
      }
    }
  }

  // ---- String syntax ----

  public void testParserSyntax() throws Exception
  {
    assertSame(schema.getNameParser(), schema.getNameParser());
    assertEquals(EntityName.EMPTY, schema.getNameParser().parse(""));
    assertEquals(name("ClassDefinitions", "person"),
        schema.getNameParser().parse("ClassDefinitions/person"));
  }

  public void testResolveByString() throws Exception
  {
    assertSame(plain, schema.resolve("AttributeDefinitions/cn"));
    assertSame(clazz, schema.resolve("ClassDefinitions/person"));
  }

  public void testNamespaceUriStaysOneComponent() throws Exception
  {
    String text = schema.getNameParser().format(
        name("AttributeDefinitions", DC, "title"));
    assertEquals(name("AttributeDefinitions", DC, "title"),
        schema.getNameParser().parse(text));
    assertSame(namespaced, schema.resolve(text));
  }

  public void testRoundTripOfEveryName() throws Exception
  {
    EntityName[] names = { EntityName.EMPTY, name("TypeDefinitions"),
        name("AttributeDefinitions", DC, "title"), name("a/b", "c\\d") };
    for (int i = 0; i < names.length; i++)
      assertEquals(names[i], schema.getNameParser().parse(
          schema.getNameParser().format(names[i])));
  }
}
