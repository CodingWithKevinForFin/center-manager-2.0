package com.f1.utils.structs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import com.f1.utils.SH;

public class BasicDataList<V> extends ArrayList<V> implements DataList<V> {
	public DataView<V> singleView;

	public static void main(String[] args) {
		BasicDataList<Integer> ds = new BasicDataList<Integer>(6);
		BasicDataView<Integer> view = new BasicDataView<Integer>();
		//		view.onViewBoundsChanged(ds, 5, 10);

		ds.addView(view);
		System.out.println(view.toDebugString());
		for (int i = 0; i <= 250; i++) {
			ds.add(1000 + i);
		}
		view.onViewBoundsChanged(ds, 5, 10);
		//		view.onViewBoundsChanged(ds, 50, 55);
		//		view.onViewBoundsChanged(ds, 52, 57);
		//		view.onViewBoundsChanged(ds, 100, 157);
		//		view.onViewBoundsChanged(ds, 52, 57);
		//		view.onViewBoundsChanged(ds, 5, 10);
		//		view.onViewBoundsChanged(ds, 3, 10);
		//		view.onViewBoundsChanged(ds, 5, 10);
		//		view.onViewBoundsChanged(ds, 5, 100);
		//		ds.set(7, 20);
		//		ds.set(3, 20);
		//		ds.add(1000 + 26);
		//		System.out.println(view.toDebugString());
		//		view.onViewBoundsChanged(ds, 3, 10);
		//		//		view.onViewBoundsChanged(ds, 5, 10);
		//		view.onViewBoundsChanged(ds, 5, 100);
		//		view.onViewBoundsChanged(ds, 50, 58);
		//		view.onViewBoundsChanged(ds, 3, 10);
		//		view.onViewBoundsChanged(ds, 5, 10);
		System.out.println(SH.s(ds));
		System.out.println(view.toDebugString());
	}

	public BasicDataList(int size) {
		super(size);
	}
	public BasicDataList() {
		super();
	}

	public boolean hasView() {
		return this.singleView != null;
	}
	@Override
	public void addView(DataView<V> view) {
		if (this.singleView != null)
			throw new UnsupportedOperationException("View already exists");
		this.singleView = view;
		this.singleView.onDataInit(this);
	}

	@Override
	public void removeView(DataView<V> view) {
		if (this.singleView == null)
			throw new UnsupportedOperationException("No view exists");
		this.singleView.onDataCleared();
		if (this.singleView.equals(view))
			this.singleView = null;
	}

	@Override
	public void setViewBounds(DataView<V> view, int lower, int upper) {
		if (this.singleView == null || this.singleView != view)
			return;
		singleView.onViewBoundsChanged(this, lower, upper);

	}
	private boolean isInView(int index) {
		return this.singleView != null && this.singleView.isInsideView(index);
	}
	@Override
	public void requestData(DataView<V> view, int index) {
		if (this.singleView == null || this.singleView != view)
			return;
		if (isInView(index) && index < this.size())
			view.onDataRequested(index, super.get(index));
	}

	/*
	 * Add calls:
	 */
	@Override
	public void add(int index, V element) {
		super.add(index, element);
		if (isInView(index))
			this.singleView.onDataAdded(index, element);
		if (this.singleView != null)
			this.singleView.onDataSizeChanged(this.size());
		//TODO shift lower indexes; the view needs to handle shifting & removal // maybe syncer handles it
	}

	@Override
	public boolean add(V e) {
		int index = super.size();
		boolean b = super.add(e);
		if (isInView(index))
			this.singleView.onDataAdded(index, e);
		if (b && this.singleView != null)
			this.singleView.onDataSizeChanged(this.size());
		return b;
	}

	@Override
	public boolean addAll(Collection<? extends V> c) {
		int index = super.size();
		boolean b = super.addAll(c);
		for (V e : c) {
			if (isInView(index))
				this.singleView.onDataAdded(index, e);
			index++;
		}
		if (b && this.singleView != null)
			this.singleView.onDataSizeChanged(this.size());
		return b;
	}
	@Override
	public boolean addAll(int index, Collection<? extends V> c) {
		boolean b = super.addAll(index, c);
		for (V e : c) {
			if (isInView(index))
				this.singleView.onDataAdded(index, e);
			index++;
		}
		if (b && this.singleView != null)
			this.singleView.onDataSizeChanged(this.size());
		return b;
	}
	/*
	 * Update calls:
	 */
	@Override
	public V set(int index, V element) {
		V old = super.set(index, element);
		if (isInView(index))
			this.singleView.onDataUpdated(index, element, old);
		return old;
	}

	/*
	 * Remove calls: // Arraylist you can't add a new element so size is same
	 */
	@Override
	public V remove(int index) {
		V old = super.remove(index);
		if (isInView(index))
			this.singleView.onDataRemoved(index, old);

		if (this.singleView != null)
			this.singleView.onDataSizeChanged(this.size());
		return old;
	}
	@Override
	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		int index = super.indexOf(o);
		if (index == -1)
			return false;
		V old = super.remove(index);
		if (isInView(index))
			this.singleView.onDataRemoved(index, old);
		if (this.singleView != null)
			this.singleView.onDataSizeChanged(this.size());
		return true;
	}
	@Override
	public boolean removeAll(Collection<?> c) {
		boolean changed = false;
		for (Object e : c) {
			int index = super.indexOf(e);
			if (index != -1)
				changed = true;
			V old = super.remove(index);
			if (isInView(index))
				this.singleView.onDataRemoved(index, old);
		}
		if (changed && this.singleView != null)
			this.singleView.onDataSizeChanged(this.size());
		return changed;
	}
	@Override
	public boolean removeIf(Predicate<? super V> filter) {
		boolean r = super.removeIf(filter);
		//Niave approach
		if (this.singleView != null) {
			this.singleView.onDataCleared();
			this.singleView.onDataInit(this);
			this.singleView.onDataSizeChanged(this.size());
		}
		return r;
	}
	@Override
	protected void removeRange(int fromIndex, int toIndex) {
		super.removeRange(fromIndex, toIndex);
		//Niave approach
		if (this.singleView != null) {
			this.singleView.onDataCleared();
			this.singleView.onDataInit(this);
			this.singleView.onDataSizeChanged(this.size());
		}
	}
	@Override
	public boolean retainAll(Collection<?> c) {
		boolean r = super.retainAll(c);
		//Niave approach
		if (r && this.singleView != null) {
			this.singleView.onDataCleared();
			this.singleView.onDataInit(this);
			this.singleView.onDataSizeChanged(this.size());
		}
		return r;
	}
	@Override
	public void replaceAll(UnaryOperator<V> operator) {
		super.replaceAll(operator);
		//Niave approach
		if (this.singleView != null) {
			this.singleView.onDataCleared();
			this.singleView.onDataInit(this);
			this.singleView.onDataSizeChanged(this.size());
		}
	}
	@Override
	public void sort(Comparator<? super V> c) {
		super.sort(c);
		if (this.singleView != null) {
			this.singleView.onDataCleared();
			this.singleView.onDataInit(this);
		}
	}

	@Override
	public void clear() {
		super.clear();
		if (this.singleView != null) {
			this.singleView.onDataCleared();
			this.singleView.onDataSizeChanged(this.size());
		}
	}

}
