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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import codeu.chat.common.LinearUuidGenerator;
import codeu.chat.common.Relay;
import codeu.chat.common.Time;
import codeu.chat.common.Uuid;
import codeu.chat.common.Uuids;
import codeu.chat.util.Logger;

public final class Server implements Relay {

  private final static Logger.Log LOG = Logger.newLog(Server.class);

  private static final class Component implements Relay.Bundle.Component {

    private final Uuid id;
    private final String text;
    private final Time time;

    public Component(Uuid id, String text, Time time) {
      this.id = id;
      this.text = text;
      this.time = time;
    }

    @Override
    public Uuid id() { return id; }

    @Override
    public String text() { return text; }

    @Override
    public Time time() { return time; }

  }

  private static final class Bundle implements Relay.Bundle {

    private final Uuid id;
    private final Time time;
    private final Uuid team;
    private final Component user;
    private final Component conversation;
    private final Component message;
    //private final Component passwordHash;

    public Bundle(Uuid id,
                  Time time,
                  Uuid team,
                  Component user,
                  Component conversation,
                  Component message) {

      this.id = id;
      this.time = time;
      this.team = team;
      this.user = user;
      this.conversation = conversation;
      this.message = message;

    }

    @Override
    public Uuid id() { return id; }

    @Override
    public Time time() { return time; }

    @Override
    public Uuid team() { return team; }

    @Override
    public Component user() { return user; }

    @Override
    public Component conversation() { return conversation; }

    @Override
    public Component message() { return message; }

  }

  private final Queue<Relay.Bundle> history = new LinkedList<>();
  private final Map<Uuid, byte[]> teamSecrets = new HashMap<>();

  private final int maxHistory;
  private final int maxRead;

  // Okay, some reasoning behind why I'm using a statically initialized linear
  // generator for the ids for the relay server.
  //
  //   Point A : The ids only need to be uniqiue for a single run time of the
  //             relay. Ids from the relay are only used as a position into its
  //             history. If it repeats an id its not a problem.
  //
  //   Point B : The chance that the history would be so long that an id could
  //             be reused and appear along side's twin is way too small. The
  //             range for the ids is 1 to MAX INT (32 bit signed). This means
  //             that there would need to be 2147483646 messages in memory. If
  //             each message was 160 bytes long the relay server would need
  //             over 319 GB of ram.
  //
  // As a side note, the ids start at 1 and not 0 to avoid the first id from
  // matching the NULL id which is defined as (null, 0);

 private final Uuid.Generator idGenerator = new LinearUuidGenerator(null, 1, Integer.MAX_VALUE);

  // SERVER
  //
  // When initializing the server keep the following in mind.
  //   - Keep "maxHistory" small enough to avoid using too much memory.
  //   - Keep "maxRead" small enough to avoid any one client from connecting to
  //     the server for too long.
  public Server(int maxHistory, int maxRead) {
    this.maxHistory = Math.max(0, maxHistory);
    this.maxRead = Math.max(0, maxRead);
  }

  // ADD TEAM
  //
  // Let the relay know of a team's secret so that it will accept messages from that
  // team. If there is already a team entry, the secret will NOT be updated and the
  // call will return false.
  public boolean addTeam(Uuid id, byte[] secret) {

    LOG.info("Adding team to relay %s", id);

    final boolean open = teamSecrets.get(id) == null;

    if (open) {
      teamSecrets.put(id, secret);
    }

    LOG.info(open ?
             "Adding team was successful" :
             "Adding team failed - team id already exists");

    return open;
  }

  @Override
  public Relay.Bundle.Component pack(Uuid id, String text, Time time) {
    return new Component(id, text, time);
  }

  @Override
  public boolean write(Uuid teamId,
                       byte[] teamSecret,
                       Relay.Bundle.Component user,
                       Relay.Bundle.Component conversation,
                       Relay.Bundle.Component message) {

    if (authenticate(teamId, teamSecret)) {

      LOG.info(
          "Writing to server team=%s user=%s conversation=%s message=%s",
          teamId,
          user.id(),
          conversation.id(),
          message.id());

      if (history.size() >= maxHistory) {
         history.remove();
      }

      return history.offer(new Bundle(
          idGenerator.make(),
          Time.now(),
          teamId,
          user,
          conversation,
          message));
    } else {

      LOG.warning(
          "Unauthorized write attempt to server team=%s user=%s conversation=%s message=%s",
          teamId,
          user.id(),
          conversation.id(),
          message.id());

      return false;
    }
  }

  @Override
  public Collection<Relay.Bundle> read(Uuid teamId, byte[] teamSecret, Uuid root, int range) {

    final Collection<Relay.Bundle> found = new ArrayList<>();

    if (authenticate(teamId, teamSecret)) {

      int remaining = Math.min(range, maxRead);

      LOG.info(
         "Request to read from server requested=%d allowed=%d",
          range,
          maxRead);

      // Writing is a one way flag (once set it will not be unset) that switches
      // between looking for the starting message and writing all messages to the
      // output.
      boolean writing = root == Uuids.NULL || root == null;

      for (final Relay.Bundle message : history) {

        if (writing && remaining > 0) {
          found.add(message);
        }

        remaining = Math.max(remaining - 1, 0);

        // Only update "writing" after the check as the root is already known and
        // should not be included in the output.
        writing |= Uuids.equals(root, message.id());
      }

      LOG.info(
          "Read request complete requested=%d fullfilled=%d",
          range,
          found.size());

    } else {

      LOG.info(
          "Unauthroized attempt to read from server team=%s",
          teamId);
    }

    return found;
  }

  private boolean authenticate(Uuid id, byte[] secret) {
    return id != null && Arrays.equals(secret, teamSecrets.get(id));
  }
}
