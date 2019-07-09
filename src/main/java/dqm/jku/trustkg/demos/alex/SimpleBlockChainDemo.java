package dqm.jku.trustkg.demos.alex;

import java.util.ArrayList;

import dqm.jku.trustkg.blockchain.SimpleBlock;
import dqm.jku.trustkg.util.HashingUtils;

// adapted from Tutorial from CryptoKass

public class SimpleBlockChainDemo {
  public static ArrayList<SimpleBlock> blockchain = new ArrayList<SimpleBlock>();
  public static int difficulty = 5;

  public static void main(String[] args) {
    // add our blocks to the blockchain ArrayList:

    System.out.println("Trying to Mine block 1... ");
    addBlock(new SimpleBlock("Hi im the first block", "0"));

    System.out.println("Trying to Mine block 2... ");
    addBlock(new SimpleBlock("Yo im the second block", blockchain.get(blockchain.size() - 1).getHash()));

    System.out.println("Trying to Mine block 3... ");
    addBlock(new SimpleBlock("Hey im the third block", blockchain.get(blockchain.size() - 1).getHash()));

    System.out.println("\nBlockchain is Valid: " + isChainValid());

    String blockchainJson = HashingUtils.getJson(blockchain);
    System.out.println("\nThe block chain: ");
    System.out.println(blockchainJson);
  }

  public static Boolean isChainValid() {
    SimpleBlock currentBlock;
    SimpleBlock previousBlock;
    String hashTarget = new String(new char[difficulty]).replace('\0', '0');

    // loop through blockchain to check hashes:
    for (int i = 1; i < blockchain.size(); i++) {
      currentBlock = blockchain.get(i);
      previousBlock = blockchain.get(i - 1);
      // compare registered hash and calculated hash:
      if (!currentBlock.getHash().equals(currentBlock.getHash())) {
        System.out.println("Current Hashes not equal");
        return false;
      }
      // compare previous hash and registered previous hash
      if (!previousBlock.getHash().equals(currentBlock.getPreviousHash())) {
        System.out.println("Previous Hashes not equal");
        return false;
      }
      // check if hash is solved
      if (!currentBlock.getHash().substring(0, difficulty).equals(hashTarget)) {
        System.out.println("This block hasn't been mined");
        return false;
      }

    }
    return true;
  }

  public static void addBlock(SimpleBlock newBlock) {
    newBlock.mineBlock(difficulty);
    blockchain.add(newBlock);
  }
}
