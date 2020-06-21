package dqm.jku.trustkg.util;

/**
 * 
 * @author optimusseptim
 *
 *         Utility class for using try-parsers, a concept found in C#, but had
 *         to be implemented in Java first
 */
public class TryParsers {

  /**
   * Tries if parsing an integer is possible
   * 
   * @param s the string to be parsed
   * @return true, if integer, false otherwise
   */
  public static boolean tryParseInt(String s) {
    try {
      Integer.parseInt(s);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  /**
   * Tries if parsing a long is possible
   * 
   * @param s the string to be parsed
   * @return true, if long, false otherwise
   */
  public static boolean tryParseLong(String s) {
    try {
      Long.parseLong(s);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  /**
   * Tries if parsing a double is possible
   * 
   * @param s the string to be parsed
   * @return true, if double, false otherwise
   */
  public static boolean tryParseDouble(String s) {
    try {
      Double.parseDouble(s);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }
}
