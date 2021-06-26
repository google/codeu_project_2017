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

package codeu.chat.server.endpoints;

import java.util.HashMap;
import java.util.Map;

/**
 * Act as a central registry of end-points, allowing a new end-point to be created and used without
 * needing to update anything using the end-points.
 *
 * WARNING: Using this pattern will make the code analysis think that the end-points are not
 * actually being used. However, they are used if they are calling |EndPointRegistry.instance.register()|.
 */
public final class EndPointRegistry {

  public static EndPointRegistry instance = new EndPointRegistry();

  private final Map<Integer, EndPoint> mEndPoints = new HashMap<>();

  private EndPointRegistry() {
    // Do not allow additional instances other than |instance|.

    register(new GetAllConversationsEndPoint());
    register(new GetAllUsersEndPoint());
    register(new GetConversationsByIdEndPoint());
    register(new GetMessagesByIdEndPoint());
    register(new GetUserByIdEndPoint());
    register(new NewConversationEndPoint());
    register(new NewMessageEndPoint());
    register(new NewUserEndPoint());
  }

  private void register(EndPoint endPoint) {
    mEndPoints.put(endPoint.getRequestCode(), endPoint);
  }

  /**
   * Get all currently registered end-points.
   */
  public Iterable<EndPoint> getAll() {
    return mEndPoints.values();
  }
}
