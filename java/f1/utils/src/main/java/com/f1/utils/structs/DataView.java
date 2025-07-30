package com.f1.utils.structs;

public interface DataView<V> {
	// This is a Data and View model, where the view is only a partial view.
	/*
	 * Example:
	 * 
	 * Data				View
	 * 0 - A			0 (2) - C 
	 * 1 - B			1 (3) - D
	 * 2 - C
	 * 3 - D
	 */

	public boolean isInsideView(int position);

	public void onViewBoundsChanged(DataList<V> dataSyncer, int newLowerBound, int newUpperBound);
	public void onDataRequested(int index, V value);
	public void onDataInit(DataList<V> dataSyncer);
	public void onDataCleared();
	public void onDataAdded(int index, V newValue);
	public void onDataUpdated(int index, V newValue, V oldValue);
	public void onDataRemoved(int index, V oldValue);
	public void onDataSizeChanged(int newSize);

	public int getViewSize();
	public int getViewLowerBound();
	public int getViewUpperBound();

	public void setCapacity(int newCap);
	public void optimize();

}
