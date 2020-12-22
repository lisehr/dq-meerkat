package dqm.jku.dqmeerkat.util.sparse;
public class SparseVectorEntry<R>{
	public final R lable;
	public final double value;
	
	public SparseVectorEntry(R lable, double value) {
		super();
		this.lable = lable;
		this.value = value;
	}
}
