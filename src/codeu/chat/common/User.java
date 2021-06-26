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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.UUID;

public final class User {

  public static final Serializer<User> SERIALIZER = new Serializer<User>() {

    @Override
    public void write(OutputStream out, User value) throws IOException {
      Serializers.UUID.write(out, value.id);
      Serializers.STRING.write(out, value.name);
      Serializers.DATE.write(out, value.creation);

    }

    @Override
    public User read(InputStream in) throws IOException {

      return new User(
          Serializers.UUID.read(in),
          Serializers.STRING.read(in),
          Serializers.DATE.read(in)
      );

    }
  };

  public final UUID id;
  public final String name;
  public final Date creation;

  public User(UUID id, String name, Date creation) {

    this.id = id;
    this.name = name;
    this.creation = creation;

  }
}
