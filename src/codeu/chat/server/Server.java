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

package codeu.chat.server;

import codeu.chat.common.NetworkCode;
import codeu.chat.server.endpoints.EndPoint;
import codeu.chat.server.endpoints.EndPointRegistry;
import codeu.chat.util.Serializers;
import codeu.chat.util.Timeline;
import codeu.chat.util.connections.Connection;
import codeu.chat.util.logging.Log;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class Server {

  private final Timeline timeline = new Timeline();

  private final Model model = new Model();

  private Map<Integer, EndPoint> mEndPoints = new HashMap<>();

  public Server() {
    for (var endpoint : EndPointRegistry.instance.getAll()) {
      mEndPoints.put(endpoint.getRequestCode(), endpoint);
    }
  }

  public void handleConnection(Connection connection) {
    timeline.submit(() -> {
      try {
        Log.instance.info("Handling connection...");

        var type = Serializers.INTEGER.read(connection.in());

        var endPoint = mEndPoints.get(type);

        if (endPoint == null) {
          Log.instance.error("Unsupported request type %d.", type);

          // In the case that the message was not handled make a dummy message with
          // the type "NO_MESSAGE" so that the client still gets something.
          Serializers.INTEGER.write(connection.out(), NetworkCode.NO_MESSAGE);
        } else {
          endPoint.handleConnection(model, connection.in(), connection.out());
          Log.instance.info("Handled request type %d.", type);
        }
      } catch (IOException ex) {
        Log.instance.error("Exception while handling connection: %s", ex.getMessage());
      }

      try {
        connection.close();
      } catch (IOException ex) {
        Log.instance.error("Exception while closing connection: %s", ex.getMessage());
      }
    });
  }
}
