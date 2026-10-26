package com.optimasc.entity.event;

import java.util.EventObject;

import com.optimasc.entity.Entity;

/** Progress report delivered to an {@link OperationListener} by a
 *  long-running operation that processes many entities.
 *
 *  <p>Every event carries:</p>
 *  <ul>
 *    <li>the {@linkplain #getSourceEntity() source entity}, which
 *        is the entity the operation is about to process &mdash;
 *        the next entity to be copied or removed;</li>
 *    <li>the amount of work {@linkplain #getDone() done} so far
 *        and the {@linkplain #getTotal() total} amount of work,
 *        both counted in entities.</li>
 *  </ul>
 *
 *  <p>The source of an {@code OperationEvent} does not have the
 *  meaning it has for an {@link EntityEvent}. An
 *  {@link OperationListener} is not registered on an entity, so
 *  there is no registration point to report; the source is simply
 *  the entity the event is about, and it changes from one event
 *  to the next as the operation advances through the subtree. The
 *  entity the operation was started on is not carried by the
 *  event: the caller, which supplied it together with the
 *  listener, already knows it.</p>
 *
 *  <p>The event is also the channel through which the listener
 *  answers the operation: calling {@link #cancel()} asks for the
 *  operation to be stopped. For that reason, and unlike
 *  {@link EntityEvent}, instances of this class are <em>not</em>
 *  immutable. This is safe because an event is delivered to a
 *  single listener, synchronously, on the thread that runs the
 *  operation; it is not shared, and it must not be retained after
 *  {@link OperationListener#operationProgress} returns.</p>
 *
 *  <p>This class extends {@link EventObject} to participate in the
 *  standard Java event model. {@link EventObject#getSource()}
 *  returns the same {@link Entity} as {@link #getSourceEntity()},
 *  typed as {@link Object} per the {@code EventObject} contract;
 *  {@link #getSourceEntity()} is the preferred typed accessor.</p>
 *
 *  @see OperationListener
 *  @see com.optimasc.entity.EntityUtilities
 *  @author Carl Eric Codere
 */
public class OperationEvent extends EventObject
{
  private static final long serialVersionUID = 1L;

  // -----------------------------------------------------------------------
  // Constants
  // -----------------------------------------------------------------------

  /** Value returned by {@link #getTotal()} when the total amount
   *  of work is not known.
   *  <p>Value: {@code -1}.</p>
   */
  public static final long UNKNOWN = -1L;

  // -----------------------------------------------------------------------
  // Fields
  // -----------------------------------------------------------------------

  /** The number of entities processed so far. Never negative. */
  private final long done;

  /** The total number of entities to process, or
   *  {@link #UNKNOWN}. */
  private final long total;

  /** Whether the listener has requested cancellation. */
  private boolean cancelled;

  // -----------------------------------------------------------------------
  // Constructors
  // -----------------------------------------------------------------------

  /** Creates a new {@code OperationEvent}.
   *
   * @param source [in] The entity about to be processed. Must not
   *               be {@code null}.
   * @param done   [in] The number of entities or octets 
   *               processed so far. Must not be negative.
   * @param total  [in] The total number of entities or octets to process,
   *               or {@link #UNKNOWN}. If known, must not be less
   *               than {@code done}.
   * @throws IllegalArgumentException if {@code source} is
   *   {@code null}, if {@code done} is negative, or if
   *   {@code total} is neither {@link #UNKNOWN} nor a value
   *   greater than or equal to {@code done}.
   */
  public OperationEvent(Entity source, long done, long total)
  {
    super(checkSource(source));
    if (done < 0)
      throw new IllegalArgumentException(
          "'done' must not be negative; got " + done);
    if (total != UNKNOWN && total < done)
      throw new IllegalArgumentException(
          "'total' must be UNKNOWN or not less than 'done'; got "
          + total);
    this.done      = done;
    this.total     = total;
    this.cancelled = false;
  }

  /** Validates the source argument before passing it to the
   *  {@link EventObject} constructor, which would itself throw
   *  {@link IllegalArgumentException} on a {@code null} source but
   *  with a less specific message. */
  private static Entity checkSource(Entity source)
  {
    if (source == null)
      throw new IllegalArgumentException("'source' must not be null.");
    return source;
  }

  // -----------------------------------------------------------------------
  // Accessors
  // -----------------------------------------------------------------------

  /** Returns the entity the operation is about to process, typed
   *  as {@link Entity}.
   *
   *  <p>For a copy this is the entity on the source side that is
   *  about to be copied; for a removal it is the entity about to
   *  be removed. This is the typed equivalent of
   *  {@link EventObject#getSource()}; both methods return the same
   *  object.</p>
   *
   *  <p>The entity is valid for the duration of the listener call
   *  only. In a removal it no longer exists once the listener has
   *  returned, and operations on it then have undefined behavior;
   *  listeners should extract what they need to display (name,
   *  object class) during the call and not retain the
   *  reference.</p>
   *
   * @return The non-null entity about to be processed.
   */
  public Entity getSourceEntity()
  {
    return (Entity) getSource();
  }

  /** Returns the number of entities the operation has finished
   *  processing.
   *
   *  <p>The count does not include the
   *  {@linkplain #getSourceEntity() source entity}, which has not
   *  been processed yet: the first event of an operation reports
   *  {@code 0}. Within one operation the value never
   *  decreases.</p>
   *
   * @return The number of entities processed so far; zero or
   *   positive.
   */
  public long getDone()
  {
    return done;
  }

  /** Returns the total number of entities the operation will
   *  process, if known.
   *
   *  <p>Determining the total requires a traversal of the whole
   *  subtree before the operation starts. An operation that does
   *  not perform one reports {@link #UNKNOWN}, and a consumer
   *  should then present the progress as a running count instead
   *  of a percentage.</p>
   *
   * @return The total number of entities, never less than
   *   {@link #getDone()}, or {@link #UNKNOWN} if it has not been
   *   determined.
   */
  public long getTotal()
  {
    return total;
  }

  // -----------------------------------------------------------------------
  // Cancellation
  // -----------------------------------------------------------------------

  /** Requests that the operation be cancelled.
   *
   *  <p>The operation checks for the request when the listener
   *  returns. It then stops without processing the
   *  {@linkplain #getSourceEntity() source entity} and throws
   *  {@link com.optimasc.entity.EntityException} with error code
   *  {@link com.optimasc.entity.EntityException#OPERATION_CANCELLED}.
   *  The entities already processed are not restored.</p>
   *
   *  <p>The request cannot be withdrawn. Calling this method more
   *  than once has no further effect, and calling it after the
   *  listener has returned has no effect at all.</p>
   */
  public void cancel()
  {
    cancelled = true;
  }

  /** Returns whether cancellation of the operation has been
   *  requested through {@link #cancel()}.
   *
   * @return {@code true} if {@link #cancel()} has been called on
   *   this event.
   */
  public boolean isCancelled()
  {
    return cancelled;
  }

  // -----------------------------------------------------------------------
  // Object overrides
  // -----------------------------------------------------------------------

  /** Returns a string representation of this event suitable for
   *  logging and debugging. The format is not specified and must
   *  not be parsed.
   */
  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    sb.append("OperationEvent[source=").append(getSource());
    sb.append(", done=").append(done);
    sb.append(", total=");
    if (total == UNKNOWN)
      sb.append("UNKNOWN");
    else
      sb.append(total);
    if (cancelled)
      sb.append(", cancelled");
    sb.append("]");
    return sb.toString();
  }
}
