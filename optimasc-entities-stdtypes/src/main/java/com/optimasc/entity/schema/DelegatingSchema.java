package com.optimasc.entity.schema;

import com.optimasc.schema.ClassDefinition;
import com.optimasc.schema.ItemDefinition;

/**
 * A {@link BasicSchema} that resolves point lookups against a parent schema on
 * a local miss, enabling a layered schema hierarchy (for example a
 * domain-specific schema layered over the global {@link StandardSchema}).
 *
 * <h3>Resolution rules</h3>
 * <p>The three single-result lookups - {@link #getItemDefinition(String)},
 * {@link #getTypeDefinition(String)}, and {@link #getClassDefinition(String)} -
 * are <strong>local-first</strong>: this schema's own definitions are consulted
 * first, and the parent is queried only when no local definition matches. This
 * lets a domain schema <em>override</em> a standard definition simply by
 * registering one under the same expanded name or OID; the local definition
 * shadows the parent's. When no local definition exists, the parent's is
 * returned, so a domain item may reference a standard syntax (resolved through
 * the parent) without that syntax being duplicated locally.</p>
 *
 * <h3>Iteration and tree view are local-only</h3>
 * <p>The collection accessors ({@link #getItemDefinitions()},
 * {@link #getTypeDefinitions()}, {@link #getClassDefinitions()}) and the
 * browsable entity tree exposed via {@link #getRoot()} reflect
 * <strong>only the definitions registered in this schema</strong>; they do not
 * include the parent's definitions. This keeps each schema's listing honest
 * about what it itself defines. A consumer that wants to present the full
 * layered picture (for example a visual schema browser) should enumerate each
 * schema in the chain - including the parent - as a distinct root rather than
 * relying on a single flattened iterator.</p>
 *
 * <h3>Registration and overriding</h3>
 * <p>Registration uses the inherited {@link #add(com.optimasc.entity.Definition)}.
 * Note that the inherited duplicate check is <em>local only</em>: adding a
 * definition whose name or OID also exists in the parent succeeds and produces
 * an intentional override (the local definition shadows the parent's on
 * lookup). No diagnostic is emitted for such shadowing - it is the supported
 * mechanism for domain specialization.</p>
 *
 * <p>This class is not synchronized against concurrent multithreaded access.</p>
 *
 * @see BasicSchema
 * @see StandardSchema
 * @author Carl Eric Codere
 */
public class DelegatingSchema extends BasicSchema
{
  /** The parent schema consulted on a local lookup miss. Never {@code null}. */
  protected final Schema parent;

  /**
   * Constructs a delegating schema with the given parent.
   *
   * @param parent
   *          [in] The schema to consult when a lookup finds no local
   *          definition. Must not be {@code null}; use a plain
   *          {@link BasicSchema} if no delegation is desired.
   * @throws IllegalArgumentException
   *           if {@code parent} is {@code null}.
   */
  public DelegatingSchema(Schema parent)
  {
    super();
    if (parent == null)
      throw new IllegalArgumentException(
          "'parent' must not be null; use BasicSchema for a non-delegating "
          + "schema.");
    this.parent = parent;
  }

  /**
   * Returns the parent schema that this schema delegates to on a local miss.
   *
   * @return The non-null parent {@link Schema}.
   */
  public Schema getParent()
  {
    return parent;
  }

  // -----------------------------------------------------------------------
  // Schema — point lookups (local-first, then parent)
  // -----------------------------------------------------------------------

  /**
   * {@inheritDoc}
   *
   * <p>Returns the local item definition if one matches; otherwise delegates
   * to the parent schema.</p>
   */
  public ItemDefinition getItemDefinition(String key)
  {
    ItemDefinition local = super.getItemDefinition(key);
    if (local != null)
      return local;
    return parent.getItemDefinition(key);
  }

  /**
   * {@inheritDoc}
   *
   * <p>Returns the local type definition if one matches; otherwise delegates
   * to the parent schema.</p>
   */
  public EntityTypeDefinition getTypeDefinition(String key)
  {
    EntityTypeDefinition local = super.getTypeDefinition(key);
    if (local != null)
      return local;
    return parent.getTypeDefinition(key);
  }

  /**
   * {@inheritDoc}
   *
   * <p>Returns the local class definition if one matches; otherwise delegates
   * to the parent schema.</p>
   */
  public ClassDefinition getClassDefinition(String key)
  {
    ClassDefinition local = super.getClassDefinition(key);
    if (local != null)
      return local;
    return parent.getClassDefinition(key);
  }
}
