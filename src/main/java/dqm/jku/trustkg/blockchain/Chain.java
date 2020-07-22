package dqm.jku.trustkg.blockchain;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;

/**
 * abstract base class for a blockchain, adapted from CryptoKass' tutorial
 * https://medium.com/programmers-blockchain/create-simple-blockchain-java-tutorial-from-scratch-6eeed3cb03fa
 * 
 * @author optimusseptim
 * 
 */

@RDFNamespaces({ "dsd = http://dqm.faw.jku.at/dsd#" })
@RDFBean("dsd:Chain")
public abstract class Chain {
  private int difficulty; // difficulty value (amount of zeroes in front of the hash)
  private static final int HASH_LEN = 64; // standard length for a 256 bit SHA-256 hash
  private static final int STD_DIFFICULTY = 5; // standard difficulty for a block according to tutorial by cryptokass
  private String id; // the id of the chain

  public Chain() {
    this.difficulty = STD_DIFFICULTY;
  }

  public Chain(String id) {
    this.difficulty = STD_DIFFICULTY;
    this.id = id;
  }

  public Chain(int difficulty, String id) {
    if (difficulty < 1 || difficulty > HASH_LEN) throw new IllegalArgumentException("Difficulty is too small or too big!");
    this.difficulty = difficulty;
    this.id = id;
  }

  /**
   * Gets the id of the chain
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
   * Get the difficulty value of the Chain
   * 
   * @return difficulty
   */
  @RDF("dsd:hasDifficulty")
  public int getDifficulty() {
    return difficulty;
  }

  /**
   * Sets the difficulty value (security threat but needed by rdfbeans)
   * 
   * @param difficulty the difficulty to set
   */
  public void setDifficulty(int difficulty) {
    this.difficulty = difficulty;
  }

}
