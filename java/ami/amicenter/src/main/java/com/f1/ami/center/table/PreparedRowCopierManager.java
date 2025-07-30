package com.f1.ami.center.table;

import com.f1.base.BasicTypes;
import com.f1.utils.structs.table.columnar.ColumnarColumn;
import com.f1.utils.structs.table.columnar.ColumnarColumnBoolean;
import com.f1.utils.structs.table.columnar.ColumnarColumnByte;
import com.f1.utils.structs.table.columnar.ColumnarColumnChar;
import com.f1.utils.structs.table.columnar.ColumnarColumnDouble;
import com.f1.utils.structs.table.columnar.ColumnarColumnFloat;
import com.f1.utils.structs.table.columnar.ColumnarColumnInt;
import com.f1.utils.structs.table.columnar.ColumnarColumnLong;
import com.f1.utils.structs.table.columnar.ColumnarColumnPrimitive;
import com.f1.utils.structs.table.columnar.ColumnarColumnShort;
import com.f1.utils.structs.table.columnar.ColumnarRow;

public class PreparedRowCopierManager {
	public static class Copier_Long implements PreparedRowCopier {
		private ColumnarColumnLong source;
		private AmiColumn target;

		public Copier_Long(ColumnarColumnLong source, AmiColumn target) {
			this.source = source;
			this.target = target;
		}

		@Override
		public void copy(ColumnarRow srcRow, AmiPreparedRow tgtRow) {
			if (source.isNull(srcRow))
				tgtRow.setNull(target);
			else
				tgtRow.setLong(target, source.getLong(srcRow));
		}
	}

	public static class Copier_Int implements PreparedRowCopier {
		private ColumnarColumnInt source;
		private AmiColumn target;

		public Copier_Int(ColumnarColumnInt source, AmiColumn target) {
			this.source = source;
			this.target = target;
		}

		@Override
		public void copy(ColumnarRow srcRow, AmiPreparedRow tgtRow) {
			if (source.isNull(srcRow))
				tgtRow.setNull(target);
			else
				tgtRow.setLong(target, source.getInt(srcRow));
		}
	}

	public static class Copier_Short implements PreparedRowCopier {
		private ColumnarColumnShort source;
		private AmiColumn target;

		public Copier_Short(ColumnarColumnShort source, AmiColumn target) {
			this.source = source;
			this.target = target;
		}
		@Override
		public void copy(ColumnarRow srcRow, AmiPreparedRow tgtRow) {
			if (source.isNull(srcRow))
				tgtRow.setNull(target);
			else
				tgtRow.setLong(target, source.getShort(srcRow));
		}
	}

	public static class Copier_Byte implements PreparedRowCopier {
		private ColumnarColumnByte source;
		private AmiColumn target;

		public Copier_Byte(ColumnarColumnByte source, AmiColumn target) {
			this.source = source;
			this.target = target;
		}
		@Override
		public void copy(ColumnarRow srcRow, AmiPreparedRow tgtRow) {
			if (source.isNull(srcRow))
				tgtRow.setNull(target);
			else
				tgtRow.setLong(target, source.getByte(srcRow));
		}
	}

	public static class Copier_Double implements PreparedRowCopier {
		private ColumnarColumnDouble source;
		private AmiColumn target;

		public Copier_Double(ColumnarColumnDouble source, AmiColumn target) {
			this.source = source;
			this.target = target;
		}

		@Override
		public void copy(ColumnarRow srcRow, AmiPreparedRow tgtRow) {
			if (source.isNull(srcRow))
				tgtRow.setNull(target);
			else
				tgtRow.setDouble(target, source.getDouble(srcRow));
		}
	}

	public static class Copier_Float implements PreparedRowCopier {
		private ColumnarColumnFloat source;
		private AmiColumn target;

		public Copier_Float(ColumnarColumnFloat source, AmiColumn target) {
			this.source = source;
			this.target = target;
		}
		@Override
		public void copy(ColumnarRow srcRow, AmiPreparedRow tgtRow) {
			if (source.isNull(srcRow))
				tgtRow.setNull(target);
			else
				tgtRow.setDouble(target, source.getFloat(srcRow));
		}
	}

	public static class Copier_Boolean implements PreparedRowCopier {
		private ColumnarColumnBoolean source;
		private AmiColumn target;

		public Copier_Boolean(ColumnarColumnBoolean source, AmiColumn target) {
			this.source = source;
			this.target = target;
		}
		@Override
		public void copy(ColumnarRow srcRow, AmiPreparedRow tgtRow) {
			if (source.isNull(srcRow))
				tgtRow.setNull(target);
			else
				tgtRow.setLong(target, source.getBoolean(srcRow) ? 1 : 0);
		}
	}

	public static class Copier_Char implements PreparedRowCopier {
		private ColumnarColumnChar source;
		private AmiColumn target;

		public Copier_Char(ColumnarColumnChar source, AmiColumn target) {
			this.source = source;
			this.target = target;
		}
		@Override
		public void copy(ColumnarRow srcRow, AmiPreparedRow tgtRow) {
			if (source.isNull(srcRow))
				tgtRow.setNull(target);
			else
				tgtRow.setLong(target, source.getCharacter(srcRow));
		}
	}

	public static class Copier_Comparable implements PreparedRowCopier {

		private ColumnarColumn source;
		private AmiColumn target;

		public Copier_Comparable(ColumnarColumn source, AmiColumn target) {
			this.source = source;
			this.target = target;
		}

		@Override
		public void copy(ColumnarRow srcRow, AmiPreparedRow tgtRow) {
			if (source.isNull(srcRow))
				tgtRow.setNull(target);
			else
				tgtRow.setComparable(this.target, (Comparable) srcRow.getAt(this.source.getLocation()));
		}
	};

	static public PreparedRowCopier getRowCopier(ColumnarColumn source, AmiColumn target) {
		if (source instanceof ColumnarColumnPrimitive) {
			switch (source.getBasicType()) {
				case BasicTypes.BYTE:
					return new Copier_Byte((ColumnarColumnByte) source, target);
				case BasicTypes.SHORT:
					return new Copier_Short((ColumnarColumnShort) source, target);
				case BasicTypes.INT:
					return new Copier_Int((ColumnarColumnInt) source, target);
				case BasicTypes.LONG:
					return new Copier_Long((ColumnarColumnLong) source, target);
				case BasicTypes.FLOAT:
					return new Copier_Float((ColumnarColumnFloat) source, target);
				case BasicTypes.DOUBLE:
					return new Copier_Double((ColumnarColumnDouble) source, target);
				case BasicTypes.CHAR:
					return new Copier_Char((ColumnarColumnChar) source, target);
				case BasicTypes.BOOLEAN:
					return new Copier_Boolean((ColumnarColumnBoolean) source, target);
			}
		}
		return new Copier_Comparable(source, target);
	}

}
