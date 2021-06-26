// Copyright 2021 Google LLC.
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

package codeu.chat.server.endpoints;

import codeu.chat.common.Message;
import codeu.chat.common.NetworkCode;
import codeu.chat.server.CreationController;
import codeu.chat.server.Model;
import codeu.chat.util.Serializers;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * The end-point that handles the clients request to add new message to an existing conversation.
 */
final class NewMessageEndPoint extends EndPoint {

  public NewMessageEndPoint() {
    super(NetworkCode.NEW_MESSAGE_REQUEST, NetworkCode.NEW_MESSAGE_RESPONSE);
  }

  @Override
  public void handleConnection(Model model, InputStream in, OutputStream out) throws IOException {
    var author = Serializers.UUID.read(in);
    var conversation = Serializers.UUID.read(in);
    var content = Serializers.STRING.read(in);

    var controller = new CreationController(model);
    var message = controller.newMessage(author, conversation, content);

    Serializers.INTEGER.write(out, getResponseCode());
    Serializers.nullable(Message.SERIALIZER).write(out, message);
  }
}
