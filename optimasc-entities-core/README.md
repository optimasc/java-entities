The objective of this project is to create a set of interfaces and infrastructure that can be
used to represent any tree-like structures, which may or may not contain attributes or properties.

The objective is to try to make this package as portable as possible across Android and Java desktop, 
hence relying on minimal external dependencies except core java interfaces.

Initially the idea was to use the JNDI Java interfaces DirContext and other 
classes are too complex for the simple use cases.

The main use-case is to be able to represent these structures in user-interfaces
where users will be able to traverse the tree, display the attributes and potentially
edit them. Here are some concrete use-cases:
 
* A filesystem view where size, last modification date could be attributes of files or directories. (Through VFS2)
* Library database of books
* A collection of resources, used for inventories such as CD's
* An X.500 directory including its subordinate and their attributes through JNDI
* An XML document being displayed through DOM interface
* Display of structured files, such as RIFF/IFF that contain chunks (entries) and potential subentries.
* A SQL table where each attribute represents a column and each entry represents a row, where the primary key
  could be the unique identifier.
* A UPnP ContentDirectory service representation of media files.  
  
We try to mimic a bit DirContext, knowing that each item is uniquely identified 
with its name in a namespace, the attributes are typed. The result of the 
design as a base is something to similar found in the uploaded documents.

Following the design:
* Entity represents a name item with a unique name in the namespace.
* An entity may have one or more attributes.
* An Entity may have zero or more subordinate entities.
* Attributes can be identified with an RDF compatible expandedName (optional namespaceURI+name) AND an optional OID.
* Attributes are similar to JNDI Attributes.
* Error management follows error codes defined in HTTP 1.1 and UpnpException protocols.
* Each entity may be identified with an Object class name which may be used to indicate allowed subordinates and attributes.
* There could 'virtual' named entities that acts as links as reference to another entity (link symbolic links in filesystems)

Design decision:
* getChildren returns a List to be more compatible with the Swing Treemodel structure, and lazy loading
  is still possible.
  
  
## Package structure

```
com.optimasc.entity
├── Entity.java                   Interface — read-only node view
├── ModifiableEntity.java         Interface — extends Entity with write operations
├── ObservableEntity.java         Interface — extends Entity with event registration
├── EntityModification.java       Class     — describes a single attribute modification (add/replace/remove)
├── EntityName.java               Class     — immutable ordered sequence of path components
├── EntityNameParser.java         Interface — parses/formats EntityName strings
├── AbstractEntityNameParser.java Abstract  — base class for EntityNameParser implementations
├── DefaultEntityNameParser.java  Class     — default slash-separated path parser
├── EntityNameSyntax.java         Class     — syntax constants for name parsers
├── Namespace.java                Interface — opened handle to a backend tree
├── NamespaceProvider.java        Interface — factory that opens Namespace instances
implementation
├── Definition.java               Interface — named, described concept (base for schema definitions)
├── Features.java                 Class     — standard feature-name constants for Namespace.hasFeature()
├── EntityException.java          Class     — base unchecked exception with UPnP-aligned error codes
│
├── event/
│   ├── EntityEvent.java          Class     — immutable change notification
│   ├── EntityListener.java       Interface — receives EntityEvent notifications
│   └── EntityAdapter.java        Abstract  — convenience no-op base for EntityListener
│
└── schema/
    ├── Schema.java               Interface — typed entry point for schema introspection
    ├── ItemDefinition.java       Interface — schema for a single attribute/property/column
    ├── TypeDefinition.java       Interface — schema for a data type or syntax
    ├── ClassDefinition.java      Interface — schema for an object class or table
    ├── AttributeUsage.java       Class     — enumeration of attribute usage kinds (LDAP-style)
    ├── DefaultDefinition.java    Class     — mutable in-memory Definition implementation
    ├── DefaultItemDefinition.java Class    — mutable in-memory ItemDefinition implementation
    ├── DefaultTypeDefinition.java Class    — mutable in-memory TypeDefinition implementation
    ├── DefaultClassDefinition.java Class   — mutable in-memory ClassDefinition implementation
    └── BasicSchema.java          Class     — mutable in-memory Schema implementation
        └── SchemaUtilities.java  Class     — static helpers for working with Schema instances
```  

## Ready-to-use implementations

The kit ships several concrete classes so that common cases require no custom implementation.

**Attributes and attribute sets**

`DefaultAttribute` is the standard mutable, in-memory `Attribute`. The two-argument constructor
`new DefaultAttribute(name, valueClass)` covers most cases; the full constructor adds an OID,
namespace URI, ordering flag, and context type for language-tagged or otherwise qualified values.
`DefaultAttributes` is the corresponding mutable `Attributes` collection. Construct it with
`new DefaultAttributes(caseIgnored)` — pass `true` for LDAP-style case-insensitive lookup, `false`
for XML/filesystem-style exact matching.

**Attribute modifications**

`EntityModification` is a concrete, immutable value class. Construct one modification item per change
and pass the array to `ModifiableEntity.modifyAttributes`:

```java

## How the pieces fit together

The three central interfaces form a short pipeline:

    NamespaceProvider  -->  Namespace  -->  Entity (root, then children)

* `NamespaceProvider` is a factory. It takes a `Properties` bag of configuration
  (URL, credentials, backend-specific settings) and produces a configured,
  authenticated `Namespace`. One provider per backend type: an LDAP provider, a
  filesystem provider, a DOM provider, and so on.
* `Namespace` is the opened handle. Once obtained, it carries the connection
  state and exposes the name syntax for the backend. Its methods take no further
  configuration. When the consumer is done, `Namespace.close()` releases the
  backend resources.
* `Entity` is the per-node view. Tree traversal starts at `Namespace.getRoot()`
  and proceeds through `Entity.getChildren()`. Attributes are read through
  `Entity.getAttributes(...)`.

A typical consumer:

```java
NamespaceProvider provider = new LdapNamespaceProvider();
Properties config = new Properties();
config.setProperty(NamespaceProvider.PROVIDER_URL,         "ldap://example.com");
config.setProperty(NamespaceProvider.SECURITY_PRINCIPAL,   "cn=admin,dc=example,dc=com");
config.setProperty(NamespaceProvider.SECURITY_CREDENTIALS, "secret");

Namespace ns = provider.open(config);
try {
    Entity root = ns.getRoot();
    // walk the tree, read attributes, modify if supported, ...
} finally {
    ns.close();
}
```

## Capability detection

Not every backend supports every feature. Capability detection follows three
patterns, each matched to its grain:

* **Backend-wide behavioural properties** are queried on the namespace via
  `Namespace.hasFeature(String)` using constants from the `Features` class:
  `SCHEMA`, `LINKS`, `ATTRIBUTES`. These answer whether existing methods on
  `Entity` return real data or empty placeholders.
* **Per-entity structural capabilities** are detected with `instanceof` against
  a sub-interface of `Entity`. `ModifiableEntity` indicates the entity can be
  written to; `ObservableEntity` indicates event listeners can be registered on
  it. These vary per node, not per backend.
* **Backend-specific escape hatches** are retrieved through
  `Namespace.getCapability(Class<T>)`, which returns a typed handle (such as the
  underlying JNDI `Context`) or `null` if not supported. Used only for vendor
  extensions outside the portable contract.

## Implementing a new provider

To add support for a new backend:

1. Implement `NamespaceProvider`. Define `String` constants for any
   backend-specific property keys (in addition to the standard
   `PROVIDER_URL` / `SECURITY_PRINCIPAL` / `SECURITY_CREDENTIALS`). Implement
   `accepts(Properties)` as a cheap syntactic check; do the real work
   (connect, authenticate, validate) in `open(Properties)`.
2. Implement `Namespace`. Provide an `EntityNameParser` for the backend's name
   syntax (often a subclass of `AbstractEntityNameParser`), a `getRoot()` that
   returns the root entity, and `resolve(...)` for name lookup. Override
   `hasFeature(String)` to advertise which features the backend supports.
   Implement `close()` to release backend resources.
3. Implement `Entity` for each node type the backend exposes. Most adapters
   need only one or two concrete `Entity` classes; if the backend is writable,
   those classes also implement `ModifiableEntity`. For event-capable backends,
   implement `ObservableEntity` as well.

The portable consumer code (tree walkers, Swing `TreeModel` adapters,
attribute panels) never knows which backend it is talking to. It works against
`Namespace` and `Entity` only, branches on `hasFeature` and `instanceof` where
behaviour varies, and stays unchanged when a new provider is added.
