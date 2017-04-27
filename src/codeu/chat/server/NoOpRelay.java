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

import java.util.ArrayList;
import java.util.Collection;

import codeu.chat.common.Relay;
import codeu.chat.common.Secret;
import codeu.chat.util.Time;
import codeu.chat.util.Uuid;

// NO OP RELAY
//
// The no op relay is an empty implementation of the relay interface
// that will always return positive values for calls so that a server
// can use this relay when other relays are not available.
public final class NoOpRelay implements Relay {

  @Override
  public Relay.Bundle.Component pack(final Uuid id,
                                     final String text,
                                     final Time time) {

    return new Relay.Bundle.Component() {
      @Override
      public Uuid id() { return id; }

      @Override
      public Time time() { return time; }

      @Override
      public String text() { return text; }
    };
  }

  @Override
  public boolean write(Uuid teamId,
                       Secret teamSecret,
                       Relay.Bundle.Component user,
                       Relay.Bundle.Component conversation,
                       Relay.Bundle.Component message) {

    return true;
  }

  @Override
  public Collection<Relay.Bundle> read(Uuid teamId,
                                       Secret teamSecret,
                                       Uuid root,
                                       int range) {

    return new ArrayList<Relay.Bundle>();
  }
}
