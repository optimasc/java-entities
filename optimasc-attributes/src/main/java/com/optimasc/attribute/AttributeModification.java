package com.optimasc.attribute;

/** Represents a single modification to apply to attributes.
 *
 *  <p>This class is the portable equivalent of
 *  {@code javax.naming.directory.ModificationItem}, with the operation
 *  constants inlined (they live on {@code DirContext}. 
 *  The numeric values are intentionally identical
 *  to JNDI's so that an adapter layer can pass them through without
 *  translation:</p>
 *  <ul>
 *    <li>{@link #ADD_ATTRIBUTE}     = 1  ({@code DirContext.ADD_ATTRIBUTE})</li>
 *    <li>{@link #REPLACE_ATTRIBUTE} = 2  ({@code DirContext.REPLACE_ATTRIBUTE})</li>
 *    <li>{@link #REMOVE_ATTRIBUTE}  = 3  ({@code DirContext.REMOVE_ATTRIBUTE})</li>
 *  </ul>
 *
 *  <p>A {@code AttributeModification} instance is not synchronized against
 *  concurrent multithreaded access. Multiple threads trying to access and
 *  modify a single instance should lock the object.</p>
 *
 *  @author Carl Eric Codere
 */
public class AttributeModification
{
  // -----------------------------------------------------------------------
  // Operation constants  (same numeric values as DirContext for easy bridging)
  // -----------------------------------------------------------------------

  /** Add an attribute or add values to an existing attribute.
   *
   *  <p>If the named attribute does not exist, it is created with the
   *  supplied values. If it already exists, the supplied values are
   *  appended to the existing value set. Adding a duplicate value to an
   *  unordered attribute is an error; adding a duplicate value to an
   *  ordered attribute appends a second copy.</p>
   *
   *  <p>Value: {@code 1} — identical to {@code DirContext.ADD_ATTRIBUTE}.</p>
   */
  public static final int ADD_ATTRIBUTE = 1;

  /** Replace an attribute's values.
   *
   *  <p>If the attribute already exists, all existing values are replaced
   *  by the supplied values. If the attribute does not exist, it is
   *  created. If no values are supplied in the attribute, all existing
   *  values are deleted (equivalent to removing the attribute entirely
   *  on backends that do not permit zero-value attributes).</p>
   *
   *  <p>Value: {@code 2} — identical to {@code DirContext.REPLACE_ATTRIBUTE}.</p>
   */
  public static final int REPLACE_ATTRIBUTE = 2;

  /** Remove an attribute or specific values from an attribute.
   *
   *  <p>If values are specified in the attribute, only those values are
   *  removed from the existing value set; the attribute itself is retained
   *  if other values remain. If no values are specified, the entire
   *  attribute is removed. Attempting to remove a value that does not
   *  exist is ignored by most backends.</p>
   *
   *  <p>Value: {@code 3} — identical to {@code DirContext.REMOVE_ATTRIBUTE}.</p>
   */
  public static final int REMOVE_ATTRIBUTE = 3;

  // -----------------------------------------------------------------------
  // Fields
  // -----------------------------------------------------------------------

  /** The modification operation code. One of {@link #ADD_ATTRIBUTE},
   *  {@link #REPLACE_ATTRIBUTE}, or {@link #REMOVE_ATTRIBUTE}. */
  private final int modOp;

  /** The attribute to use for this modification. Never {@code null}. */
  private final Attribute attribute;

  // -----------------------------------------------------------------------
  // Constructor
  // -----------------------------------------------------------------------

  /** Creates a new {@code AttributeModification}.
   *
   * @param modOp     [in] The modification operation to apply. Must be one
   *                  of {@link #ADD_ATTRIBUTE}, {@link #REPLACE_ATTRIBUTE},
   *                  or {@link #REMOVE_ATTRIBUTE}.
   * @param attribute [in] The non-null attribute to use for this
   *                  modification.
   * @throws IllegalArgumentException if {@code attribute} is {@code null},
   *                                  or if {@code modOp} is not one of the
   *                                  three defined operation codes.
   */
  public AttributeModification(int modOp, Attribute attribute)
  {
    if (attribute == null)
      throw new IllegalArgumentException(
          "'attribute' must not be null.");
    if (modOp != ADD_ATTRIBUTE &&
        modOp != REPLACE_ATTRIBUTE &&
        modOp != REMOVE_ATTRIBUTE)
      throw new IllegalArgumentException(
          "Invalid modification operation code: " + modOp
          + ". Must be ADD_ATTRIBUTE (1), REPLACE_ATTRIBUTE (2), "
          + "or REMOVE_ATTRIBUTE (3).");
    this.modOp     = modOp;
    this.attribute = attribute;
  }

  // -----------------------------------------------------------------------
  // Accessors
  // -----------------------------------------------------------------------

  /** Returns the modification operation code for this modification item.
   *
   * @return One of {@link #ADD_ATTRIBUTE}, {@link #REPLACE_ATTRIBUTE},
   *         or {@link #REMOVE_ATTRIBUTE}.
   */
  public int getModificationOp()
  {
    return modOp;
  }

  /** Returns the attribute associated with this modification item.
   *
   * @return The non-null {@link Attribute} to use for the
   *         modification.
   */
  public Attribute getAttribute()
  {
    return attribute;
  }

  // -----------------------------------------------------------------------
  // Object overrides
  // -----------------------------------------------------------------------

  /** Returns a string representation of this modification item consisting
   *  of the operation name and the attribute. Intended for debugging only
   *  and not to be interpreted programmatically.
   */
  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    switch (modOp)
    {
      case ADD_ATTRIBUTE:
        sb.append("ADD_ATTRIBUTE");
        break;
      case REPLACE_ATTRIBUTE:
        sb.append("REPLACE_ATTRIBUTE");
        break;
      case REMOVE_ATTRIBUTE:
        sb.append("REMOVE_ATTRIBUTE");
        break;
      default:
        sb.append("UNKNOWN_OP(");
        sb.append(modOp);
        sb.append(")");
        break;
    }
    sb.append(": ");
    sb.append(attribute.toString());
    return sb.toString();
  }
}
