package dqm.jku.dqmeerkat.util.numericvals;

/**
 * Utility class for working with value distributions
 * 
 * @author optimusseptim
 *
 */
public class ValueDistributionUtils {
  /**
   * Calculates the number of classes of a histogram via the sturges rule (k = 1 +
   * 3.32 * log10(N))
   * 
   * @param size the size of the dataset
   * @return amount of classes
   */
  public static int calculateNumberClasses(long size) {
    return (int) Math.ceil(1 + (3.32 * Math.log10((double) size)));
  }

}
