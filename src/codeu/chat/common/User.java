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
import codeu.chat.common.Uuid;
import codeu.chat.common.Uuids;
import java.io.Serializable;

public final class User implements Serializable{

  public static final Serializer<User> SERIALIZER = new Serializer<User>() {

    @Override
    public void write(OutputStream out, User value) throws IOException {

      Uuids.SERIALIZER.write(out, value.id);
      Serializers.STRING.write(out, value.name);
      Time.SERIALIZER.write(out, value.creation);

    }

    @Override
    public User read(InputStream in) throws IOException {

      return new User(
          Uuids.SERIALIZER.read(in),
          Serializers.STRING.read(in),
          Time.SERIALIZER.read(in)
      );

    }
  };

  public final Uuid id;
  public final String name;
  public final Time creation;

  public User(Uuid id, String name, Time creation) {

    this.id = id;
    this.name = name;
    this.creation = creation;

  }
}
