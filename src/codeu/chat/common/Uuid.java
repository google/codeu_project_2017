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

public interface Uuid {

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
  interface Generator {
    Uuid make();
  }

  Uuid root();
  int id();

}
