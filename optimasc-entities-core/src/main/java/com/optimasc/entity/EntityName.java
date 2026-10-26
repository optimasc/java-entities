package com.optimasc.entity;

import java.util.Iterator;
import java.util.NoSuchElementException;

/** An immutable, ordered sequence of path components naming an
 *  {@link Entity} within a {@link Namespace}.
 *
 *  <p>An {@code EntityName} represents the structural <em>path</em>
 *  portion of a name-in-namespace: an ordered list of resolvable
 *  components, from the root-most component at index {@code 0} to
 *  the leaf-most at index {@code size() - 1}. Each component is a
 *  string that the parent entity at that level would recognize as
 *  identifying one of its children.</p>
 *
 *  <p>Syntax-specific elements &mdash; scheme, authority, drive
 *  letter, separator character, escaping rules, LDAP component
 *  order, Unicode normalization &mdash; are the responsibility of
 *  the {@link EntityNameParser} that produced the name. Two
 *  {@code EntityName} instances are equal if and only if their
 *  components are pairwise equal as strings (using
 *  {@link String#equals}); the parser is responsible for
 *  performing whatever normalization is required to make string
 *  equality meaningful within its syntax. By the time a caller
 *  holds an {@code EntityName}, its components are already in
 *  normalized form.</p>
 *
 *  <p>{@code EntityName} is a pure value type: it has no identity
 *  beyond its components, instances are immutable, and equality is
 *  defined structurally on the component sequence. This class is
 *  declared {@code final} so that the immutability and equality
 *  contracts cannot be subverted by subclassing; adapters that
 *  need to carry additional metadata alongside a name should hold
 *  an {@code EntityName} as a field rather than extend it.</p>
 *
 *  <p>The empty name (one with {@link #size()} equal to zero, also
 *  available as the {@link #EMPTY} constant) represents the root
 *  of a namespace. Resolving the empty name through
 *  {@link Namespace#resolve(EntityName)} yields the root entity.
 *  The empty name has no parent and no last component; calling
 *  {@link #getParent()} or {@link #getLastComponent()} on it
 *  throws {@link IndexOutOfBoundsException}.</p>
 *
 *  <p>All derivation methods ({@link #getPrefix},
 *  {@link #getSuffix}, {@link #getParent}) return new
 *  {@code EntityName} instances and never mutate the receiver.
 *  For efficiency, derived names may share the backing component
 *  array with their source; this sharing is invisible to callers
 *  and does not affect equality, hashing, or thread-safety.</p>
 *
 *  <p>Instances of this class are safe for concurrent use by
 *  multiple threads without external synchronization.</p>
 *
 *  @see EntityNameParser
 *  @see Namespace
 *  @author Carl Eric Codere
 */
public final class EntityName
{
  // -----------------------------------------------------------------------
  // Constants
  // -----------------------------------------------------------------------

  /** The empty (root) name. Has {@link #size()} equal to zero and
   *  represents the root of a namespace. Resolving this name
   *  through {@link Namespace#resolve(EntityName)} yields the
   *  root entity. */
  public static final EntityName EMPTY = new EntityName(new String[0], 0, 0);

  // -----------------------------------------------------------------------
  // Fields
  // -----------------------------------------------------------------------

  /** Backing array of components. May be shared with derived
   *  names produced by {@link #getPrefix} or {@link #getSuffix};
   *  callers must never see this reference. */
  private final String[] components;

  /** Offset of the first logical component within
   *  {@link #components}. */
  private final int offset;

  /** Number of logical components (the value returned by
   *  {@link #size}). */
  private final int length;

  /** Cached hash code; {@code 0} means "not yet computed". A real
   *  zero hash is harmless &mdash; the only cost is recomputation
   *  on subsequent calls. */
  private int hash;

  // -----------------------------------------------------------------------
  // Construction
  // -----------------------------------------------------------------------

  /** Creates a name from an array of components.
   *
   *  <p>The array is defensively copied; callers may modify it
   *  after construction without affecting the resulting name.
   *  Components are taken in the order given: index {@code 0} is
   *  the root-most component and index {@code components.length - 1}
   *  is the leaf-most.</p>
   *
   *  <p>Empty-string components are permitted by this constructor
   *  if the chosen syntax allows them; this constructor does not
   *  enforce any syntactic constraint. Such enforcement is the
   *  responsibility of the {@link EntityNameParser} that produced
   *  the components.</p>
   *
   *  <p>To create the empty name, pass an empty array, or simply
   *  use {@link #EMPTY}.</p>
   *
   * @param components [in] The components, root-most first. Must
   *   not be {@code null}; must not contain any {@code null}
   *   elements.
   * @throws IllegalArgumentException if {@code components} is
   *   {@code null} or contains a {@code null} element.
   */
  public EntityName(String[] components)
  {
    if (components == null)
      throw new IllegalArgumentException(
          "'components' must not be null.");
    String[] copy = new String[components.length];
    for (int i = 0; i < components.length; i++)
    {
      if (components[i] == null)
        throw new IllegalArgumentException(
            "Component at index " + i + " is null.");
      copy[i] = components[i];
    }
    this.components = copy;
    this.offset     = 0;
    this.length     = copy.length;
  }

  /** Internal slice constructor used by {@link #getPrefix} and
   *  {@link #getSuffix} to share the backing array without an
   *  extra copy. Package-private; external callers must use the
   *  public constructor or the derivation methods.
   *
   * @param components Shared backing array. Must not be
   *   {@code null}.
   * @param offset     Offset of the first logical component.
   * @param length     Number of logical components.
   */
  EntityName(String[] components, int offset, int length)
  {
    this.components = components;
    this.offset     = offset;
    this.length     = length;
  }

  // -----------------------------------------------------------------------
  // Query
  // -----------------------------------------------------------------------

  /** Returns the number of components in this name.
   *
   *  <p>The empty name (representing the root of a namespace) has
   *  size {@code 0}.</p>
   *
   * @return The component count; zero or positive.
   */
  public int size()
  {
    return length;
  }

  /** Returns {@code true} if this name has no components.
   *
   *  <p>Equivalent to {@code size() == 0}. An empty name
   *  represents the root of a namespace.</p>
   *
   * @return {@code true} if this name is the empty (root) name.
   */
  public boolean isEmpty()
  {
    return length == 0;
  }

  /** Returns the component at the given position.
   *
   *  <p>Component {@code 0} is the root-most component; component
   *  {@code size() - 1} is the leaf-most.</p>
   *
   *  <p>The returned string is the <em>unescaped</em> component
   *  value, suitable for direct comparison with a child entity's
   *  display name or naming attribute value. Any escaping
   *  required by the underlying syntax has been removed by the
   *  parser.</p>
   *
   * @param index [in] Position in the range {@code [0, size())}.
   * @return The non-null component at {@code index}. Components
   *   may be empty strings if the syntax permits, but never
   *   {@code null}.
   * @throws IndexOutOfBoundsException if {@code index} is
   *   negative or not less than {@link #size()}.
   */
  public String get(int index)
  {
    if (index < 0 || index >= length)
      throw new IndexOutOfBoundsException(
          "Index " + index + " out of range [0, " + length + ").");
    return components[offset + index];
  }

  /** Returns the leaf-most component.
   *
   *  <p>Equivalent to {@code get(size() - 1)}.</p>
   *
   * @return The non-null leaf-most component.
   * @throws IndexOutOfBoundsException if this name is empty (the
   *   empty name has no last component).
   */
  public String getLastComponent()
  {
    if (length == 0)
      throw new IndexOutOfBoundsException(
          "The empty name has no last component.");
    return components[offset + length - 1];
  }

  // -----------------------------------------------------------------------
  // Derivation
  // -----------------------------------------------------------------------

  /** Returns a name composed of the first {@code count}
   *  components of this name (positions {@code [0, count)}).
   *
   *  <p>{@code getPrefix(0)} returns the empty name;
   *  {@code getPrefix(size())} returns a name equal to this one
   *  (and, as an implementation detail, returns {@code this}
   *  itself).</p>
   *
   *  <p>The returned name may share the backing component array
   *  with this name; this sharing is invisible to callers because
   *  {@code EntityName} is immutable.</p>
   *
   * @param count [in] The number of leading components to
   *   include, in the range {@code [0, size()]}.
   * @return A new {@code EntityName} containing the first
   *   {@code count} components of this name.
   * @throws IndexOutOfBoundsException if {@code count} is
   *   negative or greater than {@link #size()}.
   */
  public EntityName getPrefix(int count)
  {
    if (count < 0 || count > length)
      throw new IndexOutOfBoundsException(
          "Count " + count + " out of range [0, " + length + "].");
    if (count == length)
      return this;
    if (count == 0)
      return EMPTY;
    return new EntityName(components, offset, count);
  }

  /** Returns a name composed of the components of this name
   *  starting at position {@code start} (positions
   *  {@code [start, size())}).
   *
   *  <p>{@code getSuffix(0)} returns a name equal to this one
   *  (and, as an implementation detail, returns {@code this}
   *  itself); {@code getSuffix(size())} returns the empty name.
   *  This operation is the inverse of {@link #getPrefix}: for any
   *  valid index {@code i},
   *  {@code getPrefix(i).size() + getSuffix(i).size() == size()}.</p>
   *
   *  <p>The returned name may share the backing component array
   *  with this name; this sharing is invisible to callers because
   *  {@code EntityName} is immutable.</p>
   *
   * @param start [in] The starting position, in the range
   *   {@code [0, size()]}.
   * @return A new {@code EntityName} containing the components
   *   from {@code start} through the end of this name.
   * @throws IndexOutOfBoundsException if {@code start} is
   *   negative or greater than {@link #size()}.
   */
  public EntityName getSuffix(int start)
  {
    if (start < 0 || start > length)
      throw new IndexOutOfBoundsException(
          "Start " + start + " out of range [0, " + length + "].");
    if (start == 0)
      return this;
    if (start == length)
      return EMPTY;
    return new EntityName(components, offset + start, length - start);
  }

  /** Returns this name's parent: a new name containing all
   *  components except the leaf-most.
   *
   *  <p>Convenience equivalent to
   *  {@code getPrefix(size() - 1)}. The parent of a one-component
   *  name is the empty name. Calling this method on an empty name
   *  throws {@link IndexOutOfBoundsException}, because the empty
   *  name has no parent.</p>
   *
   * @return A new {@code EntityName} representing this name's
   *   parent.
   * @throws IndexOutOfBoundsException if this name is empty.
   */
  public EntityName getParent()
  {
    if (length == 0)
      throw new IndexOutOfBoundsException(
          "The empty name has no parent.");
    return getPrefix(length - 1);
  }
  
  /** Returns a new name with the given component appended to the
   *  leaf end of this name.
   *
   *  <p>The new name has {@link #size()} one greater than this
   *  name's size; its first {@code size()} components are this
   *  name's components in order, and its last component is
   *  {@code component}. This name is unchanged.</p>
   *
   *  <p>This is the structural building block for constructing
   *  child or sibling names from an existing name: a child name is
   *  the parent name appended with the child's leaf component, and
   *  a sibling rename is {@code getParent().append(newLeaf)}.</p>
   *
   *  <p>As with the array constructor, this method does not
   *  enforce any syntactic constraint on {@code component}; the
   *  caller is responsible for supplying a component that is
   *  valid in the parser's syntax. Components passed to this
   *  method are assumed to be already in the parser's normalized
   *  form &mdash; that is, in the same form
   *  {@link EntityNameParser#parse(String)} would have produced
   *  for the equivalent external string.</p>
   *
   * @param component [in] The component to append. Must not be
   *   {@code null}.
   * @return A new {@code EntityName} with {@code component}
   *   appended as the new leaf.
   * @throws IllegalArgumentException if {@code component} is
   *   {@code null}.
   */
  public EntityName append(String component)
  {
    if (component == null)
      throw new IllegalArgumentException(
          "'component' must not be null.");
    String[] copy = new String[length + 1];
    for (int i = 0; i < length; i++)
      copy[i] = components[offset + i];
    copy[length] = component;
    return new EntityName(copy, 0, length + 1);
  }  

  // -----------------------------------------------------------------------
  // Relational queries
  // -----------------------------------------------------------------------

  /** Returns {@code true} if this name begins with the given
   *  prefix &mdash; that is, if
   *  {@code prefix.size() <= this.size()} and
   *  {@code get(i).equals(prefix.get(i))} for every {@code i} in
   *  {@code [0, prefix.size())}.
   *
   *  <p>The empty name is a prefix of every name, including
   *  itself. Every name is a prefix of itself.</p>
   *
   *  <p>This method is the basis for ancestor tests:
   *  {@code a} is a strict ancestor of {@code b} if and only if
   *  {@code b.startsWith(a) && b.size() > a.size()}.</p>
   *
   * @param prefix [in] The candidate prefix. Must not be
   *   {@code null}.
   * @return {@code true} if {@code prefix} is a prefix of this
   *   name.
   * @throws IllegalArgumentException if {@code prefix} is
   *   {@code null}.
   */
  public boolean startsWith(EntityName prefix)
  {
    if (prefix == null)
      throw new IllegalArgumentException(
          "'prefix' must not be null.");
    if (prefix.length > this.length)
      return false;
    for (int i = 0; i < prefix.length; i++)
    {
      if (!this.components[this.offset + i]
            .equals(prefix.components[prefix.offset + i]))
        return false;
    }
    return true;
  }

  // -----------------------------------------------------------------------
  // Iteration
  // -----------------------------------------------------------------------

  /** Returns an {@link Iterator} over the components of this
   *  name, in order from root-most ({@link #get}{@code (0)}) to
   *  leaf-most ({@link #get}{@code (}{@link #size}{@code () - 1)}).
   *
   *  <p>Each call returns a fresh, independent iterator
   *  positioned before the first component. The returned iterator
   *  does not support {@link Iterator#remove()};
   *  {@code EntityName} is immutable.</p>
   *
   * @return A non-null iterator over the components.
   */
  public Iterator<String> getAll()
  {
    return new Iterator<String>()
    {
      private int cursor = 0;

      public boolean hasNext()
      {
        return cursor < length;
      }

      public String next()
      {
        if (cursor >= length)
          throw new NoSuchElementException();
        return components[offset + cursor++];
      }

      public void remove()
      {
        throw new UnsupportedOperationException(
            "EntityName is immutable.");
      }
    };
  }

  // -----------------------------------------------------------------------
  // Equality, hashing, debug
  // -----------------------------------------------------------------------

  /** Returns {@code true} if {@code obj} is an {@code EntityName}
   *  with the same component sequence as this name.
   *
   *  <p>Two {@code EntityName} instances are equal if and only if
   *  they have the same {@link #size()} and their components are
   *  pairwise equal as strings (using {@link String#equals}).</p>
   *
   *  <p>The parser that produced the names is responsible for any
   *  normalization (case folding, Unicode normalization,
   *  whitespace canonicalization) required to make this equality
   *  meaningful within its syntax. By the time a caller holds an
   *  {@code EntityName}, its components are already in normalized
   *  form, and so two names produced by the same parser from
   *  equivalent external strings will compare equal.</p>
   *
   * @param obj [in] The reference object with which to compare.
   * @return {@code true} if this name equals {@code obj}.
   */
  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (!(obj instanceof EntityName))
      return false;
    EntityName other = (EntityName) obj;
    if (this.length != other.length)
      return false;
    for (int i = 0; i < length; i++)
    {
      if (!this.components[this.offset + i]
            .equals(other.components[other.offset + i]))
        return false;
    }
    return true;
  }

  /** Returns a hash code consistent with {@link #equals(Object)}:
   *  two names that compare equal under {@link #equals} return
   *  the same hash code.
   *
   *  <p>The hash code is computed from the component sequence
   *  using the same polynomial formula as {@link java.util.List}:
   *  {@code h = 1; for each c: h = 31*h + c.hashCode()}. The
   *  result is cached on first computation and reused on
   *  subsequent calls.</p>
   *
   *  <p>The empty name has hash code {@code 1}, matching the
   *  convention for empty lists.</p>
   *
   * @return A hash code for this name.
   */
  public int hashCode()
  {
    int h = hash;
    if (h == 0)
    {
      h = 1;
      for (int i = 0; i < length; i++)
        h = 31 * h + components[offset + i].hashCode();
      hash = h;
    }
    return h;
  }

  /** Returns a string representation of this name suitable for
   *  debugging only, of the form {@code "[a, b, c]"} for a name
   *  with components {@code a}, {@code b}, {@code c}, and
   *  {@code "[]"} for the empty name.
   *
   *  <p>The returned string is <em>not</em> a valid
   *  name-in-namespace string and must not be fed back to
   *  {@link EntityNameParser#parse(String)}. Callers that need
   *  the canonical external form must use
   *  {@link EntityNameParser#format(EntityName)} with an
   *  appropriate parser, which will re-apply scheme, separator,
   *  escaping, and any other syntax-specific encoding.</p>
   *
   * @return A non-null string representation, intended for
   *   diagnostics only.
   */
  public String toString()
  {
    if (length == 0)
      return "[]";
    StringBuffer sb = new StringBuffer();
    sb.append('[');
    for (int i = 0; i < length; i++)
    {
      if (i > 0)
        sb.append(", ");
      sb.append(components[offset + i]);
    }
    sb.append(']');
    return sb.toString();
  }
}
