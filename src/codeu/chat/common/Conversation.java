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
import java.util.Collection;
import java.util.HashSet;

import codeu.chat.util.Serializer;
import codeu.chat.util.Serializers;
import codeu.chat.util.Compression;
import codeu.chat.util.Compressions;
import codeu.chat.util.Time;
import codeu.chat.util.Uuid;

public final class Conversation {

  public static final Compression<Conversation> CONVERSATION = new Compression<Conversation>(){

    @Override
    public byte[] compress(Conversation data){
        ByteArrayOutputStream convoStream = new ByteArrayOutputStream();
        try{
            toStream(convoStream, data);
        }catch (IOException e){
            e.printStackTrace();
        }
        byte[] byteConvo = convoStream.toByteArray();

        return Compressions.BYTES.compress(byteConvo);
    }

    @Override
    public Conversation decompress(byte[] data){

      data = Compressions.BYTES.decompress(data);

      ByteArrayInputStream byteConvo = new ByteArrayInputStream(data);

      Conversation convo = new Conversation(Uuid.NULL, Uuid.NULL, Time.now(), "");
      try {
        convo = fromStream(byteConvo);
      }catch (IOException e){
          e.printStackTrace();
      }
      return convo;
    }

  };

  public static final Serializer<Conversation> SERIALIZER = new Serializer<Conversation>() {

    @Override
    public void write(OutputStream out, Conversation value) throws IOException {

      byte[] conversation = CONVERSATION.compress(value);
      Serializers.BYTES.write(out, conversation);

    }

    @Override
    public Conversation read(InputStream in) throws IOException {

      byte[] conversation = Serializers.BYTES.read(in);
      return CONVERSATION.decompress(conversation);

    }
  };

  public final ConversationSummary summary;

  public final Uuid id;
  public final Uuid owner;
  public final Time creation;
  public final String title;
  public final Collection<Uuid> users = new HashSet<>();
  public Uuid firstMessage = Uuid.NULL;
  public Uuid lastMessage = Uuid.NULL;

  public Conversation(Uuid id, Uuid owner, Time creation, String title) {

    this.id = id;
    this.owner = owner;
    this.creation = creation;
    this.title = title;

    this.summary = new ConversationSummary(id, owner, creation, title);

  }

  /**
  * @param a, b The conversations that are compared to each other
  * @return true if the fields of the conversations are identical, otherwise false
  */
  public static boolean equals(Conversation a, Conversation b){
    //Checking the conversation summary for equality checks all other fields
    return a.users.equals(b.users) && ConversationSummary.equals(a.summary, b.summary);
  }

  /**
  * @brief Formerly the overridden Serializer write
  */
  public static void toStream(OutputStream out, Conversation value) throws IOException{
      Uuid.SERIALIZER.write(out, value.id);
      Uuid.SERIALIZER.write(out, value.owner);
      Time.SERIALIZER.write(out, value.creation);
      Serializers.STRING.write(out, value.title);
      Serializers.collection(Uuid.SERIALIZER).write(out, value.users);
      Uuid.SERIALIZER.write(out, value.firstMessage);
      Uuid.SERIALIZER.write(out, value.lastMessage);
  }

  /**
  * @brief Formerly the overridden Serializer read
  */
  public static Conversation fromStream(InputStream in) throws IOException {

      final Conversation value = new Conversation(
          Uuid.SERIALIZER.read(in),
          Uuid.SERIALIZER.read(in),
          Time.SERIALIZER.read(in),
          Serializers.STRING.read(in)
      );

      value.users.addAll(Serializers.collection(Uuid.SERIALIZER).read(in));

      value.firstMessage = Uuid.SERIALIZER.read(in);
      value.lastMessage = Uuid.SERIALIZER.read(in);

      return value;

  }
}
