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
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;

import codeu.chat.util.Serializer;
import codeu.chat.util.Serializers;
import codeu.chat.util.Compression;
import codeu.chat.util.Compressions;
import codeu.chat.util.Time;
import codeu.chat.util.Uuid;

public final class User {

  public static final Compression<User> USER = new Compression<User>(){

    @Override
    public byte[] compress(User data){

        ByteArrayOutputStream userStream = new ByteArrayOutputStream();
        try{
          toStream(userStream, data);
        }catch (IOException e){
          e.printStackTrace();
        }
        byte[] byteUser = userStream.toByteArray();

        return Compressions.BYTES.compress(byteUser);

    }

    @Override
    public User decompress(byte[] data){

      data = Compressions.BYTES.decompress(data);

      ByteArrayInputStream byteUser = new ByteArrayInputStream(data);

      User userSummary = new User(Uuid.NULL, "", Time.now());
      try {
        userSummary = fromStream(byteUser);
      }catch (IOException e){
        e.printStackTrace();
      }
      return userSummary;
    }
  };

  public static final Serializer<User> SERIALIZER = new Serializer<User>() {

    @Override
    public void write(OutputStream out, User value) throws IOException {

      byte[] user = USER.compress(value);
      Serializers.BYTES.write(out, user);

    }

    @Override
    public User read(InputStream in) throws IOException {

      byte[] user = Serializers.BYTES.read(in);
      return USER.decompress(user);

    }
  };

  public final Uuid id;
  public final String name;
  public final Time creation;
  public Uuid token;

  public User(Uuid id, String name, Time creation) {

    this.id = id;
    this.name = name;
    this.creation = creation;
    this.token = null; // This will be null unless explicitly set.
                       // Is not transferred in the serializer for security.

  }

  /**
  * @param a, b The users that are compared to each other
  * @return true if the fields of the users are identical, otherwise false
  */
  public static boolean equals(User a, User b){
    return a.name.equals(b.name) && a.creation.compareTo(b.creation) == 0 && Uuid.equals(a.id, b.id);
  }

  /**
  * @brief Formerly the overridden Serializer write
  */
  public static void toStream(OutputStream out, User value) throws IOException{

      Uuid.SERIALIZER.write(out, value.id);
      Serializers.STRING.write(out, value.name);
      Time.SERIALIZER.write(out, value.creation);

  }

  /**
  * @brief Formerly the overridden Serializer read
  */
  public static User fromStream(InputStream in) throws IOException {

      return new User(
          Uuid.SERIALIZER.read(in),
          Serializers.STRING.read(in),
          Time.SERIALIZER.read(in)
      );

  }
}
