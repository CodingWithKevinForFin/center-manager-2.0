package com.f1.utils.converter.json2;

import java.util.logging.Logger;

import com.f1.base.Table;
import com.f1.utils.TableHelper;

public class TableToJsonConverter extends AbstractJsonConverter<Table> {
	private static final long serialVersionUID = -2601423463036000477L;
	private static final Logger log = Logger.getLogger(TableToJsonConverter.class.getName());
	private static final int[] COMMA_OR_CLOSE = new int[] { ',', '}' };
	private static final int[] SPACE_OR_COLON = new int[] { ':', ' ' };

	public TableToJsonConverter() {
		super((Class) Table.class);
	}

	@Override
	public void objectToString(Table t, ToJsonConverterSession session) {
		session.getConverter().objectToString(TableHelper.toMapOfLists(t), session);
	}

	@Override
	public Object stringToObject(FromJsonConverterSession session) {
		throw new UnsupportedOperationException();
	}
	@Override
	public boolean isLeaf() {
		return false;
	}

}
