# Client
Attached is my sample code for a Http Client that will generate an xml from any user inputted command and sends it to the server through a post method.

Each post request runs on its own thread which allows the user to continue to put in commands without waiting.

You can download the jar file from the "store/" directory.

The program can be run with the following command: "java -jar Client.jar"

You may specify the ip and port on run: -ip "ipAddress" -p "port"  (ie. java -jar Client.jar -ip servercomputer -p 6000)

Note: if no ip or port is specified, the default values are localhost:6175

The program life file is generated in the directory that it is run from
