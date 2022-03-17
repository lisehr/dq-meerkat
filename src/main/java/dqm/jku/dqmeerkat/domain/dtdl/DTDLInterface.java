package dqm.jku.dqmeerkat.domain.dtdl;

import java.util.ArrayList;
import java.util.List;

/**
 * <h2>DTDLInterface</h2>
 * <summary>TODO Insert do cheader</summary>
 *
 * @author meindl
 * @since 17.03.2022
 */
public class DTDLInterface extends DTDLObject {
    private String id;
    private final String context = "dtmi:dtdl:context;2";
    private List<DTDLObject> contents = new ArrayList<>();

    public DTDLInterface(String id) {
        this.id = id;
        this.type = DTDLType.INTERFACE;
    }
}
