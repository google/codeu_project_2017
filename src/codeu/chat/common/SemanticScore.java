package codeu.chat.common;

import com.google.cloud.language.v1beta1.Document;
import com.google.cloud.language.v1beta1.Document.Type;
import com.google.cloud.language.v1beta1.Sentiment;

/**
 * Created by rsharif on 5/3/17.
 */
public class SemanticScore {

    private int score;              // the average score for this object
    private int numScores;          // this will mark how many messages have been included in the calculation of the current score

    public SemanticScore() {
        score = 0;
        numScores = 0;
    }

    public int addMessage(Message m) {

        // todo (raami) : based on the message given, calculate the score, and update the score. Then return the newly updated score

        return 0;
    }

    private void updateScore(Sentiment score ) {

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
