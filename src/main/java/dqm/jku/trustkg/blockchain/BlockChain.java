package dqm.jku.trustkg.blockchain;

import java.util.ArrayList;
import java.util.Objects;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

@RDFNamespaces({ 
  "foaf = http://xmlns.com/foaf/0.1/",
})
@RDFBean("foaf:BlockChain")
public class BlockChain {
  private ArrayList<Block> blockChain = new ArrayList<>();
  private int difficulty;
  private static final int HASH_LEN = 64; // standard length for a 256 bit SHA-256 hash
  private static final int STD_DIFFICULTY = 5; // standard difficulty for a block according to tutorial by cryptokass
  
  public BlockChain() {
    this.difficulty = STD_DIFFICULTY;
  }

  public BlockChain(int difficulty) {
    if (difficulty < 1 || difficulty > HASH_LEN) throw new IllegalArgumentException("Difficulty is too small or too big!");
    this.difficulty = difficulty;
  }

  /**
   * Get the difficulty value of the Chain
   * @return difficulty
   */
  @RDF("foaf:difficulty")
  public int getDifficulty() {
    return difficulty;
  }

  /**
   * Access a specific block element of the chain
   * @param index the index of the element
   * @return the block element if index is inside bounderies, null otherwise
   */
  public Block accessBlock(int index) {
    if (index < 0 || index > blockChain.size()) return null;
    return blockChain.get(index);
  }

  /**
   * Adds a new mined block to the chain 
   * @param block the block to be added
   * @return true if added, false otherwise
   */
  public boolean addBlock(Block block) {
    if (block == null) return false;
    block.mineBlock(difficulty);
    return blockChain.add(block);
  }
  
  /**
   * Gets the last calculated hash for adding a new block for instance
   * @return String with the last hash, "0", if no block is in the chain
   */
  public String getPreviousHash() {
    if (chainSize() == 0) return "0";
    else return accessBlock(chainSize() - 1).getHash();
  }

  /**
   * Gets the size of the blockchain
   * @return size
   */
  public int chainSize() {
    return blockChain.size();
  }
  
  /**
   * Method for checking, if the blockchain is valid
   * @return true if valid, false if otherwise (errors are printed to the console)
   */
  public Boolean isChainValid() {
    Block currentBlock;
    Block previousBlock;
    String hashTarget = new String(new char[this.difficulty]).replace('\0', '0');

    // loop through blockchain to check hashes:
    for (int i = 1; i < chainSize(); i++) {
      currentBlock = accessBlock(i);
      previousBlock = accessBlock(i - 1);
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
      if (!currentBlock.getHash().substring(0, this.difficulty).equals(hashTarget)) {
        System.out.println("This block hasn't been mined");
        return false;
      }
    }
    return true;
  }

  /**
   * @return the blockChain
   */
  @RDF("foaf:hasBlock")
  public ArrayList<Block> getBlockChain() {
    return blockChain;
  }

  /**
   * @param blockChain the blockChain to set
   */
  public void setBlockChain(ArrayList<Block> blockChain) {
    this.blockChain.clear();
    this.blockChain.addAll(blockChain);
  }

  /**
   * @param difficulty the difficulty to set
   */
  public void setDifficulty(int difficulty) {
    this.difficulty = difficulty;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (!(obj instanceof BlockChain)) return false;
    BlockChain other = (BlockChain) obj;
    return Objects.equals(blockChain, other.blockChain) && difficulty == other.difficulty;
  }
  
  
}
