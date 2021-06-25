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

package codeu.chat.client;

import java.util.UUID;

public final class Context {

  private UUID mUser = null;
  private UUID mConversation = null;

  public UUID user() {
    return mUser;
  }

  public UUID conversation() {
    return mConversation;
  }

  public void changeUser(UUID id) {
    // When changing users, it means that our current conversation and message are no longer value.
    mUser = id;
    mConversation = null;
  }

  public void changeConversation(UUID id) {
    // When changing conversations, it means that our current message is no longer value.
    mConversation = id;
  }
}
