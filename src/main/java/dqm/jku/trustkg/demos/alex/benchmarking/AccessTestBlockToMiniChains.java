package dqm.jku.trustkg.demos.alex.benchmarking;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import dqm.jku.trustkg.blockchain.Block;
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
 * Test class for access testing of blockchains compared to minichains
 * 
 * @author optimusseptim
 *
 */
public class AccessTestBlockToMiniChains {
  private static final boolean DEBUG = true;
  private static final int TESTRUNS = 10;
  private static final int SETSOFELEMS = 3;

  public static void main(String args[]) throws IOException {
    DSConnector conn = FileSelectionUtil.connectToCSV(1);

    if (DEBUG) {
      System.out.println("Connection established!");
      System.out.println("Start testing...");
      System.out.println("Printing results to file!");
      System.setOut(new PrintStream(Constants.RESOURCES + "/export/benchmarking/AccessTestBlockToMiniChain_" + System.currentTimeMillis() + ".txt"));
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
    System.out.println("Filling Blockchains...");
    BlockChain test1 = new BlockChain();
    MiniBlockChain test2 = new MiniBlockChain("test");
    System.out.println("Blockchain:");
    for (DSDElement e : elements) {
      test1.addBlock(new DSDBlock(test1.getPreviousHash(), e));
      System.out.println("Added block with element: " + e.getURI());
    }
    blankline();
    System.out.println("MiniBlockchain:");
    for (DSDElement e : elements) {
      test2.addDSDElement(e);
      System.out.println("Added block with element: " + e.getURI());
    }

    blankline();
    System.out.println("Test 1: reading from initial chain without changes");
    System.out.println(TESTRUNS + " test runs...");
    System.out.println("-----------------------------------------------------------------------");
    List<Float> percentages = new ArrayList<Float>();
    for (int i = 0; i < TESTRUNS; i++) {
      long time1 = measureAccessTime(test1, TESTRUNS);
      long time2 = measureAccessTimeMC(test2, elements.get(TESTRUNS), 0);
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
    System.out.println("Filling Blockchains...");
    System.out.println("Blockchain:");
    for (int i = 0; i < SETSOFELEMS; i++) {
      for (DSDElement e : elements) {
        test1.addBlock(new DSDBlock(test1.getPreviousHash(), e));
        System.out.println("Added block with element: " + e.getURI());
      }
      blankline();
      System.out.println("MiniBlockchain:");
      for (DSDElement e : elements) {
        test2.addDSDElement(e);
        System.out.println("Added block with element: " + e.getURI());
      }
    }
    System.out.println("Test 2: reading chain with changes");
    System.out.println(TESTRUNS + " test runs... | " + SETSOFELEMS + " sets of elements to be added");
    System.out.println("-----------------------------------------------------------------------");
    percentages = new ArrayList<Float>();
    for (int i = 0; i < TESTRUNS; i++) {
      long time1 = measureAccessTime(test1, TESTRUNS * SETSOFELEMS);
      long time2 = measureAccessTimeMC(test2, elements.get(TESTRUNS), 2);
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
    if (result < 0) System.out.println(String.format("Mesuring with MiniBlockChain was faster by %d ns than measuring with BlockChain.", Math.abs(result)));
    else if (result > 0) System.out.println(String.format("Mesuring with BlockChain was faster by %d ns than measuring with MiniBlockChain.", Math.abs(result)));
    else if (result == 0) System.out.println("Both methods are equally fast!");
    float percent = 100.0f - ((time2 * 100.0f) / time1);
    System.out.println(String.format("The faster method resulted in a performance growth by %.2f %%", percent));
    return percent;
  }

  /**
   * Makes a measurement for accesstime with a miniBlockchain
   * 
   * @param mbC   the mini blockchain to be tested
   * @param elem  the element to be accessed
   * @param index the index in the minichain
   * @return accesstime
   */
  private static long measureAccessTimeMC(MiniBlockChain mbC, DSDElement elem, int index) {
    blankline();
    System.out.println("Starting with MiniBlockChain access test");
    long sTime = System.nanoTime();
    Block target = mbC.accessElementOfMiniChain(elem.getURI(), index);
    if (elem.getURI().equals(target.getId())) System.out.println("Block " + target.getId() + " found!");
    long eTime = System.nanoTime();
    long result = eTime - sTime;
    System.out.println(String.format("Test ended with a time of %d ns", result));
    blankline();
    return result;
  }

  /**
   * Makes a measurement for accesstime of a blockchain
   * 
   * @param bC    the blockchain to be tested
   * @param index the index in the chain
   * @return accesstime
   */
  private static long measureAccessTime(BlockChain bC, int index) {
    blankline();
    System.out.println("Starting with BlockChain access test");
    long sTime = System.nanoTime();
    Block target = bC.accessBlock(index);
    System.out.println("Block " + target.getId() + " found!");
    long eTime = System.nanoTime();
    long result = eTime - sTime;
    System.out.println(String.format("Test ended with a time of %d ns", result));
    blankline();
    return result;
  }
}