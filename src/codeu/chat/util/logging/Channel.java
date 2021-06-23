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

package codeu.chat.util.logging;

import java.util.Date;

/**
 * A means of writing data from the log to some medium (e.g. standard out or a file). Implementing
 * this interface will allow the log to know how to output the data that it collects.
 */
interface Channel {

  void write(java.util.logging.Level level, Date time, StackTraceElement[] stack, String message);
}

