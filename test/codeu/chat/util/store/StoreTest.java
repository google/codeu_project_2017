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

package codeu.chat.util.store;

import java.util.Comparator;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

public final class StoreTest {

  private static final Comparator<Integer> COMPARATOR = new Comparator<Integer>() {
    @Override
    public int compare(Integer a, Integer b) { return a.compareTo(b); }
  };

  private Store<Integer, Integer> store;

  @Before
  public void doBefore() {
    store = new Store<>(COMPARATOR);
  }

  @Test
  public void testOrderInOrderInsert() {

    store.insert(0, 0);
    store.insert(1, 10);
    store.insert(2, 20);
    store.insert(3, 30);
    store.insert(4, 40);

    final int[] order = { 0, 10, 20, 30, 40 };
    assertOrder(store.all(), order);
  }

  @Test
  public void testOrderReverseOrderInsert() {

    store.insert(4, 40);
    store.insert(3, 30);
    store.insert(2, 20);
    store.insert(1, 10);
    store.insert(0, 0);

    final int[] order = { 0, 10, 20, 30, 40 };
    assertOrder(store.all(), order);
  }

  @Test
  public void testOrderPingPongOrderInsert() {

    store.insert(0, 0);
    store.insert(4, 40);
    store.insert(1, 10);
    store.insert(3, 30);
    store.insert(2, 20);

    final int[] order = { 0, 10, 20, 30, 40 };
    assertOrder(store.all(), order);
  }

  @Test
  public void testBefore() {
    store.insert(0, 0);
    store.insert(1, 10);
    store.insert(2, 20);
    store.insert(3, 30);
    store.insert(4, 40);

    final int[] order = { 0, 10, 20 };
    assertOrder(store.before(2), order);
  }

  @Test
  public void testAfter() {
    store.insert(0, 0);
    store.insert(1, 10);
    store.insert(2, 20);
    store.insert(3, 30);
    store.insert(4, 40);

    final int[] order = { 20, 30, 40 };
    assertOrder(store.after(2), order);
  }

  @Test
  public void testRange() {
    store.insert(0, 0);
    store.insert(1, 10);
    store.insert(2, 20);
    store.insert(3, 30);
    store.insert(4, 40);

    final int[] order = { 10, 20, 30 };
    assertOrder(store.range(1, 3), order);
  }

  @Test
  public void testAt() {
    store.insert(0, 0);
    store.insert(1, 10);
    store.insert(2, 20);
    store.insert(2, 21);
    store.insert(2, 22);
    store.insert(3, 30);
    store.insert(4, 40);

    final int[] order = { 20, 21, 22 };
    assertOrder(store.at(2), order);
  }

  @Test
  public void testFirst() {
    store.insert(0, 0);
    store.insert(0, 1);

    store.insert(1, 10);
    store.insert(1, 11);

    store.insert(2, 20);
    store.insert(2, 21);

    store.insert(3, 30);
    store.insert(3, 31);

    store.insert(4, 40);
    store.insert(4, 41);

    assertTrue(store.first(0) == 0);
    assertTrue(store.first(1) == 10);
    assertTrue(store.first(2) == 20);
    assertTrue(store.first(3) == 30);
    assertTrue(store.first(4) == 40);
  }

  private static void assertOrder(Iterable<Integer> actual, int[] expected) {

    int at = 0;

    for (final Integer i : actual) {
      assertTrue(i == expected[at]);
      at += 1;
    }

    assertTrue(at == expected.length);
  }
}
