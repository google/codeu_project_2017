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
            servlet = new Server(Uuid.fromString(String.valueOf(teamNumber)), Secret.parse("16"), new NoOpRelay());
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


    private static boolean setUpIsDone = false;
    @Before
    public void setUp() throws IOException {
        if (setUpIsDone) {
            return;
        }
        servlet = new Servlet(8000);
        users = new ArrayList<>();
        setUpIsDone = true;
    }

    @Test
    public void testAddUser() throws IOException, InterruptedException {

        String[] usernames = {"George P. Burdell", "Satya Nadella", "Johnny Ives"};

        for (String st : usernames) {
            MediaType mediaType = MediaType.parse("application/octet-stream");
            RequestBody body = RequestBody.create(mediaType, st);
            Request request = new Request.Builder()
                    .url("http://127.0.0.1:8000/")
                    .post(body)
                    .addHeader("type", "NEW_USER_REQUEST")
                    .build();

            new Thread(servlet).start();
            Submit sub = new Submit(request);
            new Thread(sub).start();
            Thread.sleep(200);
            JsonObject jsonObject = (new JsonParser()).parse(sub.getResult()).getAsJsonObject();
            users.add(jsonObject.get("uuid").toString());
            assertTrue("Unable to create user " + st + ".",
                    jsonObject.get("name").toString().equals("\"" + st + "\""));
        }
    }

    @Test
    public void testListUsers() throws IOException, InterruptedException {

        Request request = new Request.Builder()
                .url("http://127.0.0.1:8000/")
                .get()
                .addHeader("type", "GET_USERS_EXCLUDING_REQUEST")
                .addHeader("ids", "[" + users.get(0) + "]")
                .build();

        new Thread(servlet).start();
        Submit sub = new Submit(request);
        new Thread(sub).start();
        Thread.sleep(200);
        JsonArray jsonArray = (new JsonParser()).parse(sub.getResult()).getAsJsonArray();
        assertTrue("Unable to create user, received " + sub.getResult() + " instead.",
                jsonArray.size() == 2);
    }

    @AfterClass
    public static void doAfter() throws IOException {
        servlet.getServer().kill();
    }

}
