package com.f1.console.impl.shell;

public interface UserShellCtrlBreakListener {

	int KEYCODE_CTRL_A = 1;
	int KEYCODE_CTRL_B = 2;
	int KEYCODE_CTRL_C = 3;
	int KEYCODE_CTRL_D = 4;
	int KEYCODE_CTRL_E = 5;
	int KEYCODE_CTRL_F = 6;
	int KEYCODE_CTRL_G = 7;
	int KEYCODE_CTRL_H = 8;
	int KEYCODE_CTRL_I = 9;
	int KEYCODE_CTRL_J = 10;
	int KEYCODE_CTRL_K = 11;
	int KEYCODE_CTRL_L = 12;
	int KEYCODE_CTRL_N = 14;
	int KEYCODE_CTRL_O = 15;
	int KEYCODE_CTRL_P = 16;
	int KEYCODE_CTRL_Q = 17;
	int KEYCODE_CTRL_R = 18;
	int KEYCODE_CTRL_S = 19;
	int KEYCODE_CTRL_T = 20;
	int KEYCODE_CTRL_U = 21;
	int KEYCODE_CTRL_V = 22;
	int KEYCODE_CTRL_W = 23;
	int KEYCODE_CTRL_X = 24;
	int KEYCODE_CTRL_Y = 25;
	int KEYCODE_CTRL_Z = 26;

	void onCtrlBreakListener(int ctrlPressedCount, int code);
	void onCtrlBreakListenerDuringReadline(int code);

}
