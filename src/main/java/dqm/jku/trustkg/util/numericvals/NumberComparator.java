package dqm.jku.trustkg.util.numericvals;

import java.util.Comparator;

/**
 * A comparator specifically built for comparing numbers when calculating data profiling metrics
 * @author optimusseptim
 *
 */
public class NumberComparator implements Comparator<Number>{

  @Override
  public int compare(Number a, Number b) {
    if (a.doubleValue() > b.doubleValue()) return 1;
    else if (a.doubleValue() < b.doubleValue()) return -1;
    return 0;
  }

}
