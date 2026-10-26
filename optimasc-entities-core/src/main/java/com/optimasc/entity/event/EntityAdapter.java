package com.optimasc.entity.event;

/** Abstract convenience implementation of {@link EntityListener}
 *  that provides empty default implementations of all listener
 *  methods.
 *
 *  <p>This class follows the {@code XxxAdapter} convention used
 *  throughout the Java platform &mdash; for example
 *  {@link java.awt.event.MouseAdapter},
 *  {@link java.awt.event.WindowAdapter}, and
 *  {@link javax.swing.event.MouseInputAdapter} &mdash; for
 *  listener interfaces with several methods. It exists so that a
 *  consumer interested in only a subset of events can override
 *  just the relevant methods rather than implementing the entire
 *  {@link EntityListener} interface with empty bodies.</p>
 *
 *  <p>Typical use:</p>
 *  <pre>
 *    observable.addEntityListener(
 *        ObservableEntity.SCOPE_ONELEVEL,
 *        new EntityAdapter() {
 *          public void objectRenamed(EntityEvent evt) {
 *            tree.nodeRenamed(evt.getAffectedEntity(),
 *                             evt.getOldName(),
 *                             evt.getNewName());
 *          }
 *        });
 *  </pre>
 *
 *  <p>All methods are empty no-ops. Subclasses override the
 *  methods of interest; unimplemented event types are silently
 *  ignored. In particular, {@link #listenerFailed} has an empty
 *  default; consumers that need to react to listener failures
 *  &mdash; for example, to surface a "view disconnected" message
 *  in the user interface &mdash; must override it.</p>
 *
 *  @see EntityListener
 *  @see EntityEvent
 *  @author Carl Eric Codere
 */
public abstract class EntityAdapter implements EntityListener
{
  /** Creates a new {@code EntityAdapter}. Provided for subclass
   *  invocation; there is no per-instance state. */
  protected EntityAdapter()
  {
  }

  /** Empty default implementation. Override to react to entity
   *  additions.
   *
   * @param evt [in] The non-null event. Ignored by this default
   *   implementation.
   */
  public void objectAdded(EntityEvent evt)
  {
  }

  /** Empty default implementation. Override to react to entity
   *  removals.
   *
   * @param evt [in] The non-null event. Ignored by this default
   *   implementation.
   */
  public void objectRemoved(EntityEvent evt)
  {
  }

  /** Empty default implementation. Override to react to entity
   *  renames.
   *
   * @param evt [in] The non-null event. Ignored by this default
   *   implementation.
   */
  public void objectRenamed(EntityEvent evt)
  {
  }

  /** Empty default implementation. Override to react to attribute
   *  modifications.
   *
   * @param evt [in] The non-null event. Ignored by this default
   *   implementation.
   */
  public void objectChanged(EntityEvent evt)
  {
  }

  /** Empty default implementation. Override to react to listener
   *  failures &mdash; for example, to surface a "view
   *  disconnected" indicator in the user interface or to attempt
   *  re-registration.
   *
   * @param evt [in] The non-null event. Ignored by this default
   *   implementation.
   */
  public void listenerFailed(EntityEvent evt)
  {
  }
}
