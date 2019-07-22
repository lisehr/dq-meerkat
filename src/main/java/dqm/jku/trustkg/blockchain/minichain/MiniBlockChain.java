package dqm.jku.trustkg.blockchain.minichain;

import java.util.SortedSet;
import java.util.TreeSet;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;

import dqm.jku.trustkg.blockchain.Block;
import dqm.jku.trustkg.blockchain.blocks.DSDBlock;
import dqm.jku.trustkg.dsd.elements.DSDElement;

@RDFNamespaces({ "foaf = http://xmlns.com/foaf/0.1/", "mbc = http://example.com/structures/miniblockchain/" })
@RDFBean("foaf:MiniBlockChain")
public class MiniBlockChain {
  private SortedSet<MiniChain> minichains = new TreeSet<>();
  private String id;

  public MiniBlockChain() {

  }

  public MiniBlockChain(String id) {
    this.id = id;
  }

  /**
   * @return the id
   */
  @RDFSubject(prefix = "mbc:")
  public String getId() {
    return id;
  }

  /**
   * @param id the id to set
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * @return the minichains
   */
  @RDF("foaf:hasMinichain")
  public SortedSet<MiniChain> getMinichains() {
    return minichains;
  }

  /**
   * @param minichains the minichains to set
   */
  public void setMinichains(SortedSet<MiniChain> minichains) {
    this.minichains = minichains;
  }

  public MiniChain getMiniChain(String blockId) {
    for (MiniChain m : minichains) {
      if (m.getBlockId().equals(blockId)) return m;
    }
    return null;
  }

  public Block accessElementOfMiniChain(String blockId, int index) {
    MiniChain target = getMiniChain(blockId);
    if (target == null) return null;
    return target.accessBlock(index);
  }

  public boolean areChainsValid() {
    for (MiniChain m : minichains)
      if (!m.isChainValid()) return false;
    return true;
  }

  public boolean isSpecificChainValid(String blockId) {
    MiniChain target = getMiniChain(blockId);
    if (target == null) return false;
    return target.isChainValid();
  }

  public boolean addBlock(Block block, String uri) {
    if (block == null) return false;
    MiniChain target = getMiniChain(uri);
    if (target == null) {
      addMiniChain(block, uri);
      return true;
    } else return target.addBlock(block);
  }

  public boolean addDSDElement(DSDElement e) {
    return this.addBlock(new DSDBlock(findPreviousHash(e), e), e.getURI());
  }

  private void addMiniChain(Block block, String uri) {
    MiniChain target = new MiniChain(uri);
    target.addBlock(block);
    minichains.add(target);
  }

  public String findPreviousHash(DSDElement e) {
    for (MiniChain m : minichains)
      if (m.getBlockId().equals(e.getURI())) return m.getPreviousHash();
    return "0";
  }

}
