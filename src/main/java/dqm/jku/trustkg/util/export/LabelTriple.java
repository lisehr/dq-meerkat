package dqm.jku.trustkg.util.export;

/**
 * Class for instanciating Triples used for labeling profiling metrics with
 * categories and titles according to a key.
 * 
 * @author optimusseptim
 *
 * @param <E> generic for the key
 * @param <F> generic for the category
 * @param <G> generic for the labeltitle
 */
public class LabelTriple<E, F, G> {
  private E key;
  private F cat;
  private G label;

  public LabelTriple(E key, F cat, G label) {
    this.key = key;
    this.cat = cat;
    this.label = label;
  }

  /**
   * Gets the key value
   * 
   * @return key
   */
  public E getKey() {
    return key;
  }

  /**
   * Gets the category
   * 
   * @param key the key to be searched
   * @return category, null if key not found
   */
  public F getCat(E key) {
    if (this.key.equals(key)) return cat;
    return null;
  }

  /**
   * Gets the label
   * 
   * @param key the key to be searched
   * @return label, null if key not found
   */
  public G getLabel(E key) {
    if (this.key.equals(key)) return label;
    return null;
  }
}
