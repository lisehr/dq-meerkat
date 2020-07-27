package dqm.jku.trustkg.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;

import dqm.jku.trustkg.connectors.ConnectorCSV;

/**
 * 
 * @author optimusseptim
 *
 *         Utility class for file selection purposes
 */
public class FileSelectionUtil {
  private static final String PREFIX = "src/main/java/dqm/jku/trustkg/resources/";
  private static final String CSV = "csv/";
  private static final String PATTERNS = "patterns/";

  /**
   * Method for connecting to a specific CSV file in the resource folder
   * 
   * @param index the number of the file, listed alphabetically
   * @return CSVConnector
   * @throws IOException
   */
  public static ConnectorCSV connectToCSV(int index) throws IOException {
    // walk resources package to make a selection on which csv file should be used
    // for the demo
    Stream<Path> paths = Files.walk(Paths.get(PREFIX + CSV));
    List<Path> files = paths.collect(Collectors.toList());
    files = files.subList(1, files.size());
    paths.close();

    return new ConnectorCSV(files.get(index).toString(), ",", "\n", FilenameUtils.removeExtension(files.get(index).getFileName().toString()), true);

  }

  /**
   * Method for connecting to a specific CSV file in the resource folder
   * 
   * @param index the number of the file, listed alphabetically
   * @param name  the filename
   * @return CSVConnector
   * @throws IOException
   */
  public static ConnectorCSV connectToCSV(int index, String name) throws IOException {
    // walk resources package to make a selection on which csv file should be used
    // for the demo
    Stream<Path> paths = Files.walk(Paths.get(PREFIX + CSV));
    List<Path> files = paths.collect(Collectors.toList());
    paths.close();

    return new ConnectorCSV(files.get(index).toString(), ",", "\n", name, false);

  }

  /**
   * Method for reading all regex patterns in the respective file
   * 
   * @param index the file index, sorted alphabetically
   * @return list of strings containing regex patterns
   * @throws IOException
   */
  public static List<String> readAllPatternsOfFile(int index) throws IOException {
    // walk resources package to make a selection on which pattern file should be
    // used for the demo
    Stream<Path> paths = Files.walk(Paths.get(PREFIX + PATTERNS));
    List<Path> files = paths.collect(Collectors.toList());
    paths.close();

    if (files.size() > 1) {
      return Files.readAllLines(files.get(index));
    } else {
      // return empty list if no patterns found
      return new ArrayList<String>();
    }
  }

}
