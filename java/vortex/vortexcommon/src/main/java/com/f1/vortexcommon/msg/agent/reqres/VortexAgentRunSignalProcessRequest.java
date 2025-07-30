package com.f1.vortexcommon.msg.agent.reqres;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VA.RSPQ")
public interface VortexAgentRunSignalProcessRequest extends VortexAgentRequest {

	byte SIG_HUP = 1;//Hangup (POSIX)
	byte SIG_INT = 2;//Terminal interrupt (ANSI)
	byte SIG_QUIT = 3;//Terminal quit (POSIX)
	byte SIG_ILL = 4;//Illegal instruction (ANSI)
	byte SIG_TRAP = 5;//Trace trap (POSIX)
	byte SIG_IOT = 6;//IOT Trap (4.2 BSD)
	byte SIG_BUS = 7;//BUS error (4.2 BSD)
	byte SIG_FPE = 8;//Floating point exception (ANSI)
	byte SIG_KILL = 9;//Kill(can't be caught or ignored) (POSIX)
	byte SIG_USR1 = 10;//User defined signal 1 (POSIX)
	byte SIG_SEGV = 11;//Invalid memory segment access (ANSI)
	byte SIG_USR2 = 12;//User defined signal 2 (POSIX)
	byte SIG_PIPE = 13;//Write on a pipe with no reader, Broken pipe (POSIX)
	byte SIG_ALRM = 14;//Alarm clock (POSIX)
	byte SIG_TERM = 15;//Termination (ANSI)
	byte SIG_STKFLT = 16;//Stack fault
	byte SIG_CHLD = 17;//Child process has stopped or exited, changed (POSIX)
	byte SIG_CONT = 18;// Continue executing, if stopped (POSIX)
	byte SIG_STOP = 19;//Stop executing(can't be caught or ignored) (POSIX)
	byte SIG_TSTP = 20;//Terminal stop signal (POSIX)
	byte SIG_TTIN = 21;//Background process trying to read, from TTY (POSIX)
	byte SIG_TTOU = 22;//Background process trying to write, to TTY (POSIX)
	byte SIG_URG = 23;//Urgent condition on socket (4.2 BSD)
	byte SIG_XCPU = 24;//CPU limit exceeded (4.2 BSD)
	byte SIG_XFSZ = 25;//File size limit exceeded (4.2 BSD)
	byte SIG_VTALRM = 26;//	Virtual alarm clock (4.2 BSD)
	byte SIG_PROF = 27;//Profiling alarm clock (4.2 BSD)
	byte SIG_WINCH = 28;//Window size change (4.3 BSD, Sun)
	byte SIG_IO = 29;//I/O now possible (4.2 BSD)
	byte SIG_PWR = 30;//Power failure restart (System V)

	@PID(10)
	public String getProcessPid();
	public void setProcessPid(String pid);

	@PID(12)
	public long getProcessStartTime();
	public void setProcessStartTime(long processStartTime);

	@PID(13)
	public String getProcessOwner();
	public void setProcessOwner(String owner);

	@PID(14)
	public byte getSignal();
	public void setSignal(byte killType);

	@PID(15)
	public long getProcessId();
	public void setProcessId(long id);
}
