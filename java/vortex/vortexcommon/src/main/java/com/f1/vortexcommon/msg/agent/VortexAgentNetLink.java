package com.f1.vortexcommon.msg.agent;

import com.f1.base.PID;
import com.f1.base.PartialMessage;
import com.f1.base.VID;

@VID("F1.VA.NL")
public interface VortexAgentNetLink extends PartialMessage, VortexAgentEntity {

	// PIDS

	// http://linux-ip.net/gl/ip-cref/node16.html
	short STATE_UP = 1;
	short STATE_LOOPBACK = 2;
	short STATE_BROADCAST = 4;
	short STATE_POINTTOPOINT = 8;
	short STATE_MULTICAST = 16;
	short STATE_PROMISC = 32;
	short STATE_ALLMULTI = 64;
	short STATE_NOARP = 128;
	short STATE_DYNAMIC = 256;
	short STATE_SLAVE = 512;
	short STATE_LOWER_UP = 1024;

	byte TYPE_ETHER = 1;
	byte TYPE_SIT = 2;

	byte PID_NAME = 2;
	byte PID_INDEX = 3;
	byte PID_MTU = 4;
	byte PID_RX_PACKETS = 5;
	byte PID_RX_ERRORS = 6;
	byte PID_RX_DROPPED = 7;
	byte PID_RX_OVERRUN = 8;
	byte PID_RX_MULTICAST = 9;
	byte PID_TX_PACKETS = 11;
	byte PID_TX_ERRORS = 12;
	byte PID_TX_DROPPED = 13;
	byte PID_TX_CARRIER = 14;
	byte PID_TX_COLLSNS = 15;
	byte PID_STATE = 16;
	byte PID_TRANSMISSION_DETAILS = 18;
	byte PID_BROADCAST = 19;
	byte PID_MAC = 20;

	@PID(PID_NAME)
	public String getName();
	public void setName(String mac);

	@PID(PID_INDEX)
	public int getIndex();
	public void setIndex(int mac);

	@PID(PID_MTU)
	public long getMtu();
	public void setMtu(long mtu);

	@PID(PID_RX_PACKETS)
	public long getRxPackets();
	public void setRxPackets(long rxPackets);

	@PID(PID_RX_ERRORS)
	public long getRxErrors();
	public void setRxErrors(long rxErrors);

	@PID(PID_RX_DROPPED)
	public long getRxDropped();
	public void setRxDropped(long rxDropped);

	@PID(PID_RX_OVERRUN)
	public long getRxOverrun();
	public void setRxOverrun(long rxOverrun);

	@PID(PID_RX_MULTICAST)
	public long getRxMulticast();
	public void setRxMulticast(long rxMulticast);

	@PID(PID_TX_PACKETS)
	public long getTxPackets();
	public void setTxPackets(long txPackets);

	@PID(PID_TX_ERRORS)
	public long getTxErrors();
	public void setTxErrors(long txErrors);

	@PID(PID_TX_DROPPED)
	public long getTxDropped();
	public void setTxDropped(long txDropped);

	@PID(PID_TX_CARRIER)
	public long getTxCarrier();
	public void setTxCarrier(long txOverrun);

	@PID(PID_TX_COLLSNS)
	public long getTxCollsns();
	public void setTxCollsns(long txMulticast);

	@PID(PID_STATE)
	public short getState();
	public void setState(short state);

	@PID(PID_TRANSMISSION_DETAILS)
	public String getTransmissionDetails();
	public void setTransmissionDetails(String transmissionDetails);

	@PID(PID_BROADCAST)
	public String getBroadcast();
	public void setBroadcast(String mac);

	@PID(PID_MAC)
	public String getMac();
	public void setMac(String mac);
}
