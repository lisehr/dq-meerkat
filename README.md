# Block DaQ
A Blockchain-based Knowledge Graph with methods for annotating data profiles to created schemes from CSV-Files.

# Blockchain aspect
To generate temper-free persistance of stored data a blockchain is used. Creating blockchains in Java is a very difficult task since Reflections exist (more in section Persistance),
so a pseudo-temper-free (therefore only temper-evident) chain is the nearest result is what can be achieved.

## Hashing Algorithms
Used in BigChainDB and generally a widespread approach:
- Standard Hash: SHA3-256
- Keypair Hash: Ed25519 (Encoded via Base58)

## ?Mini?chains
Minichains are a further approach to improve performance of blockchains. Since DSDElements are seperated objects, they can be seperated into several miniature versions of blockchains, one for each DSDElement.
As shown in the tests in the package *demos.alex.benchmarking*, these chains are slightly better in adding and creating (about 9-15%) and significantly better for accessing data (about 75%).
Each chain contains a blockchain for the element, adapted with an identifier for the minichain and flags used for merging and deletion control.

# Persistance
To persist changes in the chain and also generate a graph out of it, multiple storing methods are used. These are:
- GraphDB (for storing the RDF based graphs)
- InfluxDB to store records and the values from the data profiles

## GraphDB
GraphDB by ontotext is a software which can be downloaded and installed in its standalone form from http://graphdb.ontotext.com/. This method of storing knowledge graphs was mainly chosen because it supports rdf stores
and is usable via Java in an embedded mode. To store concrete Java objects in GraphDB, they have to be mapped to RDF format. Since Java does not naturally support such a mapping toolbox, an external library has to be chosen.

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

## InfluxDB


# Visualization

## Grafana


# Useful links
## API
* JAVA API for BigChainDB: https://github.com/bigchaindb/java-bigchaindb-driver
* Fluree Docs: https://docs.flur.ee/docs 
* GraphDB Quick Start Guide: http://graphdb.ontotext.com/documentation/free/quick-start-guide.html
* InfluxDB: https://docs.influxdata.com/influxdb/v1.7/
* InfluxDB-Java: https://github.com/influxdata/influxdb-java
* InfluxDB: https://www.baeldung.com/java-influxdb
## Blockchains
* Creating Blockchain in Java from Scratch: https://medium.com/programmers-blockchain/create-simple-blockchain-java-tutorial-from-scratch-6eeed3cb03fa
* Tutorial Code for Blockchains in Java: https://github.com/CryptoKass/NoobChain-Tutorial-Part-1/tree/master/src/noobchain

# Competitors
GraphPath: https://www.graphpath.ai/

# Alternative Approaches to Blockchain Technology (not used anymore in this project)

## BigChainDB
A local instance of bigchaindb is found at the folder BigChainDBServer. To manually install it, the following steps are required:
	- extract zip-folder
	- install make, docker and docker-compose
	- When running on linux, please make sure to follow these steps:
		- install docker.io
		- run docker in super user mode (sudo)
	- make sure that docker is running by checking the hello-world example "docker run hello-world"
	- open terminal window in the extracted folder and type "make run"

The server is then running via "http://localhost:9984". If the server throws Exceptions like (IOException: Unexpected End of Stream), then try to reconfigure the server
with the following instructions:
	- make clean
	- make run
Alternatively, "make start" daemonizes the server, so it can be stopped via "make stop".

Also, when running on linux, there exists a bash-script, which can start the server for you.
It can be accessed via "bash startServer.sh"

## Other Approaches to Blockchain Technology
Since BigChainDB has serious flaws concerning supporting java, other approaches were needed. Some of those are:
- Fluree (flur.ee)
- GraphChain (makolab.com/en/innovation/graphchain)
- Graphen Protocol (no Implementation available at the moment, only the whitepaper)
- Taking a conventional Database and adding the blockchain aspect to it (Neo4j, GraphDB, CovenantSQL)

### FlureeDB
Fluree (https://flur.ee) is a semantic graph database with an approach towards immutability and is free to use. It is based on Java
and does not need any more prequesites than running on Java 8.
The server can be accessed via localhost:8080, if not specified otherwise.
A very detailed documentation for accessing and managing the database can be found via the Docs tab (link also in Section "Useful Links")

### GraphChain
RDF Database, Blockchain features
More Research on it needed!

### GraphDB
RDF Database, Java support, no Blockchain included!

### CovenantSQL
https://covenantsql.io/

### Neo4j
https://neo4j.com/

### ChainSQL
http://chainsql.net/
Warning: everything is in Chinese

# Methods for persisting data
Our approach is to store needed data in three different databases, mainly taylormade for each usecase:
- Knowledge Graph: GraphDB
- Blockchain: still undecided, but something slim like SQLlite
- Timeseries for measurements: InfluxDB



