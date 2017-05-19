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
      SentimentScore sentimentScore2 = new SentimentScore(20, 10);

      SentimentScore.SERIALIZER.write(writer, sentimentScore1);
      SentimentScore.SERIALIZER.write(writer, sentimentScore2);

      ByteArrayInputStream byteStream = new ByteArrayInputStream(outputStream.toByteArray());
      BufferedReader reader = new BufferedReader(new InputStreamReader(byteStream));

      SentimentScore value1 = SentimentScore.SERIALIZER.read(reader);
      SentimentScore value2 = SentimentScore.SERIALIZER.read(reader);

      assertEquals(sentimentScore1.getScore(), value1.getScore());
      assertEquals(sentimentScore1.getNumScores(), value1.getNumScores());

      assertEquals(sentimentScore2.getScore(), value2.getScore());
      assertEquals(sentimentScore2.getNumScores(), value2.getNumScores());

    } catch (IOException exc) {
      System.out.println("Exception thrown");
    }

  }

}
