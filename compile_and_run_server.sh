rm -rf ./bin/com/
rm -rf ./bin/META-INF/
unzip -d ./bin ./third_party/gson-2.8.0.jar
sh make.sh
cd './bin'
java codeu.chat.ServerMain "100.101" "ABABAB" "2007"