/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.converter.bytes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.f1.base.BasicTypes;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.utils.structs.table.BasicTable;

public class TableToByteArrayConverter implements ByteArrayConverter<Table> {

	@Override
	public void write(Table table, ToByteArrayConverterSession session) throws IOException {
		if (session.handleIfAlreadyConverted(table))
			return;
		ObjectToByteArrayConverter converter = session.getConverter();
		ByteArrayConverter<Class> classConverter = (ByteArrayConverter<Class>) converter.getConverter(BasicTypes.CLASS);
		DataOutput stream = session.getStream();
		int colsCount = table.getColumnsCount();
		String title = table.getTitle();
		if (title != null && colsCount > 0) {
			stream.writeShort(-colsCount);
			stream.writeUTF(title);
		} else
			stream.writeShort(colsCount);

		stream.writeInt(table.getSize());
		for (int i = 0; i < colsCount; i++)
			classConverter.write(table.getColumnAt(i).getType(), session);
		for (int i = 0; i < colsCount; i++)
			converter.write(table.getColumnAt(i).getId(), session);
		for (Row row : table.getRows())
			for (Object o : row)
				converter.write(o, session);
	}
	@Override
	public Table read(FromByteArrayConverterSession session) throws IOException {
		int id = session.handleIfAlreadyConverted();
		if (id < 0)
			return (Table) session.get(id);
		ObjectToByteArrayConverter converter = session.getConverter();
		ByteArrayConverter<Class> classConverter = (ByteArrayConverter<Class>) converter.getConverter(BasicTypes.CLASS);
		DataInput stream = session.getStream();
		int colsCount = stream.readShort();
		String title;
		if (colsCount < 0) {
			colsCount = -colsCount;
			title = stream.readUTF();
		} else
			title = null;
		int rowsCount = stream.readInt();
		Class<?>[] types = new Class[colsCount];
		String[] ids = new String[colsCount];
		for (int i = 0; i < colsCount; i++)
			types[i] = classConverter.read(session);
		for (int i = 0; i < colsCount; i++)
			ids[i] = (String) converter.read(session);
		BasicTable r = new BasicTable(types, ids, rowsCount);
		for (int i = 0; i < rowsCount; i++) {
			Object row[] = new Object[colsCount];
			for (int j = 0; j < colsCount; j++) {
				row[j] = converter.read(session);
			}
			r.getRows().addRow(row);
		}
		if (title != null)
			r.setTitle(title);
		session.store(id, r);
		return r;
	}

	@Override
	public byte getBasicType() {
		return BasicTypes.TABLE;
	}

	@Override
	public boolean isCompatible(Class<?> o) {
		return Table.class.isAssignableFrom(o);
	}

}
