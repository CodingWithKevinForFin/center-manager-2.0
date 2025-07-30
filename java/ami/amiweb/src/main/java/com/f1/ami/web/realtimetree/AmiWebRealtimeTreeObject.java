package com.f1.ami.web.realtimetree;

import com.f1.ami.web.AmiWebObject;
import com.f1.utils.OH;
import com.f1.utils.sql.aggs.AggDeltaCalculator;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

//one per amiobject
public class AmiWebRealtimeTreeObject {

	private AmiWebObject inner;
	private Object cache[];
	private AmiWebRealtimeTreeRow row; // Leaf TreeRow * only Leaf TreeRows contain TreeObjects

	public AmiWebRealtimeTreeObject(AmiWebObject o, int deltaAggCacheSize) {
		this.cache = new Object[deltaAggCacheSize]; // cache is for old values 
		this.inner = o;
	}

	public AmiWebObject getObject() {
		return inner;
	}

	public void setRow(AmiWebRealtimeTreeRow row) {
		this.row = row;
	}
	public AmiWebRealtimeTreeRow getRow() {
		return row;
	}

	public void onTreeCalcsChanged(int deltaColumnCacheSize) {
		this.cache = new Object[deltaColumnCacheSize];
	}

	public void onUpdate(AmiWebObject o, AmiWebRealtimeTreeRow treeRow, AggDeltaCalculator[] deltaAggregates, ReusableCalcFrameStack sf) {
		// Is not used, also is flawed because it depends on the treenodes
		this.inner = o;
		sf.reset(this.inner);
		for (int i = 0; i < deltaAggregates.length; i++) {
			Object newValue = deltaAggregates[i].getUnderlying(sf);
			Object oldValue = cache[i];
			if (OH.ne(oldValue, newValue)) {
				for (AmiWebRealtimeTreeRow row = treeRow; row != null; row = row.getParent()) {
					if (row.getNode().isFiltered()) {
						row.setIsDeltaAggCached(i, false);
						break;
					}
					if (row.isDeltaAggCached(i)) {
						Object aggValue = deltaAggregates[i].applyDelta(row.getCache(i), oldValue, newValue);
						if (aggValue == AggDeltaCalculator.NOT_AGGEGATED)
							row.setIsDeltaAggCached(i, false);
						else
							row.setCache(i, aggValue);
					}
				}
			}
			cache[i] = newValue;
		}
		for (AmiWebRealtimeTreeRow row = treeRow; row != null; row = row.getParent())
			row.setIsSnapshotCached(false);
	}

	public void onAdd(AmiWebRealtimeTreeRow treeRow, AggDeltaCalculator[] deltaAggregates, ReusableCalcFrameStack sf) {
		sf.reset(this.inner);
		for (int i = 0; i < deltaAggregates.length; i++) {
			Object newValue = deltaAggregates[i].getUnderlying(sf);
			for (AmiWebRealtimeTreeRow row = treeRow; row != null; row = row.getParent()) {
				if (row.getNode().isFiltered()) {
					row.setIsDeltaAggCached(i, false);
					break;
				}
				if (row.isDeltaAggCached(i)) {
					Object aggValue = deltaAggregates[i].applyDelta(row.getCache(i), null, newValue);
					if (aggValue == AggDeltaCalculator.NOT_AGGEGATED)
						row.setIsDeltaAggCached(i, false);
					else
						row.setCache(i, aggValue);
				}
			}

			cache[i] = newValue;
		}
		for (AmiWebRealtimeTreeRow row = treeRow; row != null; row = row.getParent())
			row.setIsSnapshotCached(false);
		this.row = treeRow;
	}

	public void onRemove(AmiWebRealtimeTreeRow treeRow, AggDeltaCalculator[] deltaAggregates) {
		for (int i = 0; i < deltaAggregates.length; i++) {
			Object oldValue = cache[i];
			if (oldValue == null)
				continue;
			for (AmiWebRealtimeTreeRow row = treeRow; row != null; row = row.getParent()) {
				if (row.getNode().isFiltered()) {
					row.setIsDeltaAggCached(i, false);
					break;
				}
				if (row.isDeltaAggCached(i)) {
					Object aggValue = deltaAggregates[i].applyDelta(row.getCache(i), oldValue, null);
					if (aggValue == AggDeltaCalculator.NOT_AGGEGATED)
						row.setIsDeltaAggCached(i, false);
					else
						row.setCache(i, aggValue);
				}
			}
			cache[i] = null;
		}
		for (AmiWebRealtimeTreeRow row = treeRow; row != null; row = row.getParent())
			row.setIsSnapshotCached(false);
		this.row = null;
	}

}
