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

  private final static Logger.Log LOG = Logger.newLog(Timeline.class);

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
  // separated to allow this thread to be safely interrupted.
  private final Thread scheduler = new Thread() {
    @Override
    public void run() {
      while (running) {

        Event next;

        try {
          next = backlog.take();
        } catch (InterruptedException ex) {
          // Rather than try to handle the exception here, set "next"
          // to null and let the normal flow handle the case.
          next = null;
        }

        long sleep = 0;

        if (next != null) {

          final long now = System.currentTimeMillis();

          // Check which queue the event should be added to. If it
          // is time to execute, it should be added to the "todo"
          // queue. If it is not time, it should be added back to the
          // "backlog".
          // If the item is added back to the backlog, we know how long
          // it will be until it will be executed. That means we can sleep
          // until then.
          if (next.time <= now) {
            forceAdd(todo, next.callback);
            sleep = 0;
          } else {
            // Put it back (it's not time).
            forceAdd(backlog, next);
            sleep = next.time - now;
          }
        }

        if (sleep > 0) {
          try {
            Thread.sleep(sleep);
          } catch (InterruptedException ex) {
            // There are two cases this will happen:
            //  1. A new item was added and we are being woken to
            //     check if we need to update the time.
            //  2. It is time to exit and we need to wake-up so that
            //     we can check that "running" is "false".
          }
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
          // Catch all exceptions here to stop any rogue action from
          // take down the timeline.
          LOG.warning(
              "An exception was seen on the timeline (%s)",
              ex.toString());
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
    forceAdd(backlog, event);
    scheduler.interrupt();  // wake it up
  }

  // STOP
  //
  // Tell the timeline to shutdown. This is a non-blocking call.
  public void stop() {
    running = false;

    // Interrupt does not force a thread to exit. It signals the
    // thead that it is time to stop execution. As the threads may
    // be sleeping, this will force them awake.
    executor.interrupt();
    scheduler.interrupt();
  }

  // JOIN
  //
  // Wait for the timeline to shutdown. This is a blocking call.
  public void join() {
    forceJoin(executor);
    forceJoin(scheduler);
  }

  private static void forceJoin(Thread thread) {
    while (true) {
      try {
        thread.join();
        break;
      } catch (InterruptedException ex) {
        // Do nothing - allow this to try again.
      }
    }
  }

  private static <T> void forceAdd(BlockingQueue<T> queue, T value) {
    while (!queue.offer(value)) {
      LOG.warning("Failed to add to queue, trying again...");
    }
  }
}
