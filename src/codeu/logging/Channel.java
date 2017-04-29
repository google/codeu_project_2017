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

public interface Channel {

  // WRITE
  //
  // Write a single line out to this channel. As a channel may have muluple
  // lines pushed to it at one time, the lines should be considered continous
  // until flush is called.
  //
  void write(String line) throws IOException;

  // FLUSH
  //
  // Flush will be called after a group of lines have been written.
  //
  void flush() throws IOException ;

  // CLOSE
  //
  // This will be called when the channel is no longer used and should release
  // any underlying resources.
  //
  void close();
}
