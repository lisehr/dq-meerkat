package dqm.jku.trustkg.util.numericvals;

import java.util.Comparator;

public class NumberComparator implements Comparator<Number>{

  @Override
  public int compare(Number a, Number b) {
    if (a.doubleValue() > b.doubleValue()) return 1;
    if (a.doubleValue() < b.doubleValue()) return -1;
    return 0;
  }

}
