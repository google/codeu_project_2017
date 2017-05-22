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
import org.junit.Test;
/**
 * Created by rsharif on 5/19/17.
 */
public class MessageTest {

  @Test
  public void testJsonSerialization() {

    try {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      PrintWriter writer = new PrintWriter(outputStream, true);

      Uuid id = Uuid.parse("100.100");
      Uuid previous = Uuid.parse("100.101");
      Time creation = Time.fromMs(10);
      Uuid author = Uuid.parse("100.102");
      String content = "Hello";
      Uuid next = Uuid.parse("100.103");

      Message message = new Message(id, next, previous, creation, author, content);

      Message.SERIALIZER.write(writer, message);

      ByteArrayInputStream byteStream = new ByteArrayInputStream(outputStream.toByteArray());
      BufferedReader reader = new BufferedReader(new InputStreamReader(byteStream));

      Message value = Message.SERIALIZER.read(reader);

      assertEquals(message.id, value.id);
      assertEquals(message.previous, value.previous);
      assertEquals(creation.inMs(), value.creation.inMs());
      assertEquals(message.author, value.author);
      assertEquals(message.content, value.content);
      assertEquals(message.next, value.next);

    } catch (IOException exc) {
      System.out.println("Exception thrown");
    }

  }


}
