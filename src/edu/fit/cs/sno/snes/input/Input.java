package edu.fit.cs.sno.snes.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.EnumMap;
import java.util.Map;

import edu.fit.cs.sno.snes.Core;
import edu.fit.cs.sno.snes.cpu.hwregs.CPURegisters;
import edu.fit.cs.sno.snes.ppu.OAM;
import edu.fit.cs.sno.snes.ppu.PPU;

public class Input {
	public static Map<SNESController, Boolean> state = new EnumMap<SNESController, Boolean>(SNESController.class);
	static {
		state.put(SNESController.A, false);
		state.put(SNESController.B, false);
		state.put(SNESController.X, false);
		state.put(SNESController.Y, false);
		state.put(SNESController.L, false);
		state.put(SNESController.R, false);
		state.put(SNESController.SELECT, false);
		state.put(SNESController.START, false);
		state.put(SNESController.UP, false);
		state.put(SNESController.DOWN, false);
		state.put(SNESController.LEFT, false);
		state.put(SNESController.RIGHT, false);
	}
	
	public static boolean readButton(int i) {
		switch (i) {
			case 0: return state.get(SNESController.B);
			case 1: return state.get(SNESController.Y);
			case 2: return state.get(SNESController.SELECT);
			case 3: return state.get(SNESController.START);
			case 4: return state.get(SNESController.UP);
			case 5: return state.get(SNESController.DOWN);
			case 6: return state.get(SNESController.LEFT);
			case 7: return state.get(SNESController.RIGHT);
			case 8: return state.get(SNESController.A);
			case 9: return state.get(SNESController.X);
			case 10: return state.get(SNESController.L);
			case 11: return state.get(SNESController.R);
			case 12: 
			case 13:
			case 14:
			case 15: return false;
			default: return true;
		}
	}
	
	public static void autoRead() {
		int joy1lVal = 0, joy1hVal = 0;
		
		joy1lVal |= (state.get(SNESController.A) ? 0x80 : 0);
		joy1lVal |= (state.get(SNESController.X) ? 0x40 : 0);
		joy1lVal |= (state.get(SNESController.L) ? 0x20 : 0);
		joy1lVal |= (state.get(SNESController.R) ? 0x10 : 0);
		
		joy1hVal |= (state.get(SNESController.B) ? 0x80 : 0);
		joy1hVal |= (state.get(SNESController.Y) ? 0x40 : 0);
		joy1hVal |= (state.get(SNESController.SELECT) ? 0x20 : 0);
		joy1hVal |= (state.get(SNESController.START) ? 0x10 : 0);
		joy1hVal |= (state.get(SNESController.UP) ? 0x08 : 0);
		joy1hVal |= (state.get(SNESController.DOWN) ? 0x04 : 0);
		joy1hVal |= (state.get(SNESController.LEFT) ? 0x02 : 0);
		joy1hVal |= (state.get(SNESController.RIGHT) ? 0x01 : 0);
		
		CPURegisters.joy1l.setValue(joy1lVal);
		CPURegisters.joy1h.setValue(joy1hVal);
	}
	
	public static KeyListener keyListener = new KeyListener() {

		@Override
		public void keyPressed(KeyEvent e) {
			if (!handleFunctionKeys(e)) {
				Input.state.put(SNESController.fromKeyCode(e.getKeyCode()), true);
			}
			
			// Don't draw frames while pressed
			if (e.getKeyCode() == KeyEvent.VK_BACK_QUOTE) {
				PPU.renderFrames = false;
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			Input.state.put(SNESController.fromKeyCode(e.getKeyCode()), false);
			if (e.getKeyCode() == KeyEvent.VK_BACK_QUOTE) {
				PPU.renderFrames = true;
			}
		}

		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	};
	
	public static boolean handleFunctionKeys(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_1:
				PPU.bg[0].userEnabled = !PPU.bg[0].userEnabled;
				return true;
			case KeyEvent.VK_2:
				PPU.bg[1].userEnabled = !PPU.bg[1].userEnabled;
				return true;
			case KeyEvent.VK_3:
				PPU.bg[2].userEnabled = !PPU.bg[2].userEnabled;
				return true;
			case KeyEvent.VK_4:
				PPU.bg[3].userEnabled = !PPU.bg[3].userEnabled;
				return true;
			case KeyEvent.VK_5:
				OAM.userEnabled = !OAM.userEnabled;
				return true;
			case KeyEvent.VK_F1:
				Core.pause = !Core.pause;
				return true;
			case KeyEvent.VK_F2:
				Core.pause = true;
				Core.advanceFrameOnce = true;
				return true;
			case KeyEvent.VK_F11:
				PPU.drawWindow1 = !PPU.drawWindow1;
				return true;
			case KeyEvent.VK_F12:
				PPU.drawWindow2 = !PPU.drawWindow2;
				return true;
		}
		
		return false;
	}
}
