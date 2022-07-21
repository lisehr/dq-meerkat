package dqm.jku.dqmeerkat.quality;

import com.influxdb.client.domain.WritePrecision;
import dqm.jku.dqmeerkat.dsd.elements.*;
import dqm.jku.dqmeerkat.dsd.records.Record;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.generator.DataProfileSkeletonGenerator;
import dqm.jku.dqmeerkat.quality.generator.FilePatternRecognitionGenerator;
import dqm.jku.dqmeerkat.quality.generator.FullSkeletonGenerator;
import dqm.jku.dqmeerkat.quality.generator.IsolationForestSkeletonGenerator;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle;
import dqm.jku.dqmeerkat.quality.profilingstatistics.graphmetrics.*;
import dqm.jku.dqmeerkat.util.Miscellaneous.DBType;
import dqm.jku.dqmeerkat.util.numericvals.NumberComparator;
import org.cyberborean.rdfbeans.annotations.*;
import org.influxdb.dto.Point;
import org.influxdb.dto.Point.Builder;

import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.*;

/**
 * Class for creating the data structure of a DataProfile. A DataProfile (DP)
 * can contain multiple statistics {@link dqm.jku.dqmeerkat.quality.profilingstatistics}, which are calulated and evaluated
 *
 * @author optimusseptim
 */
@RDFNamespaces({"dsd = http://dqm.faw.jku.at/dsd#"})
@RDFBean("dsd:quality/structures/DataProfile")
public class DataProfile {
    private List<ProfileStatistic> statistics = new ArrayList<>(); // a list containing all profile metrics
    private DSDElement elem; // DSDElement, where this DataProfile is annotated to
    private List<DataProfileSkeletonGenerator> generators;
    private String uri; // uniform resource identifier of this profile

    public DataProfile() {

    }

    public DataProfile(RecordList rs, DSDElement d, DataProfileSkeletonGenerator... generators) throws NoSuchMethodException {
        this.elem = d;
        this.generators = Arrays.stream(generators).collect(Collectors.toList());
        this.uri = elem.getURI() + "/profile";

        Optional<Datasource> ds = DSDElement.getAllDatasources().stream().findFirst();

        // TODO add neo4j data generator, remove this differentiation
        if (ds.isPresent() && ds.get().getDBType().equals(DBType.NEO4J)) {
            createDataProfileSkeletonNeo4j();
            calculateReferenceDataProfileNeo4j(rs);
        } else {
            createDataProfileSkeletonRDB();
            calculateReferenceDataProfile(rs);
        }
    }

    public DataProfile(RecordList rs, DSDElement d) throws NoSuchMethodException {
        this(rs, d, new FullSkeletonGenerator(), new IsolationForestSkeletonGenerator());
    }


    public DataProfile(RecordList records, DSDElement d, String filePath)
            throws NoSuchMethodException {
        this(records, d, new FullSkeletonGenerator(), new IsolationForestSkeletonGenerator(),
                new FilePatternRecognitionGenerator(filePath));
    }


    /**
     * calculates an initial data profile based on the values inserted in the
     * reference profile
     *
     * @param rl the list of records used for calculation
     * @throws NoSuchMethodException
     */
    private void calculateReferenceDataProfile(RecordList rl) throws NoSuchMethodException {
        if (elem instanceof Attribute) calculateSingleColumn(rl);
        else if (elem instanceof Concept) calculateMultiColumn(rl);
    }

    /**
     * Gets the URI
     *
     * @return uri
     */
    @RDFSubject
    public String getURI() {
        return uri;
    }

    /**
     * Sets the URI (security threat but used by rdfbeans)
     *
     * @param uri
     */
    public void setURI(String uri) {
        this.uri = uri;
    }

    /**
     * Helper method for calculating the single column values for the profile
     *
     * @param rl the recordlist for measuring
     * @throws NoSuchMethodException
     */
    private void calculateSingleColumn(RecordList rl) throws NoSuchMethodException {
        List<Number> l = createValueList(rl);
        for (ProfileStatistic p : statistics) {
            if (needsRecordListCalc(p)) p.calculation(rl, p.getValue());
            else p.calculationNumeric(l, p.getValue());
        }
    }

    private void calculateMultiColumn(RecordList rl) {
        for (ProfileStatistic p : statistics) {
            p.calculation(rl, p.getValue());
        }
    }

    /**
     * Helper method to determine if a record list calculation is needed (i.e. a
     * list of numeric values can lead to incorrect metrics)
     *
     * @param p the profile metric to check
     * @return true, if the profile metric needs to be calculated with a record
     * list, false if not
     */
    private boolean needsRecordListCalc(ProfileStatistic p) {
        return p.getTitle().equals(unique) || p.getTitle().equals(keyCand) || p.getTitle().equals(nullValP) || p.getTitle().equals(nullVal) || p.getTitle().equals(numrows) || p.getTitle().equals(card) || p.getTitle().equals(hist);
    }

    /**
     * Helper method for creating a list of numeric values
     *
     * @param rl the record list for measuring
     * @return list of numeric values of the records
     */
    private List<Number> createValueList(RecordList rl) {
        List<Number> list = new ArrayList<Number>();
        Attribute a = (Attribute) elem;
        for (Record r : rl) {
            Number field = null;
            Class<?> clazz = a.getDataType();
            if ((String.class.isAssignableFrom(clazz) || Date.class.isAssignableFrom(clazz)) && r.getField(a) != null)
                field = r.getField(a).toString().length();
            else if (a.getConcept().getDatasource().getDBType().equals(DBType.CSV)) field = (Number) r.getField(a);
            else if (a.getConcept().getDatasource().getDBType().equals(DBType.MYSQL) || a.getConcept().getDatasource().getDBType().equals(DBType.PENTAHOETL)) {
                if (Number.class.isAssignableFrom(clazz)) field = (Number) r.getField(a);
            }
            if (field != null)
                list.add(field);
        }
        list.sort(new NumberComparator());
        return list;
    }

    /**
     * Method to create a reference data profile on which calculations can be
     * made. The structure is defined by the generator, passed during object instantiation
     */
    public void createDataProfileSkeletonRDB() {
        statistics = generators.stream()
                .flatMap(generator1 -> generator1.generateSkeleton(this).stream())
                .collect(Collectors.toList());
    }

    /**
     * Method for printing out the data profile.
     */
    public void printProfile() {
        System.out.println("Data Profile:");
        if (statistics.stream().anyMatch(p -> p.getValueClass().equals(String.class)))
            System.out.println("Strings use String length for value length metrics!");

        // TODO: Check if only printing the outlier indices makes sense --> therefore the following line is temporarily commented out
        //if (metrics.stream().anyMatch(p -> p.getCat().equals(MetricCategory.out))) System.out.println("Outlier Detection shows all record numbers that contain outliers!");
        SortedSet<ProfileStatistic> metricSorted = new TreeSet<>(statistics);
        for (ProfileStatistic p : metricSorted) {
            System.out.println(p.toString());
        }
        System.out.println();
    }

    /**
     * Method for printing out the data profile as a string.
     */
    public String getProfileString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Data Profile:");
        sb.append('\n');
        if (statistics.size() == 0) sb.append("Datatype currently not supported for RDP creation!");
        if (statistics.stream().anyMatch(p -> p.getValueClass().equals(String.class))) {
            sb.append("Strings use String length for value length metrics!");
            sb.append('\n');
        }
        SortedSet<ProfileStatistic> metricSorted = new TreeSet<>();
        metricSorted.addAll(statistics);
        for (ProfileStatistic p : metricSorted) {
            sb.append(p.toString());
            sb.append('\n');
        }
        sb.append('\n');
        return sb.toString();
    }

    /**
     * Method for getting the set of metrics
     *
     * @return set of metrics
     */
    @RDF("dsd:includesMetric")
    @RDFContainer
    public List<ProfileStatistic> getStatistics() {
        return statistics;
    }

    /**
     * Method for getting the set of metrics
     *
     * @return set of selected metrics
     */
    public List<ProfileStatistic> getNonDependentStatistics() {
        List<ProfileStatistic> mlist = new ArrayList<ProfileStatistic>();
        // Cardinalities
        // No number of rows and metrics that depend on the num rows (RDP size != DP size)
        mlist.add(this.getStatistic(StatisticTitle.card));
        mlist.add(this.getStatistic(StatisticTitle.nullVal));

        // Data type info
        mlist.add(this.getStatistic(StatisticTitle.bt));
        mlist.add(this.getStatistic(StatisticTitle.dt));
        mlist.add(this.getStatistic(StatisticTitle.min));
        mlist.add(this.getStatistic(StatisticTitle.max));
        mlist.add(this.getStatistic(StatisticTitle.avg));
        mlist.add(this.getStatistic(StatisticTitle.med));
        mlist.add(this.getStatistic(StatisticTitle.sd));
        mlist.add(this.getStatistic(StatisticTitle.mad));
        mlist.add(this.getStatistic(StatisticTitle.dig));
        mlist.add(this.getStatistic(StatisticTitle.dec));
        if (this.getStatistic(StatisticTitle.pattern) != null) {
            mlist.add(this.getStatistic(StatisticTitle.pattern));
        }

        // Histogram
        mlist.add(this.getStatistic(StatisticTitle.hist));

        // Dependencies
        mlist.add(this.getStatistic(keyCand));

        return mlist.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Method for getting all metrics that are useful for single records (e.g., in a data stream) and do not rely on any kind of aggregation
     *
     * @return set of selected metrics
     */
    public List<ProfileStatistic> getNonAggregateStatistics() {
        List<ProfileStatistic> mlist = new ArrayList<ProfileStatistic>();
        // Cardinalities
        // No number of rows and metrics that depend on the num rows (RDP size != DP size)
        mlist.add(this.getStatistic(StatisticTitle.nullVal));

        // Data type info
        mlist.add(this.getStatistic(StatisticTitle.bt));
        mlist.add(this.getStatistic(StatisticTitle.dt));
        mlist.add(this.getStatistic(StatisticTitle.min));
        mlist.add(this.getStatistic(StatisticTitle.max));

        mlist.add(this.getStatistic(StatisticTitle.dig));
        if (this.getStatistic(StatisticTitle.pattern) != null) {
            mlist.add(this.getStatistic(StatisticTitle.pattern));
        }

        // Dependencies
        mlist.add(this.getStatistic(keyCand));

        return mlist;
    }

    /**
     * Sets the metrics (security threat but used by rdfbeans)
     *
     * @param statistics the metrics to set
     */
    public void setStatistics(List<ProfileStatistic> statistics) {
        this.statistics = statistics;
    }

    /**
     * Method for adding a metric via a data profile object
     *
     * @param m the metric to be added
     */
    public void addStatistic(ProfileStatistic m) {
        this.statistics.add(m);
    }

    /**
     * Method for getting a specific ProfileStatistic with its corresponding label.
     *
     * @param title the label to compare with the profile metrics
     * @return ProfileMetric if found, null otherwise
     */
    public ProfileStatistic getStatistic(StatisticTitle title) {
        for (ProfileStatistic m : statistics) {
            if (m.getTitle().equals(title)) return m;
        }
        return null;
    }

    /**
     * Sets the dsd element (security threat but used by rdfbeans)
     *
     * @param elem the elem to set
     */
    public void setElem(DSDElement elem) {
        this.elem = elem;
    }

    /**
     * Gets the reference dsd element, used for calculation
     *
     * @return the reference element
     */
    @RDF("dsd:annotatedTo")
    public DSDElement getElem() {
        return elem;
    }

    /**
     * Method for creating a point of measure for InfluxDB.
     *
     * @param measure the builder for a measurement
     * @return a measuring point for insertion into InfluxDB
     */
    @Deprecated
    public Point createMeasuringPoint(Builder measure) {
        SortedSet<ProfileStatistic> metricSorted = new TreeSet<>();
        metricSorted.addAll(statistics);
        for (ProfileStatistic p : metricSorted) {
            if (!p.getTitle().equals(hist) && !p.getTitle().equals(mad))
                addMeasuringValue(p, measure);
        }
        return measure.build();
    }

    /**
     * <p>Transforms the statistics into measuring points for InfluxDB 2.x to be persisted. </p>
     *
     * @param measurementDescriptor description or name of the point to create
     * @param timestampMillis       time, when the data point has been taken
     * @param writePrecision        Precision of time to be written
     * @return com.influxdb.client.write.Point that can be persisted using a
     * {@link dqm.jku.dqmeerkat.influxdb.InfluxDBConnectionV2}
     */
    public com.influxdb.client.write.Point createMeasuringPoint(String measurementDescriptor, long timestampMillis,
                                                                WritePrecision writePrecision) {
        SortedSet<ProfileStatistic> metricSorted = new TreeSet<>(statistics);
        com.influxdb.client.write.Point point = new com.influxdb.client.write.Point(measurementDescriptor)
                .time(timestampMillis, writePrecision);
        for (ProfileStatistic p : metricSorted) {
            if (!p.getTitle().equals(hist) && !p.getTitle().equals(mad))
                addMeasuringValue(p, point);
        }
        return point;
    }

    /**
     * Helper method for adding the correct measuring value (including its data
     * type) to the builder
     *
     * @param p       the profile metric to add
     * @param measure the builder for a measurement
     */
    private void addMeasuringValue(ProfileStatistic p, com.influxdb.client.write.Point measure) {
        try {
            // TODO refactor for readability and extensability when using generics in ProfileStatistics
            if (p.getValue() == null)
                measure.addField(p.getLabel(), 0); // TODO: replace 0 with NaN, when hitting v2.0 of influxdb
            else if (p.getValueClass().equals(Long.class))
                measure.addField(p.getLabel(), ((Number) p.getNumericVal()).longValue());
            else if (p.getValueClass().equals(Double.class) || p.getLabel().equals(sd.getLabel()) ||
                    p.getLabel().equals(summary.getLabel()))
                measure.addField(p.getLabel(), ((Number) p.getNumericVal()).doubleValue());
            else if (p.getValueClass().equals(String.class) && (p.getLabel().equals(bt.getLabel()) || p.getLabel().equals(dt.getLabel())))
                measure.addField(p.getLabel(), (String) p.getValue());
            else if (p.getValueClass().equals(Boolean.class)) measure.addField(p.getLabel(), (boolean) p.getValue());
            else measure.addField(p.getLabel(), ((Number) p.getNumericVal()).intValue());
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    /**
     * Helper method for adding the correct measuring value (including its data
     * type) to the builder
     *
     * @param p       the profile metric to add
     * @param measure the builder for a measurement
     */
    private void addMeasuringValue(ProfileStatistic p, Builder measure) {
        try {
            if (p.getValue() == null || p.getLabel().equals(pattern.getLabel()))
                measure.addField(p.getLabel(), 0); // TODO: replace 0 with NaN, when hitting v2.0 of influxdb
            else if (p.getValueClass().equals(Long.class))
                measure.addField(p.getLabel(), ((Number) p.getNumericVal()).longValue());
            else if (p.getValueClass().equals(Double.class) || p.getLabel().equals(sd.getLabel()))
                measure.addField(p.getLabel(), ((Number) p.getNumericVal()).doubleValue());
            else if (p.getValueClass().equals(String.class) && (p.getLabel().equals(bt.getLabel()) || p.getLabel().equals(dt.getLabel())))
                measure.addField(p.getLabel(), (String) p.getValue());
            else if (p.getValueClass().equals(Boolean.class)) measure.addField(p.getLabel(), (boolean) p.getValue());
            else measure.addField(p.getLabel(), ((Number) p.getNumericVal()).intValue());
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    public boolean profileStatisticIsNumeric(ProfileStatistic profileStatistic) {
        return profileStatistic.getTitle() == avg || profileStatistic.getTitle() == sd || profileStatistic.getTitle() == max || profileStatistic.getTitle() == min || profileStatistic.getTitle() == med
                || profileStatistic.getTitle() == nullValP || profileStatistic.getTitle() == unique;

    }

    // ------------------- Graph Data Profiling ------------------------

    public void createDataProfileSkeletonNeo4j() {
        // distinguish between concept, attribute and datasource??
        if (elem instanceof Concept) {
            Concept c = (Concept) elem;

            ReferenceAssociation association = (ReferenceAssociation) c.getDatasource().getAssociation("Ref/" + c.getLabelOriginal());
            String neoType = association.getNeo4JType();

            ProfileStatistic size = new NumEntries(this);
            statistics.add(size);
            ProfileStatistic distinctEntries = new DistinctEntries(this);
            statistics.add(distinctEntries);
            ProfileStatistic type = new GraphType(this);
            statistics.add(type);
            ProfileStatistic maximum = new MaximumEntry(this);
            statistics.add(maximum);
            ProfileStatistic minimum = new MinimumEntry(this);
            statistics.add(minimum);
            ProfileStatistic median = new MedianEntry(this);
            statistics.add(median);

        }
    }

    private void calculateReferenceDataProfileNeo4j(RecordList rl) throws NoSuchMethodException {
        // distinguish between multi column and single column profiling

        if (elem instanceof Concept) {
            Concept c = (Concept) elem;
            calculateSingleColumnNeo4j(rl);
        }
    }

    private void calculateSingleColumnNeo4j(RecordList rl) throws NoSuchMethodException {
        for (ProfileStatistic p : statistics) {
            p.calculation(rl, p.getValue());
        }
    }


}
