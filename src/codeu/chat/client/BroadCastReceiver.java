package codeu.chat.client;

import codeu.chat.common.ConversationSummary;
import codeu.chat.common.Message;
import codeu.chat.common.NetworkCode;
import codeu.chat.util.Serializers;
import codeu.chat.util.connections.Connection;
import codeu.chat.util.connections.ConnectionSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by rsharif on 3/23/17.
 */
public class BroadCastReceiver extends Thread {

  private ConnectionSource source;
  private PrintWriter out;
  private BroadcastEvent response;
  private boolean alive;
  private BufferedReader in;
  private int lastType;
  private AtomicBoolean receivedResponse;

  // A broadcast event will be fired whenever a new broadcast is pushed to the client.
  @FunctionalInterface
  public interface BroadcastEvent {

    void onBroadcast(Message message);
  }

  public BroadCastReceiver(ConnectionSource source) {
    this.source = source;
    this.alive = true;
    this.receivedResponse = new AtomicBoolean(false);
  }


  @Override
  public void run() {

    try (
        final Connection myConnection = this.source.connect()
    ) {
      in = new BufferedReader(new InputStreamReader(myConnection.in()));
      out = new PrintWriter(myConnection.out(), true);

      while (alive) {

        if (!receivedResponse.get()) {

          int type = Serializers.INTEGER.read(in);

          if (type == NetworkCode.NEW_BROADCAST) {
            Message message = Message.SERIALIZER.read(in);
            if (response != null) {
              response.onBroadcast(message);
            }
            // todo send a broadcast response to inform server that broadcast was received
          } else if (type == NetworkCode.JOIN_CONVERSATION_RESPONSE) {
            receivedResponse.set(true);
          } else {
            this.lastType = type;
            receivedResponse.set(true);
            Thread.yield();
          }

        }
      }

    } catch (IOException exc) {
      if (alive) {
        System.out.println("Error connecting with broadcaster");
      }
    }

  }

  public void joinConversation(ConversationSummary old, ConversationSummary newCon) {

    Serializers.INTEGER.write(out, NetworkCode.JOIN_CONVERSATION_REQUEST);
    Serializers.nullable(ConversationSummary.SERIALIZER).write(out, old);
    Serializers.nullable(ConversationSummary.SERIALIZER).write(out, newCon);
    while (!this.receivedResponse.get()) {

    }
    this.receivedResponse.set(false);

  }

  public void exit() {
    alive = false;
    try {
      // todo: (Issue) -- the buffered reader isn't closing
      source.close();
      in.close();
    } catch (IOException exc) {
      // todo error... there was an error closing the input stream
    }
  }

  public void onBroadCast(BroadcastEvent broadcastEvent) {
    this.response = broadcastEvent;
  }


  public int getType() {

    while (!receivedResponse.get()) {
      // Thread.sleep() ??
    }
    return this.lastType;

  }

  public BufferedReader getInputStream() {

    while (!receivedResponse.get()) {
      // Thread.sleep() ??
    }
    return in;

  }

  public void responseProcessed() {
    receivedResponse.set(false);
  }

  public PrintWriter out() {
    return out;
  }

}