package dqm.jku.dqmeerkat.api.rest.client.tributech;

import com.google.api.client.auth.oauth2.Credential;
import dqm.jku.dqmeerkat.api.rest.client.oauth2.ClientCredentialsRestClient;
import dqm.jku.dqmeerkat.domain.tributech.IntDataSample;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

/**
 * <h2>DataApiTestClient</h2>
 * <summary>TODO Insert do cheader</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 25.07.2022
 */
public class DataApiTestClient extends ClientCredentialsRestClient<IntDataSample> {
    private final WebClient client;

    public DataApiTestClient(String tokenServerUrl, String authorizationServerUrl, String clientId, String clientSecret, String baseUrl) {
        super(tokenServerUrl, authorizationServerUrl, clientId, clientSecret, baseUrl);
        Credential token = this.authorize(List.of("profile", "email", "data-api", "node-id"));
        this.client = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + token.getAccessToken())
                .build();
    }

    @Override
    public IntDataSample get(String url) {
        return client.get().uri(baseUrl + url).retrieve().bodyToMono(IntDataSample.class).block();
    }

    @Override
    public IntDataSample get() {
        return null;
    }

    @Override
    public List<IntDataSample> getMultiple() {
        return client.get()
                .retrieve()
                .bodyToFlux(IntDataSample.class)
                .collectList()
                .block();
    }

    @Override
    public List<IntDataSample> getMultiple(String url) {
        return client.get()
                .uri(baseUrl + url)
                .retrieve()
                .bodyToFlux(IntDataSample.class)
                .collectList()
                .block();
    }

    @Override
    public void post(String url, IntDataSample body) {

    }

    @Override
    public void post(IntDataSample body) {

    }
}
