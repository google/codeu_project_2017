package codeu.chat.server;

import codeu.chat.common.*;
import codeu.chat.util.Serializers;
import codeu.chat.util.connections.Connection;

import java.io.IOException;
import java.io.InputStream;
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

/*
todo check the synchronization for conflicts (synchronize linked lists??)
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


    private class ConnectionListener implements Runnable {

        private Connection myConnection;


        public ConnectionListener(Connection myConnection) {
            this.myConnection = myConnection;
        }

        @Override
        public void run() {

             // Connection listener will always listen to this connection until an exception
            // is given off

            try {

                while (handleCommand(myConnection));

            } catch (IOException exc) {
                System.out.println("IOException in BroadCast System");
            }
            System.out.println("*********************Thread Exiting *****************");
        }

    }



    public BroadCastSystem(){
        conversationUsers = new ConcurrentHashMap<>();
        messagesToBroadcast = new LinkedBlockingQueue<>();

        MessageBroadCaster messageBroadCaster = new MessageBroadCaster();

        broadCaster = new Thread(messageBroadCaster);

        broadCaster.start();
    }

    /**
     * Add the given connection to the conversation with the given uuid
     * @param connection the connection to be added
     * @param uuid the uuid of the conversation
     */
    private void addConnection(Connection connection, Uuid uuid){

        if (connection == null) throw new NullPointerException();
        List<Connection> recipients = conversationUsers.get(uuid.id());
        recipients.add(connection);

    }


    /** Remove the given connection from the conversation with the given uuid
     *
     * @param connection the connection to be removed
     * @param uuid the uuid of the conversation
     */
    private void removeConnection(Connection connection, Uuid uuid){

        if (connection == null) throw new NullPointerException();
        List<Connection> recipients = conversationUsers.get(uuid.id());
        recipients.remove(connection);

    }


    /**
     * Change the conversation of the given client connection.
     * @param connection the connection of the client which is changing conversations
     * @param oldCon the conversation summary of the clients current connection (the conversation they are leaving)
     * @param newCon the conversation summary of the conversation the client would like to connect to
     */
    public void switchConversation(Connection connection, ConversationSummary oldCon, ConversationSummary newCon){

        if (newCon == null && oldCon == null)
            throw new NullPointerException();
        if (oldCon != null)
            removeConnection(connection, oldCon.id);
        if (newCon != null)
            addConnection(connection, newCon.id);

    }


    public void broadCastMessage(ConversationMessageLink messageLink){


        int conversationId = messageLink.conversationUuid.id();
        Message message = messageLink.message;

        // using an iterator in order to remove connections if they return an exception
        // this is in case the client has disconnected for whatever reason

        System.out.println("Testing list at uid + " + conversationId + " : " + conversationUsers.get(conversationId));
        Iterator<Connection> myIterator = conversationUsers.get(conversationId).iterator();

        while (myIterator.hasNext()) {
            Connection connection = myIterator.next();

            /*
            The connection is synchronized to make sure that messages sent to client are sent in their
            bundle. That is, without synchronization client may get the following if they send a new message request
            at the same time as a broadcast is going out:
                    >> NEW_BROADCAST
                    >> NEW_MESSAGE_RESPONSE
                    >> (message from broadcast)
                    >> (message from message response)

            In that example, client cannot tell which message is for which network code

            **** This issue may also be resolved if the serializers use a bufferedReader instead
            */

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


    public void handleConnection(Connection connection) {

        ConnectionListener connectionListener = new ConnectionListener(connection);
        Thread connectionThread = new Thread(connectionListener);
        connectionThread.start();

    }

    public boolean handleCommand(Connection connection) throws IOException{
        InputStream in = connection.in();
        OutputStream out = connection.out();


        int type = Serializers.INTEGER.read(in);

        // When the type is -1, the client has closed the connection.
        if (type == -1) {
            return false;
        }

        if (type == NetworkCode.JOIN_CONVERSATION_REQUEST) {

            System.out.println("Conversation request received");

            ConversationSummary old = Serializers.nullable(ConversationSummary.SERIALIZER).read(in);
            ConversationSummary newCon = Serializers.nullable(ConversationSummary.SERIALIZER).read(in);

            switchConversation(connection,old,newCon);

            Serializers.INTEGER.write(out,NetworkCode.JOIN_CONVERSATION_RESPONSE);

            System.out.println("conversation response sent");
        }

        return true;

    }






}
