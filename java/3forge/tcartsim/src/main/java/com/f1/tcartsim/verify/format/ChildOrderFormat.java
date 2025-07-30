/**
 * 
 */
package com.f1.tcartsim.verify.format;

/**
 * This class contains the positions of fields in Child Order records parsed by ANVIL.
 * @author george
 *
 */
public final class ChildOrderFormat extends RecordFormat {

	public static final int POSITION_SYMBOL = 2;
	public static final int POSITION_LIMITPX = 3;
	public static final int POSITION_SIZE = 4;
	public static final int POSITION_PARENTORDERID = 5;
	public static final int POSITION_STATUS = 6;
	public static final int POSITION_CHILDORDERID = 7;
	public static final int POSITION_ORIGCHILDORDERID = 8;
	public static final int POSITION_CURRENCY = 9;
	public static final int POSITION_VARIANTS = 10;
	
}
