package com.f1.ami.center.table;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.f1.ami.center.table.prepared.AmiPreparedCell;
import com.f1.ami.center.table.prepared.AmiPreparedCell_BigDecimal;
import com.f1.ami.center.table.prepared.AmiPreparedCell_BigInt;
import com.f1.ami.center.table.prepared.AmiPreparedCell_Binary;
import com.f1.ami.center.table.prepared.AmiPreparedCell_Boolean;
import com.f1.ami.center.table.prepared.AmiPreparedCell_Byte;
import com.f1.ami.center.table.prepared.AmiPreparedCell_Char;
import com.f1.ami.center.table.prepared.AmiPreparedCell_Complex;
import com.f1.ami.center.table.prepared.AmiPreparedCell_Double;
import com.f1.ami.center.table.prepared.AmiPreparedCell_Enum;
import com.f1.ami.center.table.prepared.AmiPreparedCell_Float;
import com.f1.ami.center.table.prepared.AmiPreparedCell_Int;
import com.f1.ami.center.table.prepared.AmiPreparedCell_Long;
import com.f1.ami.center.table.prepared.AmiPreparedCell_Short;
import com.f1.ami.center.table.prepared.AmiPreparedCell_String;
import com.f1.ami.center.table.prepared.AmiPreparedCell_UTC;
import com.f1.ami.center.table.prepared.AmiPreparedCell_UTCN;
import com.f1.ami.center.table.prepared.AmiPreparedCell_UUID;
import com.f1.base.Bytes;
import com.f1.base.Complex;
import com.f1.base.ToStringable;
import com.f1.base.UUID;
import com.f1.utils.OH;
import com.f1.utils.ToDoException;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiPreparedRowImpl implements AmiPreparedRow, ToStringable {

	private static final long DOUBLE_NULL_AS_LONG_BITS = Double.doubleToLongBits(AmiTable.NULL_DECIMAL);

	private long revision = 1;//incremented each time reset is called
	final private AmiPreparedCell[] cells;//one per column in the target table
	final AmiPreparedCell[] setFields;//Fields that have been set to a value
	private int setFieldsCount;//Number of fields that have been set

	final private AmiTableImpl amiTable;

	private boolean ignoreWriteFailed;
	final private boolean includeReserved;

	public AmiPreparedRowImpl(AmiTableImpl table, boolean includeReserved) {
		this.amiTable = table;
		this.includeReserved = includeReserved;
		this.setFields = new AmiPreparedCell[this.amiTable.getColumnsCount()];
		this.cells = new AmiPreparedCell[this.amiTable.getColumnsCount()];
		for (int i = 0; i < table.getColumnsCount(); i++) {
			AmiColumnImpl<?> col = table.getColumnAt(i);
			switch (col.getAmiType()) {
				case AmiTable.TYPE_DOUBLE:
					cells[i] = new AmiPreparedCell_Double(col, includeReserved);
					break;
				case AmiTable.TYPE_FLOAT:
					cells[i] = new AmiPreparedCell_Float(col, includeReserved);
					break;
				case AmiTable.TYPE_BOOLEAN:
					cells[i] = new AmiPreparedCell_Boolean(col, includeReserved);
					break;
				case AmiTable.TYPE_INT:
					cells[i] = new AmiPreparedCell_Int(col, includeReserved);
					break;
				case AmiTable.TYPE_SHORT:
					cells[i] = new AmiPreparedCell_Short(col, includeReserved);
					break;
				case AmiTable.TYPE_BYTE:
					cells[i] = new AmiPreparedCell_Byte(col, includeReserved);
					break;
				case AmiTable.TYPE_CHAR:
					cells[i] = new AmiPreparedCell_Char(col, includeReserved);
					break;
				case AmiTable.TYPE_LONG:
					cells[i] = new AmiPreparedCell_Long(col, includeReserved);
					break;
				case AmiTable.TYPE_UTC:
					cells[i] = new AmiPreparedCell_UTC(col, includeReserved);
					break;
				case AmiTable.TYPE_UTCN:
					cells[i] = new AmiPreparedCell_UTCN(col, includeReserved);
					break;
				case AmiTable.TYPE_STRING:
					cells[i] = new AmiPreparedCell_String(col, includeReserved);
					break;
				case AmiTable.TYPE_ENUM:
					cells[i] = new AmiPreparedCell_Enum(col, includeReserved);
					break;
				case AmiTable.TYPE_COMPLEX:
					cells[i] = new AmiPreparedCell_Complex(col, includeReserved);
					break;
				case AmiTable.TYPE_BIGDEC:
					cells[i] = new AmiPreparedCell_BigDecimal(col, includeReserved);
					break;
				case AmiTable.TYPE_BIGINT:
					cells[i] = new AmiPreparedCell_BigInt(col, includeReserved);
					break;
				case AmiTable.TYPE_UUID:
					cells[i] = new AmiPreparedCell_UUID(col, includeReserved);
					break;
				case AmiTable.TYPE_BINARY:
					cells[i] = new AmiPreparedCell_Binary(col, includeReserved);
					break;
				default:
					throw new RuntimeException("Bad col type: " + col.getAmiType());
			}
		}
		reset();
	}
	@Override
	public boolean setLong(AmiColumn col, long value) {
		AmiPreparedCell cell = this.cells[col.getLocation()];
		if (!cell.canSet()) {
			if (ignoreWriteFailed)
				return false;
			throw new RuntimeException("Can not set read only column " + col.getName() + " to " + value);
		}
		cell.setLong(value);
		markIsSet(cell);
		return true;
	}
	private void markIsSet(AmiPreparedCell col) {
		if (col.setRevision(this.revision))
			this.setFields[this.setFieldsCount++] = col;
	}
	@Override
	public boolean setDouble(AmiColumn col, double value) {
		AmiPreparedCell cell = this.cells[col.getLocation()];
		if (!cell.canSet()) {
			if (ignoreWriteFailed)
				return false;
			throw new RuntimeException("Can not set read only column " + col.getName() + " to " + value);
		}
		cell.setDouble(value);
		markIsSet(cell);
		return true;
	}
	@Override
	public boolean setString(AmiColumn col, String value) {
		AmiPreparedCell cell = this.cells[col.getLocation()];
		if (!cell.canSet()) {
			if (ignoreWriteFailed)
				return false;
			throw new RuntimeException("Can not set read only column " + col.getName() + " to " + value);
		}
		cell.setString(value);
		markIsSet(cell);
		return true;
	}
	@Override
	public boolean setNull(AmiColumn col) {
		AmiPreparedCell cell = this.cells[col.getLocation()];
		if (!cell.canSet()) {
			if (ignoreWriteFailed)
				return false;
			throw new RuntimeException("Can not set read only column " + col.getName() + " to null");
		}
		cell.setNull();
		markIsSet(cell);
		return true;
	}

	@Override
	public long getLong(AmiColumn col) {
		OH.assertTrue(isSet(col));
		return this.cells[col.getLocation()].getLong();
	}
	@Override
	public double getDouble(AmiColumn col) {
		OH.assertTrue(isSet(col));
		return this.cells[col.getLocation()].getDouble();
	}
	@Override
	public String getString(AmiColumn col) {
		return !isSet(col) ? null : this.cells[col.getLocation()].getString();
	}

	@Override
	public Comparable getComparable(AmiColumn col) {
		return !isSet(col) ? null : this.cells[col.getLocation()].getComparable();
	}
	@Override
	public boolean setComparable(String col, Comparable value) {
		return setComparable(getColumn(col), value);
	}
	@Override
	public boolean setComparable(int pos, Comparable value) {
		AmiPreparedCell cell = this.cells[pos];
		if (!cell.canSet()) {
			if (ignoreWriteFailed)
				return false;
			throw new RuntimeException("Can not set read only column " + cell.getColumn().getName() + " to " + value);
		}
		cell.setComparable(value);
		markIsSet(cell);
		return true;
	}
	@Override
	public boolean setComparable(AmiColumn col, Comparable value) {
		AmiPreparedCell cell = this.cells[col.getLocation()];
		if (!cell.canSet()) {
			if (ignoreWriteFailed)
				return false;
			throw new RuntimeException("Can not set read only column " + col.getName() + " to " + value);
		}
		cell.setComparable(value);
		markIsSet(cell);
		return true;
	}
	@Override
	public boolean getIsNull(AmiColumn col) {
		return !isSet(col) || this.cells[col.getLocation()].getIsNull();
	}

	@Override
	public int getRowNum() {
		return -1;
	}

	@Override
	public AmiTableImpl getAmiTable() {
		return amiTable;
	}

	private AmiColumnImpl<?> getColumn(String col) {
		return amiTable.getColumn(col);
	}

	private AmiColumnImpl<?> getColumn(int col) {
		return amiTable.getColumnAt(col);
	}

	@Override
	public boolean getIsNull(int colpos) {
		return !isSet(colpos) || this.cells[colpos].getIsNull();
	}

	@Override
	public boolean getIsNull(String col) {
		return getIsNull(getColumn(col));
	}

	@Override
	public boolean setNull(int colpos) {
		AmiPreparedCell cell = this.cells[colpos];
		if (!cell.canSet()) {
			if (ignoreWriteFailed)
				return false;
			throw new RuntimeException("Can not set read only column " + cell.getColumn().getName() + " to null");
		}
		cell.setNull();
		markIsSet(cell);
		return true;
	}

	@Override
	public boolean setNull(String col) {
		return setNull(getColumn(col));

	}

	@Override
	public long getAmiId() {
		return -1L;
	}

	@Override
	public String getString(int colpos) {
		return !isSet(colpos) ? null : this.cells[colpos].getString();
	}

	@Override
	public String getString(String col) {
		return getString(getColumn(col));
	}

	@Override
	public boolean setString(int colpos, String value) {
		AmiPreparedCell cell = this.cells[colpos];
		if (!cell.canSet()) {
			if (ignoreWriteFailed)
				return false;
			throw new RuntimeException("Can not set read only column " + cell.getColumn().getName() + " to " + value);
		}
		cell.setString(value);
		markIsSet(cell);
		return true;
	}

	@Override
	public boolean setString(String col, String value) {
		return setString(getColumn(col), value);
	}

	@Override
	public long getLong(int colpos) {
		OH.assertTrue(isSet(colpos));
		return this.cells[colpos].getLong();
	}

	@Override
	public long getLong(String col) {
		return getLong(getColumn(col));
	}

	@Override
	public boolean setLong(int colpos, long value) {
		AmiPreparedCell cell = this.cells[colpos];
		if (!cell.canSet()) {
			if (ignoreWriteFailed)
				return false;
			throw new RuntimeException("Can not set read only column " + cell.getColumn().getName() + " to " + value);
		}
		cell.setLong(value);
		markIsSet(cell);
		return true;
	}

	@Override
	public boolean setLong(String col, long value) {
		return setLong(getColumn(col), value);
	}

	@Override
	public double getDouble(int colpos) {
		OH.assertTrue(isSet(colpos));
		return this.cells[colpos].getDouble();
	}

	@Override
	public double getDouble(String col) {
		return getDouble(getColumn(col));
	}

	@Override
	public boolean setDouble(int colpos, double value) {
		AmiPreparedCell cell = this.cells[colpos];
		if (!cell.canSet()) {
			if (ignoreWriteFailed)
				return false;
			throw new RuntimeException("Can not set read only column " + cell.getColumn().getName() + " to " + value);
		}
		cell.setDouble(value);
		markIsSet(cell);
		return true;
	}

	@Override
	public boolean setDouble(String col, double value) {
		return setDouble(getColumn(col), value);
	}

	@Override
	public void reset() {
		this.ignoreWriteFailed = false;
		if (this.setFieldsCount != 0) {
			revision++;
			this.setFieldsCount = 0;
		}
	}

	public void setIgnoreWriteFailed(boolean iwf) {
		this.ignoreWriteFailed = iwf;
	}
	public boolean getIgnoreWriteFailed() {
		return this.ignoreWriteFailed;
	}
	@Override
	public boolean isSet(AmiColumn field) {
		return this.cells[field.getLocation()].getRevision() == this.revision;
	}
	public boolean isSet(int colpos) {
		return this.cells[colpos].getRevision() == this.revision;
	}
	@Override
	public void setVisible(boolean b) {
		throw new ToDoException();
	}
	@Override
	public boolean getVisible() {
		return false;
	}
	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append("[");
		boolean first = true;
		for (int i = 0; i < amiTable.getColumnsCount(); i++) {
			AmiColumnImpl<?> column = amiTable.getColumnAt(i);
			if (!isSet(column))
				continue;
			if (first)
				first = false;
			else
				sink.append(", ");
			sink.append(column.getName()).append("=");
			sink.append(getString(column));
		}
		return sink.append("]");
	}
	@Override
	public Object putAt(int i, Object value) {
		if (value == null)
			setNull(i);
		else if (value instanceof Number) {
			if (value instanceof Double || value instanceof Float)
				setDouble(i, ((Number) value).doubleValue());
			else if (value instanceof BigDecimal || value instanceof BigInteger || value instanceof Complex)
				setComparable(i, (Comparable) value);
			else
				setLong(i, ((Number) value).longValue());
		} else if (value instanceof Boolean) {
			setLong(i, ((Boolean) value).booleanValue() ? 1L : 0L);
		} else if (value instanceof Character)
			setLong(i, (Character) value);
		else if (value instanceof String)
			setString(i, (String) value);
		else if (value instanceof Bytes)
			setComparable(i, (Bytes) value);
		else if (value instanceof UUID)
			setComparable(i, (UUID) value);
		else
			throw new ClassCastException("Unknown type: " + value.getClass());
		return null;
	}
	@Override
	public int size() {
		return this.setFields.length;
	}
	@Override
	public boolean isEmpty() {
		return false;
	}
	@Override
	public boolean containsKey(Object key) {
		AmiColumnImpl<?> col = this.amiTable.getColumnNoThrow((String) key);
		return col != null & isSet(col);
	}
	@Override
	public boolean containsValue(Object value) {
		for (int i = 0; i < this.setFieldsCount; i++)
			if (OH.eq(value, this.setFields[i].getComparable()))
				return true;
		return false;
	}
	@Override
	public Object get(Object key) {
		return getComparable(this.amiTable.getColumn((String) key));
	}
	@Override
	public Object put(String key, Object value) {
		if (key instanceof String) {
			AmiColumnImpl<?> col = amiTable.getColumnNoThrow((String) key);
			if (col != null && value instanceof Comparable) {
				AmiPreparedCell cell = this.cells[col.getLocation()];
				if (cell.canSet()) {
					cell.setComparable((Comparable) value);
					markIsSet(cell);
				}
			}
		}
		return null;
	}
	@Override
	public Object getValue(String key) {
		return getComparable(this.amiTable.getColumn(key));
	}
	@Override
	public Object putValue(String key, Object value) {
		AmiColumnImpl<?> col = amiTable.getColumnNoThrow((String) key);
		if (col != null && value instanceof Comparable) {
			AmiPreparedCell cell = this.cells[col.getLocation()];
			if (cell.canSet()) {
				cell.setComparable((Comparable) value);
				markIsSet(cell);
			}
		}
		return null;
	}
	@Override
	public Object remove(Object key) {
		throw new UnsupportedOperationException();
	}
	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		for (Entry<? extends String, ? extends Object> entry : m.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}
	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}
	@Override
	public Set<String> keySet() {
		return this.amiTable.getTable().getColumnsMap().keySet();
	}

	private Collection<Object> values;

	@Override
	public Collection values() {
		if (this.values == null)
			values = new ValuesCollection();
		return this.values;
	}

	public class ValuesCollection implements Collection<Object> {

		@Override
		public int size() {
			return AmiPreparedRowImpl.this.setFieldsCount;
		}

		@Override
		public boolean isEmpty() {
			return AmiPreparedRowImpl.this.setFieldsCount == 0;
		}

		@Override
		public boolean contains(Object o) {
			return AmiPreparedRowImpl.this.containsValue(o);
		}

		@Override
		public Iterator<Object> iterator() {
			return AmiPreparedRowImpl.this.valuesIterator();
		}

		@Override
		public Object[] toArray() {
			throw new UnsupportedOperationException();
		}

		@Override
		public <T> T[] toArray(T[] a) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean add(Object e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(Object o) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			for (Object o : c) {
				if (!this.contains(o))
					return false;
			}
			return true;
		}

		@Override
		public boolean addAll(Collection<? extends Object> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}
	}

	public Iterator<Object> valuesIterator() {
		return new ValuesIterator();
	}

	public class ValuesIterator implements Iterator<Object> {
		private int pos = 0; // -1 IS EMPTY

		public ValuesIterator() {
			// Do nothing
		}
		@Override
		public boolean hasNext() {
			return pos < setFieldsCount;
		}
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		@Override
		public Object next() {
			return AmiPreparedRowImpl.this.setFields[pos++].getComparable();
		}
	}

	private Set<Map.Entry<String, Object>> entrySet;

	@Override
	public Set entrySet() {
		if (this.entrySet == null) {
			entrySet = new EntrySet();
		}
		return this.entrySet;
		//throw new UnsupportedOperationException();
	}

	public class EntrySet implements Set<Entry<String, Object>> {
		@Override
		public int size() {
			return AmiPreparedRowImpl.this.setFieldsCount;
		}

		@Override
		public boolean isEmpty() {
			return AmiPreparedRowImpl.this.setFieldsCount == 0;
		}

		@Override
		public boolean contains(Object o) {
			if (o instanceof AmiPreparedCell) {
				final AmiPreparedCell e = (AmiPreparedCell) o;
				return AmiPreparedRowImpl.this.containsKey(e.getKey()) && OH.eq(e.getValue(), AmiPreparedRowImpl.this.get(e.getKey()));
			}
			return false;
		}

		@Override
		public Iterator<Entry<String, Object>> iterator() {
			return AmiPreparedRowImpl.this.entryIterator();
		}

		@Override
		public Object[] toArray() {
			throw new UnsupportedOperationException();
		}

		@Override
		public <T> T[] toArray(T[] a) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean add(Entry<String, Object> e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(Object o) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			for (Object o : c) {
				if (!this.contains(o))
					return false;
			}
			return true;
		}

		@Override
		public boolean addAll(Collection<? extends Entry<String, Object>> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}
	}

	public Iterator<Entry<String, Object>> entryIterator() {
		return new EntryIterator();
	}

	public class EntryIterator implements Iterator<Entry<String, Object>> {
		private int pos = 0;

		public EntryIterator() {
		}
		@Override
		public boolean hasNext() {
			return pos < setFieldsCount;
		}
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		@Override
		public Entry<String, Object> next() {
			return AmiPreparedRowImpl.this.setFields[pos++];
		}
	}

	@Override
	public boolean setString(int colpos, String value, CalcFrameStack sf) {
		return setString(colpos, value);
	}
	@Override
	public boolean setString(String col, String value, CalcFrameStack sf) {
		return setString(col, value);
	}
	@Override
	public boolean setString(AmiColumn col, String value, CalcFrameStack sf) {
		return setString(col, value);
	}
	@Override
	public boolean setLong(int colpos, long value, CalcFrameStack sf) {
		return setLong(colpos, value);
	}
	@Override
	public boolean setLong(String col, long value, CalcFrameStack sf) {
		return setLong(col, value);
	}
	@Override
	public boolean setLong(AmiColumn col, long value, CalcFrameStack sf) {
		return setLong(col, value);
	}
	@Override
	public boolean setDouble(int colpos, double value, CalcFrameStack sf) {
		return setDouble(colpos, value);
	}
	@Override
	public boolean setDouble(String col, double value, CalcFrameStack sf) {
		return setDouble(col, value);
	}
	@Override
	public boolean setDouble(AmiColumn col, double value, CalcFrameStack sf) {
		return setDouble(col, value);
	}
	@Override
	public boolean setComparable(int col, Comparable value, CalcFrameStack sf) {
		return setComparable(col, value);
	}
	@Override
	public boolean setComparable(String col, Comparable value, CalcFrameStack sf) {
		return setComparable(col, value);
	}
	@Override
	public boolean setComparable(AmiColumn col, Comparable value, CalcFrameStack sf) {
		return setComparable(col, value);
	}
	@Override
	public boolean setNull(int colpos, CalcFrameStack sf) {
		return setNull(colpos);
	}
	@Override
	public boolean setNull(String col, CalcFrameStack sf) {
		return setNull(col);
	}
	@Override
	public boolean setNull(AmiColumn col, CalcFrameStack sf) {
		return setNull(col);
	}
	@Override
	public Object putAt(int i, Object value, CalcFrameStack sf) {
		return putAt(i, value);
	}

	public int getFieldSetCount() {
		return this.setFieldsCount;
	}

	public AmiPreparedCell getFieldSetAt(int n) {
		return this.setFields[n];
	}

	@Override
	public Comparable getComparable(int col) {
		return getComparable(getColumn(col));
	}
	@Override
	public Comparable getComparable(String col) {
		return getComparable(getColumn(col));
	}
	public boolean getIncludesReserved() {
		return includeReserved;
	}
	@Override
	public Class<?> getType(String key) {
		return this.amiTable.getTable().getColumnTypesMapping().getType(key);
	}
	@Override
	public Iterable<String> getVarKeys() {
		return this.amiTable.getTable().getColumnTypesMapping().getVarKeys();
	}
	@Override
	public int getVarsCount() {
		return this.setFields.length;
	}
	@Override
	public boolean isVarsEmpty() {
		return false;
	}

}
