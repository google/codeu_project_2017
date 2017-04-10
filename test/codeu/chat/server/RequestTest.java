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
import okhttp3.*;
import okhttp3.Request;
import org.junit.Test;
import org.junit.Before;

import codeu.chat.util.Uuid;

import java.io.*;

public final class RequestTest {


    /**
     * Private inner class that creates a mini on-demand server.
     */
    private class Servlet implements Runnable {

        Server servlet;
        ConnectionSource serverSource;

        public Servlet(int port) throws IOException {
            servlet = new Server(Uuid.fromString("16"), Secret.parse("16"), new NoOpRelay());
            serverSource = ServerConnectionSource.forPort(port);
        }

        public Server getServer() {
            return servlet;
        }

        public void kill() {
            servlet.kill();
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

    @Before
    public void doBefore() throws IOException {
        Servlet servlet = new Servlet(8000);
        new Thread(servlet).start();
    }

    @Test
    public void testAddUser() throws IOException, InterruptedException {

        MediaType mediaType = MediaType.parse("application/octet-stream");
        RequestBody body = RequestBody.create(mediaType, "Geroeo");
        Request request = new Request.Builder()
                .url("http://127.0.0.1:8000/")
                .post(body)
                .addHeader("type", "NEW_USER_REQUEST")
                .build();

        Submit sub = new Submit(request);
        new Thread(sub).start();
        Thread.sleep(100);
        assertTrue("Unable to create user, received " + sub.getResult() + "instead.", sub.getResult().startsWith("[UUID:"));
    }

}
