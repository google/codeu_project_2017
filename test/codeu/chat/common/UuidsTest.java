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

public final class UuidsTest {

  @Test
  public void testValidSingleLink() {

    final String string = "100";
    final Uuid id = Uuids.fromString(string);

    assertNotNull(id);
    assertNull(id.root());
    assertEquals(id.id(), 100);
  }

  @Test
  public void testValidMultiLink() {

    final String string = "100.200";
    final Uuid id = Uuids.fromString(string);

    assertNotNull(id);
    assertNotNull(id.root());
    assertNull(id.root().root());

    assertEquals(id.id(), 200);
    assertEquals(id.root().id(), 100);
  }
}
