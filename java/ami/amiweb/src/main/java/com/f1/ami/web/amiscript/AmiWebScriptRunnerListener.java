package com.f1.ami.web.amiscript;

public interface AmiWebScriptRunnerListener {

	public void onScriptRunStateChanged(AmiWebScriptRunner amiWebScriptRunner, byte oldState, byte state);

}
