package codeu.chat.util;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.Test;
/**
 * Created by rsharif on 5/22/17.
 */
public class SerializersTests {

  @Test
  public void testDoubleSerializer() {

    try {

      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

      double[] tests = new double[10];

      for (int i = 0; i < tests.length; i++) {
        tests[i] = Math.random() * 1000000 * Math.pow(-1, (int)(Math.random() * 2) + 1);
        Serializers.DOUBLE.write(outputStream, tests[i]);
      }

      ByteArrayInputStream byteStream = new ByteArrayInputStream(outputStream.toByteArray());

      for (int i = 0; i < tests.length; i++) {
        assertEquals(tests[i], Serializers.DOUBLE.read(byteStream), 0.000001);
      }

    } catch (IOException exc) {
      System.out.println("Exception thrown");
    }

  }

}
