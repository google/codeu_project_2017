[![Build Status](https://travis-ci.org/petosa/codeu_project_2017.svg?branch=master)](https://travis-ci.org/petosa/codeu_project_2017)
# Magenta Messenger

## WHAT'S NEW?
I implemented a REST web api for this server backend provided by Google.
Out of the box, the communication mechanism used serializiation and an arbitrary
protocol using network codes to direct traffic. This is practically impossible to
use in the Javascript context we were looking for, so it was necessary to implement
a RESTful api on top of the existing server.

The API is implemented in such way that the server can receive both HTTP requests and
Google encoded requests in the same stream: it intelligently detects incoming messages
and switches functionality based off protocol. I kept Google protocol functionality to
allow for communication with Relay, which supports the Google protocol rather than the
RESTful protocol. That said, for purposes outside of Relay, the REST API enjoys wide-
spread support form libraries like Ajax for use within Javascript. It would almost
be impossible to implement a Node.js webserver frontend with a RESTful API to communicate
with the backend, especially since the Google protocol relayed serialized Java objects,
making it a language dependent protocol.

Each REST API function is fully tested, with JUnit tests automatically running in Travis
CI after each push. The Wiki section of the project now sports thorough API documentation
so that web developers can easily implement functionality (for u Ayesha).


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
