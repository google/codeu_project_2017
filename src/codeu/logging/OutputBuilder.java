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

package codeu.logging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

final class OutputBuilder {

  // There are two outputs. This is so that one can be used to iterate over
  // while the other one is used to hold the outputs that should be kept. An
  // output will be dropped if there is an exception writing to it.
  private final Collection<Channel> sourceChannels = new ArrayList<>();
  private final Collection<Channel> remainingChannels = new ArrayList<>();

  // These are the lines that need to be written output. Normally I would use a queue
  // for this but as I want the logger to be lighter-weight, I will use an array list
  // so there are fewer heap calls.
  private final Collection<String> lines = new ArrayList<>();

  public void register(Channel channel) {
    sourceChannels.add(channel);
  }

  public void append(String line) {
    lines.add(line);
  }

  public void push() {

    // Clear the outputB list so that it can be repopulated based on
    // which outputs successfully wrote out.
    remainingChannels.clear();

    for (final Channel out : sourceChannels) {
      try {
        for (final String line : lines) {
          out.write(line);
        }
        out.flush();
        remainingChannels.add(out);
      } catch (IOException ex) {
        // Do nothing - the output would not be added to remainingChannels
        // and will therefore be removed from the rotation.
        out.close();
      }
    }

    // All the output has been pushed to each channel - so now the
    // output should be cleared to avoid double outputs.
    lines.clear();

    // reset sourceChannels to match remainingChannels
    sourceChannels.clear();
    sourceChannels.addAll(remainingChannels);
  }
}
