package dqm.jku.trustkg.blockchain;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;

@RDFNamespaces({ "foaf = http://xmlns.com/foaf/0.1/", "bc = http://example.com/structures/blockchain/" })
@RDFBean("foaf:Chain")
public abstract class Chain {
  private int difficulty;
  private static final int HASH_LEN = 64; // standard length for a 256 bit SHA-256 hash
  private static final int STD_DIFFICULTY = 5; // standard difficulty for a block according to tutorial by cryptokass
  private String id;

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
   * @return the id
   */
  @RDFSubject(prefix = "bc:")
  public String getId() {
    return id;
  }

  /**
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
  @RDF("foaf:difficulty")
  public int getDifficulty() {
    return difficulty;
  }


  /**
   * @param difficulty the difficulty to set
   */
  public void setDifficulty(int difficulty) {
    this.difficulty = difficulty;
  }

}
