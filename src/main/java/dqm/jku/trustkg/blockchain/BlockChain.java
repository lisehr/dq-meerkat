package dqm.jku.trustkg.blockchain;

import java.util.ArrayList;

public class BlockChain {
  private final ArrayList<Block> blockChain = new ArrayList<>();
  private final int difficulty;
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
}
