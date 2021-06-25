// Copyright 2021 Google LLC.
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

import java.util.UUID;

public abstract class ServerController {

  private final Model mModel;

  public ServerController(Model model) {
    this.mModel = model;
  }

  protected Model model() {
    return mModel;
  }

  protected UUID randomUUID() {
    var candidate = UUID.randomUUID();

    while (isIdInUse(candidate)) {
      // Assuming that "randomUuid" is actually well implemented, this loop should never be needed,
      // but just in case make sure that the Uuid is not actually in use before returning it.
      candidate = UUID.randomUUID();
    }

    return candidate;
  }

  private boolean isIdInUse(UUID id) {
    return mModel.conversations().containsKey(id) ||
        mModel.messages().containsKey(id) ||
        mModel.users().containsKey(id);
  }
}
