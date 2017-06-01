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

import java.util.Collection;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

import codeu.chat.common.Relay;
import codeu.chat.common.Time;
import codeu.chat.common.Uuid;
import codeu.chat.common.Uuids;

public final class ServerTest {

  @Test
  public void testAddTeam() {

    final Server relay = new Server(8, 8);

    final Uuid team = makeTestUuid(3);
    final byte[] secret = { 0x00, 0x01, 0x02 };

    assertTrue(relay.addTeam(team, secret));
  }

  @Test
  public void testWriteSuccess() {

    final Server relay = new Server(8, 8);

    final Uuid team = makeTestUuid(3);
    final byte[] secret = { 0x00, 0x01, 0x02 };

    assertTrue(relay.addTeam(team, secret));

    assertTrue(relay.write(team,
                           secret,
                           relay.pack(makeTestUuid(4), "User", Time.now()),
                           relay.pack(makeTestUuid(5), "Conversation", Time.now()),
                           relay.pack(makeTestUuid(6), "Hello World", Time.now())));
  }

  @Test
  public void testWriteFailNoTeam() {

    final Server relay = new Server(8, 8);

    final Uuid team = makeTestUuid(3);
    final byte[] secret = { 0x00, 0x01, 0x02 };

    assertFalse(relay.write(team,
                           secret,
                           relay.pack(makeTestUuid(4), "User", Time.now()),
                           relay.pack(makeTestUuid(5), "Conversation", Time.now()),
                           relay.pack(makeTestUuid(6), "Hello World", Time.now())));
  }

  @Test
  public void testWriteFailWrongSecret() {

    final Server relay = new Server(8, 8);

    final Uuid team = makeTestUuid(3);
    final byte[] secret = { 0x00, 0x01, 0x02 };
    final byte[] wrongSecret = { 0x00, 0x01, 0x03 };

    assertTrue(relay.addTeam(team, secret));

    assertFalse(relay.write(team,
                           wrongSecret,
                           relay.pack(makeTestUuid(4), "User", Time.now()),
                           relay.pack(makeTestUuid(5), "Conversation", Time.now()),
                           relay.pack(makeTestUuid(6), "Hello World", Time.now())));
  }

  @Test
  public void testWriteAndReadSuccess() {

    final Server relay = new Server(8, 8);

    final Uuid team = makeTestUuid(3);
    final byte[] secret = { 0x00, 0x01, 0x02 };

    assertTrue(relay.addTeam(team, secret));

    assertTrue(relay.write(team,
                           secret,
                           relay.pack(makeTestUuid(4), "User", Time.now()),
                           relay.pack(makeTestUuid(5), "Conversation", Time.now()),
                           relay.pack(makeTestUuid(6), "Hello World", Time.now())));

    final Collection<Relay.Bundle> read = relay.read(team, secret, Uuids.NULL, 1);
    assertTrue(read.size() == 1);

    // By the assertion above this loop should only execute once as there should only
    // be a single value in the collection.

    for (final Relay.Bundle bundle : read) {
      assertTrue(Uuids.equals(bundle.team(), team));
      assertTrue(Uuids.equals(bundle.user().id(), makeTestUuid(4)));
      assertTrue(Uuids.equals(bundle.conversation().id(), makeTestUuid(5)));
      assertTrue(Uuids.equals(bundle.message().id(), makeTestUuid(6)));
    }
  }

  @Test
  public void testReadFailWrongSecret() {

    final Server relay = new Server(8, 8);

    final Uuid team = makeTestUuid(3);
    final byte[] secret = { 0x00, 0x01, 0x02 };
    final byte[] wrongSecret = { 0x00, 0x01, 0x00 };

    assertTrue(relay.addTeam(team, secret));

    assertFalse(relay.write(team,
                            wrongSecret,
                            relay.pack(makeTestUuid(4), "User", Time.now()),
                            relay.pack(makeTestUuid(5), "Conversation", Time.now()),
                            relay.pack(makeTestUuid(6), "Hello World", Time.now())));
  }

  @Test
  public void testReadFailMissingTeam() {

    final Server relay = new Server(8, 8);

    final Uuid team = makeTestUuid(3);
    final byte[] secret = { 0x00, 0x01, 0x02 };

    assertTrue(relay.addTeam(team, secret));

    assertFalse(relay.write(makeTestUuid(33),
                            secret,
                            relay.pack(makeTestUuid(4), "User", Time.now()),
                            relay.pack(makeTestUuid(5), "Conversation", Time.now()),
                            relay.pack(makeTestUuid(6), "Hello World", Time.now())));
  }

  @Test
  public void testReadLimited() {

    final Server relay = new Server(8, 1);

    final Uuid team = makeTestUuid(3);
    final byte[] secret = { 0x00, 0x01, 0x02 };

    assertTrue(relay.addTeam(team, secret));

    assertTrue(relay.write(team,
                           secret,
                           relay.pack(makeTestUuid(4), "User", Time.now()),
                           relay.pack(makeTestUuid(5), "Conversation", Time.now()),
                           relay.pack(makeTestUuid(6), "Hello World", Time.now())));

    assertTrue(relay.write(team,
                           secret,
                           relay.pack(makeTestUuid(4), "User", Time.now()),
                           relay.pack(makeTestUuid(5), "Conversation", Time.now()),
                           relay.pack(makeTestUuid(7), "Hello World... again", Time.now())));


    final Collection<Relay.Bundle> read = relay.read(team, secret, Uuids.NULL, 2);
    assertTrue(read.size() == 1);
  }

  @Test
  public void testHistoryOverwrite() {

    final Server relay = new Server(1, 8);

    final Uuid team = makeTestUuid(3);
    final byte[] secret = { 0x00, 0x01, 0x02 };

    assertTrue(relay.addTeam(team, secret));

    assertTrue(relay.write(team,
                           secret,
                           relay.pack(makeTestUuid(4), "User", Time.now()),
                           relay.pack(makeTestUuid(5), "Conversation", Time.now()),
                           relay.pack(makeTestUuid(6), "Hello World", Time.now())));

    assertTrue(relay.write(team,
                           secret,
                           relay.pack(makeTestUuid(4), "User", Time.now()),
                           relay.pack(makeTestUuid(5), "Conversation", Time.now()),
                           relay.pack(makeTestUuid(7), "Hello World... again", Time.now())));

    final Collection<Relay.Bundle> read = relay.read(team, secret, Uuids.NULL, 2);
    assertTrue(read.size() == 1);

    // By the assertion above this loop should only execute once as there should only
    // be a single value in the collection.

    for (final Relay.Bundle bundle : read) {
      assertTrue(Uuids.equals(bundle.team(), team));
      assertTrue(Uuids.equals(bundle.user().id(), makeTestUuid(4)));
      assertTrue(Uuids.equals(bundle.conversation().id(), makeTestUuid(5)));
      assertTrue(Uuids.equals(bundle.message().id(), makeTestUuid(7)));
    }
  }

  private static Uuid makeTestUuid(final int id) {
    return Uuids.complete(new Uuid() {
      @Override
      public Uuid root() { return null; }
      @Override
      public int id() { return id; }
    });
  }
}
