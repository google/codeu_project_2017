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

package codeu.chat.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
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
import codeu.chat.util.connections.ConnectionSource;

public final class RemoteRelay implements Relay {

  private final static Logger.Log LOG = Logger.newLog(RemoteRelay.class);

  private static final class Component implements Relay.Bundle.Component {

    private final Uuid id;
    private final Time time;
    private final String text;

    public Component(Uuid id, Time time, String text) {
      this.id = id;
      this.time = time;
      this.text = text;
    }

    @Override
    public Uuid id() { return id; }

    @Override
    public Time time() { return time; }

    @Override
    public String text() { return text; }
  }

  private static final Serializer<Relay.Bundle.Component> COMPONENT_SERIALIZER =
      new Serializer<Relay.Bundle.Component>() {

    @Override
    public Relay.Bundle.Component read(InputStream in) throws IOException {

      final Uuid id = Uuid.SERIALIZER.read(in);
      final String text = Serializers.STRING.read(in);
      final Time time = Time.SERIALIZER.read(in);

      return new Component(id, time, text);
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

  private final ConnectionSource source;

  public RemoteRelay(ConnectionSource source) {
    this.source = source;
  }

  @Override
  public Relay.Bundle.Component pack(Uuid id, String text, Time time) {
    return new Component(id, time, text);
  }

  @Override
  public boolean write(Uuid teamId,
                       Secret teamSecret,
                       Relay.Bundle.Component user,
                       Relay.Bundle.Component conversation,
                       Relay.Bundle.Component message) {

    boolean result = false;

    try (final Connection connection = source.connect()) {

      Serializers.INTEGER.write(connection.out(), NetworkCode.RELAY_WRITE_REQUEST);
      Uuid.SERIALIZER.write(connection.out(), teamId);
      Secret.SERIALIZER.write(connection.out(), teamSecret);
      COMPONENT_SERIALIZER.write(connection.out(), user);
      COMPONENT_SERIALIZER.write(connection.out(), conversation);
      COMPONENT_SERIALIZER.write(connection.out(), message);

      if (Serializers.INTEGER.read(connection.in()) == NetworkCode.RELAY_WRITE_RESPONSE) {
        result = Serializers.BOOLEAN.read(connection.in());
      } else {
        LOG.error("Server did not handle RELAY_WRITE_REQUEST");
      }
    } catch (Exception ex) {
      LOG.error(ex, "Unexpected error when sending RELAY_WRITE_REQUEST");;
    }

    return result;
  }

  @Override
  public Collection<Relay.Bundle> read(Uuid teamId, Secret teamSecret, Uuid root, int range) {

    final Collection<Relay.Bundle> result = new ArrayList<>();

    try (final Connection connection = source.connect()) {

      Serializers.INTEGER.write(connection.out(), NetworkCode.RELAY_READ_REQUEST);
      Uuid.SERIALIZER.write(connection.out(), teamId);
      Secret.SERIALIZER.write(connection.out(), teamSecret);
      Uuid.SERIALIZER.write(connection.out(), root);
      Serializers.INTEGER.write(connection.out(), range);

      if (Serializers.INTEGER.read(connection.in()) == NetworkCode.RELAY_READ_RESPONSE) {
        result.addAll(Serializers.collection(BUNDLE_SERIALIZER).read(connection.in()));
      } else {
        LOG.error("Server did not handle RELAY_READ_REQUEST");
      }
    } catch (Exception ex) {
      LOG.error(ex, "Unexpected error when sending RELAY_READ_REQUEST");
    }

    return result;
  }
}
