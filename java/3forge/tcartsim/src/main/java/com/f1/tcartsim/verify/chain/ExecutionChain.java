/**
 * 
 */
package com.f1.tcartsim.verify.chain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

import com.f1.tcartsim.verify.record.ExecutionRecord;
import com.f1.tcartsim.verify.util.VerifyFormulas;
import com.f1.utils.SH;

/**
 * @author george
 * 
 */
public class ExecutionChain {
	public static Comparator<ExecutionChain> compareTime = new Comparator<ExecutionChain>() {
		@Override
		public int compare(ExecutionChain lhs, ExecutionChain rhs) {
			return lhs.getTime() > rhs.getTime() ? 1 : lhs.getTime() < rhs.getTime() ? -1 : 0;
		}
	};

	private ExecutionRecord first;
	private ExecutionRecord last;

	private String parentOrderID;
	private long nFills;
	private long nBusts;
	private long nExecutions;
	private double filledValue;
	private long bustedSize;
	private double bustedValue;
	private double nationalBestBid;
	private double nationalBestOffer;
	private double tradePricePlusOne;
	private double tradePricePlusFive;

	private ArrayList<ExecutionRecord> data;

	public ExecutionChain(ExecutionRecord r) {
		this.first = r;
		this.last = r;
		data = new ArrayList<ExecutionRecord>();
		addExecution(r);
	}

	public void addExecution(ExecutionRecord r) {
		this.last = r;
		data.add(r);
	}

	public void update() {
		boolean isBusted = false;
		long size = 0;
		for (int i = 0; i < data.size(); i++) {
			ExecutionRecord e = data.get(i);
			if (size == 0 && e.getSize() > 0) {
				//New Executions
				nExecutions++;
				size = e.getSize();
				if (isBusted) {
					bustedSize = 0;
				}
			} else if (size > 0 && e.getSize() == 0) {
				//Busts
				nBusts++;
				bustedSize = size;
				size = e.getSize();
				isBusted = true;
			} else if (size > 0 && e.getSize() > 0) {
				//Modifies
			} else if (size == 0 && e.getSize() == 0) {
				//Repeat Bust
			}
		}
		nFills = nExecutions - nBusts;
		filledValue = VerifyFormulas.value(this.getSize(), this.getPx());
		bustedValue = VerifyFormulas.value(bustedSize, this.getPx());
	}

	public void updateWithChildOrders(Map<String, ChildOrderChain> map) {
		if (SH.equals(getChildOrderID(), "")) {
			parentOrderID = first.getParentOrderID();
		} else {
			if (map.containsKey(getChildOrderID()))
				parentOrderID = map.get(getChildOrderID()).getParentOrderID();
			else {
				parentOrderID = "";
				throw new IllegalArgumentException();
			}

		}
	}
	public String getParentOrderID() {
		return parentOrderID;
	}

	public String getChildOrderID() {
		return first.getChildOrderID();
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

	public String getExecutionID() {
		return first.getExecutionID();
	}

	public String getExecIndicator() {
		return first.getExecIndicator();
	}

	public String getExchange() {
		return first.getExchange();
	}

	private long getSize() {
		return last.getSize();
	}

	public long getTime() {
		return last.getTime();
	}

	public double getPx() {
		return last.getPx();
	}

	public long getnExecutions() {
		return nExecutions;
	}

	public long getnFills() {
		return nFills;
	}

	public long getnBusts() {
		return nBusts;
	}

	public long getFilledSize() {
		return last.getSize();
	}

	public double getFilledValue() {
		return filledValue;
	}

	public long getBustedSize() {
		return bustedSize;
	}

	public double getBustedValue() {
		return bustedValue;
	}

	public double getNationalBestBid() {
		return nationalBestBid;
	}

	public void setNationalBestBid(double nationalBestBid) {
		this.nationalBestBid = nationalBestBid;
	}

	public double getNationalBestOffer() {
		return nationalBestOffer;
	}

	public void setNationalBestOffer(double nationalBestOffer) {
		this.nationalBestOffer = nationalBestOffer;
	}

	public double getTradePricePlusOne() {
		return tradePricePlusOne;
	}

	public void setTradePricePlusOne(double tradePricePlusOne) {
		this.tradePricePlusOne = tradePricePlusOne;
	}

	public double getTradePricePlusFive() {
		return tradePricePlusFive;
	}

	public void setTradePricePlusFive(double tradePricePlusFive) {
		this.tradePricePlusFive = tradePricePlusFive;
	}

}
