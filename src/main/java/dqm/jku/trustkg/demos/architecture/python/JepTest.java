package dqm.jku.trustkg.demos.architecture.python;

import jep.Interpreter;
import jep.JepException;
import jep.SharedInterpreter;

public class JepTest {
	public static void main(String[] args) {
		// Tested on Python 3.8.6
		try (Interpreter interp = new SharedInterpreter()) {
	    interp.exec("from java.lang import System");
	    interp.exec("s = 'Hello World'");
	    interp.exec("System.out.println(s)");
	    interp.exec("print(s)");
	    interp.exec("print(s[1:3])");
	    interp.exec("System.out.println(s[1:3])");
	} catch (JepException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
}
