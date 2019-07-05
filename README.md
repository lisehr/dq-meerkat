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

# Useful links
JAVA API for BigChainDB: https://github.com/bigchaindb/java-bigchaindb-driver 
