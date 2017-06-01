package codeu.chat.client;

import codeu.chat.common.ConversationSummary;
import codeu.chat.common.Message;
import codeu.chat.common.NetworkCode;
import codeu.chat.util.Serializers;
import codeu.chat.util.Uuid;
import codeu.chat.util.connections.Connection;
import codeu.chat.util.connections.ConnectionSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
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
  private List<Message> storedMessages;
  private ConversationSummary currentCon;

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
            Uuid conversationUuid = Uuid.SERIALIZER.read(in);
            Message message = Message.SERIALIZER.read(in);
            if (currentCon.id.equals(conversationUuid)) {
              if (response != null) {
                response.onBroadcast(message);
              }
              if (storedMessages != null) {
                storedMessages.add(message);
              }
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
    } finally {
      try {
        in.close();
      } catch (IOException exc) {
        System.out.println("Error closing");
      }
    }

  }

  public void joinConversation(ConversationSummary newCon) {

    Serializers.INTEGER.write(out, NetworkCode.JOIN_CONVERSATION_REQUEST);
    Serializers.nullable(ConversationSummary.SERIALIZER).write(out, currentCon);
    Serializers.nullable(ConversationSummary.SERIALIZER).write(out, newCon);
    while (!this.receivedResponse.get()) {

    }
    currentCon = newCon;
    this.receivedResponse.set(false);

  }

  public void exit() {
    alive = false;
    out.close();
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

  public void setMessages(List<Message> list) {
    storedMessages = list;
  }

}