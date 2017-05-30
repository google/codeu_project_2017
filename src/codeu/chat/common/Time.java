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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import codeu.chat.util.Serializer;
import codeu.chat.util.Serializers;

public final class Time implements Comparable<Time> {

  public static final Serializer<Time> SERIALIZER = new Serializer<Time>() {

    @Override
    public void write(OutputStream out, Time value) throws IOException {

      Serializers.INTEGER.write(out, (int)(0xFFFFFFFF & (value.totalMs >>> 32)));
      Serializers.INTEGER.write(out, (int)(0xFFFFFFFF & (value.totalMs >>> 0)));

    }

    @Override
    public Time read(InputStream in) throws IOException {

      final long high = (long)Serializers.INTEGER.read(in);
      final long low = (long)Serializers.INTEGER.read(in);

      return Time.fromMs((high << 32) | low);

    }
  };

  private static final SimpleDateFormat formatter =
      new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss.SSS");

  private final long totalMs;

  private Time(long totalMs) { this.totalMs = totalMs; }

  public long inMs() { return totalMs; }

  @Override
  public int compareTo(Time other) {
    return Long.compare(totalMs, other.totalMs);
  }

  public boolean inRange(Time start, Time end) {
    return totalMs >= start.totalMs && totalMs <= end.totalMs;
  }

  @Override
  public String toString() {
    return formatter.format(new Date(totalMs));
  }

  public static Time fromMs(long ms) { return new Time(ms); }

  public static Time now() { return Time.fromMs(System.currentTimeMillis()); }

}
