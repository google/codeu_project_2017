package codeu.chat.util;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * Tests the morse converter.
 *
 * @author Lauren
 *
 */
public class MorseConverterTest {

  /**
   * Tests a word converted to morse.
   */
  @Test
  public void testWordConversion() {
    String morse = MorseConverter.wordToMorse("hi");
    assertTrue(morse.equals(".... .. /"));
  }

  /**
   * Tests that more than one sentence can be translated.
   */
  @Test
  public void testSentenceConversion() {
    String paragraph = "Hi there. This is morse code. But is it? Let's check!";
    String morse = MorseConverter.paragraphToMorse(paragraph);
    assertTrue(morse.equals(".... .. /– .... . .–. . /– .... .. ... /"
        + ".. ... /–– ––– .–. ... . /–.–. ––– –.. . /"
        + "–... ..– – /.. ... /.. – /.–.. . – /... /"
        + "–.–. .... . –.–. –.– /"));

  }

  /**
   * Tests that a morse word is translated to the correct integer.
   */
  @Test
  public void testSoundTranslate() {
    String morse = ".–.–– / –//";
    List<Integer> translate = MorseConverter.morseToSoundTranslate(morse);
    List<Integer> expected = new ArrayList<>();
    expected.add(0);
    expected.add(1);
    expected.add(0);
    expected.add(1);
    expected.add(1);
    expected.add(2);
    expected.add(3);
    expected.add(2);
    expected.add(1);
    expected.add(3);
    expected.add(3);
    for (int i = 0; i < translate.size(); i++) {
      assertTrue(expected.get(i).equals(translate.get(i)));
    }
  }
}
