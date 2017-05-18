
# CODEU CHAT SERVER | README


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

  There is a single python script build.py that is used to build and run
  the project. There are many ways to run a python script. The syntax
  below should work almost anywhere if the script is in your current directory.

  1. To build the project:
       ```
       $ python build.py clean
       $ python build.py build
       ```

  1. To test the project:
       ```
       $ python build.py run codeu.chat.TestRunner
       ```

  1. To run the project you will need to run both the client and the server. Run
     the following two commands in separate shells:

       ```
       $ python build.py run codeu.chat.ServerMain <team_id> <team_secret> <port> <persistent-dir>
       $ python build.py run codeu.chat.ClientMain "<host>@<port>"
       ```

     You must provide the following startup arguments to `ServerMain`:
     + `<team_id>` and `<team_secret>`: a numeric id for your team, and a secret
       code, which are used to authenticate your server with the Relay server.
       You can specify any integer value for `<team_id>`, and a value expressed
       in hexadecimal format (using numbers `0-9` and letters in the range
       `A-F`) for `<team_secret>` when you launch the server in your local setup
       since it will not connect to the Relay server.
     + `<port>`: the TCP port that your Server will listen on for connections
       from the Client. You can use any value between 1024 and 65535, as long as
       there is no other service currently listening on that port in your
       system. The server will return an error:

         ```
         java.net.BindException: Address already in use (Bind failed)
         ```

       if the port is already in use.
     + `<persistent-dir>`: the path where you want the server to save data between
       runs. This directory must exist when you start the server.

     The startup argument for running `ClientMain` is `<host>@<port>`:
     + `<host>`: the hostname or IP address of the computer on which the server
       is listening. If you are running server and client on the same computer,
       you can use `localhost` here.
     + `<port>`: the port on which your server is listening. Must be the same
       port number you specified when you launched `ServerMain`.
     + The "@" between `<host>` and `<port>` is mandatory.

All running images write informational and exceptional events to log files.
The default setting for log messages is "INFO". You may change this to get
more or fewer messages, and you are encouraged to add more LOG statements
to the code. The logging is implemented in `codeu.chat.util.Logger.java`,
which is built on top of `java.util.logging.Logger`, which you can refer to
for more information.

In addition to your team's client and server, the project also includes a
Relay Server. This is not needed to get your project started. You can start
it locally using `python build.py run codeu.chat.RelayMain <args>`. Look in
`RelayMain.Java` for information about arguments.


## Finding your way around the project

All the source files (except test-related source files) are in
`./src/codeu/chat`.  The test source files are in `./test/codeu/chat`. If you
use the supplied scripts to build the project, the `.class` files will be placed
in `./bin`. There is a `./third_party` directory that holds the jar files for
JUnit (a Java testing framework). Your environment may or may not already have
this installed. The supplied scripts use the version in `./third_party`.

Finally, there are some high-level design documents in the project Wiki. Please
review them as they can help you find your way around the sources.



## Source Directories

The major project components have been separated into their own packages. The
main packages/directories under `src/codeu/chat` are:

### codeu.chat.client

Classes for building a simple client (`codeu.chat.ClientMain`).

### codeu.chat.server

Classes for building the server (`codeu.chat.ServerMain`).

### codeu.chat.relay

Classes for building the Relay Server (`codeu.chat.RelayMain`). The Relay Server
is not needed to get started.

### codeu.chat.common

Classes that are shared by the clients and servers.

### codeu.chat.util

Some basic infrastructure classes used throughout the project.
