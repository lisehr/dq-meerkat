package dqm.jku.trustkg.blockchain;

import dqm.jku.trustkg.util.HashingUtils;

//adapted from Tutorial from CryptoKass

public class SimpleBlock extends Block {
  private String data; // our data will be a simple message.

  // Block Constructor.
  public SimpleBlock(String data, String previousHash) {
    super(previousHash);
    this.data = data;

    this.calculateHash(); // Making sure we do this after we set the other values.
  }

  // Calculate new hash based on blocks contents
  @Override
  public String getHashValue() {
    return HashingUtils
        .applySha256(getPreviousHash() + Long.toString(getTimeStamp()) + Integer.toString(getNonce()) + data);
  }

  // Increases nonce value until hash target is reached.
  @Override
  public void mineBlock(int difficulty) {
    String target = HashingUtils.getDificultyString(difficulty); // Create a string with difficulty * "0"
    while (!getHash().substring(0, difficulty).equals(target)) {
      incrementNonce();
      calculateHash();
    }
    System.out.println("Block Mined: " + getHash());
  }
}
