package codeu.chat.common;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.lang.InterruptedException;

import codeu.chat.util.Logger;
import codeu.chat.util.Uuid;

import com.google.firebase.*;
import com.google.firebase.database.*;
import com.google.firebase.auth.*;


public class PersistanceController{
    
    public static final String firebaseDatabaseUrl = "https://codeu-chat-database.firebaseio.com";
    private final static Logger.Log LOG = Logger.newLog(PersistanceController.class);
    private FirebaseDatabase firebaseDatabase;
    private String serverIdStr;

    public PersistanceController(String persistancePath, Uuid serverId, String firebaseKeyPath) {
        serverIdStr = Integer.toString(serverId.id());
        try {

            // Open Firebase Keys
            FileInputStream serviceAccount =
                new FileInputStream(persistancePath + firebaseKeyPath);

            // Set Firebase Credentials
            FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
                .setDatabaseUrl(firebaseDatabaseUrl)
                .build();

            // Firebase Initialization
            FirebaseApp.initializeApp(options);
            firebaseDatabase = FirebaseDatabase.getInstance();

            // Create test connection database reference
            final DatabaseReference testConnectionReference = firebaseDatabase.getReference("group");
            testConnectionReference.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String groupName = dataSnapshot.getValue(String.class);
                    LOG.info("Connected to the database %s with teamID %s",groupName,serverIdStr);
                }
                @Override
                public void onCancelled(DatabaseError error) {
                    LOG.error(error.getMessage());
                }

            });

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public List<Conversation> getAllConversations() {

        // Create semaphore for synchronized download
        final Semaphore semaphore = new Semaphore(0);

        List<Conversation> conversationsList = new ArrayList<Conversation>();

        // Create converstaions database reference
        final DatabaseReference conversationsReference = firebaseDatabase.getReference(serverIdStr)
            .child("conversations");
        conversationsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Conversation conversation = postSnapshot.getValue(Conversation.class);
                    conversationsList.add(conversation);
                }
                semaphore.release();
            }
            @Override
            public void onCancelled(DatabaseError error){
                LOG.error("Firebase Error: " + error.getMessage());
                semaphore.release();
            }
        });
        try {
            semaphore.acquire();
            LOG.info("Loaded %s conversations from firebase",conversationsList.size());
        } catch (InterruptedException error){
            LOG.error("Semaphore Error: " + error.getMessage());
        }
        return conversationsList;
    }
    public List<Message> getAllMessages() {
        final Semaphore semaphore = new Semaphore(0);
        final DatabaseReference messagesReference = firebaseDatabase.getReference(serverIdStr).child("messages");
        List<Message> list = new ArrayList<Message>();
        messagesReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Message message = postSnapshot.getValue(Message.class);
                    list.add(message);
                }
                semaphore.release();
            }
            @Override
            public void onCancelled(DatabaseError error){
                LOG.error("Firebase Error: " + error.getMessage());
                semaphore.release();
            }
        });
        try {
            semaphore.acquire();
            LOG.info("Loaded %s messages from firebase",list.size());
        } catch (InterruptedException error){
            LOG.error("Semaphore Error: " + error.getMessage());
        }
        return list;
    }
    public List<User> getAllUsers() {
        final Semaphore semaphore = new Semaphore(0);
        final DatabaseReference usersReference = firebaseDatabase.getReference(serverIdStr).child("users");
        List<User> list = new ArrayList<User>();
        usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    User user = postSnapshot.getValue(User.class);
                    list.add(user);
                }
                semaphore.release();
            }
            @Override
            public void onCancelled(DatabaseError error){
                LOG.error("Firebase Error: " + error.getMessage());
                semaphore.release();
            }
        });
        try {
            semaphore.acquire();
            LOG.info("Loaded %s users from firebase",list.size());
        } catch (InterruptedException error){
            LOG.error("Semaphore Error: " + error.getMessage());
        }
        return list;
    }
    public Boolean addConversation(Conversation conversation){
        final DatabaseReference conversationReference = firebaseDatabase.getReference(serverIdStr).child("conversations");
        conversationReference.child(Integer.toString(conversation.id.id())).setValue(conversation);
        return true;
    }
    public Boolean addMessage(Message message, Conversation conversation){
        final DatabaseReference messagesReference = firebaseDatabase.getReference(serverIdStr).child("messages");
        messagesReference.child(Integer.toString(message.id.id())).setValue(message);
        final DatabaseReference conversationsReference = firebaseDatabase.getReference(serverIdStr).child("conversations");
        conversationsReference.child(Integer.toString(conversation.id.id())).setValue(conversation);
        return true;
    }
    public Boolean addUser(User user){
        final DatabaseReference userReference = firebaseDatabase.getReference(serverIdStr).child("users");
        userReference.child(Integer.toString(user.id.id())).setValue(user);
        return true;
    }
}
