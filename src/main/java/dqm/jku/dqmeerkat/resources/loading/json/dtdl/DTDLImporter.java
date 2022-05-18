package dqm.jku.dqmeerkat.resources.loading.json.dtdl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dqm.jku.dqmeerkat.dtdl.DtdlInterface;
import dqm.jku.dqmeerkat.resources.loading.json.Importer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * <h2>DTDLImporter</h2>
 * <summary>Implementation of {@link Importer} for loading JSON files in DTDL standard. The given {@link DtdlInterface}
 * objects describe the schema data can be depicted in</summary>
 *
 * @author meindl, rainer.meindl@scch.at
 * @since 31.03.2022
 */
public class DTDLImporter extends Importer<DtdlInterface> {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public DtdlInterface importData(String path) {
        try {
            var file = new File(path);
            InputStream is = new FileInputStream(file);
            return mapper.readValue(is, DtdlInterface.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<DtdlInterface> importDataList(String path) {
        try {
            var file = new File(path);
            InputStream is = new FileInputStream(file);
            return mapper.readValue(is, new TypeReference<List<DtdlInterface>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
