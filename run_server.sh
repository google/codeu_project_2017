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

TEAM_ID="$1"
TEAM_SECRET="$2"
PORT="$3"
RELAY_ADDRESS="$4"

if [[ "TEAM_ID" == "" || "$TEAM_SECRET" == "" || "$PORT" == "" ]] ; then
  echo 'usage: <TEAM ID> <TEAM SECRET> <PORT> [RELAY ADDRESS]'
  exit 1
fi

cd './bin'
if [ "$RELAY_ADDRESS" == "" ] ; then
  java codeu.chat.ServerMain \
      "$TEAM_ID" \
      "$TEAM_SECRET" \
      "$PORT"
else
  java codeu.chat.ServerMain \
      "$TEAM_ID" \
      "$TEAM_SECRET" \
      "$PORT" \
      "$RELAY_ADDRESS"
fi
