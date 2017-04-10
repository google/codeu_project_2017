package codeu.chat.server;

import codeu.chat.server.model.Request;
/**
 * Controller for HTTP requests.
 *
 * @author  Nick Petosa
 */
import java.io.*;

public class RequestHandler {

    /**
     *
     * @param in the remote connection's data stream.
     * @return the constructed Request object.
     */
    public static Request parseRaw(InputStream in) throws IOException {

        final StringBuilder out = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        while (reader.ready()) {
            out.append((char) reader.read());
        }

        String[] struct = out.toString().split("\r\n");
        Request r = new Request();

        // Extract verb.
        // Stop split operation after finding the first space.
        r.setVerb(struct[0].split(" ", 2)[0]);

        // Extract headers.
        int i;
        for (i = 1; i < struct.length && struct[i].length() > 0; i++) {
            // Stop split operation after finding first colon + space.
            String[] entry = struct[i].split(": ", 2);
            r.addHeader(entry[0], entry[1]);
        }

        // Extract body.
        StringBuilder body = new StringBuilder();
        for (i++; i < struct.length; i++) {
            body.append(" " + struct[i]);
        }
        r.setBody(body.toString().substring(1));

        return r;
    }

    public static boolean successResponse(OutputStream out, String body) throws IOException {
        out.write(("HTTP/1.1 200 OK\r\n\r\n").getBytes());
        out.write((body).getBytes());
        return true;
    }

    public static boolean failResponse(OutputStream out, String message) throws IOException {
        out.write(("HTTP/1.1 400 Bad Request\r\n\r\n").getBytes());
        out.write((message).getBytes());
        return false;
    }

}
