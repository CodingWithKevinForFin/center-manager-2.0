package com.f1.utils.structs;

import java.util.List;

public interface DataList<V> extends List<V> {

	public void addView(DataView<V> view);
	public void setViewBounds(DataView<V> view, int lower, int upper);
	public void removeView(DataView<V> view);
	public void requestData(DataView<V> view, int index);

}
