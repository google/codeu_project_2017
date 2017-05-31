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

package codeu.chat.server;

import java.util.Random;

import codeu.chat.common.Uuid;
import codeu.chat.common.Uuids;

// Create a new random uuid. Uuids from this generator are random
// but are not guaranteed to be unique. Checking uniqueness is left
// to the caller.
final class RandomUuidGenerator implements Uuid.Generator {

  private static final class BasicUuid implements Uuid {

    private final Uuid root;
    private final int id;

    public BasicUuid(Uuid root, int id) {
      this.root = root;
      this.id = id;
    }

    @Override
    public Uuid root() { return root; }

    @Override
    public int id() { return id; }
  }

  private final Uuid commonRoot;
  private final Random random;

  public RandomUuidGenerator(Uuid root, long seed) {
    this.commonRoot = root;
    this.random = new Random(seed);
  }

  @Override
  public Uuid make() {
    return Uuids.complete(new BasicUuid(commonRoot, random.nextInt()));
  }
}
