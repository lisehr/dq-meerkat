package dqm.jku.dqmeerkat.quality.profilingstatistics;

import dqm.jku.dqmeerkat.dsd.elements.DSDElement;
import dqm.jku.dqmeerkat.dsd.records.RecordList;
import dqm.jku.dqmeerkat.quality.DataProfile;
import lombok.Getter;
import lombok.Setter;
import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * <h2>ProfileStatistics</h2>
 * <summary>TODO Insert do cheader</summary>
 *
 * @author meindl, rainer.meindl@scch.at,
 * optimusseptim
 * @since 12.07.2022
 */
@RDFNamespaces({"dsd = http://dqm.faw.jku.at/dsd#"})
@RDFBean("dsd:quality/structures/ProfileMetric")
public abstract class ProfileStatistic<T> implements Comparable<ProfileStatistic<T>> {
    protected StatisticTitle title; // the naming of the metric
    protected StatisticCategory cat; // name of metric category
    @Getter
    @Setter
    protected Class<?> valueClass; // the class of the value
    @Getter
    @Setter
    protected T value; // the value itself
    // TODO move it to own subclass? (e.g. ProfileStatisticNumeric<T extends Number>)
    @Setter
    protected Object numericVal; // numeric representation (e.g. double for values that can be integer as well), also string for unified and recognizable formats for RDFBeans for ex.
    @Setter
    protected DataProfile refProf; // reference profile for calculations

    protected String uri; // uri of the metric

    public ProfileStatistic(StatisticTitle title, StatisticCategory cat, DataProfile refProf) {
        if (title == null || refProf == null) throw new IllegalArgumentException("Parameters cannot be null!");
        this.title = title;
        this.refProf = refProf;
        this.cat = cat;
        this.uri = refProf.getURI() + '/' + this.title.getLabel().replaceAll("\\s+", "");
        value = null;
        numericVal = null;
    }


    /**
     * Method for calculating the profile metric, overridden by each metric
     *
     * @param oldVal a oldValue to be updated, null for initial calculation
     * @param rs     the recordset used for calculation
     */
    public abstract void calculation(RecordList rs, T oldVal);

    /**
     * Method for updating the metric value, overriden by each metric
     *
     * @param rs the recordset used for updating
     */
    public abstract void update(RecordList rs);

    /**
     * Returns a string representation of the metric value
     *
     * @return string repr of value
     */
    protected abstract String getValueString();

    /**
     * Method for creating a simple string representation of the metric value
     *
     * @return string repr of value
     */
    protected String getSimpleValueString() {
        if (getValue() == null) return "\tnull";
        else return "\t" + getValue().toString();
    }


    /**
     * Returns true or false, depending on whether the metric of a current DP conforms to the value in the RDP
     *
     * @return boolean conformance to RDP value
     */
    public abstract boolean checkConformance(ProfileStatistic<T> m, double threshold);

    /**
     * Gets the reference dsd element, used for calculation
     *
     * @return the reference element
     */
    public DSDElement getRefElem() {
        return refProf.getElem();
    }

    /**
     * Directly gets the text of the title
     *
     * @return label of title
     */
    public String getLabel() {
        return title.getLabel();
    }

    @RDF("dsd:hasValue")
    public Object getNumericVal() {
        return numericVal;
    }

    /**
     * Gets the title
     *
     * @return title of metric
     */
    @RDF("dsd:hasTitle")
    public StatisticTitle getTitle() {
        return title;
    }

    @RDF("dsd:isInCategory")
    public StatisticCategory getCat() {
        return cat;
    }

    @RDFSubject
    public String getUri() {
        return uri;
    }

    /**
     * Gets a String representation of the value class, used for rdf transformation
     *
     * @return string of value class
     */
    @RDF("dsd:isInValueClass")
    public String getValueClassString() {
        return this.valueClass.getName();
    }

    /**
     * Sets the value Class via the class string, passed as parameter
     *
     * @param valClass the string representation of the class
     */
    public void setValueClassString(String valClass) {
        try {
            this.valueClass = Class.forName(valClass);
        } catch (ClassNotFoundException e) {
            System.err.println("Class not found!");
        }
    }

    /**
     * Gets the reference DataProfile
     *
     * @return the reference profile
     */
    @RDF("dsd:isIncludedIn")
    public DataProfile getRefProf() {
        return refProf;
    }

    @Override
    public String toString() {
        if (value == null) return String.format("%s\tnull", title);
        else if (title.getLabel().length() < 8) return String.format("%s\t%s", title, getValueString());
        else return String.format("%s%s", title, getValueString());
    }

    /**
     * Method for returning the position of a Profilemetric in the collection of
     * this profile
     *
     * @param t the title of the metric
     * @return position if found, -1 otherwise
     */
    public int getMetricPos(StatisticTitle t) {
        List<AbstractProfileStatistic> metrics = this.getRefProf().getStatistics();
        for (int i = 0; i < metrics.size(); i++) if (metrics.get(i).getLabel().equals(t.getLabel())) return i;
        return -1;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProfileStatistic)) return false;
        ProfileStatistic<?> that = (ProfileStatistic<?>) o;
        return title == that.title &&
                valueClass.equals(that.valueClass) &&
                Objects.equals(value, that.value) &&
                uri.equals(that.uri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, valueClass, value, uri);
    }

    @Override
    public int compareTo(@NotNull ProfileStatistic<T> o) {
        return getLabel().compareTo(o.getLabel());
    }

    /**
     * Method for calculating the profile metric, overridden by each metric.
     * TODO possibly handle by implementation like NumberProfileStatistic?
     *
     * @param oldVal a oldValue to be updated, null for initial calculation
     * @param list   a sorted list, containing all values
     * @throws NoSuchMethodException in cases like null values, since here records
     *                               are not allowed for processing
     */
    public abstract void calculationNumeric(List<Number> list, Object oldVal) throws NoSuchMethodException;
}
