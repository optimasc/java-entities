package com.optimasc.entity.schema;

import com.optimasc.entity.Entity;
import com.optimasc.entity.EntityException;
import com.optimasc.entity.EntityName;
import com.optimasc.entity.EntityNameParser;
import com.optimasc.entity.Namespace;

/** Placeholder {@link Namespace} returned by
 *  {@link DefaultDefinition#getNamespace()} for definitions that
 *  have not yet been registered with a {@link BasicSchema}.
 *
 *  <p>This singleton exists solely to satisfy the non-null contract
 *  of {@link com.optimasc.entity.Entity#getNamespace()} for
 *  in-memory {@link DefaultDefinition} instances before they are
 *  passed to {@link BasicSchema#add(com.optimasc.entity.Definition)}.
 *  Once a definition is added to a {@link BasicSchema}, its
 *  namespace is replaced by that schema instance.  If the
 *  definition is later removed, its namespace is reset to this
 *  placeholder.</p>
 *
 *  <p>{@link #getScheme()} returns {@link Schema#SCHEME} so that
 *  the scheme is always valid, but {@link #getRoot()},
 *  {@link #resolve(String)}, and {@link #resolve(EntityName)} all
 *  throw {@link IllegalStateException} to make accidental use of
 *  an unbound definition's namespace detectable at runtime.</p>
 *
 *  <p>This class is package-private; external code should never
 *  need to reference it directly.</p>
 *
 *  @see DefaultDefinition
 *  @see BasicSchema
 */
final class UnboundSchemaNamespace implements Namespace
{
  /** The single instance. */
  static final UnboundSchemaNamespace INSTANCE =
      new UnboundSchemaNamespace();

  private UnboundSchemaNamespace() {}

  public String getScheme() { return Schema.SCHEME; }

  /** @throws IllegalStateException always — this definition has
   *    not been added to a {@link BasicSchema}. */
  public Entity getRoot() throws EntityException
  {
    throw new IllegalStateException(
        "This definition has not been registered with a BasicSchema. "
        + "Call BasicSchema.add(definition) first.");
  }

  /** @throws IllegalStateException always. */
  public Entity resolve(String n)
      throws java.text.ParseException, EntityException
  {
    throw new IllegalStateException(
        "This definition has not been registered with a BasicSchema.");
  }

  /** @throws IllegalStateException always. */
  public Entity resolve(EntityName n) throws EntityException
  {
    throw new IllegalStateException(
        "This definition has not been registered with a BasicSchema.");
  }

  /** @throws IllegalStateException always. */
  public EntityNameParser getNameParser()
  {
    throw new IllegalStateException(
        "This definition has not been registered with a BasicSchema.");
  }

  public <T> T getCapability(Class<T> t) { return null; }

  public boolean hasFeature(String f) { return false; }

  public void close() {}

  public String toString()
  {
    return "UnboundSchemaNamespace[unregistered definition]";
  }
}
