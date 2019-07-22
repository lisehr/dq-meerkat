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
  private String blockId;
  private boolean isEmpty = true;

  
  public MiniChain() {
    super();
  }
  
  public MiniChain(String id) {
    super(id);
  }
  
  public MiniChain(int difficulty, String id) {
    super(difficulty, id);
    this.blockId = "";
  }

  /**
   * @return the blockId
   */
  @RDF("foaf:blockId")
  public String getBlockId() {
    return blockId;
  }

  /**
   * @param blockId the blockId to set
   */
  public void setBlockId(String blockId) {
    this.blockId = blockId;
  }

  /**
   * @return the isEmpty
   */
  @RDF("foaf:isEmpty")
  public boolean isEmpty() {
    return isEmpty;
  }

  /**
   * @param isEmpty the isEmpty to set
   */
  public void setEmpty(boolean isEmpty) {
    this.isEmpty = isEmpty;
  }
    
  public boolean isAddable(Block block) {
    return this.blockId.equals(block.getId());
  }
  
  @Override
  public boolean addBlock(Block block) {
    if (block == null) return false;
    if (isEmpty) {
      isEmpty = false;
      this.blockId = block.getId();
      
    }
    return super.addBlock(block);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (!(obj instanceof MiniChain)) return false;
    MiniChain other = (MiniChain) obj;
    return Objects.equals(blockId, other.blockId) && isEmpty == other.isEmpty;
  }

  @Override
  public int compareTo(MiniChain other) {
    return this.getId().compareTo(other.getId());
  }
  
  

}
