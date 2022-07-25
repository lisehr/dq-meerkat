package dqm.jku.dqmeerkat.api.rest.client;

import science.aist.seshat.Logger;

import java.io.Serializable;

/**
 * <h2>AbstratRestClient</h2>
 * <summary>TODO Insert do cheader</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 25.07.2022
 */
public abstract class AbstractRestClient<T> {
    protected static final Logger LOGGER = Logger.getInstance();

    protected final String baseUrl;

    protected AbstractRestClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }


    public abstract T get(String url);

    public abstract T get();

    public abstract void post(String url, T body);

    public abstract void post(T body);

}
