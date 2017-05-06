#!/usr/bin/env bash

if [[ $# -eq 1 && $1 == 'reset' ]]; then
    java -cp ./third_party/sqlite-jdbc-3.16.1.jar:./bin codeu.chat.codeU_db.ResetDatabase
elif [[ $# -eq 1 && $1 == 'drop' ]]; then
    java -cp ./third_party/sqlite-jdbc-3.16.1.jar:./bin codeu.chat.codeU_db.DropDatabase
elif [[ $# -eq 1 && $1 == 'create' ]]; then
    java -cp ./third_party/sqlite-jdbc-3.16.1.jar:./bin codeu.chat.codeU_db.CreateDatabase
else
    echo 'Error: please supply one of (create/reset/drop) as a parameter'
fi