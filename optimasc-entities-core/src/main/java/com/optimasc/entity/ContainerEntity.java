package com.optimasc.entity;

import java.util.List;

/** Extends {@link Entity} with the ability to hold child entities,
 *  marking the implementing node as a <em>container</em> in the
 *  namespace hierarchy.
 *
 *  <p>The presence of this interface on an entity is the authoritative
 *  signal that the entity is structurally capable of holding children,
 *  regardless of whether any children currently exist. A consumer must
 *  use {@code instanceof ContainerEntity} to distinguish an empty
 *  container from a true leaf node &mdash; a leaf never implements this
 *  interface, while an empty container always does.</p>
 *
 *  <p>This follows the same capability-detection pattern used elsewhere
 *  in this API: {@link ModifiableEntity} signals writability and
 *  {@link ObservableEntity} signals event support, both via
 *  {@code instanceof} rather than a boolean flag. {@code ContainerEntity}
 *  applies the same idiom to structural containment.</p>
 *
 *  <p>Link entities must not implement this interface. A link is a
 *  reference to another entity; any child traversal should be performed
 *  on the resolved target, not on the link itself.</p>
 *
 *  <p>Implementations are not required to be thread-safe. For backends
 *  where child enumeration is expensive (for example, remote LDAP
 *  directories), callers are expected to invoke {@link #getChildren()}
 *  from a background thread rather than the user-interface thread.</p>
 *
 *  @see Entity
 *  @see ModifiableEntity
 *  @see ObservableEntity
 *  @author Carl Eric Codere
 */
public interface ContainerEntity extends Entity
{
  /** Returns this entity's children as an unmodifiable list, in their
   *  natural order.
   *
   *  <p>The returned list represents the entity's children at the time
   *  of the call. Callers that require a refreshed view must call this
   *  method again. Attempts to modify the returned list throw
   *  {@link UnsupportedOperationException}.</p>
   *
   *  <p>Implementations may return a lazily-populated list whose elements
   *  are computed on access, but the resulting behaviour must be
   *  indistinguishable from a pre-materialized list: random access,
   *  iteration, and {@link List#size()} must all return consistent
   *  results.</p>
   *
   *  <p>For backends where enumeration is expensive (for example, remote
   *  LDAP directories with large containers), callers are expected to
   *  invoke this method from a background thread rather than the
   *  user-interface thread. This interface does not prescribe a threading
   *  model; that responsibility rests with the consumer.</p>
   *
   *  <p>The wildcard in the return type permits adapter-specific subtypes;
   *  callers may treat all elements as {@link Entity}.</p>
   *
   *  @return A non-null, unmodifiable list of {@link Entity} instances.
   *    If the entity has no children, an empty list is returned.
   *  @throws EntityException with error code
   *    {@link EntityException#ACTION_FAILED} if the children cannot be
   *    enumerated due to a backend failure, or
   *    {@link EntityException#NO_SUCH_OBJECT} if the entity no longer
   *    exists in the backend.
   */
  List<? extends Entity> getChildren() throws EntityException;

  /** Returns a hint as to the number of children this entity has, without
   *  forcing a full enumeration. This value is intended to allow
   *  user-interface components &mdash; notably Swing tree models &mdash;
   *  to render correctly without invoking {@link #getChildren()} when
   *  doing so would be expensive.
   *
   *  <p>The returned value has the following semantics:</p>
   *  <ul>
   *    <li>{@code -1} indicates that the child count is unknown and
   *        would require enumeration to determine. A user interface
   *        should treat the entity as potentially expandable and resolve
   *        the true count on first expansion.</li>
   *    <li>{@code 0} indicates that this container currently has no
   *        children. A user interface may render it without an expansion
   *        control, but must not treat it as a leaf node.</li>        
   *    <li>A positive integer indicates the exact or best-effort count
   *        of children. Implementations that can provide an exact count
   *        cheaply (for example, from an in-memory collection or a DOM
   *        {@code NodeList}) should do so; implementations for which
   *        counting is as expensive as enumeration should return
   *        {@code -1} rather than eagerly compute.</li>
   *  </ul>
   *
   *  <p>Link entities must always return {@code 0}.</p>
   *
   *  <p>This value is advisory. A consumer must not rely on it being
   *  consistent with the number of elements actually produced by
   *  {@link #getChildren()}, as the underlying backend may change
   *  between calls.</p>
   *
   *  <p>This method never throws. Implementations that cannot determine
   *  the child count without I/O must return {@code -1}.</p>
   *
   *  @return {@code -1} if unknown, {@code 0} if the entity has no
   *    children, or a positive integer indicating the approximate or
   *    exact child count.
   */
  int getChildCountHint();
}
