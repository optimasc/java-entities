package com.optimasc.entity.event;

import java.util.EventListener;

/** Receives notifications of changes to an {@link com.optimasc.entity.Entity}
 *  or one of its descendants observed through an
 *  {@link com.optimasc.entity.ObservableEntity} registration.
 *
 *  <p>This interface is the portable equivalent of JNDI's
 *  {@code NamespaceChangeListener} and {@code ObjectChangeListener}
 *  merged into a single interface, plus a method covering the role
 *  of {@code NamingExceptionEvent}. The four change methods retain
 *  the names used by JNDI so that adapter code translating between
 *  the two models has a direct one-to-one mapping:</p>
 *
 *  <table border="1" summary="Mapping to JNDI listener methods">
 *    <tr><th>EntityListener</th><th>JNDI equivalent</th></tr>
 *    <tr><td>{@link #objectAdded(EntityEvent)}</td>
 *        <td>{@code NamespaceChangeListener.objectAdded}</td></tr>
 *    <tr><td>{@link #objectRemoved(EntityEvent)}</td>
 *        <td>{@code NamespaceChangeListener.objectRemoved}</td></tr>
 *    <tr><td>{@link #objectChanged(EntityEvent)}</td>
 *        <td>{@code ObjectChangeListener.objectChanged}</td></tr>
 *    <tr><td>{@link #listenerFailed}</td>
 *        <td>{@code NamingListener.namingExceptionThrown}</td></tr>
 *  </table>
 *
 *  <p>{@link #objectChanged} is specifically the
 *  attribute-modification notification, matching JNDI's distinction
 *  between namespace changes (add / remove / rename) and object
 *  changes (attribute modifications). It is not a generic catch-all.</p>
 *
 *  <p>Implementations that care about only a subset of these events
 *  may extend {@link EntityAdapter} instead, which provides empty
 *  default implementations of all five methods.</p>
 *
 *  <p>Listener methods are invoked on whatever thread the underlying
 *  adapter uses to process the originating change. For an in-memory
 *  adapter this is typically the thread that called the
 *  modification; for a remote adapter it may be an internal
 *  notification thread. Consumers that require delivery on a
 *  specific thread (notably the Swing event dispatch thread) must
 *  arrange the re-dispatch themselves, for example by wrapping the
 *  body of each listener method in
 *  {@link javax.swing.SwingUtilities#invokeLater}.</p>
 *
 *  <p>Listener methods should return quickly. A slow listener
 *  blocks the adapter's notification path and, on adapters with
 *  bounded notification queues, can cause events to be dropped or
 *  the registration to fail with {@link #listenerFailed}.</p>
 *
 *  @see EntityAdapter
 *  @see EntityEvent
 *  @see com.optimasc.entity.ObservableEntity
 *  @author Carl Eric Codere
 */
public interface EntityListener extends EventListener
{
  /** Invoked when an entity has been added within the scope of the
   *  listener's registration.
   *
   * @param evt [in] The non-null event describing the addition. Its
   *   {@link EntityEvent#getType() type} is
   *   {@link EntityEvent#OBJECT_ADDED}.
   */
  void objectAdded(EntityEvent evt);

  /** Invoked when an entity has been removed within the scope of
   *  the listener's registration.
   *
   *  <p>The removed entity is reported by
   *  {@link EntityEvent#getAffectedEntity()}. After this method
   *  returns, operations on that entity reference have undefined
   *  behavior; listeners should not retain it beyond the call.</p>
   *
   * @param evt [in] The non-null event describing the removal. Its
   *   {@link EntityEvent#getType() type} is
   *   {@link EntityEvent#OBJECT_REMOVED}.
   */
  void objectRemoved(EntityEvent evt);

  /** Notifies this listener that an entity has been rebound to a
   *  new location in the namespace &mdash; renamed, moved, or
   *  both &mdash; through a successful call to {@link
   *   ModifiableEntity#move(Entity,String)}.
   *
   *  <p>The event's old and new names together describe the
   *  rebinding. Listeners that care only about the rename case
   *  can compare {@code oldName.getParent()} with
   *  {@code newName.getParent()}; listeners that care only about
   *  moves can compare {@code oldName.getLastComponent()} with
   *  {@code newName.getLastComponent()}. Most listeners can
   *  ignore the distinction and simply act on the new name.</p>
   *
   * @param event [in] The event describing the rebinding. Never
   *   {@code null}.
   */
  void objectRebound(EntityEvent event);
  
  /** Invoked when an entity's attributes have been modified within
   *  the scope of the listener's registration.
   *
   *  <p>The modifications that were applied are reported by
   *  {@link EntityEvent#getModifications()}. A single invocation of
   *  this method corresponds to a single call to
   *  {@link com.optimasc.entity.ModifiableEntity#modifyAttributes};
   *  the entire batch is reported as one event.</p>
   *
   * @param evt [in] The non-null event describing the attribute
   *   changes. Its {@link EntityEvent#getType() type} is
   *   {@link EntityEvent#OBJECT_CHANGED}.
   */
  void objectChanged(EntityEvent evt);

  /** Invoked when the listener's registration has failed and no
   *  further events will be delivered to it.
   *
   *  <p>The underlying cause is reported by
   *  {@link EntityEvent#getCause()}. After this method has been
   *  invoked for a given listener, the listener is treated as
   *  unregistered by the adapter; the consumer must register a new
   *  listener to resume observation.</p>
   *
   *  <p>Adapters that cannot fail to deliver events (notably
   *  purely in-memory ones) need never invoke this method.</p>
   *
   * @param evt [in] The non-null event describing the failure. Its
   *   {@link EntityEvent#getType() type} is
   *   {@link EntityEvent#LISTENER_FAILED}.
   */
  void listenerFailed(EntityEvent evt);
}
