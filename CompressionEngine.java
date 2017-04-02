// CompressionEngine.java
// Author: Eric Zhuang
// CodeU Project Group 6

//Converts all aspects of a message into a string format
//Compresses the message content into a more storage-efficient format

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

//Use once project is compatible with Maven
//import com.google.gson.Gson

import codeu.chat.common.Time;
import codeu.chat.common.Uuid;
import codeu.chat.common.Uuids;

public class CompressionEngine{

    private GsonBuilder builder = new GsonBuilder();

    public static String compressMessage(Message msg){
        Gson gson = builder.create();
        return compress(gson.toJson(msg));
    }
    public static Message decompressMessage(String packet){
        Gson gson = builder.create();
        return gson.fromJson(decompress(packet), Message.class)
    }
}

// Class for decompressing strings, taken from https://github.com/diogoduailibe/lzstring4j
public class LZString {


  static String keyStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";


  public static String compress(String uncompressed) {

    if (uncompressed == null)
      return "";
    int value;
    HashMap<String, Integer> context_dictionary = new HashMap<String, Integer>();
    HashSet<String> context_dictionaryToCreate = new HashSet<String>();
    String context_c = "";
    String context_wc = "";
    String context_w = "";
    double context_enlargeIn = 2d; // Compensate for the first entry which
    // should not count
    int context_dictSize = 3;
    int context_numBits = 2;
    String context_data_string = "";
    int context_data_val = 0;
    int context_data_position = 0;

    for (int ii = 0; ii < uncompressed.length(); ii += 1) {
      context_c = "" + (uncompressed.charAt(ii));
      if (!context_dictionary.containsKey(context_c)) {
        context_dictionary.put(context_c, context_dictSize++);
        context_dictionaryToCreate.add(context_c);
      }

      context_wc = context_w + context_c;

      if (context_dictionary.containsKey(context_wc)) {
        context_w = context_wc;
      } else {
        if (context_dictionaryToCreate.contains(context_w)) {

          if (((int)context_w.charAt(0)) < 256) {
            for (int i = 0; i < context_numBits; i++) {
              context_data_val = (context_data_val << 1);
              if (context_data_position == 15) {
                context_data_position = 0;
                context_data_string += (char) context_data_val;
                context_data_val = 0;
              } else {
                context_data_position++;
              }
            }
            value = (int) context_w.charAt(0);
            for (int i = 0; i < 8; i++) {
              context_data_val = (context_data_val << 1)
                  | (value & 1);
              if (context_data_position == 15) {
                context_data_position = 0;
                context_data_string += (char) context_data_val;
                context_data_val = 0;
              } else {
                context_data_position++;
              }
              value = value >> 1;
            }
          } else {
            value = 1;
            for (int i = 0; i < context_numBits; i++) {
              context_data_val = (context_data_val << 1) | value;
              if (context_data_position == 15) {
                context_data_position = 0;
                context_data_string += (char) context_data_val;
                context_data_val = 0;
              } else {
                context_data_position++;
              }
              value = 0;
            }
            value = (int) context_w.charAt(0);
            for (int i = 0; i < 16; i++) {
              context_data_val = (context_data_val << 1)
                  | (value & 1);
              if (context_data_position == 15) {
                context_data_position = 0;
                context_data_string += (char) context_data_val;
                context_data_val = 0;
              } else {
                context_data_position++;
              }
              value = value >> 1;
            }
          }
          context_enlargeIn--;
          if (Double.valueOf(context_enlargeIn).intValue() == 0) {
            context_enlargeIn = Math.pow(2, context_numBits);
            context_numBits++;
          }
          context_dictionaryToCreate.remove(context_w);
        } else {
          value = context_dictionary.get(context_w);
          for (int i = 0; i < context_numBits; i++) {
            context_data_val = (context_data_val << 1)
                | (value & 1);
            if (context_data_position == 15) {
              context_data_position = 0;
              context_data_string += (char) context_data_val;
              context_data_val = 0;
            } else {
              context_data_position++;
            }
            value = value >> 1;
          }

        }
        context_enlargeIn--;
        if (Double.valueOf(context_enlargeIn).intValue() == 0) {
          context_enlargeIn = Math.pow(2, context_numBits);
          context_numBits++;
        }
        // Add wc to the dictionary.
        context_dictionary.put(context_wc, context_dictSize++);
        context_w = new String(context_c);
      }
    }

    // Output the code for w.
    if (!"".equals(context_w)) {
      if (context_dictionaryToCreate.contains(context_w)) {
        if (((int)context_w.charAt(0)) < 256) {
          for (int i = 0; i < context_numBits; i++) {
            context_data_val = (context_data_val << 1);
            if (context_data_position == 15) {
              context_data_position = 0;
              context_data_string += (char) context_data_val;
              context_data_val = 0;
            } else {
              context_data_position++;
            }
          }
          value = (int) context_w.charAt(0);
          for (int i = 0; i < 8; i++) {
            context_data_val = (context_data_val << 1)
                | (value & 1);
            if (context_data_position == 15) {
              context_data_position = 0;
              context_data_string += (char) context_data_val;
              context_data_val = 0;
            } else {
              context_data_position++;
            }
            value = value >> 1;
          }
        } else {
          value = 1;
          for (int i = 0; i < context_numBits; i++) {
            context_data_val = (context_data_val << 1) | value;
            if (context_data_position == 15) {
              context_data_position = 0;
              context_data_string += (char) context_data_val;
              context_data_val = 0;
            } else {
              context_data_position++;
            }
            value = 0;
          }
          value = (int) context_w.charAt(0);
          for (int i = 0; i < 16; i++) {
            context_data_val = (context_data_val << 1)
                | (value & 1);
            if (context_data_position == 15) {
              context_data_position = 0;
              context_data_string += (char) context_data_val;
              context_data_val = 0;
            } else {
              context_data_position++;
            }
            value = value >> 1;
          }
        }
        context_enlargeIn--;
        if (Double.valueOf(context_enlargeIn).intValue() == 0) {
          context_enlargeIn = Math.pow(2, context_numBits);
          context_numBits++;
        }
        context_dictionaryToCreate.remove(context_w);
      } else {
        value = context_dictionary.get(context_w);
        for (int i = 0; i < context_numBits; i++) {
          context_data_val = (context_data_val << 1) | (value & 1);
          if (context_data_position == 15) {
            context_data_position = 0;
            context_data_string += (char) context_data_val;
            context_data_val = 0;
          } else {
            context_data_position++;
          }
          value = value >> 1;
        }

      }
      context_enlargeIn--;
      if (Double.valueOf(context_enlargeIn).intValue() == 0) {
        context_enlargeIn = Math.pow(2, context_numBits);
        context_numBits++;
      }
    }

    // Mark the end of the stream
    value = 2;
    for (int i = 0; i < context_numBits; i++) {
      context_data_val = (context_data_val << 1) | (value & 1);
      if (context_data_position == 15) {
        context_data_position = 0;
        context_data_string += (char) context_data_val;
        context_data_val = 0;
      } else {
        context_data_position++;
      }
      value = value >> 1;
    }

    // Flush the last char
    while (true) {
      context_data_val = (context_data_val << 1);
      if (context_data_position == 15) {
        context_data_string += (char) context_data_val;
        break;
      } else
        context_data_position++;
    }
    return context_data_string;
  }

  public static String decompress(String compressed) {

    if (compressed == null)
      return "";
    if (compressed == "")
      return null;
    List<String> dictionary = new ArrayList<String>(200);
    double enlargeIn = 4;
    int dictSize = 4;
    int numBits = 3;
    String entry = "";
    StringBuilder result;
    String w;
    int bits;
    int resb;
    double maxpower;
    int power;
    String c = "";
    int d;
    Data data = Data.getInstance();
    data.string = compressed;
    data.val = (int) compressed.charAt(0);
    data.position = 32768;
    data.index = 1;

    for (int i = 0; i < 3; i += 1) {
      dictionary.add(i, "");
    }

    bits = 0;
    maxpower = Math.pow(2, 2);
    power = 1;
    while (power != Double.valueOf(maxpower).intValue()) {
      resb = data.val & data.position;
      data.position >>= 1;
      if (data.position == 0) {
        data.position = 32768;
        data.val = (int) data.string.charAt(data.index++);
      }
      bits |= (resb > 0 ? 1 : 0) * power;
      power <<= 1;
    }

    switch (bits) {
      case 0:
        bits = 0;
        maxpower = Math.pow(2, 8);
        power = 1;
        while (power != Double.valueOf(maxpower).intValue()) {
          resb = data.val & data.position;
          data.position >>= 1;
          if (data.position == 0) {
            data.position = 32768;
            data.val = (int) data.string.charAt(data.index++);
          }
          bits |= (resb > 0 ? 1 : 0) * power;
          power <<= 1;
        }
        c += (char) bits;
        break;
      case 1:
        bits = 0;
        maxpower = Math.pow(2, 16);
        power = 1;
        while (power != Double.valueOf(maxpower).intValue()) {
          resb = data.val & data.position;
          data.position >>= 1;
          if (data.position == 0) {
            data.position = 32768;
            data.val = (int) data.string.charAt(data.index++);
          }
          bits |= (resb > 0 ? 1 : 0) * power;
          power <<= 1;
        }
        c += (char) bits;
        break;
      case 2:
        return "";

    }

    dictionary.add(3, c);
    w = c;
    result = new StringBuilder(200);
    result.append(c);

   // w = result = c;

    while (true) {
      if (data.index > data.string.length()) {
        return "";
      }

      bits = 0;
      maxpower = Math.pow(2, numBits);
      power = 1;
      while (power != Double.valueOf(maxpower).intValue()) {
        resb = data.val & data.position;
        data.position >>= 1;
        if (data.position == 0) {
          data.position = 32768;
          data.val = (int) data.string.charAt(data.index++);
        }
        bits |= (resb > 0 ? 1 : 0) * power;
        power <<= 1;
      }

      switch (d = bits) {
        case 0:
          bits = 0;
          maxpower = Math.pow(2, 8);
          power = 1;
          while (power != Double.valueOf(maxpower).intValue()) {
            resb = data.val & data.position;
            data.position >>= 1;
            if (data.position == 0) {
              data.position = 32768;
              data.val = (int) data.string.charAt(data.index++);
            }
            bits |= (resb > 0 ? 1 : 0) * power;
            power <<= 1;
          }

          String temp = "";
          temp += (char) bits;
          dictionary.add(dictSize++, temp);

          d = dictSize - 1;

          enlargeIn--;

          break;
        case 1:
          bits = 0;
          maxpower = Math.pow(2, 16);
          power = 1;
          while (power != Double.valueOf(maxpower).intValue()) {
            resb = data.val & data.position;
            data.position >>= 1;
            if (data.position == 0) {
              data.position = 32768;
              data.val = (int) data.string.charAt(data.index++);
            }
            bits |= (resb > 0 ? 1 : 0) * power;
            power <<= 1;
          }

          temp = "";
          temp += (char) bits;

          dictionary.add(dictSize++, temp);

          d = dictSize - 1;

          enlargeIn--;

          break;
        case 2:
          return result.toString();
      }

      if (Double.valueOf(enlargeIn).intValue() == 0) {
        enlargeIn = Math.pow(2, numBits);
        numBits++;
      }

      if (d < dictionary.size() && dictionary.get(d) != null) {
        entry = dictionary.get(d);
      } else {
        if (d == dictSize) {
          entry = w + w.charAt(0);
        } else {
          return null;
        }
      }

      result.append(entry);

      // Add w+entry[0] to the dictionary.
      dictionary.add(dictSize++, w + entry.charAt(0));
      enlargeIn--;

      w = entry;

      if (Double.valueOf(enlargeIn).intValue() == 0) {
        enlargeIn = Math.pow(2, numBits);
        numBits++;
      }

    }
  }

class Data {
  public int val;
  public String string;
  public int position;
  public int index;

  public static Data getInstance() {
    return new Data();
  }
}
