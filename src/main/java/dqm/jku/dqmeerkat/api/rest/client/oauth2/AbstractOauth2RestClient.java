package dqm.jku.dqmeerkat.api.rest.client.oauth2;

import com.google.api.client.auth.oauth2.Credential;
import dqm.jku.dqmeerkat.api.rest.client.AbstractRestClient;

import java.util.List;

/**
 * <h2>AbstractRestClient</h2>
 * <summary>TODO Insert do cheader</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 25.07.2022
 */
public abstract class AbstractOauth2RestClient<T> extends AbstractRestClient<T> {
    protected final String tokenServerUrl;
    protected final String authorizationServerUrl;
    protected final String clientId;
    protected final String clientSecret;


    protected AbstractOauth2RestClient(String tokenServerUrl, String authorizationServerUrl, String clientId,
                                       String clientSecret, String baseUrl) {
        super(baseUrl);
        this.tokenServerUrl = tokenServerUrl;
        this.authorizationServerUrl = authorizationServerUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    protected abstract Credential authorize(List<String> scopes);
}
