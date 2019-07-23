package dqm.jku.trustkg.blockchain.blocks;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import dqm.jku.trustkg.blockchain.Block;
import dqm.jku.trustkg.dsd.elements.DSDElement;
import dqm.jku.trustkg.util.HashingUtils;

/**
 * 
 * @author optimusseptim
 *
 */
@RDFNamespaces({ "foaf = http://xmlns.com/foaf/0.1/", })
@RDFBean("foaf:DSDBlock")
public class DSDBlock extends Block {
  private DSDElement data;

  public DSDBlock() {
    super();
  }

  public DSDBlock(String previousHash, DSDElement data) {
    super(previousHash, data);
    this.data = data;
    this.calculateHash();
  }

  /**
   * Gets the data stored in the block
   * 
   * @return the dsd element stored in the block
   */
  @RDF("foaf:hasDSDElement")
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