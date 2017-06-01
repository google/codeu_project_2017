package codeu.chat.common;

import static org.junit.Assert.*;

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
public class SentimentScoreTest {

  @Test
  public void testJsonSerializer() {

    try {

      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      PrintWriter writer = new PrintWriter(outputStream, true);

      SentimentScore sentimentScore1 = new SentimentScore();

      SentimentScore.SERIALIZER.write(writer, sentimentScore1);

      ByteArrayInputStream byteStream = new ByteArrayInputStream(outputStream.toByteArray());
      BufferedReader reader = new BufferedReader(new InputStreamReader(byteStream));

      SentimentScore value1 = SentimentScore.SERIALIZER.read(reader);

      assertEquals(sentimentScore1.getScore(), value1.getScore(), 0.0001);


    } catch (IOException exc) {
      System.out.println("Exception thrown");
    }

  }

}
