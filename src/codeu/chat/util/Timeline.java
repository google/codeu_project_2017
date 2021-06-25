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

import codeu.chat.util.logging.Log;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A serialized collection of callback. The timeline manages its own thread no way to know outside
 * of the class when a submitted callback is executed.
 */
public final class Timeline {

  private final BlockingQueue<Runnable> pending = new LinkedBlockingQueue<>();

  private final Thread mWorker = new Thread(() -> {
    while (true) {
      try {
        var next = pending.take();
        next.run();
      } catch (InterruptedException ex) {
        // This should not be called since our thread is internal and we don't interrupt it.
        break;
      }
    }
  });

  public Timeline() {
    mWorker.start();
  }

  public void submit(Runnable callback) {
    while (!pending.offer(callback)) {
      Log.instance.warning("Failed to add to queue, trying again...");
    }
  }
}
