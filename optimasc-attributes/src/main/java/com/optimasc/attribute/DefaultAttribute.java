package com.optimasc.attribute;

import java.util.ArrayList;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;


/** Mutable, in-memory implementation of {@link Attribute}.
 *
 *  <p>When {@link #getContextType()} returns {@code null} the instance is
 *  not context-aware: {@link #getContextKey(int)}, {@link #getByContext(String)}
 *  and {@link #getDefaultContextKey()} all return {@code null}, and any
 *  attempt to supply a non-null context key to a mutating method throws
 *  {@link IllegalStateException}.</p>
 *
 *  <p>When {@link #getContextType()} is non-null every added value
 *  <em>must</em> carry a non-null, unique context key; duplicate context
 *  keys are rejected with {@link IllegalStateException}.</p>
 *
 *  <p>{@link #getValueClass()} is mandatory and never {@code null}; use
 *  {@code Object.class} for untyped attributes. It is used to verify
 *  the validity of objects added to this attribute.</p>
 *
 *  @author Carl Eric Codere
 */
public class DefaultAttribute implements Attribute
{
  // -----------------------------------------------------------------------
  // Identity fields
  // -----------------------------------------------------------------------

  /** OID e.g. {@code "2.5.4.3"}, or {@code null}. */
  protected String oid;
  /** Namespace IRI, or {@code null} for unnamespaced attributes. */
  protected String namespaceURI;
  /** Local name. Never {@code null}. */
  protected String localName;

  // -----------------------------------------------------------------------
  // Type fields
  // -----------------------------------------------------------------------

  /** Java class of each value. Never {@code null}. */
  protected Class<?> valueClass;
  /** Semantic type name / OID / XSD IRI, or {@code null}. */
  protected String valueTypeName;

  // -----------------------------------------------------------------------
  // Value ordering and context fields
  // -----------------------------------------------------------------------

  protected boolean isOrdered;

  /** Context type label e.g. {@code "Lang"}, or {@code null}. */
  protected String contextType;

  // -----------------------------------------------------------------------
  // Internal storage
  // -----------------------------------------------------------------------

  /** Single list of value+contextKey pairs. Never {@code null}. */
  protected List<AttributeValue> values;

  // -----------------------------------------------------------------------
  // Inner storage class
  // -----------------------------------------------------------------------

  protected static class AttributeValue
  {
    final Object value;
    /** {@code null} when the owning attribute is not context-aware. */
    final String contextKey;
    /** {@code true} if this value is the designated default. Always
     *  {@code false} for non-context-aware attributes. */
    boolean isDefault;

    AttributeValue(Object value, String contextKey, boolean isDefault)
    {
      this.value      = value;
      this.contextKey = contextKey;
      this.isDefault  = isDefault;
    }
  }  

  // -----------------------------------------------------------------------
  // Inner iterator class
  // -----------------------------------------------------------------------

  private final class ValueIterator implements Iterator<Object>
  {
    private int cursor = 0;

    public boolean hasNext()
    {
      return cursor < values.size();
    }

    public Object next()
    {
      if (!hasNext())
        throw new NoSuchElementException();
      return ((AttributeValue) values.get(cursor++)).value;
    }

    /** Not supported — attribute iterators are read-only. */
    public void remove()
    {
      throw new UnsupportedOperationException(
          "Remove via iterator is not supported; use DefaultAttribute.remove().");
    }
  }

  // -----------------------------------------------------------------------
  // Constructors
  // -----------------------------------------------------------------------

  /** Full constructor.
  *
  * @param id            OID, or {@code null}.
  * @param namespaceURI  Namespace IRI, or {@code null}.
  * @param name          Local name. Must not be {@code null}.
  * @param valueClass    Java class of values. Must not be {@code null};
  *                      use {@code Object.class} for untyped attributes.
  * @param valueTypeName Semantic type name, or {@code null}.
  * @param ordered       Whether value order is significant.
  * @param contextType   Context type label e.g. {@code "Lang"},
  *                      or {@code null} if not context-aware.
  * @throws IllegalArgumentException Thrown if name is {@code null},
  *   or valueClass is {@code null} or the namespaceURI is not null
  *   and does not end with the '/' or '#' character.
  */
 public DefaultAttribute(String id, String namespaceURI, String name,
     Class<?> valueClass, String valueTypeName,
     boolean ordered, String contextType)
 {
   if (name == null)
     throw new IllegalArgumentException(
         "'name' is mandatory and must not be null.");
   if (valueClass == null)
     throw new IllegalArgumentException(
         "'valueClass' is mandatory; use Object.class for untyped attributes.");

   if (namespaceURI != null && namespaceURI.length() > 0)
   {
       char last = namespaceURI.charAt(namespaceURI.length() - 1);
       if (last != '/' && last != '#')
           throw new IllegalArgumentException(
               "Namespace URI must end with '/' or '#' to allow unambiguous "
               + "expanded-name construction, but got: '" + namespaceURI + "'.");
   }
   this.oid           = id;
   this.namespaceURI  = namespaceURI;
   this.localName     = name;
   this.valueClass    = valueClass;
   this.valueTypeName = valueTypeName;
   this.isOrdered     = ordered;
   this.contextType   = contextType;
   this.values        = new ArrayList<AttributeValue>();
 }  
  
  
  /** Convenience constructor for a simple unnamespaced, unordered,
   *  non-context-aware attribute with a specified value.
   *  
   *  <p>The valueClass will be taken from the class of {@code value}.
   *  This method is a way to construct an attribute that will only
   *  contain a single specific value.
   *  </p>
   *
   * @param name        Local name. Must not be {@code null}.
   * @param value       The value to add. Must not be {@code null}.
   */
  public DefaultAttribute(String name, Object value)
  {
    this(null, null, name, value.getClass(), null, false, null);    
    add(value);
  }


  /** Convenience constructor for a simple unnamespaced, unordered,
   *  non-context-aware attribute.
   *
   * @param name        Local name. Must not be {@code null}.
   * @param valueClass  Java class of values. Must not be {@code null}.
   */
  public DefaultAttribute(String name, Class<?> valueClass)
  {
    this(null, null, name, valueClass, null, false, null);    
  }

  /** Convenience constructor for a namespaced, unordered,
   *  non-context-aware attribute.
   *
   * @param namespaceURI  Namespace IRI, or {@code null}.
   * @param name          Local name. Must not be {@code null}.
   * @param valueClass    Java class of values. Must not be {@code null}.
   */
  public DefaultAttribute(String namespaceURI, String name, Class<?> valueClass)
  {
    this(null, namespaceURI, name, valueClass, null, false, null);
  }

  // -----------------------------------------------------------------------
  // Attribute — identity
  // -----------------------------------------------------------------------

  /** Returns the OID e.g. {@code "2.5.4.3"}, or {@code null}. */
  public String getOID()
  {
    return oid;
  }

  /** Returns the local name. Never {@code null}. */
  public String getLocalName()
  {
    return localName;
  }

  /** Returns the namespace IRI, or {@code null}. */
  public String getNamespaceURI()
  {
    return namespaceURI;
  }

  /** Returns the expanded name in RDF IRI format:
   *  {@code namespaceURI + name} when a namespace is present,
   *  otherwise just {@code name}.
   */
  public String getExpandedName()
  {
    if (namespaceURI != null)
      return namespaceURI + localName;
    return localName;
  }

  // -----------------------------------------------------------------------
  // Attribute — type
  // -----------------------------------------------------------------------

  /** Returns the Java class of each value. Never {@code null}. */
  public Class<?> getValueClass()
  {
    return valueClass;
  }

  /** Returns the semantic type name / OID / XSD IRI, or {@code null}. */
  public String getValueTypeName()
  {
    return valueTypeName;
  }

  // -----------------------------------------------------------------------
  // Attribute — collection
  // -----------------------------------------------------------------------

  public int size()
  {
    return values.size();
  }

  public boolean isEmpty()
  {
    return values.isEmpty();
  }

  /** Returns the value at {@code index}, unwrapped from its
   *  {@link AttributeValue} holder.
   *
   * @throws IndexOutOfBoundsException if {@code index} is out of range.
   */
  public Object get(int index)
  {
    return ((AttributeValue) values.get(index)).value;
  }

  /** Returns {@code true} if any value in this attribute equals
   *  {@code value} (using {@link Object#equals}, null-safe).
   */
  public boolean contains(Object value)
  {
    int size = values.size();
    for (int i = 0; i < size; i++)
    {
      Object v = ((AttributeValue) values.get(i)).value;
      if ((value == null) && (v == null))
        return true;
      if (value.equals(v))
        return true;
    }
    return false;
  }

  public boolean isOrdered()
  {
    return isOrdered;
  }

  /** Returns a fresh {@link Iterator} over the attribute's values.
   *  Each call returns an independent iterator positioned at the first
   *  value. Values are unwrapped from their {@link AttributeValue} holders.
   *  The iterator does not support {@code remove()}.
   */
  public Iterator<Object> iterator()
  {
    return new ValueIterator();
  }

  // -----------------------------------------------------------------------
  // Contexts
  // -----------------------------------------------------------------------

  /** Returns the context type label e.g. {@code "Lang"},
   *  or {@code null} if this attribute is not context-aware.
   */
  public String getContextType()
  {
    return contextType;
  }

  /** Returns the context key for the value at {@code index},
   *  or {@code null} if this attribute is not context-aware.
   *
   * @throws IndexOutOfBoundsException if {@code index} is out of range.
   */
  public String getContextKey(int index)
  {
    if (contextType == null)
      return null;
    return ((AttributeValue) values.get(index)).contextKey;
  }

  /** Returns the first value whose context key equals {@code contextKey},
   *  or {@code null} if not found or this attribute is not context-aware.
   */
  public Object getByContext(String contextKey)
  {
    if (contextType == null)
      return null;
    int size = values.size();
    for (int i = 0; i < size; i++)
    {
      AttributeValue av = (AttributeValue) values.get(i);
      if (contextKey == null ? av.contextKey == null
                             : contextKey.equals(av.contextKey))
        return av.value;
    }
    return null;
  }

  
  /** Returns the context key of the value designated as default, or
   *  {@code null} if no default has been designated or this attribute is
   *  not context-aware. */
  public String getDefaultContextKey()
  {
    if (contextType == null)
      return null;
    int size = values.size();
    for (int i = 0; i < size; i++)
    {
      AttributeValue av = (AttributeValue) values.get(i);
      if (av.isDefault)
        return av.contextKey;
    }
    return null;
  }  

  /** Designates the value with the given context key as the default,
   *  clearing any previously designated default. Passing {@code null}
   *  clears the default without designating a new one.
   *
   * @param contextKey  The context key to designate as default, or
   *                    {@code null} to clear the current default.
   * @throws IllegalStateException    if this attribute is not context-aware.
   * @throws IllegalArgumentException if {@code contextKey} is non-null but
   *                                  no value carries that key.
   */
  public void markDefault(String contextKey)
  {
    if (contextType == null)
      throw new IllegalStateException(
          "Cannot designate a default on a non-context-aware attribute.");
    int size = values.size();
    AttributeValue target = null;
    if (contextKey != null)
    {
      for (int i = 0; i < size; i++)
      {
        AttributeValue av = (AttributeValue) values.get(i);
        if (contextKey.equals(av.contextKey))
        {
          target = av;
          break;
        }
      }
      if (target == null)
        throw new IllegalArgumentException(
            "No value carries context key '" + contextKey + "'.");
    }
    for (int i = 0; i < size; i++)
      ((AttributeValue) values.get(i)).isDefault = false;
    if (target != null)
      target.isDefault = true;
  }  
  // -----------------------------------------------------------------------
  // Mutation — on the concrete class only, not on any interface
  // -----------------------------------------------------------------------

  /** Appends a value with an associated context key.
   *
   * @param value       Value to add. Must be assignment-compatible with
   *                    {@link #getValueClass()}.
   * @param contextKey  Context key for this value, or {@code null}.
   *                    Must be {@code null} for non-context-aware attributes.
   *                    Must be non-null and unique for context-aware attributes.
   * @return {@code true} always, consistent with {@link java.util.Collection}.
   * @throws IllegalArgumentException if {@code value} is incompatible with
   *                                  {@link #getValueClass()}.
   * @throws IllegalStateException    if a non-null {@code contextKey} is
   *                                  supplied for a non-context-aware attribute,
   *                                  or if the context key is already present.
   * @throws IllegalArgumentException if {@code contextKey} is {@code null}
   *                                  for a context-aware attribute.
   */
  public boolean add(Object value, String contextKey)
  {
    validateValue(value);
    validateContextKey(contextKey, -1);
    values.add(new AttributeValue(value, contextKey, false));
    return true;
  }

  /** Appends a value without a context key.
   *  Equivalent to {@code add(value, null)}.
   *  
   *  @param value [in] The value to append
   *
   * @throws IllegalStateException if this attribute is context-aware
   *                               (a context key is mandatory in that case).
   * @throws IllegalArgumentException if {@code value} is incompatible with
   *                                  {@link #getValueClass()}.
   *                               
   */
  public boolean add(Object value)
  {
    return add(value, null);
  }

  /** Inserts a value at the specified index.
   *
   * @param index       Position at which to insert (0 &lt;= index &lt;= size()).
   * @param value       Value to insert.
   * @param contextKey  Context key, subject to the same rules as
   *                    {@link #add(Object, String)}.
   * @throws IndexOutOfBoundsException if {@code index} is out of range.
   * @throws IllegalArgumentException if {@code value} is incompatible with
   *                                  {@link #getValueClass()}.
   * @throws IllegalStateException    if a non-null {@code contextKey} is
   *                                  supplied for a non-context-aware attribute,
   *                                  or if the context key is already present.
   * @throws IllegalArgumentException if {@code contextKey} is {@code null}
   *                                  for a context-aware attribute.
   */
  public void add(int index, Object value, String contextKey)
  {
    validateValue(value);
    validateContextKey(contextKey, -1);
    values.add(index, new AttributeValue(value, contextKey, false));    
  }

  /** Replaces the value and context key at the specified index. The
   *  default key indicator is kept.
  *
  * @param index       Position to replace (0 &lt;= index &lt; size()).
  * @param value       New value.
  * @param contextKey  New context key, subject to the same rules as
  *                    {@link #add(Object, String)}.
  *                    The duplicate-key check excludes the entry being
  *                    replaced.
  * @return The previous value at {@code index}.
  * @throws IllegalArgumentException if {@code value} is incompatible with
  *                                  {@link #getValueClass()}.
  * @throws IllegalStateException    if a non-null {@code contextKey} is
  *                                  supplied for a non-context-aware attribute,
  *                                  or if the context key is already present.
  * @throws IllegalArgumentException if {@code contextKey} is {@code null}
  *                                  for a context-aware attribute.
  * @throws IndexOutOfBoundsException if {@code index} is out of range.
  */
 public Object set(int index, Object value, String contextKey)
 {
   validateValue(value);
   validateContextKey(contextKey, index);
   AttributeValue old = (AttributeValue) values.get(index);
   values.set(index, new AttributeValue(value, contextKey, old.isDefault));
   return old.value;
 }  

  /** Removes the first occurrence of {@code value} (and its context key).
   *
   * @return {@code true} if a value was removed.
   */
  public boolean remove(Object value)
  {
    int size = values.size();
    for (int i = 0; i < size; i++)
    {
      Object v = ((AttributeValue) values.get(i)).value;
      if (value == null ? v == null : value.equals(v))
      {
        values.remove(i);
        return true;
      }
    }
    return false;
  }

  /** Removes the value and context key at the specified index.
   *
   * @return The removed value.
   * @throws IndexOutOfBoundsException if {@code index} is out of range.
   */
  public Object remove(int index)
  {
    return ((AttributeValue) values.remove(index)).value;
  }

  /** Removes all values and context keys from this attribute. */
  public void clear()
  {
    values.clear();
  }

  // -----------------------------------------------------------------------
  // Object overrides
  // -----------------------------------------------------------------------

  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    sb.append(getExpandedName());
    sb.append(": ");
    int size = values.size();
    if (size == 0)
    {
      sb.append("(no values)");
      return sb.toString();
    }
    for (int i = 0; i < size; i++)
    {
      if (i > 0)
        sb.append(", ");
      AttributeValue av = (AttributeValue) values.get(i);
      if (av.contextKey != null)
      {
        sb.append(av.contextKey);
        sb.append('=');
      }
      sb.append(av.value != null ? av.value.toString() : "null");
    }
    return sb.toString();
  }

  // -----------------------------------------------------------------------
  // Private validation helpers
  // -----------------------------------------------------------------------

  /** Validates that {@code value} is assignment-compatible with
   *  {@link #valueClass}. Null values are always permitted.
   */
  private void validateValue(Object value)
  {
    if (value != null && !valueClass.isInstance(value))
      throw new IllegalArgumentException(
          "Value type mismatch: expected " + valueClass.getName()
          + " but got " + value.getClass().getName() + ".");
  }

  /** Validates a context key against the context-awareness rules.
   *
   * @param contextKey    Key to validate.
   * @param excludeIndex  Index of the existing entry to exclude from the
   *                      duplicate check (used by {@link #set}), or -1.
   */
  private void validateContextKey(String contextKey, int excludeIndex)
  {
    if (contextType == null)
    {
      if (contextKey != null)
        throw new IllegalStateException(
            "Cannot supply a context key to a non-context-aware attribute.");
    }
    else
    {
      if (contextKey == null)
        throw new IllegalArgumentException(
            "A context key is mandatory for context-aware attributes "
            + "(contextType='" + contextType + "').");
      int size = values.size();
      for (int i = 0; i < size; i++)
      {
        if (i == excludeIndex)
          continue;
        if (contextKey.equals(((AttributeValue) values.get(i)).contextKey))
          throw new IllegalStateException(
              "Context key '" + contextKey + "' is already present.");
      }
    }
  }
  
  
  /** Two {@code Attribute} instances are equal if and only if
   *  they have the same expanded name, the same value class, and the same
   *  ordered sequence of values (compared using {@link Object#equals},
   *  null-safe). OID, type name, ordering flag, context type, and default
   *  context value are not considered for equality.
   */
  public boolean equals(Object obj)
  {
      if (this == obj)
          return true;
      if (!(obj instanceof Attribute))
          return false;
      Attribute other = (Attribute) obj;
      // Expanded name must match
      if (!getExpandedName().equals(other.getExpandedName()))
          return false;
      // Value class must match
      if (!valueClass.equals(other.getValueClass()))
          return false;
      // Value count must match
      if (size() != other.size())
          return false;
      // Each value must match in order
      int size = size();
      for (int i = 0; i < size; i++)
      {
          Object va = get(i);
          Object vb = other.get(i);
          if (va == null ? vb != null : !va.equals(vb))
              return false;
      }
      return true;
  }

  public int hashCode()
  {
      int result = getExpandedName().hashCode();
      result = 31 * result + valueClass.hashCode();
      int size = size();
      for (int i = 0; i < size; i++)
      {
          Object v = get(i);
          result = 31 * result + (v != null ? v.hashCode() : 0);
      }
      return result;
  }
  
  /** {@inheritDoc}
  *
  *  <p>The array is allocated via {@link java.lang.reflect.Array#newInstance}
  *  using {@link #getValueClass()} as the component type, so the cast
  *  at the call site is always safe.</p>
  */
 public Object[] toArray()
 {
     int size = values.size();
     Object[] result = (Object[]) java.lang.reflect.Array.newInstance(
             valueClass, size);
     for (int i = 0; i < size; i++)
         result[i] = ((AttributeValue) values.get(i)).value;
     return result;
 }
}
