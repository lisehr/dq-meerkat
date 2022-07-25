package dqm.jku.dqmeerkat.api.rest.client.oauth2;

import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
import com.google.api.client.util.Throwables;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import lombok.Getter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

/**
 * <h2>ConfigurableVerificationReceiver</h2>
 * <summary>
 * {@link VerificationCodeReceiver} implementation for OAuth 2.0 AuthorizationCodeFlow implementations. Hosts a
 * simple HTTP server that listens on a random port and waits for a verification code from the Authorization Server.
 * The RestClient can then use the verification code to obtain an access token. An example implementation is shown in
 * {@link AbstractOauth2RestClient}.
 * </summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc6749#section-4.1"> OAuth 2.0 Authorization Code Grant</a>
 * @since 25.07.2022
 */
public class ConfigurableVerificationReceiver implements VerificationCodeReceiver {
    final Semaphore waitUnlessSignaled = new Semaphore(0 /* initially zero permit */);
    @Getter
    private final String redirectUri;
    @Getter
    private final int port;
    String code;
    String error;
    private HttpServer server;

    public ConfigurableVerificationReceiver(String redirectUri) {
        this(redirectUri, 8080);
    }

    public ConfigurableVerificationReceiver(String redirectUri, int port) {
        this.redirectUri = redirectUri;
        this.port = port;
    }


    @Override
    public String getRedirectUri() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        HttpContext context = server.createContext("/", new CallbackHandler());
        server.setExecutor(null);

        try {
            server.start();
        } catch (Exception e) {
            Throwables.propagateIfPossible(e);
            throw new IOException(e);
        }
        // TODO this callback dies with swagger error in the browser console
        return redirectUri;
    }

    @Override
    public String waitForCode() throws IOException {
        waitUnlessSignaled.acquireUninterruptibly();
        if (error != null) {
            throw new IOException("User authorization failed (" + error + ")");
        }
        return code;
    }

    @Override
    public void stop() throws IOException {
        waitUnlessSignaled.release();
        if (server != null) {
            try {
                server.stop(0);
            } catch (Exception e) {
                Throwables.propagateIfPossible(e);
                throw new IOException(e);
            }
            server = null;
        }
    }

    class CallbackHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            try {
                Map<String, String> parms = this.queryToMap(httpExchange.getRequestURI().getQuery());
                error = parms.get("error");
                code = parms.get("code");

                httpExchange.close();
            } finally {
                waitUnlessSignaled.release();
            }
        }

        private Map<String, String> queryToMap(String query) {
            Map<String, String> result = new HashMap<>();
            if (query != null) {
                for (String param : query.split("&")) {
                    String[] pair = param.split("=");
                    if (pair.length > 1) {
                        result.put(pair[0], pair[1]);
                    } else {
                        result.put(pair[0], "");
                    }
                }
            }
            return result;
        }
    }
}

