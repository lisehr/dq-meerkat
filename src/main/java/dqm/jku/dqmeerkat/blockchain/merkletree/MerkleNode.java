package dqm.jku.dqmeerkat.blockchain.merkletree;

import dqm.jku.dqmeerkat.util.HashingUtils;

/**
 * abstract base class for the implementation of a merklenode
 * 
 * @author optimusseptim
 *
 */

public abstract class MerkleNode {

  private MerkleNode parentNode; // node for parent
  private MerkleNode leftNode; // node for left child
  private MerkleNode rightNode; // node for right child
  private String hash; // hash of this node
  private int level; // depth of node

  public MerkleNode(MerkleNode parent, boolean isRoot) {
    leftNode = null;
    rightNode = null;
    if (parent == null && !isRoot) throw new IllegalArgumentException("Non-root element must have parent!");
    parentNode = parent;
  }

  /**
   * Checks existence of left child
   * 
   * @return true if so, false otherwise
   */
  public Boolean hasLeft() {
    return leftNode != null;
  }

  /**
   * Checks existence of right child
   * 
   * @return true if so, false otherwise
   */
  public Boolean hasRight() {
    return rightNode != null;
  }

  /**
   * Gets the left node
   * 
   * @return left node
   */
  public MerkleNode getLeft() {
    return leftNode;
  }

  /**
   * Gets the right node
   * 
   * @return right node
   */
  public MerkleNode getRight() {
    return rightNode;
  }

  /**
   * Gets the parent node
   * 
   * @return parent node
   */
  public MerkleNode getParent() {
    return parentNode;
  }

  /**
   * Acquire the hash
   * 
   * @return hash string
   */
  public String getHash() {
    return hash;
  }

  /**
   * Acquire tree depth
   * 
   * @return depth level
   */
  public int getLevel() {
    return level;
  }

  /**
   * Increments tree level
   */
  public void incrementLevel() {
    level++;
  }

  /**
   * Method to calculate the hash value
   */
  public abstract void calculateHash();

  /**
   * Method to calculate double hash values
   * 
   * @param input the string to be input for hashing
   */
  public void doubleHash(String input) {
    hash = HashingUtils.applySha256(HashingUtils.applySha256(input));
  }

}
