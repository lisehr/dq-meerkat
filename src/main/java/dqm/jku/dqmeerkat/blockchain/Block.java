package dqm.jku.dqmeerkat.blockchain;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;

import dqm.jku.dqmeerkat.dsd.elements.DSDElement;
import dqm.jku.dqmeerkat.util.HashingUtils;

/**
 * Basic class for creating a block structure for blockchains Already contains
 * its hash and nonce value as well as immutable timestamp and the hash of the
 * previous block
 * 
 * @author optimusseptim
 *
 */
@RDFNamespaces({ "dsd = http://dqm.faw.jku.at/dsd#" })
@RDFBean("dsd:structures/Block")
public abstract class Block implements Comparable<Block> {
  private String hash; // hash of the current block
  private String previousHash; // hash of the previous block
  private long timeStamp; // the timestamp in milliseconds
  private int nonce; // the nonce value (mining steps)
  private String id; // the id of the block

  public Block() {

  }

  public Block(String chainId, String previousHash) {
    if (previousHash == null) throw new IllegalArgumentException("Previous hash value must be existing!");
    this.previousHash = previousHash;
    this.id = chainId + '/' + previousHash;
    this.timeStamp = System.currentTimeMillis();
  }

  public Block(String chainId, String previousHash, DSDElement data) {
    if (previousHash == null || data == null) throw new IllegalArgumentException("Previous hash value must be existing!");
    this.previousHash = previousHash;
    this.id = chainId + '/' + previousHash;
    this.timeStamp = System.currentTimeMillis();
  }

  /**
   * Gets the id
   * 
   * @return the id
   */
  @RDFSubject
  public String getId() {
    return id;
  }

  /**
   * Sets the id (security threat but needed by rdfbeans)
   * 
   * @param id the id to set
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Get the hash value
   * 
   * @return hash
   */
  @RDF("dsd:hasHash")
  public String getHash() {
    return hash;
  }

  /**
   * Sets the hash value (security threat but needed by rdfbeans)
   * 
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
  @RDF("dsd:hasPrevHash")
  public String getPreviousHash() {
    return previousHash;
  }

  /**
   * Sets the previous hash value (security threat but needed by rdfbeans)
   * 
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
  @RDF("dsd:hasTimestamp")
  public long getTimeStamp() {
    return timeStamp;
  }

  /**
   * Sets the timestamp (security threat but needed by rdfbeans)
   * 
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
  @RDF("dsd:hasNonce")
  public int getNonce() {
    return nonce;
  }

  /**
   * Sets the nonce (security threat but needed by rdfbeans)
   * 
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

  @Override
  public int compareTo(Block other) {
    if (this.timeStamp < other.timeStamp) return -1;
    if (this.timeStamp > other.timeStamp) return 1;
    return 0;

  }

}
