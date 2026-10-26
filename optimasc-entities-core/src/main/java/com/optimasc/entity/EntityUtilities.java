package com.optimasc.entity;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import com.optimasc.entity.event.OperationEvent;
import com.optimasc.entity.event.OperationListener;

/** Static operations on whole subtrees of entities, built on the
 *  single-entity primitives of the entity interfaces.
 *
 *  <p>The modification primitives of this package act on one
 *  entity at a time and are atomic:
 *  {@link EntityCopier#copy EntityCopier.copy} copies a single
 *  entity, and {@link ModifiableEntity#removeChild
 *  ModifiableEntity.removeChild} removes a single entity (or, on
 *  backends that advertise {@link Features#SUBTREE_REMOVE}, a
 *  subtree in one atomic step). Operations that span a whole
 *  subtree, which are neither atomic nor short, are provided here,
 *  one layer above the backends, so that every backend gets them
 *  with identical behaviour and no backend has to implement
 *  them.</p>
 *
 *  <p>Because the traversal is driven from this class, it is also
 *  this class that reports progress and honours cancellation,
 *  through an optional
 *  {@link com.optimasc.entity.event.OperationListener}. A consumer
 *  therefore obtains per-entity progress and the ability to cancel
 *  between two entities on every backend, without having to ask
 *  the backend whether it supports them.</p>
 *
 *  <p>The cost of this layering is one backend call per entity,
 *  plus one enumeration per container. This is what a local
 *  filesystem or an LDAP directory requires in any case; on a
 *  remote backend it amounts to one round trip per entity.</p>
 *
 *  <p>The methods of this class are not atomic. If a method fails
 *  or is cancelled part-way, the entities processed up to that
 *  point stay processed, the exception is propagated unchanged,
 *  and any clean-up is the decision of the caller.</p>
 *
 *  <p>This class holds no state. Its methods may be called from
 *  any thread, subject to the threading rules of the entities and
 *  namespace they are applied to; in a user-interface consumer
 *  they are expected to be called from a worker thread.</p>
 *
 *  @see EntityCopier
 *  @see ModifiableEntity
 *  @see com.optimasc.entity.event.OperationListener
 *  @author Carl Eric Codere
 */
public final class EntityUtilities
{
  // -----------------------------------------------------------------------
  // Copy
  // -----------------------------------------------------------------------

  /** Copies {@code source} and, if it is a container, its entire
   *  subtree into {@code destinationParent} under the name
   *  {@code newLastComponent}.
   *
   *  <p>The subtree is copied entity by entity, parents first:
   *  {@code source} is copied under {@code destinationParent},
   *  then each child of {@code source} is copied under that new
   *  copy with its own last name component, and so on down the
   *  tree. Descent is decided by {@code instanceof}
   *  {@link ContainerEntity} on the source side; a link entity,
   *  which never implements {@link ContainerEntity}, is therefore
   *  copied as a link and never followed.</p>
   *
   *  <p>Each entity is copied with the {@link EntityCopier} of the
   *  namespace, obtained through
   *  {@link Namespace#getCapability(Class)
   *  getCapability(EntityCopier.class)}. If the namespace provides
   *  no copier, each entity is instead re-created with
   *  {@link ModifiableEntity#addChild(String, String,
   *  com.optimasc.attribute.Attributes) addChild}, from the object
   *  class and the complete attribute set of its source. This
   *  generic copy carries over only what the entity-attribute
   *  model exposes and may therefore be incomplete; it may also
   *  be refused by a backend that does not accept some of the
   *  source attributes at creation time.</p>
   *
   *  <p>If {@code listener} is not {@code null}, it is invoked
   *  once before each entity is copied. The source entity of the
   *  event is the entity, on the source side, that is about to be
   *  copied. The total reported is always
   *  {@link com.optimasc.entity.event.OperationEvent#UNKNOWN}.</p>
   *
   *  <p>The copy is not atomic. Other observers of the namespace
   *  may see a partial destination subtree while it is in
   *  progress, and if it fails or is cancelled the entities
   *  already created under {@code destinationParent} remain.</p>
   *
   * @param source             [in] The entity at the root of the
   *   subtree to copy. Must not be {@code null}.
   * @param destinationParent  [in] The parent under which the
   *   copy is bound. Must not be {@code null}. Must belong to the
   *   same namespace as {@code source}, and must be neither
   *   {@code source} itself nor one of its descendants.
   * @param newLastComponent   [in] The last name component the
   *   copy of {@code source} will have. Must not be {@code null}
   *   or empty.
   * @param listener           [in] The listener that receives
   *   progress reports and may request cancellation, or
   *   {@code null} if the caller requires neither.
   * @return The non-null copy of {@code source}, bound under
   *   {@code destinationParent}.
   * @throws IllegalArgumentException if {@code source},
   *   {@code destinationParent} or {@code newLastComponent} is
   *   {@code null}, or if {@code newLastComponent} is empty.
   * @throws EntityException with error code:
   *   <ul>
   *     <li>{@link EntityException#INCOMPATIBLE_CONTAINER} if
   *         {@code destinationParent} belongs to a different
   *         namespace from {@code source}, or is {@code source}
   *         itself or one of its descendants. Nothing has been
   *         copied.</li>
   *     <li>{@link EntityException#NOT_IMPLEMENTED} if the
   *         namespace provides no {@link EntityCopier} and an
   *         entity has to be created under a parent that is not a
   *         {@link ModifiableEntity}.</li>
   *     <li>{@link EntityException#OPERATION_CANCELLED} if
   *         {@code listener} requested cancellation. The entities
   *         copied up to that point remain.</li>
   *     <li>any error code reported by
   *         {@link EntityCopier#copy EntityCopier.copy},
   *         {@link ModifiableEntity#addChild addChild} or
   *         {@link ContainerEntity#getChildren() getChildren} for
   *         one of the entities of the subtree. The entities
   *         copied up to that point remain.</li>
   *   </ul>
   * @see EntityCopier#copy(Entity, Entity, String)
   */
  public static Entity copyTree(Entity source,
      Entity destinationParent, String newLastComponent,
      OperationListener listener)
      throws EntityException
  {
    if (source == null)
      throw new IllegalArgumentException(
          "'source' must not be null.");
    if (destinationParent == null)
      throw new IllegalArgumentException(
          "'destinationParent' must not be null.");
    if (newLastComponent == null || newLastComponent.length() == 0)
      throw new IllegalArgumentException(
          "'newLastComponent' must not be null or empty.");

    Namespace namespace = source.getNamespace();
    if (!namespace.equals(destinationParent.getNamespace()))
      throw new EntityException(
          EntityException.INCOMPATIBLE_CONTAINER,
          "Source and destination belong to different namespaces.");

    // A destination inside the source subtree would make the walk
    // copy its own output, without end.
    EntityName sourceName = source.getNameInNamespace();
    if (destinationParent.getNameInNamespace().startsWith(sourceName))
      throw new EntityException(
          EntityException.INCOMPATIBLE_CONTAINER,
          "The destination is the source itself or one of its "
          + "descendants.");

    EntityCopier copier =
        namespace.getCapability(EntityCopier.class);
    return copyNode(copier, source, destinationParent,
        newLastComponent, new Progress(listener));
  }

  /** Copies one entity, then recurses into its children.
   *
   * @param copier            The namespace copier, or
   *   {@code null} to use the generic attribute-based copy.
   * @param source            The entity to copy.
   * @param destinationParent The parent of the copy.
   * @param name              The last name component of the copy.
   * @param progress          The progress state of the operation.
   * @return The copy of {@code source}.
   */
  private static Entity copyNode(EntityCopier copier, Entity source,
      Entity destinationParent, String name, Progress progress)
  {
    progress.before(source);

    Entity copy;
    if (copier != null)
    {
      copy = copier.copy(source, destinationParent, name);
    }
    else
    {
      if (!(destinationParent instanceof ModifiableEntity))
        throw new EntityException(
            EntityException.NOT_IMPLEMENTED,
            "The namespace provides no EntityCopier and the "
            + "destination parent is not modifiable.");
      copy = ((ModifiableEntity) destinationParent).addChild(
          source.getObjectClass(), name, source.getAttributes(null));
    }
    progress.completed();

    if (source instanceof ContainerEntity)
    {
      Entity[] children =
          snapshot(((ContainerEntity) source).getChildren());
      for (int i = 0; i < children.length; i++)
      {
        Entity child = children[i];
        copyNode(copier, child, copy,
            child.getNameInNamespace().getLastComponent(), progress);
      }
    }
    return copy;
  }

  // -----------------------------------------------------------------------
  // Remove
  // -----------------------------------------------------------------------

  /** Removes {@code child} from {@code parent} together with its
   *  entire subtree, whether or not the backend can remove a
   *  non-empty entity itself.
   *
   *  <p>The method proceeds in one of two ways, selected by
   *  {@link Features#SUBTREE_REMOVE} on the namespace of
   *  {@code parent}:</p>
   *
   *  <ul>
   *    <li><strong>Feature present</strong> &mdash; the backend
   *        removes a subtree itself. This method makes a single
   *        call to {@link ModifiableEntity#removeChild(Entity)
   *        parent.removeChild(child)}; the removal is atomic and
   *        is seen by the caller as one step.</li>
   *    <li><strong>Feature absent</strong> &mdash; the backend
   *        removes empty entities only. This method removes the
   *        subtree entity by entity, children first: the
   *        descendants of {@code child} are removed from the
   *        bottom of the tree upwards, each through
   *        {@code removeChild} on its own parent, and
   *        {@code child} is removed last. Descent is decided by
   *        {@code instanceof} {@link ContainerEntity}; a link
   *        entity, which never implements
   *        {@link ContainerEntity}, is therefore removed as a
   *        link and never followed.</li>
   *  </ul>
   *
   *  <p>If {@code listener} is not {@code null}, it is invoked
   *  once before each call to {@code removeChild}. The source
   *  entity of the event is the entity about to be removed. With
   *  {@link Features#SUBTREE_REMOVE} there is consequently exactly
   *  one event, for {@code child}; without it there is one event
   *  per entity of the subtree. The total reported is always
   *  {@link com.optimasc.entity.event.OperationEvent#UNKNOWN}. A
   *  consumer can test the feature beforehand to decide how to
   *  present the operation: as a single indeterminate step, or as
   *  a running count that can be cancelled.</p>
   *
   *  <p>Entity-by-entity removal is not atomic. Other observers of
   *  the namespace may see a partially-removed subtree while it is
   *  in progress, and if it fails or is cancelled the entities
   *  already removed are not restored. Entities added to the
   *  subtree by another client while the removal is in progress
   *  are not removed and cause the removal of their container to
   *  fail with {@link EntityException#CONTAINER_NOT_EMPTY}.</p>
   *
   *  <p>After this method returns normally, {@code child} and the
   *  references to its descendants should not be used for further
   *  operations.</p>
   *
   * @param parent   [in] The entity from which {@code child} is
   *   removed. Must not be {@code null}.
   * @param child    [in] The entity at the root of the subtree to
   *   remove. Must not be {@code null}; must be a direct child of
   *   {@code parent}.
   * @param listener [in] The listener that receives progress
   *   reports and may request cancellation, or {@code null} if
   *   the caller requires neither.
   * @throws IllegalArgumentException if {@code parent} or
   *   {@code child} is {@code null}.
   * @throws EntityException with error code:
   *   <ul>
   *     <li>{@link EntityException#RESTRICTED_OBJECT} if a
   *         non-empty container of the subtree is not a
   *         {@link ModifiableEntity}, so that its children cannot
   *         be removed. The entities removed up to that point are
   *         not restored.</li>
   *     <li>{@link EntityException#OPERATION_CANCELLED} if
   *         {@code listener} requested cancellation. The entities
   *         removed up to that point are not restored.</li>
   *     <li>any error code reported by
   *         {@link ModifiableEntity#removeChild(Entity)
   *         removeChild} or
   *         {@link ContainerEntity#getChildren() getChildren} for
   *         one of the entities of the subtree. The entities
   *         removed up to that point are not restored.</li>
   *   </ul>
   * @see ModifiableEntity#removeChild(Entity)
   * @see Features#SUBTREE_REMOVE
   */
  public static void removeTree(ModifiableEntity parent,
      Entity child, OperationListener listener)
      throws EntityException
  {
    if (parent == null)
      throw new IllegalArgumentException(
          "'parent' must not be null.");
    if (child == null)
      throw new IllegalArgumentException(
          "'child' must not be null.");

    Progress progress = new Progress(listener);
    if (parent.getNamespace().hasFeature(Features.SUBTREE_REMOVE))
    {
      // The backend removes the whole subtree in one atomic step.
      progress.before(child);
      parent.removeChild(child);
      progress.completed();
    }
    else
    {
      removeNode(parent, child, progress);
    }
  }

  /** Removes the children of one entity, then the entity itself.
   *
   * @param parent   The parent of {@code child}.
   * @param child    The entity to remove.
   * @param progress The progress state of the operation.
   */
  private static void removeNode(ModifiableEntity parent,
      Entity child, Progress progress)
  {
    if (child instanceof ContainerEntity)
    {
      Entity[] children =
          snapshot(((ContainerEntity) child).getChildren());
      if (children.length > 0)
      {
        if (!(child instanceof ModifiableEntity))
          throw new EntityException(
              EntityException.RESTRICTED_OBJECT,
              "A non-empty container of the subtree is not "
              + "modifiable; its children cannot be removed.");
        ModifiableEntity container = (ModifiableEntity) child;
        for (int i = 0; i < children.length; i++)
          removeNode(container, children[i], progress);
      }
    }
    progress.before(child);
    parent.removeChild(child);
    progress.completed();
  }

  // -----------------------------------------------------------------------
  // Support
  // -----------------------------------------------------------------------

  /** Copies a list of children into an array, so that the walk is
   *  not affected by changes it makes to the tree itself, nor by a
   *  lazily-populated list.
   *
   * @param children The children as returned by
   *   {@link ContainerEntity#getChildren()}.
   * @return A new array holding the same entities in the same
   *   order.
   */
  private static Entity[] snapshot(List<? extends Entity> children)
  {
    return children.toArray(new Entity[children.size()]);
  }

  /** Progress state of one operation: the listener to notify and
   *  the number of entities processed so far. */
  private static final class Progress
  {
    /** The listener to notify, or {@code null}. */
    private final OperationListener listener;

    /** The number of entities processed so far. */
    private long done;

    Progress(OperationListener listener)
    {
      this.listener = listener;
      this.done     = 0;
    }

    /** Notifies the listener that {@code entity} is about to be
     *  processed, and stops the operation if the listener asks
     *  for it.
     *
     * @param entity The entity about to be processed.
     * @throws EntityException with error code
     *   {@link EntityException#OPERATION_CANCELLED} if the
     *   listener requested cancellation.
     */
    void before(Entity entity)
    {
      if (listener == null)
        return;
      OperationEvent evt = new OperationEvent(entity, done,
          OperationEvent.UNKNOWN);
      listener.operationProgress(evt);
      if (evt.isCancelled())
        throw new EntityException(
            EntityException.OPERATION_CANCELLED,
            "The operation was cancelled by the caller.");
    }

    /** Records that one more entity has been processed. */
    void completed()
    {
      done++;
    }
  }
  
  
  /**
   * Returns the chain of entities leading from the root of the tree down to
   * the specified entity.
   * <p>
   * The first element of the returned array is the root entity (the first
   * ancestor whose parent is <code>null</code>) and the last element is
   * <code>entity</code> itself. For a root entity, the array contains only
   * that entity. The returned array is newly allocated on every call and may
   * be freely modified by the caller.
   * <p>
   * The path is computed purely from parent navigation. Link targets
   * ({@link Entity#getLinkTarget()}) are not followed, and for backends where
   * an entity is reachable through several parents, the path reflects only the
   * parent reported by <code>getParent()</code>, which may differ from the path
   * used to reach the entity.
   *
   * @param entity the entity whose path is requested, must not be
   *        <code>null</code>.
   * @return a non-empty array of entities ordered from the root down to
   *         <code>entity</code>.
   * @throws IllegalArgumentException if <code>entity</code> is
   *         <code>null</code>.
   * @throws EntityException if the parent of an entity in the chain cannot be
   *         retrieved, for example because of a communication or access
   *         failure in the underlying backend.
   */
  public static Entity[] getPathFromRoot(Entity entity) throws EntityException
  {
    if (entity == null)
    {
      throw new IllegalArgumentException("entity must not be null");
    }
    ArrayList<Entity> nodes = new ArrayList<Entity>();
    for (Entity node = entity; node != null; node = node.getParent())
    {
      nodes.add(node);
    }
    Entity[] path = new Entity[nodes.size()];
    int last = path.length - 1;
    for (int i = 0; i < path.length; i++)
    {
      path[i] = nodes.get(last - i);
    }
    return path;
  }

  // -----------------------------------------------------------------------
  // Construction
  // -----------------------------------------------------------------------

  /** Not instantiable. */
  private EntityUtilities()
  {
  }
}
