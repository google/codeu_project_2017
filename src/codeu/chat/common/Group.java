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

import codeu.chat.util.Time;
import codeu.chat.util.Uuid;
import codeu.chat.util.Serializer;
import codeu.chat.util.Serializers;

public final class Group {

  public static final Serializer<Group> SERIALIZER = new Serializer<Group>() {

    @Override
    public void write(OutputStream out, Group value) throws IOException {

      Uuid.SERIALIZER.write(out, value.id);
      Uuid.SERIALIZER.write(out, value.owner);
      Time.SERIALIZER.write(out, value.creation);
      Serializers.STRING.write(out, value.title);
      Serializers.collection(Uuid.SERIALIZER).write(out, value.users);
      Uuid.SERIALIZER.write(out, value.firstConversation);
      Uuid.SERIALIZER.write(out, value.lastConversation);

    }

    @Override
    public Group read(InputStream in) throws IOException {

      final Group value = new Group(
          Uuid.SERIALIZER.read(in),
          Uuid.SERIALIZER.read(in),
          Time.SERIALIZER.read(in),
          Serializers.STRING.read(in)
      );

      value.users.addAll(Serializers.collection(Uuid.SERIALIZER).read(in));

      value.firstConversation = Uuid.SERIALIZER.read(in);
      value.lastConversation = Uuid.SERIALIZER.read(in);

      return value;

    }
  };

  public final GroupSummary summary;

  public final Uuid id;
  public final Uuid owner;
  public final Time creation;
  public final String title;
  public final Collection<Uuid> users = new HashSet<>();
  public Uuid firstConversation = Uuid.NULL;
  public Uuid lastConversation = Uuid.NULL;

  public Group(Uuid id, Uuid owner, Time creation, String title) {

    this.id = id;
    this.owner = owner;
    this.creation = creation;
    this.title = title;

    this.summary = new GroupSummary(id, owner, creation, title);

  }
}
