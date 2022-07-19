package dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.histogram;

import dqm.jku.dqmeerkat.dsd.elements.Attribute;
import dqm.jku.dqmeerkat.dsd.records.Record;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.quality.profilingstatistics.DependentProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.ProfileStatistic;
import dqm.jku.dqmeerkat.quality.profilingstatistics.singlecolumn.cardinality.NumRows;
import dqm.jku.dqmeerkat.util.Constants;
import dqm.jku.dqmeerkat.util.numericvals.NumberComparator;
import dqm.jku.dqmeerkat.util.numericvals.ValueDistributionUtils;
import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import java.util.ArrayList;
import java.util.List;

import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticCategory.histCat;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.hist;
import static dqm.jku.dqmeerkat.quality.profilingstatistics.StatisticTitle.numrows;


/**
 * Describes the metric Histogram, which is a value distribution (equi-width).
 *
 * @author optimusseptim
 */
@RDFNamespaces({"dsd = http://dqm.faw.jku.at/dsd#"})
@RDFBean("dsd:quality/structures/metrics/histogram/Histogram")
public class Histogram extends DependentProfileStatistic<SerializableFrequencyMap> {
    private Number min; // minimum value
    private Number max; // maximum value
    private Number classrange; // range of equi-width classes


    public Histogram(DataProfile d) {
        super(hist, histCat, d);
    }

    @Override
    public void calculation(RecordList rs, SerializableFrequencyMap oldVal) {
        this.dependencyCalculationWithRecordList(rs);
        Attribute a = (Attribute) super.getRefElem();
        List<Number> list = new ArrayList<Number>();
        for (Record r : rs) {
            Number field;
            if (a.getDataType().equals(String.class) && r.getField(a) != null) {
                field = r.getField(a).toString().length();
            } else {
                field = (Number) r.getField(a);
            }
            if (field != null) {
                list.add(field);
            }
        }
        processList(list, null);
        this.setValueClass(SerializableFrequencyMap.class);
    }

    /**
     * Helping method for creating histogram from a list
     *
     * @param list the list to be processed
     */
    private void processList(List<Number> list, SerializableFrequencyMap vals) {
        if (list.isEmpty()) {
            this.setValue(null);
            return;
        }
        list.sort(new NumberComparator());

        if (min == null) {
            min = list.get(0).doubleValue();
        } else {
            min = Math.min(min.doubleValue(), list.get(0).doubleValue());
        }

        if (max == null) {
            max = list.get(list.size() - 1).doubleValue();
        } else {
            max = Math.max(max.doubleValue(), list.get(list.size() - 1).doubleValue());
        }

        int k = ValueDistributionUtils.calculateNumberClasses((long) super.getRefProf().getStatistic(numrows).getValue());

        classrange = (max.doubleValue() - min.doubleValue()) / k;

        int[] classVals;
        if (vals == null) {
            classVals = new int[k];
        } else {
            classVals = constructArray();
        }
        for (Number n : list) {
            if (n.doubleValue() == max.doubleValue()) {
                classVals[k - 1]++;
            } else {
                classVals[(int) Math.floor((n.doubleValue() - min.doubleValue()) / classrange.doubleValue())]++;
            }
        }
        SerializableFrequencyMap classes = new SerializableFrequencyMap(this.getUri());
        for (int i = 0; i < k; i++) classes.put(i, classVals[i]);
        this.setValue(classes);
    }

    @Override
    public void update(RecordList rs) {
        Attribute a = (Attribute) super.getRefElem();
        List<Number> list = new ArrayList<Number>();
        for (Record r : rs) {
            Number field;
            if (a.getDataType().equals(String.class) && r.getField(a) != null) {
                field = ((String) r.getField(a)).length();
            } else {
                field = (Number) r.getField(a);
            }
            if (field != null) list.add(field);
        }
        processList(list, super.getValue());
    }

    /**
     * Generates an array to handle the Map easier
     *
     * @return frequency array
     */
    private int[] constructArray() {
        if (super.getValue() == null) throw new IllegalStateException("Map has to exist here!");
        int k = getNumberOfClasses();
        int[] classes = new int[k];
        int j = 0;
        for (Integer i : super.getValue().values()) {
            classes[j] = i;
            j++;
        }
        return classes;
    }

    @Override
    protected String getValueString() {
        if (super.getValue() == null) return "\tnull";
        StringBuilder sb = new StringBuilder().append("\tNumber of classes: ");
        int k = ValueDistributionUtils.calculateNumberClasses((long) super.getRefProf().getStatistic(numrows).getValue());
        sb.append(k);
        sb.append(", ClassRange: ");
        sb.append(classrange);
        sb.append(", Values: ");
        sb.append(getClassValues());
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    /**
     * Gets the minimum
     *
     * @return the min
     */
    @RDF("dsd:hasMin")
    public Number getMin() {
        return min;
    }

    /**
     * Sets the minimum (security threat but needed by rdfbeans)
     *
     * @param min the min to set
     */
    public void setMin(Number min) {
        this.min = min;
    }

    /**
     * Gets the maximum
     *
     * @return the max
     */
    @RDF("dsd:hasMax")
    public Number getMax() {
        return max;
    }

    /**
     * Sets the maximum (security threat but needed by rdfbeans)
     *
     * @param max the max to set
     */
    public void setMax(Number max) {
        this.max = max;
    }

    /**
     * Gets the classrange
     *
     * @return the classrange
     */
    @RDF("dsd:hasClassrange")
    public Number getClassrange() {
        return classrange;
    }

    /**
     * Sets the classrange (security threat but needed by rdfbeans)
     *
     * @param classrange the classrange to set
     */
    public void setClassrange(Number classrange) {
        this.classrange = classrange;
    }

    /**
     * calculate the number of classes
     *
     * @return number of classes
     */
    public int getNumberOfClasses() {
        return ValueDistributionUtils.calculateNumberClasses((long) super.getRefProf().getStatistic(numrows).getValue());
    }

    /**
     * Get string representations of class bins
     *
     * @return string of class bins
     */
    public String getClassValues() {
        StringBuilder sb = new StringBuilder();
        for (Integer i : super.getValue().values()) {
            sb.append(i);
            sb.append("-");
        }
        return sb.toString();
    }

    /**
     * Get string representations of class bins in format for CSV files
     *
     * @return string of class bins in format for CSV files
     */
    public String getClassValuesCSV() {
        if (super.getValue() == null) return "";
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (Integer i : super.getValue().values()) {
            sb.append(i);
            sb.append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        sb.append("]");
        return sb.toString();
    }

    @Override
    protected void dependencyCalculationWithRecordList(RecordList rl) {
        if (super.getMetricPos(hist) - 2 <= super.getMetricPos(numrows)) {
            super.getRefProf().getStatistic(numrows).calculation(rl, null);
        }
    }

    @Override
    protected void dependencyCheck() {
        var sizeM = super.getRefProf().getStatistic(numrows);
        if (sizeM == null) {
            sizeM = new NumRows(super.getRefProf());
            super.getRefProf().addStatistic(sizeM);
        }
    }

    @Override
    public boolean checkConformance(ProfileStatistic<SerializableFrequencyMap> m, double threshold) {
        int rdpVal = this.getNumberOfClasses();
        int dpValue = this.getNumberOfClasses();

        double lowerBound = rdpVal - (Math.abs(rdpVal) * threshold);
        double upperBound = rdpVal + (Math.abs(rdpVal) * threshold);

        boolean conf = dpValue >= lowerBound && dpValue <= upperBound;
        if (!conf && Constants.DEBUG) {
            System.out.println(this.getTitle() + " exceeded: " + dpValue + " not in [" + lowerBound + ", " + upperBound + "]");
        }
        return conf;
    }

}
