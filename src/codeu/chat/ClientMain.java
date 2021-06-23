// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package codeu.chat;

import codeu.chat.client.Controller;
import codeu.chat.client.View;
import codeu.chat.client.commandline.Chat;
import codeu.chat.util.RemoteAddress;
import codeu.chat.util.connections.ClientConnectionSource;
import codeu.chat.util.connections.ConnectionSource;
import codeu.chat.util.logging.Log;
import java.util.Scanner;

final class ClientMain {

  public static void main(String[] args) {
    Log.instance.info("============================= START OF LOG =============================");

    Log.instance.info("Starting chat client...");

    final RemoteAddress address = RemoteAddress.parse(args[0]);

    final ConnectionSource source = new ClientConnectionSource(address.host, address.port);
    final Controller controller = new Controller(source);
    final View view = new View(source);

    Log.instance.info("Creating client...");
    final Chat chat = new Chat(controller, view);

    Log.instance.info("Created client");

    final Scanner input = new Scanner(System.in);

    while (chat.handleCommand(input)) {
      // everything is done in "run"
    }

    input.close();

    Log.instance.info("chat client has exited.");
  }
}
