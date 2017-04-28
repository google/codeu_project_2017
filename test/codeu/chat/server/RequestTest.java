/**
 * Tests for HTTP requests.
 *
 * @author  Nick Petosa
 */
package codeu.chat.server;

import static org.junit.Assert.*;

import codeu.chat.common.*;
import codeu.chat.util.connections.ConnectionSource;
import codeu.chat.util.connections.ServerConnectionSource;
import com.google.gson.*;
import okhttp3.*;
import okhttp3.Request;
import org.junit.*;

import codeu.chat.util.Uuid;
import org.junit.runners.MethodSorters;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests for the REST API interface. The tests run in order as specified by the first number of their methods.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public final class RequestTest {

    private int teamNumber = 16;


    /**
     * Private inner class that creates a mini on-demand server.
     */
    private class Servlet implements Runnable {

        Server servlet;
        ConnectionSource serverSource;

        public Servlet(int port) throws IOException {
            servlet = new Server(Uuid.parse(String.valueOf(teamNumber)), Secret.parse("16"), new NoOpRelay());
            serverSource = ServerConnectionSource.forPort(port);
        }

        public Server getServer() {
            return servlet;
        }

        @Override
        public void run() {
            try {
                servlet.handleConnection(serverSource.connect());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Private inner class that simulates a request submission.
     */
    private class Submit implements Runnable {

        private Request request;
        private OkHttpClient client;
        private String result;

        public Submit(Request request) {
            client = new OkHttpClient();
            this.request = request;
            result = null;
        }

        public String getResult() {
            return result;
        }

        @Override
        public void run() {
            try {
                Response response = client.newCall(request).execute();
                result = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private static Servlet servlet;
    private static List<String> users;
    private static List<String> conversations;
    private static List<String> messages;


    private static boolean setUpIsDone = false;
    @Before
    public void setUp() throws IOException {
        if (setUpIsDone) {
            return;
        }
        servlet = new Servlet(8000);
        users = new ArrayList<>();
        conversations = new ArrayList<>();
        messages = new ArrayList<>();
        setUpIsDone = true;
    }

    /**
     * Tests whether a list of 3 usernames can successfully be registered and become User objects server side.
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void _1_testAddUser() throws IOException, InterruptedException {

        String[] usernames = {"George P. Burdell", "Satya Nadella", "Johnny Ives"};

        for (String username : usernames) {
            MediaType mediaType = MediaType.parse("application/octet-stream");
            RequestBody body = RequestBody.create(mediaType, username);
            Request request = new Request.Builder()
                    .url("http://127.0.0.1:8000/")
                    .post(body)
                    .addHeader("type", "NEW_USER")
                    .build();

            new Thread(servlet).start();
            Submit sub = new Submit(request);
            new Thread(sub).start();
            Thread.sleep(200);
            JsonObject jsonObject = (new JsonParser()).parse(sub.getResult()).getAsJsonObject();
            users.add(jsonObject.getAsJsonObject("id").get("uuid").toString());
            // Make sure that all users were created successfully
            assertTrue("Unable to create user " + username + ".",
                    jsonObject.get("name").toString().equals("\"" + username + "\""));
        }
    }

    /**
     * Test that all users are returned properly.
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void _2_testListUsers() throws IOException, InterruptedException {

        Request request = new Request.Builder()
                .url("http://127.0.0.1:8000/")
                .get()
                .addHeader("type", "ALL_USERS")
                .build();

        new Thread(servlet).start();
        Submit sub = new Submit(request);
        new Thread(sub).start();
        Thread.sleep(200);
        JsonArray jsonArray = (new JsonParser()).parse(sub.getResult()).getAsJsonArray();
        assertTrue("Did not pull all users.",
                jsonArray.size() == 3);
    }

    /**
     * See if you can get George P. Burdell using just his UUID and GET_USERS
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void _2_testGetUsers() throws IOException, InterruptedException {

        Request request = new Request.Builder()
                .url("http://127.0.0.1:8000/")
                .get()
                .addHeader("type", "GET_USERS")
                .addHeader("uuids", "[" + users.get(0) + "]")
                .build();

        new Thread(servlet).start();
        Submit sub = new Submit(request);
        new Thread(sub).start();
        Thread.sleep(200);
        JsonArray jsonArray = (new JsonParser()).parse(sub.getResult()).getAsJsonArray();
        JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
        assertTrue("Did not get the requested users.",
                jsonObject.get("name").toString().equals("\"George P. Burdell\"") && jsonArray.size() == 1);
    }

    /**
     * Test whether 3 new conversations can successfully be created.
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void _2_testAddConversation() throws IOException, InterruptedException {

        String[] topics = {"Georgia Tech", "Microsoft", "Apple"};
        int count = 0;
        for (String topic : topics) {
            MediaType mediaType = MediaType.parse("application/octet-stream");
            RequestBody body = RequestBody.create(mediaType, topic);
            Request request = new Request.Builder()
                    .url("http://127.0.0.1:8000/")
                    .post(body)
                    .addHeader("type", "NEW_CONVERSATION")
                    .addHeader("owner", users.get(count).replace("\"", ""))
                    .build();
            count++;

            new Thread(servlet).start();
            Submit sub = new Submit(request);
            new Thread(sub).start();
            Thread.sleep(200);
            JsonObject jsonObject = (new JsonParser()).parse(sub.getResult()).getAsJsonObject();
            conversations.add(jsonObject.getAsJsonObject("id").get("uuid").toString());
            assertTrue("Unable to create conversation " + topic + ".",
                    jsonObject.get("title").toString().equals("\"" + topic + "\""));
        }
    }

    /**
     * See whether we can access the Georgia Tech conversation using just its UUID and GET_CONVERSATIONS
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void _3_testGetConversations() throws IOException, InterruptedException {

        Request request = new Request.Builder()
                .url("http://127.0.0.1:8000/")
                .get()
                .addHeader("type", "GET_CONVERSATIONS")
                .addHeader("uuids", "[" + conversations.get(0) + "]")
                .build();

        new Thread(servlet).start();
        Submit sub = new Submit(request);
        new Thread(sub).start();
        Thread.sleep(200);
        JsonArray jsonArray = (new JsonParser()).parse(sub.getResult()).getAsJsonArray();
        JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
        assertTrue("Did not get the requested users.",
                jsonObject.get("title").toString().equals("\"Georgia Tech\"") && jsonArray.size() == 1);
    }

    /**
     * Return all conversations with titles that match a regex string. This test in particular wants to access the
     * Microsoft conversation.
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void _3_testFindConversations() throws IOException, InterruptedException {

        Request request = new Request.Builder()
                .url("http://127.0.0.1:8000/")
                .get()
                .addHeader("type", "FIND_CONVERSATIONS")
                .addHeader("filter", ".*soft.*")
                .build();

        new Thread(servlet).start();
        Submit sub = new Submit(request);
        new Thread(sub).start();
        Thread.sleep(200);
        JsonArray jsonArray = (new JsonParser()).parse(sub.getResult()).getAsJsonArray();
        JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
        assertTrue("Did not find filter properly",
                jsonObject.get("title").toString().equals("\"Microsoft\"") && jsonArray.size() == 1);
    }


    /**
     * Access conversations between certain dates. In this case, in the last 3 seconds, which should return all conversations.
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void _3_testTimedConversations() throws IOException, InterruptedException {

        Request request = new Request.Builder()
                .url("http://127.0.0.1:8000/")
                .get()
                .addHeader("type", "TIMED_CONVERSATIONS")
                .addHeader("from", System.currentTimeMillis() - 3000 + "")
                .addHeader("to", System.currentTimeMillis() + "")
                .build();

        new Thread(servlet).start();
        Submit sub = new Submit(request);
        new Thread(sub).start();
        Thread.sleep(200);
        JsonArray jsonArray = (new JsonParser()).parse(sub.getResult()).getAsJsonArray();
        JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
        assertTrue("Timed conversation request did not return all conversations.",
                jsonArray.size() == 3);
    }


    /**
     * Create a message and see if it was successfully added
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void _3_testAddMessage() throws IOException, InterruptedException {

        String[] msgs = {"tfw bee", "Why doesn't Steve Ballmer let me ride the elevator with him?", "Sleek and modern."};
        int count = 0;
        for (String msg : msgs) {
            MediaType mediaType = MediaType.parse("application/octet-stream");
            RequestBody body = RequestBody.create(mediaType, msg);
            Request request = new Request.Builder()
                    .url("http://127.0.0.1:8000/")
                    .post(body)
                    .addHeader("type", "NEW_MESSAGE")
                    .addHeader("conversation", conversations.get(1).replace("\"", ""))
                    .addHeader("author", users.get(count).replace("\"", ""))
                    .build();
            count++;

            new Thread(servlet).start();
            Submit sub = new Submit(request);
            new Thread(sub).start();
            Thread.sleep(200);
            JsonObject jsonObject = (new JsonParser()).parse(sub.getResult()).getAsJsonObject();
            messages.add(jsonObject.getAsJsonObject("id").get("uuid").toString());
            assertTrue("Unable to create message " + msg + ".",
                    jsonObject.get("content").toString().equals("\"" + msg + "\""));
        }
    }


    /**
     * Get the 'tfw bee' message from just its UUID and GET_MESSAGES.
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void _4_testGetMessages() throws IOException, InterruptedException {

        Request request = new Request.Builder()
                .url("http://127.0.0.1:8000/")
                .get()
                .addHeader("type", "GET_MESSAGES")
                .addHeader("uuids", "[" + messages.get(0) + "]")
                .build();

        new Thread(servlet).start();
        Submit sub = new Submit(request);
        new Thread(sub).start();
        Thread.sleep(200);
        JsonArray jsonArray = (new JsonParser()).parse(sub.getResult()).getAsJsonArray();
        JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
        assertTrue("Did not get the requested messages.",
                jsonObject.get("content").toString().equals("\"tfw bee\"") && jsonArray.size() == 1);
    }


    /**
     * Access messages created within a certain time span. In this case, the last 3 seconds, so it accesses all 3 messages.
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void _4_testTimedMessages() throws IOException, InterruptedException {

        Request request = new Request.Builder()
                .url("http://127.0.0.1:8000/")
                .get()
                .addHeader("type", "TIMED_MESSAGES")
                .addHeader("from", System.currentTimeMillis() - 3000 + "")
                .addHeader("to", System.currentTimeMillis() + "")
                .addHeader("conversation", conversations.get(1).replaceAll("\"", ""))
                .build();

        new Thread(servlet).start();
        Submit sub = new Submit(request);
        new Thread(sub).start();
        Thread.sleep(200);
        JsonArray jsonArray = (new JsonParser()).parse(sub.getResult()).getAsJsonArray();
        assertTrue("Timed conversation request did not return all conversations.",
                jsonArray.size() == 3);
    }


    /**
     * Traverse message chains, in this case only moving one up the chain to get 2 messages total.
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void _4_testRangedMessages() throws IOException, InterruptedException {

        Request request = new Request.Builder()
                .url("http://127.0.0.1:8000/")
                .get()
                .addHeader("type", "RANGED_MESSAGES")
                .addHeader("root_message", messages.get(0).replaceAll("\"", ""))
                .addHeader("range", "1")
                .build();

        new Thread(servlet).start();
        Submit sub = new Submit(request);
        new Thread(sub).start();
        Thread.sleep(200);
        JsonArray jsonArray = (new JsonParser()).parse(sub.getResult()).getAsJsonArray();
        JsonObject jsonObject = jsonArray.get(1).getAsJsonObject();
        System.out.println(jsonArray.size());
        assertTrue("Ranged conversation request did not return all conversations.",
                jsonObject.get("content").toString().contains("elevator") && jsonArray.size() == 2);
    }


    @AfterClass
    public static void doAfter() throws IOException {
        servlet.getServer().kill();
    }

}
