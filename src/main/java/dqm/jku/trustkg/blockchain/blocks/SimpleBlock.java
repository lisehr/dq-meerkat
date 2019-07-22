package dqm.jku.trustkg.blockchain.blocks;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import dqm.jku.trustkg.blockchain.Block;
import dqm.jku.trustkg.util.HashingUtils;

//adapted from Tutorial from CryptoKass

@RDFNamespaces({ "foaf = http://xmlns.com/foaf/0.1/", })
@RDFBean("foaf:SimpleBlock")
public class SimpleBlock extends Block {
  private String data; // our data will be a simple message.

  public SimpleBlock() {
    super();
  }

  // Block Constructor.
  public SimpleBlock(String data, String previousHash) {
    super(previousHash);
    this.data = data;
    this.calculateHash(); // Making sure we do this after we set the other values.
  }

  @RDF("foaf:data")
  public String getData() {
    return data;
  }

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
