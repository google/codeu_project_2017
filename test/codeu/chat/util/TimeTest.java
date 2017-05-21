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

package codeu.chat.util;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import org.junit.Test;

public final class TimeTest {

  @Test
  public void testFromMs() {
    assertEquals(0, Time.fromMs(0).inMs());
    assertEquals(10, Time.fromMs(10).inMs());
  }

  @Test
  public void testJsonSerializer() {

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PrintWriter writer = new PrintWriter(outputStream, true);

    Time value = Time.fromMs(10);

    Time.SERIALIZER.write(writer, value);

    ByteArrayInputStream input = new ByteArrayInputStream(outputStream.toByteArray());

    BufferedReader reader = new BufferedReader(new InputStreamReader(input));

    try {
      Time received = Time.SERIALIZER.read(reader);
      assertEquals(value.inMs(), received.inMs());
    } catch (IOException exc) {
      System.out.println("Exception thrown");
    }

  }

}
