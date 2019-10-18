package dqm.jku.trustkg.util.numericvals;

public class NumberValueUtils {
  
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
