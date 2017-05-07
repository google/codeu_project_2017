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

public final class ConversationSummary implements ListViewable {

  public static final Compression<ConversationSummary> CONVERSATION_SUMMARY = new Compression<ConversationSummary>(){

    @Override
    public byte[] compress(ConversationSummary data){

        ByteArrayOutputStream convoSummaryStream = new ByteArrayOutputStream();
        try{
          toStream(convoSummaryStream, data);
        }catch (IOException e){
            e.printStackTrace();
        }
        byte[] byteConvoSummary = convoSummaryStream.toByteArray();

        return Compressions.BYTES.compress(byteConvoSummary);

    }

    @Override
    public ConversationSummary decompress(byte[] data){

      data = Compressions.BYTES.decompress(data);

      ByteArrayInputStream byteConvoSummary = new ByteArrayInputStream(data);

      ConversationSummary convoSummary = new ConversationSummary(Uuid.NULL, Uuid.NULL, Time.now(), "");
      try {
        convoSummary = fromStream(byteConvoSummary);
      }catch (IOException e){
          e.printStackTrace();
      }
      return convoSummary;
    }
  };

  public static final Serializer<ConversationSummary> SERIALIZER = new Serializer<ConversationSummary>() {

    @Override
    public void write(OutputStream out, ConversationSummary value) throws IOException {

      byte[] conversationSummary = CONVERSATION_SUMMARY.compress(value);
      Serializers.BYTES.write(out, conversationSummary);

    }

    @Override
    public ConversationSummary read(InputStream in) throws IOException {

      byte[] conversationSummary = Serializers.BYTES.read(in);
      return CONVERSATION_SUMMARY.decompress(conversationSummary);

    }
  };

  public final Uuid id;
  public final Uuid owner;
  public final Time creation;
  public final String title;

  public ConversationSummary(Uuid id, Uuid owner, Time creation, String title) {

    this.id = id;
    this.owner = owner;
    this.creation = creation;
    this.title = title;

  }

  /**
  * @param a, b The summaries that are compared to each other
  * @return true if the fields of the summaries are identical, otherwise false
  */
  public static boolean equals(ConversationSummary a, ConversationSummary b){
    return a.title.equals(b.title) && a.creation.compareTo(b.creation) == 0 && Uuid.equals(a.id, b.id) 
    && Uuid.equals(a.owner, b.owner);
  }

  // How this object should appear in a user-viewable list
  @Override
  public String listView() {
    return title;
  }

  /**
  * @brief Formerly the overridden Serializer write
  */
  public static void toStream(OutputStream out, ConversationSummary value) throws IOException{

      Uuid.SERIALIZER.write(out, value.id);
      Uuid.SERIALIZER.write(out, value.owner);
      Time.SERIALIZER.write(out, value.creation);
      Serializers.STRING.write(out, value.title);

  }

  /**
  * @brief Formerly the overridden Serializer read
  */
  public static ConversationSummary fromStream(InputStream in) throws IOException {

      return new ConversationSummary(
          Uuid.SERIALIZER.read(in),
          Uuid.SERIALIZER.read(in),
          Time.SERIALIZER.read(in),
          Serializers.STRING.read(in)
      );

  }
}
