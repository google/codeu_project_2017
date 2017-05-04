#!/usr/bin/env bash

if [ $1 == 'reset' ]; then
    java -cp ./third_party/sqlite-jdbc-3.16.1.jar:./bin codeu.chat.codeU_db.ResetDatabase
else
    java -cp ./third_party/sqlite-jdbc-3.16.1.jar:./bin codeu.chat.codeU_db.CreateDatabase
fi