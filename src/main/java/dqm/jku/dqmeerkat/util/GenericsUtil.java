package dqm.jku.dqmeerkat.util;

/**
 * <h2>GenericsUtil</h2>
 * <summary>TODO Insert do cheader</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 12.07.2022
 */
public class GenericsUtil {
    public static <T> T cast(Object o) {
        return (T) o;
    }
}
