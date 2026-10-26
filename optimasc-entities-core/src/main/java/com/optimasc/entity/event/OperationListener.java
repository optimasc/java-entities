package com.optimasc.entity.event;

import java.util.EventListener;

/** Receives progress reports from a long-running operation that
 *  processes many entities, and may ask for that operation to be
 *  cancelled.
 *
 *  <h2>Scope of a listener</h2>
 *
 *  <p>Unlike an {@link EntityListener}, an
 *  {@code OperationListener} is not registered on an entity. It is
 *  passed as an argument to one invocation of one operation,
 *  receives the reports of that invocation only, and is not
 *  retained after the invocation returns. The two listener types
 *  answer different questions: an {@link EntityListener} is told
 *  what changed in the namespace, whoever caused it; an
 *  {@code OperationListener} is told how far the caller's own
 *  operation has got.</p>
 *
 *  <p>Listening is optional: wherever an {@code OperationListener}
 *  is accepted, {@code null} may be passed, and the operation then
 *  runs to completion without reporting and without the
 *  possibility of being cancelled.</p>
 *
 *  <h2>When the listener is invoked</h2>
 *
 *  <p>{@link #operationProgress} is invoked once for each entity
 *  the operation processes, immediately <em>before</em> that
 *  entity is processed. The entity is reported by
 *  {@link OperationEvent#getSourceEntity()} and is valid for the
 *  duration of the call, which lets the listener read its display
 *  name or object class for presentation to the user. No report
 *  follows the last entity: the normal return of the operation is
 *  the signal that it has completed.</p>
 *
 *  <h2>Cancellation</h2>
 *
 *  <p>A listener requests cancellation by calling
 *  {@link OperationEvent#cancel()} on the event it was handed. The
 *  operation then stops before processing that entity and
 *  throws {@link com.optimasc.entity.EntityException} with error
 *  code
 *  {@link com.optimasc.entity.EntityException#OPERATION_CANCELLED}.
 *  Work already carried out is <em>not</em> rolled back: a
 *  cancelled copy leaves a partial destination subtree and a
 *  cancelled removal leaves the entities not yet reached. Any
 *  clean-up is the decision of the caller.</p>
 *
 *  <h2>Threading and re-entrancy</h2>
 *
 *  <p>The listener is invoked synchronously, on the thread that
 *  called the operation, and never concurrently for a given
 *  operation. In line with the general contract of
 *  {@link com.optimasc.entity.Entity}, it is the consumer and not
 *  the library that moves a long-running operation to a worker
 *  thread; a user-interface consumer must consequently re-dispatch
 *  each report to its own user-interface thread, and a
 *  cancellation flag set from that thread must be safely published
 *  to the worker (for example through a {@code volatile}
 *  field).</p>
 *
 *  <p>The listener should return quickly, since the operation is
 *  suspended for the duration of the call. It may read from the
 *  source entity but must not modify the namespace. An unchecked
 *  exception thrown by the listener propagates to the caller of
 *  the operation, which is abandoned in the same partial state as
 *  a cancelled one.</p>
 *
 *  <h2>Example</h2>
 *
 *  <pre>
 *  class DialogListener implements OperationListener
 *  {
 *    volatile boolean cancelRequested;  // set by the Cancel button
 *
 *    public void operationProgress(OperationEvent evt)
 *    {
 *      if (cancelRequested)
 *      {
 *        evt.cancel();
 *        return;
 *      }
 *      final String label =
 *          evt.getSourceEntity().getDisplayName();
 *      final long done = evt.getDone();
 *      SwingUtilities.invokeLater(new Runnable() {
 *        public void run() { dialog.showProgress(label, done); }
 *      });
 *    }
 *  }
 *  </pre>
 *
 *  @see OperationEvent
 *  @see com.optimasc.entity.EntityUtilities
 *  @author Carl Eric Codere
 */
public interface OperationListener extends EventListener
{
  /** Invoked before the operation processes an entity.
   *
   *  <p>An implementation that takes no action lets the operation
   *  continue. To stop the operation, call
   *  {@link OperationEvent#cancel()} on {@code evt} before
   *  returning.</p>
   *
   * @param evt [in] The non-null event describing the entity about
   *   to be processed and the work completed so far. It is valid
   *   only for the duration of this call and must not be retained.
   */
  void operationProgress(OperationEvent evt);
}
