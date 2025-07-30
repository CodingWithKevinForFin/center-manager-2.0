package com.f1.ami.relay;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadFactory;

import com.f1.ami.relay.fh.AmiFH;
import com.f1.ami.relay.plugins.AmiRelayInvokablePlugin;
import com.f1.container.ContainerTools;

//Please note, it's assumed the user is very familiar with the AMI Real-time Messaging API
//
//For the encodedMap(s) parameters:
//
//  (A) you can use the AmiRelayMapToBytesConverter::toBytes(...) method to conveniently convert a map of params to the expected byte protocol.
//  (B) you can implement the following protocol:
//
//   TotalMessage:  <-- this is what should be passed in as encodedMaps
//       KeyValuePairCount (signed short) <--number of entries in this map
//       Key[]  <- back to back entry of all key names.  (see below)
//       Value[] <- back to back entry of all values, note there is a different protocol depending on type
//
//   Key:
//		StringLengthOfKey (signed byte)
//      Ascii Representation of key's chars (byte array)
//
//   Value for Int between 0x80 ... 0x7f:
//      0x0A value (byte)
//
//   Value for Int between  0x8000 ... 0x7fff:
//      0x0B value (2 bytes)
//
//   Value for Int between  0x800000 ... 0x7fffff:
//      0x0C value (3 bytes)
//
//   Value for Int between  0x80000000 ... 0x7fffffff:
//      0x0D value (4 bytes)
//
//   Value for Long between 0x80 ... 0x7f:
//      0x0E value (byte)
//
//   Value for Long between  0x8000 ... 0x7fff:
//      0x0F value (2 bytes)
//
//   Value for Long between  0x800000 ... 0x7fffff:
//      0x10 value (3 bytes)
//
//   Value for Long between  0x80000000 ... 0x7fffffff:
//      0x11value (4 bytes)
//      
//   Value for Long between  0x8000000000 ... 0x7fffffffff:
//      0x12 value (5 bytes)
//      
//   Value for Long between  0x800000000000 ... 0x7fffffffffff:
//      0x13 value (6 bytes)
//      
//   Value for Long between  0x80000000000000 ... 0x7fffffffffffff:
//      0x14 value (7 bytes)
//      
//   Value for Long between  0x8000000000000000 ... 0x7fffffffffffffff:
//      0x15 value (8 bytes)
//      
//   Value for Double:
//      0x06 value (8 bytes) (see Double.doubleToLongBits)
//      
//   Value for Float:
//      0x05 value (4 bytes) (see Float.floatToIntBits)
//      
//   Value for Character:
//      0x1A value (2 bytes) 
//      
//   Value for Boolean True:
//      0x02 0x01
//   Value for Boolean False:
//      0x02 0x00
//      
//   Value for string <=127 chars in length and simple ASCII (all chars between 0 ... 127)
//      0x09
//      number_of_chars (byte)
//      bytes of string (1 byte per char)
//
//   Value for string >127 chars in length and simple ASCII (all chars between 0 ... 127)
//      0x08
//      number_of_chars (int)
//      bytes of string (1 byte per char)
//      
//   Value for string with extended ASCII (at least one char not between 0 ... 127)
//      0x07
//      number_of_chars (int)
//      chars of string (2 bytes per char)
//      
//   Value for UTC
//      0x1E
//      milliseconds since epoch (6 bytes)
//      
//   Value for NANO timestamp
//      0x1F
//      nanoseconds since epoch (8 bytes)
//      
//   Value for binary data (aka byte array)
//      0x28
//      number_of_bytes (4 bytes)
//      bytes (1 byte per byte of raw data)

public interface AmiRelayIn {

	public static int RESPONSE_STATUS_OK = 0;
	public static int RESPONSE_STATUS_NOT_FOUND = 1;
	public static int RESPONSE_STATUS_ERROR = 2;

	//S (status) message
	public void onStatus(byte[] encodedMap);

	//R (response to execute command) message. 
	public void onResponse(String I_uniqueId, int S_status, String M_message, String X_executeAmiScript, Map<String, Object> params);

	//X (exit) message.  Clean=true means it was an expected exit, false is unexpected, ex line dropped
	public void onLogout(byte[] encodedMap, boolean clean);

	//L (login) message
	public void onLogin(String O_options, String PL_plugin, byte[] encodedMap);

	//C (command definition) message
	public void onCommandDef(String I_id, String N_name, int L_level, String W_whereRowLevel, String T_wherePanelLevel, String H_help, String A_formDefinition,
			String X_executeAmiScript, int P_priority, String E_enabled, String S_style, String M_multipleSelectMode, String F_fields, byte[] encodedMap, int callbacksMask);

	//O (object) message for batching. Set seqNum=-1 for no seqNum. All arrays should have same number of arguments.
	//	public void onObjects(long seqNum, String[] I_ids, String[] T_types, long E_expiresOn, byte[][] encodedMaps);

	//O (object) message. Set seqNum=-1 for no seqNum
	public void onObject(long seqNum, String I_id, String T_type, long E_expiresOn, byte[] encodedMap);

	//D (delete) message. 
	public void onObjectDelete(long origSeqnum, String I_ids, String T_type, byte[] encodedMaps);

	//when the connection is established, this should be supplied.  The optional encoded map will show as parameters on this connection
	public void onConnection(byte[] encodedMap);

	//when the connection has an unexpected error, this should be supplied.  The optional encoded map will show as parameters on this connection.  error is user-readable a message
	public void onError(byte[] encodedMap, CharSequence error);

	//Tools for this AMI instance, internal use
	public ContainerTools getTools();

	//For creating additional thread
	public ThreadFactory getThreadFactory();

	//To start another (typically sub) feedhandler.
	public void initAndStartFH(AmiFH fh2, String string);

	//get invokable plugins (see ami.relay.invokables property)
	public AmiRelayInvokablePlugin getInvokable(String typ);
	public Set<String> getInvokableTypes();
}
