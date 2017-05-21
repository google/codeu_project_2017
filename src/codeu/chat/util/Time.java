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

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class Time implements Comparable<Time> {

  public static final JsonSerializer<Time> JSON_SERIALIZER = new JsonSerializer<Time>() {
    @Override
    public JsonElement serialize(Time src, Type typeOfSrc, JsonSerializationContext
        context) {
      return src == null ? null : new JsonPrimitive(src.inMs());
    }
  };

  public static final JsonDeserializer<Time> JSON_DESERIALIZER = new JsonDeserializer<Time>() {
    @Override
    public Time deserialize(JsonElement json, Type typeOfT,  JsonDeserializationContext context)
        throws JsonParseException {

      return json == null ? null : Time.fromMs(json.getAsLong());

    }
  };

  public static final Serializer<Time> SERIALIZER = new Serializer<Time>() {

    @Override
    public void write(OutputStream out, Time value) throws IOException {

      Serializers.LONG.write(out, value.inMs());

    }

    @Override
    public Time read(InputStream in) throws IOException {

      return Time.fromMs(Serializers.LONG.read(in));

    }

    @Override
    public void write(PrintWriter out, Time value) {
      Gson gson = Serializers.GSON;
      String output = gson.toJson(value);
      out.println(output);
    }

    @Override
    public Time read(BufferedReader in) throws IOException{
      Gson gson = Serializers.GSON;
      Time value = gson.fromJson(in.readLine(), Time.class);
      return value;
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
