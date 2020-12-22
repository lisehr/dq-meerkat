package dqm.jku.dqmeerkat.util;

import java.security.MessageDigest;

import com.google.gson.GsonBuilder;

public class HashingUtils {
  /**
   * Applies Sha256 to a string and returns the result.
   * 
   * @param input the String to be hashed
   * @return hash value
   */
  public static String applySha256(String input) {

    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");

      // Applies sha256 to our input,
      byte[] hash = digest.digest(input.getBytes("UTF-8"));

      StringBuffer hexString = new StringBuffer(); // This will contain hash as hexadecimal
      for (int i = 0; i < hash.length; i++) {
        String hex = Integer.toHexString(0xff & hash[i]);
        if (hex.length() == 1) hexString.append('0');
        hexString.append(hex);
      }
      return hexString.toString();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Short hand helper to turn Object into a json string
   * 
   * @param o a object to reformat to json
   * @return Json representation of the object
   */
  public static String getJson(Object o) {
    return new GsonBuilder().setPrettyPrinting().create().toJson(o);
  }

  /**
   * Returns difficulty string target, to compare to hash. eg difficulty of 5 will
   * return "00000"
   * 
   * @param difficulty the length of the difficulty string
   * @return the difficulty string, consisting of '0's
   */
  public static String getDifficultyString(int difficulty) {
    return new String(new char[difficulty]).replace('\0', '0');
  }
}
