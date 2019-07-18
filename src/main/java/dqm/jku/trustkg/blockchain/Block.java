package dqm.jku.trustkg.blockchain;

import java.util.Date;

import dqm.jku.trustkg.util.HashingUtils;

/**
 * Basic class for creating a block structure for blockchains
 * Already contains its hash and nonce value as well as immutable timestamp and the hash of the previous block
 * @author optimusseptim
 *
 */
public abstract class Block {
  private String hash;
  public final String previousHash;
  public final long timeStamp;
  private int nonce;

  public Block(String previousHash) {
    if (previousHash == null) throw new IllegalArgumentException("Previous hash value must be existing!");
    this.previousHash = previousHash;
    this.timeStamp = new Date().getTime();
  }

  /**
   * Get the hash value
   * @return hash
   */
  public String getHash() {
    return hash;
  }

  /**
   * Calculates and sets the hash value
   */
  public void calculateHash() {
    this.hash = acquireHashValue();
  }

  /**
   * Get the previous hash value
   * @return previous hash
   */
  public String getPreviousHash() {
    return previousHash;
  }

  /**
   * Get the timestamp of the block
   * @return timestamp
   */
  public long getTimeStamp() {
    return timeStamp;
  }

  /**
   * Gets the nonce value
   * @return nonce
   */
  public int getNonce() {
    return nonce;
  }

  /**
   * Calculate new hash based on blocks contents, overriden by extending block classes to create a taylormade hash for the
   * contained data.
   * @return the hash value
   */
  public abstract String acquireHashValue();

  /**
   * Method for mining a block with a given difficulty string
   * @param difficulty the length of the mining difficulty string
   */
  public void mineBlock(int difficulty) {
    String diffStr = HashingUtils.getDifficultyString(difficulty);
    while (!getHash().substring(0, difficulty).equals(diffStr)) {
      this.nonce++;
      calculateHash();
    }
    System.out.println("Block mined!");
  }

}
