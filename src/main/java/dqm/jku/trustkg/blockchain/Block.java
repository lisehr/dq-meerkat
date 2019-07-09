package dqm.jku.trustkg.blockchain;

import java.util.Date;

public abstract class Block {
  private String hash;
  private String previousHash;
  private long timeStamp;
  private int nonce;

  
  public Block(String previousHash) {
    this.previousHash = previousHash;
    this.timeStamp = new Date().getTime();
  }  
  
  public String getHash() {
    return hash;
  }
  
  public void calculateHash() {
    this.hash = getHashValue();
  }
  
  public String getPreviousHash() {
    return previousHash;
  }
  
  public void setPreviousHash(String previousHash) {
    this.previousHash = previousHash;
  }
  
  public long getTimeStamp() {
    return timeStamp;
  }

  public int getNonce() {
    return nonce;
  }

  public void incrementNonce() {
    this.nonce++;
  }
  
  public abstract String getHashValue();
  
  public abstract void mineBlock (int difficulty);

}
