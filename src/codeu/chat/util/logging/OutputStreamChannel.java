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

package codeu.chat.util.logging;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import codeu.logging.Channel;

final class OutputStreamChannel implements Channel {

  private final Writer out;

  public OutputStreamChannel(OutputStream out) {
    this.out = new OutputStreamWriter(out);
  }

  @Override
  public void write(String line) throws IOException {
    out.write(line);
    out.write("\n");
  }

  @Override
  public void flush() throws IOException {
    out.flush();
  }

  @Override
  public void close() {
    try {
      out.close();
    } catch (Exception ex) {
      // Something went wrong closing the writer but it really does not
      // mater because this object is going away now.
    }
  }
}
