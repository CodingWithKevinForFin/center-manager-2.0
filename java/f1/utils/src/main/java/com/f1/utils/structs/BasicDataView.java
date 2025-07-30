package com.f1.utils.structs;

import java.util.ArrayList;
import java.util.Collections;

import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class BasicDataView<V> implements DataView<V> {

	private static final int UNSET_BOUNDS = -1;
	private static final int DEFAULT_CAPACITY = 100;
	private static final Object NULL = new Object();
	private static final double DEFAULT_CAPACITY_RATIO = 8; // High Ratio - because I'm using ArrayList, optimizing is expensive
	private static final double DEFAULT_OPTIMIZE_M_FACTOR = 0;
	private static final double DEFAULT_OPTIMIZE_B_FACTOR = 4;
	private ArrayList<Object> entries;
	private int lowerBound; //INCLUSIVE
	private int upperBound; //INCLUSIVE
	private int offset;
	private int viewCapacity; // Max Capacity

	public BasicDataView() {
		/*
		 * TODO: Do I need the actual data for storing
		 */
		this.entries = new ArrayList<Object>();
		this.lowerBound = UNSET_BOUNDS;
		this.upperBound = UNSET_BOUNDS;
		this.offset = UNSET_BOUNDS;
		this.viewCapacity = DEFAULT_CAPACITY; // Max cap
		//TODO: do I even need to store the list of entries?

	}
	public BasicDataView(int initViewCapacity) {
		/*
		 * TODO: Do I need the actual data for storing
		 */
		this.entries = new ArrayList<Object>();
		this.lowerBound = UNSET_BOUNDS;
		this.upperBound = UNSET_BOUNDS;
		this.offset = UNSET_BOUNDS;
		this.viewCapacity = initViewCapacity; // Max cap
		//TODO: do I even need to store the list of entries?

	}

	// View:
	// Pos	| Offset	| Value
	// ### Lower
	// ...
	// ### Upper
	// ...
	public String toDebugString() {
		StringBuilder sb = new StringBuilder();
		sb.append("View: \n");
		sb.append("ViewCap: ").append(this.viewCapacity).append("\t EntriesSize: ").append(this.entries.size()).append(" \n");
		sb.append("Pos\t| Offset\t| Value \n");
		for (int i = 0; i < this.entries.size(); i++) {
			int pos = i + offset;
			if (pos == this.lowerBound)
				sb.append("### LOWER \n");

			sb.append(pos).append("\t| ").append(i).append("\t| ").append(SH.s(this.entries.get(i))).append(" \n");

			if (pos == this.upperBound)
				sb.append("### UPPER \n");
		}

		return sb.toString();
	}

	@Override
	public void setCapacity(int newCap) {
		int oldCap = this.viewCapacity;
		this.viewCapacity = newCap;
		if (newCap < oldCap && this.entries.size() > newCap)
			this.optimize();
	}

	/*
	 * Naive formula
	 */
	public void autoCapacity() {
		int viewSize = this.upperBound - this.lowerBound + 1;
		int newCapacity = (int) (MH.round(viewSize * DEFAULT_CAPACITY_RATIO, MH.ROUND_UP));
		this.viewCapacity = newCapacity;
	}

	@Override
	public void optimize() {
		//		if (true)
		//			return;
		int viewSize = this.upperBound - this.lowerBound + 1;
		int cacheHalfSize = (int) MH.round(viewSize * DEFAULT_OPTIMIZE_M_FACTOR + DEFAULT_OPTIMIZE_B_FACTOR, MH.ROUND_UP);

		int estUpperBound = this.upperBound + cacheHalfSize;
		int currentUpperCache = this.offset + this.entries.size() - 1;
		if (estUpperBound < currentUpperCache) {
			int newUpperIndex = estUpperBound - this.offset;
			for (int i = this.entries.size() - 1; i > newUpperIndex; i--)
				this.entries.remove(i);
		}

		int estLowerBound = this.lowerBound - cacheHalfSize;
		int currentLowerCache = this.offset;
		if (estLowerBound > currentLowerCache) {
			int newSize = estUpperBound - estLowerBound + 1;
			ArrayList<Object> newList = new ArrayList<Object>(newSize);
			for (int i = estLowerBound - currentLowerCache; i < this.entries.size(); i++)
				newList.add(this.entries.get(i));
			this.offset = estLowerBound;
			this.entries.clear();
			this.entries = newList;
		}
	}
	/*
	 * Gets the seed data for the view, requires the bounds to be set first
	 */
	@Override
	public void onDataInit(DataList<V> dataSyncer) {
		if (this.lowerBound == UNSET_BOUNDS || this.upperBound == UNSET_BOUNDS) {
			//		throw new IllegalStateException("No bounds have been set for view can't init data");
			this.entries.clear();
			return;
		}
		//TODO: Do need to have a semiphore and lock? the datasyncer?
		//		OH.assertEmpty(this.entries);
		this.offset = this.getViewLowerBound();

		//Pad Values
		this.entries.clear();
		this.entries.addAll(Collections.nCopies(this.getViewUpperBound() - this.getViewLowerBound() + 1, null));
		for (int i = this.getViewLowerBound(); i <= this.getViewUpperBound(); i++) {
			dataSyncer.requestData(this, i);
		}
	}

	/*
	 * Called when the view's data needs to be cleared or the data syncer clears data
	 */
	@Override
	public void onDataCleared() {
		this.entries.clear();
		this.offset = 0;
		this.lowerBound = UNSET_BOUNDS;
		this.upperBound = UNSET_BOUNDS;
	}

	@Override
	public void onViewBoundsChanged(DataList<V> dataSyncer, int newLowerBound, int newUpperBound) {
		if (this.lowerBound == UNSET_BOUNDS || this.upperBound == UNSET_BOUNDS) {
			this.lowerBound = newLowerBound;
			this.upperBound = newUpperBound;
			this.autoCapacity();
			this.onDataInit(dataSyncer);
		} else {
			if ((newLowerBound < this.lowerBound && newUpperBound <= this.lowerBound) || newUpperBound > this.upperBound && newLowerBound >= this.upperBound) {
				this.lowerBound = newLowerBound;
				this.upperBound = newUpperBound;

				this.offset = newLowerBound;
				this.entries.clear();
				this.entries.addAll(0, Collections.nCopies(newUpperBound - newLowerBound + 1, null));
				for (int i = newLowerBound; i <= newUpperBound; i++)
					dataSyncer.requestData(this, i);

			} else {
			//TODO: might need lock
			if (newLowerBound < offset) {
				int oldLowerBound = this.lowerBound;
				this.lowerBound = newLowerBound;
				//Pad Start;
				this.entries.addAll(0, Collections.nCopies(oldLowerBound - newLowerBound, null));
				this.offset = newLowerBound;

				for (int i = newLowerBound; i < oldLowerBound; i++)
					dataSyncer.requestData(this, i);

			} else
				this.lowerBound = newLowerBound;

			if (newUpperBound >= (this.offset + this.entries.size())) {
				int oldUpperBound = this.upperBound;
				this.upperBound = newUpperBound;

				//Pad end
				this.entries.addAll(Collections.nCopies(newUpperBound - oldUpperBound, null));

				for (int i = oldUpperBound + 1; i <= newUpperBound; i++)
					dataSyncer.requestData(this, i);
			} else
				this.upperBound = newUpperBound;
			}

			if (this.entries.size() > this.viewCapacity)
				this.optimize();
		}
	}

	public V getAtIndex(int position) {
		Object o = this.entries.get(position - offset);
		if (o == this.NULL)
			return null;
		return (V) o;
	}

	@Override
	public boolean isInsideView(int position) {
		return position >= lowerBound && position <= upperBound;
	}
	/*
	 * Stack:
	 * ## Start of Data 0 
	 * ## LowerBound
	 *  Adding Missing data
	 * ## LowerEntry = Offset
	 *  Updating
	 * ## UpperEntry = Offset + entries.size() -1 (inclusive)
	 *  Adding Missing data
	 * ## UpperBound
	 */
	@Override
	public void onDataRequested(int position, V value) {
		if (position >= this.lowerBound && position < this.offset) {
			int relativePosition = position - this.offset;
			this.entries.set(relativePosition, value);
		} else if (position >= this.offset && position <= (this.offset + entries.size() - 1)) {
			V oldValue = this.getAtIndex(position);
			this.onDataUpdated(position, value, oldValue);
		} else if (position > (this.offset + entries.size() - 1) && position <= this.upperBound) {
			int relativePosition = position - this.offset;
			this.entries.set(relativePosition, value);
		}

	}

	/*
	 * Called when a new element is added, 
	 * Index should be within bounds already
	 * Resize and discard old values if it reaches the capacity
	 */
	@Override
	public void onDataAdded(int index, V newValue) {
		OH.assertNe(this.offset, UNSET_BOUNDS); // Ensure that the offset has been set
		OH.assertBetween(index, this.offset, this.offset + entries.size()); // Compares between offset <= index <= offset+length
		int relativePosition = index - this.offset;
		if (newValue == null)
			this.entries.add(relativePosition, NULL);
		else
			this.entries.add(relativePosition, newValue);
		if (this.entries.size() > this.viewCapacity)
			this.optimize();
	}

	@Override
	public void onDataUpdated(int index, V newValue, V oldValue) {
		OH.assertNe(this.offset, UNSET_BOUNDS); // Ensure that the offset has been set
		OH.assertBetween(index, this.offset, this.offset + entries.size() - 1); // Compares between offset <= index < offset+length
		int relativePosition = index - this.offset;
		if (newValue == null)
			this.entries.set(relativePosition, NULL);
		else
			this.entries.set(relativePosition, newValue);

	}

	@Override
	public void onDataRemoved(int index, V oldValue) {
		OH.assertNe(this.offset, UNSET_BOUNDS); // Ensure that the offset has been set
		OH.assertBetween(index, this.offset, this.offset + entries.size() - 1); // Compares between offset <= index < offset+length
		int relativePosition = index - this.offset;
		this.entries.remove(relativePosition);
	}

	@Override
	public void onDataSizeChanged(int newSize) {

	}

	@Override
	public int getViewSize() {
		return this.upperBound != UNSET_BOUNDS && this.lowerBound != UNSET_BOUNDS ? this.upperBound - this.lowerBound + 1 : 0;
	}

	@Override
	public int getViewLowerBound() {
		return this.lowerBound;
	}

	@Override
	public int getViewUpperBound() {
		return this.upperBound;
	}

}
