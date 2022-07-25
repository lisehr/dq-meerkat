package dqm.jku.dqmeerkat.api.rest.client;

import com.google.api.client.auth.oauth2.Credential;
import dqm.jku.dqmeerkat.api.rest.client.oauth2.ClientCredentialsRestClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

/**
 * <h2>DataApiTestClient</h2>
 * <summary>TODO Insert do cheader</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 25.07.2022
 */
public class DataApiTestClient extends ClientCredentialsRestClient<Object> {
    private final Credential token;
    private final WebClient client;

    public DataApiTestClient(String tokenServerUrl, String authorizationServerUrl, String clientId, String clientSecret, String baseUrl, String redirectUri) {
        super(tokenServerUrl, authorizationServerUrl, clientId, clientSecret, baseUrl, redirectUri);
        this.token = this.authorize(List.of("profile", "email", "data-api", "node-id"));
        this.client = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + token.getAccessToken())
                .build();
    }

    @Override
    public Object get(String url) {
        return client.get().uri(baseUrl + url).retrieve().bodyToMono(Object.class).block();
    }

    @Override
    public Object get() {
        return null;
    }

    @Override
    public void post(String url, Object body) {

    }

    @Override
    public void post(Object body) {

    }
}
