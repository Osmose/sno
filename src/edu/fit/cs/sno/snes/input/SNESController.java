package edu.fit.cs.sno.snes.input;

import java.awt.event.KeyEvent;

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
		switch (code) {
			case KeyEvent.VK_D:
				return A;
			case KeyEvent.VK_F:
				return B;
			case KeyEvent.VK_A:
				return X;
			case KeyEvent.VK_S:
				return Y;
			case KeyEvent.VK_W:
				return L;
			case KeyEvent.VK_E:
				return R;
			case KeyEvent.VK_ENTER:
				return START;
			case KeyEvent.VK_SHIFT:
				return SELECT;
			case KeyEvent.VK_UP:
				return UP;
			case KeyEvent.VK_DOWN:
				return DOWN;
			case KeyEvent.VK_LEFT:
				return LEFT;
			case KeyEvent.VK_RIGHT:
				return RIGHT;
		}
		
		return OTHER;
	}
}
