package dqm.jku.trustkg.util.python;

import jep.Interpreter;
import jep.JepException;
import jep.NDArray;
import jep.SharedInterpreter;

public class JepInterpreter {
	
	public static SharedInterpreter getInterpreter() throws JepException {
		return new SharedInterpreter();
	}
	public static void initFunctionsAndLibs(Interpreter py) throws JepException {
		// Libs
		py.exec("import numpy as np"); // has to be 1.19.3 on windows machines (fmod() bug)
		py.exec("import matplotlib.pyplot as plt"); // # graphical library for plotting
		py.exec("import statistics"); // # to calculate some medians...
		py.exec("from pyod.utils.data import generate_data, get_outliers_inliers"); // # to generate data
		
		// Functions
		py.exec("def robust_scaler(x):\n\t med = statistics.median(x)\n\t return (x - med)/np.mean(abs(x - med))");
	}
	
	public static void printNDArr(NDArray<?> arr) {
		for (int i = 0; i < arr.getDimensions()[0]; i++) {
			if (arr.getDimensions().length == 1) System.out.print(((double[]) arr.getData())[i] + "\t");
			else {
				for (int j = 0; j < arr.getDimensions()[1]; j++) {
					System.out.print(((double[]) arr.getData())[arr.getDimensions()[0] * j + i] + "\t");
				}
			}
			System.out.println();
		}
	}
}
