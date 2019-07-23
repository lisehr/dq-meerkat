package dqm.jku.trustkg.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import dqm.jku.trustkg.connectors.ConnectorCSV;
import dqm.jku.trustkg.connectors.ConnectorPartialCSV;

public class FileSelectionUtil {
  private static final String PREFIX = "src/main/java/dqm/jku/trustkg/resources/";
  
  public static ConnectorCSV connectToCSV(int index) throws IOException {
    // walk resources package to make a selection on which csv file should be used for the demo
    Stream<Path> paths = Files.walk(Paths.get(PREFIX));
    List<Path> files = paths.collect(Collectors.toList());
    paths.close();
    
    return new ConnectorCSV(
        files.get(index).toString(), ",", "\n",
        "Test", true);

  }
  
  public static ConnectorPartialCSV connectToCSVPartial(int index, int offset, int noRecords) throws IOException {
    // walk resources package to make a selection on which csv file should be used for the demo
    Stream<Path> paths = Files.walk(Paths.get(PREFIX));
    List<Path> files = paths.collect(Collectors.toList());
    paths.close();
    
    return new ConnectorPartialCSV(
        files.get(index).toString(), ",", "\n",
        "Test", true, offset, noRecords);

  }

}
