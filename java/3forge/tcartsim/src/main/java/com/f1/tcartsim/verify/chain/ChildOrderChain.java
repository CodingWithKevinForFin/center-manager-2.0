/**
 * 
 */
package com.f1.tcartsim.verify.chain;

import java.util.ArrayList;
import java.util.Map;

import com.f1.tcartsim.verify.record.ChildOrderRecord;
import com.f1.tcartsim.verify.util.VerifyFormulas;

/**
 * @author george
 * 
 */
public class ChildOrderChain {
	private ChildOrderRecord first;
	private ChildOrderRecord last;

	private long size;
	private double value;
	private boolean isFilled;
	private boolean isCanceled;
	private boolean isOpen;
	private long nNewRecords;
	private long nUpdateRecords;
	private long nCancelRecords;
	private long canceledSize;
	private double canceledValue;
	private long nExecutions;
	private long nBusts;
	private long nFills;
	private long filledSize;
	private double filledValue;
	private double avgFilledPx;
	private long bustedSize;
	private double bustedValue;
	private double avgBustedPx;
	private long openSize;
	private double openValue;
	private char side;

	private ArrayList<ChildOrderRecord> data;
	private ArrayList<String> executionChains;

	public ChildOrderChain(ChildOrderRecord r) {
		isOpen = true;
		this.first = r;
		this.last = r;
		data = new ArrayList<ChildOrderRecord>();
		executionChains = new ArrayList<String>();
		addChildOrder(r);
	}
	public void addChildOrder(ChildOrderRecord r) {
		this.last = r;
		data.add(r);
	}

	public void addExecution(String executionID) {
		executionChains.add(executionID);
	}
	public void update() {
		for (int i = 0; i < data.size(); i++) {
			ChildOrderRecord c = data.get(i);
			switch (c.getStatus()) {
				case 'N':
					// New Child Orders
					if (isCanceled) {
						isCanceled = false;
						canceledSize = 0;
					}
					size = c.getSize();
					nNewRecords++;
					break;
				case 'U':
					// Modifies
					if (isCanceled) {
						isCanceled = false;
						canceledSize = 0;
					}
					size = c.getSize();
					nUpdateRecords++;
					break;
				case 'C':
					// Cancels
					if (!isCanceled) {
						isCanceled = true;
						nCancelRecords++;
						canceledSize = size;
					}
					// Else Repeat Cancel 
					break;
			}
		}
		value = VerifyFormulas.value(size, getLimitPx());
		canceledValue = VerifyFormulas.value(canceledSize, getLimitPx());
	}
	public void updateWithExecutions(Map<String, ExecutionChain> emap) {
		for (String eid : executionChains) {
			ExecutionChain e = emap.get(eid);
			nBusts += e.getnBusts();
			nFills += e.getnFills();
			nExecutions += e.getnExecutions();
			filledSize += e.getFilledSize();
			filledValue += e.getFilledValue();
			bustedSize += e.getBustedSize();
			bustedValue += e.getBustedValue();
		}
		avgFilledPx = VerifyFormulas.avgPx(filledValue, filledSize);
		avgBustedPx = VerifyFormulas.avgPx(bustedValue, bustedSize);
		isFilled = (filledSize >= size);
		isOpen = !(isFilled || isCanceled);
		openSize = VerifyFormulas.open(size, canceledSize, filledSize);
		openValue = VerifyFormulas.value(openSize, this.getLimitPx());
	}
	public void updateWithParents(Map<String, ParentOrderChain> pmap) {
		//		if (!SH.equals(first.getParentOrderID(), ""))
		side = pmap.get(first.getParentOrderID()).getSide();
	}
	public String getSymbol() {
		return first.getSymbol();
	}

	public String getCurrency() {
		return first.getCurrency();
	}

	public String getParentOrderID() {
		return first.getParentOrderID();
	}

	public String getChildOrderID() {
		return first.getChildOrderID();
	}

	public String getOrigChildOrderID() {
		return first.getOrigChildOrderID();
	}

	public long getTime() {
		return last.getTime();
	}

	public double getLimitPx() {
		return last.getLimitPx();
	}

	public boolean hasLimitPx() {
		return last.hasLimitPx();
	}

	public ChildOrderRecord getFirst() {
		return first;
	}

	public ChildOrderRecord getLast() {
		return last;
	}

	public long getSize() {
		return size;
	}

	public double getValue() {
		return value;
	}

	public boolean isFilled() {
		return isFilled;
	}

	public boolean isCanceled() {
		return isCanceled;
	}

	public boolean isOpen() {
		return isOpen;
	}

	public long getnNewRecords() {
		return nNewRecords;
	}

	public long getnUpdateRecords() {
		return nUpdateRecords;
	}

	public long getnCancelRecords() {
		return nCancelRecords;
	}

	public long getCanceledSize() {
		return canceledSize;
	}

	public double getCanceledValue() {
		return canceledValue;
	}

	public long getnBusts() {
		return nBusts;
	}

	public long getnFills() {
		return nFills;
	}

	public long getnExecutions() {
		return nExecutions;
	}

	public long getFilledSize() {
		return filledSize;
	}

	public double getFilledValue() {
		return filledValue;
	}

	public double getAvgFilledPx() {
		return avgFilledPx;
	}

	public long getBustedSize() {
		return bustedSize;
	}

	public double getBustedValue() {
		return bustedValue;
	}

	public double getAvgBustedPx() {
		return avgBustedPx;
	}

	public long getOpenSize() {
		return openSize;
	}

	public double getOpenValue() {
		return openValue;
	}

	public char getSide() {
		return side;
	}
}
