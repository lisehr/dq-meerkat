package dqm.jku.dqmeerkat.dtdl;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.Credential;
import dqm.jku.dqmeerkat.api.rest.client.oauth2.AbstractOauth2RestClient;
import dqm.jku.dqmeerkat.dtdl.dto.DtdlDto;
import dqm.jku.dqmeerkat.dtdl.dto.DtdlGraphWrapper;
import lombok.SneakyThrows;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

/**
 * <h2>DtdlRetriever</h2>
 * <summary>Retrieves a twin defined in DTDL from a rest endpoint</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 27.04.2022
 */
public class DtdlRetriever extends AbstractOauth2RestClient<DtdlGraphWrapper> {
    private static final String TOKEN_SERVER_URL = "https://auth.int.dataspace-hub.com/auth/realms/int-node-b/protocol/openid-connect/token";
    private static final String AUTHORIZATION_SERVER_URL =
            "https://auth.int.dataspace-hub.com/auth/realms/int-node-b/protocol/openid-connect/auth";
    private static final String clientId = "twin-api";
    private static final String clientSecret = "8a823e8a-e993-4bdd-9ffd-3794ddecaeec";
    private final WebClient client;


    public DtdlRetriever() {
        this("https://twin-api.int-node-b.dataspace-node.com/twins");
    }

    public DtdlRetriever(String url) {
        super(TOKEN_SERVER_URL, AUTHORIZATION_SERVER_URL, clientId, clientSecret, url);
        Credential oauth2Credential = authorize(List.of("profile", "email", "twin-api", "catalog-api"));
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

    @Override
    public DtdlGraphWrapper get(String url) {
        return client.get()
                .uri(baseUrl + url)
                .retrieve()
                .bodyToMono(DtdlGraphWrapper.class)
                .block();
    }

    public DtdlGraphWrapper get() {
        return client.get()
                .retrieve()
                .bodyToMono(DtdlGraphWrapper.class)
                .block();
    }

    @SneakyThrows
    @Override
    public void post(String url, DtdlGraphWrapper body) {
        // Dummy class, necessary to suppress type info in dtdldtos.
        @JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
        class NoTypes {
        }
        // without mixin jackson generates a type property for each DtdlDto implementation, causes problems with validity
        ObjectMapper mapper = new ObjectMapper().addMixIn(DtdlDto.class, NoTypes.class);

        client.post()
                .uri(baseUrl + url)
                .bodyValue(mapper.writeValueAsString(body))
                .retrieve()
                .toBodilessEntity() // ignore the response
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
