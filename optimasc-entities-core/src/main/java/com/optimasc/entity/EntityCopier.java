package com.optimasc.entity;

/** System-specific capability for duplicating an existing
 *  {@link Entity} under a new parent and name.
 *
 *  <p>{@code EntityCopier} is exposed through
 *  {@link Namespace#getCapability(Class)
 *  Namespace.getCapability(EntityCopier.class)}, not as a method
 *  on {@link Entity} or {@link ModifiableEntity}, because copy
 *  semantics vary too widely across backends to fit a single
 *  uniform method:</p>
 *
 *  <ul>
 *    <li><strong>Filesystems</strong> copy file content as a byte
 *        stream and replicate selected metadata (timestamps,
 *        permissions, extended attributes). Modern filesystems
 *        may implement copy as a reflink or copy-on-write clone,
 *        moving no data at all.</li>
 *    <li><strong>LDAP directories</strong> copy by reading all
 *        attributes and writing them under a new DN. Server
 *        extensions may provide native cloning.</li>
 *    <li><strong>SQL row stores</strong> copy by
 *        {@code INSERT ... SELECT}, typically excluding
 *        auto-generated identity columns from the source.</li>
 *    <li><strong>Contact stores</strong> (vCard, VCF) clone the
 *        record but regenerate the UID, since UIDs are supposed
 *        to be unique per contact.</li>
 *    <li><strong>RIFF chunk files</strong> and similar
 *        flat-binary formats may not have meaningful in-place
 *        copy at all; copying a chunk means re-serializing it
 *        elsewhere.</li>
 *  </ul>
 *
 *  <p>Each backend implements an {@code EntityCopier} that knows
 *  the right thing for its data. Backends that have no native
 *  copy semantics (or for which the entity-attribute model does
 *  not capture all of the entity's state) may return
 *  {@code null} from {@code getCapability(EntityCopier.class)};
 *  {@link EntityUtilities#copyTree} then falls back to a generic
 *  per-attribute copy built on top of
 *  {@link ModifiableEntity#addChild(String, String,
 *  com.optimasc.attribute.Attributes)}, and callers must
 *  understand that the result may be incomplete.</p>
 *
 *  <h2>Single entity only</h2>
 *
 *  <p>A copier copies exactly one entity: its attributes and
 *  whatever content the backend attaches to the entity itself
 *  (the bytes of a file, the body of a chunk). It never copies
 *  the children of the entity. Copying a container therefore
 *  produces a new, empty container.</p>
 *
 *  <p>Copying a whole subtree is the role of
 *  {@link EntityUtilities#copyTree}, which calls the copier once
 *  for each entity, parents first. Keeping the traversal out of
 *  the copier gives every backend the same behaviour, and lets
 *  the utility report progress and be cancelled between two
 *  entities; a copier takes no part in either.</p>
 *
 *  <h2>Intra-namespace only</h2>
 *
 *  <p>An {@code EntityCopier} obtained from a namespace handles
 *  copies <em>within</em> that namespace: both source and
 *  destination must belong to the same namespace. Cross-namespace
 *  copy &mdash; for example, copying a chunk from
 *  {@code riff:///song.wav} into a directory at
 *  {@code file:///tmp/} &mdash; is a different operation entirely
 *  (read from one, format-translate, write to the other) and
 *  must be performed at a higher application layer. Copier
 *  implementations are required to reject cross-namespace
 *  destinations with
 *  {@link EntityException#INCOMPATIBLE_CONTAINER}.</p>
 *
 *  <h2>Atomicity</h2>
 *
 *  <p>An implementation should perform the copy atomically with
 *  respect to other observers when the backend permits: either
 *  the new entity exists complete, or it does not exist at all.
 *  A subtree copy made with {@link EntityUtilities#copyTree} is a
 *  sequence of such copies and is not atomic.</p>
 *
 *  <h2>Threading</h2>
 *
 *  <p>Implementations are not required to be thread-safe.
 *  Callers must serialize concurrent copy operations on the same
 *  copier instance themselves.</p>
 *
 *  @see Namespace#getCapability(Class)
 *  @see ModifiableEntity#move(Entity, String)
 *  @see EntityUtilities#copyTree
 *  @author Carl Eric Codere
 */
public interface EntityCopier
{
  /** Copies {@code source} into {@code destinationParent} under
   *  the name {@code newLastComponent}, producing and returning
   *  a newly-bound entity.
   *
   *  <p>The resulting entity satisfies:</p>
   *  <pre>
   *  result.getNameInNamespace().getLastComponent()
   *      .equals(newLastComponent)
   *  result.getParent() == destinationParent  // logically
   *  </pre>
   *
   *  <p>What is preserved from {@code source} is backend-defined:
   *  attribute values that the backend considers user-data are
   *  copied; backend-managed attributes (timestamps,
   *  server-generated identifiers, operational metadata) are
   *  regenerated for the new entity rather than carried over.
   *  Specific contracts are documented per backend.</p>
   *
   *  <p>Only {@code source} itself is copied. If {@code source}
   *  is a {@link ContainerEntity}, its children are not copied
   *  and the returned entity is an empty container; to copy the
   *  entire subtree rooted at {@code source}, use
   *  {@link EntityUtilities#copyTree}.</p>
   *
   * @param source [in] The entity to copy. Must not
   *   be {@code null}. Must belong to the same namespace as
   *   {@code destinationParent}.
   * @param destinationParent  [in] The parent under which the
   *   copy is bound. Must not be {@code null}. Must be a
   *   {@link ContainerEntity} that accepts children of
   *   {@code source}'s object class. Must belong to the same
   *   namespace as {@code source}.
   * @param newLastComponent [in] The last name component the
   *   new entity will have. Must not be {@code null} or empty.
   * @return The non-null newly-created copy of {@code source},
   *   bound under {@code destinationParent}.
   * @throws IllegalArgumentException if any argument is
   *   {@code null}, or if {@code newLastComponent} is empty.
   * @throws EntityException with error code:
   *   <ul>
   *     <li>{@link EntityException#DESTINATION_RESOURCE_NOT_FOUND}
   *         if {@code destinationParent} no longer exists in the
   *         namespace.</li>
   *     <li>{@link EntityException#INCOMPATIBLE_CONTAINER} if
   *         {@code destinationParent} does not accept children
   *         of {@code source}'s object class, rejects the
   *         syntactic shape of {@code newLastComponent}, or
   *         belongs to a different namespace from
   *         {@code source}.</li>
   *     <li>{@link EntityException#DUPLICATE_ENTITY} if an entity
   *         already exists under {@code destinationParent} with
   *         last component {@code newLastComponent}.</li>
   *     <li>{@link EntityException#NOT_IMPLEMENTED} if the
   *         backend supports the entity model but cannot perform
   *         copy &mdash; for example, a read-only mounted source
   *         or a destination format that has no representation
   *         for {@code source}'s object class.</li>
   *     <li>{@link EntityException#ACTION_FAILED} if the
   *         operation fails for any other backend-specific
   *         reason.</li>
   *   </ul>
   */
  Entity copy(Entity source, Entity destinationParent,
      String newLastComponent)
      throws EntityException;
}