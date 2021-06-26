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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

public final class Serializers {

  public static final Serializer<Boolean> BOOLEAN = new Serializer<>() {

    @Override
    public void write(OutputStream out, Boolean value) throws IOException {
      out.write(value ? 1 : 0);
    }

    @Override
    public Boolean read(InputStream in) throws IOException {
      return in.read() != 0;
    }
  };

  public static final Serializer<Integer> INTEGER = new Serializer<>() {

    @Override
    public void write(OutputStream out, Integer value) throws IOException {

      for (int i = 24; i >= 0; i -= 8) {
        out.write(0xFF & (value >>> i));
      }

    }

    @Override
    public Integer read(InputStream in) throws IOException {

      int value = 0;

      for (int i = 0; i < 4; i++) {
        value = (value << 8) | in.read();
      }

      return value;

    }
  };

  public static final Serializer<byte[]> BYTES = new Serializer<>() {

    @Override
    public void write(OutputStream out, byte[] value) throws IOException {

      INTEGER.write(out, value.length);
      out.write(value);

    }

    @Override
    public byte[] read(InputStream input) throws IOException {

      final int length = INTEGER.read(input);
      final byte[] array = new byte[length];

      for (int i = 0; i < length; i++) {
        array[i] = (byte) input.read();
      }

      return array;

    }
  };

  public static final Serializer<String> STRING = new Serializer<>() {

    @Override
    public void write(OutputStream out, String value) throws IOException {

      BYTES.write(out, value.getBytes());

    }

    @Override
    public String read(InputStream input) throws IOException {

      return new String(BYTES.read(input));

    }
  };

  public static final Serializer<UUID> UUID = new Serializer<>() {
    @Override
    public void write(OutputStream out, UUID value) throws IOException {
      var str = value.toString();
      out.write(str.length());  // The UUID will be smaller than 255 so this will fit in a byte.
      out.write(str.getBytes());
    }

    @Override
    public UUID read(InputStream in) throws IOException {
      var length = in.read();

      var buffer = new byte[length];
      in.read(buffer);

      var str = new String(buffer);
      return java.util.UUID.fromString(str);
    }
  };

  public static final Serializer<Date> DATE = new Serializer<>() {
    @Override
    public void write(OutputStream out, Date value) throws IOException {
      var time = value.getTime();

      for (int i = 56; i >= 0; i -= 8) {
        out.write((int) (0xFF & (time >>> i)));
      }
    }

    @Override
    public Date read(InputStream in) throws IOException {
      long time = 0;

      for (int i = 0; i < 8; i++) {
        time = (time << 8) | in.read();
      }

      return new Date(time);
    }
  };

  public static <T> Serializer<Collection<T>> collection(final Serializer<T> serializer) {

    return new Serializer<>() {

      @Override
      public void write(OutputStream out, Collection<T> value) throws IOException {
        INTEGER.write(out, value.size());
        for (final T x : value) {
          serializer.write(out, x);
        }
      }

      @Override
      public Collection<T> read(InputStream in) throws IOException {
        final int size = INTEGER.read(in);
        Collection<T> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
          list.add(serializer.read(in));
        }
        return list;
      }
    };
  }

  public static <T> Serializer<T> nullable(final Serializer<T> serializer) {

    final int NO_VALUE = 0x00;
    final int YES_VALUE = 0xFF;

    return new Serializer<T>() {

      @Override
      public void write(OutputStream out, T value) throws IOException {
        if (value == null) {
          out.write(NO_VALUE);
        } else {
          out.write(YES_VALUE);
          serializer.write(out, value);
        }
      }

      @Override
      public T read(InputStream in) throws IOException {
        return in.read() == NO_VALUE ? null : serializer.read(in);
      }
    };
  }
}

