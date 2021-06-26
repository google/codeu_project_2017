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

import codeu.chat.common.ConversationSummary;
import codeu.chat.common.NetworkCode;
import codeu.chat.server.Model;
import codeu.chat.server.SummaryView;
import codeu.chat.util.Serializers;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * The end-point that handles the client's request for all conversations and responds by sending a
 * conversation summary for each conversation.
 */
final class GetAllConversationsEndPoint extends EndPoint {

  public GetAllConversationsEndPoint() {
    super(NetworkCode.GET_ALL_CONVERSATIONS_REQUEST, NetworkCode.GET_ALL_CONVERSATIONS_RESPONSE);
  }

  @Override
  public void handleConnection(Model model, InputStream in, OutputStream out) throws IOException {
    var view = new SummaryView(model);
    var conversations = view.getConversations();

    Serializers.INTEGER.write(out, NetworkCode.GET_ALL_CONVERSATIONS_RESPONSE);
    Serializers.collection(ConversationSummary.SERIALIZER).write(out, conversations);
  }
}
