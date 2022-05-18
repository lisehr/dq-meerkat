package dqm.jku.dqmeerkat.util;

/**
 * <h2>StringUtil</h2>
 * <p>Utility class for additional logic to handle strings</p>
 *
 * @author Rainer Meindl, rainer.meindl@scch.at
 * @since 19.01.2022
 **/
public class StringUtil {
    /**
     * <p>
     * checks if the given string is null or empty
     * </p>
     *
     * @param str string, that might be null or empty (i.E. "")
     * @return true iff the given string is either null or empty.
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }
}
