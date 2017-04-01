#!/bin/bash
if [ ! -d "./bin/com/" ]; then
  unzip -d ./bin ./third_party/gson-2.8.0.jar
fi
sh make.sh
LOCAL_MACHINE="$1@2007"

cd './bin'

java codeu.chat.MainGuiClient "$LOCAL_MACHINE"