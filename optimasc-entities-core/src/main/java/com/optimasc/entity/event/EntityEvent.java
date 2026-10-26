package com.optimasc.entity.event;

import java.util.EventObject;

import com.optimasc.attribute.AttributeModification;
import com.optimasc.entity.Entity;
import com.optimasc.entity.EntityName;

/** Notification of a change to an {@link Entity} or one of its
 *  descendants observed through an
 *  {@link com.optimasc.entity.ObservableEntity} registration.
 *
 *  <p>This class is the portable equivalent of
 *  {@code javax.naming.event.NamingEvent}, with the simplifications
 *  permitted by the absence of a {@code DirContext} dependency. The
 *  numeric values of the change-type constants are intentionally
 *  identical to those defined on JNDI's {@code NamingEvent} so that an
 *  adapter layer can translate JNDI events to {@code EntityEvent}
 *  instances without re-mapping codes. {@link #LISTENER_FAILED} is
 *  added as a fifth type, covering the role of JNDI's separate
 *  {@code NamingExceptionEvent}.</p>
 *
 *  <p>Every event carries:</p>
 *  <ul>
 *    <li>the {@linkplain #getType() type} of change;</li>
 *    <li>the {@linkplain #getSourceEntity() source entity}, which is
 *        the entity on which the listener was originally registered;</li>
 *    <li>the {@linkplain #getAffectedEntity() affected entity}, which
 *        is the entity that actually changed &mdash; identical to the
 *        source for {@link com.optimasc.entity.ObservableEntity#SCOPE_OBJECT}
 *        registrations, and a descendant of the source for
 *        {@link com.optimasc.entity.ObservableEntity#SCOPE_ONELEVEL} or
 *        {@link com.optimasc.entity.ObservableEntity#SCOPE_SUBTREE}
 *        registrations.</li>
 *  </ul>
 *
 *  <p>Type-specific payload is exposed through accessors that return
 *  {@code null} when not applicable to the event's type:</p>
 *  <ul>
 *    <li>{@link #getOldName()} and {@link #getNewName()} are populated
 *        for {@link #OBJECT_REBOUND} events and {@code null} otherwise.
 *        Both are full name-in-namespace strings. A rename event is
 *        fired for any change to the affected entity's
 *        {@link Entity#getNameInNamespace()}, whether the change is an
 *        in-place rename or a move to a different parent.</li>
 *    <li>{@link #getModifications()} is populated for
 *        {@link #OBJECT_CHANGED} events and {@code null} otherwise.
 *        A single event describes an entire batch passed to
 *        {@link com.optimasc.entity.ModifiableEntity#modifyAttributes};
 *        listeners receive one event per batch, not one per
 *        modification item, so the atomicity of the batch is preserved
 *        in the notification stream.</li>
 *    <li>{@link #getCause()} is populated for {@link #LISTENER_FAILED}
 *        events and {@code null} otherwise. When this event fires, no
 *        further events will be delivered to the failed listener
 *        registration; the listener is effectively unregistered. The
 *        consumer is responsible for deciding whether to re-register,
 *        refresh manually, or surface the failure to the user.</li>
 *  </ul>
 *
 *  <p>This class extends {@link EventObject} to participate in the
 *  standard Java event model. The {@link EventObject#getSource()}
 *  method returns the same {@link Entity} as
 *  {@link #getSourceEntity()}, typed as {@link Object} per the
 *  {@code EventObject} contract; {@link #getSourceEntity()} is the
 *  preferred typed accessor.</p>
 *
 *  <p>Instances of this class are immutable and may be safely shared
 *  across threads. The thread on which an event is delivered is
 *  determined by the {@link com.optimasc.entity.ObservableEntity}
 *  implementation; consumers that require delivery on a specific
 *  thread (such as the Swing event dispatch thread) must arrange the
 *  re-dispatch themselves, typically by wrapping listener bodies in
 *  {@link javax.swing.SwingUtilities#invokeLater}.</p>
 *
 *  @see EntityListener
 *  @see com.optimasc.entity.ObservableEntity
 *  @author Carl Eric Codere
 */
public class EntityEvent extends EventObject
{
  private static final long serialVersionUID = 1L;

  // -----------------------------------------------------------------------
  // Event-type constants
  // (numeric values aligned with javax.naming.event.NamingEvent)
  // -----------------------------------------------------------------------

  /** An entity has been added. The new entity is reported by
   *  {@link #getAffectedEntity()}.
   *  <p>Value: {@code 0} &mdash; identical to
   *  {@code NamingEvent.OBJECT_ADDED}.</p>
   */
  public static final int OBJECT_ADDED = 0;

  /** An entity has been removed. The removed entity is reported by
   *  {@link #getAffectedEntity()}; subsequent operations on it have
   *  undefined behavior.
   *  <p>Value: {@code 1} &mdash; identical to
   *  {@code NamingEvent.OBJECT_REMOVED}.</p>
   */
  public static final int OBJECT_REMOVED = 1;

  /** An entity has been rebound to a new location in the namespace
   *  &mdash; renamed in place, moved to a different parent, or
   *  both. The affected entity is reported by
   *  {@link #getAffectedEntity()}; the previous and current
   *  names-in-namespace are reported by {@link #getOldName()} and
   *  {@link #getNewName()}.
   *
   *  <p>This event fires for any change to the affected entity's
   *  {@link Entity#getNameInNamespace()} produced through a
   *  successful call to
   *  {@link com.optimasc.entity.ModifiableEntity#rebind(com.optimasc.entity.EntityName)}.
   *  Listeners that care about the rename-vs-move distinction can
   *  recover it from the names themselves:
   *  {@code oldName.getParent().equals(newName.getParent())}
   *  identifies a pure rename, and
   *  {@code oldName.getLastComponent().equals(newName.getLastComponent())}
   *  identifies a pure move. Most listeners can ignore the
   *  distinction and simply act on the new name.</p>
   *
   *  <p>Adapters that cannot distinguish a rebind from a
   *  remove-and-add at the backend level may instead fire
   *  {@link #OBJECT_REMOVED} followed by {@link #OBJECT_ADDED};
   *  this is permitted but discouraged when the backend supports
   *  a true rebind operation.</p>
   *
   *  <p>Value: {@code 2} &mdash; identical to
   *  {@code NamingEvent.OBJECT_RENAMED}; the constant name differs
   *  to reflect that this event covers move as well as rename.</p>
   */
  public static final int OBJECT_REBOUND = 2;  

  /** An entity's attributes have changed. The affected entity is
   *  reported by {@link #getAffectedEntity()}; the modifications that
   *  were applied are reported by {@link #getModifications()}.
   *
   *  <p>A single {@code OBJECT_CHANGED} event corresponds to a single
   *  call to
   *  {@link com.optimasc.entity.ModifiableEntity#modifyAttributes},
   *  regardless of how many {@link AttributeModification} instances
   *  were in the batch. Listeners observe the same atomic unit the
   *  caller submitted.</p>
   *
   *  <p>Value: {@code 3} &mdash; identical to
   *  {@code NamingEvent.OBJECT_CHANGED}.</p>
   */
  public static final int OBJECT_CHANGED = 3;

  /** The listener registration has failed and no further events will
   *  be delivered to it. The underlying cause is reported by
   *  {@link #getCause()}.
   *
   *  <p>This event covers the role of JNDI's separate
   *  {@code NamingExceptionEvent}. It fires when an adapter can no
   *  longer guarantee that future mutations in the registered scope
   *  will be reported &mdash; for example, when a remote directory
   *  connection drops, a filesystem watch is invalidated, or a
   *  subscription is rejected by the backend. After this event has
   *  been delivered, the listener is treated as unregistered;
   *  consumers that wish to resume observation must register a new
   *  listener.</p>
   *
   *  <p>In-memory adapters that cannot fail to deliver events need
   *  never fire this type.</p>
   *
   *  <p>Value: {@code 4} &mdash; no JNDI equivalent.</p>
   */
  public static final int LISTENER_FAILED = 4;

  // -----------------------------------------------------------------------
  // Fields
  // -----------------------------------------------------------------------

  /** The event type. One of {@link #OBJECT_ADDED},
   *  {@link #OBJECT_REMOVED}, {@link #OBJECT_REBOUND},
   *  {@link #OBJECT_CHANGED}, or {@link #LISTENER_FAILED}. */
  private final int type;

  /** The entity that actually changed. For {@code SCOPE_OBJECT}
   *  registrations this is identical to the source. Never {@code null}
   *  except for {@link #LISTENER_FAILED}, where it may be {@code null}
   *  if the failure occurred before any specific entity was
   *  identified. */
  private final Entity affected;
  
  /** The name-in-namespace of the affected entity before the
   *  rebind, or {@code null} if this is not an
   *  {@link #OBJECT_REBOUND} event. */
  private final EntityName oldName;

  /** The name-in-namespace of the affected entity after the
   *  rebind, or {@code null} if this is not an
   *  {@link #OBJECT_REBOUND} event. */
  private final EntityName newName;
  
  /** The modifications that were applied, or {@code null} if this is
   *  not an {@link #OBJECT_CHANGED} event. A defensive copy is taken
   *  at construction time. */
  private final AttributeModification[] modifications;

  /** The cause of the listener failure, or {@code null} if this is
   *  not a {@link #LISTENER_FAILED} event. */
  private final Throwable cause;

  // -----------------------------------------------------------------------
  // Constructors
  // -----------------------------------------------------------------------

  /** Creates a new {@code EntityEvent} for an
   *  {@link #OBJECT_ADDED} or {@link #OBJECT_REMOVED} notification.
   *
   * @param source   [in] The entity on which the listener was
   *                 registered. Must not be {@code null}.
   * @param type     [in] Must be {@link #OBJECT_ADDED} or
   *                 {@link #OBJECT_REMOVED}.
   * @param affected [in] The entity that was added or removed. Must
   *                 not be {@code null}.
   * @throws IllegalArgumentException if {@code source} or
   *   {@code affected} is {@code null}, or if {@code type} is not
   *   one of the two accepted values.
   */
  public EntityEvent(Entity source, int type, Entity affected)
  {
    super(checkSource(source));
    if (type != OBJECT_ADDED && type != OBJECT_REMOVED)
      throw new IllegalArgumentException(
          "Type must be OBJECT_ADDED or OBJECT_REMOVED for this "
          + "constructor; got " + type);
    if (affected == null)
      throw new IllegalArgumentException(
          "'affected' must not be null.");
    this.type          = type;
    this.affected      = affected;
    this.oldName       = null;
    this.newName       = null;
    this.modifications = null;
    this.cause         = null;
  }

  /** Creates a new {@code EntityEvent} for an
   *  {@link #OBJECT_REBOUND} notification.
   *
   * @param source   [in] The entity on which the listener was
   *                 registered. Must not be {@code null}.
   * @param affected [in] The entity whose name changed. Must not be
   *                 {@code null}.
   * @param oldName  [in] The previous name-in-namespace. Must not be
   *                 {@code null}.
   * @param newName  [in] The current name-in-namespace. Must not be
   *                 {@code null}.
   * @throws IllegalArgumentException if any argument is {@code null}.
   */
  public EntityEvent(Entity source, Entity affected,
                     EntityName oldName, EntityName newName)
  {
    super(checkSource(source));
    if (affected == null)
      throw new IllegalArgumentException(
          "'affected' must not be null.");
    if (oldName == null)
      throw new IllegalArgumentException(
          "'oldName' must not be null.");
    if (newName == null)
      throw new IllegalArgumentException(
          "'newName' must not be null.");
    this.type          = OBJECT_REBOUND;
    this.affected      = affected;
    this.oldName       = oldName;
    this.newName       = newName;
    this.modifications = null;
    this.cause         = null;
  }

  /** Creates a new {@code EntityEvent} for an
   *  {@link #OBJECT_CHANGED} notification.
   *
   *  <p>The {@code modifications} array is defensively copied; later
   *  changes to the caller's array do not affect this event. The
   *  array elements themselves are not cloned.</p>
   *
   * @param source        [in] The entity on which the listener was
   *                      registered. Must not be {@code null}.
   * @param affected      [in] The entity whose attributes changed.
   *                      Must not be {@code null}.
   * @param modifications [in] The modifications that were applied,
   *                      in the order they were submitted. Must not
   *                      be {@code null}; an empty array is
   *                      permitted.
   * @throws IllegalArgumentException if any argument is {@code null},
   *   or if {@code modifications} contains a {@code null} element.
   */
  public EntityEvent(Entity source, Entity affected,
                     AttributeModification[] modifications)
  {
    super(checkSource(source));
    if (affected == null)
      throw new IllegalArgumentException(
          "'affected' must not be null.");
    if (modifications == null)
      throw new IllegalArgumentException(
          "'modifications' must not be null.");
    AttributeModification[] copy =
        new AttributeModification[modifications.length];
    for (int i = 0; i < modifications.length; i++)
    {
      if (modifications[i] == null)
        throw new IllegalArgumentException(
            "modifications[" + i + "] must not be null.");
      copy[i] = modifications[i];
    }
    this.type          = OBJECT_CHANGED;
    this.affected      = affected;
    this.oldName       = null;
    this.newName       = null;
    this.modifications = copy;
    this.cause         = null;
  }

  /** Creates a new {@code EntityEvent} for a
   *  {@link #LISTENER_FAILED} notification.
   *
   * @param source   [in] The entity on which the listener was
   *                 registered. Must not be {@code null}.
   * @param affected [in] The entity that was being observed when the
   *                 failure was detected, or {@code null} if the
   *                 failure cannot be attributed to a specific
   *                 entity.
   * @param cause    [in] The underlying cause of the failure. Must
   *                 not be {@code null}.
   * @throws IllegalArgumentException if {@code source} or
   *   {@code cause} is {@code null}.
   */
  public EntityEvent(Entity source, Entity affected, Throwable cause)
  {
    super(checkSource(source));
    if (cause == null)
      throw new IllegalArgumentException(
          "'cause' must not be null.");
    this.type          = LISTENER_FAILED;
    this.affected      = affected;
    this.oldName       = null;
    this.newName       = null;
    this.modifications = null;
    this.cause         = cause;
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

  /** Returns the event-type code for this event.
   *
   * @return One of {@link #OBJECT_ADDED}, {@link #OBJECT_REMOVED},
   *   {@link #OBJECT_REBOUND}, {@link #OBJECT_CHANGED}, or
   *   {@link #LISTENER_FAILED}.
   */
  public int getType()
  {
    return type;
  }

  /** Returns the entity on which the listener was registered, typed
   *  as {@link Entity}.
   *
   *  <p>This is the typed equivalent of
   *  {@link EventObject#getSource()}; both methods return the same
   *  object.</p>
   *
   * @return The non-null source entity.
   */
  public Entity getSourceEntity()
  {
    return (Entity) getSource();
  }

  /** Returns the entity that actually changed.
   *
   *  <p>For listeners registered with
   *  {@link com.optimasc.entity.ObservableEntity#SCOPE_OBJECT}, this
   *  is identical to {@link #getSourceEntity()}. For listeners
   *  registered with
   *  {@link com.optimasc.entity.ObservableEntity#SCOPE_ONELEVEL} or
   *  {@link com.optimasc.entity.ObservableEntity#SCOPE_SUBTREE}, this
   *  is a descendant of the source.</p>
   *
   *  <p>For {@link #LISTENER_FAILED} events the affected entity may
   *  be {@code null} if the failure cannot be attributed to a
   *  specific entity (for example, a transport-level error before
   *  any change had been seen).</p>
   *
   * @return The affected entity, or {@code null} only for some
   *   {@link #LISTENER_FAILED} events.
   */
  public Entity getAffectedEntity()
  {
    return affected;
  }
  
  /** Returns the previous name-in-namespace of the affected
   *  entity, for {@link #OBJECT_REBOUND} events.
   *
   * @return The previous name-in-namespace, or {@code null} if
   *   this is not an {@link #OBJECT_REBOUND} event.
   */
  public EntityName getOldName()
  {
    return oldName;
  }

  /** Returns the current name-in-namespace of the affected
   *  entity, for {@link #OBJECT_REBOUND} events.
   *
   * @return The current name-in-namespace, or {@code null} if
   *   this is not an {@link #OBJECT_REBOUND} event.
   */
  public EntityName getNewName()
  {
    return newName;
  }
  

  /** Returns the modifications that were applied to the affected
   *  entity, for {@link #OBJECT_CHANGED} events.
   *
   *  <p>The returned array is a defensive copy; modifications to it
   *  do not affect the event. The order of the modifications matches
   *  the order in which they were submitted to
   *  {@link com.optimasc.entity.ModifiableEntity#modifyAttributes}.</p>
   *
   * @return A non-null, possibly empty array of modifications, or
   *   {@code null} if this is not an {@link #OBJECT_CHANGED} event.
   */
  public AttributeModification[] getModifications()
  {
    if (modifications == null)
      return null;
    AttributeModification[] copy =
        new AttributeModification[modifications.length];
    System.arraycopy(modifications, 0, copy, 0, modifications.length);
    return copy;
  }

  /** Returns the underlying cause of the listener failure, for
   *  {@link #LISTENER_FAILED} events.
   *
   * @return The cause, or {@code null} if this is not a
   *   {@link #LISTENER_FAILED} event.
   */
  public Throwable getCause()
  {
    return cause;
  }

  // -----------------------------------------------------------------------
  // Object overrides
  // -----------------------------------------------------------------------

  /** Returns a string representation of this event suitable for
   *  logging and debugging. The format is not specified and must not
   *  be parsed.
   */
  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    sb.append("EntityEvent[");
    switch (type)
    {
      case OBJECT_ADDED:    sb.append("OBJECT_ADDED");    break;
      case OBJECT_REMOVED:  sb.append("OBJECT_REMOVED");  break;
      case OBJECT_REBOUND:  sb.append("OBJECT_REBOUND");  break;
      case OBJECT_CHANGED:  sb.append("OBJECT_CHANGED");  break;
      case LISTENER_FAILED: sb.append("LISTENER_FAILED"); break;
      default:
        sb.append("UNKNOWN(").append(type).append(")");
        break;
    }
    sb.append(", affected=").append(affected);
    if (type == OBJECT_REBOUND)
    {
      sb.append(", oldName=").append(oldName);
      sb.append(", newName=").append(newName);
    }
    else if (type == OBJECT_CHANGED)
    {
      sb.append(", modifications=").append(modifications.length);
    }
    else if (type == LISTENER_FAILED)
    {
      sb.append(", cause=").append(cause);
    }
    sb.append("]");
    return sb.toString();
  }
}
