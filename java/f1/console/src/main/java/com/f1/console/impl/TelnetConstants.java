package com.f1.console.impl;

import com.f1.utils.SH;

public class TelnetConstants {
	/** End Of Record */
	public final static byte TELNET_EOR = (byte) 239;

	/** Telnet option: binary mode */
	public final static byte TELNET_OPTION_BINARY = (byte) 0;

	/** Telnet option: echo text (RFC 857) */
	public final static byte TELNET_OPTION_ECHO = (byte) 1;

	/** Telnet option: suppress go ahead (RFC 858) */
	public final static byte TELNET_OPTION_SGA = (byte) 3;

	/** Telnet option: status (RFC 859) */
	public final static byte TELNET_OPTION_STATUS = (byte) 5;

	/** Telnet option: timing mark (RFC 860) */
	public final static byte TELNET_OPTION_TM = (byte) 6;

	/** Telnet option: Terminal Type (RFC 1091) */
	public final static byte TELNET_OPTION_TTYPE = (byte) 24;

	/** Telnet option: End Of Record */
	public final static byte TELNET_OPTION_EOR = (byte) 25;

	/** Telnet option: Negotiate About Window Size (RFC 1073) */
	public final static byte TELNET_OPTION_NAWS = (byte) 31;

	/** Telnet option: terminal speed (RFC 1079) */
	public final static byte TELNET_OPTION_TS = (byte) 32;

	/** Telnet option: remote flow control (RFC 1372) */
	public final static byte TELNET_OPTION_RFCNTRL = (byte) 33;

	/** Telnet option: line mode (RFC 1148) */
	public final static byte TELNET_OPTION_LMODE = (byte) 34;

	/** Telnet option: environment variables (RFC 1408) */
	public final static byte TELNET_OPTION_EVARS = (byte) 36;

	/** Telnet option: new environment (RFC 1572) */
	public final static byte TELNET_OPTION_NEW_ENVIRONMENT = (byte) 39;

	/** Sub-negotiation End */
	public final static byte TELNET_SE = (byte) 240;

	/** no operation */
	public final static byte TELNET_NOP = (byte) 241;

	/** data mark */
	public final static byte TELNET_DM = (byte) 242;

	/** break */
	public final static byte TELNET_BRK = (byte) 243;

	/** suspend */
	public final static byte TELNET_IP = (byte) 244;

	/** abort output */
	public final static byte TELNET_AO = (byte) 245;

	/** are you there */
	public final static byte TELNET_AYT = (byte) 246;

	/** erase character */
	public final static byte TELNET_EC = (byte) 247;

	/** erase line */
	public final static byte TELNET_EL = (byte) 248;

	/** go ahead */
	public final static byte TELNET_GA = (byte) 249;

	/** Sub-negotiation Begin */
	public final static byte TELNET_SB = (byte) 250;

	/** WILL */
	public final static byte TELNET_WILL = (byte) 251;

	/** WONT */
	public final static byte TELNET_WONT = (byte) 252;

	/** DO */
	public final static byte TELNET_DO = (byte) 253;

	/** DONT */
	public final static byte TELNET_DONT = (byte) 254;

	/** IAC - interpret as command */
	public final static byte TELNET_IAC = (byte) 255;

	public final static byte TELNET_REQUIRED = 1;
	public final static byte TELNET_OPTIONAL = 0;

	public final static String NEWLINE = "" + SH.CHAR_LF + SH.CHAR_CR;
}
