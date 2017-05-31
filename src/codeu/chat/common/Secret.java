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

public final class Secret {

  // PARSE
  //
  // Take in the string representation of the secret and convert it into a byte
  // array. The string form of a secret should be a hex string that will be
  // converted into a byte array.
  //
  // For example: "ABABAB" becomes { 0xAB, 0xAB, 0xAB }
  public static byte[] parse(String stringSecret) {

    final byte[] expanded = new byte[stringSecret.length() + stringSecret.length() % 2];

    final int offset = stringSecret.length() % 2;

    for (int i = 0; i < stringSecret.length(); i++) {
      expanded[offset + i] = (byte)toHex(stringSecret.charAt(i));
    }

    final byte[] compressed = new byte[expanded.length / 2];

    for (int i = 0; i < compressed.length; i++) {
      compressed[i] = (byte)((expanded[2 * i] << 4) | expanded[2 * i + 1]);
    }

    return compressed;
  }

  private static final int toHex(char c) {

    // If an invalid value was given, it will be treated as 0.

    switch(c) {
      case '0':
      case '1':
      case '2':
      case '3':
      case '4':
      case '5':
      case '6':
      case '7':
      case '8':
      case '9':
        return c - '0';

      case 'A':
      case 'B':
      case 'C':
      case 'D':
      case 'E':
      case 'F':
        return c - 'A';

      default:
        return 0;
    }
  }
}
