package com.optimasc.entity;

/** Base exception for all entity and directory operations.
 *
 *  <p>It serves as the root of the exception hierarchy for the 
 *  {@code com.optimasc.entity} package and is intended to be 
 *  subclassed for domain-specific error codes.</p>
 *
 *  <p>Error codes follow the UPnP Device Architecture 2.0 
 *  (April 2020) and ContentDirectory:4 SDCP (June 2015). 
 *  <ul>
 *    <li><strong>4xx</strong> &mdash; Protocol / request errors: the
 *        caller made an invalid request.</li>
 *    <li><strong>5xx</strong> &mdash; Server / backend errors: the
 *        backend encountered an error processing a valid request.</li>
 *    <li><strong>6xx</strong> &mdash; Argument value errors: a specific
 *        argument value is invalid. Defined by the UPnP Forum Technical
 *        Committee and have the same meaning across all UPnP services.</li>
 *    <li><strong>7xx</strong> &mdash; Action-specific errors: defined by
 *        the UPnP Forum Working Committee for the ContentDirectory
 *        service. Subclasses may add further codes in this range.</li>
 *  </ul>
 *
 *  <p>Although this class extends {@link RuntimeException} (unchecked),
 *  methods that are genuinely failure-prone declare {@code throws
 *  EntityException} explicitly as a documentation signal to callers.</p>
 *
 *  <p>This class is not synchronized against concurrent multithreaded
 *  access.</p>
 *
 *  @author Carl Eric Codere
 */
public class EntityException extends RuntimeException
{
  // -----------------------------------------------------------------------
  // 4xx — Protocol / request errors
  // (UPnP Device Architecture 2.0, section on Control)
  // -----------------------------------------------------------------------

  /** The requested operation is not valid for this entity or object 
   *  type or in its current state.
   *  <p>UPnP Device Architecture 2.0 standard error code.</p>
   *  Value: {@code 401}
   */
  public static final int INVALID_OPERATION = 401;

  /** Invalid arguments: not enough arguments, arguments in the wrong
   *  order, or one or more arguments are of the wrong data type.
   *  <p>UPnP Device Architecture 2.0 standard error code.</p>
   *  Value: {@code 402}
   *  
   *  <p>This is equivalent to 
   *  {@code java.lang.IllegalArgumentException}.</p>
   *  
   */
  public static final int INVALID_ARGS = 402;

  // -----------------------------------------------------------------------
  // 5xx — Server / backend errors
  // (UPnP Device Architecture 2.0, section on Control)
  // -----------------------------------------------------------------------

  /** The action failed due to an unspecified internal error in 
   *  the backend, such as an I/O failure, lost connection, or 
   *  unhandled exception. Use more specific 7xx codes when the 
   *  failure reason is known.
   *  <p>UPnP Device Architecture 2.0 standard error code.</p>
   *  Value: {@code 501}
   *  
   *  <p>This is equivalent to 
   *  {@code javax.naming.CommunicationException}.</p>
   *  
   *  @see #CANNOT_PROCESS
   *  
   */
  public static final int ACTION_FAILED = 501;

  // -----------------------------------------------------------------------
  // 6xx — Argument value errors
  // (UPnP Forum Technical Committee — same meaning across all UPnP services)
  // -----------------------------------------------------------------------

  /** The argument value is invalid for reasons other than range.
   *  <p>UPnP Device Architecture 2.0 common action error.</p>
   *  Value: {@code 600}
   *
   *  <p>Use {@link #ARGUMENT_VALUE_OUT_OF_RANGE} (601) when the value is
   *  the right type but outside permitted bounds. Use this code when the
   *  value is wrong for any other reason: wrong type, illegal format, or
   *  unrecognised identifier.</p>
   */
  public static final int ARGUMENT_VALUE_INVALID = 600;

  /** An argument value is less than the minimum or greater than the
   *  maximum value of the allowed range, or is not in the allowed value
   *  list.
   *  <p>UPnP Device Architecture 2.0 common action error.</p>
   *  Value: {@code 601}
   */
  public static final int ARGUMENT_VALUE_OUT_OF_RANGE = 601;

  /** The requested action is optional and is not implemented by this
   *  backend or service.
   *  <p>UPnP Device Architecture 2.0 common action error.</p>
   *  Value: {@code 602}
   *  
   *  <p>This is equivalent to 
   *  {@code javax.naming.OperationNotSupportedException}.</p>
   */
  public static final int NOT_IMPLEMENTED = 602;

  /** The backend has run out of memory or storage and cannot complete the operation.
   *  <p>UPnP Device Architecture 2.0 common action error.</p>
   *  Value: {@code 603}
   *  
   *  <p>This is equivalent to 
   *  {@code javax.naming.InsufficientResourcesException}.</p>
   *  
   */
  public static final int INSUFFICIENT_RESOURCES = 603;

  /** A string argument is too long for the backend to handle.
   *  <p>UPnP Device Architecture 2.0 common action error.</p>
   *  Value: {@code 605}
   *  
   *  <p>This is equivalent to 
   *  {@code javax.naming.SizeLimitExceededException}.</p>
   *  
   */
  public static final int STRING_TOO_LONG = 605;

  // -----------------------------------------------------------------------
  // 7xx — Entity / directory domain errors
  // (UPnP AV ContentDirectory service specification, Working Committee)
  // -----------------------------------------------------------------------

  /** The specified named entity or object does not exist in the backend.
   *  <p>UPnP ContentDirectory service error code (CDS 701).</p>
   *  Value: {@code 701}
   *  <p>This is equivalent to 
   *  {@code javax.naming.NameNotFoundException}.</p>
   *  
   */
  public static final int NO_SUCH_OBJECT = 701;

  /** The current value of the attribute does not match the value supplied by 
   *  the caller as a precondition for the update. This typically indicates 
   *  that the attribute was modified by another operation after the caller 
   *  last read it.
   *  <p>UPnP ContentDirectory service error code (CDS 702).</p>
   *  Value: {@code 702}
   */
  public static final int INVALID_CURRENT_ATTRIBUTE_VALUE = 702;

  /** The attribute value specified in an update ({@code REPLACE_ATTRIBUTE})
   *  operation is invalid for the target attribute because it does not meet
   *  the schema's structural or syntactic requirements for that attribute.
   *
   *  <p>This code is specific to write operations: it fires when a value
   *  being <em>written</em> violates the attribute's declared schema
   *  (e.g. wrong syntax, value not in the permitted list, multiplicity
   *  constraint violated). For a value that is structurally well-formed
   *  but incompatible with the attribute's declared type or format,
   *  use {@link #INVALID_ATTRIBUTE_VALUE} (712) instead.</p>
   *
   *  <p>UPnP ContentDirectory service error code (CDS 703).</p>
   *  Value: {@code 703}
   *
   *  <p>This is equivalent to
   *  {@code javax.naming.directory.SchemaViolationException}.</p>
   *
   *  @see #INVALID_ATTRIBUTE_VALUE
   */
  public static final int INVALID_NEW_ATTRIBUTE_VALUE = 703;
  
  /** The operation attempted to remove an attribute that is either 
   *  read-only at the backend level, or declared mandatory by the 
   *  schema and therefore cannot be absent.
   *  <p>UPnP ContentDirectory service error code (CDS 704).</p>
   *  Value: {@code 704}
   *  <p>This is equivalent to 
   *  {@code javax.naming.AttributeModificationException}.</p>
   *  
   */
  public static final int CANNOT_DELETE_ATTRIBUTE = 704;
  

  /** The operation attempted to modify an attribute whose 
   *  value is controlled by the backend and cannot be set by callers.
   *  
   *  <p>UPnP ContentDirectory service error code (CDS 705).</p>
   *  Value: {@code 705}
   *  
   *  <p>This is equivalent to 
   *  {@code javax.naming.AttributeModificationException}.</p>
   *  
   */
  public static final int CANNOT_UPDATE_ATTRIBUTE = 705;

  /** The specified sort criteria is not supported or is syntactically
   *  invalid.
   *  <p>UPnP ContentDirectory service error code (CDS 709).</p>
   *  Value: {@code 709}
   */
  public static final int INVALID_SORT_CRITERIA = 709;


  /** The operation was refused because the target entity is marked as 
   * restricted and cannot be modified or deleted. 
   * This corresponds to the {@code restricted} flag in UPnP CDS.
   *  <p>UPnP ContentDirectory service error code (CDS 711).</p>
   *  Value: {@code 711}
   */
  public static final int RESTRICTED_OBJECT = 711;

  /** An attribute value is structurally invalid or incompatible with the
   *  attribute's declared type or format, regardless of the operation being
   *  performed.
   *
   *  <p>This code covers type mismatches, illegal formats, and values that
   *  cannot be coerced into the attribute's declared value class (e.g. a
   *  non-numeric string supplied for an integer attribute, a malformed IRI,
   *  or a date string that does not conform to the expected pattern).
   *  It applies to any operation — read, add, replace, or remove — whenever
   *  the value itself is the problem.</p>
   *
   *  <p>Contrast with {@link #INVALID_NEW_ATTRIBUTE_VALUE} (703), which is
   *  specific to write operations that violate schema-level constraints
   *  (multiplicity, permitted-value lists, mandatory fields). When a write
   *  operation fails for both reasons, prefer 703 as it is more specific.</p>
   *
   *  <p>UPnP ContentDirectory service error code (CDS 712).</p>
   *  Value: {@code 712}
   *
   *  <p>This is equivalent to
   *  {@code javax.naming.directory.InvalidAttributeValueException}.</p>
   *
   *  @see #INVALID_NEW_ATTRIBUTE_VALUE
   */
  public static final int INVALID_ATTRIBUTE_VALUE = 712;
  
  /** The operation failed because a specified source resource could not
   *  be found.
   *  <p>UPnP ContentDirectory service error code (CDS 714).</p>
   *  Value: {@code 714}
   */
  public static final int SOURCE_RESOURCE_NOT_FOUND = 714;

  /** The operation failed because a specified destination resource could
   *  not be found.
   *  <p>UPnP ContentDirectory service error code (CDS 718).</p>
   *  Value: {@code 718}
   */
  public static final int DESTINATION_RESOURCE_NOT_FOUND = 718;

  /** The backend is temporarily unable to process this request due to 
   *  its current state. This is a transient state, and operation
   *  may be attempted later.
   *  <p>UPnP ContentDirectory service error code (CDS 720).</p>
   *  Value: {@code 720}
   *  <p>This is equivalent to 
   *  {@code javax.naming.ServiceUnavailableException}.</p>
   *  
   *  @see #ACTION_FAILED
   */
  public static final int CANNOT_PROCESS = 720;

  /** The entity cannot be created under the specified entity because it
   *  is not allowed due to the schema restrictions.  
   *  <p>UPnP ContentDirectory service error code (CDS 722).</p>
   *  Value: {@code 722}
   *  <p>This is equivalent to 
   *  {@code javax.naming.directory.SchemaViolationException}.</p>
   *  
   */
  public static final int INCOMPATIBLE_CONTAINER = 722;
  
  /** The operation was abandoned before completion because the
   *  caller requested its cancellation, by calling
   *  {@link com.com.optimasc.entity.event.OperationEvent#cancel()}
   *  from its
   *  {@link com.com.optimasc.entity.event.OperationListener}.
   *
   *  <p>This code does not report a fault in the backend or in the
   *  request: the operation was proceeding normally and stopped
   *  because it was asked to. Consumers should generally not
   *  present it to the user as an error.</p>
   *
   *  <p>Cancellation does not undo the work already carried out.
   *  The backend is left in a consistent but possibly partial
   *  state &mdash; for example, a destination subtree containing
   *  only some of the entities of a subtree copy &mdash; and any
   *  clean-up is the responsibility of the caller.</p>
   *
   *  <p>Not defined in UPnP ContentDirectory:4, where a transfer
   *  is stopped through a separate action
   *  ({@code StopTransferResource}) and not reported as an error
   *  of the transfer itself; assigned to code 751 within the local
   *  extension range, following {@link #DUPLICATE_ENTITY}.</p>
   *  Value: {@code 751}
   *
   *  <p>This is equivalent to
   *  {@code javax.naming.InterruptedNamingException}, and to the
   *  LDAP {@code canceled} (118) result code of RFC 3909.</p>
   *
   *  @see EntityUtilities
   */
  public static final int OPERATION_CANCELLED = 751;

  /** The entity cannot be removed because it still has children,
   *  and the backend removes empty entities only.
   *
   *  <p>This code is raised by
   *  {@link ModifiableEntity#removeChild(Entity)} on namespaces
   *  that do not advertise {@link Features#SUBTREE_REMOVE}. The
   *  caller must remove the children first, or use
   *  {@link EntityUtilities#removeTree}, which does so.</p>
   *
   *  <p>Not defined in UPnP ContentDirectory:4; assigned to code
   *  752 within the local extension range, following
   *  {@link #OPERATION_CANCELLED}.</p>
   *  Value: {@code 752}
   *
   *  <p>This is equivalent to
   *  {@code javax.naming.ContextNotEmptyException}, and to the
   *  LDAP {@code notAllowedOnNonLeaf} (66) result code.</p>
   *
   *  @see Features#SUBTREE_REMOVE
   */
  public static final int CONTAINER_NOT_EMPTY = 752;  
  
  /** An entity with the same name already exists under the target
   *  container and cannot be created again. The caller must either
   *  choose a different name or remove the existing entity before
   *  retrying the operation.
   *
   *  <p>This code is raised by {@link ModifiableEntity#addChild} when
   *  the requested display name is already bound in the target container.
   *  It is distinct from {@link #INCOMPATIBLE_CONTAINER} (722), which
   *  signals a schema constraint on the <em>type</em> of child, not a
   *  name collision.</p>
   *
   *  <p>Not defined in UPnP ContentDirectory:4 (CDS does not allow
   *  caller-chosen names); assigned to code 750 within the local
   *  extension range with no conflict against any published
   *  CDS:4 code.</p>
   *  Value: {@code 750}
   *
   *  <p>This is equivalent to
   *  {@code javax.naming.NameAlreadyBoundException}.</p>
   *
   *  @see #INCOMPATIBLE_CONTAINER
   */
  public static final int DUPLICATE_ENTITY = 750;  
  
  /** The operation was refused because the caller does not have
   *  sufficient access rights to the target entity or resource.
   *  <p>UPnP ContentDirectory service error code (CDS 740).</p>
   *  Value: {@code 740}
   *  <p>This is equivalent to 
   *  {@code javax.naming.NoPermissionException} or 
   *  {@code javax.naming.AuthenticationException}.</p>
   *  
   */
  public static final int ACCESS_DENIED = 740;

  // -----------------------------------------------------------------------
  // Fields
  // -----------------------------------------------------------------------

  /** The UPnP-aligned error code. */
  protected int errorCode;

  // -----------------------------------------------------------------------
  // Constructors
  // -----------------------------------------------------------------------

  /** Creates a new {@code EntityException} with an error code and a
   *  detail message.
   *
   * @param errorCode [in] The error code. Should be one of the constants
   *                  defined on this class or a subclass.
   * @param message   [in] A human-readable description of the error.
   *                  May be {@code null}.
   */
  public EntityException(int errorCode, String message)
  {
    super(message);
    this.errorCode = errorCode;
  }

  /** Creates a new {@code EntityException} with an error code and a
   *  causing throwable. The detail message is taken from the cause.
   *
   * @param errorCode [in] The error code.
   * @param cause     [in] The underlying cause. Must not be {@code null}.
   */
  public EntityException(int errorCode, Throwable cause)
  {
    super(cause.getMessage(), cause);
    this.errorCode = errorCode;
  }

  /** Creates a new {@code EntityException} with an error code, a detail
   *  message, and a causing throwable.
   *
   * @param errorCode [in] The error code.
   * @param message   [in] A human-readable description of the error.
   * @param cause     [in] The underlying cause. May be {@code null}.
   */
  public EntityException(int errorCode, String message, Throwable cause)
  {
    super(message, cause);
    this.errorCode = errorCode;
  }

  // -----------------------------------------------------------------------
  // Accessors
  // -----------------------------------------------------------------------

  /** Returns the error code associated with this exception.
   *
   * @return The error code. Will be one of the constants defined on this
   *         class or a subclass.
   */
  public int getErrorCode()
  {
    return errorCode;
  }

  // -----------------------------------------------------------------------
  // Object overrides
  // -----------------------------------------------------------------------

  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    sb.append(getClass().getName());
    sb.append("[");
    sb.append(errorCode);
    sb.append("]");
    String msg = getMessage();
    if (msg != null)
    {
      sb.append(": ");
      sb.append(msg);
    }
    return sb.toString();
  }
}
