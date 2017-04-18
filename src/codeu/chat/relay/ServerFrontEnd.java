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

package codeu.chat.relay;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

import codeu.chat.common.NetworkCode;
import codeu.chat.common.Relay;
import codeu.chat.common.Secret;
import codeu.chat.util.Logger;
import codeu.chat.util.Serializer;
import codeu.chat.util.Serializers;
import codeu.chat.util.Time;
import codeu.chat.util.Uuid;
import codeu.chat.util.connections.Connection;

public final class ServerFrontEnd {

  private final static Logger.Log LOG = Logger.newLog(ServerFrontEnd.class);

  private static final Serializer<Relay.Bundle.Component> COMPONENT_SERIALIZER =
      new Serializer<Relay.Bundle.Component>() {

    @Override
    public Relay.Bundle.Component read(InputStream in) throws IOException {

      final Uuid id = Uuid.SERIALIZER.read(in);
      final String text = Serializers.STRING.read(in);
      final Time time = Time.SERIALIZER.read(in);

      // I could have passed the relay and use its "pack" method but that would
      // have been more work than just building an object here.
      return new Relay.Bundle.Component() {
        @Override
        public Uuid id() { return id; }
        @Override
        public String text() { return text; }
        @Override
        public Time time() { return time; }
      };
    }

    @Override
    public void write(OutputStream out, Relay.Bundle.Component value) throws IOException {
      Uuid.SERIALIZER.write(out, value.id());
      Serializers.STRING.write(out, value.text());
      Time.SERIALIZER.write(out, value.time());
    }
  };

  private static final Serializer<Relay.Bundle> BUNDLE_SERIALIZER =
      new Serializer<Relay.Bundle>() {

    @Override
    public Relay.Bundle read(InputStream in) throws IOException {

      final Uuid id = Uuid.SERIALIZER.read(in);
      final Time time = Time.SERIALIZER.read(in);
      final Uuid team = Uuid.SERIALIZER.read(in);
      final Relay.Bundle.Component user = COMPONENT_SERIALIZER.read(in);
      final Relay.Bundle.Component conversation = COMPONENT_SERIALIZER.read(in);
      final Relay.Bundle.Component message = COMPONENT_SERIALIZER.read(in);

      return new Relay.Bundle() {
        @Override
        public Uuid id() { return id; }
        @Override
        public Time time() { return time; }
        @Override
        public Uuid team() { return team; }
        @Override
        public Relay.Bundle.Component user() { return user; }
        @Override
        public Relay.Bundle.Component conversation() { return conversation; }
        @Override
        public Relay.Bundle.Component message() { return message; }
      };
    }

    @Override
    public void write(OutputStream out, Relay.Bundle value) throws IOException {
      Uuid.SERIALIZER.write(out, value.id());
      Time.SERIALIZER.write(out, value.time());
      Uuid.SERIALIZER.write(out, value.team());
      COMPONENT_SERIALIZER.write(out, value.user());
      COMPONENT_SERIALIZER.write(out, value.conversation());
      COMPONENT_SERIALIZER.write(out, value.message());
    }
  };

  private final Relay backEnd;

  public ServerFrontEnd(Relay backEnd) {
    this.backEnd = backEnd;
  }

  public void handleConnection(Connection connection) throws IOException {

    LOG.info("Handling Connection - start");

    switch (Serializers.INTEGER.read(connection.in())) {
      case NetworkCode.RELAY_READ_REQUEST: handleReadMessage(connection); break;
      case NetworkCode.RELAY_WRITE_REQUEST: handleWriteMessage(connection); break;
    }

    LOG.info("Handling Connection - end");
  }

  private void handleReadMessage(Connection connection) throws IOException {

    LOG.info("Handling Read Message - start");

    final Uuid teamId = Uuid.SERIALIZER.read(connection.in());
    final Secret teamSecret = Secret.SERIALIZER.read(connection.in());
    final Uuid root = Uuid.SERIALIZER.read(connection.in());
    final int range = Serializers.INTEGER.read(connection.in());

    LOG.info(
        "Reading team=%s root=%s range=%d",
        teamId,
        root,
        range);

    final Collection<Relay.Bundle> result = backEnd.read(teamId, teamSecret, root, range);

    LOG.info("Reading result.size=%d", result.size());

    Serializers.INTEGER.write(connection.out(), NetworkCode.RELAY_READ_RESPONSE);
    Serializers.collection(BUNDLE_SERIALIZER).write(connection.out(), result);

    LOG.info("Handling Read Message - end");
  }

  private void handleWriteMessage(Connection connection) throws IOException {

    LOG.info("Handling Write Message - start");

    final Uuid teamId = Uuid.SERIALIZER.read(connection.in());
    final Secret teamSecret = Secret.SERIALIZER.read(connection.in());
    final Relay.Bundle.Component user = COMPONENT_SERIALIZER.read(connection.in());
    final Relay.Bundle.Component conversation = COMPONENT_SERIALIZER.read(connection.in());
    final Relay.Bundle.Component message = COMPONENT_SERIALIZER.read(connection.in());

    LOG.info(
        "Writing team=%s user=%s conversation=%s message=%s",
        teamId,
        user.id(),
        conversation.id(),
        message.id());

    final boolean result = backEnd.write(teamId,
                                         teamSecret,
                                         user,
                                         conversation,
                                         message);

    LOG.info("Writing result=%s", result ? "success" : "fail");

    Serializers.INTEGER.write(connection.out(), NetworkCode.RELAY_WRITE_RESPONSE);
    Serializers.BOOLEAN.write(connection.out(), result);

    LOG.info("Handling Write Message - end");
  }
}
