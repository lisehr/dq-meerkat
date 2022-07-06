package dqm.jku.dqmeerkat.quality.config;

import lombok.Getter;

/**
 * <h2>DataSummaryConfigComponent</h2>
 * TODO implement me
 */
@Getter
public class DataSummaryConfigComponent extends ConfigComponent {
    private final int k;

    public DataSummaryConfigComponent(int k) {
        this.k = k;
    }



}
