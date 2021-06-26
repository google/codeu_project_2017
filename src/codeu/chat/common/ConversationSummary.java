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

public final class ConversationSummary {

  public static final Serializer<ConversationSummary> SERIALIZER = new Serializer<>() {

    @Override
    public void write(OutputStream out, ConversationSummary value) throws IOException {

      Serializers.UUID.write(out, value.id);
      Serializers.UUID.write(out, value.owner);
      Serializers.DATE.write(out, value.creation);
      Serializers.STRING.write(out, value.title);

    }

    @Override
    public ConversationSummary read(InputStream in) throws IOException {

      return new ConversationSummary(
          Serializers.UUID.read(in),
          Serializers.UUID.read(in),
          Serializers.DATE.read(in),
          Serializers.STRING.read(in)
      );

    }
  };

  public final UUID id;
  public final UUID owner;
  public final Date creation;
  public final String title;

  public ConversationSummary(UUID id, UUID owner, Date creation, String title) {

    this.id = id;
    this.owner = owner;
    this.creation = creation;
    this.title = title;

  }
}
