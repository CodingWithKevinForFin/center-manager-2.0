package com.f1.utils.structs;

import java.util.Arrays;

import com.f1.base.ToStringable;
import com.f1.utils.AH;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class TwoDimensionArray<T> implements ToStringable {
	private static final Object[][] EMPTY_OBJECT_ARRAY = new Object[][] {};

	//y --> x --> value
	private Object[][] rows = EMPTY_OBJECT_ARRAY;
	private int width;
	private int height;

	public TwoDimensionArray(int width, int height) {
		OH.assertGe(width, 0);
		OH.assertGe(height, 0);
		this.width = width;
		this.height = height;
	}
	public void setWidth(int width) {
		if (width != this.width) {
			if (width < this.width) {
				if (width < 0)
					throw new IndexOutOfBoundsException(SH.toString(width));
				else if (width == 0)
					rows = EMPTY_OBJECT_ARRAY;
				else {
					//forward scan each row & check to see if the array needs to be reduced
					for (int i = 0; i < rows.length; i++) {
						final Object row[] = rows[i];
						if (row != null)
							for (int j = width; j < row.length; j++)
								row[j] = null;//allow gc to do its work
					}
				}
			}
			this.width = width;
		}

	}
	public void setHeight(int height) {
		if (height != this.height) {
			if (height < this.height) {
				if (height < 0)
					throw new IndexOutOfBoundsException(SH.toString(height));
				else if (height == 0)
					rows = EMPTY_OBJECT_ARRAY;
				else {
					for (int j = height; j < rows.length; j++)
						rows[j] = null;//allow gc to do its work
				}
			}
			this.height = height;
		}
	}

	public T getAt(int x, int y) {
		// x is column/width, y is row/height
		if (y < rows.length) {
			Object[] row = rows[y];
			if (row != null && x < row.length)
				return (T) row[x];
		} else {
			OH.assertLt(y, height);
		}
		OH.assertLt(x, width);
		return null;
	}
	public Object getNoThrow(int x, int y) {
		if (y < rows.length) {
			Object[] row = rows[y];
			if (row != null && x < row.length)
				return (T) row[x];
		}
		return null;
	}
	public T set(int x, int y, T value) {
		if (y < rows.length) {
			// get the row
			Object[] row = rows[y];
			if (row == null) {
				OH.assertLt(x, width);
				// add it using the col ind
				(rows[y] = new Object[width])[x] = value;
				return null;
			} else if (x < row.length) {
				T r = (T) row[x];
				row[x] = value;
				return r;
			} else {
				OH.assertLt(x, width);
				rows[y] = row = Arrays.copyOf(row, width);
				row[x] = value;
				return null;
			}
		} else {
			OH.assertLt(y, height);
			OH.assertLt(x, width);
			rows = Arrays.copyOf(rows, height);
			(rows[y] = new Object[width])[x] = value;
			return null;
		}
	}
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	@Override
	public StringBuilder toString(StringBuilder sb) {
		if (height == 0)
			return sb.append("[]");
		else if (width == 0)
			return SH.repeat("[],", height - 1, sb.append('[')).append("[]]");
		else if (rows.length == 0) {
			sb.append('[');
			for (int i = 0; i < height; i++) {
				if (i == 0)
					sb.append('[');
				else
					sb.append(",[");
				SH.repeat("null,", width - 1, sb);
				sb.append("null]");
			}
			return sb.append(']');
		}
		for (int i = 0; i < this.height; i++) {
			sb.append(i == 0 ? "[[" : "],[");
			Object[] row = i < rows.length ? rows[i] : null;
			if (row == null || row.length == 0) {
				SH.repeat("null,", width - 1, sb).append("null");
			} else {
				SH.s(row[0], sb);
				int w = Math.min(width, row.length);
				int j = 1;
				while (j < w)
					SH.s(row[j++], sb.append(','));
				while (j++ < width)
					sb.append(",null");
			}
		}
		return sb.append("]]");
	}

	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	public void removeRow(int row) {
		OH.assertBetweenExcluding(row, 0, this.height);
		this.height--;
		AH.removeInplace(this.rows, row, 1);
	}
	public void removeCol(int col) {
		OH.assertBetweenExcluding(col, 0, this.width);
		this.width--;
		for (int i = 0; i < this.height; i++)
			AH.removeInplace(this.rows[i], col, 1);
	}
	public void insertRow(int row) {
		this.rows = AH.insert(this.rows, row, (Object[]) null);
		this.height++;
	}
	public void insertCol(int col) {
		for (int i = 0; i < this.height; i++)
			this.rows[i] = AH.insert(this.rows[i], col, null);
		this.width++;
	}
	public int getXIndex(T o) {
		// x is columns/width, y is rows/height
		// column first, then row
		for (int y = 0; y < this.height; y++)
			if (this.rows[y] != null)
				for (int x = 0; x < this.width; x++)
					if (OH.eq(this.rows[y][x], o))
						return x;
		return -1;
	}
	public int getYIndex(T o) {
		for (int y = 0; y < this.height; y++)
			if (this.rows[y] != null)
				for (int x = 0; x < this.width; x++)
					if (OH.eq(this.rows[y][x], o))
						return y;
		return -1;
	}
}
