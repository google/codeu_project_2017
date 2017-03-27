#!/bin/bash

sh make.sh
LOCAL_MACHINE="$1@2007"

cd './bin'

java codeu.chat.MainGuiClient "$LOCAL_MACHINE"