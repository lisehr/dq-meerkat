package dqm.jku.trustkg.blockchain.merkletree;

public class MerkleTree {
  private MerkleNode root;
  private int size;
  
  public MerkleTree() {
    root = new MerkleHashNode(null, true);
    size = 0;
  }
  
  // rejected because of security reasons

}
