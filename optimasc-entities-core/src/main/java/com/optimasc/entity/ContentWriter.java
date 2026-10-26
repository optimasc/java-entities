package com.optimasc.entity;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

/** A pending replacement of the content of a {@link ContentEntity},
 *  obtained from {@link ContentEntity#openContentWriter()}.
 *
 *  <p>A content writer separates <em>writing</em> the new content
 *  from <em>replacing</em> the original one. The new content is
 *  written to the stream returned by {@link #getOutputStream()};
 *  the original content is replaced only when {@link #commit()}
 *  completes successfully. Closing the writer without committing it
 *  abandons the replacement and leaves the original content
 *  unchanged. Closing the stream returned by
 *  {@link #getOutputStream()} does not commit the replacement.</p>
 *
 *  <p>The intended usage, compatible with Java 1.5, is:</p>
 *
 *  <pre>
 *    ContentWriter writer = entity.openContentWriter();
 *    try {
 *        OutputStream out = writer.getOutputStream();
 *        // write the complete new content to out
 *        writer.commit();
 *    } finally {
 *        writer.close();
 *    }
 *  </pre>
 *
 *  <p>A writer has three states: <em>open</em>, after creation;
 *  <em>committed</em>, after a successful {@link #commit()}; and
 *  <em>closed</em>, after {@link #close()}. A failed commit leaves
 *  the writer unusable for further writing; it must still be
 *  closed.</p>
 *
 *  <h2>Positioning</h2>
 *
 *  <p>The stream returned by {@link #getOutputStream()} may implement
 *  additional interfaces providing extra capabilities, such as
 *  positioning through {@code com.optimasc.io.Seekable}, where the
 *  position is that of the next write. This allows formats whose
 *  headers refer to offsets of data written later to be produced in
 *  one pass. Consumers must test for such interfaces with
 *  <code>instanceof</code> and must not assume them.</p>
 *
 *  <p>Implementations are not required to be thread-safe.</p>
 *
 *  @see ContentEntity#openContentWriter()
 *  @author Carl Eric Codere
 */
public interface ContentWriter extends Closeable
{
  /** Returns the stream to which the new content is written.
   *
   *  <p>Every call returns the same stream. Data written to it is
   *  not visible through the entity until {@link #commit()}
   *  completes successfully.</p>
   *
   *  @return The non-null stream for the new content.
   *  @throws IllegalStateException if this writer has been committed
   *    or closed, or if a commit has failed.
   */
  OutputStream getOutputStream();

  /** Replaces the content of the entity with the data written to the
   *  stream returned by {@link #getOutputStream()}.
   *
   *  <p>The output stream is flushed and closed by this method if
   *  the caller has not already done so. When this method returns
   *  normally, the replacement is complete and visible to readers of
   *  the entity. When it throws, the original content is unchanged
   *  and the writer can no longer be used except to be closed.</p>
   *
   *  @throws IllegalStateException if this writer has already been
   *    committed or closed, or if a commit has already failed.
   *  @throws EntityException with error code
   *    {@link EntityException#NO_SUCH_OBJECT} if the entity no longer
   *    exists in the backend;
   *    {@link EntityException#ACCESS_DENIED} if the content cannot be
   *    replaced with the current access rights;
   *    {@link EntityException#INSUFFICIENT_RESOURCES} if the backend
   *    has run out of storage; or
   *    {@link EntityException#ACTION_FAILED} if the replacement fails
   *    because of any other backend failure, including a failure
   *    while flushing the written data.
   */
  void commit() throws EntityException;

  /** Releases the resources held by this writer.
   *
   *  <p>If this writer has not been committed, the replacement is
   *  abandoned: the written data is discarded and the original
   *  content is left unchanged. If it has been committed, this
   *  method only releases resources. Calling this method more than
   *  once has no further effect.</p>
   *
   *  @throws IOException if the resources cannot be released. The
   *    original content is unaffected by such a failure when the
   *    writer was not committed.
   */
  void close() throws IOException;
}
