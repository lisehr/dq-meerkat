package dqm.jku.dqmeerkat.util;

public class Constants {

	public static final boolean STORE_CONSTRAINT_VIOLATIONS = true;
	public static final boolean DEBUG = false;
	public static final boolean SIMILARITY_DEBUG = false;
	public static final boolean PRINT_WORKING_MESSAGES = false;
	public static final boolean ENABLE_JEP = false; // if true JEP (Java Embedded Python) is activated (please make sure
													// it is working and installed when setting this to true!)

	// Todo: WN_HOME can be removed?
	public static final String WN_HOME = "src/resources/wordnet"; // location of wordnet

	/* Resources constants */

	public static final String RESOURCES_FOLDER = "src/main/java/dqm/jku/dqmeerkat/resources/";

	/**
	 * This enum provides easy access to the file paths of the most used resource
	 * files.
	 */
	public enum FileName {
		// Paths in are relative to the RESOURCES_FOLDER constant!
		acceleration("csv/Acceleration.csv"), dataCoSupplyChainDataset("csv/DataCoSupplyChainDataset.csv"),
		popularBabyNames("csv/Popular_Baby_Names.csv"), salesRecords("csv/100000 Sales Records.csv");

		private final String relativePath;

		FileName(String relativePath) {
			this.relativePath = relativePath;
		}

		@Override
		public String toString() {
			return relativePath;
		}

		/**
		 *
		 * @return The path from the project root to the file
		 */
		public String getPath() {
			return RESOURCES_FOLDER + relativePath;
		}
	}

	/* Time Constants */

	public final static long MILLIS_IN_SEC = 1000;
	public final static long SEC_IN_MIN = 60;
	public final static long MIN_IN_HOUR = 60;
	public final static long HOUR_IN_DAY = 24;
	public final static long DAY_IN_MONTH = 30;
	public final static long DAY_IN_YEAR = 365;

	/* Graph Constants */

	public final static String DEFAULT_URI = "http://example.com";
	public final static String DEFAULT_PREFIX = "ex:";

}
