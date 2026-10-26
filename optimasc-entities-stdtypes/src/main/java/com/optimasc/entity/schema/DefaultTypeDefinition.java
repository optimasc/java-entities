package com.optimasc.entity.schema;

import java.text.Format;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import com.optimasc.attribute.DefaultAttribute;
import com.optimasc.datatypes.Datatype;
import com.optimasc.schema.Definition;
import com.optimasc.schema.TypeDefinition;

/** Default, mutable, in-memory implementation of {@link EntityTypeDefinition}.
 *
 *  <p>Extends {@link DefaultDefinition} and adds 
 *  getters for the attributes specific to a type definition: the
 *  Java class used to represent values in memory ({@link DefaultTypeDefinition#getValueClass()}).
 *  </p>
 *
 *  <p>When the Java class is not explicitly set, it defaults to
 *  {@code java.lang.String}.</p>
 *
 *  <p>This class is not synchronized against concurrent multithreaded
 *  access.</p>
 *
 *  @see EntityTypeDefinition
 *  @see DefaultDefinition
 *  @author Carl Eric Codere
 */
public class DefaultTypeDefinition extends DefaultDefinition
    implements EntityTypeDefinition
{
  // -----------------------------------------------------------------------
  // Constructors
  // -----------------------------------------------------------------------
  
  
  /** Constructs a type definition with a local name, an
   *  auto-generated identifier, and a mandatory datatype.
   *
   * @param namespaceURI [in] Namespace URI, or {@code null}.
   * @param localName    [in] Name. Must not be {@code null} or empty,
   *                     and must not exceed
   *                     {@link Definition#NAME_MAX_LENGTH} characters.
   * @param datatype     [in] The datatype defining the value space.
   *                     Must not be {@code null}.
   * @throws IllegalArgumentException if {@code localName} violates its
   *   constraints or {@code datatype} is {@code null}.
   */
  public DefaultTypeDefinition(String namespaceURI, String localName,
      Datatype datatype)
  {
    super(namespaceURI, localName);
    setObjectClass(Definition.OBJECT_CLASS_NAME);
    setDatatype(datatype);
  }

  /** Constructs a type definition with an explicit identifier, a
   *  description, and a mandatory datatype.
   *
   * @param id           [in] Unique identifier. Must not be {@code null}
   *                     or empty.
   * @param namespaceURI [in] Namespace URI, or {@code null}.
   * @param localName    [in] Name. Must not be {@code null} or empty,
   *                     and must not exceed
   *                     {@link Definition#NAME_MAX_LENGTH} characters.
   * @param description  [in] Human-readable description, or {@code null}.
   *                     When non-null, must not exceed
   *                     {@link Definition#DESC_MAX_LENGTH} characters.
   *                     This description is authoritative for catalog
   *                     display.
   * @param datatype     [in] The datatype defining the value space.
   *                     Must not be {@code null}.
   * @throws IllegalArgumentException if any argument violates its
   *   constraints.
   */
  public DefaultTypeDefinition(String id, String namespaceURI,
      String localName, String description, Datatype datatype)
  {
    super(id, namespaceURI, localName, description);
    setObjectClass(TypeDefinition.OBJECT_CLASS_NAME);
    setDatatype(datatype);
  }
  
  
  /** Constructs a type definition with a local name, an
   *  auto-generated identifier, and a datatype instantiated by class
   *  name.
   *
   *  <p>Intended for dynamic construction where the datatype is
   *  identified by a fully-qualified class name string. The named class
   *  must be a concrete {@link Datatype} subclass with a public
   *  no-argument constructor.</p>
   *
   * @param namespaceURI    [in] Namespace URI, or {@code null}.
   * @param localName       [in] Name. Must not be {@code null} or empty,
   *                        and must not exceed
   *                        {@link Definition#NAME_MAX_LENGTH} characters.
   * @param datatypeClassName [in] Fully-qualified name of a concrete
   *                        {@link Datatype} subclass. Must not be
   *                        {@code null} or empty, and must identify a
   *                        loadable, instantiable class.
   * @throws IllegalArgumentException if {@code localName} violates its
   *   constraints, or {@code datatypeClassName} is {@code null}, empty,
   *   not found, or not instantiable.
   */
  public DefaultTypeDefinition(String namespaceURI, String localName,
      String datatypeClassName)
  {
    super(namespaceURI, localName);
    setObjectClass(TypeDefinition.OBJECT_CLASS_NAME);
    setDatatypeByName(datatypeClassName);
  }  
  // -----------------------------------------------------------------------
  // TypeDefinition — typed setters
  // -----------------------------------------------------------------------

  /** Sets the datatype defining the value space of this type.
  *
  * @param datatype [in] A non-null {@link Datatype} instance.
  * @throws IllegalArgumentException if {@code datatype} is
  *   {@code null}.
  */
 public void setDatatype(Datatype datatype)
 {
   if (datatype == null)
     throw new IllegalArgumentException("Datatype must not be null.");
   DefaultAttribute attr =
       new DefaultAttribute(KEY_DATATYPE, Datatype.class);
   attr.add(datatype);
   attributes.put(attr);
 }

 /** {@inheritDoc} */
 public Datatype getDatatype()
 {
   com.optimasc.attribute.Attribute attr =
       attributes.get(KEY_DATATYPE);
   return attr != null && attr.size() > 0
       ? (Datatype) attr.get(0) : null;
 }  
 
 
 /** Sets the datatype by instantiating it from a fully-qualified
  *  class name.
  *
  *  <p>The named class must be a concrete {@link Datatype} subclass
  *  exposing a public no-argument constructor. {@link ClassNotFoundException},
  *  {@link InstantiationException}, and {@link IllegalAccessException}
  *  are wrapped in {@link IllegalArgumentException}.</p>
  *
  * @param datatypeClassName [in] Fully-qualified name of a concrete
  *                          {@link Datatype} subclass. Must not be
  *                          {@code null} or empty.
  * @throws IllegalArgumentException if {@code datatypeClassName} is
  *   {@code null} or empty, the class cannot be found, is not a
  *   {@link Datatype}, cannot be instantiated (no public no-arg
  *   constructor), or is not accessible.
  */
 public void setDatatypeByName(String datatypeClassName)
 {
   if (datatypeClassName == null || datatypeClassName.length() == 0)
     throw new IllegalArgumentException(
         "Datatype class name must not be null or empty.");
   Class<?> datatypeClass;
   try
   {
     datatypeClass = Class.forName(datatypeClassName);
   }
   catch (ClassNotFoundException e)
   {
     IllegalArgumentException ex = new IllegalArgumentException(
         "Datatype class '" + datatypeClassName + "' not found.");
     ex.initCause(e);
     throw ex;
   }
   if (Datatype.class.isAssignableFrom(datatypeClass) == false)
     throw new IllegalArgumentException(
         "Class '" + datatypeClassName + "' is not a Datatype.");
   Datatype datatype;
   try
   {
     datatype = (Datatype) datatypeClass.newInstance();
   }
   catch (InstantiationException e)
   {
     IllegalArgumentException ex = new IllegalArgumentException(
         "Datatype class '" + datatypeClassName
         + "' could not be instantiated. "
         + "Ensure it has a public no-argument constructor.");
     ex.initCause(e);
     throw ex;
   }
   catch (IllegalAccessException e)
   {
     IllegalArgumentException ex = new IllegalArgumentException(
         "Datatype class '" + datatypeClassName
         + "' or its no-argument constructor is not accessible.");
     ex.initCause(e);
     throw ex;
   }
   setDatatype(datatype);
 }

@Override
public Class<?> getValueClass()
{
  // TODO Auto-generated method stub
  return null;
}

@Override
public boolean isValid(Object value)
{
  // TODO Auto-generated method stub
  return false;
} 


  

}
