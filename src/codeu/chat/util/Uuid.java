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

import java.lang.StringBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

public final class Uuid {

  public static final Uuid NULL = new Uuid(0);

  public static final Serializer<Uuid> SERIALIZER = new Serializer<Uuid>() {

    @Override
    public void write(OutputStream out, Uuid value) throws IOException {

      int length = 0;
      for (Uuid current = value; current != null; current = current.root()) {
        length += 1;
      }

      // To make things easy, limit the max length to be 255. It should be unlikely
      // that this limit will ever be reached as most chains should be less than
      // three long.
      if (length >= 0 && length <= 255) {
        out.write(length);
      } else {
        throw new IOException("Max supported Uuid chain length is 255");
      }

      for (Uuid current = value; current != null; current = current.root()) {
        Serializers.INTEGER.write(out, current.id());
      }
    }

    @Override
    public Uuid read(InputStream in) throws IOException {

      // "input.read" can only return one by of data so there is no need
      // to check that the bounds of 0 to 255 is respected.
      final int length = in.read();
      final int[] chain = new int[length];

      for (int i = 0; i < length; i++) {
        chain[i] = Serializers.INTEGER.read(in);
      }

      Uuid head = null;

      for (int i = length - 1; i >= 0; i--) {
        head = new Uuid(head, chain[i]);
      }

      return head;
    }
  };


  // GENERATOR
  //
  // This interface defines the inteface used for any class that will
  // create Uuids. It is nested in here as for naming reasons. The two
  // options was to have it sit along side Uuid can be called UuidGenerator
  // or to scope it inside of Uuid so that it would be called Uuid.Generator.
  //
  // As the generator is in a way a replacement for a constructor, it felt
  // better to place it inside the Uuid rather than have it side equal to
  // Uuid.
  public interface Generator {
    Uuid make();
  }

  private final Uuid root;
  private final int id;

  public Uuid(Uuid root, int id) {
    this.root = root;
    this.id = id;
  }

  public Uuid(int id) {
    this.root = null;
    this.id = id;
  }

  public Uuid root() {
    return root;
  }

  public int id() {
    return id;
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof Uuid && equals(this, (Uuid) other);
  }

  @Override
  public int hashCode() { return hash(this); }

  @Override
  public String toString() {
    return toString(this);
  }

  // Check if two Uuids share the same root. This check is only one level deep.
  public static boolean related(Uuid a, Uuid b) {
    return equals(a.root(), b.root());
  }

  // Check if two Uuids represent the same value even if they are different refereces. This
  // means that all ids from the tail to the root have the same ids.
  public static boolean equals(Uuid a, Uuid b) {

    // First check if 'a' and 'b' refer to the same instance. This also
    // checks if both 'a' and 'b' are null which saves us from having to
    // check 'a' == null && 'b' == null.

    if (a == b) {
      return true;
    }

    if (a == null && b != null) {
      return false;
    }

    if (a != null && b == null) {
      return false;
    }

    // Check id before checking the root as the ids are more likely to differ
    // and will short-circuit the logic preventing us from wasting time checking
    // the full chain.
    return a.id() == b.id() && equals(a.root(), b.root());

  }

  // Compute a hash code for the Uuids by walking up the chain.
  private static int hash(Uuid id) {

    int hash = 0;

    for (Uuid current = id; current != null; current = current.root()) {
      hash ^= Objects.hash(current.id());
    }

    return hash;
  }

  // Compute human-readable representation for Uuids
  // Use long internally to avoid negative integers.
  private static String toString(Uuid id) {
    final StringBuilder build = new StringBuilder();
    buildString(id, build);
    return build.substring(1);  // index of 1 to skip initial '.'
  }

  private static void buildString(Uuid current, StringBuilder build) {
    final long mask = (1L << 32) - 1;  // removes sign extension
    if (current != null) {
      buildString(current.root(), build);
      build.append(".").append(current.id() & mask);
    }
  }

  // Parse
  //
  // Create a uuid from a sting.
  public static Uuid parse(String string) throws IOException {
    return parse(null, string.split("\\."), 0);
  }

  private static Uuid parse(final Uuid root, String[] tokens, int index) throws IOException {

    final long id = Long.parseLong(tokens[index]);

    if ((id >> 32) != 0) {
      throw new IOException(String.format(
          "ID value '%s' is too large to be an unsigned 32 bit integer",
          tokens[index]));
    }

    final Uuid link = new Uuid(root, (int)(id & 0xFFFFFFFF));

    final int nextIndex = index + 1;

    return nextIndex < tokens.length ?
        parse(link, tokens, nextIndex) :
        link;
  }
}
