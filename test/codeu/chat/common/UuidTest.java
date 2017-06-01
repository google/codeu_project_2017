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
import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.AfterClass;

public final class UuidTest {

  private static final class TestUuid implements Uuid {

    private final Uuid root;
    private final int id;

    public TestUuid(Uuid root, int id) {
      this.root = root;
      this.id = id;
    }

    @Override
    public Uuid root() { return root; }

    @Override
    public int id() { return id; }

  }

  @BeforeClass
  public static void doBeforeClass() { }

  @Test
  public void testBadId() {
    assertNotNull(Uuids.NULL);
    assertEquals(Uuids.NULL.root(), null);
    assertEquals(Uuids.NULL.id(), 0);
  }

  @Test
  public void testEqualsNoRoot() {
    final Uuid u1 = new TestUuid(null, 5);
    final Uuid u2 = new TestUuid(null, 5);
    assertTrue(Uuids.equals(u1, u1));
    assertTrue(Uuids.equals(u2, u2));
    assertTrue(Uuids.equals(u1, u2));
    assertTrue(Uuids.equals(u2, u1));
  }

  @Test
  public void testEqualsWithRoot() {
    final Uuid r1 = new TestUuid(null, 3);
    final Uuid r2 = new TestUuid(null, 3);

    final Uuid u1 = new TestUuid(r1, 5);
    final Uuid u2 = new TestUuid(r2, 5);

    assertTrue(Uuids.equals(u1, u1));
    assertTrue(Uuids.equals(u2, u2));
    assertTrue(Uuids.equals(u1, u2));
    assertTrue(Uuids.equals(u2, u1));
  }

  @Test
  public void testNotEqualsNoRoot() {
    final Uuid u1 = new TestUuid(null, 5);
    final Uuid u2 = new TestUuid(null, 3);
    assertFalse(Uuids.equals(u1, u2));
    assertFalse(Uuids.equals(u2, u1));
  }

  @Test
  public void testNotEqualsWithRoot() {
    final Uuid r1 = new TestUuid(null, 1);
    final Uuid r2 = new TestUuid(null, 3);

    final Uuid u1 = new TestUuid(r1, 5);
    final Uuid u2 = new TestUuid(r2, 5);

    assertFalse(Uuids.equals(u1, u2));
    assertFalse(Uuids.equals(u2, u1));
  }

  @Test
  public void testNotEqualsMixLength() {
    final Uuid r1 = new TestUuid(null, 1);

    final Uuid u1 = new TestUuid(r1, 5);
    final Uuid u2 = new TestUuid(null, 5);

    assertFalse(Uuids.equals(u1, u2));
    assertFalse(Uuids.equals(u2, u1));
  }

  @Test
  public void testRootEqual() {
    final Uuid r = new TestUuid(null, 1);

    final Uuid u1 = new TestUuid(r, 2);
    final Uuid u2 = new TestUuid(r, 3);

    assertTrue(Uuids.related(u1, u2));
  }

  @Test
  public void testRootEqualNot() {
    final Uuid r1 = new TestUuid(null, 1);
    final Uuid r2 = new TestUuid(null, 2);

    final Uuid u1 = new TestUuid(r1, 3);
    final Uuid u2 = new TestUuid(r2, 4);

    assertFalse(Uuids.related(u1, u2));
  }

  @AfterClass
  public static void doAfterClass() { }
}
