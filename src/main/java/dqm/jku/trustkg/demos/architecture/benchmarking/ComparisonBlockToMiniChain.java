package dqm.jku.trustkg.demos.architecture.benchmarking;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import dqm.jku.trustkg.blockchain.blocks.DSDBlock;
import dqm.jku.trustkg.blockchain.minichain.MiniBlockChain;
import dqm.jku.trustkg.blockchain.standardchain.BlockChain;
import dqm.jku.trustkg.connectors.DSConnector;
import dqm.jku.trustkg.dsd.elements.Attribute;
import dqm.jku.trustkg.dsd.elements.Concept;
import dqm.jku.trustkg.dsd.elements.DSDElement;
import dqm.jku.trustkg.dsd.elements.Datasource;
import dqm.jku.trustkg.util.Constants;
import dqm.jku.trustkg.util.FileSelectionUtil;

/**
 * Test class for complete comparison testing of blockchains compared to
 * minichains
 * 
 * @author optimusseptim
 *
 */
public class ComparisonBlockToMiniChain {
  private static final boolean DEBUG = true;
  private static final int TESTRUNS = 10;
  private static final int SETSOFELEMS = 3;

  public static void main(String args[]) throws IOException {
    DSConnector conn = FileSelectionUtil.connectToCSV(1);

    if (DEBUG) {
      System.out.println("Connection established!");
      System.out.println("Start testing...");
      System.out.println("Printing results to file!");
      System.setOut(new PrintStream(Constants.RESOURCES + "/export/benchmarking/ComparisonBlockToMiniChain_" + System.currentTimeMillis() + ".txt"));
    }

    Datasource ds = conn.loadSchema();
    ArrayList<DSDElement> elements = new ArrayList<>();
    for (Concept c : ds.getConcepts()) {
      elements.add(c);
      for (Attribute a : c.getAttributes()) {
        elements.add(a);
      }
    }
    System.out.println("Tests for different Blockchain structures with a test size of " + elements.size() + " elements");
    System.out.println("Test 1: creating initial chain without changes");
    System.out.println(TESTRUNS + " test runs...");
    System.out.println("-----------------------------------------------------------------------");
    List<Float> percentages = new ArrayList<Float>();
    for (int i = 0; i < TESTRUNS; i++) {
      BlockChain test1 = new BlockChain();
      MiniBlockChain test2 = new MiniBlockChain("test");
      long time1 = measureCreatingTime(test1, elements);
      long time2 = measureCreatingTimeMC(test2, elements);
      long result = time2 - time1;
      percentages.add(determineResult(result, time1, time2));
    }
    blankline();
    float avgP = percentages.stream().reduce((float) 0.0, (a, b) -> a + b);
    avgP /= (float) TESTRUNS;
    System.out.println(String.format("MiniBlockChains were faster with an average performance growth of %.2f %%", avgP));
    blankline();
    System.out.println("=================================================================================");
    blankline();
    System.out.println("Test 2: creating chain with changes");
    System.out.println(TESTRUNS + " test runs... | " + SETSOFELEMS + " sets of elements to be added");
    System.out.println("-----------------------------------------------------------------------");
    for (int i = 0; i < TESTRUNS; i++) {
      BlockChain test1 = new BlockChain();
      MiniBlockChain test2 = new MiniBlockChain("test");
      long time1 = 0;
      long time2 = 0;
      for (int j = 0; j < SETSOFELEMS; j++) {
        time1 += measureCreatingTime(test1, elements);
        time2 += measureCreatingTimeMC(test2, elements);
      }
      long result = time2 - time1;
      percentages.add(determineResult(result, time1, time2));
    }
    blankline();
    avgP = percentages.stream().reduce((float) 0.0, (a, b) -> a + b);
    avgP /= (float) TESTRUNS;
    System.out.println(String.format("MiniBlockChains were faster with an average performance growth of %.2f %%", avgP));

    if (DEBUG) {
      PrintStream consoleStream = new PrintStream(new FileOutputStream(FileDescriptor.out));
      System.setOut(consoleStream);
      System.out.println("Finished!");
    }
  }

  /**
   * Helper method for creating a blank line
   */
  private static void blankline() {
    System.out.println();
  }

  /**
   * Creates result of measurement via three parameters
   * 
   * @param result the difference between the two systems
   * @param time1  the time of the old system
   * @param time2  the time of the new system
   * @return speedup in per cent
   */
  private static float determineResult(long result, long time1, long time2) {
    blankline();
    if (result < 0) System.out.println(String.format("Mesuring with MiniBlockChain was faster by %d ms than measuring with BlockChain.", Math.abs(result)));
    else if (result > 0) System.out.println(String.format("Mesuring with BlockChain was faster by %d ms than measuring with MiniBlockChain.", Math.abs(result)));
    else if (result == 0) System.out.println("Both methods are equally fast!");
    float percent = 100.0f - ((time2 * 100.0f) / time1);
    System.out.println(String.format("The faster method resulted in a performance growth by %.2f %%", percent));
    return percent;
  }

  /**
   * Makes a measurement for creation with a miniBlockchain
   * 
   * @param mbC      the mini blockchain to be tested
   * @param elements the elements to be added
   * @return creation time
   */
  private static long measureCreatingTimeMC(MiniBlockChain mbC, ArrayList<DSDElement> elements) {
    blankline();
    System.out.println("Starting with MiniBlockChain creation test");
    long sTime = System.currentTimeMillis();
    for (DSDElement e : elements) {
      mbC.addDSDElement(e);
      System.out.println("Added block with element: " + e.getURI());
    }
    long eTime = System.currentTimeMillis();
    long result = eTime - sTime;
    System.out.println(String.format("Test ended with testsize of %d blocks and a time of %d ms", elements.size(), result));
    blankline();
    return result;
  }

  /**
   * Makes a measurement for creation with a Blockchain
   * 
   * @param bC       the blockchain to be tested
   * @param elements the elements to be added
   * @return creation time
   */
  private static long measureCreatingTime(BlockChain bC, ArrayList<DSDElement> elements) {
    System.out.println();
    System.out.println("Starting with BlockChain creation test");
    long sTime = System.currentTimeMillis();
    for (DSDElement e : elements) {
      bC.addBlock(new DSDBlock(bC.getId(), bC.getPreviousHash(), e));
      System.out.println("Added block with element: " + e.getURI());
    }
    long eTime = System.currentTimeMillis();
    long result = eTime - sTime;
    System.out.println(String.format("Test ended with testsize of %d blocks and a time of %d ms", elements.size(), result));
    System.out.println();
    return result;
  }
}
