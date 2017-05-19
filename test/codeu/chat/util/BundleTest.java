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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

public final class BundleTest {

  @Test
  public void setAndGetField() {

    final Bundle bundle = new Bundle();
    assertEquals(bundle, bundle.set("key", "value"));
    assertEquals("value", bundle.get("key"));
  }

  @Test
  public void addAndGetChild() {

    final Bundle child = new Bundle();
    final Bundle parent = new Bundle();
    assertEquals(parent, parent.add(child));

    final List<Bundle> children = new ArrayList<>();
    for (final Bundle c : parent.children()) {
      children.add(c);
    }

    assertEquals(1, children.size());
    assertEquals(child, children.get(0));
  }

  @Test
  public void setAndGetSubBundle() {

    final Bundle parent = new Bundle();
    final Bundle subBundle = new Bundle();
    assertEquals(parent, parent.setSubBundle("key", subBundle));
    assertEquals(subBundle, parent.getSubBundle("key"));
  }

  @Test
  public void writeAndRead() throws IOException {

    final Bundle child = new Bundle();
    child.set("key1", "value1");
    final Bundle parent = new Bundle();
    parent.set("key0", "value0");
    parent.add(child);

    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    Bundle.write(out, parent);

    final ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
    final Bundle readParent = Bundle.read(in);

    assertEquals("value0", readParent.get("key0"));

    final List<Bundle> readChildren = new ArrayList<>();
    for (final Bundle c : readParent.children()) {
      readChildren.add(c);
    }

    assertEquals(1, readChildren.size());
    assertEquals("value1", readChildren.get(0).get("key1"));
  }
}
