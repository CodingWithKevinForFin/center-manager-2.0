package com.f1.ami.center;

import com.f1.utils.structs.table.columnar.ColumnarColumnEnumMapper;

public class AmiCenterEnumMapper implements ColumnarColumnEnumMapper {

	private AmiCenterState state;

	public AmiCenterEnumMapper(AmiCenterState state) {
		this.state = state;
	}

	@Override
	public String getEnumString(int id) {
		return state.getAmiValueString(id);
	}

	@Override
	public int getEnumId(String string) {
		return state.getAmiStringPool(string);
	}

}
