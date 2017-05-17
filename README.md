
# CODEU CHAT SERVER | README



#########################################

## RUBAN'S README ADDITIONS

# Technologies Used
Jersey Framework (https://jersey.java.net/) 
(Allows Java to make and recieve http requests)

Apache Maven (https://maven.apache.org/) 
(Allows easier importing, updating, and managing of libraries)


# To Build/Run Java Server
Download Maven (https://maven.apache.org/download.cgi)
In the terminal, navigate to the home directory of the project
To build the project, run the following in the terminal:
  ```
  mvn clean
  mvn package
  ```

You will want to run the server, and one of the clients below:


To run the server, run the following in the terminal 
(the arguments are '<Team Id> <Team Secret> <Port> <Relay Address (optional)>':
  ```
  mvn exec:java -Dexec.args="1 1 2000" -Dexec.mainClass="com.codeu.Main"
  ```
  It should then start running on port specified, with additional resources running on port 8080 (this port number can be changed in the code)


To run the web client, run the following in the terminal 
(the arguments are: 'localhost@<Server's Port>'):
  ```
  mvn exec:java -Dexec.args="localhost@2000" -Dexec.mainClass="com.codeu.WebClientMain"
  ```

To run the commandline client, run the following in the terminal
(the arguments are: 'localhost@<Server's Port>'):
  ```
  mvn exec:java -Dexec.args="localhost@2000" -Dexec.mainClass="com.codeu.CommandLineClientMain"
  ```


Quit with 'control - C' in the terminal


# To Build/Run Angular2 Code
Download Node.js 6.9.x and npm 3.x.x or newer (https://nodejs.org/en/download/)
In the terminal, navigate to the home directory of the project
To run the Angular2 project, run the following in the terminal
  ```
  npm start
  ```

At the time of writing, Ruban is unsure whether other steps need to be followed, so if that does not work, you can try going here for other things to try before (https://angular.io/docs/ts/latest/cli-quickstart.html)

Once it starts, it should automatically direct you to: http://localhost:3000/dashboard

Quit with 'control - C' in the terminal

# Potentially Helpful Tutorials
For Jersey Framework's JAX-RS APIs: 
http://www.vogella.com/tutorials/REST/article.html


#########################################





## DISCLAIMER

CODEU is a program created by Google to develop the skills of future software
engineers. This project is not an offical Google Product. This project is a
playground for those looking to develop their coding and software engineering
skills.


## ENVIRONMENT

All instructions here are relative to a LINUX environment. There will be some
differences if you are working on a non-LINUX system. We will not support any
other development environment.

This project was built using JAVA 7. It is recommended that you install
JAVA&nbsp;7 when working with this project.


## GETTING STARTED

  1. To build the project:
       ```
       $ sh clean.sh
       $ sh make.sh
       ```

  1. To test the project:
       ```
       $ sh test.sh
       ```

  1. To run the project you will need to run both the client and the server. Run
     the following two commands in separate shells:

       ```
       $ sh run_server.sh
       $ sh run_client.sh
       ```

     The `run_server` and `run_client` scripts have hard-coded addresses for
     your local machine. If you are running the server on a different machine
     than the client, you will need to change the host portion of the address
     in `run_client.sh` to the name of the host where your server is running.
     Make sure the client and server are using the same port number.

All running images write informational and exceptional events to log files.
The default setting for log messages is "INFO". You may change this to get
more or fewer messages, and you are encouraged to add more LOG statements
to the code. The logging is implemented in `codeu.chat.util.Logger.java`,
which is built on top of `java.util.logging.Logger`, which you can refer to
for more information.

In addition to your team's client and server, the project also includes a
Relay Server and a script that runs it (`run_relay.sh`).
This is not needed to get started with the project.


## Finding your way around the project

All the source files (except test-related source files) are in
`./src/codeu/chat`.  The test source files are in `./test/codeu/chat`. If you
use the supplied scripts to build the project, the `.class` files will be placed
in `./bin`. There is a `./third_party` directory that holds the jar files for
JUnit (a Java testing framework). Your environment may or may not already have
this installed. The supplied scripts use the version in ./third_party.

Finally, there are some high-level design documents in the project Wiki. Please
review them as they can help you find your way around the sources.


## Source Directories

The major project components have been separated into their own packages. The
main packages/directories under `src/codeu/chat` are:

### codeu.chat.client

Classes for building the two clients (`codeu.chat.ClientMain` and
`codeu.chat.SimpleGuiClientMain`).

### codeu.chat.server

Classes for building the server (`codeu.chat.ServerMain`).

### codeu.chat.relay

Classes for building the Relay Server (`codeu.chat.RelayMain`). The Relay Server
is not needed to get started.

### codeu.chat.common

Classes that are shared by the clients and servers.

### codeu.chat.util

Some basic infrastructure classes used throughout the project.
