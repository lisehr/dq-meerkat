# Block-DaQ
A Blockchain-based Knowledge Graph for Data Quality Measurement using Reference Data Profiles

## Disclaimer
The provided frameworks and Bash-script are created via Linux Mint 19 (Tara) so it might not be working for every system.

# Blockchain aspect
To generate temper-free persistance of stored data a blockchain is used. Creating blockchains in Java is a difficult task since Reflections exist (more in section Persistance),
so a pseudo-temper-free (therefore only temper-evident) chain is the nearest result is what can be achieved.

## Hashing Algorithms
Used in BigChainDB and generally a widespread approach:
- Standard Hash: SHA3-256
- Keypair Hash: Ed25519 (Encoded via Base58)

## ?Mini?chains
Minichains are a further approach to improve performance of blockchains. Since DSDElements are seperated objects, they can be seperated into several miniature versions of blockchains, one for each DSDElement.
As shown in the tests in the package *demos.alex.benchmarking*, these chains are slightly better in adding and creating (about 9-15%) and significantly better for accessing data (about 75%).
Each chain contains a blockchain for the element, adapted with an identifier for the minichain and flags used for merging and deletion control.

# Persistence
To persist changes in the chain and also generate a graph out of it, multiple storing methods are used. These are:
- GraphDB (for storing the RDF based graphs)
- InfluxDB to store records and the values from the data profiles

## GraphDB
GraphDB by ontotext is a software which can be downloaded and installed in its standalone form from http://graphdb.ontotext.com/. This method of storing knowledge graphs was mainly chosen because it supports rdf stores
and is usable via Java in an embedded mode. To store concrete Java objects in GraphDB, they have to be mapped to RDF format. Since Java does not naturally support such a mapping toolbox, an external library has to be chosen.
An aspect, which cannot be forgotten is the connection between Java and GraphDB. The embedded version mainly uses RDF4j as API. 

## Mapping Java objects to RDF
Three choices for mapping libraries were possible, namely:
- Jackson (converts objects to JSON from which conversion is possible, not used because cyclic references could not be handled)
- Pinto (not used, because is was not supported anymore and more extensive frameworks like Empire are not needed)
- RDFBeans (annotation-based mapping from Java to RDF)

### RDFBeans
RDFBeans (https://rdfbeans.github.io/) is a library, which allows a developer to annotate java classes and fields to generate RDF triples from them. It also supports mapping back from RDF to Java objects as
well, which is one of the main reasons why it was chosen instead of the other two libraries. There are mainly four annotations used in this project:
- @RDFNamespaces (used above the class to define namespaces)
- @RDFBean (signalizes that the class is transformable into a RDF format)
- @RDFSubject (used above the getter of the id of this object to easily retrieve it)
- @RDF (used above the getters of all other fields to mark them for transformation)

### Security threats with Reflections
However, this method of implementing a mapper to and from RDF comes with a serious limitation in terms of security. The main aspect which can be labeled as a security threat in Java (and in other programming languages
like C# or Python) is Reflection and adaptions from this concept. Reflections allow developers to create and modify objects by only knowing the class itself. The aspects of a class (like its fields and methods) as a whole can 
be adapted without respecting any security structure like private or final fields, since they can be shut down programatically with simple method calls.

## InfluxDB
To prepare the data profiles for visualization, a storage method for the metrics has to be used. A common approach for continous monitoring are time-series databases like InfluxDB, which is a popular choice for storing
such values. This database explicitly provides Java API for using it inside a program.

Anyways the database has to be running outside of the program, otherwise nothing will be stored. As of now, no embedded mode (like Derby or GraphDB supports) is planned. For this usage case, a Bash-script called *startInflux.sh* is 
provided as well as a script for opening the InfluxDB-Console (for easy querying the data) with *startInfluxConsole.sh*.

# Visualization
To visualize the data stored in the InfluxDB instance, a method for easily process it is prefered. A tool, which supports our needs and can directly take the data from Influx is Grafana.

## Grafana
Grafana (https://grafana.com/) is a dashboard for visualization, which can be used in a browser after it is booted on the console. For that specific reason a Bash-script called *startGrafana.sh* is provided for starting the program up and 
automatically opens a browser window of the preferred browser with the URL of the dashboard, which is http://localhost:3000 in the default setting.

The Java API was not working by the time of the creation of the project so it is neglected and replaced by the browser version.

# Useful links
## API
* GraphDB Quick Start Guide: http://graphdb.ontotext.com/documentation/free/quick-start-guide.html
* InfluxDB: https://docs.influxdata.com/influxdb/v1.7/
* InfluxDB-Java: https://github.com/influxdata/influxdb-java
* InfluxDB: https://www.baeldung.com/java-influxdb
* Grafana Docs: https://grafana.com/docs/
* RDFBeans: https://rdfbeans.github.io/ 
* RDF4j: https://rdf4j.eclipse.org/
* Quaiie (usage of connectors): http://dqm.faw.jku.at/
## Blockchains
* Creating Blockchain in Java from Scratch: https://medium.com/programmers-blockchain/create-simple-blockchain-java-tutorial-from-scratch-6eeed3cb03fa
* Tutorial Code for Blockchains in Java: https://github.com/CryptoKass/NoobChain-Tutorial-Part-1/tree/master/src/noobchain
