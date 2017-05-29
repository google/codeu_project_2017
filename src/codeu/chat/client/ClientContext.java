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

package codeu.chat.client;

import codeu.chat.client.ClientConversation;
import codeu.chat.client.ClientMessage;
import codeu.chat.client.ClientUser;
import codeu.chat.client.Controller;
import codeu.chat.client.View;
import codeu.chat.util.Timeline;
import codeu.chat.util.Logger;

public final class ClientContext {

  private static final Logger.Log LOG = Logger.newLog(ClientContext.class);
  private static final int SERVER_REFRESH_MS = 5000;  // 5 seconds
  public final ClientUser user;
  public final ClientConversation conversation;
  public final ClientMessage message;
  private final Timeline timeline = new Timeline();

  public ClientContext(Controller controller, View view) {
    user = new ClientUser(controller, view);
    conversation = new ClientConversation(controller, view, user);
    message = new ClientMessage(controller, view, user, conversation);

    timeline.scheduleNow(new Runnable() {
      @Override
      public void run() {
        try {
          LOG.info("Updating...");
          user.updateUsers();
          message.updateMessages(false);
        } catch (Exception ex) {
          LOG.error(ex, "Failed to update.");
        }
        timeline.scheduleIn(SERVER_REFRESH_MS, this);
      }
    });
  }
}
