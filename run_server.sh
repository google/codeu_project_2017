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

PORT="$1"
PERSISTENT_DIR="$2"

if [[ "$PORT" == "" || "$PERSISTENT_DIR" == "" ]] ; then
  echo 'usage: <PORT> <PERSISTENT>'
  echo ''
  echo 'PORT :           The port that the server will listen to for incoming '
  echo '                 connections. This can be anything from 1024 to 65535.'
  echo 'PERSISTENT DIR : The directory where the server can save data that will'
  echo '                 exists between runs.'
  echo ''
  exit 1
fi

cd './bin'
java codeu.chat.ServerMain "$PORT" "$PERSISTENT_DIR"
