package dqm.jku.dqmeerkat.quality.conformance;

import java.io.IOException;

/**
 * <h2>RDPConformanceChecker</h2>
 * <summary>Interface for any conformance checking of reference data profiles (RDP). RDPs should be provided in
 * constructors, along with other configuration</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 20.04.2022
 */
public interface RDPConformanceChecker {


    /**
     * run the conformance check and initialise the internal components to generate a report, when necessary
     *
     * @throws NoSuchMethodException when checking instances of the dsd elements
     * @throws IOException           when accessing (csv)data
     */
    void runConformanceCheck() throws NoSuchMethodException, IOException;

    /**
     * returns the report as string, after it has been initialised by the  runConformanceCheck method
     *
     * @return String containing the report in a human-readable format
     */
    String getReport();
}
