package dqm.jku.trustkg.blockchain.merkletree;

import dqm.jku.trustkg.blockchain.Block;

/**
 * Implementation of a MerkleDataNode as a extension for a merklenode
 * 
 * @author optimusseptim
 *
 */
public class MerkleDataNode extends MerkleNode {
  Block block; // the block of data contained in the node

  public MerkleDataNode(Block block, MerkleNode parent, boolean isRoot) {
    super(parent, isRoot);
    if (block == null) throw new IllegalArgumentException("Block must not be null!");
    this.block = block;
  }

  @Override
  public void calculateHash() {
    super.doubleHash(block.getHash());
  }

}
