#!/bin/bash

# Created by Ruban

cd './bin'
java codeu.chat.WebServerMain


# if [ "$RELAY_ADDRESS" == "" ] ; then
#   java codeu.chat.ServerMain \
#       "$TEAM_ID" \
#       "$TEAM_SECRET" \
#       "$PORT"
# else
#   java codeu.chat.ServerMain \
#       "$TEAM_ID" \
#       "$TEAM_SECRET" \
#       "$PORT" \
#       "$RELAY_ADDRESS"
# fi
