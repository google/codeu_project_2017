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

import java.util.Collection;

import codeu.chat.util.Time;
import codeu.chat.util.Uuid;

// RELAY
//
// This is the interface for communicating with the relay. Communication with the relay
// is limited to two simple interfaces (writes and reads). As the relay is not expected
// to track conversations or users, each bundle sent to the relay and received from the
// the relay must be atomic and contain all information about the user, conversation, and
// message.
public interface Relay {

  // BUNDLE
  //
  // The relay's representation of what a message looks like. As the relay is not
  // going to track users and conversations so all information about a message must
  // be in the bundle. This means that each bundle will have a copy of the author's
  // id and name, a copy of the conversation's id and title. All information about
  // a message must be present for a server to recreate the series of events with
  // as little reasoning as possible.
  interface Bundle {

    // COMPONENT
    //
    // As there is a lot of similar information in a bundle. Component groups together
    // common fields to make the bundle interface easier to read. As a bundle is made-up
    // of three parts (user, conversation, and message) and each parts have a uuid,
    // string, and time field it cluttered the interface. A commonent is just a wrapper
    // to make the Bundle interface easier to read.
    interface Component {

      // ID
      //
      // The id for the component. As just about every piece of data has an ID, this
      // id is the id for the piece of data that the component is pointing to.
      Uuid id();

      // TEXT
      //
      // The text for the component. As just about every piece of data has a string value,
      // this string is used to represent that string value. For a user it will be their
      // name, for a conversation it is the title, and for a message it is the content.
      String text();

      // TIME
      //
      // The time for the component. As just about every piece of data has a time value,
      // this time is used to represent that time value. For messages, users, and conversations
      // this is the creation time.
      Time time();

    }

    // ID
    //
    // The id for the bundle. This is the id of the bundle which can be used with read as the root.
    Uuid id();

    // ID
    //
    // The time for when the bundle was created on the relay. This is the time that the bundle was
    // created on the relay and not the time the message was created on the server.
    Time time();

    // TEAM
    //
    // This id of the team that owns this message. This is the id of the team that sent the bundle
    // to the relay.
    Uuid team();

    // USER
    //
    // All the information about the user who authored the message.
    Component user();

    // CONVERSATION
    //
    // All the infromation about the conversation that the message is part of.
    Component conversation();

    // MESSAGE
    //
    // All the information about the message that was sent from the server to
    // the relay.
    Component message();

  }

  // PACK
  //
  // Pack together a uuid, string, and time into a component. This is to make
  // the signature for "write" to be shorter and easier to read.
  Bundle.Component pack(Uuid id, String text, Time time);

  // WRITE
  //
  // Write a single message and all its extra data to the relay server. A message
  // must have information about the user and conversation that the message is
  // part of as the relay does not track users or conversations. In order to write
  // a message to the relay, a team must write their team id and team secret or
  // else the relay will reject the message.
  boolean write(Uuid teamId,
                Secret teamSecret,
                Bundle.Component user,
                Bundle.Component conversation,
                Bundle.Component message);

  // READ
  //
  // Read a series of bundles from the relay. Given a Uuid as the starting point
  // the relay will return up to but may return less than the range. The range must
  // be positive. Negative ranges are not allowed and will return an empty
  // collection. If the root is Uuids.NULL then the relay will start sending from
  // its earliest point. If the root is not found the relay will treat it as if it
  // was given Uuids.NULL.
  Collection<Bundle> read(Uuid teamId, Secret teamSecret, Uuid root, int range);

}
