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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

// TIMELINE
//
// The timeline is a time ordered collection of executable units. This is used
// when work needs to be ordered by time. The timeline manages its own threads
// and there is no way to know outside of the code that is executed when the
// code has been executed.
public final class Timeline {

  private static final class Event implements Comparable<Event> {

    public final long time;
    public final Runnable callback;

    public Event(long time, Runnable callback) {
      this.time = time;
      this.callback = callback;
    }

    @Override
    public int compareTo(Event other) {
      return Long.compare(time, other.time);
    }
  }

  private final BlockingQueue<Event> backlog = new PriorityBlockingQueue<>();
  private final BlockingQueue<Runnable> todo = new LinkedBlockingQueue<>();

  private boolean running = true;

  // This thread is used to track the time of events and moves events from the
  // "backlog" queue to the "todo" queue when it is time to execute. They are
  // seperated to allow this thread to be safely interrupted.
  private final Thread scheduler = new Thread() {
    @Override
    public void run() {
      while (running) {
        final long now = System.currentTimeMillis();
        final Event next = backlog.poll();
        try {
          if (next == null) {
            Thread.sleep(10000);
          } else if (next.time > now) {
            // Put it back (it's not time).
            while (!backlog.offer(next)) {
              // force this to go through
            }
            Thread.sleep(next.time - now);
          } else {
            while (!todo.offer(next.callback)) {
              // force this to go through
            }
          }
        } catch (InterruptedException ex) {
          // A new event was added - need to recheck all the times.
        }
      }
    }
  };

  // This thread is used to run the code that was given to the time line. This
  // worker does not need to know anything about the time. Once an event gets to
  // here - it is considered "on time" and will be executed.
  private final Thread executor = new Thread() {
    @Override
    public void run() {
      while (running) {
        try {
          todo.take().run();
        } catch (Exception ex) {
          // Catch all exceptions here to stop any rouge action from
          // take down the timeline.
        }
      }
    }
  };

  public Timeline() {
    scheduler.start();
    executor.start();
  }

  // SCHEDULE NOW
  //
  // Add an event to the timeline so that it will occur as soon as possible.
  public void scheduleNow(Runnable callback) {
    scheduleAt(System.currentTimeMillis(), callback);
  }

  // SCHEDULE IN
  //
  // Add an event to the timeline so that it will occur in approximately in a
  // set amount of milliseconds.
  public void scheduleIn(long ms, Runnable callback) {
    scheduleAt(System.currentTimeMillis() + ms, callback);
  }

  // SCHEDULE AT
  //
  // Add an event to the timeline so that will occur approximately at a fixed
  // point in time.
  public void scheduleAt(long timeMs, Runnable callback) {
    final Event event = new Event(timeMs, callback);
    while (!backlog.offer(event)) {
      // force add
    }
    scheduler.interrupt();  // wake it up
  }

  public void stop() {
    running = false;
    forceStop(executor);
    forceStop(scheduler);
  }

  private static void forceStop(Thread thread) {
    while (true) {
      try {
        thread.interrupt();
        thread.join();
        break;
      } catch (InterruptedException ex) {
        // Do nothing - allow this to try again.
      }
    }
  }
}
