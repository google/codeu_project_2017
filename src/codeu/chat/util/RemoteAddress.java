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

package codeu.chat.util;

public final class RemoteAddress {

  public final String host;
  public final int port;

  public RemoteAddress(String host, int port) {
    this.host = host;
    this.port = port;
  }

  @Override
  public String toString() { return String.format("%s@%d", host, port); }

  // TODO : add tests for this
  public static RemoteAddress parse(String string) {
    final String[] tokens = string.split("@");
    return new RemoteAddress(tokens[0], Integer.parseInt(tokens[1]));
  }
}
