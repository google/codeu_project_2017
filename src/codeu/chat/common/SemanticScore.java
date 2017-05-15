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
public class SemanticScore {

  private int score;              // the average score for this object
  private int numScores;          // this will mark how many messages have been included in the calculation of the current score


  public static final Serializer<SemanticScore> SERIALIZER = new Serializer<SemanticScore>() {
    @Override
    public void write(OutputStream out, SemanticScore value) throws IOException {
      Serializers.INTEGER.write(out, value.score);
      Serializers.INTEGER.write(out, value.numScores);
    }

    @Override
    public SemanticScore read(InputStream in) throws IOException {

      return new SemanticScore(
          Serializers.INTEGER.read(in),
          Serializers.INTEGER.read(in)
      );

    }
  };


  public SemanticScore() {
    score = 0;
    numScores = 0;
  }

  public SemanticScore(int score, int numScores) {
    this.score = score;
    this.numScores = numScores;
  }

  /*
  When a new message is sent to the server, the server will add the message to the authors semantic score
  using this method
   */
  public int addMessage(Message m) {

    // todo (raami) : based on the message given, calculate the score, and update the score. Then return the newly updated score

    return 0;
  }

  private void updateScore(Sentiment score) {

    // todo (raami) : based on the given sentiment, update the average score.

  }

  private Sentiment calculateSentiment(Message message) {

    // todo (raami) : connect to the natural language API and return the sentiment for the given message

    return null;
  }

  public int getScore() {
    return this.score;
  }

  public int getNumScores() {
    return this.numScores;
  }

}
