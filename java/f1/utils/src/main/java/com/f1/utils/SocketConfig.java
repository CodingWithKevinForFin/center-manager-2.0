package com.f1.utils;

import java.net.Socket;
import java.net.SocketException;

import com.f1.base.Lockable;
import com.f1.base.LockedException;

public class SocketConfig implements Lockable {

	private boolean locked;
	private Boolean keepAlive;
	private Boolean oobInline;
	private Boolean reuseAddress;
	private Boolean tcpNoDelay;
	private Integer receivedBufferSize;
	private Integer sendBufferSize;
	private Integer soLinger;
	private Integer soTimeout;
	private Integer trafficClass;

	private Integer perfConnectionTime;
	private Integer perfLatency;
	private Integer perfBandwidth;

	public Socket applyToSocket(Socket socket) throws SocketException {
		if (keepAlive != null)
			socket.setKeepAlive(keepAlive);
		if (oobInline != null)
			socket.setOOBInline(oobInline);
		if (perfConnectionTime != null)
			socket.setPerformancePreferences(perfConnectionTime, perfLatency, perfBandwidth);
		if (receivedBufferSize != null)
			socket.setReceiveBufferSize(receivedBufferSize);
		if (reuseAddress != null)
			socket.setReuseAddress(reuseAddress);
		if (sendBufferSize != null)
			socket.setSendBufferSize(sendBufferSize);
		if (soLinger != null)
			socket.setSoLinger(true, soLinger);
		if (soTimeout != null)
			socket.setSoTimeout(soTimeout);
		if (tcpNoDelay != null)
			socket.setTcpNoDelay(tcpNoDelay);
		if (trafficClass != null)
			socket.setTrafficClass(trafficClass);
		return socket;
	}

	public Boolean getLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		LockedException.assertNotLocked(this);
		this.locked = locked;
	}

	//keep alive
	public void resetKeepAlive() {
		LockedException.assertNotLocked(this);
		this.keepAlive = null;
	}
	public void setKeepAlive(boolean keepAlive) {
		LockedException.assertNotLocked(this);
		this.keepAlive = keepAlive;
	}
	public Boolean getKeepAlive() {
		return keepAlive;
	}

	//oob inline
	public void resetOobInline() {
		LockedException.assertNotLocked(this);
		this.oobInline = null;
	}
	public void setOobInline(boolean oobInline) {
		LockedException.assertNotLocked(this);
		this.oobInline = oobInline;
	}
	public Boolean getOobInline() {
		return oobInline;
	}

	//reuse address
	public void resetReuseAddress() {
		LockedException.assertNotLocked(this);
		this.reuseAddress = null;
	}
	public void setReuseAddress(boolean reuseAddress) {
		LockedException.assertNotLocked(this);
		this.reuseAddress = reuseAddress;
	}
	public Boolean getReuseAddress() {
		return reuseAddress;
	}

	//tcp no delay
	public void resetTcpNoDelay() {
		LockedException.assertNotLocked(this);
		this.tcpNoDelay = null;
	}
	public void setTcpNoDelay(boolean tcpNoDelay) {
		LockedException.assertNotLocked(this);
		this.tcpNoDelay = tcpNoDelay;
	}
	public Boolean getTcpNoDelay() {
		return tcpNoDelay;
	}

	//received buffer size
	public void resetReceivedBufferSize() {
		LockedException.assertNotLocked(this);
		this.receivedBufferSize = null;
	}
	public void setReceivedBufferSize(int receivedBufferSize) {
		LockedException.assertNotLocked(this);
		this.receivedBufferSize = receivedBufferSize;
	}
	public Integer getReceivedBufferSize() {
		return receivedBufferSize;
	}

	public void resetSendBufferSize() {
		LockedException.assertNotLocked(this);
		this.sendBufferSize = null;
	}
	public void setSendBufferSize(int sendBufferSize) {
		LockedException.assertNotLocked(this);
		this.sendBufferSize = sendBufferSize;
	}
	public Integer getSendBufferSize() {
		return sendBufferSize;
	}

	//so linger
	public void resetSoLinger() {
		LockedException.assertNotLocked(this);
		this.soLinger = null;
	}
	public void setSoLinger(int soLinger) {
		LockedException.assertNotLocked(this);
		this.soLinger = soLinger;
	}
	public Integer getSoLinger() {
		return soLinger;
	}

	//sotimeout
	public void resetSoTimeout() {
		LockedException.assertNotLocked(this);
		this.soTimeout = null;
	}
	public void setSoTimeout(int soTimeout) {
		LockedException.assertNotLocked(this);
		this.soTimeout = soTimeout;
	}
	public Integer getSoTimeout() {
		return soTimeout;
	}

	//traffic class
	public void resetTrafficClass() {
		LockedException.assertNotLocked(this);
		this.trafficClass = null;
	}
	public void setTrafficClass(int trafficClass) {
		LockedException.assertNotLocked(this);
		this.trafficClass = trafficClass;
	}
	public Integer getTrafficClass() {
		return trafficClass;
	}

	//perf
	public void resetPerfConnection() {
		this.perfConnectionTime = null;
		this.perfLatency = null;
		this.perfBandwidth = null;
	}
	public void setPerfConnection(int perfConnectionTime, int perfLatency, int perfBandwidth) {
		LockedException.assertNotLocked(this);
		this.perfConnectionTime = perfConnectionTime;
		this.perfLatency = perfLatency;
		this.perfBandwidth = perfBandwidth;
	}
	public Integer getPerfConnectionTime() {
		return perfConnectionTime;
	}
	public Integer getPerfLatency() {
		return perfLatency;
	}
	public Integer getPerfBandwidth() {
		return perfBandwidth;
	}

	@Override
	public void lock() {
		this.locked = true;
	}

	@Override
	public boolean isLocked() {
		return locked;
	}

}
