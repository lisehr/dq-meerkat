package dqm.jku.dqmeerkat.quality.profilingmetrics.multicolumn.outliers;

import static dqm.jku.dqmeerkat.quality.profilingmetrics.MetricCategory.*;
import static dqm.jku.dqmeerkat.quality.profilingmetrics.MetricTitle.*;

import java.util.ArrayList;
import java.util.List;

import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingmetrics.ProfileMetric;
import dqm.jku.dqmeerkat.util.converters.DoubleArrayConverter;

import de.lmu.ifi.dbs.elki.algorithm.outlier.lof.LOF;
import de.lmu.ifi.dbs.elki.algorithm.Algorithm;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.StaticArrayDatabase;
import de.lmu.ifi.dbs.elki.datasource.ArrayAdapterDatabaseConnection;
import de.lmu.ifi.dbs.elki.datasource.DatabaseConnection;
import de.lmu.ifi.dbs.elki.result.outlier.OutlierResult;
import de.lmu.ifi.dbs.elki.utilities.ClassGenericsUtil;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.ListParameterization;


/**
 * A multicolumn profilemetric which computes the Local Outlier Factor
 * using the Library "Elki".
 *
 * The LOF algorithm itself is described in https://doi.org/10.1145/335191.335388
 *
 * @author Johannes Schrott
 */

public class LocalOutlierFactor extends ProfileMetric {

    public LocalOutlierFactor() {

    }

    public LocalOutlierFactor(DataProfile dp) {
        super(lof, out, dp);
    }

    @Override
    public void calculation(RecordList rs, Object oldVal) {
        if (rs.size() == 0)
            throw new IllegalArgumentException("An empty RecordList was passed as an argument. For a calculation, the RecordList must contain elements.");

        double[][] data = DoubleArrayConverter.extractNumericAttributesToDoubleArray(rs);

        // Create "Database" for use with ELKI; this steps were used: https://elki-project.github.io/howto/java_api#creating-a-database-from-a-double-array
        DatabaseConnection dbc = new ArrayAdapterDatabaseConnection(data);
        Database database = new StaticArrayDatabase(dbc, null);
        database.initialize();
        // TODO --> Lizenz Problem Elki: AGPL ??#

        int k = data.length / 50; // TODO: muss gut ausgewählt werden (ggfs auch abhängig von der Anzahl an Elementen?
        // wenn zu kleinen hohe Statistische Schwankungen --> mind 10 sei epmpfohlen
        // wenn abhängig von anzahl an records, dann sind ergebnisse bei unterschiedlichen Batch-größen nicht mehr vergleichbar, da ja ein anderes k verwendet wird.
        // k abhängig von der Anzahl

        // Setup parameters for the LOF
        ListParameterization params = new ListParameterization();
        params.addParameter(LOF.Parameterizer.K_ID, k);

        // Run the LOF Algorithm
        Algorithm alg = ClassGenericsUtil.parameterizeOrAbort(LOF.class, params);
        OutlierResult result = (OutlierResult) alg.run(database);

        // Extract the computed results to an Array List.
        ArrayList<Double> resultList = new ArrayList<>();
        result.getScores().forEachDouble((id, v) -> resultList.add(v)); // we don't need the results internal id!

        this.setValueClass(ArrayList.class);
        this.setValue(resultList);

        // TODO:
        System.out.println("A limit from which it is considered an outlier has yet to be set!"); // oder sollte man automatisch die höchsten 10% als Ausreißer betrachten?


    }

    @Override
    public void calculationNumeric(List<Number> list, Object oldVal) throws NoSuchMethodException {
        throw new NoSuchMethodException("calculationNumeric does not work for multicolumn metrics, as a list of values cannot be represented by a single number.");
    }



    @Override
    public void update(RecordList rs) {
        this.calculation(rs, null);
    }

    @Override
    protected String getValueString() {
        // TODO Auto-generated method stub
        return this.getSimpleValueString();
    }

    @Override
    public boolean checkConformance(ProfileMetric m, double threshold) {
        // TODO Auto-generated method stub

        //
        return false;
    }

}
