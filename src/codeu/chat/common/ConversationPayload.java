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

package codeu.chat.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import codeu.chat.util.Serializer;
import codeu.chat.util.Serializers;
import codeu.chat.util.Uuid;

public final class ConversationPayload {

  public static final Serializer<ConversationPayload> SERIALIZER = new Serializer<ConversationPayload>() {

    @Override
    public void write(OutputStream out, ConversationPayload value) throws IOException {

      Uuid.SERIALIZER.write(out, value.id);
      Uuid.SERIALIZER.write(out, value.firstMessage);
      Uuid.SERIALIZER.write(out, value.lastMessage);

    }

    @Override
    public ConversationPayload read(InputStream in) throws IOException {

      return new ConversationPayload(
          Uuid.SERIALIZER.read(in),
          Uuid.SERIALIZER.read(in),
          Uuid.SERIALIZER.read(in));

    }
  };

  public final Uuid id;

  // These are allowed to be updated and therefore are not marked final
  public Uuid firstMessage = Uuid.NULL;
  public Uuid lastMessage = Uuid.NULL;

  public ConversationPayload(Uuid id) {
    this.id = id;
  }

  public ConversationPayload(Uuid id, Uuid firstMessage, Uuid lastMessage) {
    this.id = id;
    this.firstMessage = firstMessage;
    this.lastMessage = lastMessage;
  }
}
