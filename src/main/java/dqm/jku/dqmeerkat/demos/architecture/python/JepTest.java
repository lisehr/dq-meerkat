package dqm.jku.dqmeerkat.demos.architecture.python;

import dqm.jku.dqmeerkat.util.python.JepInterpreter;
import jep.Interpreter;
import jep.JepException;

public class JepTest {
	public static void main(String[] args) {
		// Tested on Python 3.8.6
		try (Interpreter interp = JepInterpreter.getInterpreter()) {
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
