package util.sparse;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Bernhard
 *
 * @param <R>
 * @param <C>
 */
public class SparseLabeledMatrix<R, C> implements Iterable<SparseMatrixEntry<R, C>> {

	private Map<R, SparseLabeledVector<C>> rows = new ConcurrentHashMap<R, SparseLabeledVector<C>>();
	private Map<C, SparseLabeledVector<R>> cols = new ConcurrentHashMap<C, SparseLabeledVector<R>>();
	private AtomicLong entries = new AtomicLong();

	private SparseLabeledVector<C> getCreateRow(R row) {
		synchronized (row) {
			SparseLabeledVector<C> rowVec = rows.get(row);
			if (rowVec == null) {
				rowVec = new SparseLabeledVector<C>();
				rows.put(row, rowVec);
			}
			return rowVec;
		}

	}

	private SparseLabeledVector<R> getCreateCol(C col) {
		synchronized (col) {
			SparseLabeledVector<R> colVec = cols.get(col);
			if (colVec == null) {
				colVec = new SparseLabeledVector<R>();
				cols.put(col, colVec);
			}
			return colVec;
		}
	}

	/**
	 * @param row
	 *            a row key
	 * @param col
	 *            a column key
	 * @return the specified value or 0
	 */
	public double get(R row, C col) {
		SparseLabeledVector<C> rowVec = rows.get(row);
		if (rowVec == null) {
			return 0;
		}
		return rowVec.get(col);
	}

	/**
	 * @param row
	 *            a row key
	 * @return the SparseLabeledVector associated with row or an empty one
	 */
	public SparseLabeledVector<C> getRow(R row) {
		SparseLabeledVector<C> rowVec = rows.get(row);
		return rowVec == null ? new SparseLabeledVector<C>() : rowVec;
	}

	public Set<R> getRowLabels() {
		return Collections.unmodifiableSet(rows.keySet());
	}

	public Set<C> getColLabels() {
		return Collections.unmodifiableSet(cols.keySet());
	}

	/**
	 * @param col
	 *            a column key
	 * @return the SparseLabeledVector associated with row or an empty one
	 */
	public SparseLabeledVector<R> getCol(C col) {
		SparseLabeledVector<R> rowVec = cols.get(col);
		return rowVec == null ? new SparseLabeledVector<R>() : rowVec;
	}

	/**
	 * @return The number of rows stored
	 */
	public int nRows() {
		return rows.size();
	}

	/**
	 * @return The number of columns stored
	 */
	public int nCols() {
		return cols.size();
	}

	/**
	 * @param row
	 *            a row key
	 * @param col
	 *            a column key
	 * @param value
	 * 
	 *            sets the entry <row,column> to value
	 */
	public void set(R row, C col, double value) {
		if (get(row, col) == 0) {
			entries.incrementAndGet();
		}
		SparseLabeledVector<C> rowVec = getCreateRow(row);
		SparseLabeledVector<R> colVec = getCreateCol(col);
		rowVec.set(col, value);
		colVec.set(row, value);
	}

	/**
	 * @param row
	 * @param col
	 * @param value
	 * 
	 *            Increases the specified entry by value
	 */
	public void inc(R row, C col, double value) {
		double v = get(row, col);
		set(row, col, v + value);
	}

	/**
	 * @param row
	 * @param col
	 * 
	 *            Increases the specified entry by 1.0
	 */
	public void inc(R row, C col) {
		inc(row, col, 1.0);
	}

	public boolean hasRow(R row) {
		return rows.containsKey(row);
	}

	public boolean hasCol(C col) {
		return cols.containsKey(col);
	}

	public boolean hasField(R row, C col) {
		return (cols.containsKey(col) && cols.get(col).keySet().contains(row));
	}

	public Set<R> getRowSet() {
		return rows.keySet();
	}

	public Set<C> getColSet() {
		return cols.keySet();
	}

	/**
	 * @return the number of entries in the matrix were it not sparse.
	 */
	public long getMaxSize() {
		return ((long) rows.size()) * cols.size();
	}

	/**
	 * @return the actual number of stored entries
	 */
	public long nEntries() {
		return entries.get();
	}

	public void multiplyAll(double value) {
		Iterator<R> rowsIterator = rows.keySet().iterator();
		Iterator<C> colsIterator = null;
		while (rowsIterator.hasNext()) {
			R row = rowsIterator.next();
			colsIterator = rows.get(row).iterator();
			while (colsIterator.hasNext()) {
				C col = colsIterator.next();
				set(row, col, get(row, col) * value);
			}
		}
	}

	@Override
	public Iterator<SparseMatrixEntry<R, C>> iterator() {
		return new Iterator<SparseMatrixEntry<R, C>>() {
			Iterator<R> rowsIterator = rows.keySet().iterator();
			Iterator<C> colsIterator = null;
			R row;

			@Override
			public boolean hasNext() {
				while (colsIterator == null || !colsIterator.hasNext()) {
					if (!rowsIterator.hasNext())
						return false;
					row = rowsIterator.next();
					colsIterator = rows.get(row).iterator();
				}
				return true;
			}

			@Override
			public SparseMatrixEntry<R, C> next() {
				while (colsIterator == null || !colsIterator.hasNext()) {
					if (!rowsIterator.hasNext())
						return null;
					row = rowsIterator.next();
					colsIterator = rows.get(row).iterator();
				}
				C col = colsIterator.next();
				return new SparseMatrixEntry<R, C>(row, col, get(row, col));
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException("NYI");
			}

		};
	}

	public void removeRow(R row) {
		rows.remove(row);
		for (SparseLabeledVector<R> col : cols.values()) {
			if (col.remove(row)) {
				entries.decrementAndGet();
			}
		}
	}

	public void removeCol(C col) {
		cols.remove(col);
		for (SparseLabeledVector<C> row : rows.values()) {
			if (row.remove(col)) {
				entries.decrementAndGet();
			}
		}
	}
	
	@Override
	public String toString() {
		String result = "\t | ";
		Iterator<C> iter = getColLabels().iterator();
		while(iter.hasNext()) {
			result += iter.next().toString() + " | ";
		}
		result += "\n";
		for(Entry<R, SparseLabeledVector<C>> r : rows.entrySet()) {
			result += r.getKey() + " | ";
			for(C col : r.getValue()) {
				result += r.getValue().get(col) + " | ";
			}
			result += "\n";
		}
		return result;
	}

}
