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
import codeu.chat.common.Secret;
import codeu.chat.util.Time;
import codeu.chat.util.Uuid;

public final class ServerTest {

  @Test
  public void testAddTeam() {

    final Server relay = new Server(8, 8);

    final Uuid team = new Uuid(3);
    final Secret secret = new Secret((byte)0x00, (byte)0x01, (byte)0x02);

    assertTrue(relay.addTeam(team, secret));
  }

  @Test
  public void testWriteSuccess() {

    final Server relay = new Server(8, 8);

    final Uuid team = new Uuid(3);
    final Secret secret = new Secret((byte)0x00, (byte)0x01, (byte)0x02);

    assertTrue(relay.addTeam(team, secret));

    assertTrue(relay.write(team,
                           secret,
                           relay.pack(new Uuid(4), "User", Time.now()),
                           relay.pack(new Uuid(5), "Conversation", Time.now()),
                           relay.pack(new Uuid(6), "Hello World", Time.now())));
  }

  @Test
  public void testWriteFailNoTeam() {

    final Server relay = new Server(8, 8);

    final Uuid team = new Uuid(3);
    final Secret secret = new Secret((byte)0x00, (byte)0x01, (byte)0x02);

    assertFalse(relay.write(team,
                           secret,
                           relay.pack(new Uuid(4), "User", Time.now()),
                           relay.pack(new Uuid(5), "Conversation", Time.now()),
                           relay.pack(new Uuid(6), "Hello World", Time.now())));
  }

  @Test
  public void testWriteFailWrongSecret() {

    final Server relay = new Server(8, 8);

    final Uuid team = new Uuid(3);
    final Secret secret = new Secret((byte)0x00, (byte)0x01, (byte)0x02);
    final Secret wrongSecret = new Secret((byte)0x00, (byte)0x01, (byte)0x03);

    assertTrue(relay.addTeam(team, secret));

    assertFalse(relay.write(team,
                           wrongSecret,
                           relay.pack(new Uuid(4), "User", Time.now()),
                           relay.pack(new Uuid(5), "Conversation", Time.now()),
                           relay.pack(new Uuid(6), "Hello World", Time.now())));
  }

  @Test
  public void testWriteAndReadSuccess() {

    final Server relay = new Server(8, 8);

    final Uuid team = new Uuid(3);
    final Secret secret = new Secret((byte)0x00, (byte)0x01, (byte)0x02);

    assertTrue(relay.addTeam(team, secret));

    assertTrue(relay.write(team,
                           secret,
                           relay.pack(new Uuid(4), "User", Time.now()),
                           relay.pack(new Uuid(5), "Conversation", Time.now()),
                           relay.pack(new Uuid(6), "Hello World", Time.now())));

    final Collection<Relay.Bundle> read = relay.read(team, secret, Uuid.NULL, 1);
    assertTrue(read.size() == 1);

    // By the assertion above this loop should only execute once as there should only
    // be a single value in the collection.

    for (final Relay.Bundle bundle : read) {
      assertTrue(Uuid.equals(bundle.team(), team));
      assertTrue(Uuid.equals(bundle.user().id(), new Uuid(4)));
      assertTrue(Uuid.equals(bundle.conversation().id(), new Uuid(5)));
      assertTrue(Uuid.equals(bundle.message().id(), new Uuid(6)));
    }
  }

  @Test
  public void testReadFailWrongSecret() {

    final Server relay = new Server(8, 8);

    final Uuid team = new Uuid(3);
    final Secret secret = new Secret((byte)0x00, (byte)0x01, (byte)0x02);
    final Secret wrongSecret = new Secret((byte)0x00, (byte)0x01, (byte)0x00);

    assertTrue(relay.addTeam(team, secret));

    assertFalse(relay.write(team,
                            wrongSecret,
                            relay.pack(new Uuid(4), "User", Time.now()),
                            relay.pack(new Uuid(5), "Conversation", Time.now()),
                            relay.pack(new Uuid(6), "Hello World", Time.now())));
  }

  @Test
  public void testReadFailMissingTeam() {

    final Server relay = new Server(8, 8);

    final Uuid team = new Uuid(3);
    final Secret secret = new Secret((byte)0x00, (byte)0x01, (byte)0x02);

    assertTrue(relay.addTeam(team, secret));

    assertFalse(relay.write(new Uuid(33),
                            secret,
                            relay.pack(new Uuid(4), "User", Time.now()),
                            relay.pack(new Uuid(5), "Conversation", Time.now()),
                            relay.pack(new Uuid(6), "Hello World", Time.now())));
  }

  @Test
  public void testReadLimited() {

    final Server relay = new Server(8, 1);

    final Uuid team = new Uuid(3);
    final Secret secret = new Secret((byte)0x00, (byte)0x01, (byte)0x02);

    assertTrue(relay.addTeam(team, secret));

    assertTrue(relay.write(team,
                           secret,
                           relay.pack(new Uuid(4), "User", Time.now()),
                           relay.pack(new Uuid(5), "Conversation", Time.now()),
                           relay.pack(new Uuid(6), "Hello World", Time.now())));

    assertTrue(relay.write(team,
                           secret,
                           relay.pack(new Uuid(4), "User", Time.now()),
                           relay.pack(new Uuid(5), "Conversation", Time.now()),
                           relay.pack(new Uuid(7), "Hello World... again", Time.now())));


    final Collection<Relay.Bundle> read = relay.read(team, secret, Uuid.NULL, 2);
    assertTrue(read.size() == 1);
  }

  @Test
  public void testHistoryOverwrite() {

    final Server relay = new Server(1, 8);

    final Uuid team = new Uuid(3);
    final Secret secret = new Secret((byte)0x00, (byte)0x01, (byte)0x02);

    assertTrue(relay.addTeam(team, secret));

    assertTrue(relay.write(team,
                           secret,
                           relay.pack(new Uuid(4), "User", Time.now()),
                           relay.pack(new Uuid(5), "Conversation", Time.now()),
                           relay.pack(new Uuid(6), "Hello World", Time.now())));

    assertTrue(relay.write(team,
                           secret,
                           relay.pack(new Uuid(4), "User", Time.now()),
                           relay.pack(new Uuid(5), "Conversation", Time.now()),
                           relay.pack(new Uuid(7), "Hello World... again", Time.now())));

    final Collection<Relay.Bundle> read = relay.read(team, secret, Uuid.NULL, 2);
    assertTrue(read.size() == 1);

    // By the assertion above this loop should only execute once as there should only
    // be a single value in the collection.

    for (final Relay.Bundle bundle : read) {
      assertTrue(Uuid.equals(bundle.team(), team));
      assertTrue(Uuid.equals(bundle.user().id(), new Uuid(4)));
      assertTrue(Uuid.equals(bundle.conversation().id(), new Uuid(5)));
      assertTrue(Uuid.equals(bundle.message().id(), new Uuid(7)));
    }
  }

  @Test
  public void testReadWithMissingRoot() {

    final Server relay = new Server(8, 8);

    final Uuid team = new Uuid(3);
    final Secret secret = new Secret((byte)0x00, (byte)0x01, (byte)0x02);

    assertTrue(relay.addTeam(team, secret));

    assertTrue(relay.write(team,
                           secret,
                           relay.pack(new Uuid(4), "User", Time.now()),
                           relay.pack(new Uuid(5), "Conversation", Time.now()),
                           relay.pack(new Uuid(6), "Hello World", Time.now())));

    final Collection<Relay.Bundle> read = relay.read(team, secret, new Uuid(7), 1);
    assertTrue(read.size() == 1);

    // By the assertion above this loop should only execute once as there should only
    // be a single value in the collection.

    for (final Relay.Bundle bundle : read) {
      assertTrue(Uuid.equals(bundle.team(), team));
      assertTrue(Uuid.equals(bundle.user().id(), new Uuid(4)));
      assertTrue(Uuid.equals(bundle.conversation().id(), new Uuid(5)));
      assertTrue(Uuid.equals(bundle.message().id(), new Uuid(6)));
    }
  }

  @Test
  public void testReadMidHistory() {

    final Server relay = new Server(8, 8);

    final Uuid team = new Uuid(3);
    final Secret secret = new Secret((byte)0x00, (byte)0x01, (byte)0x02);

    assertTrue(relay.addTeam(team, secret));

    assertTrue(relay.write(team,
                           secret,
                           relay.pack(new Uuid(4), "User", Time.now()),
                           relay.pack(new Uuid(5), "Conversation", Time.now()),
                           relay.pack(new Uuid(6), "Hello World", Time.now())));

    assertTrue(relay.write(team,
                           secret,
                           relay.pack(new Uuid(7), "User", Time.now()),
                           relay.pack(new Uuid(8), "Conversation", Time.now()),
                           relay.pack(new Uuid(9), "Hello World", Time.now())));

    assertTrue(relay.write(team,
                           secret,
                           relay.pack(new Uuid(10), "User", Time.now()),
                           relay.pack(new Uuid(11), "Conversation", Time.now()),
                           relay.pack(new Uuid(12), "Hello World", Time.now())));

    final Collection<Relay.Bundle> read = relay.read(team, secret, new Uuid(2), 1);
    assertTrue(read.size() == 1);

    // By the assertion above this loop should only execute once as there should only
    // be a single value in the collection.

    for (final Relay.Bundle bundle : read) {

      // The relay server uses a linear id generator starting at 1 - so starting
      // bundle 2, the id should be 3.
      assertTrue(Uuid.equals(bundle.id(), new Uuid(3)));
    }
  }
}
