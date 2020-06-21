package dqm.jku.trustkg.util.numericvals;

/**
 * Utility class for working with Number values
 * 
 * @author optimusseptim
 *
 */
public class NumberValueUtils {

  /**
   * Methods to count the digits of a number
   * 
   * @param n the number to be calculated from
   * @return number of digits
   */
  public static int countDigits(Number n) {
    int i = 0;
    int number = n.intValue();
    if (number < 0) number *= -1;

    while (number > 0) {
      number /= 10;
      i++;
    }
    return i;
  }

}
