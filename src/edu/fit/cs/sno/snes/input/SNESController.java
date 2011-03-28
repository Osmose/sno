package edu.fit.cs.sno.snes.input;

import edu.fit.cs.sno.util.Settings;

public enum SNESController {
	A,
	B,
	X,
	Y,
	SELECT,
	START,
	UP,
	DOWN,
	LEFT,
	RIGHT,
	L,
	R,
	OTHER;
	
	public static SNESController fromKeyCode(int code) {
            
            if (code == Settings.getInt(Settings.P1INPUT_A))
                return A;
            if (code == Settings.getInt(Settings.P1INPUT_B))
                return B;
            if (code == Settings.getInt(Settings.P1INPUT_X))
		return X;
            if (code == Settings.getInt(Settings.P1INPUT_Y))
				return Y;
            if (code == Settings.getInt(Settings.P1INPUT_L))
				return L;
            if (code == Settings.getInt(Settings.P1INPUT_R))
				return R;
            if (code == Settings.getInt(Settings.P1INPUT_START))
				return START;
            if (code == Settings.getInt(Settings.P1INPUT_SELECT))
				return SELECT;
            if (code == Settings.getInt(Settings.P1INPUT_UP))
				return UP;
            if (code == Settings.getInt(Settings.P1INPUT_DOWN))
				return DOWN;
            if (code == Settings.getInt(Settings.P1INPUT_LEFT))
				return LEFT;
            if (code == Settings.getInt(Settings.P1INPUT_RIGHT))
				return RIGHT;
		
		return OTHER;
	}
}
