package dqm.jku.dqmeerkat.util.sparse;

public class SparseMatrixEntry<R, C> {
	public final R row;
	public final C col;
	public final double d;

	public SparseMatrixEntry(R row, C col, double d) {
		super();
		this.row = row;
		this.col = col;
		this.d = d;
	}
}
