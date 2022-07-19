package dqm.jku.dqmeerkat.quality.profilingstatistics.multicolumn.outliers;

import de.lmu.ifi.dbs.elki.algorithm.Algorithm;
import de.lmu.ifi.dbs.elki.algorithm.outlier.lof.LOF;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.StaticArrayDatabase;
import de.lmu.ifi.dbs.elki.datasource.ArrayAdapterDatabaseConnection;
import de.lmu.ifi.dbs.elki.datasource.DatabaseConnection;
import de.lmu.ifi.dbs.elki.result.outlier.OutlierResult;
import de.lmu.ifi.dbs.elki.utilities.ClassGenericsUtil;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.ListParameterization;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;
import dqm.jku.dqmeerkat.util.converters.DoubleArrayConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory.out;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.lof;
import static dqm.jku.dqmeerkat.util.GenericsUtil.cast;


/**
 * A multicolumn profilemetric which computes the Local Outlier Factor
 * using the Library "Elki".
 * <p>
 * The LOF algorithm itself is described in https://doi.org/10.1145/335191.335388
 *
 * @author Johannes Schrott
 */

public class LocalOutlierFactor extends ProfileStatistic<List<Double>> {

    final private static float FACTOR_FOR_K = 0.1F; // Must be between 0 and 1
    final private static double OUTLIER_THRESHOLD = 1.1;


    public LocalOutlierFactor(DataProfile dp) {
        super(lof, out, dp);
    }

    @Override
    public void calculation(RecordList rs, List<Double> oldVal) {
        if (rs.size() == 0)
            throw new IllegalArgumentException("An empty RecordList was passed as an argument. For a calculation, the RecordList must contain elements.");

        double[][] data = DoubleArrayConverter.extractNumericAttributesToDoubleArray(rs);

        // Create "Database" for use with ELKI; this steps were used: https://elki-project.github.io/howto/java_api#creating-a-database-from-a-double-array
        DatabaseConnection dbc = new ArrayAdapterDatabaseConnection(data);
        Database database = new StaticArrayDatabase(dbc, null);
        database.initialize();

        // Choosing: int k for the algorithm
        // according to https://doi.org/10.1145/335191.335388 (section 6) a minimum of 10 is advised
        // in order to achieve best results, all the values between the minimum k and the maximum k should computed
        // and the minimum, the maximum or the mean of this results should then be taken.
        // As Computing LOF for all this k Values would take too long, we only compute it for the minimum (k=10), the maximum (k=length of data * 10%) and the mean value of this two.
        // In the end the LOFs of this three k Values are taken and the mean value is returned as result.

        int kMin = 10;
        int kMax = Math.max(10, Math.round(data.length * FACTOR_FOR_K));
        int kMiddle = (kMin + kMax) / 2;

        int[] kValues = {kMin, kMiddle, kMax};

        // For each k compute the LOF values
        List<ArrayList<Double>> results = Arrays.stream(kValues)
                .parallel()
                .mapToObj(k -> {
                    ListParameterization params = new ListParameterization();
                    params.addParameter(LOF.Parameterizer.K_ID, k);

                    // Run the LOF Algorithm
                    Algorithm alg = ClassGenericsUtil.parameterizeOrAbort(LOF.class, params);
                    OutlierResult result = (OutlierResult) alg.run(database);

                    // Extract the computed results to an Array List.
                    ArrayList<Double> resultList = new ArrayList<>();
                    result.getScores().forEachDouble((id, v) -> resultList.add(v)); // we don't need the results internal id!
                    return resultList;
                }).collect(Collectors.toList());

        // Merge all the results from the different k-Values to one resultlist containg the mean LOF for each record
        ArrayList<Double> resultList = results.get(0);
        IntStream.range(0, rs.size()).parallel().forEach(i -> { // for every resultList
            IntStream.range(1, results.size()).parallel().forEach(j -> { // do for every index
                resultList.set(i, (results.get(j).get(i) + resultList.get(i)) / 2);
            });
        });

        this.setValueClass(cast(ArrayList.class));
        this.setValue(resultList);


    }

    @Override
    public void update(RecordList rs) {
        this.calculation(rs, null);
    }

    @Override
    protected String getValueString() {

        ArrayList<Double> results = (ArrayList<Double>) getValue();

        long noOfOutliers = results.stream().filter(d -> d > OUTLIER_THRESHOLD).count();
        double maxLOF = results.stream().mapToDouble(Double::doubleValue).max().orElse(1);
        double minLOF = results.stream().mapToDouble(Double::doubleValue).min().orElse(1);

        return this.getSimpleValueString() +
                "\nNo of outliers: " +
                noOfOutliers + " of " + results.size() + " records.\n" +
                (double) noOfOutliers / results.size() * 100 + "% are outliers\n" +
                maxLOF + " is the maximum LOF\n" +
                minLOF + " is the minimum LOF";
    }

    @Override
    public boolean checkConformance(ProfileStatistic<List<Double>> m, double threshold) {        // TODO Auto-generated method stub
        return false;
    }

}
