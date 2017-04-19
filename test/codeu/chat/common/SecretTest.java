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

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import org.junit.Test;

public final class SecretTest {

  @Test
  public void testParseEvenLength() throws IOException {

    final String input = "2345";
    final Secret expected = new Secret((byte)0x23, (byte)0x45);
    final Secret actual = Secret.parse(input);

    assertNotNull(actual);
    assertEquals(actual, expected);
  }

  @Test
  public void testParseEvenLengthWithUppercaseLetters() throws IOException {

    final String input = "ABCDEF";
    final Secret expected = new Secret((byte) 0xAB, (byte) 0xCD, (byte) 0xEF);
    final Secret actual = Secret.parse(input);

    assertNotNull(actual);
    assertEquals(actual, expected);
  }

  @Test
  public void testParseEvenLengthWithLowercaseLetters() throws IOException {

    final String input = "abcdef";
    final Secret expected = new Secret((byte) 0xAB, (byte) 0xCD, (byte) 0xEF);
    final Secret actual = Secret.parse(input);

    assertNotNull(actual);
    assertEquals(actual, expected);
  }

  @Test
  public void testParseEvenLengthWithLeadingZero() throws IOException {

    final String input = "012345";
    final Secret expected = new Secret((byte)0x01, (byte)0x23, (byte)0x45);
    final Secret actual = Secret.parse(input);

    assertNotNull(actual);
    assertEquals(actual, expected);
  }

  @Test
  public void testParseEvenLengthWithLeadingDoubleZero() throws IOException {

    final String input = "00123456";
    final Secret expected = new Secret((byte)0x00, (byte)0x12, (byte)0x34, (byte)0x56);
    final Secret actual = Secret.parse(input);

    assertNotNull(actual);
    assertEquals(actual, expected);
  }

  @Test
  public void testParseOddLength() throws IOException {

    final String input = "12345";
    final Secret expected = new Secret((byte)0x01, (byte)0x23, (byte)0x45);
    final Secret actual = Secret.parse(input);

    assertNotNull(actual);
    assertEquals(actual, expected);
  }

  @Test
  public void testParseOddLengthWithLeadingZero() throws IOException {

    final String input = "0123456";
    final Secret expected = new Secret((byte)0x00, (byte)0x12, (byte)0x34, (byte)0x56);
    final Secret actual = Secret.parse(input);

    assertNotNull(actual);
    assertEquals(actual, expected);
  }

  @Test
  public void testParseOddLengthWithLeadingDoubleZero() throws IOException {

    final String input = "001234567";
    final Secret expected = new Secret((byte)0x00, (byte)0x01, (byte)0x23, (byte)0x45, (byte)0x67);
    final Secret actual = Secret.parse(input);

    assertNotNull(actual);
    assertEquals(actual, expected);
  }
}
