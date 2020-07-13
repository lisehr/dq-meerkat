package dqm.jku.trustkg.blockchain.merkletree;

/**
 * Implementation of a merklenode with additional hashes for left and right
 * children
 * 
 * @author optimusseptim
 * 
 */
public class MerkleHashNode extends MerkleNode {

  public MerkleHashNode(MerkleNode parent, boolean isRoot) {
    super(parent, isRoot);
  }

  @Override
  public void calculateHash() {
    String leftHash = "";
    String rightHash = "";
    if (super.hasLeft()) leftHash = super.getLeft().getHash();
    if (super.hasRight()) rightHash = super.getRight().getHash();
    else rightHash = new String(leftHash);
    super.doubleHash(leftHash + rightHash);
  }

}
