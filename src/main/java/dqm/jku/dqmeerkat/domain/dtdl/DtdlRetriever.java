package dqm.jku.dqmeerkat.domain.dtdl;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import dqm.jku.dqmeerkat.domain.dtdl.dto.DtdlDto;
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
    private final WebClient client = WebClient.create("https://foass.1001010.com/");

    public void retrieve() {
        LOGGER.info(client.get()
                .uri("you/dtdl/me")
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
    public void publish(DtdlDto dto) {
        // Dummy class, necessary to suppress type info in dtdldtos
        @JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
        class NoTypes {
        }

        ObjectMapper mapper = new ObjectMapper().addMixIn(DtdlDto.class, NoTypes.class);

        var testString = mapper.writeValueAsString(dto);
        LOGGER.info(testString);

    }


}
