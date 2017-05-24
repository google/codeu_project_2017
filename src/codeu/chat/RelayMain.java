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
import java.io.FileReader;
import java.io.IOException;

import codeu.chat.common.Secret;
import codeu.chat.relay.Server;
import codeu.chat.relay.ServerFrontEnd;
import codeu.chat.util.Logger;
import codeu.chat.util.Timeline;
import codeu.chat.util.Uuid;
import codeu.chat.util.connections.Connection;
import codeu.chat.util.connections.ConnectionSource;
import codeu.chat.util.connections.ServerConnectionSource;

final class RelayMain {

  private static final Logger.Log LOG = Logger.newLog(RelayMain.class);

  public static void main(String[] args) {

    Logger.enableConsoleOutput();

    try {
      Logger.enableFileOutput("chat_relay_log.log");
    } catch (IOException ex) {
      LOG.error(ex, "Failed to set logger to write to file");
    }

    LOG.info("============================= START OF LOG =============================");

    final int myPort = Integer.parseInt(args[0]);

    try (final ConnectionSource source = ServerConnectionSource.forPort(myPort)) {

      // Limit the number of messages that the server tracks to be 1024 and limit the
      // max number of messages that the relay will send out to be 16.
      final Server relay = new Server(1024, 16);

      LOG.info("Relay object created.");

      LOG.info("Starting relay...");

      startRelay(relay, source, args[1]);

    } catch (IOException ex) {
      LOG.error(ex, "Failed to establish server accept port");
    }
  }

  private static void startRelay(final Server relay,
                                 final ConnectionSource source,
                                 final String teamFile) {

    final ServerFrontEnd frontEnd = new ServerFrontEnd(relay);
    LOG.info("Relay front end object created.");

    final Timeline timeline = new Timeline();
    LOG.info("Relay timeline created.");

    timeline.scheduleNow(new Runnable() {
      @Override
      public void run() {
        LOG.info("Loading team data...");
        loadTeamInfo(relay, teamFile);
        LOG.info("Done loading team data.");

        // Add this again in 1 minute so that new team entries will be added to
        // the relay. This won't support updating entries.
        timeline.scheduleIn(60000, this);
      }
    });

    LOG.info("Starting relay main loop...");

    while (true) {
      try {

        LOG.info("Establishing connection...");
        final Connection connection = source.connect();
        LOG.info("Connection established.");

        timeline.scheduleNow(new Runnable() {
          @Override
          public void run() {
            try {
              frontEnd.handleConnection(connection);
            } catch (Exception ex) {
              LOG.error(ex, "Exception handling connection.");
            }
          }
        });

      } catch (IOException ex) {
        LOG.error(ex, "Failed to establish connection.");
      }
    }
  }

  private static void loadTeamInfo(Server relay, String file) {

    try (final BufferedReader reader = new BufferedReader(new FileReader(file))) {

      String line;
      for (line = reader.readLine();
           line != null;
           line = reader.readLine()) {

        line = line.trim();

        if (line.length() == 0) {
          // This line is blank, skip it
        } else if (line.startsWith("#")) {
          // this is a comment, skip it
        } else {

          try {

            final String[] tokens = line.split(":");

            // There are just so many things that could go wrong when parsing
            // this line that it is not worth trying to handle ahead of time.
            // So instead just try to parse it and catch any exception.

            final Uuid id = Uuid.parse(tokens[0].trim());
            final Secret secret = Secret.parse(tokens[1].trim());

            relay.addTeam(id, secret);
          } catch (Exception ex) {
            LOG.error(ex, "Skipping line \"%s\". Could not parse", line);
          }
        }
      }
    } catch (IOException ex) {
      LOG.error(ex, "Failed to load team data");
    }
  }
}
