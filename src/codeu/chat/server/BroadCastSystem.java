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


    private ConcurrentHashMap<Integer, List<Connection>> conversationUsers;
    private final BlockingQueue<Message> messagesToBroadcast;
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

            // todo this will constantly listen to it's connection

            // handle commands "join conversation" , "new broadcast"



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


    public void broadCastMessage(Message message){

        Uuid conversationId = message.id;

        // using an iterator in order to remove connections if they return an exception
        // this is in case the client has disconnected for whatever reason
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
    public void addMessage(Message message){
        if (message == null) throw new NullPointerException("Message cannot be null");
        messagesToBroadcast.add(message);
    }


    public void handleConnection(Connection connection) {
        // todo this will create a new thread that listens to this connection

        ConnectionListener connectionListener = new ConnectionListener(connection);
        Thread connectionThread = new Thread(connectionListener);
        connectionThread.start();

    }

    public void handleCommand(Connection connection) throws IOException{
        InputStream in = connection.in();
        OutputStream out = connection.out();


        int type = Serializers.INTEGER.read(in);

        if (type == NetworkCode.JOIN_CONVERSATION_REQUEST) {

            // todo call switch conversation for the connection
            // Get the conversation summaries for the old and new conversation


            Serializers.INTEGER.write(out,NetworkCode.JOIN_CONVERSATION_RESPONSE);
        }

    }






}
