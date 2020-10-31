package dqm.jku.trustkg.demos.architecture.blockchain;

import dqm.jku.trustkg.blockchain.blocks.SimpleBlock;
import dqm.jku.trustkg.blockchain.standardchain.BlockChain;
import dqm.jku.trustkg.util.HashingUtils;

/**
 * adapted from Tutorial from CryptoKass
 * https://medium.com/programmers-blockchain/create-simple-blockchain-java-tutorial-from-scratch-6eeed3cb03fa
 * 
 * @author optimusseptim
 *
 */
public class SimpleBlockChainDemo {
  public static void main(String[] args) {
    // instanciate blockchain
    BlockChain blockchain = new BlockChain();
    blockchain.setId("http://example.com/blockchain/demo1");

    // add our blocks to the blockchain ArrayList:

    System.out.println("Trying to Mine block 1... ");
    blockchain.addBlock(new SimpleBlock(blockchain.getId(), "Hi im the first block", "0"));

    System.out.println("Trying to Mine block 2... ");
    blockchain.addBlock(new SimpleBlock(blockchain.getId(), "Yo im the second block", blockchain.accessBlock(blockchain.chainSize() - 1).getHash()));

    System.out.println("Trying to Mine block 3... ");
    blockchain.addBlock(new SimpleBlock(blockchain.getId(), "Hey im the third block", blockchain.accessBlock(blockchain.chainSize() - 1).getHash()));

    System.out.println("\nBlockchain is Valid: " + blockchain.isChainValid());

    String blockchainJson = HashingUtils.getJson(blockchain);
    System.out.println("\nThe block chain: ");
    System.out.println(blockchainJson);
  }

}
