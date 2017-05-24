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

package codeu.chat.client.commandline;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

// PANEL
//
// A panel is a collection of commands that are to be executed within a specifc
// context. Similar to how panels are used in a GUI, is is a command line
// equivalent.
//
final class Panel {

  public interface Command {
    void invoke(List<String> args);
  }

  private final Map<String, Command> commands = new HashMap<>();

  // REGISTER
  //
  // Register the command to be called when the given command name is
  // given on the command line.
  //
  public void register(String commandName, Command command) {
    commands.put(commandName, command);
  }

  // HANDLE COMMAND
  //
  // Given a command name and the rest of the line (from the command line) call
  // the correct command. If no command is found for the givem command name, false
  // will be returned. True will be return if a command is found. Whether or not
  // the command was successful is not returned.
  //
  public boolean handleCommand(String commandName, List<String> args) {
    final Command command = commands.get(commandName);
    if (command != null) {
      command.invoke(args);
    }
    return command != null;
  }
}
