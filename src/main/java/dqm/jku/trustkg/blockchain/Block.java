package dqm.jku.trustkg.blockchain;

import java.util.Date;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import dqm.jku.trustkg.util.HashingUtils;

/**
 * Basic class for creating a block structure for blockchains Already contains
 * its hash and nonce value as well as immutable timestamp and the hash of the
 * previous block
 * 
 * @author optimusseptim
 *
 */
@RDFNamespaces({ "foaf = http://xmlns.com/foaf/0.1/", })
@RDFBean("foaf:Block")
public abstract class Block {
  private String hash;
  private String previousHash;
  private long timeStamp;
  private int nonce;

  public Block() {
    
  }
  
  public Block(String previousHash) {
    if (previousHash == null) throw new IllegalArgumentException("Previous hash value must be existing!");
    this.previousHash = previousHash;
    this.timeStamp = new Date().getTime();
  }

  /**
   * Get the hash value
   * 
   * @return hash
   */
  @RDF("foaf:hash")
  public String getHash() {
    return hash;
  }

  /**
   * @param hash the hash to set
   */
  public void setHash(String hash) {
    this.hash = hash;
  }

  /**
   * Calculates and sets the hash value
   */
  public void calculateHash() {
    this.hash = acquireHashValue();
  }

  /**
   * Get the previous hash value
   * 
   * @return previous hash
   */
  @RDF("foaf:prevHash")
  public String getPreviousHash() {
    return previousHash;
  }

  /**
   * @param previousHash the previousHash to set
   */
  public void setPreviousHash(String previousHash) {
    this.previousHash = previousHash;
  }

  /**
   * Get the timestamp of the block
   * 
   * @return timestamp
   */
  @RDF("foaf:timestamp")
  public long getTimeStamp() {
    return timeStamp;
  }

  /**
   * @param timeStamp the timeStamp to set
   */
  public void setTimeStamp(long timeStamp) {
    this.timeStamp = timeStamp;
  }

  /**
   * Gets the nonce value
   * 
   * @return nonce
   */
  @RDF("foaf:nonce")
  public int getNonce() {
    return nonce;
  }

  /**
   * @param nonce the nonce to set
   */
  public void setNonce(int nonce) {
    this.nonce = nonce;
  }

  /**
   * Calculate new hash based on blocks contents, overriden by extending block
   * classes to create a taylormade hash for the contained data.
   * 
   * @return the hash value
   */
  public abstract String acquireHashValue();

  /**
   * Method for mining a block with a given difficulty string
   * 
   * @param difficulty the length of the mining difficulty string
   */
  public void mineBlock(int difficulty) {
    String diffStr = HashingUtils.getDifficultyString(difficulty);
    while (!getHash().substring(0, difficulty).equals(diffStr)) {
      this.nonce++;
      calculateHash();
    }
    System.out.println("Block " + getName() + " mined!");
  }

  protected abstract String getName();

}
