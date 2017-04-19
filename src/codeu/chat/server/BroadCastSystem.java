package codeu.chat.server;

import codeu.chat.common.*;
import codeu.chat.util.Serializers;
import codeu.chat.util.connections.Connection;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by rsharif on 3/17/17.
 */


public class BroadCastSystem {

    private class ConversationMessageLink {

        Uuid conversationUuid;
        Message message;

        ConversationMessageLink(Uuid conversationUuid, Message message) {
            this.conversationUuid = conversationUuid;
            this.message = message;
        }

    }

    private ConcurrentHashMap<Integer, List<Connection>> conversationUsers;
    private final BlockingQueue<ConversationMessageLink> messagesToBroadcast;
    private final Thread broadCaster;

    private class MessageBroadCaster implements Runnable {
        @Override
        public void run() {

            try {
                while (true) {
                    broadCastMessage(messagesToBroadcast.take());
                }
            } catch (InterruptedException exc) {
                System.out.println("Error broadcasting message");
            }

        }
    }

    public BroadCastSystem(){
        conversationUsers = new ConcurrentHashMap<>();
        messagesToBroadcast = new LinkedBlockingQueue<>();

        MessageBroadCaster messageBroadCaster = new MessageBroadCaster();

        broadCaster = new Thread(messageBroadCaster);

        broadCaster.start();
    }

    private void addConnection(Connection connection, Uuid uuid){

        if (connection == null) throw new NullPointerException();
        List<Connection> recipients = conversationUsers.get(uuid.id());
        recipients.add(connection);

    }

    private void removeConnection(Connection connection, Uuid uuid){

        if (connection == null) throw new NullPointerException();
        List<Connection> recipients = conversationUsers.get(uuid.id());
        recipients.remove(connection);

    }

    public void switchConversation(Connection connection, ConversationSummary oldCon, ConversationSummary newCon){

        if (newCon == null && oldCon == null)
            throw new NullPointerException();
        if (oldCon != null)
            removeConnection(connection, oldCon.id);
        if (newCon != null)
            addConnection(connection, newCon.id);

    }

    private void broadCastMessage(ConversationMessageLink messageLink){


        int conversationId = messageLink.conversationUuid.id();
        Message message = messageLink.message;

        // using an iterator in order to remove connections if they return an exception
        // this is in case the client has disconnected for whatever reason

        Iterator<Connection> myIterator = conversationUsers.get(conversationId).iterator();

        while (myIterator.hasNext()) {
            Connection connection = myIterator.next();

            try {

                OutputStream out = connection.out();

                synchronized (out) {
                    Serializers.INTEGER.write(out, NetworkCode.NEW_BROADCAST);
                    Message.SERIALIZER.write(out, message);
                }

            } catch (Exception ex) {
                try {
                    connection.close();
                } catch (IOException ioexc) {
                    System.out.println("Error closing socket");
                }
                myIterator.remove();
            }

        }

    }

    public void addConversation(ConversationSummary conversationSummary){

        if (conversationSummary == null) throw new NullPointerException("Cannot add null to conversations");
        if (conversationSummary.id == null) throw new NullPointerException("Null ID in conversation summary");

        int uid = conversationSummary.id.id();

        if (conversationUsers.contains(uid)) throw new IllegalArgumentException("Conversation already exists");

        conversationUsers.put(uid, Collections.synchronizedList(new LinkedList<>()));

    }

    // adds the given message to the list of messages that need to be broadcasted
    public void addMessage(Uuid conversationUuid, Message message){
        if (message == null) throw new NullPointerException("Message cannot be null");
        messagesToBroadcast.add(new ConversationMessageLink(conversationUuid, message));
    }

}
