# trustkg
A Blockchain-based Knowledge Graph

# BigChainDB
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

# Hashing Algorithms
Used in BigChainDB:
- Standard Hash: SHA3-256
- Keypair Hash: Ed25519 (Encoded via Base58)

# Alternative Approaches to Blockchain Technology
Since BigChainDB has serious flaws concerning supporting java, other approaches were needed. Some of those are:
	- Fluree (flur.ee)
	- GraphChain (makolab.com/en/innovation/graphchain)
	- Graphen Protocol (no Implementation available at the moment, only the whitepaper)
	- Taking a conventional Database and adding the blockchain aspect to it (Neo4j, GraphDB, CovenantSQL)

# FlureeDB
Fluree (https://flur.ee) is a semantic graph database with an approach towards immutability and is free to use. It is based on Java
and does not need any more prequesites than running on Java 8.
The server can be accessed via localhost:8080, if not specified otherwise.
A very detailed documentation for accessing and managing the database can be found via the Docs tab (link also in Section "Useful Links")

# GraphChain
RDF Database, Blockchain features
More Research on it needed!

# GraphDB
RDF Database, Java support, no Blockchain included!

# CovenantSQL
https://covenantsql.io/

# Neo4j
https://neo4j.com/

# ChainSQL
http://chainsql.net/
Warning: everything is in Chinese

# Useful links
JAVA API for BigChainDB: https://github.com/bigchaindb/java-bigchaindb-driver
Fluree Docs: https://docs.flur.ee/docs 
GraphDB Quick Start Guide: http://graphdb.ontotext.com/documentation/free/quick-start-guide.html
Creating Blockchain in Java from Scratch: https://medium.com/programmers-blockchain/create-simple-blockchain-java-tutorial-from-scratch-6eeed3cb03fa

# Competitors
GraphPath: https://www.graphpath.ai/

