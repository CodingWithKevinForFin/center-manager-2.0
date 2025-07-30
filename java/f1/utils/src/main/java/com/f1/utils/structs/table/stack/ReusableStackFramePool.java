package com.f1.utils.structs.table.stack;

import com.f1.base.CalcFrame;

public class ReusableStackFramePool {

	//	private List<ReusableCalcFrameStack> pool = new ArrayList<ReusableCalcFrameStack>();

	public ReusableCalcFrameStack borrow(CalcFrameStack sf, CalcFrame frame) {
		//		if (pool.isEmpty())
		return new ReusableCalcFrameStack(sf, frame);
		//		else
		//			return pool.remove(pool.size() - 1).reset(sf, frame);
	}

	public void release(ReusableCalcFrameStack rsf) {
		//		rsf.clear();
		//		pool.add(rsf);
	}
}
