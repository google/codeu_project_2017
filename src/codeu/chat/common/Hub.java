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

package codeu.chat.common;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import codeu.chat.util.connections.Connection;
import codeu.chat.util.connections.ConnectionSource;

// HUB
//
// A hub allows opening connections with a connection source and using each
// connection to be safely run on different threads. This allow servers
// to connect to more clients at one time but still serve them one-at-a-time.
public final class Hub implements Runnable {

  // HANDLER
  //
  // The interface to define what work the HUB should do per connection. When
  // a connection is availble, "handle" will be used. Exceptions do not need
  // to be handled by "handle". If there is an exception during execution,
  // "onException" will be called. Each connection should be considered
  // independent.
  public interface Handler {

    // HANDLE
    //
    // Takes the given connection and completes all work for that connection
    // independent of any past or future connections.
    void handle(Connection connection) throws Exception;

    // ON EXCEPTION
    //
    // When "handle" throws an exception, "onException" is called to that the
    // exception is passed up to the main logic layer.
    void onException(Exception ex);

  }

  // PROCESSOR
  //
  // The interface for handling a single action.
  //
  private interface Processor {

    // PROCESS
    //
    // Perform a single pass of processing. Returns true if it can handle another
    // round of processing. Returns false if it cannot do any more work.
    boolean process();

  }

  private final BlockingQueue<Connection> connections = new LinkedBlockingQueue<>();

  private final ConnectionSource source;
  private final Handler handler;

  private final Processor createConnection = new Processor() {

    @Override
    public boolean process() {
      try {
        connections.put(source.connect());
        return true;
      } catch (InterruptedException ex) {
        return false;
      } catch (Exception ex) {
        return true;
      }
    }
  };

  private final Processor handleConnection = new Processor() {
    @Override
    public boolean process() {
      try {

        final Connection connection = connections.take();
        handler.handle(connection);
        connection.close();
        return true;

      } catch (InterruptedException ex) {

        return false;

      } catch (Exception ex) {

        handler.onException(ex);
        return true;

      }
    }
  };

  private final Thread connectionThread = threadFromProcessor(createConnection);
  private final Thread handlerThread = threadFromProcessor(handleConnection);

  public Hub(ConnectionSource source, Handler handler) {

    this.source = source;
    this.handler = handler;

  }

  public void run() {

    connectionThread.start();
    handlerThread.start();

    forceJoin(connectionThread);
    forceJoin(handlerThread);

  }

  private static Thread threadFromProcessor(final Processor processor) {
    return new Thread() {
      @Override
      public void run() { while (processor.process()) { } }
    };
  }

  private static void forceJoin(Thread thread) {
    while (true) {
      try {
        thread.join();
        break;
      } catch (InterruptedException ex) {
        continue;
      }
    }
  }
}
