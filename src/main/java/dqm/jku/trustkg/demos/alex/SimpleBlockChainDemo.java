package dqm.jku.trustkg.demos.alex;

import dqm.jku.trustkg.blockchain.BlockChain;
import dqm.jku.trustkg.blockchain.SimpleBlock;
import dqm.jku.trustkg.util.HashingUtils;

// adapted from Tutorial from CryptoKass

public class SimpleBlockChainDemo {
  public static void main(String[] args) {
    // instanciate blockchain
    BlockChain blockchain = new BlockChain();

    // add our blocks to the blockchain ArrayList:

    System.out.println("Trying to Mine block 1... ");
    blockchain.addBlock(new SimpleBlock("Hi im the first block", "0"));

    System.out.println("Trying to Mine block 2... ");
    blockchain.addBlock(new SimpleBlock("Yo im the second block", blockchain.accessBlock(blockchain.chainSize() - 1).getHash()));

    System.out.println("Trying to Mine block 3... ");
    blockchain.addBlock(new SimpleBlock("Hey im the third block", blockchain.accessBlock(blockchain.chainSize() - 1).getHash()));

    System.out.println("\nBlockchain is Valid: " + blockchain.isChainValid());

    String blockchainJson = HashingUtils.getJson(blockchain);
    System.out.println("\nThe block chain: ");
    System.out.println(blockchainJson);
  }

}
