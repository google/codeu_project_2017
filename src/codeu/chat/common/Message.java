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

import codeu.chat.util.Serializer;
import codeu.chat.util.Serializers;
import codeu.chat.util.Time;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public final class Message {

  public static final Serializer<Message> SERIALIZER = new Serializer<>() {
    @Override
    public void write(OutputStream out, Message value) throws IOException {
      Serializers.UUID.write(out, value.id);

      if (value.previous == null) {
        Serializers.BOOLEAN.write(out, false);
      } else {
        Serializers.BOOLEAN.write(out, true);
        Serializers.UUID.write(out, value.previous);
      }

      Time.SERIALIZER.write(out, value.creation);
      Serializers.UUID.write(out, value.author);
      Serializers.STRING.write(out, value.content);
    }

    @Override
    public Message read(InputStream in) throws IOException {
      var id = Serializers.UUID.read(in);

      UUID previous = null;
      if (Serializers.BOOLEAN.read(in)) {
        previous = Serializers.UUID.read(in);
      }

      var time = Time.SERIALIZER.read(in);
      var author = Serializers.UUID.read(in);
      var content = Serializers.STRING.read(in);

      return new Message(id, previous, time, author, content);
    }
  };

  public final UUID id;
  public final UUID previous;
  public final Time creation;
  public final UUID author;
  public final String content;

  public Message(UUID id, UUID previous, Time creation, UUID author, String content) {

    this.id = id;
    this.previous = previous;
    this.creation = creation;
    this.author = author;
    this.content = content;

  }
}
