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

import java.util.Arrays;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.AfterClass;

public final class SecretTest {

  @Test
  public void testParseEvenLength() {

    final String input = "2345";
    final byte[] expected = { 0x23, 0x45 };

    final byte[] actual = Secret.parse(input);

    assertNotNull(actual);
    assertTrue(Arrays.equals(expected, actual));

  }

  @Test
  public void testParseEvenLengthWithLeadingZero() {

    final String input = "012345";
    final byte[] expected = { 0x01, 0x23, 0x45 };

    final byte[] actual = Secret.parse(input);

    assertNotNull(actual);
    assertTrue(Arrays.equals(expected, actual));
  }

  @Test
  public void testParseEvenLengthWithLeadingDoubleZero() {

    final String input = "00123456";
    final byte[] expected = { 0x00, 0x12, 0x34, 0x56 };

    final byte[] actual = Secret.parse(input);

    assertNotNull(actual);
    assertTrue(Arrays.equals(expected, actual));
  }

  @Test
  public void testParseOddLength() {

    final String input = "12345";
    final byte[] expected = { 0x01, 0x23, 0x45 };

    final byte[] actual = Secret.parse(input);

    assertNotNull(actual);
    assertTrue(Arrays.equals(expected, actual));
  }

  @Test
  public void testParseOddLengthWithLeadingZero() {

    final String input = "0123456";
    final byte[] expected = { 0x00, 0x12, 0x34, 0x56 };

    final byte[] actual = Secret.parse(input);

    assertNotNull(actual);
    assertTrue(Arrays.equals(expected, actual));
  }

  @Test
  public void testParseOddLengthWithLeadingDoubleZero() {

    final String input = "001234567";
    final byte[] expected = { 0x00, 0x01, 0x23, 0x45, 0x67 };

    final byte[] actual = Secret.parse(input);

    assertNotNull(actual);
    assertTrue(Arrays.equals(expected, actual));
  }
}
