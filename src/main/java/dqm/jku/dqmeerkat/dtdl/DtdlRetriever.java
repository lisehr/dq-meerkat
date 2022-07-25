package dqm.jku.dqmeerkat.dtdl;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.Throwables;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import dqm.jku.dqmeerkat.dtdl.dto.DtdlDto;
import dqm.jku.dqmeerkat.dtdl.dto.DtdlGraphWrapper;
import lombok.SneakyThrows;
import org.springframework.web.reactive.function.client.WebClient;
import science.aist.seshat.Logger;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

/**
 * <h2>DtdlRetriever</h2>
 * <summary>Retrieves a twin defined in DTDL from a rest endpoint</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 27.04.2022
 */
public class DtdlRetriever {
    private static final Logger LOGGER = Logger.getInstance();
    private static final String TOKEN_SERVER_URL = "https://auth.int.dataspace-hub.com/auth/realms/int-node-b/protocol/openid-connect/token";
    private static final String AUTHORIZATION_SERVER_URL =
            "https://auth.int.dataspace-hub.com/auth/realms/int-node-b/protocol/openid-connect/auth";
    private final WebClient client;
    private final String clientId = "twin-api";
    private final String clientSecret = "8a823e8a-e993-4bdd-9ffd-3794ddecaeec";
    private Credential oauth2Credential;


    public DtdlRetriever() {
        this("https://twin-api.int-node-b.dataspace-node.com/twins");
    }

    public DtdlRetriever(String url) {
        try {
            FileDataStoreFactory dataStoreFactory = new FileDataStoreFactory(new File("./tmp/tokens"));
//            // set up authorization code flow
//            AuthorizationCodeFlow flow = new AuthorizationCodeFlow.Builder(BearerToken
//                    .authorizationHeaderAccessMethod(),
//                    new NetHttpTransport(),
//                    new GsonFactory(),
//                    new GenericUrl(TOKEN_SERVER_URL),
//                    new ClientParametersAuthentication(
//                            clientId, clientSecret),
//                    clientId,
//                    AUTHORIZATION_SERVER_URL).setScopes(List.of("profile", "email", "twin-api", "catalog-api"))
//                    .setDataStoreFactory(dataStoreFactory).build();
//            // authorize
//            var receiver = new VerificationCodeReceiver() {
//                final Semaphore waitUnlessSignaled = new Semaphore(0 /* initially zero permit */);
//                String code;
//                String error;
//                private HttpServer server;
//
//                @Override
//                public String getRedirectUri() throws IOException {
//                    server = HttpServer.create(new InetSocketAddress(8080), 0);
//                    HttpContext context = server.createContext("/", new CallbackHandler());
//                    server.setExecutor(null);
//
//                    try {
//                        server.start();
//                    } catch (Exception e) {
//                        Throwables.propagateIfPossible(e);
//                        throw new IOException(e);
//                    }
//                    // TODO this callback dies with swagger error in the browser console
//                    return "https://twin-api.int-node-b.dataspace-node.com/oauth2-redirect.html";
//                }
//
//                @Override
//                public String waitForCode() throws IOException {
//                    waitUnlessSignaled.acquireUninterruptibly();
//                    if (error != null) {
//                        throw new IOException("User authorization failed (" + error + ")");
//                    }
//                    return code;
//                }
//
//                @Override
//                public void stop() throws IOException {
//                    waitUnlessSignaled.release();
//                    if (server != null) {
//                        try {
//                            server.stop(0);
//                        } catch (Exception e) {
//                            Throwables.propagateIfPossible(e);
//                            throw new IOException(e);
//                        }
//                        server = null;
//                    }
//                }
//
//                class CallbackHandler implements HttpHandler {
//                    @Override
//                    public void handle(HttpExchange httpExchange) throws IOException {
//                        try {
//                            Map<String, String> parms = this.queryToMap(httpExchange.getRequestURI().getQuery());
//                            error = parms.get("error");
//                            code = parms.get("code");
//
//                            httpExchange.close();
//                        } finally {
//                            waitUnlessSignaled.release();
//                        }
//                    }
//
//                    private Map<String, String> queryToMap(String query) {
//                        Map<String, String> result = new HashMap<String, String>();
//                        if (query != null) {
//                            for (String param : query.split("&")) {
//                                String[] pair = param.split("=");
//                                if (pair.length > 1) {
//                                    result.put(pair[0], pair[1]);
//                                } else {
//                                    result.put(pair[0], "");
//                                }
//                            }
//                        }
//                        return result;
//                    }
//                }
//            };
//            oauth2Credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize(null);

        } catch (IOException e) {
            // TODO do not continue if authorization fails
            LOGGER.error("Could not create authorization flow. Continuing for now...", e);

        }
        if (oauth2Credential == null) {
//            throw new IllegalStateException("Could not create authorization flow");
            client = WebClient.builder()
                    .baseUrl(url)
                    .defaultHeader("Authorization", "Bearer " + "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJOQUJRUF93VXRtdmQ4ZS1KMmZyRkdPd25wQTNLSDJfSG5PQ1d3ZzVuQ3RjIn0.eyJleHAiOjE2NTg3NDE5MzAsImlhdCI6MTY1ODc0MTYzMCwiYXV0aF90aW1lIjoxNjU4NzM5MzM3LCJqdGkiOiI4NjM3ZGM5Yi02ODc0LTQ5MmUtOWE2MS1lZDlkNjMyMTI4OTgiLCJpc3MiOiJodHRwczovL2F1dGguaW50LmRhdGFzcGFjZS1odWIuY29tL2F1dGgvcmVhbG1zL2ludC1ub2RlLWIiLCJhdWQiOlsidHdpbi1hcGkiLCJjYXRhbG9nLWFwaSIsInJlYWxtLW1hbmFnZW1lbnQiXSwic3ViIjoiN2VjYzRlOWMtM2VlNy00NGFhLWI2YjUtODQ0YzdiOTVjZDA2IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoidHdpbi1hcGkiLCJzZXNzaW9uX3N0YXRlIjoiZjhhMDNhNWYtNjkwNC00YWJmLThiMTEtZGJjNDU0YzQ0OTFmIiwiYWNyIjoiMCIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwczovL3R3aW4tYXBpLmludC1ub2RlLWIuZGF0YXNwYWNlLW5vZGUuY29tIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsImFkbWluIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJyZWFsbS1tYW5hZ2VtZW50Ijp7InJvbGVzIjpbIm1hbmFnZS11c2VycyIsInZpZXctdXNlcnMiLCJxdWVyeS1ncm91cHMiLCJxdWVyeS11c2VycyJdfX0sInNjb3BlIjoicHJvZmlsZSBjYXRhbG9nLWFwaSBlbWFpbCBub2RlLWlkIHR3aW4tYXBpIiwibm9kZS1pZCI6IjgyMDcwMzA5LWEyMDEtNGU3Ni04NjcwLTI1MWFiZTkxZjhhZSIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJub2RlLW5hbWUiOiJpbnQtbm9kZS1iIiwibmFtZSI6IlJhaW5lciBNZWluZGwiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJyYWluZXIubWVpbmRsQHNjY2guYXQiLCJnaXZlbl9uYW1lIjoiUmFpbmVyIiwiZmFtaWx5X25hbWUiOiJNZWluZGwiLCJlbWFpbCI6InJhaW5lci5tZWluZGxAc2NjaC5hdCJ9.j49wmP8mjP-sWIGxpra1ISw7_o3fS4tFiGJZfkvMpLtGicHYAJMFh_q3VC6e8Ehk7YrHYaIcxkaYV-VO1KlhuZYbBxUCUHwtjrYngHILZ6wS4AI0ilfrP7HNqOTbN7hNxEdrNWeq4YoPyr5XBEhSKc2nljn_hBHToxX-g5GuME4Ft-zUUxaHoqSKyKKYknfCsjTu_HPmj88nngQ0EuelS8OKN6McNwENdek7dDWDwD_uJCiHx21y3hW2fsQrpAYMQI9-gs4BWxAWlh5CRepVNdL_7IpWOulL-16VsW7bfvqOsgS6DzXMUbboEUaCjFOljlgqAi8uoKeSnM_VXNpWMA")
                    .build();
        } else {
            client = WebClient.builder()
                    .baseUrl(url)
                    .defaultHeader("Authorization", "Bearer " + oauth2Credential.getAccessToken())
                    .build();
        }
    }

    public DtdlGraphWrapper get() {
        return client.get()
                .retrieve()
                .bodyToMono(DtdlGraphWrapper.class)
                .block();
    }

    /**
     * <p>
     * transforms the given dto into json and posts it on the URL defined in the constructor
     * </p>
     *
     * @param dto
     */
    @SneakyThrows
    public void post(DtdlGraphWrapper dto) {
        // Dummy class, necessary to suppress type info in dtdldtos.
        @JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
        class NoTypes {
        }
        // without mixin jackson generates a type property for each DtdlDto implementation, causes problems with validity
        ObjectMapper mapper = new ObjectMapper().addMixIn(DtdlDto.class, NoTypes.class);


        client.post()
                .bodyValue(mapper.writeValueAsString(dto))
                .retrieve()
                .toBodilessEntity() // ignore the response
                .block();


    }


}
