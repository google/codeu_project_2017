[![Build Status](https://travis-ci.org/petosa/codeu_project_2017.svg?branch=master)](https://travis-ci.org/petosa/codeu_project_2017)
# Magenta Messenger

Try it online! (Warning! Slow.) http://130.211.140.178:10110

## How to run

  1. To build the project:
       ```
       $ bash clean.sh
       $ bash make.sh
       ```

  2. To test the project:
       ```
       $ bash test.sh
       ```

  3. To run the project you will only need to run the server. The format for such a command is shown below.

       ```
       $ bash run_server.sh <team_id> <team_secret> <port> <persistent-dir>
       ```
      If you don't know what you should put here, you can just do 
       ```
       $ bash run_server.sh 16 16 8000 persistent
       ```
       and then connect to http://localhost:8000 to see the web client and start using the messenger locally.
       
       
## FEATURES
### REST API
We needed a REST API in order to talk to our server from a web server. This change is completely documented in terms of code changes in comments and in terms of functionality in our wiki's API documentation.
####PROS:
+ We now have a platform agnostic chat server and can host a mobile app, web app, or whatever using the same backend.
+ Dual protocol switching depending on incoming request; backwards compatible.
####CONS:
- Security problems by sharing a port for two protocols (network code and REST).

### WEB SERVER
We used our REST API to host static HTML, CSS, and JS files for clients to download to talk to our server.
####PROS:
+ Leverages our existing REST backend to and a huge amount of functionality.
+ Handles pre-flight requests as well as static content delivery.
####CONS:
- Security problems. Since pages and content are delivered in a very rudimentary way, it may be possible for a malicious actor to exploit our system.
### WEB SITE
We needed a web UI to replace the CLI and provide a clean interface for communicating with the server.
####PROS:
+ Can be accessed from mobile or desktop
+ Can leverage popular web frameworks to make a really cool UI
####CONS:
- There are better ways of implementing polling than checking every second.
- Different screensizes and browsers means compatability issues.
- Google's hosting is very slow.

## BUG SQUASHING
### Bug 1:
Issue: Conversion from String to int was failing.
Diagnosis: String needed to be converted to a Long before it could be cast to an int
Cure: Changed Integer.parseInt to (int) Long.parseLong.

### Bug 2:
Issue: when hosting on Google's server, web server hangs and does not respond to any requests.
Diagnosis: When Google's relay server is down, the timeline gets clogged and blocks any other requests from passing through. The result is a hanging server/
Cure: Completely removed Relay from my application (see commented out block in Server.java). It was only causing problems, and I value reliability.



## ENVIRONMENT

All instructions here are relative to a LINUX environment. There will be some
differences if you are working on a non-LINUX system. We will not support any
other development environment.

This project was built using JAVA 7. It is recommended that you install
JAVA&nbsp;7 when working with this project.


## Finding your way around the project

In terms of changes from the barebones server Google handed us; magmemgui is completely our code and contains all of our frontend web client code. For the server, all changes are inside Server.java, Request.java, or RequestHandler.java. Check the diff for exact changes.
