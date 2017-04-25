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


    private static boolean setUpIsDone = false;
    @Before
    public void setUp() throws IOException {
        if (setUpIsDone) {
            return;
        }
        servlet = new Servlet(8000);
        users = new ArrayList<>();
        conversations = new ArrayList<>();
        setUpIsDone = true;
    }

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
            users.add(jsonObject.get("uuid").toString());
            assertTrue("Unable to create user " + username + ".",
                    jsonObject.get("name").toString().equals("\"" + username + "\""));
        }
    }

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

    @Test
    public void _3_testAddConversation() throws IOException, InterruptedException {

        while (users.size() == 0) {
            Thread.sleep(100);
        }

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
            conversations.add(jsonObject.get("uuid").toString());
            assertTrue("Unable to create conversation " + topic + ".",
                    jsonObject.getAsJsonObject("summary").get("title").toString().equals("\"" + topic + "\""));
        }
    }


    @AfterClass
    public static void doAfter() throws IOException {
        servlet.getServer().kill();
    }

}
