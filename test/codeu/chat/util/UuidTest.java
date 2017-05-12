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

import java.io.IOException;
import static org.junit.Assert.*;
import org.junit.Test;

public final class UuidTest {

  @Test
  public void testBadId() {
    assertNotNull(Uuid.NULL);
    assertEquals(Uuid.NULL.root(), null);
    assertEquals(Uuid.NULL.id(), 0);
  }

  @Test
  public void testEqualsNoRoot() {
    final Uuid u1 = new Uuid(5);
    final Uuid u2 = new Uuid(5);
    assertTrue(Uuid.equals(u1, u1));
    assertTrue(Uuid.equals(u2, u2));
    assertTrue(Uuid.equals(u1, u2));
    assertTrue(Uuid.equals(u2, u1));
  }

  @Test
  public void testEqualsWithRoot() {
    final Uuid r1 = new Uuid(3);
    final Uuid r2 = new Uuid(3);

    final Uuid u1 = new Uuid(r1, 5);
    final Uuid u2 = new Uuid(r2, 5);

    assertTrue(Uuid.equals(u1, u1));
    assertTrue(Uuid.equals(u2, u2));
    assertTrue(Uuid.equals(u1, u2));
    assertTrue(Uuid.equals(u2, u1));
  }

  @Test
  public void testNotEqualsNoRoot() {
    final Uuid u1 = new Uuid(5);
    final Uuid u2 = new Uuid(3);
    assertFalse(Uuid.equals(u1, u2));
    assertFalse(Uuid.equals(u2, u1));
  }

  @Test
  public void testNotEqualsWithRoot() {
    final Uuid r1 = new Uuid(1);
    final Uuid r2 = new Uuid(3);

    final Uuid u1 = new Uuid(r1, 5);
    final Uuid u2 = new Uuid(r2, 5);

    assertFalse(Uuid.equals(u1, u2));
    assertFalse(Uuid.equals(u2, u1));
  }

  @Test
  public void testNotEqualsMixLength() {
    final Uuid r1 = new Uuid(1);

    final Uuid u1 = new Uuid(r1, 5);
    final Uuid u2 = new Uuid(5);

    assertFalse(Uuid.equals(u1, u2));
    assertFalse(Uuid.equals(u2, u1));
  }

  @Test
  public void testRootEqual() {
    final Uuid r = new Uuid(1);

    final Uuid u1 = new Uuid(r, 2);
    final Uuid u2 = new Uuid(r, 3);

    assertTrue(Uuid.related(u1, u2));
  }

  @Test
  public void testRootEqualNot() {
    final Uuid r1 = new Uuid(1);
    final Uuid r2 = new Uuid(2);

    final Uuid u1 = new Uuid(r1, 3);
    final Uuid u2 = new Uuid(r2, 4);

    assertFalse(Uuid.related(u1, u2));
  }

  @Test
  public void testValidSingleLink() throws IOException {

    final String string = "100";
    final Uuid id = Uuid.parse(string);

    assertNotNull(id);
    assertNull(id.root());
    assertEquals(id.id(), 100);
  }

  @Test
  public void testValidMultiLink() throws IOException {

    final String string = "100.200";
    final Uuid id = Uuid.parse(string);

    assertNotNull(id);
    assertNotNull(id.root());
    assertNull(id.root().root());

    assertEquals(id.id(), 200);
    assertEquals(id.root().id(), 100);
  }

  @Test
  public void testLargeId() throws IOException {

    // Use a id value that would be too large for Integer.parseInt to handle
    // but would still parse if we could use unsigned integers.
    final String string = Long.toString(0xFFFFFFFFL);
    final Uuid id = Uuid.parse(string);

    assertNotNull(id);
    assertEquals(id.id(), 0xFFFFFFFF);
  }

  @Test
  public void testParsingToString() throws IOException {

    final Uuid start = new Uuid(new Uuid(1), 2);
    final String string = start.toString();
    final Uuid end = Uuid.parse(string);

    assertEquals(start, end);
  }
}
