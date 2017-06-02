#!/bin/bash

# Runs main chat GUI using the <HOST> and <PORT> parameters

HOST="$1"
PORT="$2"

if [[ "$HOST" == "" || "$PORT" == "" ]] ; then
  echo 'usage: <HOST> <PORT>'
  exit 1
fi

cd './bin'

java codeu.chat.MainGuiClient "$HOST@$PORT"