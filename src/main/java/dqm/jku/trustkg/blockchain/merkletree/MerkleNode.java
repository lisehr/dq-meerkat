package dqm.jku.trustkg.blockchain.merkletree;

import dqm.jku.trustkg.util.HashingUtils;

public abstract class MerkleNode {
  
  private MerkleNode parentNode;
  private MerkleNode leftNode;
  private MerkleNode rightNode;
  private String hash;
  private int level;
  
  public MerkleNode(MerkleNode parent, boolean isRoot) {
    leftNode = null;
    rightNode = null;
    if (parent == null && !isRoot) throw new IllegalArgumentException("Non-root element must have parent!");
    parentNode = parent;
  }
  
  public Boolean hasLeft() {
    return leftNode != null;
  }
  
  public Boolean hasRight() {
    return rightNode != null;
  }

  public MerkleNode getLeft() {
    return leftNode;
  }
  
  public MerkleNode getRight() {
    return rightNode;
  }
  
  public MerkleNode getParent() {
    return parentNode;
  }
  
  public String getHash() {
    return hash;
  }
  
  public int getLevel() {
    return level;
  }
  
  public void incrementLevel() {
    level++;
  }
    
  public abstract void calculateHash();
  
  public void doubleHash(String input) {
    hash = HashingUtils.applySha256(HashingUtils.applySha256(input));
  }

}
