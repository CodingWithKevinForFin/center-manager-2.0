package com.threeforge.clients.wafra;

public class ErrorInfoCodes {
	public static final String E001 = "3FW-E001:Authentication Error, could not establish a valid connection to AMIDB";
	public static final String E002 = "3FW-E002:Connection timeout";
	public static final String E003 = "3FW-E003:Encrypted password not found, looking for plaintext password";
	public static final String E004 = "3FW-E004:There is a SQL Exception, please check your SQL queries";
	public static final String E005 = "3FW-E005:Connection closed error";
	public static final String E006 = "3FW-E006:JDBC Driver Error";
	public static final String I001 = "3FW-I001:Connection established. Running script!";
	public static final String I002 = "3FW-I002:Finished running queries";
	public static final String I003 = "3FW-I003:Connection closed !!";
	public static final String I004 = "3FW-I004:Running Queries";
	public static final String I005 = "3FW-I005:Starting Wafra 3forge JDBC Client";
	public static final String I006 = "3FW-I006:Query finished and returned: ";
	public static final String I007 = "3FW-I007:Connecting Wafra 3forge JDBC Client, attempting to connect to url: ";
}
