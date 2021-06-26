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

import codeu.chat.server.Model;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * An endpoint takes a request and generates a response.
 */
public abstract class EndPoint {

  private final int mRequestCode;
  private final int mResponseCode;

  public EndPoint(int requestCode, int responseCode) {
    mRequestCode = requestCode;
    mResponseCode = responseCode;
  }

  /**
   * Gets the request code that the end-point is responsible for responding it.
   */
  public final int getRequestCode() {
    return mRequestCode;
  }

  /**
   * Gets the response code that the end-point will use in its response.
   */
  public final int getResponseCode() {
    return mResponseCode;
  }

  /**
   * Handles the incoming request. The request code will have already been read from the input
   * stream. The endpoint is still responsible for writing the response code.
   */
  public abstract void handleConnection(
      Model model, InputStream in, OutputStream out) throws IOException;
}
