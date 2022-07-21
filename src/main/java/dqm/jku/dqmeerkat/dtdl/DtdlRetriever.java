package dqm.jku.dqmeerkat.dtdl;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import dqm.jku.dqmeerkat.dtdl.dto.DtdlDto;
import dqm.jku.dqmeerkat.dtdl.dto.DtdlGraphWrapper;
import lombok.SneakyThrows;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
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
    private final WebClient client;

    public DtdlRetriever() {
        this("https://twin-api.play-tributech.dataspace-node.com/");
    }

    public DtdlRetriever(String url) {
//        ServerOAuth2AuthorizedClientExchangeFilterFunction oauth =
//                new ServerOAuth2AuthorizedClientExchangeFilterFunction();
        client = WebClient.create(url);
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
