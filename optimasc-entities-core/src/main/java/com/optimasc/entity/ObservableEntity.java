package com.optimasc.entity;

import com.optimasc.entity.event.EntityListener;

/** Extends {@link Entity} with operations that register and
 *  deregister listeners for changes to this entity and, optionally,
 *  its descendants. An entity implementing this interface supports
 *  observation; an entity implementing only {@link Entity} does not.
 *
 *  <p>Consumers determine whether a given entity is observable by
 *  testing with <code>instanceof</code>:</p>
 *
 *  <pre>
 *    if (entity instanceof ObservableEntity) {
 *        ObservableEntity obs = (ObservableEntity) entity;
 *        obs.addEntityListener(ObservableEntity.SCOPE_SUBTREE, listener);
 *    }
 *  </pre>
 *
 *  <p>Observation is orthogonal to mutation. A read-only adapter
 *  may still be observable (a remote directory exposed through a
 *  read-only view but still notified of server-side changes); a
 *  mutable adapter may not be observable (an in-memory store
 *  modified only through this API, with no notification machinery).
 *  Consumers that need both capabilities must check for both.</p>
 *
 *  <p>This interface is the portable equivalent of JNDI's
 *  {@code EventContext} and {@code EventDirContext}, simplified to
 *  match the rest of the {@code com.optimasc.entity} package:</p>
 *  <ul>
 *    <li>A single listener interface ({@link EntityListener})
 *        carrying methods named after JNDI's
 *        {@code NamespaceChangeListener} and
 *        {@code ObjectChangeListener}, plus a
 *        {@code listenerFailed} method covering the role of
 *        {@code NamingExceptionEvent}.</li>
 *    <li>Three scope constants with the same semantics as JNDI's
 *        {@code OBJECT_SCOPE}, {@code ONELEVEL_SCOPE}, and
 *        {@code SUBTREE_SCOPE}.</li>
 *    <li>No filter-based registration variant. Listeners that need
 *        filtering should implement it within their event handlers.</li>
 *  </ul>
 *
 *  <h2>Scope semantics</h2>
 *
 *  <p>Each registration is made at a specific scope, which
 *  determines which entities can trigger events for that listener:</p>
 *  <ul>
 *    <li>{@link #SCOPE_OBJECT} &mdash; the listener receives events
 *        only for changes to this entity itself. The
 *        {@link com.optimasc.entity.event.EntityEvent#getAffectedEntity()
 *        affected entity} of every event is identical to this
 *        entity. Rename and attribute-change events on this entity
 *        are reported; add and remove events for children are not.</li>
 *    <li>{@link #SCOPE_ONELEVEL} &mdash; the listener receives
 *        events for changes to the direct children of this entity.
 *        The affected entity of every event is a direct child;
 *        events on this entity itself are not reported, nor are
 *        events deeper than one level.</li>
 *    <li>{@link #SCOPE_SUBTREE} &mdash; the listener receives events
 *        for changes to this entity and any descendant at any depth.</li>
 *  </ul>
 *
 *  <h2>Event delivery and threading</h2>
 *
 *  <p>Listener methods are invoked on whatever thread the underlying
 *  adapter uses to process the originating change. For an in-memory
 *  adapter this is typically the thread that called the modification
 *  method; for a remote or asynchronous adapter it may be an internal
 *  notification thread. Consumers that require delivery on a specific
 *  thread (notably the Swing event dispatch thread) must arrange the
 *  re-dispatch themselves, for example by wrapping listener bodies
 *  in {@link javax.swing.SwingUtilities#invokeLater}.</p>
 *
 *  <p>A single call to
 *  {@link ModifiableEntity#modifyAttributes(EntityModification[])}
 *  produces exactly one
 *  {@link com.optimasc.entity.event.EntityEvent#OBJECT_CHANGED}
 *  event per matching listener, carrying the entire batch in
 *  {@link com.optimasc.entity.event.EntityEvent#getModifications()}.
 *  Listeners observe the same atomic unit the caller submitted.</p>
 *
 *  <h2>Rename detection</h2>
 *
 *  <p>An {@link com.optimasc.entity.event.EntityEvent#OBJECT_RENAMED}
 *  event is fired for any change to an entity's
 *  {@link Entity#getNameInNamespace()}, whether the change results
 *  from an in-place rename (only the trailing name component
 *  changes) or from a move to a different parent (the prefix
 *  changes). Adapters that cannot distinguish a move from a
 *  remove-and-add at the backend level may instead fire
 *  {@code OBJECT_REMOVED} followed by {@code OBJECT_ADDED}; this is
 *  permitted but discouraged when the backend supports a true
 *  rename operation.</p>
 *
 *  <h2>Listener failure</h2>
 *
 *  <p>An adapter that can no longer guarantee delivery of further
 *  events for a registration must fire a
 *  {@link com.optimasc.entity.event.EntityEvent#LISTENER_FAILED}
 *  event to the affected listener. After such an event has been
 *  delivered, the listener is treated as unregistered; the consumer
 *  must call {@link #addEntityListener} again to resume observation.
 *  Adapters that cannot fail to deliver events need never fire this
 *  type.</p>
 *
 *  <p>Implementations are not required to be thread-safe. In a
 *  Swing-based consumer, registration and deregistration calls are
 *  expected to occur on the event dispatch thread.</p>
 *
 *  @see com.optimasc.entity.event.EntityListener
 *  @see com.optimasc.entity.event.EntityAdapter
 *  @see com.optimasc.entity.event.EntityEvent
 *  @author Carl Eric Codere
 */
public interface ObservableEntity extends Entity
{
  // -----------------------------------------------------------------------
  // Scope constants
  // (numeric values aligned with javax.naming.event.EventContext)
  // -----------------------------------------------------------------------

  /** Events are reported only for changes to this entity itself.
   *  <p>Value: {@code 0} &mdash; identical to
   *  {@code EventContext.OBJECT_SCOPE}.</p>
   */
  int SCOPE_OBJECT = 0;

  /** Events are reported only for changes to the direct children of
   *  this entity.
   *  <p>Value: {@code 1} &mdash; identical to
   *  {@code EventContext.ONELEVEL_SCOPE}.</p>
   */
  int SCOPE_ONELEVEL = 1;

  /** Events are reported for changes to this entity and any
   *  descendant at any depth.
   *  <p>Value: {@code 2} &mdash; identical to
   *  {@code EventContext.SUBTREE_SCOPE}.</p>
   */
  int SCOPE_SUBTREE = 2;

  // -----------------------------------------------------------------------
  // Registration
  // -----------------------------------------------------------------------

  /** Registers a listener for changes within the given scope.
   *
   *  <p>The same listener instance may be registered multiple
   *  times, possibly at different scopes; each registration is
   *  treated independently and each will receive matching events.
   *  Symmetrically, {@link #removeEntityListener} removes a single
   *  registration; a listener registered <em>n</em> times must be
   *  removed <em>n</em> times for it to stop receiving events.</p>
   *
   *  <p>The implementation does not retain a strong reference
   *  contract; consumers must explicitly deregister listeners when
   *  they are no longer needed, or the listener (and any object
   *  it references) will remain reachable for the lifetime of the
   *  registration.</p>
   *
   * @param scope    [in] The scope of the registration. Must be one
   *                 of {@link #SCOPE_OBJECT}, {@link #SCOPE_ONELEVEL},
   *                 or {@link #SCOPE_SUBTREE}.
   * @param listener [in] The non-null listener to register.
   * @throws EntityException with error code
   *   {@link EntityException#INVALID_ARGS} if {@code listener} is
   *   {@code null} or {@code scope} is not one of the three defined
   *   constants; with error code
   *   {@link EntityException#ACTION_FAILED} if the registration
   *   cannot be established because of a backend failure (for
   *   example, an underlying subscription cannot be opened).
   */
  void addEntityListener(int scope, EntityListener listener)
      throws EntityException;

  /** Deregisters a previously-registered listener.
   *
   *  <p>If the listener was registered more than once, only one
   *  registration is removed; further registrations remain active.
   *  If the listener was not registered (or has already been
   *  unregistered, including by a
   *  {@link com.optimasc.entity.event.EntityEvent#LISTENER_FAILED}
   *  notification), this method has no effect and returns
   *  normally.</p>
   *
   * @param listener [in] The non-null listener to deregister.
   * @throws EntityException with error code
   *   {@link EntityException#INVALID_ARGS} if {@code listener} is
   *   {@code null}; with error code
   *   {@link EntityException#ACTION_FAILED} if the deregistration
   *   cannot be completed because of a backend failure.
   */
  void removeEntityListener(EntityListener listener)
      throws EntityException;
}
