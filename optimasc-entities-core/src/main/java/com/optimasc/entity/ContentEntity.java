package com.optimasc.entity;

import java.io.InputStream;

/** Extends {@link Entity} with access to the content of the resource
 *  that the entity represents.
 *
 *  <p>The <em>content</em> of an entity is the sequence of bytes of
 *  its resource: the bytes of a file in a filesystem, the bytes
 *  downloaded from the resource of a UPnP ContentDirectory item, the
 *  data bytes of a RIFF or PNG chunk, and so on. Attributes describe
 *  the content; this interface gives access to it.</p>
 *
 *  <p>Consumers determine whether a given entity has accessible
 *  content by testing with <code>instanceof</code>:</p>
 *
 *  <pre>
 *    if (entity instanceof ContentEntity) {
 *        InputStream in = ((ContentEntity) entity).openInputStream();
 *        try {
 *            // read the content
 *        } finally {
 *            in.close();
 *        }
 *    }
 *  </pre>
 *
 *  <p>This follows the same capability-detection pattern as
 *  {@link ContainerEntity} and {@link ModifiableEntity}. Whether an
 *  adapter's entities implement this interface is decided by the
 *  capabilities of the backend, not by the current state of a
 *  particular item: an item whose content cannot currently be read
 *  (for example because of access rights) still implements this
 *  interface, and {@link #openInputStream()} fails at call time.</p>
 *
 *  <p>Entities whose resource is a list of children rather than a
 *  sequence of bytes, such as filesystem folders, do not implement
 *  this interface. Link entities must not implement this interface;
 *  content is read from the resolved target, not from the link.</p>
 *
 *  <h2>Resource identification</h2>
 *
 *  <p>The content accessed through this interface is that of the
 *  entity's primary resource. When the entity exposes a
 *  {@code locator} attribute with several values, the primary
 *  resource is the one identified by its first value. The
 *  {@code locator} attribute identifies the content; it is not
 *  required to be usable for accessing it.</p>
 *
 *  <h2>Reading</h2>
 *
 *  <p>Reading is supported by every implementation of this
 *  interface. The stream returned by {@link #openInputStream()} may
 *  implement additional interfaces providing extra capabilities, such
 *  as positioning within the content through
 *  {@code com.optimasc.io.Seekable}. Consumers must test for such
 *  interfaces with <code>instanceof</code> and must not assume them.
 *  The returned stream is not required to support
 *  {@link InputStream#mark(int)}; consumers that need marking must
 *  wrap the stream themselves.</p>
 *
 *  <h2>Replacing the content</h2>
 *
 *  <p>Replacing the content is optional. Whether the entities of a
 *  namespace support it is a property of the backend as a whole, and
 *  is declared by the namespace through the
 *  {@link Features#CONTENT_WRITE} feature. When that feature is not
 *  declared, {@link #openContentWriter()} always fails with
 *  {@link EntityException#NOT_IMPLEMENTED}. When it is declared,
 *  {@link #openContentWriter()} may still fail for a particular item,
 *  for example with {@link EntityException#ACCESS_DENIED}.</p>
 *
 *  <p>Unlike entry modification, which is a cluster of operations
 *  detected with <code>instanceof {@link ModifiableEntity}</code>,
 *  content replacement is a single operation. It is therefore
 *  declared on this interface and detected through a feature rather
 *  than through a separate sub-interface.</p>
 *
 *  <p>Content replacement is atomic: the content is replaced only
 *  when {@link ContentWriter#commit()} completes successfully. Until
 *  then, readers of the entity observe the original content. If the
 *  replacement is abandoned, or {@code commit()} fails, the original
 *  content is left unchanged. While a content writer is open, the
 *  original content may still be read through
 *  {@link #openInputStream()}, so that a consumer can produce the new
 *  content from the original one, for example to rewrite the embedded
 *  metadata of a file while copying its payload unchanged.</p>
 *
 *  <p>Content replacement is independent of {@link ModifiableEntity}.
 *  It changes the resource, not the position of the entity in the
 *  namespace or its user-modifiable attributes.</p>
 *
 *  <p>After a successful commit, attributes that the backend derives
 *  from the content, such as the size or the storage modification
 *  time, reflect the new content. If the entity is an
 *  {@link ObservableEntity}, the replacement is reported as a single
 *  {@link com.optimasc.entity.event.EntityEvent#OBJECT_CHANGED} event
 *  carrying an empty array of modifications, since no attribute was
 *  modified through
 *  {@link ModifiableEntity#modifyAttributes(com.optimasc.attribute.AttributeModification[])}.</p>
 *
 *  <h2>Threading</h2>
 *
 *  <p>Implementations are not required to be thread-safe. Opening,
 *  reading, writing and committing content is backend access: in a
 *  user-interface consumer it is expected to be performed on the same
 *  background thread as other backend operations, never on the event
 *  dispatch thread.</p>
 *
 *  @see ContentWriter
 *  @see Features#CONTENT_WRITE
 *  @see ContainerEntity
 *  @author Carl Eric Codere
 */
public interface ContentEntity extends Entity
{
  /** Opens the content of this entity for reading.
   *
   *  <p>Each call returns a new, independent stream positioned at the
   *  start of the content. The caller owns the returned stream and
   *  must close it. A resource with no content returns a stream that
   *  is immediately at its end.</p>
   *
   *  <p>Failures that occur while reading, after the stream has been
   *  opened, are reported by the stream as
   *  {@link java.io.IOException}.</p>
   *
   *  @return A non-null stream over the content of this entity.
   *  @throws EntityException with error code
   *    {@link EntityException#NO_SUCH_OBJECT} if the entity no longer
   *    exists in the backend;
   *    {@link EntityException#ACCESS_DENIED} if the content cannot be
   *    read with the current access rights; or
   *    {@link EntityException#ACTION_FAILED} if the content cannot be
   *    opened because of any other backend failure.
   */
  InputStream openInputStream() throws EntityException;

  /** Opens a writer that replaces the content of this entity.
   *
   *  <p>The returned writer starts with empty content: the caller
   *  writes the complete new content, not a modification of the
   *  existing one. Nothing is replaced until
   *  {@link ContentWriter#commit()} is called and completes
   *  successfully.</p>
   *
   *  <p>The caller owns the returned writer and must close it, whether
   *  or not it was committed:</p>
   *
   *  <pre>
   *    ContentWriter writer = entity.openContentWriter();
   *    try {
   *        OutputStream out = writer.getOutputStream();
   *        // write the complete new content to out
   *        writer.commit();
   *    } finally {
   *        writer.close();   // abandons the replacement if not committed
   *    }
   *  </pre>
   *
   *  <p>When content replacement is not supported by the backend, this
   *  method fails immediately, before any data is written. The
   *  behaviour when several writers are open on the same entity at the
   *  same time is backend-defined; callers must not rely on it.</p>
   *
   *  @return A non-null writer for the new content of this entity.
   *  @throws EntityException with error code
   *    {@link EntityException#NOT_IMPLEMENTED} if the namespace does
   *    not declare the {@link Features#CONTENT_WRITE} feature;
   *    {@link EntityException#NO_SUCH_OBJECT} if the entity no longer
   *    exists in the backend;
   *    {@link EntityException#ACCESS_DENIED} if the content of this
   *    item cannot be replaced with the current access rights;
   *    {@link EntityException#INSUFFICIENT_RESOURCES} if storage for
   *    the new content cannot be allocated; or
   *    {@link EntityException#ACTION_FAILED} if the writer cannot be
   *    opened because of any other backend failure.
   */
  ContentWriter openContentWriter() throws EntityException;
}
