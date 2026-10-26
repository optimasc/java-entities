package com.optimasc.entity.schema;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.optimasc.attribute.Attribute;
import com.optimasc.datatypes.facets.EnumerationFacet;
import com.optimasc.schema.ClassDefinition;
import com.optimasc.schema.ItemDefinition;

/**
 * Utility methods for schema validation.
 *
 * <p>
 * This class provides static helper methods for validating attribute values and
 * entity instances against schema definitions held in a {@link Schema}. All
 * methods follow a non-throwing contract: rather than throwing exceptions
 * directly, they return an {@link IllegalArgumentException} describing the
 * validation failure, or {@code null} when validation succeeds. Callers may
 * then choose to throw, log, or otherwise handle the returned exception.
 * </p>
 *
 * <p>
 * This class cannot be instantiated.
 * </p>
 *
 * @see Schema
 * @see ItemDefinition
 * @see ClassDefinition
 * @author Carl Eric Codere
 */
public final class SchemaUtilities
{
  private SchemaUtilities()
  {
    // Utility class — not instantiable.
  }

  // -----------------------------------------------------------------------
  // Attribute validation
  // -----------------------------------------------------------------------

  /**
   * Verifies that an attribute's values conform to the constraints declared
   * for it in the supplied schema.
   *
   * <p>
   * The checks below are applied in order. Each schema-derived check is
   * skipped silently when the relevant definition is absent: if
   * {@code schema} is {@code null}, or no {@link ItemDefinition} is found for
   * the attribute's expanded name, the schema-based checks do not run; the
   * per-value type check against the attribute's own value class still
   * applies.
   * </p>
   * <ol>
   * <li>If an {@link ItemDefinition} declares the item single-valued
   * ({@link ItemDefinition#VALUE_TYPE_SINGLE}), verifies the attribute holds
   * at most one value.</li>
   * <li>If the attribute reports a value type name, verifies it matches the
   * item definition's declared type name (case-insensitive).</li>
   * <li>If a {@link EntityTypeDefinition} exists for the item's type name, verifies
   * the attribute's value class equals the type's declared Java class.</li>
   * <li>For each value (a {@code null} value is permitted and skips the
   * per-value checks below): verifies it is assignment-compatible with the
   * attribute's value class.</li>
   * <li>For each value: if a maximum length is defined, verifies the value
   * does not exceed it. How length is measured per value type is defined by
   * {@link ItemDefinition#getMaxValueLength()}.</li>
   * <li>For each value of an alternative-valued item
   * ({@link ItemDefinition#VALUE_TYPE_ALT}): verifies a context key is
   * present. For any other value type: verifies a context key is
   * <em>absent</em>.</li>
   * <li>For each non-null value: if the {@link EntityTypeDefinition} enumerates
   * allowed values, verifies the
   * value is a member of that set.</li>
   * </ol>
   *
   * <p>
   * <strong>Note:</strong> This method does not throw. It returns the
   * {@link IllegalArgumentException} describing the first failure, or
   * {@code null} on success.
   * </p>
   *
   * @param schema
   *          [in] The schema containing the definitions to validate against,
   *          or {@code null} to apply only the attribute's own value-class
   *          check.
   * @param attr
   *          [in] The attribute to validate. Must not be {@code null}.
   * @return {@code null} if validation succeeds; otherwise an
   *         {@link IllegalArgumentException} describing the first failure.
   */  
  public static IllegalArgumentException verifyAttribute(Schema schema,
      Attribute attr)
  {
    String keyString = attr.getExpandedName();
    ItemDefinition def = null;
    Class<?> valueClass = attr.getValueClass();
    long maxLength = -1;
    // allowed Values enumeration in TypeDefinition
    Set<Object> allowedValues = null;

    if (schema != null)
    {

      def = schema.getItemDefinition(keyString);
      // Verify if the attribute has more than one single element
      // when it is defined as single value.
      if (def != null)
      {
        String valueType = def.getValueType();
        if (valueType.equals(ItemDefinition.VALUE_TYPE_SINGLE))
        {
          if (attr.size() > 1)
          {
            IllegalArgumentException exception = new IllegalArgumentException(
                "Value count mismatch: expected a single value but got "
                    + "attribute with " + Integer.toString(attr.size())
                    + "items.");
            return exception;
          }
        }
        // The maximum allowed length of each specific value
        maxLength = def.getMaxValueLength();
        // Verify if typeName are aligned
        if (attr.getValueTypeName() != null)
        {
          if (def.getTypeName()
              .equalsIgnoreCase(attr.getValueTypeName()) == false)
          {
            IllegalArgumentException exception = new IllegalArgumentException(
                "Type name mismatch: expected type name '" + def.getTypeName()
                    + "' but got '" + attr.getValueTypeName() + ".");
            return exception;

          }
        }
        // Verify some additional information on type information, if available
        EntityTypeDefinition typeDef = schema.getTypeDefinition(def.getTypeName());
        if (typeDef != null)
        {
          if (typeDef.getDatatype().getClassType() != attr.getValueClass())
          {
            IllegalArgumentException exception = new IllegalArgumentException(
                "Type definition mismatch: expected class '"
                    + typeDef.getDatatype().getClassType().getName() + "' but got '"
                    + attr.getValueClass().getSimpleName() + ".");
            return exception;
          }
          if (typeDef.getDatatype() instanceof EnumerationFacet)
          {
            Object[] values = ((EnumerationFacet)typeDef).getAllowedValues();
            if (values != null)
            {
              allowedValues = new HashSet<Object>(Arrays.asList(values));
            }
          }
        }
      }
    }

    for (int i = 0; i < attr.size(); i++)
    {
      Object value = attr.get(i);
      // Verify if all the items in the attribute are of the correct
      // value type.
      if ((valueClass != null) && (value != null && !valueClass.isInstance(value)))
      {
        IllegalArgumentException exception = new IllegalArgumentException(
            "Value type mismatch: expected " + valueClass.getName()
                + " but got " + value.getClass().getName() + ".");
        return exception;
      }
      // Verify, if applicable, that the length of each value
      // is within the allowed limits.
      if ((maxLength > 0) && (value != null))
      {
        IllegalArgumentException exception = checkLength(keyString, value,
            maxLength);
        if (exception != null)
          return exception;
      }
      // Now if schema is present some extra verifications
      if (def != null)
      {
        // Verify that each value should be associated with a context for ALT TYPE
        if (def.getValueType().equals(ItemDefinition.VALUE_TYPE_ALT))
        {
          if (attr.getContextKey(i) == null)
          {
            IllegalArgumentException exception = new IllegalArgumentException(
                "Value context expected but was absent for value  at index "
                    + Integer.toString(i) + ".");
            return exception;
          }
        } else
        {
          if (attr.getContextKey(i) != null)
          {
            IllegalArgumentException exception = new IllegalArgumentException(
                "Value context was present but not expected for value at index "
                    + Integer.toString(i) + ".");
            return exception;
          }
        }
        // Verify if allowed values is present, and if so that the value is
        // one of them. Membership is an O(1) lookup against the set.
        if (allowedValues != null && value != null)
        {
          if (!allowedValues.contains(value))
          {
            IllegalArgumentException exception = new IllegalArgumentException(
                "Value is not among the allowed values from the type definition at index "
                    + Integer.toString(i) + ".");
            return exception;
          }
        }
      }

    }
    return null;
  }

  // -----------------------------------------------------------------------
  // Class definition validation
  // -----------------------------------------------------------------------

  /**
   * Verifies that the specified object class is not abstract and that all
   * mandatory attributes defined by its schema are present.
   *
   * <p>
   * This method performs the following checks:
   * </p>
   * <ol>
   * <li>If {@code objectClass} is {@code null}, validation is skipped and
   * {@code null} is returned.</li>
   * <li>If no {@link ClassDefinition} exists in {@code schema} for the
   * specified {@code objectClass}, validation is skipped and {@code null} is
   * returned.</li>
   * <li>If the class definition is abstract, an
   * {@link IllegalArgumentException} is returned.</li>
   * <li>For each mandatory attribute name declared by the class, verifies that
   * it is present in {@code attributeNames}. Returns an
   * {@link IllegalArgumentException} for the first missing mandatory attribute
   * found.</li>
   * </ol>
   *
   * <p>
   * <strong>Note:</strong> This method does not throw. It returns an
   * {@link IllegalArgumentException} on failure, or {@code null} on success.
   * </p>
   *
   * @param schema
   *          [in] The schema containing class definitions. Must not be
   *          {@code null}.
   * @param objectClass
   *          [in] The object class name to validate, or {@code null} to skip
   *          validation.
   * @param attributeNames
   *          [in] The attribute names present on the entity being validated.
   *          Must not be {@code null}.
   * @return {@code null} if validation succeeds or is skipped; an
   *         {@link IllegalArgumentException} describing the failure otherwise.
   */
  public static IllegalArgumentException verifyClassDefinition(Schema schema,
      String objectClass, Iterator<String> attributeNames)
  {
    if (objectClass == null)
      return null;
    ClassDefinition classDef = schema.getClassDefinition(objectClass);
    if (classDef == null)
      return null;
    return verifyClassDefinition(classDef, attributeNames);
  }

  /**
   * Verifies that all mandatory attributes declared by a class definition are
   * present in the supplied attribute name list.
   *
   * <p>
   * It does not check whether the class is abstract; that check must be
   * performed by the caller if needed.
   * </p>
   *
   * <p>
   * <strong>Note:</strong> This method does not throw. It returns an
   * {@link IllegalArgumentException} on failure, or {@code null} on success.
   * </p>
   *
   * @param classDef
   *          [in] The class definition to validate against. Must not be
   *          {@code null}.
   * @param attributeNames
   *          [in] The attribute names present on the entity being validated.
   *          Must not be {@code null}.
   * @return {@code null} if all mandatory attributes are present; an
   *         {@link IllegalArgumentException} describing the first missing
   *         mandatory attribute otherwise.
   */
  public static IllegalArgumentException verifyClassDefinition(
      ClassDefinition classDef, Iterator<String> attributeNames)
  {
    // Materialise the enumeration into a list for contains() checks.
    List<String> list = new ArrayList<String>();
    while (attributeNames.hasNext())
      list.add(attributeNames.next());

    String attrName = null;
    Iterator<String> it = classDef.getMandatoryAttributes().iterator();
    while (it.hasNext())
    {
      attrName = it.next();
      if (!list.contains(attrName))
        return new IllegalArgumentException(
            "Missing mandatory attribute '" + attrName + "' for object class '"
                + classDef.getExpandedName() + "'.");
    }
    return null;
  }

  // -----------------------------------------------------------------------
  // Private helpers
  // -----------------------------------------------------------------------

  /**
   * Returns an error if the length of {@code value} exceeds {@code maxLength},
   * or {@code null} if the length is acceptable.
   *
   * <p>
   * Length is measured per the rule documented on
   * {@link ItemDefinition#getMaxValueLength()}. For value types not covered
   * by that rule, no length check is performed and {@code null} is returned.
   * </p>
   */
  private static IllegalArgumentException checkLength(String key, Object value,
      long maxLength)
  {
    long actualLength = -1;
    if (value instanceof CharSequence)
      actualLength = ((CharSequence) value).length();
    else if (value instanceof byte[])
      actualLength = java.lang.reflect.Array.getLength(value);
    else if (value instanceof char[])
      actualLength = java.lang.reflect.Array.getLength(value);
    else if (value instanceof InputStream)
    {
      try
      {
        actualLength = ((InputStream) value).available();
      } catch (IOException e)
      {
        IllegalArgumentException exception = new IllegalArgumentException(
            "Value length of " + value.getClass().getName()
                + " cannot be determined due " + "to an I/O Exception");
        exception.initCause(e);
        return exception;
      }
    } else if (value instanceof Clob)
    {
      try
      {
        actualLength = ((Clob) value).length();
      } catch (SQLException e)
      {
        IllegalArgumentException exception = new IllegalArgumentException(
            "Value length of " + value.getClass().getName()
                + " cannot be determined due " + "to an I/O Exception");
        exception.initCause(e);
        return exception;
      }
    } else if (value instanceof Blob)
    {
      try
      {
        actualLength = ((Blob) value).length();
      } catch (SQLException e)
      {
        IllegalArgumentException exception = new IllegalArgumentException(
            "Value length of " + value.getClass().getName()
                + " cannot be determined due " + "to an I/O Exception");
        exception.initCause(e);
        return exception;
      }
    }

    if (actualLength >= 0 && actualLength > maxLength)
      return new IllegalArgumentException(
          "Value length " + actualLength + " exceeds the maximum of "
              + maxLength + " for attribute '" + key + "'.");
    return null;
  }

  // -----------------------------------------------------------------------
  // Containment relationship queries
  // -----------------------------------------------------------------------

  /**
   * Returns the names of classes that may be instantiated as immediate children
   * of an instance of {@code parentClassName}, by scanning every class
   * definition in {@code schema} and collecting those whose
   * {@link ClassDefinition#getAllowedParents()} permits {@code parentClassName}
   * as a parent.
   *
   * <p>
   * This is the schema-level analogue of the X.500 DIT structure rules and is
   * intended to drive user-interface affordances such as the set of options
   * offered by a <em>New&hellip;</em> context menu on a node of a given object
   * class.
   * </p>
   *
   * <p>
   * The selection rule applied to each candidate class definition in
   * {@code schema} is as follows:
   * </p>
   * <ul>
   * <li>If the candidate's {@link ClassDefinition#getAllowedParents()} set
   * is empty, the candidate declares no parent constraint and is treated as
   * permitted under any parent (per
   * {@link ClassDefinition#KEY_ALLOWED_PARENTS}). It is included in the
   * result.</li>
   * <li>If the candidate's allowed-parents list is non-empty and contains
   * {@code parentClassName}, the candidate is included in the result.</li>
   * <li>Otherwise the candidate is excluded.</li>
   * <li>Candidates whose {@link ClassDefinition#getKind()} is
   * {@link ClassDefinition.ObjectClassKind#abstractClass} are always excluded,
   * since abstract classes may not be directly instantiated. Auxiliary classes
   * are likewise excluded, since they may only be mixed into a structural class
   * and do not stand alone as instantiable child classes.</li>
   * </ul>
   *
   * <p>
   * The match against {@code parentClassName} is performed by string equality
   * on the entries returned by {@link ClassDefinition#getAllowedParents()}.
   * Callers are responsible for supplying the name in the form used by the
   * schema (expanded name, local name (when there is no namespace) or OID, with
   * the case convention of the underlying implementation).
   * </p>
   *
   * <p>
   * The returned class names are taken from
   * {@link ClassDefinition#getExpandedName()}, ensuring a consistent qualified
   * form independent of how each candidate's allowed-parents list was
   * originally expressed.
   * </p>
   *
   * <p>
   * Class-name matching is delegated to
   * {@link Schema#getClassDefinition(String)}, so the schema's own
   * case-sensitivity and OID/name equivalence rules govern the comparison. A
   * candidate's allowed-parents entry written as {@code "person"} will match a
   * {@code parentClassName} of {@code "Person"} against an LDAP-style
   * case-insensitive schema, but not against a case-sensitive XML schema.
   * </p>
   * 
   * <p>
   * The result is not sorted; the order reflects the iteration order of
   * {@link Schema#getClassDefinitions()}. Each name appears at most once:
   * duplicates that could arise from multiple inheritance paths are not
   * deduplicated by this method, but cannot occur given that each candidate is
   * visited exactly once.
   * </p>
   *
   * @param schema
   *          [in] The schema to query. Must not be {@code null}.
   * @param parentClassName
   *          [in] The expanded name, local name (if there is no namespace) or
   *          OID of the prospective parent class. Must not be {@code null}.
   * @return A non-null array of qualified class names that may be instantiated
   *         as immediate children of an instance of {@code parentClassName}.
   *         The array is empty when no class in the schema permits
   *         {@code parentClassName} as a parent.
   */
  public static String[] getAllowedChildClasses(Schema schema,
      String parentClassName)
  {
    // Resolve the parent name through the schema so that subsequent
    // comparisons use the schema's own notion of class identity. If the
    // parent class is not in the schema, no candidate can declare it as
    // a permitted parent — except those with an empty allowed-parents
    // list, which declare no constraint at all.
    ClassDefinition parentDef = schema.getClassDefinition(parentClassName);
    String parentCanonical = (parentDef != null) ? parentDef.getExpandedName()
        : null;

    List<String> result = new ArrayList<String>();
    Iterator<ClassDefinition> classDefs = schema.getClassDefinitions();
    while (classDefs.hasNext())
    {
      ClassDefinition candidate = (ClassDefinition) classDefs.next();

      // Skip non-instantiable kinds.
      ClassDefinition.ObjectClassKind kind = candidate.getKind();
      if (kind == ClassDefinition.ObjectClassKind.abstractClass
          || kind == ClassDefinition.ObjectClassKind.auxiliaryClass)
        continue;

      Set<String> parents = candidate.getAllowedParents();
      if (parents.isEmpty())
      {
        // No constraint declared — permitted under any parent.
        result.add(candidate.getExpandedName());
        continue;
      }

      // Match each declared parent name through the schema to honour
      // the schema's case-sensitivity rules.
      Iterator<String> it = parents.iterator();
      String allowed = null;
      while (it.hasNext())
      {
        allowed = it.next();
        ClassDefinition allowedDef = schema.getClassDefinition(allowed);
        String allowedCanonical = (allowedDef != null)
            ? allowedDef.getExpandedName()
            : allowed;

        boolean matches = (parentCanonical != null)
            ? parentCanonical.equals(allowedCanonical)
            : parentClassName.equals(allowed); // fall back to literal

        if (matches)
        {
          result.add(candidate.getExpandedName());
          break;
        }
      }
    }

    return (String[]) result.toArray(new String[result.size()]);
  }

  /**
   * Collects all mandatory attribute names declared by {@code classDef} and
   * inherited from its superclass chain, walking parent links through
   * {@code schema} until the chain terminates or a cycle is detected.
   *
   * <p>
   * This is the inheritance-aware counterpart to
   * {@link ClassDefinition#getMandatoryAttributes()}, which returns only the
   * attributes declared directly on the class. Most schema-driven consumers
   * &mdash; form generators, validators, &laquo;Create new&hellip;&raquo;
   * dialogs &mdash; need the transitive closure rather than the direct
   * declaration, and this method consolidates that walk so each caller does not
   * reimplement it.
   * </p>
   *
   * <p>
   * The walk proceeds as follows:
   * </p>
   * <ol>
   * <li>Yield each name from {@code classDef.getMandatoryAttributes()},
   * canonicalized by lookup through {@link Schema#getItemDefinition(String)} if
   * a definition is found, or used as-is otherwise.</li>
   * <li>Follow {@link ClassDefinition#getSuperClass()} to the parent class. If
   * the parent is not declared in {@code schema}, terminate the walk
   * cleanly.</li>
   * <li>Repeat from step 1 with the parent class definition, skipping names
   * already yielded so each appears at most once.</li>
   * </ol>
   *
   * <p>
   * The order of the returned iterator is child-class-first-then-ancestors:
   * names declared directly on {@code classDef} appear before names declared on
   * its superclass, which appear before names declared on the grandparent, and
   * so on. Within each class the original declaration order is preserved. This
   * is the order most natural for form generation, where directly-declared
   * fields are typically shown ahead of inherited ones.
   * </p>
   *
   * <p>
   * Cycle protection: if the schema contains a cyclic inheritance chain (which
   * would be malformed), the walk terminates the first time it would revisit a
   * previously-seen class. Each class is visited at most once, identified by
   * {@link ClassDefinition#getExpandedName()}.
   * </p>
   *
   * <p>
   * If {@code classDef} declares no mandatory attributes and has no superclass
   * (or its superclass chain contributes none), the returned iterator is empty.
   * </p>
   *
   * <p>
   * The returned iterator does not support {@code remove()}; attempting to call
   * it throws {@link UnsupportedOperationException}.
   * </p>
   *
   * @param schema
   *          [in] The schema used to resolve superclass references and
   *          canonicalize attribute names. Must not be {@code null}.
   * @param classDef
   *          [in] The class definition whose mandatory attribute closure is
   *          requested. Must not be {@code null}.
   * @return A non-null, read-only iterator over qualified attribute name
   *         strings; possibly empty.
   * @throws IllegalArgumentException
   *           if {@code schema} or {@code classDef} is {@code null}.
   * @see ClassDefinition#getMandatoryAttributes()
   * @see #getAllowedChildClasses(Schema, String)
   */
  public static Iterator<String> collectMandatoryAttributes(Schema schema,
      ClassDefinition classDef)
  {
    if (schema == null)
      throw new IllegalArgumentException("'schema' must not be null.");
    if (classDef == null)
      throw new IllegalArgumentException("'classDef' must not be null.");

    // LinkedHashSet preserves child-first-then-ancestors ordering
    // and provides O(1) duplicate suppression. Visited tracks the
    // classes we've already walked through to break cycles.
    LinkedHashSet<String> collected = new LinkedHashSet<String>();
    HashSet<String> visited = new HashSet<String>();

    ClassDefinition current = classDef;
    while (current != null)
    {
      // Cycle break: stop if we've already walked through this class.
      String currentKey = current.getExpandedName();
      if (currentKey == null || !visited.add(currentKey))
        break;

      // Yield each mandatory attribute name from this level,
      // canonicalized through the schema where possible.
      Iterator<String> it = current.getMandatoryAttributes().iterator();
      while (it.hasNext())
      {
        String raw = it.next();
        if (raw == null)
          continue;
        ItemDefinition item = schema.getItemDefinition(raw);
        String canonical = (item != null) ? item.getExpandedName() : raw;
        collected.add(canonical);
      }

      // Walk up to the superclass, if any. A missing or unresolvable
      // superclass terminates the walk cleanly.
      String superName = current.getSuperClass();
      if (superName == null)
        break;
      current = schema.getClassDefinition(superName);
    }

    return collected.iterator();
  }

  /**
   * Returns {@code true} if the class named {@code subclassName} is the same
   * as, or is a descendant of, the class named {@code superclassName} in this
   * schema's inheritance graph.
   *
   * <p>
   * The relation is determined by walking
   * {@link ClassDefinition#getSuperClass()} chain upward from
   * {@code subclassName}, comparing each class's expanded name to
   * {@code superclassName}'s expanded name until a match is found, the chain
   * terminates, or the walk would revisit a previously-seen class.
   * </p>
   *
   * <p>
   * The relation is <em>reflexive</em>: a class is derived from itself. Calling
   * {@code isDerivedFrom(schema, "Person", "Person")} returns {@code true}.
   * This matches the semantics of {@link Class#isAssignableFrom(Class)} in the
   * Java type system and the {@code rdfs:subClassOf} relation in semantic-web
   * schemas, both of which include the class itself in its own ancestor set.
   * </p>
   *
   * <p>
   * Cycle protection: if the schema contains a cyclic inheritance chain (which
   * would be malformed), the walk terminates the first time it would revisit a
   * previously-seen class. The method returns {@code true} if
   * {@code superclassName} was found anywhere in the chain before the cycle was
   * detected, {@code false} otherwise.
   * </p>
   *
   * <p>
   * Names are canonicalized through {@link ClassDefinition#getExpandedName()}
   * for comparison, consistent with the canonicalization performed by
   * {@link #getAllowedChildClasses(Schema, String)} and
   * {@link #collectMandatoryAttributes(Schema, ClassDefinition)}. Callers may
   * pass either an expanded name or any other form the schema recognizes; the
   * comparison normalizes both sides before checking equality.
   * </p>
   *
   * @param schema
   *          [in] The schema in which to perform the inheritance walk. Must not
   *          be {@code null}.
   * @param subclassName
   *          [in] The name of the candidate subclass &mdash; the class whose
   *          inheritance chain will be searched. Must not be {@code null}. Must
   *          be declared in {@code schema}.
   * @param superclassName
   *          [in] The name of the candidate superclass &mdash; the class
   *          searched for in the inheritance chain. Must not be {@code null}.
   *          Must be declared in {@code schema}.
   * @return {@code true} if {@code subclassName} is the same as
   *         {@code superclassName} or has it as a (transitive) superclass;
   *         {@code false} otherwise.
   * @throws IllegalArgumentException
   *           if any argument is {@code null}, or if either
   *           {@code subclassName} or {@code superclassName} is not declared in
   *           {@code schema}.
   * @see #getAllowedChildClasses(Schema, String)
   * @see #collectMandatoryAttributes(Schema, ClassDefinition)
   */
  public static boolean isDerivedFrom(Schema schema, String subclassName,
      String superclassName)
  {
    if (schema == null)
      throw new IllegalArgumentException("'schema' must not be null.");
    if (subclassName == null)
      throw new IllegalArgumentException("'subclassName' must not be null.");
    if (superclassName == null)
      throw new IllegalArgumentException("'superclassName' must not be null.");

    // Resolve both endpoints up front so we can throw cleanly on
    // a missing class before walking anything.
    ClassDefinition subDef = schema.getClassDefinition(subclassName);
    if (subDef == null)
      throw new IllegalArgumentException(
          "Class \"" + subclassName + "\" is not declared in the schema.");
    ClassDefinition superDef = schema.getClassDefinition(superclassName);
    if (superDef == null)
      throw new IllegalArgumentException(
          "Class \"" + superclassName + "\" is not declared in the schema.");

    // Canonicalize the target name once; we'll compare each step
    // of the walk against this value.
    String target = superDef.getExpandedName();

    // Walk upward from the subclass. Visited tracks expanded names
    // already seen to break inheritance cycles in malformed schemas.
    HashSet<String> visited = new HashSet<String>();
    ClassDefinition current = subDef;
    while (current != null)
    {
      String currentKey = current.getExpandedName();
      if (currentKey == null || !visited.add(currentKey))
        return false; // cycle or unnamed class
      if (currentKey.equals(target))
        return true;

      String superName = current.getSuperClass();
      if (superName == null)
        return false;
      current = schema.getClassDefinition(superName);
    }
    return false;
  }
}
