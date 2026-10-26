package com.optimasc.entity;

import com.optimasc.attribute.AttributeModification;
import com.optimasc.attribute.Attributes;
import com.optimasc.entity.Entity;

/** Extends {@link Entity} with operations that modify the 
 *  node's attributes or its position in the hierarchy. A node 
 *  implementing this interface supports mutation; a node 
 *  implementing only {@link Entity} does not.
 *  
 *  <p>Consumers determine whether a given node is modifiable 
 *  by testing with <code>instanceof</code>:</p>
 *  
 *  <pre>
 *    if (node instanceof ModifiableEntity) {
 *        ModifiableEntity mNode = (ModifiableEntity) node;
 *        mNode.modifyAttributes(mods);
 *    }
 *  </pre>
 *  
 *  <p>All modification methods in this interface honor an 
 *  <em>all-or-nothing validation</em> contract: before any 
 *  change takes effect, the implementation validates the 
 *  requested modification against the schema and any 
 *  implementation-defined integrity rules. If validation fails, 
 *  no change is applied and the method throws 
 *  {@link ModificationException}.</p>
 *  
 *  <p>Implementations are strongly encouraged to also provide 
 *  application atomicity &mdash; that is, to ensure that 
 *  concurrent readers observe either the full pre-modification 
 *  state or the full post-modification state, never an 
 *  intermediate. Implementations that cannot provide this 
 *  stronger guarantee (for example, because the backing store 
 *  does not support transactions) must document the exception 
 *  clearly.</p>
 *  
 *  <p>This interface does not specify how interested parties 
 *  are notified of modifications. User-interface consumers 
 *  that require notification should register listeners through 
 *  the appropriate adapter-specific mechanism when available
 *  ({@see ObservableEntity}).</p>
 *  
 *  @author Carl Eric Codere
 */


public interface ModifiableEntity extends Entity
{
  /** Applies a batch of attribute modifications to this node.
   *  
   *  <p>This method honors the following contract:</p>
   *  <ol>
   *    <li>Before applying any modification, the implementation 
   *        validates every modification in the batch against the 
   *        schema and any implementation-defined integrity rules. 
   *        Validation is sequential: modification <em>i</em> is 
   *        validated against the state that would result from 
   *        applying modifications <em>0</em> through <em>i-1</em>. 
   *        This allows batches that include, for example, a 
   *        <code>REMOVE_ATTRIBUTE</code> followed by an 
   *        <code>ADD_ATTRIBUTE</code> on the same single-valued 
   *        attribute to validate correctly.</li>
   *    <li>If validation fails for any modification, no 
   *        modification is applied. The implementation throws 
   *        {@link EntityException}, identifying the index 
   *        of the failed modification and the reason.</li>
   *    <li>If validation succeeds for every modification, the 
   *        implementation applies all modifications and returns 
   *        normally.</li>
   *  </ol>
   *  
   *  <p>The semantics of individual modification operations 
   *  follow those defined by 
   *  {@link AttributeModification}:</p>
   *  <ul>
   *    <li><code>ADD_ATTRIBUTE</code> adds the given values to 
   *        the named attribute, creating the attribute if 
   *        necessary.</li>
   *    <li><code>REPLACE_ATTRIBUTE</code> replaces all existing 
   *        values of the named attribute with the given values, 
   *        creating the attribute if necessary.</li>
   *    <li><code>REMOVE_ATTRIBUTE</code> removes the given values 
   *        from the named attribute, or removes the entire 
   *        attribute if no values are specified.</li>
   *  </ul>
   *  
   *  <p>This method applies modifications unconditionally with 
   *  respect to the current state: a <code>REPLACE_ATTRIBUTE</code> 
   *  operation does not verify that the attribute's current value 
   *  matches any expected value. Callers that require optimistic 
   *  locking must implement it at a higher level.</p>
   *  
   *  <p>Two categories of attribute are not writable through this 
   *  method and any modification targeting them is rejected with 
   *  {@link EntityException#CANNOT_UPDATE_ATTRIBUTE}:</p>
   *  <ul>
   *    <li><strong>Backend-managed attributes</strong> &mdash; 
   *        attributes whose values are maintained by the underlying 
   *        store rather than by client applications. Typical 
   *        examples are entry timestamps, server-generated 
   *        identifiers, and replication or housekeeping metadata. 
   *        Backends that expose a schema typically mark such 
   *        attributes through their usage classification (for 
   *        example, a non-{@code userApplication} value of 
   *        {@link com.optimasc.entity.schema.AttributeUsage}); 
   *        backends that do not expose schema information apply 
   *        the equivalent restriction internally based on the 
   *        attribute's role in the store.</li>
   *    <li><strong>Naming attributes of this entity</strong> 
   *        &mdash; attributes whose values contribute to this 
   *        entity's current last name component. These attributes 
   *        are otherwise ordinary user-writable attributes and 
   *        may be freely modified on entities where they are not 
   *        naming attributes; the restriction is structural and 
   *        applies only to the entity for which the attribute 
   *        currently participates in naming. To change a naming 
   *        attribute's value, use {@link #rebind(EntityName)}, 
   *        which updates both the attribute value and the 
   *        parent's binding of this entity atomically.</li>
   *  </ul>
   *  
   *  <p>Schema introspection, when available, lets callers 
   *  distinguish the two cases programmatically &mdash; for 
   *  example by reading an attribute's usage classification or by 
   *  inspecting the entity's class definition for its naming 
   *  attributes. Backends without schema support are nonetheless 
   *  required to enforce the same restrictions; callers without 
   *  schema access must rely on the exception message or simply 
   *  treat {@link EntityException#CANNOT_UPDATE_ATTRIBUTE} as a 
   *  signal that the modification path is not available for that 
   *  attribute on that entity.</p>
   *  
   *  @param mods [in] The modifications to apply, in order. 
   *    Must not be <code>null</code>; must not contain 
   *    <code>null</code> elements. An empty array is permitted 
   *    and is a no-op.
   *  @throws EntityException with error code 
   *    {@link EntityException#CANNOT_UPDATE_ATTRIBUTE} if any 
   *    modification targets a backend-managed attribute or a 
   *    naming attribute of this entity; or with another error 
   *    code if validation fails for any modification, or if the 
   *    modifications cannot be applied for an 
   *    implementation-defined reason.
   *  @see #rebind(EntityName)
   */
  void modifyAttributes(AttributeModification[] mods) 
      throws EntityException;  

   
   /** Creates a new child entity of the given object class, with 
    *  the given last name component and additional attributes, and 
    *  adds it to this entity's children.
    *  
    *  <p>The {@code name} parameter is the last component of the 
    *  new child's name-in-namespace. After this method returns, 
    *  the new child satisfies:</p>
    *  <pre>
    *    child.getNameInNamespace().getLastComponent().equals(name)
    *  </pre>
    *  <p>and is reachable from the namespace through 
    *  {@link Namespace#resolve(EntityName)} at the name formed by 
    *  appending {@code name} to this entity's name-in-namespace.</p>
    *  
    *  <p>The backend translates {@code name} into a value for 
    *  whatever attribute (or attributes) it uses as the naming 
    *  attribute for the given object class &mdash; the 
    *  {@code cn} value for a typical LDAP entry, the file name 
    *  for a filesystem entry, the {@code @id} value for an XML 
    *  element, and so on. Callers work only with the structural 
    *  last-component value; the attribute-level mapping is a 
    *  backend concern.</p>
    *  
    *  <p>If the {@code attributes} parameter also supplies a value 
    *  for what the backend would use as the naming attribute, and 
    *  that value disagrees with {@code name}, the modification is 
    *  rejected with 
    *  {@link EntityException#INVALID_ATTRIBUTE_VALUE}: the caller 
    *  has specified two contradictory values for the same 
    *  conceptual slot and must reconcile them. Supplying the 
    *  naming attribute redundantly with a value that agrees with 
    *  {@code name} is permitted and has no additional effect.</p>
    *  
    *  <p>The {@code attributes} parameter supplies additional 
    *  attribute values to set on the new child at creation time. 
    *  Backends that validate schema constraints at creation 
    *  (notably LDAP) require all mandatory attributes other than 
    *  the naming attribute to be present in this parameter; 
    *  callers must supply them. The implementation throws 
    *  {@link EntityException#INVALID_NEW_ATTRIBUTE_VALUE} if 
    *  mandatory attributes are missing, or 
    *  {@link EntityException#INVALID_ATTRIBUTE_VALUE} if any 
    *  value is not valid for its attribute's syntax.</p>
    *  
    *  <p>Pass <code>null</code> for the attributes parameter to 
    *  indicate that no additional attributes are being supplied. 
    *  This is equivalent to passing an empty attribute set, and 
    *  is convenient for object classes whose only mandatory 
    *  attribute is the naming attribute.</p>
    *  
    *  @param childClass [in] The object class of the new child. 
    *    Must not be <code>null</code>.
    *  @param name [in] The last name component for the new child. 
    *    Must not be <code>null</code>; must not be empty; must be 
    *    syntactically valid as a name component in this entity's 
    *    namespace.
    *  @param attributes [in] Additional attributes to set on the 
    *    new child at creation time, or <code>null</code> if none.
    *  @return The newly-created child. Never <code>null</code>.
    *  @throws EntityException if the creation fails. Specific 
    *    error codes are used for well-defined failure modes:
    *    <ul>
    *      <li>{@link EntityException#DUPLICATE_ENTITY} &mdash; 
    *          a child with the same last name component already 
    *          exists under this entity.</li>
    *      <li>{@link EntityException#INCOMPATIBLE_CONTAINER} 
    *          &mdash; the class is not permitted beneath this 
    *          entity, or the schema is otherwise violated.</li>
    *      <li>{@link EntityException#INVALID_NEW_ATTRIBUTE_VALUE} 
    *          &mdash; a mandatory attribute for the object class 
    *          is missing from {@code attributes}.</li>
    *      <li>{@link EntityException#INVALID_ATTRIBUTE_VALUE} 
    *          &mdash; an attribute value does not conform to its 
    *          declared syntax, or the value supplied for the 
    *          naming attribute in {@code attributes} disagrees 
    *          with {@code name}.</li>
    *      <li>{@link EntityException} with another code &mdash; 
    *          any other backend failure.</li>
    *    </ul>
    *  @see #rebind(EntityName)
    */
   ModifiableEntity addChild(String childClass, 
                             String name,
                             Attributes attributes) 
       throws EntityException;   
   
   
   /** Removes the given child from this node's children.
   *
   *  <p>The given child must be a direct child of this node,
   *  as returned by {@link ContainerEntity#getChildren()}. An
   *  attempt to remove a node that is not a direct child causes
   *  this method to throw {@link EntityException}.</p>
   *
   *  <p>A child that has no children of its own &mdash; a leaf,
   *  or an empty container &mdash; can always be removed through
   *  this method. What happens when the child still has children
   *  depends on the namespace, and is declared by
   *  {@link Features#SUBTREE_REMOVE}:</p>
   *  <ul>
   *    <li>if the feature is present, the child and its entire
   *        subtree are removed from the backend;</li>
   *    <li>if the feature is absent, the removal is refused with
   *        {@link EntityException#CONTAINER_NOT_EMPTY} and
   *        nothing is removed. This method never emulates the
   *        removal of a subtree by removing its entities one at
   *        a time.</li>
   *  </ul>
   *
   *  <p>To remove a subtree whatever the backend, with progress
   *  reporting and cancellation, use
   *  {@link EntityUtilities#removeTree}.</p>
   *
   *  <p>Atomicity: this operation is atomic from the perspective
   *  of other observers of the namespace. Either the child is
   *  removed &mdash; together with its subtree, on namespaces
   *  that advertise {@link Features#SUBTREE_REMOVE} &mdash; or
   *  nothing is; a partially-removed subtree is never
   *  visible.</p>
   *
   *  <p>Implementations may impose additional constraints on
   *  removal. For example, an implementation may refuse to
   *  remove a child that is referenced from elsewhere in the
   *  backend, or a child that the schema declares as mandatory
   *  beneath this node. Such refusals are reported by throwing
   *  {@link EntityException}.</p>
   *
   *  <p>After this method returns normally, the given child
   *  reference, and any reference to one of its descendants,
   *  should not be used for further operations. Its behavior is
   *  undefined.</p>
   *
   *  @param child [in] The child to remove. Must not be
   *    <code>null</code>; must be a direct child of this node.
   *  @throws EntityException with error code
   *    {@link EntityException#CONTAINER_NOT_EMPTY} if the child
   *    still has children and the namespace does not advertise
   *    {@link Features#SUBTREE_REMOVE}; or with another error
   *    code if the given node is not a child of this node, or if
   *    the removal is refused for an implementation-defined
   *    reason.
   *  @see EntityUtilities#removeTree
   *  @see Features#SUBTREE_REMOVE
   */   
  void removeChild(Entity child) 
      throws EntityException;
  
  /** Moves this entity to a new location in the namespace,
   *  changing its parent, its last name component, or both
   *  atomically.
   *
   *  <p>This is the structural counterpart to
   *  {@link #modifyAttributes(AttributeModification[])}: where
   *  {@code modifyAttributes} changes the values an entity
   *  carries, {@code move} changes how the entity is named and
   *  bound within the tree. After a successful call,
   *  {@link Entity#getNameInNamespace()} returns the name formed
   *  by appending {@code newLastComponent} to
   *  {@code destinationParent}'s name-in-namespace, and the
   *  entity is reachable through
   *  {@link Namespace#resolve(EntityName)} at that name.</p>
   *
   *  <p>The operation covers three distinct intents through a
   *  single primitive:</p>
   *
   *  <ul>
   *    <li><strong>Rename</strong> &mdash; {@code destinationParent}
   *        is this entity's current parent and
   *        {@code newLastComponent} differs from the current last
   *        component. The entity stays where it is and its leaf
   *        identifier changes (filesystem {@code rename} within a
   *        directory, LDAP {@code modifyDN} with a new RDN but no
   *        {@code newSuperior}). Example:
   *        <pre>
   *  entity.move(entity.getParent(), "newname");
   *        </pre></li>
   *    <li><strong>Move</strong> &mdash; {@code destinationParent}
   *        is a different entity and {@code newLastComponent}
   *        equals the current last component. The entity is
   *        unbound from its current parent and bound under the new
   *        one (filesystem {@code rename} across directories, LDAP
   *        {@code modifyDN} with a {@code newSuperior}). Example:
   *        <pre>
   *  String keepName =
   *      entity.getNameInNamespace().getLastComponent();
   *  entity.move(newParent, keepName);
   *        </pre></li>
   *    <li><strong>Move-with-rename</strong> &mdash; both the
   *        parent and the last component change. This is a single
   *        atomic operation, not a move followed by a rename, and
   *        is the natural way to relocate an entity into a
   *        container that already has a sibling with this entity's
   *        current last component. Example:
   *        <pre>
   *  entity.move(newParent, "renamed-on-arrival");
   *        </pre></li>
   *  </ul>
   *
   *  <p>The naming-attribute mapping is handled by the backend.
   *  Callers work only with the structural last-component value;
   *  whatever attribute (or attributes) the backend uses to
   *  realize naming &mdash; an LDAP RDN value, a filesystem entry
   *  name, an XML {@code @id} &mdash; is updated by the
   *  implementation as part of this operation. Attempting to
   *  change the naming attribute through
   *  {@link #modifyAttributes(AttributeModification[])} is rejected
   *  with {@link EntityException#CANNOT_UPDATE_ATTRIBUTE}; this
   *  method is the only path that can change it.</p>
   *
   *  <p>{@code destinationParent} must belong to the same
   *  {@link Namespace} as this entity. Cross-namespace moves
   *  &mdash; relocating an entity from one backend to another
   *  &mdash; are not moves but copy-and-delete operations with
   *  format translation, and must be performed at a higher layer
   *  using {@link com.optimasc.entity.EntityCopier} together with
   *  {@link #removeChild(Entity)}. Implementations must reject
   *  cross-namespace destinations.</p>
   *
   *  <p>The root entity of a namespace cannot be moved: it has no
   *  parent to move within, and its name-in-namespace
   *  ({@link EntityName#EMPTY}) has no last component to preserve
   *  or replace. Attempting to call {@code move} on the root is
   *  an error and implementations reject it with
   *  {@link EntityException#ACTION_FAILED}.</p>
   *
   *  <p>Atomicity: this operation is atomic from the perspective
   *  of other observers of the namespace. No intermediate state
   *  in which the entity is unbound, or bound under both names,
   *  or partially-renamed is visible to other clients. Backends
   *  that cannot guarantee atomicity for the move-with-rename
   *  case throw {@link EntityException#NOT_IMPLEMENTED} rather
   *  than performing a non-atomic emulation.</p>
   *
   * @param destinationParent [in] The entity that will become
   *   this entity's parent after the move. Must not be
   *   {@code null}. Must be a {@link ContainerEntity} that
   *   accepts children of this entity's object class. Must
   *   belong to the same namespace as this entity.
   * @param newLastComponent  [in] The last name component this
   *   entity will have after the move. Must not be {@code null}
   *   or empty. Must satisfy the syntactic constraints the
   *   destination parent imposes on child names.
   * @throws IllegalArgumentException if either argument is
   *   {@code null}, or if {@code newLastComponent} is empty.
   * @throws EntityException with error code:
   *   <ul>
   *     <li>{@link EntityException#DESTINATION_RESOURCE_NOT_FOUND}
   *         if {@code destinationParent} no longer exists in the
   *         namespace.</li>
   *     <li>{@link EntityException#INCOMPATIBLE_CONTAINER} if
   *         {@code destinationParent} does not accept children
   *         of this entity's object class, rejects the syntactic
   *         shape of {@code newLastComponent}, or belongs to a
   *         different namespace.</li>
   *     <li>{@link EntityException#DUPLICATE_ENTITY} if an entity
   *         already exists under {@code destinationParent} with
   *         last component {@code newLastComponent}.</li>
   *     <li>{@link EntityException#CANNOT_UPDATE_ATTRIBUTE} if
   *         the backend's naming attribute is operational or
   *         otherwise not writable for this entity (rare; usually
   *         indicates an entity that should not have implemented
   *         {@code ModifiableEntity} in the first place).</li>
   *     <li>{@link EntityException#NOT_IMPLEMENTED} if the
   *         backend supports modification in general but cannot
   *         perform the requested move atomically.</li>
   *     <li>{@link EntityException#ACTION_FAILED} if the
   *         operation fails for any other backend-specific
   *         reason.</li>
   *   </ul>
   * @see Entity#getNameInNamespace()
   * @see EntityName
   * @see com.optimasc.entity.EntityCopier
   */
  void move(Entity destinationParent, String newLastComponent)
      throws EntityException;  
}