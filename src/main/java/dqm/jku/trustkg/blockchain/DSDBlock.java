package dqm.jku.trustkg.blockchain;

import dqm.jku.trustkg.dsd.elements.DSDElement;
import dqm.jku.trustkg.util.HashingUtils;

public class DSDBlock extends Block {
  private DSDElement data;

  public DSDBlock(String previousHash) {
    super(previousHash);
  }

  @Override
  public String getHashValue() {
    return HashingUtils.applySha256(getPreviousHash() + Long.toString(getTimeStamp()) + data.getURI() + Integer.toString(getNonce()));
  }

  @Override
  public void mineBlock(int difficulty) {
     String diffStr = HashingUtils.getDificultyString(difficulty);
     while(!getHash().substring(0, difficulty).equals(diffStr)) {
       incrementNonce();
       
     }
  }

}
