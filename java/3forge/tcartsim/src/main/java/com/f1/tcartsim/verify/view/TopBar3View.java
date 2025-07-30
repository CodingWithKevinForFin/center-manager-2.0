/**
 * 
 */
package com.f1.tcartsim.verify.view;

import java.util.Map;

import com.f1.tcartsim.verify.chain.ExecutionChain;
import com.f1.tcartsim.verify.chain.ParentOrderChain;
import com.f1.tcartsim.verify.util.VerifyFormulas;

/**
 * @author george
 * 
 */
public class TopBar3View extends View {

	public TopBar3View() {
		addColumn(String.class, "side");
		addColumn(Double.class, "% Filled");
		addColumn(Long.class, "ParentOrder");
		addColumn(Long.class, "Filled");
		addColumn(Double.class, "Avg Fill Sz");
		addColumn(Double.class, "Notional");
		addColumn(Long.class, "Executions");
		addColumn(Double.class, "VWAP");
	}

	public void populate2(Map<String, ParentOrderChain> parentOrderChainMap, Map<String, ExecutionChain> executionChainMap) {
		long parentOrderB = 0;
		double pfilledB = 0;
		long filledB = 0;
		long targetB = 0;
		double avgFillSzB = 0;
		double notionalB = 0;
		long executionsB = 0;
		double vwapB = 0;

		long parentOrderS = 0;
		double pfilledS = 0;
		long filledS = 0;
		long targetS = 0;
		double avgFillSzS = 0;
		double notionalS = 0;
		long executionsS = 0;
		double vwapS = 0;

		for (String eid : executionChainMap.keySet()) {
			ExecutionChain e = executionChainMap.get(eid);
			if (e.getSide() == 'B') {
				filledB += e.getFilledSize();
				notionalB += e.getFilledValue();
				executionsB++;
			} else {
				filledS += e.getFilledSize();
				notionalS += e.getFilledValue();
				executionsS++;
			}
		}

		for (String pid : parentOrderChainMap.keySet()) {
			ParentOrderChain p = parentOrderChainMap.get(pid);
			if (p.getSide() == 'B') {
				parentOrderB++;
				//				filledB += p.getTnFilledSize();
				//				notionalB += p.getTnFilledValue();
				//				executionsB += p.getTnFills();
				targetB += p.getSize();
			} else {
				parentOrderS++;
				//				filledS += p.getTnFilledSize();
				//				notionalS += p.getTnFilledValue();
				//				executionsS += p.getTnFills();
				targetS += p.getSize();
			}
		}
		pfilledB = VerifyFormulas.percentFilled(filledB, targetB);
		pfilledS = VerifyFormulas.percentFilled(filledS, targetS);
		avgFillSzB = VerifyFormulas.avgFillSize(filledB, executionsB);
		avgFillSzS = VerifyFormulas.avgFillSize(filledS, executionsS);
		vwapB = VerifyFormulas.vwap(notionalB, filledB);
		vwapS = VerifyFormulas.vwap(notionalS, filledS);
		addRow("Sell", pfilledS, parentOrderS, filledS, avgFillSzS, notionalS, executionsS, vwapS);
		addRow("Buy", pfilledB, parentOrderB, filledB, avgFillSzB, notionalB, executionsB, vwapB);
	}

	@Deprecated
	public void populate(Map<String, ParentOrderChain> parentOrderChainMap) {
		long parentOrderB = 0;
		double pfilledB = 0;
		long filledB = 0;
		long targetB = 0;
		double avgFillSzB = 0;
		double notionalB = 0;
		long executionsB = 0;
		double vwapB = 0;

		long parentOrderS = 0;
		double pfilledS = 0;
		long filledS = 0;
		long targetS = 0;
		double avgFillSzS = 0;
		double notionalS = 0;
		long executionsS = 0;
		double vwapS = 0;

		for (String pid : parentOrderChainMap.keySet()) {
			ParentOrderChain p = parentOrderChainMap.get(pid);
			if (p.getSide() == 'B') {
				parentOrderB++;
				filledB += p.getTnFilledSize();
				notionalB += p.getTnFilledValue();
				executionsB += p.getTnFills();
				targetB += p.getSize();
			} else {
				parentOrderS++;
				filledS += p.getTnFilledSize();
				notionalS += p.getTnFilledValue();
				executionsS += p.getTnFills();
				targetS += p.getSize();
			}
		}
		pfilledB = VerifyFormulas.percentFilled(filledB, targetB);
		pfilledS = VerifyFormulas.percentFilled(filledS, targetS);
		avgFillSzB = VerifyFormulas.avgFillSize(filledB, executionsB);
		avgFillSzS = VerifyFormulas.avgFillSize(filledS, executionsS);
		vwapB = VerifyFormulas.vwap(notionalB, filledB);
		vwapS = VerifyFormulas.vwap(notionalS, filledS);
		addRow("Sell", pfilledS, parentOrderS, filledS, avgFillSzS, notionalS, executionsS, vwapS);
		addRow("Buy", pfilledB, parentOrderB, filledB, avgFillSzB, notionalB, executionsB, vwapB);
	}

}