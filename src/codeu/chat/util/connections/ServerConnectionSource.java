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

package codeu.chat.util.connections;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

// SERVER CONNECTION SOURCE
//
// Implements the ConnectionSource interface for servers who need to host
// one of their ports so that clients can connect to it. Calls to "connect"
// will block until a connection is established.
public final class ServerConnectionSource implements ConnectionSource {

  private final ServerSocket serverSocket;

  private ServerConnectionSource(ServerSocket serverSocket) {
    this.serverSocket = serverSocket;
  }

  @Override
  public Connection connect() throws IOException {
    return fromSocket(serverSocket.accept());
  }

  @Override
  public void close() throws IOException {
    serverSocket.close();
  }

  private static Connection fromSocket(final Socket socket) throws IOException {

    return new Connection() {

      @Override
      public InputStream in() throws IOException {
        return socket.getInputStream();
      }

      @Override
      public OutputStream out() throws IOException {
        return socket.getOutputStream();
      }

      @Override
      public void close() throws IOException {
        socket.close();
      }
    };
  }

  public static ConnectionSource forPort(int port) throws IOException {
    return new ServerConnectionSource(new ServerSocket(port));
  }
}
