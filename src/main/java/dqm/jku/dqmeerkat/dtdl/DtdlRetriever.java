package dqm.jku.dqmeerkat.dtdl;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import dqm.jku.dqmeerkat.dtdl.dto.DtdlDto;
import dqm.jku.dqmeerkat.dtdl.dto.DtdlGraphWrapper;
import lombok.SneakyThrows;
import org.springframework.web.reactive.function.client.WebClient;
import science.aist.seshat.Logger;

/**
 * <h2>DtdlRetriever</h2>
 * <summary>Retrieves a twin defined in DTDL from a rest endpoint</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 27.04.2022
 */
public class DtdlRetriever {
    private static final Logger LOGGER = Logger.getInstance();
    private final WebClient client = WebClient.create("https://twin-api.play-tributech.dataspace-node.com/");
    private final String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJtX1dLVjZlU2dzdGhEc0NZamtXMDBBWXNpLXJXR2o2R3BaNzhlZVJVZ1JBIn0.eyJleHAiOjE2NTIxODc0MTksImlhdCI6MTY1MjE4NzExOSwiYXV0aF90aW1lIjoxNjUyMTgxOTk4LCJqdGkiOiJhZmRmMjllZi0wOGMzLTQ2YTEtYTlkYy1jMTAwM2Y0NjA2ZWQiLCJpc3MiOiJodHRwczovL2F1dGgucGxheS5kYXRhc3BhY2UtaHViLmNvbS9hdXRoL3JlYWxtcy9wbGF5LXRyaWJ1dGVjaCIsImF1ZCI6WyJ0d2luLWFwaSIsImNhdGFsb2ctYXBpIiwicmVhbG0tbWFuYWdlbWVudCJdLCJzdWIiOiI5OTVkMWRlZC02YTZhLTQ4MTYtYWU4ZS1iYTg5OWE4MzNmMWEiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJ0d2luLWFwaSIsInNlc3Npb25fc3RhdGUiOiI5MjM0NGQyOC04YmQ5LTQ1YTMtYTEyZi1iY2Q2ZTg3YThmOGUiLCJhY3IiOiIwIiwiYWxsb3dlZC1vcmlnaW5zIjpbImh0dHBzOi8vdHdpbi1hcGkucGxheS10cmlidXRlY2guZGF0YXNwYWNlLW5vZGUuY29tIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsImFkbWluIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJyZWFsbS1tYW5hZ2VtZW50Ijp7InJvbGVzIjpbIm1hbmFnZS11c2VycyIsInZpZXctdXNlcnMiLCJxdWVyeS1ncm91cHMiLCJxdWVyeS11c2VycyJdfX0sInNjb3BlIjoicHJvZmlsZSBub2RlLWlkIGNhdGFsb2ctYXBpIHR3aW4tYXBpIGVtYWlsIiwibm9kZS1pZCI6ImI0ZjdhNWE4LTM2NzEtNDEzNC05ZGI4LTVjODNhODlhYjJlOCIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJub2RlLW5hbWUiOiJwbGF5LXRyaWJ1dGVjaCIsIm5hbWUiOiJQYXRyaWNrIExhbXBsbWFpciIsInByZWZlcnJlZF91c2VybmFtZSI6ImFkbWluLW5vZGVAdHJpYnV0ZWNoLXBsYXlncm91bmQuY29tIiwiZ2l2ZW5fbmFtZSI6IlBhdHJpY2siLCJmYW1pbHlfbmFtZSI6IkxhbXBsbWFpciIsImVtYWlsIjoiYWRtaW4tbm9kZUB0cmlidXRlY2gtcGxheWdyb3VuZC5jb20ifQ.yiywl-jU7Fy_cuzViFUgB3Z4IUopIhnSyjZX76CpmoqKH8pFGcA-GHYozUxS8m2-zhkSL88W029xpKxjqoDF0LeI6aYllYx52hnUVVUbSlYqv2ezPpV5sRdYV56A1IIzoLBranLhOTdlDMiaWfB4Wo5pygaDSnINSnlxUjRy-44qvM5RQfMBbAkIFiyvABbHJmA4lJQLNkbt9shOdMwVtKbcACeTVMQPeP9ab1ZVaNChIryDsNTEb9VbBOl_pWqSbsIxmyCZb-yYGsx1Xts5LpmfO6ehw6_iErutpsFm64qTZgPBk7vRyZVNWMAI20wqCv04tyhsE0qQSTqqaV29xQ";

    public void retrieve() {
        LOGGER.info(client.get()
                .uri("twins?pageNumber=1&pageSize=100")
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token)) // TODO Add oauth2 authentification
                .retrieve()
                .bodyToMono(String.class)
                .block());
    }

    /**
     * rudimentary first implementation to see if (1) the model transformation into a digital twin works, (2) the model
     * is valid and (3) the remote server can be accessed and data can be published there
     *
     * @param dto
     */
    @SneakyThrows
    public void publish(DtdlGraphWrapper dto) {
        // Dummy class, necessary to suppress type info in dtdldtos.
        @JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
        class NoTypes {
        }
        // without mixin jackson generates a type property for each DtdlDto implementation, causes problems with validity
        ObjectMapper mapper = new ObjectMapper().addMixIn(DtdlDto.class, NoTypes.class);

        var testString = mapper.writeValueAsString(dto);


        LOGGER.info(testString);

        // TODO use webclient to publish it to tributech.

    }


}
