package codeu.chat.common;

import codeu.chat.util.Serializer;
import codeu.chat.util.Serializers;
import com.google.cloud.language.spi.v1beta2.LanguageServiceClient;

import com.google.cloud.language.v1beta2.AnalyzeSentimentResponse;
import com.google.cloud.language.v1beta2.Document;
import com.google.cloud.language.v1beta2.Document.Type;
import com.google.cloud.language.v1beta2.Sentiment;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by rsharif on 5/3/17.
 */
public class SentimentScore {

  private int score;

  /*
   * this will mark how many messages have been included in the calculation of the current score
   * todo: figure out how this will be used
   */

  private int numScores;

  /*
   * The language client is used to connect to the natural language API.
   * Requires environment variable GOOGLE_APPLICATION_CREDENTIALS=<PATH/TO/SERVICE-ACCOUNT/JSON>
   * Requires Google Cloud SDK
   */


  /*
   * The language client is static because it was found that the first sentiment analysis
   * had a long delay. Having a static client ensures that this delay happens only once
   * (the first time an analysis is requested)
   */
  private static LanguageServiceClient languageClient;

  public static final Serializer<SentimentScore> SERIALIZER = new Serializer<SentimentScore>() {
    @Override
    public void write(OutputStream out, SentimentScore value) throws IOException {
      Serializers.INTEGER.write(out, value.score);
      Serializers.INTEGER.write(out, value.numScores);
    }

    @Override
    public SentimentScore read(InputStream in) throws IOException {

      return new SentimentScore(
          Serializers.INTEGER.read(in),
          Serializers.INTEGER.read(in)
      );

    }
  };


  public SentimentScore() {
    score = 0;
    numScores = 0;
  }

  public SentimentScore(int score, int numScores) {
    this.score = score;
    this.numScores = numScores;
  }

  /*
   * When a new message is sent to the server, the server will add the message to the authors sentiment score
   * using this method
   */
  public int addMessage(Message m) {

    if (m == null) return score;

    try {
      Sentiment sentiment = calculateSentiment(m);
      updateScore(sentiment);
    } catch (IOException exc) {
      System.out.println("Error with sentiment analysis");
    }

    return score;

  }

  private void updateScore(Sentiment score) {

    // todo: based on the given sentiment, update the sentiment score.
    /*
     * How will a specific score impact the users current score.
     * the sentiment has both a score and a magnitude. How will they both be used in the
     * calculation?
    */
  }

  private Sentiment calculateSentiment(Message message) throws IOException {

    String content = message.content;

    if (languageClient == null) languageClient = LanguageServiceClient.create();

    Document doc = Document.newBuilder()
        .setContent(content)
        .setType(Type.PLAIN_TEXT)
        .build();

    AnalyzeSentimentResponse response = languageClient.analyzeSentiment(doc);

    return response.getDocumentSentiment();

  }

  public int getScore() {
    return this.score;
  }

  public int getNumScores() {
    return this.numScores;
  }

}