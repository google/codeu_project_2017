package codeu.chat.common;

import static org.junit.Assert.*;

import codeu.chat.util.Time;
import codeu.chat.util.Uuid;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashSet;
import org.junit.Test;

/**
 * Created by rsharif on 5/19/17.
 */
public class ConversationTest {

  @Test
  public void testJsonSerialization() {

    try {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      PrintWriter writer = new PrintWriter(outputStream, true);

      // Set the conversation variables

      Uuid id = Uuid.parse("100.101");
      Uuid owner = Uuid.parse("100.102");
      Time creation = Time.fromMs(10);
      String title = "Conversation 1";

      HashSet<Uuid> users = new HashSet<>();
      users.add(Uuid.parse("100.103"));
      users.add(Uuid.parse("100.104"));

      Uuid firstMessage = Uuid.parse("100.105");
      Uuid lastMessage = Uuid.parse("100.106");

      // Create the conversation

      Conversation conversation = new Conversation(id, owner, creation, title);
      conversation.users.addAll(users);
      conversation.firstMessage = firstMessage;
      conversation.lastMessage = lastMessage;


      // Write the conversation to the output stream

      Conversation.SERIALIZER.write(writer, conversation);

      // Get back the conversation from the input stream

      ByteArrayInputStream byteStream = new ByteArrayInputStream(outputStream.toByteArray());
      BufferedReader reader = new BufferedReader(new InputStreamReader(byteStream));

      Conversation value = Conversation.SERIALIZER.read(reader);

      // Test that the properties are the same

      assertEquals(id, value.id);
      assertEquals(owner, value.owner);
      assertEquals(10, value.creation.inMs());
      assertEquals(title, value.title);
      assertEquals(users, value.users);
      assertEquals(firstMessage, value.firstMessage);
      assertEquals(lastMessage, value.lastMessage);

    } catch (IOException exc) {
      System.out.println("Exception thrown");
    }

  }


}
