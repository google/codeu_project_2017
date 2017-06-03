/**
 * Model class for an HTTP request.
 *
 * @author  Nick Petosa
 */
package codeu.chat.server.model;

import java.util.HashMap;


/**
 * A model class which encapsulates data about a received request.
 */
public class Request {

    private String verb;
    private String endpoint;
    private HashMap<String, String> headers;
    private String body;

    public Request() {
        headers = new HashMap<>();
    }

    /**
     *
     * @return this request's verb (GET, POST, PUT, ...).
     */
    public String getVerb() {
        return verb;
    }


    /**
     *
     * @return this request's endpoint.
     */
    public String getEndpoint() {return endpoint; }

    /**
     *
     * @param verb sets this request's verb (GET, POST, PUT, ...).
     */
    public void setVerb(String verb) {
        this.verb = verb;
    }

    /**
     *
     * @param endpoint sets this request's endpoint.
     */
    public void setEndpoint(String endpoint) {this.endpoint = endpoint; }

    /**
     *
     * @param key key of header to add.
     * @param value value of header to add.
     */
    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    /**
     *
     * @param key key of header to access.
     * @return value of key in headers. Returns null if no such key exists.
     */
    public String getHeader(String key) {
        return headers.get(key);
    }

    /**
     *
     * @param body value of request body text.
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     *
     * @return value of the request body.
     */
    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "Request[verb=" + verb + ", \nheaders=" + headers + ", \nbody=" + body + "]";
    }

}

