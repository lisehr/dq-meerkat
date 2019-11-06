package dqm.jku.trustkg.util.export;

public class LabelTriple<E, F, G> {
  private E key;
  private F cat;
  private G label;

  public LabelTriple(E key, F cat, G label) {
    this.key = key;
    this.cat = cat;
    this.label = label;
  }
  
  public E getKey() {
    return key;
  }
  
  public F getCat(E key) {
    if(this.key.equals(key)) return cat;
    return null;
  }
  
  public G getLabel(E key) {
    if(this.key.equals(key)) return label;
    return null;
  }
}
