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

public final class User {

  public static final Serializer<User> SERIALIZER = new Serializer<User>() {

    @Override
    public void write(OutputStream out, User value) throws IOException {
      // System.out.println("in write User");
      Uuids.SERIALIZER.write(out, value.id);
      Serializers.STRING.write(out, value.name);
      Time.SERIALIZER.write(out, value.creation);
      Serializers.STRING.write(out, value.nickname);
      Serializers.STRING.write(out, value.getPassword());
    }

    @Override
    public User read(InputStream in) throws IOException {
      // System.out.println("in read User");
      return new User(
          Uuids.SERIALIZER.read(in),
          Serializers.STRING.read(in),
          Time.SERIALIZER.read(in),
          Serializers.STRING.read(in),
          Serializers.STRING.read(in)
      );
    }
  };

  public final Uuid id;
  public final String name;
  public final Time creation;
  public String nickname;
  private String password;

  public User(Uuid id, String name, Time creation) {

    this.id = id;
    this.name = name;
    this.creation = creation;

  }

  public User(Uuid id, String name, Time creation, String nickname, String pass) {

    this.id = id;
    this.name = name;
    this.nickname = nickname;
    this.creation = creation;
    this.password = pass;

  }

  public void setNickname(String nickname){
    this.nickname = nickname; 
  }

  public String getNickname(){
    return this.nickname;
  }

  public boolean ifCorrectPassword(String pass){
    return this.password.equals(pass);
  }

  //TODO: fix this
  public String getPassword(){
    return this.password;
  }
}
