if [ ! -d "./bin/com/" ]; then
  unzip -d ./bin ./third_party/gson-2.8.0.jar
fi
sh make.sh
cd './bin'
java codeu.chat.ServerMain "100.101" "ABABAB" "2007"