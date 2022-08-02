package dqm.jku.dqmeerkat.resources.export.json.dtdl;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import dqm.jku.dqmeerkat.dtdl.dto.*;
import dqm.jku.dqmeerkat.quality.DataProfile;
import dqm.jku.dqmeerkat.resources.export.DataExporter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <h2>DtdlGraphExporter</h2>
 * <summary>
 * Transforms a whole Dtdl graph as defined in {@link DtdlGraphWrapper} into a json of DTDL and exports it to a file.
 * </summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 10.05.2022
 */
@AllArgsConstructor
public class DtdlGraphExporter implements DataExporter<DataProfile> {

    @Getter
    private final UUID profiledStream;

    @Override
    public void export(DataProfile toExport, String filePath, String fileName) {
        throw new UnsupportedOperationException("not Implemented yet");
    }

    @SneakyThrows
    @Override
    public String export(DataProfile toExport) {
        var profileDto = DatasourceDto.builder()
                .metaData(new MetaDataDto("dtmi:scch:at:dq:Dataprofile;2"))
                .build();
        var profileDtos = toExport.getStatistics().stream()
                .filter(statistic -> statistic.getValue() != null)
                .map(profileStatistic -> ProfileStatisticDto.builder()
                        .value(profileStatistic.getValue().toString())
                        .title(profileStatistic.getTitle().toString())
                        .category(profileStatistic.getCat().toString())
                        .metaData(new MetaDataDto("dtmi:scch:at:dq:ProfileStatistic;2"))
                        .build())
                .collect(Collectors.toList());
        var relationships = profileDtos.stream().map(profileStatisticDto -> RelationshipDto.builder()
                        .targetId(profileStatisticDto.getDtId())
                        .sourceId(profileDto.getDtId())
                        .relationshipName(profileStatisticDto.getTitle()
                                .replace(" ", "")
                                .replace("%", "Percentof")
                                .replace("#", "NrOf")) // relationship must not have whitespaces, #, % or other special characters!
                        .build())
                .collect(Collectors.toList());
        // relationship to data stream
        relationships.add(RelationshipDto.builder()
                .relationshipName("ProfiledStream")
                .sourceId(profileDto.getDtId())
                .targetId(profiledStream)
                .build());
        var graph = new DtdlGraph();
        graph.addDigitalTwin(profileDto);
        profileDtos.forEach(graph::addDigitalTwin);
        relationships.forEach(graph::addRelationship);
        var graphWrapper = new DtdlGraphWrapper(graph);

        @JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
        class NoTypes {
        }
        // without mixin jackson generates a type property for each DtdlDto implementation, causes problems with validity
        ObjectMapper mapper = new ObjectMapper().addMixIn(DtdlDto.class, NoTypes.class);

        return mapper.writeValueAsString(graphWrapper);
    }
}
