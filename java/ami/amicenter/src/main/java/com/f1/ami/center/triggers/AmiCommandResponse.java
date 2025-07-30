package com.f1.ami.center.triggers;

import com.f1.ami.amicommon.msg.AmiRelayRunAmiCommandResponse;

/**
 * A response to an {@link AmiCommandTrigger} call. See {@link AmiCommandTrigger#onCommand(AmiCommandRequest)}. A response includes a status, text to the user, and AmiScript that
 * can be executed on the front end.
 */
public class AmiCommandResponse {

	/**
	 * Keep command dialog open. Usually an AmiMessage is included which is displayed to user.
	 */
	public static final byte STATUS_DONT_CLOSE_DIALOG = AmiRelayRunAmiCommandResponse.STATUS_DONT_CLOSE_DIALOG;

	/**
	 * Okay, close the dialog
	 */
	public static final byte STATUS_OKAY = AmiRelayRunAmiCommandResponse.STATUS_OKAY;
	private int statusCode;
	private String amiMessage;
	private String amiScript;

	/**
	 * 
	 * @param statusCode
	 *            see STATUS_... codes
	 * @param amiMessage
	 *            message to display to user
	 * @param amiScript
	 *            script to execute on frontend, or null
	 */
	public AmiCommandResponse(int statusCode, String amiMessage, String amiScript) {
		this.statusCode = statusCode;
		this.amiMessage = amiMessage;
		this.amiScript = amiScript;
	}

	/**
	 * @return See response codes
	 */
	public int getStatusCode() {
		return this.statusCode;
	}

	/**
	 * 
	 * @param statusCode
	 *            see STATUS_... codes
	 */
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 * @return the message to display to user
	 */
	public String getAmiMessage() {
		return this.amiMessage;
	}

	/**
	 * 
	 * @param message
	 * 			  the message to display to user
	 */
	public void setAmiMessage(String message) {
		this.amiMessage = message;
	}

	/**
	 * @return the AMI Script to execute on frontend
	 */
	public String getAmiScript() {
		return this.amiScript;
	}
	/**
	 * 
	 * @param amiScript
	 *            the AMI Script to execute on frontend
	 */
	public void setAmiScript(String amiScript) {
		this.amiScript = amiScript;
	}

}
