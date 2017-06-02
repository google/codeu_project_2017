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

javac -Xlint $(find * | grep "\\.java$") -d ./bin -sourcepath ./src -cp ./third_party/junit4.jar:./third_party/mockito-core-2.8.29.jar:./third_party/javassist-3.18.1-GA.jar:./third_party/cglib-nodep-2.2.2.jar:./third_party/byte-buddy-1.6.5.jar:./third_party/byte-buddy-agent-1.6.5.jar:./third_party/objenesis-2.5.jar:./bin
javac -Xlint $(find * | grep "\\.java$") -d ./bin -sourcepath ./test -cp ./third_party/junit4.jar:./third_party/mockito-core-2.8.29.jar:./third_party/javassist-3.18.1-GA.jar:./third_party/cglib-nodep-2.2.2.jar:./third_party/byte-buddy-1.6.5.jar:./third_party/byte-buddy-agent-1.6.5.jar:./third_party/objenesis-2.5.jar:./bin



