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
     * Responsible for interpreting request queries. Populates a request object given a remote connection input stream and
     * then returns it. For reference, here's an example request parseRaw may encounter:
     *
     * POST  HTTP/1.1
     * Host: 127.0.0.1:8000
     * type: ALL_USERS
     *
     * This method simple structures this data.
     *
     * Parses the raw string that it is getting from the INPUT string as arg.
     *  --> client connection to server, looks at data, and interprets what the data means.
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
        if (r.getVerb().equals("POST")) {
            r.setBody(body.toString().substring(1));
        }

        return r;
    }


    /**
     *
     * Tells the user the request was successful, and typically includes the JSONified object result(s) as well via string
     *
     * @param out the remote connection's data stream.
     * @param body the response message.
     * @return success.
     */
    public static boolean successResponse(OutputStream out, String body) throws IOException {
        out.write(("HTTP/1.1 200 OK\r\n\r\n").getBytes());
        out.write((body).getBytes());
        return true;
    }

    /**
     *
     * Tells the user the request was a failure, and
     *
     * @param out the remote connection's data stream.
     * @param message the response message.
     * @return success.
     */
    public static boolean failResponse(OutputStream out, String message) throws IOException {
        out.write(("HTTP/1.1 400 Bad Request\r\n\r\n").getBytes());
        out.write((message).getBytes());
        return false;
    }

    /**
     *
     * Handles pre-flight
     *
     * @param out the remote connection's data stream.
     * @return success.
     */
    public static boolean optionsResponse(OutputStream out, Request r) throws IOException {
        out.write(("HTTP/1.1 200 OK\r\nAccess-Control-Allow-Origin: http://localhost:1128\r\nAccess-Control-Allow-Methods:" +
          "GET, POST, PATCH, PUT, DELETE, OPTIONS\r\nAccess-Control-Allow-Headers:" +
          r.getHeader("Access-Control-Request-Headers")).getBytes());
        return true;
    }

}
