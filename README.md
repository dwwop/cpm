# Reference Implementation of the Common Provenance Model

This library, originally developed as part of a master's thesis ([available here](https://is.muni.cz/auth/th/sv0z0/)), is a reference implementation of the Common Provenance Model (CPM) from the ISO 23494 standard. It reflects the model's state as of Spring 2025 ([reference](https://zenodo.org/records/14526108)) and is implemented as an extension of the [ProvToolbox](https://github.com/lucmoreau/ProvToolbox) library.

## Status

[![Pipeline](https://github.com/dwwop/cpm/actions/workflows/maven.yml/badge.svg)](https://github.com/dwwop/cpm/actions/workflows/maven.yml)
![Coverage](https://img.shields.io/badge/coverage-88%25-brightgreen)
![License](https://img.shields.io/badge/license-Apache%202.0-blue)

## Requirements

- Java 23
- Maven 3.9.9

## Installation

Since this library is not yet published to Maven Central, download the latest `.jar` files
from [Releases](https://github.com/dwwop/cpm/releases) and add them to your project manually.

### cpm-core
Copy the cpm-core `.jar` to your local repo:

```sh
mvn install:install-file \
  -Dfile=cpm-core-1.0.0.jar \
  -DgroupId=cz.muni.fi.cpm \
  -DartifactId=cpm-core \
  -Dversion=1.0.0 \
  -Dpackaging=jar
  ```

Then add the dependency:

```xml
<dependency>
    <groupId>cz.muni.fi.cpm</groupId>
    <artifactId>cpm-core</artifactId>
    <version>1.0.0</version>
</dependency>
```

### cpm-template
Copy the cpm-template `.jar` to your local repo:

```sh
mvn install:install-file \
  -Dfile=cpm-template-1.0.0.jar \
  -DgroupId=cz.muni.fi.cpm \
  -DartifactId=cpm-template \
  -Dversion=1.0.0 \
  -Dpackaging=jar
  ```

Then add the dependency:

```xml
<dependency>
    <groupId>cz.muni.fi.cpm</groupId>
    <artifactId>cpm-template</artifactId>
    <version>1.0.0</version>
</dependency>
```

---

## `cpm-core` Module Overview

The `cpm-core` module centers around the `CpmDocument` class, which enables graph-based traversal and querying of provenance documents. Internally, the document is represented as a traversable graph composed of nodes and edges.

### Initialization of `CpmDocument`

The `CpmDocument` class can be initialized in multiple ways. The two most commonly used approaches are:

#### Initialization from a ProvToolBox `Document`

This method is suitable when working with a ProvToolBox `Document` containing exactly one bundle:

```java
ProvFactory pF = new ProvFactory();
ICpmFactory cF = new CpmMergedFactory(pF);
ICpmProvFactory cPF = new CpmProvFactory(pF);

Document document = pF.newDocument();
document.setNamespace(cPF.newCpmNamespace());

QualifiedName id = pF.newQualifiedName("uri", "bundle", "ex");
Bundle bundle = pF.newNamedBundle(id, new ArrayList<>());
document.getStatementOrBundle().add(bundle);

QualifiedName id1 = cPF.newCpmQualifiedName("qN1");
Entity entity = cPF.getProvFactory().newEntity(id1);

QualifiedName id2 = cPF.newCpmQualifiedName("qN2");
Agent agent = cPF.getProvFactory().newAgent(id2);

Relation relation = cPF.getProvFactory().newWasAttributedTo(cPF.newCpmQualifiedName("attr"), id1, id2);

bundle.getStatement().add(entity, agent, relation);

CpmDocument doc = new CpmDocument(document, pF, cPF, cF);
```

#### Initialization from a List of Statements

This alternative approach allows for initialization using a list of statements and an explicit bundle identifier:

```java
ProvFactory pF = new ProvFactory();
ICpmFactory cF = new CpmMergedFactory(pF);
ICpmProvFactory cPF = new CpmProvFactory(pF);

QualifiedName id1 = cPF.newCpmQualifiedName("qN1");
Entity entity = cPF.getProvFactory().newEntity(id1);

QualifiedName id2 = cPF.newCpmQualifiedName("qN2");
Agent agent = cPF.getProvFactory().newAgent(id2);

Relation relation = cPF.getProvFactory().newWasAttributedTo(cPF.newCpmQualifiedName("attr"), id1, id2);

QualifiedName bundleId = pF.newQualifiedName("uri", "bundle", "ex");

CpmDocument doc = new CpmDocument(List.of(entity, agent, relation), bundleId, pF, cPF, cF);
```

### `ICpmFactory` Interface

The `ICpmFactory` interface defines how statements with identical identifiers are processed within the graph structure. The module provides the following core implementations:

#### Merged Implementation

* **`CpmMergedFactory`**
  Merges statements with the same identifier using custom algorithms provided by `ProvUtilities2`.

#### Divided Implementations

These implementations retain all statements sharing the same identifier, differing in how they handle statement ordering:

* **`CpmUnorderedFactory`**
  Does not preserve the original order of statements during conversions between `CpmDocument` and ProvToolBox `Document`.

* **`CpmOrderedFactory`**
  Preserves the original statement order from the source ProvToolBox `Document`.

### Traversing the Graph

The `CpmDocument` graph structure allows standard traversal algorithms. The following example demonstrates breadth-first search to extract a connected subgraph:

```java
public List<INode> getConnectedSubgraph(CpmDocument cpmDoc, QualifiedName startNodeIdentifier) {
    List<INode> result = new ArrayList<>();
    Queue<INode> toProcess = new LinkedList<>();
    
    INode startNode = cpmDoc.getNode(startNodeIdentifier);
    
    toProcess.add(startNode);
    result.add(startNode);

    while (!toProcess.isEmpty()) {
        INode current = toProcess.poll();

        for (IEdge edge : current.getCauseEdges()) {
            toProcess.add(edge.getEffect());
            result.add(edge.getEffect());
        }
    }

    return result;
}
```

### Functionalities Provided by `CpmDocument`

The `CpmDocument` class supports a wide range of operations, including:

1. Retrieving entities, agents, or activities by identifier.
2. Retrieving relations by identifier, or based on source and target identifiers.
3. Identifying the main activity.
4. Accessing forward and backward connectors.
5. Navigating preceding or successive connectors by identifier.
6. Extracting the traversal information subgraph of the bundle.
7. Extracting domain-specific provenance as a subgraph.
8. Identifying relations between traversal and domain-specific provenance parts.
9. Reconstructing a full document from traversal and domain-specific subgraphs, and cross-part relations.

### Document Modification

The `CpmDocument` supports mutation through a set of defined operations:

* **Addition**
  Use `doAction` methods to add statements.

* **Removal**
  Use `remove`-prefixed methods to delete statements or nodes.

* **Modification**
  Use methods prefixed with `setNew` or `setCollectionMembers` to update identifiers and collection memberships.

### Customizing Traversal Strategy

The classification of nodes into traversal or domain-specific components is governed by the `ITIStrategy` interface. The default implementation relies on attributes of the underlying PROV elements.

To apply a custom strategy, implement `ITIStrategy` and register it with the document:

```java
cpmDoc.setTIStrategy(customTiStrategy);
```


## `cpm-template` Module Overview

The `cpm-template` module is designed to define and instantiate the template for creating a `Document` that encapsulates traversal information within the CPM framework.

Traversal information can be instantiated in two ways: from a JSON file or directly in-memory.

### JSON-Based Instantiation

The structure of the required JSON file is defined by the [template schema](https://github.com/dwwop/cpm/blob/main/cpm-template/src/main/resources/template_schema.json). This schema outlines all necessary properties and expected formats.

Example JSON template:

```json
{
  "prefixes": {
    "ex": "www.example.org/"
  },
  "mainActivity": {
    "id": "ex:activity1",
    "startTime": "2011-11-16T16:05:00",
    "endTime": "2011-11-16T18:05:00",
    "used": [
      {
        "bcId": "ex:backConnector1"
      }
    ],
    "generated": [
      "ex:forwardConnector1"
    ]
  },
  "bundleName": "ex:bundle1",
  "backwardConnectors": [
    {
      "id": "ex:backConnector1"
    }
  ],
  "forwardConnectors": [
    {
      "id": "ex:forwardConnector1",
      "derivedFrom": [
        "ex:backConnector1"
      ]
    }
  ]
}
```

To construct a `Document` from a JSON `InputStream`, use the following code:

```java
ITraversalInformationDeserializer deserializer = new TraversalInformationDeserializer();
Document doc = deserializer.deserializeDocument(inputStream);
```

### In-Memory Instantiation

Alternatively, traversal information can be created programmatically and mapped to a `Document` directly in memory.

The following code reproduces the structure of the JSON example above:

```java
DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
ProvFactory pF = new org.openprovenance.prov.vanilla.ProvFactory();
TraversalInformation ti = new TraversalInformation();

ti.setPrefixes(Map.of("ex", "www.example.com/"));
ti.setBundleName(ti.getNamespace().qualifiedName("ex", "bundle1", pF));

MainActivity mA = new MainActivity(ti.getNamespace().qualifiedName("ex", "activity1", pF));
mA.setStartTime(datatypeFactory.newXMLGregorianCalendar("2011-11-16T16:05:00"));
mA.setEndTime(datatypeFactory.newXMLGregorianCalendar("2011-11-16T18:05:00"));
ti.setMainActivity(mA);

QualifiedName bcID = ti.getNamespace().qualifiedName("ex", "backConnector1", pF);
BackwardConnector bC = new BackwardConnector(bcID);
ti.getBackwardConnectors().add(bC);

MainActivityUsed used = new MainActivityUsed(bcID);
mA.setUsed(List.of(used));

QualifiedName fcID = ti.getNamespace().qualifiedName("ex", "forwardConnector1", pF);
mA.setGenerated(List.of(fcID));

ForwardConnector fC = new ForwardConnector(fcID);
fC.setDerivedFrom(List.of(bC.getId()));
ti.getForwardConnectors().add(fC);

ITemplateProvMapper mapper = new TemplateProvMapper(new CpmProvFactory(pF));
Document doc = mapper.map(ti);
```

#### Agent Merging

By default, sender and receiver agents with the same identifier are treated as distinct. To enable automatic merging into a single agent with both types, configure the mapper as follows:

Using the constructor:

```java
ITemplateProvMapper mapper = new TemplateProvMapper(new CpmProvFactory(pF), true);
```

Or using the setter:

```java
mapper.setMergeAgents(true);
```

The mapper can be passed to the `TraversalInformationDeserializer` to merge agents during JSON deserialisation as well.

## MMCI Dataset

TODO


## EMBRC dataset

TODO

---

## Contributing

Thank you for your interest in contributing!

### Setup

1. Fork the repository.
2. Clone your fork and create a feature branch:
   ```sh
   git checkout -b feat/your-feature
   ```

3. Build and test:

   ```sh
   mvn clean verify
   ```

### Commit Guidelines

Use [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/). Examples:

* `feat(core): add support for X`
* `fix(template): correct behavior of Y`
* `docs: README commit guideline`

### Pull Requests

* Target the `main` branch.
* Ensure all tests pass.
* Include relevant tests and documentation.

By contributing, you agree that your contributions will be licensed under the Apache 2.0 License.

