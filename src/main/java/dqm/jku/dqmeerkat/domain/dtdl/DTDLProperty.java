package dqm.jku.dqmeerkat.domain.dtdl;

/**
 * <h2>DTDLProperty</h2>
 * <summary>TODO Insert do cheader</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 17.03.2022
 */
public class DTDLProperty extends DTDLObject {
    private String name;
    private boolean writable;

    public DTDLProperty() {
        this.type = DTDLType.PROPERTY;
    }
}
