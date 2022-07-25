package dqm.jku.dqmeerkat.api.rest.client.oauth2;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import dqm.jku.dqmeerkat.api.rest.client.AbstractRestClient;
import lombok.SneakyThrows;

import java.io.File;
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
    protected final String redirectUri;


    protected AbstractOauth2RestClient(String tokenServerUrl, String authorizationServerUrl, String clientId,
                                       String clientSecret, String baseUrl, String redirectUri) {
        super(baseUrl);
        this.tokenServerUrl = tokenServerUrl;
        this.authorizationServerUrl = authorizationServerUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;

    }

    @SneakyThrows
    protected Credential authorize(List<String> scopes) {
        FileDataStoreFactory dataStoreFactory = new FileDataStoreFactory(new File("./tmp/tokens"));
        AuthorizationCodeFlow flow = new AuthorizationCodeFlow.Builder(BearerToken
                .authorizationHeaderAccessMethod(),
                new NetHttpTransport(),
                new GsonFactory(),
                new GenericUrl(tokenServerUrl),
                new ClientParametersAuthentication(clientId, clientSecret),
                clientId,
                authorizationServerUrl)
                .setScopes(scopes)
                .setDataStoreFactory(dataStoreFactory)
                .build();

        var receiver = new ConfigurableVerificationReceiver(redirectUri);
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize(null);
    }
}
