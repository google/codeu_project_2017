// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package codeu.chat.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import codeu.chat.util.Serializer;
import codeu.chat.util.Serializers;

public final class Secret {

  // Use integers to avoid sign problems with bytes
  private static final Map<Integer, Character> TO_HEX = new HashMap<>();
  private static final Map<Character, Integer> FROM_HEX = new HashMap<>();

  static {

    TO_HEX.put(0x0, '0');
    TO_HEX.put(0x1, '1');
    TO_HEX.put(0x2, '2');
    TO_HEX.put(0x3, '3');
    TO_HEX.put(0x4, '4');
    TO_HEX.put(0x5, '5');
    TO_HEX.put(0x6, '6');
    TO_HEX.put(0x7, '7');
    TO_HEX.put(0x8, '8');
    TO_HEX.put(0x9, '9');
    TO_HEX.put(0xA, 'A');
    TO_HEX.put(0xB, 'B');
    TO_HEX.put(0xC, 'C');
    TO_HEX.put(0xD, 'D');
    TO_HEX.put(0xE, 'E');
    TO_HEX.put(0xF, 'F');

    FROM_HEX.put('0', 0x0);
    FROM_HEX.put('1', 0x1);
    FROM_HEX.put('2', 0x2);
    FROM_HEX.put('3', 0x3);
    FROM_HEX.put('4', 0x4);
    FROM_HEX.put('5', 0x5);
    FROM_HEX.put('6', 0x6);
    FROM_HEX.put('7', 0x7);
    FROM_HEX.put('8', 0x8);
    FROM_HEX.put('9', 0x9);
    FROM_HEX.put('A', 0xA);
    FROM_HEX.put('B', 0xB);
    FROM_HEX.put('C', 0xC);
    FROM_HEX.put('D', 0xD);
    FROM_HEX.put('E', 0xE);
    FROM_HEX.put('F', 0xF);
    FROM_HEX.put('a', 0xA);
    FROM_HEX.put('b', 0xB);
    FROM_HEX.put('c', 0xC);
    FROM_HEX.put('d', 0xD);
    FROM_HEX.put('e', 0xE);
    FROM_HEX.put('f', 0xF);
  }

  public static final Serializer<Secret> SERIALIZER = new Serializer<Secret>() {
    @Override
    public void write(OutputStream out, Secret value) throws IOException {
      Serializers.BYTES.write(out, value.bytes);
    }

    @Override
    public Secret read(InputStream in) throws IOException {
      return new Secret(Serializers.BYTES.read(in));
    }
  };

  private final byte[] bytes;

  public Secret(byte... bytes) {
    this.bytes = Arrays.copyOf(bytes, bytes.length);
  }

  @Override
  public String toString() {
    final StringBuilder string = new StringBuilder();

    for (final byte b : bytes) {
      string.append(TO_HEX.get((0xF0 & b) >>> 8));
      string.append(TO_HEX.get((0x0F & b)));
    }

    return string.toString();
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof Secret ?
        Arrays.equals(bytes, ((Secret)other).bytes) :
        false;
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(bytes);
  }

  // PARSE
  //
  // Take in the string representation of the secret and convert it into a byte
  // array. The string form of a secret should be a hex string that will be
  // converted into a byte array.
  //
  // For example: "ABABAB" becomes { 0xAB, 0xAB, 0xAB }
  public static Secret parse(String string) throws IOException {

    // A single byte requires two hex characters which means that for n bytes
    // there must be 2 * n hex digits. It is not a problem if an odd number of
    // hex characters are given - just pad the string at the start with a '0'.
    final String paddedString = string.length() % 2 == 0 ?
        string :
        "0" + string;

    // Make sure the string only has valid hex characters. Do it after we pad
    // to make sure that we don't accodently invalidate the string.
    for (int i = 0; i < paddedString.length(); i++) {
      if (FROM_HEX.get(paddedString.charAt(i)) == null) {
        throw new IOException(String.format(
            "Unsupported character '%c'",
            paddedString.charAt(i)));
      }
    }

    final byte[] bytes = new byte[paddedString.length() / 2];

    for (int i = 0; i < bytes.length; i++) {
      final int head = FROM_HEX.get(paddedString.charAt(2 * i));
      final int tail = FROM_HEX.get(paddedString.charAt(2 * i + 1));
      bytes[i] = (byte) ((head << 4) | tail);
    }

    return new Secret(bytes);
  }
}
