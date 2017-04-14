package codeu.chat.client;

import codeu.chat.common.ConversationSummary;
import codeu.chat.common.Message;
import codeu.chat.common.NetworkCode;
import codeu.chat.util.Serializers;
import codeu.chat.util.connections.Connection;
import codeu.chat.util.connections.ConnectionSource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by rsharif on 3/23/17.
 */
public class BroadCastReceiver extends Thread{

    private ConnectionSource mySource;
    private OutputStream out;
    private BroadcastEvent myResponse;
    private boolean alive;
    private InputStream in;
    private int lastType;
    private AtomicBoolean receivedResponse;

    // A broadcast event will be fired whenever a new broadcast is pushed to the client.
    @FunctionalInterface
    public interface BroadcastEvent { void onBroadcast(Message message); }

    public BroadCastReceiver(ConnectionSource mySource) {
        this.mySource = mySource;
        this.alive = true;
        this.receivedResponse = new AtomicBoolean(false);
    }


    @Override
    public void run() {

        try (
                final Connection myConnection = this.mySource.connect()
        ) {
            in = myConnection.in();
            out = myConnection.out();

            while (alive) {


                if (!receivedResponse.get()) {

                    int type = Serializers.INTEGER.read(in);

                    if (type == NetworkCode.NEW_BROADCAST) {
                        Message message = Message.SERIALIZER.read(in);
                        if (myResponse != null) myResponse.onBroadcast(message);
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
            if(alive) System.out.println("Error connecting with broadcaster");
        }

    }

    public void joinConversation(ConversationSummary old, ConversationSummary newCon){

        try {

            Serializers.INTEGER.write(out, NetworkCode.JOIN_CONVERSATION_REQUEST);
            Serializers.nullable(ConversationSummary.SERIALIZER).write(out, old);
            Serializers.nullable(ConversationSummary.SERIALIZER).write(out, newCon);
            while (!this.receivedResponse.get()) ;
            this.receivedResponse.set(false);
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

    public void onBroadCast(BroadcastEvent broadcastEvent) {
        this.myResponse = broadcastEvent;
    }


    public int getType() {

        while (!receivedResponse.get());
        return this.lastType;

    }

    public InputStream getInputStream() {

        while (!receivedResponse.get());
        return in;

    }

    public void responseProcessed() {
        receivedResponse.set(false);
    }

    public OutputStream out() {
        return out;
    }

}
