package dqm.jku.trustkg.blockchain;

import dqm.jku.trustkg.util.HashingUtils;

//adapted from Tutorial from CryptoKass

public class SimpleBlock extends Block {
  private final String data; // our data will be a simple message.

  // Block Constructor.
  public SimpleBlock(String data, String previousHash) {
    super(previousHash);
    this.data = data;
    this.calculateHash(); // Making sure we do this after we set the other values.
  }

  public String getData() {
    return data;
  }

  @Override
  public String acquireHashValue() {
    return HashingUtils.applySha256(getPreviousHash() + Long.toString(getTimeStamp()) + Integer.toString(getNonce()) + data);
  }
}
