package com.f1.ami.center.table;

import java.util.logging.Logger;

import com.f1.utils.LH;
import com.f1.utils.SH;

public class AmiImdbWarningsManager {

	private static final Logger log = LH.get();
	private static final int MAX_WARNINGS_BYTES = 10000;
	private long warningsCount = 0;
	private long totalWarningsCount = 0;
	final private StringBuilder warningsBuffer = new StringBuilder();
	//	final private String[] stackName;
	//	final private byte[] stackType;
	//	private int stackSize;
	//	private int maxStackSize;

	public AmiImdbWarningsManager(int maxStackSize) {
		//		this.maxStackSize = maxStackSize;
		//		this.stackName = new String[this.maxStackSize];
		//		this.stackType = new byte[this.maxStackSize];
		//		this.stackSize = 0;
	}

	public void onWarning(String errorCode, AmiTable target, String objectName, String actionType, String description, AmiRow row) {
		this.warningsCount++;
		this.totalWarningsCount++;
		if (warningsBuffer.length() > MAX_WARNINGS_BYTES)
			return;
		this.warningsBuffer.append(errorCode).append(" -- ");
		if (target != null) {
			this.warningsBuffer.append(target.getName());
			if (objectName != null || actionType != null)
				warningsBuffer.append(":");
		}
		if (objectName != null) {
			this.warningsBuffer.append(objectName);
			if (actionType != null)
				warningsBuffer.append(":");
		}
		if (actionType != null)
			this.warningsBuffer.append(actionType);
		if (description != null)
			this.warningsBuffer.append("  ").append(description);
		if (row != null)
			this.warningsBuffer.append(" For Row ==> ").append(row);
		this.warningsBuffer.append('\n');
	}

	public long getTotalWarningsCount() {
		return this.totalWarningsCount;
	}
	public String drainWarnings() {
		if (warningsCount == 0)
			return "";
		if (warningsBuffer.length() > MAX_WARNINGS_BYTES)
			warningsBuffer.append("TOO_MANY_WARNINGS ").append(warningsCount).append(" total warning(s)\n");
		this.warningsCount = 0;
		return SH.toStringAndClear(this.warningsBuffer);
	}

	//	public boolean pushStack(byte type, String name) {
	//		if (this.stackSize >= this.maxStackSize) {
	//			onWarning("STACK_OVERFLOW", null, null, getStackString(), null, null);
	//			return false;
	//		}
	//		this.stackType[this.stackSize] = type;
	//		this.stackName[this.stackSize] = name;
	//		this.stackSize++;
	//		return true;
	//	}

	//	private String getStackString() {
	//		StringBuilder sb = new StringBuilder();
	//		for (int i = 0; i < this.stackSize; i++) {
	//			if (i != 0)
	//				sb.append(" --> ");
	//			sb.append(this.stackName[i]).append(':').append(AmiTableUtils.toStringForTriggerType(this.stackType[i]));
	//		}
	//		return sb.toString();
	//	}

	//	public void popStack(byte type, String name) {
	//		this.stackSize--;
	//		OH.assertEq(stackType[this.stackSize], type);
	//	}

	//	public boolean isInStack(byte type) {
	//		for (int i = 0; i < this.stackSize; i++)
	//			if (this.stackType[i] == type)
	//				return true;
	//		return false;
	//	}
}
