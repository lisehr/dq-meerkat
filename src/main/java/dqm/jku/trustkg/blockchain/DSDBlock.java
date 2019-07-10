package dqm.jku.trustkg.blockchain;

import dqm.jku.trustkg.dsd.elements.DSDElement;
import dqm.jku.trustkg.util.HashingUtils;

/**
 * 
 * @author optimusseptim
 *
 */
public class DSDBlock extends Block {
  private final DSDElement data;

  public DSDBlock(String previousHash, DSDElement data) {
    super(previousHash);
    if (data == null) throw new IllegalArgumentException("Data cannot be null!");
    this.data = data;
  }

  public DSDElement getData() {
    return data;
  }

  @Override
  public String acquireHashValue() {
    return HashingUtils.applySha256(getPreviousHash() + Long.toString(getTimeStamp()) + data.getURI() + Integer.toString(getNonce()));
  }
}
