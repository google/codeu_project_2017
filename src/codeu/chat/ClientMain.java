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

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import codeu.chat.client.commandline.Chat;
import codeu.chat.client.core.Context;
import codeu.chat.util.RemoteAddress;
import codeu.chat.util.connections.ClientConnectionSource;
import codeu.chat.util.connections.ConnectionSource;

import codeu.chat.util.logging.ChatLog;
import codeu.logging.Logger;

final class ClientMain {

  private static final Logger LOG = ChatLog.logger(ClientMain.class);

  static {
    try {
      // Have the client write its log to a file. The log is set to always
      // append so that old runs do not get overwritten.
      ChatLog.register(new FileOutputStream("client.log", true));
    } catch (IOException ex) {
      // Cannot add the file writter as the file location is not availble.
    }
  }

  public static void main(String [] args) {

    final RemoteAddress address = RemoteAddress.parse(args[0]);
    final ConnectionSource source = new ClientConnectionSource(address.host, address.port);

    final Chat chat = new Chat(new Context(source));
    LOG.verbose("Created chat instance");

    boolean keepRunning = true;

    try (final BufferedReader input = new BufferedReader(new InputStreamReader(System.in))) {
      while (keepRunning) {

        System.out.print(">>> ");
        final String command = input.readLine().trim();

        LOG.verbose("Running command %s", command);
        keepRunning = chat.handleCommand(command);
      }
    } catch (IOException ex) {
      LOG.error(ex, "Unhandled exception during while executing command");
    }
  }
}
