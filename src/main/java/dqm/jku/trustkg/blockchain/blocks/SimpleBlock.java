package dqm.jku.trustkg.blockchain.blocks;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import dqm.jku.trustkg.blockchain.Block;
import dqm.jku.trustkg.util.HashingUtils;

/**
 * adapted from Tutorial from CryptoKass
 * https://medium.com/programmers-blockchain/create-simple-blockchain-java-tutorial-from-scratch-6eeed3cb03fa
 * 
 * @author optimusseptim
 */
@RDFNamespaces({ "dsd = http://dqm.faw.jku.at/dsd#" })
@RDFBean("dsd:SimpleBlock")
public class SimpleBlock extends Block {
  private String data; // our data will be a simple message.

  public SimpleBlock() {
    super();
  }

  // Block Constructor.
  public SimpleBlock(String chainId, String data, String previousHash) {
    super(chainId, previousHash);
    this.data = data;
    this.calculateHash(); // Making sure we do this after we set the other values.
  }

  /**
   * Gets the data contained in the block
   * 
   * @return data string
   */
  @RDF("dsd:hasData")
  public String getData() {
    return data;
  }

  /**
   * Sets the data contained in the block
   * 
   * @param data the data to be set
   */
  public void setData(String data) {
    this.data = data;
  }

  @Override
  public String acquireHashValue() {
    return HashingUtils.applySha256(getPreviousHash() + Long.toString(getTimeStamp()) + Integer.toString(getNonce()) + data);
  }

  @Override
  protected String getName() {
    return data;
  }
}
