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
import java.text.SimpleDateFormat;
import java.util.Date;

public final class Time implements Comparable<Time> {

  public static final Serializer<Time> SERIALIZER = new Serializer<Time>() {

    @Override
    public void write(OutputStream out, Time value) throws IOException {

      Serializers.LONG.write(out, value.inMs());

    }

    @Override
    public Time read(InputStream in) throws IOException {

      return Time.fromMs(Serializers.LONG.read(in));

    }
  };

  private static final SimpleDateFormat formatter =
      new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss.SSS");

  private final Date date;

  private Time(long totalMs) { this.date = new Date(totalMs); }

  public long inMs() { return date.getTime(); }

  @Override
  public int compareTo(Time other) {
    return date.compareTo(other.date);
  }

  public boolean inRange(Time start, Time end) {
    return this.compareTo(start) >= 0 && this.compareTo(end) <= 0;
  }

  @Override
  public String toString() {
    return formatter.format(date);
  }

  public static Time fromMs(long ms) { return new Time(ms); }

  public static Time now() { return Time.fromMs(System.currentTimeMillis()); }

}
