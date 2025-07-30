package com.f1.anvil.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.f1.ami.center.table.AmiColumn;
import com.f1.ami.center.table.AmiImdb;
import com.f1.ami.center.table.AmiImdbSession;
import com.f1.ami.center.table.AmiPreparedRow;
import com.f1.ami.center.table.AmiRow;
import com.f1.ami.center.table.AmiTable;
import com.f1.utils.AH;
import com.f1.utils.LH;
import com.f1.utils.OH;

public class AnvilTableCopier {

	final private AmiTable src;
	final private AmiTable snk;
	final private AmiColumn snkTimeColumn;
	final private AmiColumn[] srcColumns;
	final private AmiColumn[] snkColumns;
	final private AmiPreparedRow snkRow;
	final private static Logger log = LH.get(AnvilTableCopier.class);

	public AnvilTableCopier(AmiTable source, AmiTable dest, AmiColumn destTimeColumn) {
		this.src = source;
		this.snk = dest;
		this.snkTimeColumn = destTimeColumn;
		int cnt = 0;
		OH.assertEqIdentity(destTimeColumn.getAmiTable(), dest);
		OH.assertNeIdentity(source, dest);
		List<AmiColumn> srcCols = new ArrayList<AmiColumn>();
		List<AmiColumn> dstCols = new ArrayList<AmiColumn>();
		for (int i = 0; i < source.getColumnsCount(); i++) {
			AmiColumn sourceColumn = source.getColumnAt(i);
			if (sourceColumn.getName().length() == 1)
				continue;
			AmiColumn destColumn = dest.getColumnNoThrow(sourceColumn.getName());
			if (destColumn == null)
				continue;
			if (destColumn == this.snkTimeColumn)
				throw new RuntimeException("time column must be unique name: " + destColumn.getName());
			OH.assertEq(sourceColumn.getAmiType(), destColumn.getAmiType());
			srcCols.add(sourceColumn);
			dstCols.add(destColumn);
		}
		this.srcColumns = AH.toArray(srcCols, AmiColumn.class);
		this.snkColumns = AH.toArray(dstCols, AmiColumn.class);
		this.snkRow = this.snk.createAmiPreparedRow();
	}

	public AnvilTableCopier(AmiImdb imdb, String source, String sink, String sinkTimeColumn) {
		this(imdb.getAmiTable(source), imdb.getAmiTable(sink), imdb.getAmiTable(sink).getColumn(sinkTimeColumn));
	}

	public int copy(long time, AmiImdbSession session) {
		int cnt = src.getRowsCount();
		for (int pos = 0; pos < cnt; pos++) {
			AmiRow row = src.getAmiRowAt(pos);
			for (int j = 0; j < srcColumns.length; j++) {
				snkColumns[j].copyToFrom(snkRow, srcColumns[j], row, session);
				if (snkTimeColumn != null)
					snkRow.setLong(snkTimeColumn, time);
			}
			if (snk.insertAmiRow(snkRow, session) == null)
				LH.info(log, "Failed to insert into '", snk.getName(), "' row: ", snk);
		}
		return cnt;
	}
	public AmiPreparedRow getTargetSinkRomw() {
		return this.snkRow;
	}
	public AmiRow copy(AmiRow sourceRow, long time, AmiImdbSession session) {
		for (int j = 0; j < srcColumns.length; j++) {
			snkColumns[j].copyToFrom(snkRow, srcColumns[j], sourceRow, session);
			if (snkTimeColumn != null)
				snkRow.setLong(snkTimeColumn, time);
		}
		return snk.insertAmiRow(snkRow, session);
	}

}
