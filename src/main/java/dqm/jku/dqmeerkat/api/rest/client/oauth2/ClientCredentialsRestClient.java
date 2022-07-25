package dqm.jku.dqmeerkat.api.rest.client.oauth2;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientCredentialsTokenRequest;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.BasicAuthentication;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.SneakyThrows;

import java.util.List;

/**
 * <h2>ClientCredentialsRestClient</h2>
 * <summary>TODO Insert do cheader</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 25.07.2022
 */
public abstract class ClientCredentialsRestClient<T> extends AbstractOauth2RestClient<T> {
    public ClientCredentialsRestClient(String tokenServerUrl, String authorizationServerUrl, String clientId,
                                       String clientSecret, String baseUrl, String redirectUri) {
        super(tokenServerUrl, authorizationServerUrl, clientId, clientSecret, baseUrl, redirectUri);
    }

    @SneakyThrows
    @Override
    protected Credential authorize(List<String> scopes) {
        var tokenResponse = new ClientCredentialsTokenRequest(new NetHttpTransport(),
                new GsonFactory(), new GenericUrl(tokenServerUrl))
                .setClientAuthentication(
                        new BasicAuthentication("data-api", "b3d1c827-1008-4436-97c2-68398f6143a4"))
                .setScopes(scopes)
                .execute();
        var credential = new Credential.Builder(BearerToken.authorizationHeaderAccessMethod()).build();
        credential.setAccessToken(tokenResponse.getAccessToken());

        return credential;
    }
}
