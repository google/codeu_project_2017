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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

// BUNDLE
//
// A bundle is a collection of data that can be written to output streams
// and read from an input stream. It is a key-value pair map with nested
// children.
//
public final class Bundle {

  private final Map<String, String> fields = new HashMap<>();
  private final Map<String, Bundle> subBundles = new HashMap<>();
  private final Collection<Bundle> children = new HashSet<>();

  // SET
  //
  // Set the value under the given key.
  //
  public Bundle set(String key, String value) {
    fields.put(key, value);
    return this;
  }

  // GET
  //
  // Get the value mapped to the given key. Requesting a value from the bundle
  // that does not exist will yield a null value.
  //
  public String get(String key) {
    return fields.get(key);
  }

  // SET SUB BUNDLE
  //
  // Set the bundle value under the given key.
  //
  public Bundle setSubBundle(String key, Bundle value) {
    subBundles.put(key, value);
    return this;
  }

  // GET SUB BUNDLE
  //
  // Get the bundle under the given key.
  //
  public Bundle getSubBundle(String key) {
    return subBundles.get(key);
  }

  // ADD
  //
  // Add a child to this bundle.
  //
  public Bundle add(Bundle child) {
    children.add(child);
    return this;
  }

  // CHILDREN
  //
  // Get an iterator for all the children in this bundle.
  //
  public Iterable<Bundle> children() {
    return children;
  }

  // WRITE
  //
  // Write the bundle to the given output stream.
  //
  public static void write(OutputStream out, Bundle bundle) throws IOException {

    writeInteger(out, bundle.fields.size());
    writeInteger(out, bundle.subBundles.size());
    writeInteger(out, bundle.children.size());

    for (final Map.Entry<String, String> entry : bundle.fields.entrySet()) {

      final byte[] key = entry.getKey().getBytes();
      final byte[] value = entry.getValue().getBytes();

      writeInteger(out, key.length);
      writeInteger(out, value.length);

      out.write(key);
      out.write(value);
    }

    for (final Map.Entry<String, Bundle> entry : bundle.subBundles.entrySet()) {

      final byte[] key = entry.getKey().getBytes();
      writeInteger(out, key.length);
      out.write(key);

      write(out, entry.getValue());
    }

    for (final Bundle child : bundle.children) {
      write(out, child);
    }
  }

  // READ
  //
  // Read a bundle from the given input stream. Any failure to read a bundle
  // will result in an IOException getting thrown.
  //
  public static Bundle read(InputStream in) throws IOException {

    final Bundle bundle = new Bundle();

    final int numFields = readInteger(in);
    final int numSubBundles = readInteger(in);
    final int numChildren = readInteger(in);

    for (int i = 0; i < numFields; i++) {

      final int keyLength = readInteger(in);
      final int valueLength = readInteger(in);

      final String key = new String(readBytes(in, keyLength));
      final String value = new String(readBytes(in, valueLength));

      bundle.set(key, value);
    }

    for (int i = 0; i < numSubBundles; i++) {
      final int keyLength = readInteger(in);
      final String key = new String(readBytes(in, keyLength));

      bundle.setSubBundle(key, read(in));
    }

    for (int i = 0; i < numChildren; i++) {
      bundle.add(read(in));
    }

    return bundle;
  }

  private static void writeInteger(OutputStream out, int value) throws IOException {
    out.write(0xFF & (value >>> 24));
    out.write(0xFF & (value >>> 16));
    out.write(0xFF & (value >>> 8));
    out.write(0xFF & (value >>> 0));
  }

  private static int readInteger(InputStream in) throws IOException {
    final int b0 = readByte(in);
    final int b1 = readByte(in);
    final int b2 = readByte(in);
    final int b3 = readByte(in);

    return (b0 << 24) | (b1 << 16) | (b2 << 8) | b3;
  }

  private static byte[] readBytes(InputStream in, int length) throws IOException {
    final byte[] bytes = new byte[length];
    for (int i = 0; i < length; i++) {
      bytes[i] = (byte)readByte(in);
    }
    return bytes;
  }

  private static int readByte(InputStream in) throws IOException {
    final int b = in.read();
    if (b == -1) {
      throw new IOException("Unexpected EOS");
    }
    return b;
  }
}
