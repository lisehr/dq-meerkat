package dqm.jku.dqmeerkat.blockchain.blocks;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import dqm.jku.dqmeerkat.blockchain.Block;
import dqm.jku.dqmeerkat.dsd.elements.DSDElement;
import dqm.jku.dqmeerkat.util.HashingUtils;

/**
 * This class defines a block data structure for a blockchain containing an DSD
 * element.
 * 
 * @author optimusseptim
 */
@RDFNamespaces({ "dsd = http://dqm.faw.jku.at/dsd#" })
@RDFBean("dsd:DSDBlock")
public class DSDBlock extends Block {
  private DSDElement data; // data in form of a DSDElement

  public DSDBlock() {
    super();
  }

  public DSDBlock(String chainId, String previousHash, DSDElement data) {
    super(chainId, previousHash, data);
    this.data = data;
    this.calculateHash();
  }

  /**
   * Gets the data stored in the block
   * 
   * @return the dsd element stored in the block
   */
  @RDF("dsd:hasDSDElement")
  public DSDElement getData() {
    return data;
  }

  /**
   * Sets the data stored in the block (security threat but needed for rdfbeans)
   * 
   * @param data the data to be set
   */
  public void setData(DSDElement data) {
    this.data = data;
  }

  @Override
  public String acquireHashValue() {
    return HashingUtils.applySha256(getPreviousHash() + Long.toString(getTimeStamp()) + data.getURI() + Integer.toString(getNonce()));
  }

  @Override
  protected String getName() {
    return data.getURI();
  }
}
