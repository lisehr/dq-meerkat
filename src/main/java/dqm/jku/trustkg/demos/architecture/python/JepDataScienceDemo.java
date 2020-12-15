package dqm.jku.trustkg.demos.architecture.python;

import dqm.jku.trustkg.util.python.JepInterpreter;
import jep.Interpreter;
import jep.JepException;
import jep.NDArray;

public class JepDataScienceDemo {
	public static void main(String[] args) {
		// Tested on Python 3.8.6
		// Taken from Python Outlier Detection Demo by M.Bechny
		try (Interpreter interp = JepInterpreter.getInterpreter()) {
			interp.exec("import numpy as np"); // has to be 1.19.3 on windows machines (fmod() bug)
			interp.exec("import matplotlib.pyplot as plt"); // # graphical library for plotting
			interp.exec("import statistics"); // # to calculate some medians...
			interp.exec("from pyod.utils.data import generate_data, get_outliers_inliers"); // # to generate data
			interp.exec("contamination = 0.1"); // # percentage of outliers
			System.out.println(interp.getValue("contamination"));
			interp.exec("n_train = 300"); // # number of training points
			System.out.println(interp.getValue("n_train"));
			interp.exec("n_test = 0"); // # number of testing points (not used in the notebook)
			System.out.println(interp.getValue("n_test"));
			interp.exec("X_train, Y_train, X_test, Y_test = generate_data(n_train=n_train, n_test=n_test, contamination=contamination)");
			interp.eval("X_train.shape");
			System.out.println(interp.eval("jep.JEP_NUMPY_ENABLED"));
			interp.exec("a = np.float32(333.333)");
			System.out.println(interp.getValue("a"));
			NDArray<?> arr = (NDArray<?>) interp.getValue("X_train[:10,:]");
			JepInterpreter.printNDArr(arr);
			JepInterpreter.printNDArr((NDArray<?>) interp.getValue("Y_train"));
			
			
			// define a function
			interp.exec("def robust_scaler(x):\n\t med = statistics.median(x)\n\t return (x - med)/np.mean(abs(x - med))");

			// use function
			interp.exec("robust_scaler(X_train[:,[1]])");
			interp.exec("X_train_scaled = np.apply_along_axis(robust_scaler, 0, X_train)");
			JepInterpreter.printNDArr((NDArray<?>)interp.getValue("X_train_scaled[:10,:]"));
			
			// print outliers
			interp.exec("plt.scatter(X_train_scaled[:,[0]], X_train_scaled[:,[1]], c = ['red' if (x > 0)  else 'green' for x in Y_train ])");
			interp.exec("plt.title('True outliers in the scaled data')");
			interp.exec("plt.xlabel('X1')");
			interp.exec("plt.ylabel('X2')");
			interp.exec("plt.show()"); // plot works on some machines 

		} catch (JepException e) {
			e.printStackTrace();
		}
	}
	


}
