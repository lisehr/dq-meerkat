package dqm.jku.dqmeerkat.domain.dtdl;

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
    private final WebClient client = WebClient.create("http://https://foass.1001010.com/");

    public void retrieve() {
        LOGGER.info(client.get()
                .uri("you/dtdl/me")
                .retrieve()
                .bodyToMono(String.class)
                .block());

    }


}
