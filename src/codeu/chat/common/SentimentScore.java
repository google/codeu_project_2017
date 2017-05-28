package codeu.chat.common;

import codeu.chat.util.Serializer;
import codeu.chat.util.Serializers;
import com.google.cloud.language.spi.v1beta2.LanguageServiceClient;

import com.google.cloud.language.v1beta2.AnalyzeSentimentResponse;
import com.google.cloud.language.v1beta2.Document;
import com.google.cloud.language.v1beta2.Document.Type;
import com.google.cloud.language.v1beta2.Sentiment;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Created by rsharif on 5/3/17.
 */
public class SentimentScore {

  private double score;

  /*
   * This keeps track of the sum of all the past weights to be used in updating the score
   */

  private double weighting;

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
      Serializers.DOUBLE.write(out, value.score);
      Serializers.DOUBLE.write(out, value.weighting);
    }

    @Override
    public SentimentScore read(InputStream in) throws IOException {

      return new SentimentScore(
          Serializers.DOUBLE.read(in),
          Serializers.DOUBLE.read(in)
      );

    }

    @Override
    public void write(PrintWriter out, SentimentScore value) {
      Gson gson = Serializers.GSON;
      String output = gson.toJson(value);
      out.println(output);
    }

    @Override
    public SentimentScore read(BufferedReader in) throws IOException {
      Gson gson = Serializers.GSON;
      SentimentScore value = gson.fromJson(in.readLine(), SentimentScore.class);
      return value;
    }
  };

  public SentimentScore() {
    score = 0;
    weighting = 0;
  }

  // This was made private because it shouldn't be used other than by the serializer
  private SentimentScore(double score, double weighting) {
    this.score = score;
    this.weighting = weighting;
  }

  /*
   * When a new message is sent to the server, the server will add the message to the authors sentiment score
   * using this method
   */
  public double addMessage(Message m) {

    if (m == null) {
      return score;
    }

    try {
      Sentiment sentiment = calculateSentiment(m);
      updateScore(sentiment);
    } catch (IOException exc) {
      System.out.println("Error with sentiment analysis");
    }

    return score;

  }

  private void updateScore(Sentiment sentiment) {

    // todo: based on the given sentiment, update the sentiment score.
    /*
     * How will a specific score impact the users current score.
     * the sentiment has both a score and a magnitude. How will they both be used in the
     * calculation?
    */

    final float score = sentiment.getScore();
    final float magnitude = sentiment.getMagnitude();

    // the following is a temporary solution to avoid the weighting getting too high
    final double nextWeighting = Math.min(this.weighting + magnitude, 50);

    this.score = (this.score * this.weighting) +  (score * magnitude);
    this.score /= nextWeighting;
    this.weighting = nextWeighting;

  }

  private Sentiment calculateSentiment(Message message) throws IOException {

    String content = message.content;

    if (languageClient == null) {
      languageClient = LanguageServiceClient.create();
    }

    Document doc = Document.newBuilder()
        .setContent(content)
        .setType(Type.PLAIN_TEXT)
        .build();

    AnalyzeSentimentResponse response = languageClient.analyzeSentiment(doc);

    return response.getDocumentSentiment();

  }

  public double getScore() {
    return this.score;
  }

}