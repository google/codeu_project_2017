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
import java.util.Collection;
import java.util.HashSet;

import codeu.chat.common.Uuid;
import codeu.chat.common.Uuids;
import codeu.chat.util.Serializer;
import codeu.chat.util.Serializers;

public final class Conversation {

  public static final Serializer<Conversation> SERIALIZER = new Serializer<Conversation>() {

    @Override
    public void write(OutputStream out, Conversation value) throws IOException {

      Uuids.SERIALIZER.write(out, value.id);
      Uuids.SERIALIZER.write(out, value.owner);
      Time.SERIALIZER.write(out, value.creation);
      Serializers.STRING.write(out, value.title);
      Serializers.collection(Uuids.SERIALIZER).write(out, value.users);
      Uuids.SERIALIZER.write(out, value.firstMessage);
      Uuids.SERIALIZER.write(out, value.lastMessage);

    }

    @Override
    public Conversation read(InputStream in) throws IOException {

      final Conversation value = new Conversation(
          Uuids.SERIALIZER.read(in),
          Uuids.SERIALIZER.read(in),
          Time.SERIALIZER.read(in),
          Serializers.STRING.read(in)
      );

      value.users.addAll(Serializers.collection(Uuids.SERIALIZER).read(in));

      value.firstMessage = Uuids.SERIALIZER.read(in);
      value.lastMessage = Uuids.SERIALIZER.read(in);

      return value;

    }
  };

  public final ConversationSummary summary;

  public final Uuid id;
  public final Uuid owner;
  public final Time creation;
  public final String title;
  public final Collection<Uuid> users = new HashSet<>();
  public Uuid firstMessage = Uuids.NULL;
  public Uuid lastMessage = Uuids.NULL;

  public Conversation(Uuid id, Uuid owner, Time creation, String title) {

    this.id = id;
    this.owner = owner;
    this.creation = creation;
    this.title = title;

    this.summary = new ConversationSummary(id, owner, creation, title);

  }
}
