package dqm.jku.trustkg.util;

import java.util.concurrent.TimeUnit;

public class Miscellaneous {
	
	public enum DBType { UNDEFINED, CSV, MYSQL, ORACLE, CASSANDRA, NEO4J }

	public static String getTimeStatistics(long from) {
		long to = System.nanoTime();
		long nanos = to - from;
		long minutes = TimeUnit.NANOSECONDS.toMinutes(nanos);
		nanos -= TimeUnit.MINUTES.toNanos(minutes);
		long seconds = TimeUnit.NANOSECONDS.toSeconds(nanos);
		nanos -= TimeUnit.SECONDS.toNanos(seconds);
		long millis = TimeUnit.NANOSECONDS.toMillis(nanos);
		nanos -= TimeUnit.MILLISECONDS.toNanos(millis);

		return String.format("Execution time: %02d min, %02d sec, %02d ms", minutes, seconds, millis);
	}

}
