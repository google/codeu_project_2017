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
import java.util.Collection;
import java.util.HashSet;

import codeu.chat.util.Serializer;
import codeu.chat.util.Serializers;
import codeu.chat.util.Time;
import codeu.chat.util.Uuid;

import com.google.firebase.database.Exclude;

public final class Conversation {

  public static final Serializer<Conversation> SERIALIZER = new Serializer<Conversation>() {

    @Override
    public void write(OutputStream out, Conversation value) throws IOException {

      Uuid.SERIALIZER.write(out, value.id);
      Uuid.SERIALIZER.write(out, value.owner);
      Time.SERIALIZER.write(out, value.creation);
      Serializers.STRING.write(out, value.title);
      Serializers.STRING.write(out, RSA.keyToString(value.PublicKey().getNumber()));
      Serializers.STRING.write(out, RSA.keyToString(value.SecretKey().getNumber()));
      Serializers.STRING.write(out, RSA.keyToString(value.PublicKey().getModulus()));
      Serializers.collection(Uuid.SERIALIZER).write(out, value.users);
      Uuid.SERIALIZER.write(out, value.firstMessage);
      Uuid.SERIALIZER.write(out, value.lastMessage);

    }

    @Override
    public Conversation read(InputStream in) throws IOException {

      final Conversation value = new Conversation(
          Uuid.SERIALIZER.read(in),
          Uuid.SERIALIZER.read(in),
          Time.SERIALIZER.read(in),
          Serializers.STRING.read(in),
          Serializers.STRING.read(in),
          Serializers.STRING.read(in),
          Serializers.STRING.read(in)
      );

      value.users.addAll(Serializers.collection(Uuid.SERIALIZER).read(in));

      value.firstMessage = Uuid.SERIALIZER.read(in);
      value.lastMessage = Uuid.SERIALIZER.read(in);

      return value;

    }
  };

  public ConversationSummary summary;

  public Uuid id;
  public Uuid owner;
  public Time creation;
  public String title;

  public String getPublicNumber() {
    return publicNumber;
  }

  public void setPublicNumber(String publicNumber) {
    this.publicNumber = publicNumber;
  }

  public String getSecretNumber() {
    return secretNumber;
  }

  public void setSecretNumber(String secretNumber) {
    this.secretNumber = secretNumber;
  }

  public String getModulus() {
    return modulus;
  }

  public void setModulus(String modulus) {
    this.modulus = modulus;
  }

  //Firebase is unable to parse BigIntegers, so the keys are saved as Strings
  private String publicNumber;
  private String secretNumber;
  private String modulus;

  @Exclude
  public final Collection<Uuid> users = new HashSet<>();
  public Uuid firstMessage = Uuid.NULL;
  public Uuid lastMessage = Uuid.NULL;

  public Conversation(Uuid id, Uuid owner, Time creation, String title) {

    this.id = id;
    this.owner = owner;
    this.creation = creation;
    this.title = title;

    this.summary = new ConversationSummary(id, owner, creation, title);

  }

  public Conversation(Uuid id, Uuid owner, Time creation, String title, EncryptionKey publicKey, EncryptionKey secretKey) {

    this.id = id;
    this.owner = owner;
    this.creation = creation;
    this.title = title;
    this.publicNumber = RSA.keyToString(publicKey.getNumber());
    this.secretNumber = RSA.keyToString(secretKey.getNumber());
    this.modulus = RSA.keyToString(publicKey.getModulus());

    this.summary = new ConversationSummary(id, owner, creation, title);

  }

  public Conversation(Uuid id, Uuid owner, Time creation, String title, String publicNumber, String secretNumber, String modulus) {

    this.id = id;
    this.owner = owner;
    this.creation = creation;
    this.title = title;
    this.publicNumber = publicNumber;
    this.secretNumber = secretNumber;
    this.modulus = modulus;

    this.summary = new ConversationSummary(id, owner, creation, title);

  }

  public void setSecretKey( EncryptionKey secretKey){
    this.secretNumber = RSA.keyToString(secretKey.getNumber());
    this.modulus = RSA.keyToString(secretKey.getModulus());
  }

  public void setPublicKey( EncryptionKey publicKey){
    this.publicNumber = RSA.keyToString(publicKey.getNumber());
    this.modulus = RSA.keyToString(publicKey.getModulus());
  }

  public EncryptionKey PublicKey(){
    return new EncryptionKey(RSA.keyToBigInteger(publicNumber), RSA.keyToBigInteger(modulus));
  }

  public EncryptionKey SecretKey(){
    return new EncryptionKey(RSA.keyToBigInteger(secretNumber), RSA.keyToBigInteger(modulus));
  }

  // Constructor with no arguments (needed for Firebase)
  public Conversation(){

  }
}
