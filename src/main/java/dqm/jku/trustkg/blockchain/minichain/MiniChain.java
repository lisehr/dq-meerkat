package dqm.jku.trustkg.blockchain.minichain;

import java.util.Objects;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

import dqm.jku.trustkg.blockchain.Block;
import dqm.jku.trustkg.blockchain.standardchain.BlockChain;

@RDFNamespaces({ "foaf = http://xmlns.com/foaf/0.1/", "bc = http://example.com/structures/blockchain/" })
@RDFBean("foaf:MiniChain")
public class MiniChain extends BlockChain implements Comparable<MiniChain> {
  private String chainId = ""; // the id of the minichain
  private boolean isEmpty = true; // flag if the chain is empty yet

  public MiniChain() {
    super();
  }

  public MiniChain(String id) {
    super(id);
    this.chainId = id;
  }

  public MiniChain(int difficulty, String id) {
    super(difficulty, id);
  }

  /**
   * Gets the chain id
   * 
   * @return the chainId
   */
  @RDF("foaf:chainId")
  public String getChainId() {
    return chainId;
  }

  /**
   * Sets the chain id (security threat but needed for rdfbeans)
   * 
   * @param chainId the chainId to set
   */
  public void setChainId(String chainId) {
    this.chainId = chainId;
  }

  /**
   * Gets the empty flag
   * 
   * @return the isEmpty
   */
  @RDF("foaf:isEmpty")
  public boolean isEmpty() {
    return isEmpty;
  }

  /**
   * Sets the empty flag (security threat but needed for rdfbeans)
   * 
   * @param isEmpty the isEmpty to set
   */
  public void setEmpty(boolean isEmpty) {
    this.isEmpty = isEmpty;
  }

  /**
   * Checks if the block is addable to this minichain
   * 
   * @param block the block to be added
   * @return true if addable, false otherwise
   */
  public boolean isAddable(Block block) {
    return this.chainId.equals(block.getId());
  }

  @Override
  public boolean addBlock(Block block) {
    if (block == null) return false;
    if (isEmpty) {
      isEmpty = false;
      if (chainId.equals("")) this.chainId = block.getId();
    }
    return super.addBlock(block);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (!(obj instanceof MiniChain)) return false;
    MiniChain other = (MiniChain) obj;
    return Objects.equals(chainId, other.chainId) && isEmpty == other.isEmpty;
  }

  @Override
  public int compareTo(MiniChain other) {
    return this.getId().compareTo(other.getId());
  }
}