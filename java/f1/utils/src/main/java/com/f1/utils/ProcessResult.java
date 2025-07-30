package com.f1.utils;

import com.f1.utils.structs.Tuple3;

public class ProcessResult extends Tuple3<Process, byte[], byte[]> {
	//extends tuple for backwards compatibility

	public ProcessResult(Process process, byte[] stdout, byte[] stderr) {
		super(process, stdout, stderr);
	}

	public Process getProcess() {
		return super.getA();
	}
	public byte[] getStdout() {
		return super.getB();
	}
	public byte[] getStderr() {
		return super.getC();
	}
	public int getExitCode() {
		return getProcess().exitValue();
	}

	public String toString() {
		return "[" + getExitCode() + "] stdout==>" + getStdoutText() + "    stderr==>" + getStderrText();
	}

	public String getStderrText() {
		return toString(getStderr());
	}

	public String getStdoutText() {
		return toString(getStdout());
	}

	private String toString(byte[] a) {
		return a == null ? null : (SH.isAscii(a, .95) ? new String(a) : ("<" + a.length + " bytes>"));
	}

}
