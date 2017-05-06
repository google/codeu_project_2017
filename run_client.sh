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
if [[ "$1" == 'test' ]]; then
    java -cp ./third_party/sqlite-jdbc-3.16.1.jar:./bin codeu.chat.ClientMain "localhost@2222"
else

    HOST="$1"
    PORT="$2"

    if [[ "$HOST" == "" || "$PORT" == "" ]] ; then
      echo 'usage: <HOST> <PORT>'
      exit 1
    fi

    java -cp ./third_party/sqlite-jdbc-3.16.1.jar:./bin codeu.chat.ClientMain "$HOST@$PORT"
fi