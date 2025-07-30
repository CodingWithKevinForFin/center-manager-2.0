/**
 * 
 */
package com.f1.tcartsim.verify.chain;

import java.util.ArrayList;
import java.util.Map;

import com.f1.tcartsim.verify.record.ParentOrderRecord;
import com.f1.tcartsim.verify.util.VerifyFormulas;

/**
 * 
 * @author george
 * 
 */
public class ParentOrderChain {
	private ParentOrderRecord first;
	private ParentOrderRecord last;

	private long size;
	private double value;
	private long nNewRecords;
	private long nUpdateRecords;
	private long nCancelRecords;
	private long canceledSize;
	private double canceledValue;
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

	private long nChildOrders;
	private long nExecutions;
	private long tnChildCanceledSize;
	private double tnChildCanceledValue;
	private double tnAvgChildCanceledPx;
	private long tnChildSize;
	private double tnChildValue;
	private double tnAvgChildPx;
	private long tnChildFills;
	private long tnChildFilledSize;
	private double tnChildFilledValue;
	private double tnAvgChildFilledPx;

	private long tnLeavesSize;
	private double tnLeavesValue;
	private long tnFills;
	private long tnFilledSize;
	private double tnFilledValue;
	private double tnAvgFilledPx;
	private boolean isFilled;
	private boolean isCanceled;
	private boolean isOpen;

	private ArrayList<ParentOrderRecord> data;
	private ArrayList<String> childOrderChains;
	private ArrayList<String> executionChains;

	public ParentOrderChain(ParentOrderRecord r) {
		isOpen = true;
		first = r;
		last = r;
		data = new ArrayList<ParentOrderRecord>();
		executionChains = new ArrayList<String>();
		childOrderChains = new ArrayList<String>();
		addParentOrder(r);
	}

	public void addParentOrder(ParentOrderRecord r) {
		this.last = r;
		data.add(r);
	}

	public void addExecution(String executionID) {
		executionChains.add(executionID);
	}

	public void addChildOrder(String childOrderID) {
		childOrderChains.add(childOrderID);
	}

	public void update() {
		for (int i = 0; i < data.size(); i++) {
			ParentOrderRecord p = data.get(i);
			switch (p.getStatus()) {
				case 'N':
					// New Parent orders
					if (isCanceled) {
						isCanceled = false;
						canceledSize = 0;
					}

					size = p.getSize();
					nNewRecords++;
					break;
				case 'U':
					// Modifies
					if (isCanceled) {
						isCanceled = false;
						canceledSize = 0;
					}
					size = p.getSize();
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
		value = size * getLimitPx();
		canceledValue = canceledSize * getLimitPx();
	}

	public void updateWithExecutions(Map<String, ExecutionChain> emap) {
		for (String eid : executionChains) {
			ExecutionChain e = emap.get(eid);
			nBusts += e.getnBusts();
			nFills += e.getnFills();
			filledSize += e.getFilledSize();
			filledValue += e.getFilledValue();
			bustedSize += e.getBustedSize();
			bustedValue += e.getBustedValue();
		}
		avgFilledPx = VerifyFormulas.avgPx(filledValue, filledSize);
		avgBustedPx = VerifyFormulas.avgPx(bustedValue, bustedSize);
		openSize = VerifyFormulas.open(size, canceledSize, filledSize);
		openValue = VerifyFormulas.value(openSize, this.getLimitPx());
	}

	public void updateWithChildOrders(Map<String, ChildOrderChain> cmap) {
		nChildOrders = childOrderChains.size();
		for (String cid : childOrderChains) {
			ChildOrderChain c = cmap.get(cid);
			nExecutions += c.getnExecutions();
			tnChildCanceledSize += c.getCanceledSize();
			tnChildCanceledValue += c.getCanceledValue();
			tnChildSize += c.getSize();
			tnChildValue += c.getValue();
			tnChildFilledSize += c.getFilledSize();
			tnChildFilledValue += c.getFilledValue();
			tnLeavesSize += c.getOpenSize();
			tnChildFills += c.getnFills();
		}
		tnAvgChildCanceledPx = VerifyFormulas.avgPx(tnChildCanceledValue, tnChildCanceledSize);
		tnAvgChildPx = VerifyFormulas.avgPx(tnChildValue, tnChildSize);
		tnAvgFilledPx = VerifyFormulas.avgPx(tnChildFilledValue, tnChildFilledSize);

		tnFills = tnChildFills + nFills;
		tnFilledSize = tnChildFilledSize + filledSize;
		tnFilledValue = tnChildFilledValue + filledValue;
		tnAvgFilledPx = VerifyFormulas.avgPx(tnFilledValue, tnFilledSize);
		isFilled = tnFilledSize >= size;
		isOpen = !(isFilled || isCanceled);
		//		tnLeavesSize = VerifyFormulas.calcLeaves(tnChildSize, tnChildCanceledSize, tnFilledSize);
		tnLeavesValue = VerifyFormulas.value(tnLeavesSize, this.getLimitPx());
	}
	public String getParentOrderID() {
		return first.getParentOrderID();
	}
	public String getSymbol() {
		return first.getSymbol();
	}
	public String getCurrency() {
		return first.getCurrency();
	}
	public char getSide() {
		return first.getSide();
	}
	public String getSystem() {
		return first.getSystem();
	}
	public String getStrategy() {
		return first.getStrategy();
	}
	public String getAccount() {
		return first.getAccount();
	}
	public long getStartTime() {
		return first.getStartTime();
	}
	public long getEndTime() {
		return first.getEndTime();
	}
	public long getTime() {
		return last.getTime();
	}
	public double getLimitPx() {
		return last.getLimitPx();
	}
	public long getSize() {
		return size;
	}
	public double getValue() {
		return value;
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
	public long getnChildOrders() {
		return nChildOrders;
	}
	public long getnExecutions() {
		return nExecutions;
	}
	public long getTnChildCanceledSize() {
		return tnChildCanceledSize;
	}
	public double getTnChildCanceledValue() {
		return tnChildCanceledValue;
	}
	public double getTnAvgChildCanceledPx() {
		return tnAvgChildCanceledPx;
	}
	public long getTnChildSize() {
		return tnChildSize;
	}
	public double getTnChildValue() {
		return tnChildValue;
	}
	public double getTnAvgChildPx() {
		return tnAvgChildPx;
	}
	public long getTnChildFills() {
		return tnChildFills;
	}
	public double getTnChildFilledValue() {
		return tnChildFilledValue;
	}
	public double getTnAvgChildFilledPx() {
		return tnAvgChildFilledPx;
	}
	public long getTnLeavesSize() {
		return tnLeavesSize;
	}
	public double getTnLeavesValue() {
		return tnLeavesValue;
	}
	public ParentOrderRecord getLast() {
		return last;
	}

	public long getTnFills() {
		return tnFills;
	}

	public double getTnFilledValue() {
		return tnFilledValue;
	}
	public double getTnAvgFilledPx() {
		return tnAvgFilledPx;
	}
	public long getTnChildFilledSize() {
		return tnChildFilledSize;
	}

	public long getTnFilledSize() {
		return tnFilledSize;
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

}
