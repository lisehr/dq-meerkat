package dqm.jku.trustkg.blockchain.minichain;

import java.util.SortedSet;
import java.util.TreeSet;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFContainer;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;

import dqm.jku.trustkg.blockchain.Block;
import dqm.jku.trustkg.blockchain.blocks.DSDBlock;
import dqm.jku.trustkg.dsd.elements.DSDElement;

/**
 * Blockchain implementation for creating blockchains with a set of minichains.
 * 
 * @author optimusseptim
 * 
 */
@RDFNamespaces({ "dsd = http://dqm.faw.jku.at/dsd#" })
@RDFBean("dsd:MiniBlockChain")
public class MiniBlockChain {
  private SortedSet<MiniChain> minichains = new TreeSet<>(); // the set of minichains for each dsdblock set
  private String id; // the id of the miniblockchain

  public MiniBlockChain() {

  }

  public MiniBlockChain(String id) {
    this.id = id;
  }

  /**
   * Gets the id of the chain
   * 
   * @return the id
   */
  @RDFSubject
  public String getId() {
    return id;
  }

  /**
   * Sets the id (security threat but needed for rdfbeans)
   * 
   * @param id the id to set
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Gets the minichains stored in the chain
   * 
   * @return the minichains
   */
  @RDF("dsd:hasMinichain")
  @RDFContainer
  public SortedSet<MiniChain> getMinichains() {
    return minichains;
  }

  /**
   * Sets the minichains (security threat but needed for rdfbeans)
   * 
   * @param minichains the minichains to set
   */
  public void setMinichains(SortedSet<MiniChain> minichains) {
    this.minichains = minichains;
  }

  /**
   * Gets the minichain by its id
   * 
   * @param chainId the id of the stored block
   * @return the minichain if found, null otherwise
   */
  public MiniChain getMiniChain(String chainId) {
    for (MiniChain m : minichains) {
      if (m.getChainId().equals(chainId)) return m;
    }
    return null;
  }

  /**
   * Accesses the element of a minichain by its blockId and index in the minichain
   * 
   * @param chainId the id of the minichain
   * @param index   the index of the block
   * @return block if found, null otherwise
   */
  public Block accessElementOfMiniChain(String chainId, int index) {
    MiniChain target = getMiniChain(chainId);
    if (target == null) return null;
    return target.accessBlock(index);
  }

  /**
   * Check if the chain is valid
   * 
   * @return true if every minichain is valid, false if at least one is invalid
   */
  public boolean areChainsValid() {
    for (MiniChain m : minichains) if (!m.isChainValid()) return false;
    return true;
  }

  /**
   * Checks if a specific minichain is valid
   * 
   * @param chainId the id of the minichain
   * @return true if valid, false if not found or not valid
   */
  public boolean isSpecificChainValid(String chainId) {
    MiniChain target = getMiniChain(chainId);
    if (target == null) return false;
    return target.isChainValid();
  }

  /**
   * Adds a block to the minichain network. The block is added to an existing
   * minichain or a new one is created
   * 
   * @param block the block to be set
   * @param uri   the uri of the minichain
   * @return true if added, false otherwise
   */
  public boolean addBlock(Block block, String uri) {
    if (block == null) return false;
    MiniChain target = getMiniChain(uri);
    if (target == null) {
      addMiniChain(block, uri);
      return true;
    } else return target.addBlock(block);
  }

  /**
   * Shortcut for adding a DSDElement to the existing minichain network. Works
   * internally with addBlock
   * 
   * @param e the DSDElement to be added
   * @return true if added, false otherwise
   */
  public boolean addDSDElement(DSDElement e) {
    return this.addBlock(new DSDBlock(id, findPreviousHash(e), e), e.getURI());
  }

  /**
   * Helper method for adding a new minichain to the minichain network
   * 
   * @param block the genesis block of the new chain
   * @param uri   the uri of the block (minichain id)
   */
  private void addMiniChain(Block block, String uri) {
    MiniChain target = new MiniChain(uri);
    target.addBlock(block);
    minichains.add(target);
  }

  /**
   * Finds the previousHash of the minichain via a dsd element
   * 
   * @param e the dsdelement to be checked
   * @return previousHash if found, "0" if not (genesis block)
   */
  public String findPreviousHash(DSDElement e) {
    for (MiniChain m : minichains) if (m.getChainId().equals(e.getURI())) return m.getPreviousHash();
    return "0";
  }

  /**
   * Flags a specific minichain as deleted, does nothing if no minichain is found
   * 
   * @param chainId the id of the minichain
   */
  public void deleteChain(String chainId) {
    MiniChain mc = this.getMiniChain(chainId);
    if (mc == null) return;
    mc.delete();
  }

  /**
   * Flags a specific minichain as merged, does nothing if no minichain is found
   * 
   * @param chainId the id of the minichain
   */
  public void mergeChain(String chainId) {
    MiniChain mc = this.getMiniChain(chainId);
    if (mc == null) return;
    mc.merge();
  }

}
