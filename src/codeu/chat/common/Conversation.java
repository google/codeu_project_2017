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
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

public final class Conversation {

  public static final Serializer<Conversation> SERIALIZER = new Serializer<>() {

    @Override
    public void write(OutputStream out, Conversation value) throws IOException {
      Serializers.UUID.write(out, value.id);
      Serializers.UUID.write(out, value.owner);
      Time.SERIALIZER.write(out, value.creation);
      Serializers.STRING.write(out, value.title);
      Serializers.collection(Serializers.UUID).write(out, value.users);

      if (value.lastMessage == null) {
        Serializers.BOOLEAN.write(out, false);
      } else {
        Serializers.BOOLEAN.write(out, true);
        Serializers.UUID.write(out, value.lastMessage);
      }
    }

    @Override
    public Conversation read(InputStream in) throws IOException {
      var id = Serializers.UUID.read(in);
      var owner = Serializers.UUID.read(in);
      var created = Time.SERIALIZER.read(in);
      var title = Serializers.STRING.read(in);
      var users = Serializers.collection(Serializers.UUID).read(in);
      var lastMessage = Serializers.BOOLEAN.read(in) ? Serializers.UUID.read(in) : null;

      return new Conversation(id, owner, created, title, users, lastMessage);
    }
  };

  public final UUID id;
  public final UUID owner;
  public final Time creation;
  public final String title;
  public final Collection<UUID> users;
  public final UUID lastMessage;

  public Conversation(UUID id, UUID owner, Time creation, String title, Collection<UUID> users,
      UUID lastMessage) {
    this.id = id;
    this.owner = owner;
    this.creation = creation;
    this.title = title;
    this.users = new HashSet<>(users);
    this.lastMessage = lastMessage;
  }
}
