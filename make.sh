#!/bin/bash

# Copyright 2017 Google Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

set -e

mkdir -p bin

# # unsuccessful attempts at trying to add all the nessesary jars to the classpath
# javac -Xlint $(find * | grep "\\.java$") -d ./bin -sourcepath ./src -cp ./third_party/junit4.jar:./third_party/jersey-common-2.25.1.jar:./third_party/jaxrs-ri/api/javax.ws.rs-api-2.0.1.jar:./third_party/jersey-container-grizzly2-http-2.26-b03-sources.jar:./third_party/jersey-container-grizzly2-http-2.26-b03.jar:./third_party/grizzly-http-server-2.3.28.jar:./third_party/jersey-server-2.25.1.jar:./bin

# javac -Xlint $(find * | grep "\\.java$") -d ./bin -sourcepath ./src -cp ./third_party/junit4.jar:./third_party/jaxrs-ri/api/javax.ws.rs-api-2.0.1.jar:./third_party/jaxrs-ri/lib/jersey-common.jar:./third_party/jaxrs-ri/lib/jersey-server.jar:./third_party/jersey-container-grizzly2-http-2.26-b03-sources.jar:./third_party/jersey-container-grizzly2-http-2.26-b03.jar:./third_party/grizzly-http-server-2.3.28.jar:./bin

javac -Xlint $(find * | grep "\\.java$") -d ./bin -sourcepath ./src -cp ./third_party/junit4.jar:./bin
javac -Xlint $(find * | grep "\\.java$") -d ./bin -sourcepath ./test -cp ./third_party/junit4.jar:./bin
