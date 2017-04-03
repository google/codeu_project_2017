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

import java.math.BigInteger;
import java.security.*;

public class Password {

  private Password() {}

  public static String getHashCode(String value, String salt) {
    String hashValue = null;
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      md.update(value.getBytes());
      md.update(salt.getBytes());
      byte[] array = md.digest();

      StringBuffer hexString = new StringBuffer();
      for (int i = 0; i < array.length; i++) {
        hexString.append(Integer.toHexString(0xFF & array[i]));
      }
      hashValue = hexString.toString();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return hashValue;
  }

  public static String generateSalt() {
    SecureRandom random = new SecureRandom();
    return new BigInteger(130, random).toString(32);
  }
}
