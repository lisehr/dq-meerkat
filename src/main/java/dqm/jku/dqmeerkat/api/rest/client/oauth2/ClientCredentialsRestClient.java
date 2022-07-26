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
 * <summary>
 * {@link AbstractOauth2RestClient} subclass for OAuth2 authorisation using client credentials.
 * </summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc6749#section-2.3">OAuth 2.0 Client Authentication</a>
 * @since 25.07.2022
 */
public abstract class ClientCredentialsRestClient<T> extends AbstractOauth2RestClient<T> {
    public ClientCredentialsRestClient(String tokenServerUrl, String authorizationServerUrl, String clientId,
                                       String clientSecret, String baseUrl) {
        super(tokenServerUrl, authorizationServerUrl, clientId, clientSecret, baseUrl);
    }

    /**
     * Authorizes the client according to OAuth2.0. client credentials flow. The authorisation token is stored in
     * an uninitialized Credential object. The only truly safe method to call in this token is to getAccessToken(),
     * in which the token is stored.
     *
     * @param scopes the scopes to authorize for
     * @return an uninitialized Credential object, which can be used to get the access token using the getAccessToken()
     * method. Other methods may not be safe to call
     */
    @SneakyThrows
    @Override
    protected Credential authorize(List<String> scopes) {
        var tokenResponse = new ClientCredentialsTokenRequest(new NetHttpTransport(),
                new GsonFactory(), new GenericUrl(tokenServerUrl))
                .setClientAuthentication(
                        new BasicAuthentication(clientId, clientSecret))
                .setScopes(scopes)
                .execute();
        // Create an uninitialized Credential object and return it after setting the previously retrieved token.
        return new Credential
                .Builder(BearerToken.authorizationHeaderAccessMethod())
                .build()
                .setAccessToken(tokenResponse.getAccessToken());
    }
}
