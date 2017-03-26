package codeu.chat.client;

import codeu.chat.common.ConversationSummary;
import codeu.chat.common.Message;
import codeu.chat.common.NetworkCode;
import codeu.chat.util.Serializer;
import codeu.chat.util.Serializers;
import codeu.chat.util.connections.Connection;
import codeu.chat.util.connections.ConnectionSource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by rsharif on 3/23/17.
 */
public class BroadCastReceiver extends Thread{

    ConnectionSource mySource;
    OutputStream out;
    BroadcastEvent myResponse;
    boolean alive;
    InputStream in;


    // A broadcast event will be fired whenever a new broadcast is pushed to the client.
    @FunctionalInterface
    public interface BroadcastEvent { void onBroadcast(Message message); }

    public BroadCastReceiver(ConnectionSource mySource, BroadcastEvent broadcastEvent) {
        this.mySource = mySource;
        this.myResponse = broadcastEvent;
        this.alive = true;
    }


    @Override
    public void run() {

        try (
                final Connection myConnection = this.mySource.connect()
        ) {
            in = myConnection.in();
            out = myConnection.out();

            while (alive) {
                int type = Serializers.INTEGER.read(in);

                if (type == NetworkCode.NEW_BROADCAST) {
                    Message message = Message.SERIALIZER.read(in);
                    myResponse.onBroadcast(message);
                    // todo send a broadcast response to inform server that broadcast was received
                }

                else if(type == NetworkCode.JOIN_CONVERSATION_RESPONSE) {
                    System.out.println("Conversation response received");
                }

            }

        } catch (IOException exc) {
            System.out.println("Error connecting with broadcaster");
        }

    }

    public void joinConversation(ConversationSummary old, ConversationSummary newCon){

        try {

            Serializers.INTEGER.write(out, NetworkCode.JOIN_CONVERSATION_REQUEST);
            Serializers.nullable(ConversationSummary.SERIALIZER).write(out, old);
            Serializers.nullable(ConversationSummary.SERIALIZER).write(out, newCon);

        } catch (IOException exc) {
            System.out.println("Error in join conversation");
        }

    }

    public void exit() {
        alive = false;
        try {
            in.close();
        } catch (IOException exc){
            // todo error... there was an error closing the input stream
        }
    }


}
