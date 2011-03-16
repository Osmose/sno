package edu.fit.cs.sno.snes.cpu;

import edu.fit.cs.sno.snes.Core;
import edu.fit.cs.sno.snes.common.Instruction;
import edu.fit.cs.sno.snes.common.Register;
import edu.fit.cs.sno.snes.common.Size;
import edu.fit.cs.sno.snes.cpu.hwregs.CPURegisters;
import edu.fit.cs.sno.snes.cpu.instructions.Add;
import edu.fit.cs.sno.snes.cpu.instructions.AndAWithMemory;
import edu.fit.cs.sno.snes.cpu.instructions.BlockMove;
import edu.fit.cs.sno.snes.cpu.instructions.Branching;
import edu.fit.cs.sno.snes.cpu.instructions.CompareAWithMemory;
import edu.fit.cs.sno.snes.cpu.instructions.CompareXWithMemory;
import edu.fit.cs.sno.snes.cpu.instructions.CompareYWithMemory;
import edu.fit.cs.sno.snes.cpu.instructions.Decrement;
import edu.fit.cs.sno.snes.cpu.instructions.EOrAWithMemory;
import edu.fit.cs.sno.snes.cpu.instructions.FlagOps;
import edu.fit.cs.sno.snes.cpu.instructions.Increment;
import edu.fit.cs.sno.snes.cpu.instructions.Jump;
import edu.fit.cs.sno.snes.cpu.instructions.LoadAFromMemory;
import edu.fit.cs.sno.snes.cpu.instructions.LoadXFromMemory;
import edu.fit.cs.sno.snes.cpu.instructions.LoadYFromMemory;
import edu.fit.cs.sno.snes.cpu.instructions.Misc;
import edu.fit.cs.sno.snes.cpu.instructions.OrAWithMemory;
import edu.fit.cs.sno.snes.cpu.instructions.Pull;
import edu.fit.cs.sno.snes.cpu.instructions.Push;
import edu.fit.cs.sno.snes.cpu.instructions.Return;
import edu.fit.cs.sno.snes.cpu.instructions.Rotate;
import edu.fit.cs.sno.snes.cpu.instructions.Shift;
import edu.fit.cs.sno.snes.cpu.instructions.StoreAToMemory;
import edu.fit.cs.sno.snes.cpu.instructions.StoreXToMemory;
import edu.fit.cs.sno.snes.cpu.instructions.StoreYToMemory;
import edu.fit.cs.sno.snes.cpu.instructions.StoreZeroToMemory;
import edu.fit.cs.sno.snes.cpu.instructions.Subtract;
import edu.fit.cs.sno.snes.cpu.instructions.TestBits;
import edu.fit.cs.sno.snes.cpu.instructions.Transfer;
import edu.fit.cs.sno.snes.debug.CPUState;
import edu.fit.cs.sno.util.Log;
import edu.fit.cs.sno.util.Settings;
import edu.fit.cs.sno.util.Util;

public class CPU {
	public static final int PAGE_SIZE = 256;
	public static Register a = null;	// Accumulator
	public static Register b = null;	// "B" Register (Stores top bytes of A in 8-bit mode)
	public static Register dbr = null;	// Data Bank Register
	public static Register x = null;	// X Index Register
	public static Register y = null;	// Y Index Register
	public static Register dp = null;	// Direct Page Register
	public static Register sp = null;	// Stack Pointer
	public static Register pbr = null;	// Program Bank Register
	public static Register pc = null;	// Program Counter
	public static StatusRegister status = null;		// Processor Status Register
	public static boolean emulationMode = false;
	
	public static Register dataReg = null;	// Data Register - Holds data based on op addressing mode
	public static int dataBank = 0;			// Data Bank - Holds the bank being referenced by an op
	public static int dataAddr = 0;			// Data Address - Holds the address being referenced by an op
	
	// Used for determining how many cycles an instruction takes
	public static boolean indexCrossedPageBoundary;
	
	// Related to hardware registers
	public static boolean NMIEnable = false;
	public static boolean standardControllerRead = false;
	public static boolean timerH = false;
	public static boolean timerV = false;
	
	public static int htime = 0x1FF;
	public static int vtime = 0x1FF;
	public static boolean irqFlag = false;
	
	private static boolean intVBlank;
	
	private static boolean tracingEnabled;
	
	/**
	 * Initializes the CPU to a blank state. Needed for unit tests.
	 */
	public static void resetCPU() {
		a   = new Register(Size.SHORT, 0);	// Accumulator
		dbr = new Register(Size.BYTE, 0);	// Data Bank Register
		x   = new Register(Size.SHORT, 0);	// X Index Register
		y   = new Register(Size.SHORT, 0);	// Y Index Register
		dp  = new Register(Size.SHORT, 0);	// Direct Page Register
		sp  = new Register(Size.SHORT, 0x01FF);	// Stack Pointer
		pbr = new Register(Size.BYTE, 0);	// Program Bank Register
		pc  = new Register(Size.SHORT, 0);	// Program Counter
		status = new StatusRegister();		// Processor Status Register
		emulationMode = false;
		dataReg = new Register(Size.SHORT, 0);	// Data Register - Holds data based on op addressing mode
		dataBank = 0;			// Data Bank - Holds the bank being referenced by an op
		dataAddr = 0;			// Data Address - Holds the address being referenced by an op
		
		indexCrossedPageBoundary = false;
		
		tracingEnabled = Settings.get(Settings.CPU_DEBUG_TRACE).equals("true");
	}
	
	public static Instruction[] jmp = new Instruction[0x100];	// Jump Table

	/**
	 * Ye olde Jump Table initialization
	 */
	static {
		resetCPU();
		
		
		// Add with Carry
        jmp[0x69] = Add.addImmediate;
        jmp[0x61] = Add.addDPIndirectX;
        jmp[0x63] = Add.addStackRelative;
        jmp[0x65] = Add.addDP;
        jmp[0x67] = Add.addDPIndirectLong;
        jmp[0x6D] = Add.addAbsolute;
        jmp[0x6F] = Add.addAbsoluteLong;
        jmp[0x71] = Add.addDPIndirectY;
        jmp[0x72] = Add.addDPIndirect;
        jmp[0x73] = Add.addSRIndirectY;
        jmp[0x75] = Add.addDPX;
        jmp[0x77] = Add.addDPIndirectLongY;
        jmp[0x79] = Add.addAbsoluteY;
        jmp[0x7D] = Add.addAbsoluteX;
        jmp[0x7F] = Add.addAbsoluteLongX;
        
        // AND Accumulator with Memory
		jmp[0x21] = AndAWithMemory.andAMemDPIndirectX;
		jmp[0x23] = AndAWithMemory.andAMemStackRelative;
		jmp[0x25] = AndAWithMemory.andAMemDP;
		jmp[0x27] = AndAWithMemory.andAMemDPIndirectLong;
		jmp[0x29] = AndAWithMemory.andAMemImmediate;
		jmp[0x2D] = AndAWithMemory.andAMemAbsolute;
		jmp[0x2F] = AndAWithMemory.andAMemAbsoluteLong;
		jmp[0x31] = AndAWithMemory.andAMemDPIndirectY;
		jmp[0x32] = AndAWithMemory.andAMemDPIndirect;
		jmp[0x33] = AndAWithMemory.andAMemSRIndirectY;
		jmp[0x35] = AndAWithMemory.andAMemDPIndexedX;
		jmp[0x37] = AndAWithMemory.andAMemDPIndirectLongY;
		jmp[0x39] = AndAWithMemory.andAMemAbsoluteY;
		jmp[0x3D] = AndAWithMemory.andAMemAbsoluteX;
		jmp[0x3F] = AndAWithMemory.andAMemAbsoluteLongX;
	
		// Compare A with Memory
		jmp[0xC1] = CompareAWithMemory.cmpADPIndirectX;
		jmp[0xC3] = CompareAWithMemory.cmpAStackRelative;
		jmp[0xC5] = CompareAWithMemory.cmpADP;
		jmp[0xC7] = CompareAWithMemory.cmpADPIndirectLong;
		jmp[0xC9] = CompareAWithMemory.cmpAImmediate;
		jmp[0xCD] = CompareAWithMemory.cmpAAbsolute;
		jmp[0xCF] = CompareAWithMemory.cmpAAbsoluteLong;
		jmp[0xD1] = CompareAWithMemory.cmpADPIndirectY;
		jmp[0xD2] = CompareAWithMemory.cmpADPIndirect;
		jmp[0xD3] = CompareAWithMemory.cmpASRIndirectY;
		jmp[0xD5] = CompareAWithMemory.cmpADPX;
		jmp[0xD7] = CompareAWithMemory.cmpADPIndirectLongY;
		jmp[0xD9] = CompareAWithMemory.cmpAAbsoluteY;
		jmp[0xDD] = CompareAWithMemory.cmpAAbsoluteX;
		jmp[0xDF] = CompareAWithMemory.cmpAAbsoluteLongX;
		
		// Compare X With Memory
		jmp[0xE0] = CompareXWithMemory.cmpXImmediate;
		jmp[0xE4] = CompareXWithMemory.cmpXDP;
		jmp[0xEC] = CompareXWithMemory.cmpXAbsolute;
		
		// Compare Y With Memory
		jmp[0xC0] = CompareYWithMemory.cmpYImmediate;
		jmp[0xC4] = CompareYWithMemory.cmpYDP;
		jmp[0xCC] = CompareYWithMemory.cmpYAbsolute;
		
		// Decrement
		jmp[0x3A] = Decrement.decAccumulator;
        jmp[0xCE] = Decrement.decAbsolute;
        jmp[0xC6] = Decrement.decDirectPage;
        jmp[0xDE] = Decrement.decAbsoluteX;
        jmp[0xD6] = Decrement.decDirectPageX;
        jmp[0xCA] = Decrement.decX;
        jmp[0x88] = Decrement.decY;
		
        // Flag Ops
		jmp[0x18] = FlagOps.clearCarryFlag;
		jmp[0x38] = FlagOps.setCarryFlag;
		jmp[0x58] = FlagOps.clearInterruptDisableFlag;
        jmp[0x78] = FlagOps.setInterruptDisableFlag;
		jmp[0xB8] = FlagOps.clearOverflowFlag;
        jmp[0xC2] = FlagOps.resetProcessorStatusBits;
        jmp[0xD8] = FlagOps.clearDecimalModeFlag;
        jmp[0xE2] = FlagOps.setProcessorStatusBits;
        jmp[0xF8] = FlagOps.setDecimalModeFlag;
		jmp[0xFB] = FlagOps.exchangeCarryEmulationFlag;
        
        // Increment
		jmp[0x1A] = Increment.incAccumulator;
        jmp[0xC8] = Increment.incY;
		jmp[0xE6] = Increment.incDirectPage;
        jmp[0xE8] = Increment.incX;
		jmp[0xEE] = Increment.incAbsolute;
		jmp[0xF6] = Increment.incDirectPageX;
		jmp[0xFE] = Increment.incAbsoluteX;
		
		// Jump
		jmp[0x4C] = Jump.jumpAbsolute;
		jmp[0x6C] = Jump.jumpAbsoluteIndirect;
		jmp[0x7C] = Jump.jumpAbsoluteIndexedIndirect;
		jmp[0x5C] = Jump.jumpAbsoluteLong;
		jmp[0xDC] = Jump.jumpAbsoluteIndirectLong;
		
		// Jump to Subroutine
		jmp[0x20] = Jump.jumpSubAbsolute;
		jmp[0xFC] = Jump.jumpSubAbsoluteIndexedIndirect;
		jmp[0x22] = Jump.jumpSubLong;
		
		// Branching
		jmp[0x10] = Branching.branchPlus;
		jmp[0x30] = Branching.branchMinus;
		jmp[0x50] = Branching.branchOverflowClear;
		jmp[0x70] = Branching.branchOverflowSet;
		jmp[0x80] = Branching.branchAlways;
		jmp[0x82] = Branching.branchAlwaysLong;
		jmp[0x90] = Branching.branchCarryClear;
		jmp[0xB0] = Branching.branchCarrySet;
		jmp[0xD0] = Branching.branchNotEqual;
		jmp[0xF0] = Branching.branchEqual;
		
		// Load Accumulator from Memory
		jmp[0xA1] = LoadAFromMemory.loadADPIndexedIndirectX;
		jmp[0xA3] = LoadAFromMemory.loadAStackRelative;
		jmp[0xA5] = LoadAFromMemory.loadADirectPage;
		jmp[0xA7] = LoadAFromMemory.loadADPIndirectLong;
		jmp[0xA9] = LoadAFromMemory.loadAImmediate;
		jmp[0xAD] = LoadAFromMemory.loadAAbsolute;
		jmp[0xAF] = LoadAFromMemory.loadAAbsoluteLong;
		jmp[0xB1] = LoadAFromMemory.loadADPIndirectIndexedY;
		jmp[0xB2] = LoadAFromMemory.loadADPIndirect;
		jmp[0xB3] = LoadAFromMemory.loadASRIndirectIndexedY;
		jmp[0xB5] = LoadAFromMemory.loadADirectPageX;
		jmp[0xB7] = LoadAFromMemory.loadADPIndirectLongIndexedY;
		jmp[0xB9] = LoadAFromMemory.loadAAbsoluteIndexedY;
		jmp[0xBD] = LoadAFromMemory.loadAAbsoluteIndexedX;
		jmp[0xBF] = LoadAFromMemory.loadAAbsoluteLongIndexedX;
		
		// Load X from Memory
		jmp[0xA2] = LoadXFromMemory.loadXImmediate;
		jmp[0xAE] = LoadXFromMemory.loadXAbsolute;
		jmp[0xA6] = LoadXFromMemory.loadXDirectPage;
		jmp[0xBE] = LoadXFromMemory.loadXAbsoluteY;
		jmp[0xB6] = LoadXFromMemory.loadXDirectPageY;
		
		// Load X from Memory
		jmp[0xA0] = LoadYFromMemory.loadYImmediate;
		jmp[0xAC] = LoadYFromMemory.loadYAbsolute;
		jmp[0xA4] = LoadYFromMemory.loadYDirectPage;
		jmp[0xBC] = LoadYFromMemory.loadYAbsoluteX;
		jmp[0xB4] = LoadYFromMemory.loadYDirectPageX;

        // OR Accumulator with Memory
		jmp[0x01] = OrAWithMemory.orAMemDPIndirectX;
		jmp[0x03] = OrAWithMemory.orAMemStackRelative;
		jmp[0x05] = OrAWithMemory.orAMemDP;
		jmp[0x07] = OrAWithMemory.orAMemDPIndirectLong;
		jmp[0x09] = OrAWithMemory.orAMemImmediate;
		jmp[0x0D] = OrAWithMemory.orAMemAbsolute;
		jmp[0x0F] = OrAWithMemory.orAMemAbsoluteLong;
		jmp[0x11] = OrAWithMemory.orAMemDPIndirectY;
		jmp[0x12] = OrAWithMemory.orAMemDPIndirect;
		jmp[0x13] = OrAWithMemory.orAMemSRIndirectY;
		jmp[0x15] = OrAWithMemory.orAMemDPIndexedX;
		jmp[0x17] = OrAWithMemory.orAMemDPIndirectLongY;
		jmp[0x19] = OrAWithMemory.orAMemAbsoluteY;
		jmp[0x1D] = OrAWithMemory.orAMemAbsoluteX;
		jmp[0x1F] = OrAWithMemory.orAMemAbsoluteLongX;
		
		// EOR Accumulator with Memory
		jmp[0x41] = EOrAWithMemory.eorAMemDPIndirectX;
		jmp[0x43] = EOrAWithMemory.eorAMemStackRelative;
		jmp[0x45] = EOrAWithMemory.eorAMemDP;
		jmp[0x47] = EOrAWithMemory.eorAMemDPIndirectLong;
		jmp[0x49] = EOrAWithMemory.eorAMemImmediate;
		jmp[0x4D] = EOrAWithMemory.eorAMemAbsolute;
		jmp[0x4F] = EOrAWithMemory.eorAMemAbsoluteLong;
		jmp[0x51] = EOrAWithMemory.eorAMemDPIndirectY;
		jmp[0x52] = EOrAWithMemory.eorAMemDPIndirect;
		jmp[0x53] = EOrAWithMemory.eorAMemSRIndirectY;
		jmp[0x55] = EOrAWithMemory.eorAMemDPIndexedX;
		jmp[0x57] = EOrAWithMemory.eorAMemDPIndirectLongY;
		jmp[0x59] = EOrAWithMemory.eorAMemAbsoluteY;
		jmp[0x5D] = EOrAWithMemory.eorAMemAbsoluteX;
		jmp[0x5F] = EOrAWithMemory.eorAMemAbsoluteLongX;
        
		// Pull
		jmp[0x68] = Pull.pullAccumulator;
		jmp[0xAB] = Pull.pullDataBank;
		jmp[0x2B] = Pull.pullDirectPage;
		jmp[0x28] = Pull.pullStatus;
		jmp[0xFA] = Pull.pullX;
		jmp[0x7A] = Pull.pullY;
		
		// Push
		jmp[0xF4] = Push.pushEffectiveAbsolute;
		jmp[0xD4] = Push.pushEffectiveIndirect;
		jmp[0x62] = Push.pushEffectivePC;
		jmp[0x48] = Push.pushAccumulator;
		jmp[0x8B] = Push.pushDataBank;
		jmp[0x0B] = Push.pushDirectPage;
		jmp[0x4B] = Push.pushProgramBank;
		jmp[0x08] = Push.pushStatus;
		jmp[0xDA] = Push.pushX;
		jmp[0x5A] = Push.pushY;
		
		// Return
		jmp[0x40] = Return.returnFromInterrupt;
		jmp[0x6B] = Return.returnFromSubroutineLong;
		jmp[0x60] = Return.returnFromSubroutine;
		
		// Rotate
		jmp[0x2A] = Rotate.rotateLeftAccumulator;
		jmp[0x2E] = Rotate.rotateLeftAbsolute;
		jmp[0x26] = Rotate.rotateLeftDP;
		jmp[0x3E] = Rotate.rotateLeftAbsoluteX;
		jmp[0x36] = Rotate.rotateLeftDPX;
		jmp[0x6A] = Rotate.rotateRightAccumulator;
		jmp[0x6E] = Rotate.rotateRightAbsolute;
		jmp[0x66] = Rotate.rotateRightDP;
		jmp[0x7E] = Rotate.rotateRightAbsoluteX;
		jmp[0x76] = Rotate.rotateRightDPX;
		
		// Shift
		jmp[0x0A] = Shift.shiftLeftAccumulator;
		jmp[0x06] = Shift.shiftLeftDP;
		jmp[0x0E] = Shift.shiftLeftAbsolute;
		jmp[0x1E] = Shift.shiftLeftAbsoluteX;
		jmp[0x16] = Shift.shiftLeftDPX;
		jmp[0x4A] = Shift.shiftRightAccumulator;
		jmp[0x4E] = Shift.shiftRightAbsolute;
		jmp[0x46] = Shift.shiftRightDP;
		jmp[0x5E] = Shift.shiftRightAbsoluteX;
		jmp[0x56] = Shift.shiftRightDPX;
		
		// Store X to Memory
        jmp[0x86] = StoreXToMemory.storeXDirectPage;
        jmp[0x8E] = StoreXToMemory.storeXAbsolute;
        jmp[0x96] = StoreXToMemory.storeXDPIndexedY;
        
        // Store Y to Memory
        jmp[0x84] = StoreYToMemory.storeYDirectPage;
        jmp[0x8C] = StoreYToMemory.storeYAbsolute;
        jmp[0x94] = StoreYToMemory.storeYDPIndexedX;
        
        // Store A to Memory
        jmp[0x81] = StoreAToMemory.saveADPIndexedIndirectX;
        jmp[0x83] = StoreAToMemory.saveAStackRelative;
        jmp[0x85] = StoreAToMemory.saveADirectPage;
        jmp[0x87] = StoreAToMemory.saveADPIndirectLong;
        jmp[0x8D] = StoreAToMemory.saveAAbsolute;
        jmp[0x8F] = StoreAToMemory.saveAAbsoluteLong;
        jmp[0x91] = StoreAToMemory.saveADPIndirectIndexedY;
        jmp[0x92] = StoreAToMemory.saveADPIndirect;
        jmp[0x93] = StoreAToMemory.saveASRIndirectIndexedY;
        jmp[0x95] = StoreAToMemory.saveADirectPageX;
        jmp[0x97] = StoreAToMemory.saveADPIndirectLongIndexedY;
        jmp[0x99] = StoreAToMemory.saveAAbsoluteIndexedY;
        jmp[0x9D] = StoreAToMemory.saveAAbsoluteIndexedX;
        jmp[0x9F] = StoreAToMemory.saveAAbsoluteLongIndexedX;
        
        // Store Zero to Memory
        jmp[0x64] = StoreZeroToMemory.storeZeroDirectPage;
        jmp[0x74] = StoreZeroToMemory.storeZeroDPIndexedX;
        jmp[0x9C] = StoreZeroToMemory.storeZeroAbsolute;
        jmp[0x9E] = StoreZeroToMemory.storeZeroAbsoluteIndexedX;
        
        // Subtract with Borrow from Accumulator
        jmp[0xE9] = Subtract.subFromAImmediate;
        jmp[0xE1] = Subtract.subFromADPIndirectX;
        jmp[0xE3] = Subtract.subFromAStackRelative;
        jmp[0xE5] = Subtract.subFromADP;
        jmp[0xE7] = Subtract.subFromADPIndirectLong;
        jmp[0xED] = Subtract.subFromAAbsolute;
        jmp[0xEF] = Subtract.subFromAAbsoluteLong;
        jmp[0xF1] = Subtract.subFromADPIndirectY;
        jmp[0xF2] = Subtract.subFromADPIndirect;
        jmp[0xF3] = Subtract.subFromASRIndirectY;
        jmp[0xF5] = Subtract.subFromADPX;
        jmp[0xF7] = Subtract.subFromADPIndirectLongY;
        jmp[0xF9] = Subtract.subFromAAbsoluteY;
        jmp[0xFD] = Subtract.subFromAAbsoluteX;
        jmp[0xFF] = Subtract.subFromAAbsoluteLongX;
		
        // Test Bits
        jmp[0x04] = TestBits.testSetDP;
        jmp[0x0C] = TestBits.testSetAbsolute;
        jmp[0x89] = TestBits.testImmediate;
        jmp[0x2C] = TestBits.testAbsolute;
        jmp[0x24] = TestBits.testDP;
        jmp[0x3C] = TestBits.testAbsoluteX;
        jmp[0x34] = TestBits.testDPX;
		jmp[0x14] = TestBits.testResetDP;
		jmp[0x1C] = TestBits.testResetAbsolute;
        
		// Transfer
		jmp[0xAA] = Transfer.transferAtoX;
		jmp[0xA8] = Transfer.transferAtoY;
		jmp[0x5B] = Transfer.transferAtoDP;
		jmp[0x1B] = Transfer.transferAtoSP;
		jmp[0x7B] = Transfer.transferDPtoA;
		jmp[0x3B] = Transfer.transferSPtoA;
		jmp[0xBA] = Transfer.transferSPtoX;
		jmp[0x8A] = Transfer.transferXtoA;
		jmp[0x9A] = Transfer.transferXtoSP;
		jmp[0x9B] = Transfer.transferXtoY;
		jmp[0x98] = Transfer.transferYtoA;
		jmp[0xBB] = Transfer.transferYtoX;
		
		// Miscellaneous Instructions
		jmp[0x00] = Misc.softwareBreak;
		jmp[0xEA] = Misc.nop;
		jmp[0xEB] = Misc.exchangeBA;
		jmp[0x02] = Misc.coprocessorEnable;
		jmp[0xDB] = Misc.stopProcessor;
		
		//Block Move
		jmp[0x54] = BlockMove.blockMoveNegative;
		jmp[0x44] = BlockMove.blockMovePositive;
		
		//Unimplemented
		/*
		 * 0xCB Wait for Interrupt - Implied - WAI
		 */
	}
	
	/**
	 * When run, the next instruction cycle will trigger an NMI
	 */
	public static void triggerVBlank() {
		intVBlank = true;
	}
	
	public static boolean checkInterrupts() {
		if (NMIEnable && intVBlank) {
			if (!Settings.isTrue(Settings.CPU_ALT_DEBUG)) {
				Log.debug("Running vblank handler: " + Integer.toHexString(Core.mem.get(Size.SHORT, 0, 0xFFEA)));
			} else {
				Log.instruction("*** NMI");
			}
			stackPush(Size.BYTE,  status.getValue());
			stackPush(Size.BYTE,  pbr.getValue());
			stackPush(Size.SHORT, pc.getValue());
			pbr.setValue(0);
			if (!CPU.emulationMode)
				pc.setValue(Core.mem.read(Size.SHORT, 0, 0xFFEA));
			else
				pc.setValue(Core.mem.read(Size.SHORT, 0, 0xFFFA));
			intVBlank = false;
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Processes the instruction at the current PBR/PC as well as handling interrupts and other
	 * per-cycle things.
	 * 
	 * @return Number of cycles the last instruction took
	 */
	public static void cycle() {
		indexCrossedPageBoundary = false;
		
		checkInterrupts();
		
		// Save bank/pc values for display later
		int bank = pbr.getValue();
		int addr = pc.getValue();
		
		// Get the current opcode
		int opcode = Core.mem.read(Size.BYTE, pbr.getValue(), pc.getValue());
		pc.add(1);
		
		// Get the current instruction
		Instruction inst = jmp[opcode];
		if (inst == null) 
			throw new RuntimeException(String.format("Instruction not implemented: 0x%02x", opcode));
		
		// Init argument array
		int[] args;
		if (inst.addrMode == AddressingMode.IMPLIED) {	// Implied instructions determine their argument count
			args = new int[inst.argCount];
		} else {
			args = new int[inst.addrMode.getNumArgs()];
		}
		
		// Grab arguments
		if (args.length > 0) {
			for (int k = 0; k < args.length; k++) {
				args[k] = Core.mem.read(Size.BYTE, pbr.getValue(), pc.getValue());
				pc.add(1);
			}
		}
		
		// Log current instruction
		if (Log.instruction.enabled()) 
			logInstruction(opcode, args, bank, addr);	

		if(tracingEnabled)
			CPUState.saveState(opcode, args);
		
		// Perform the instruction (finally)
		Timing.cycle(inst.run(args)*6);
	}
	
	/**
	 * Performs a single instruction (used by unit tests)
	 * 
	 * @param opcode Opcode of instruction to perform
	 * @param args Arguments to pass to instruction
	 */
	public static void doOp(int opcode, int[] args) {
		Instruction inst = jmp[opcode];
		
		//loadDataRegister(inst.addrMode, inst.size.getRealSize(), args);
		inst.run(args);
	}
	
	/**
	 * Loads the data register and associated vars with data based on addressing mode and arguments
	 * 
	 * @param mode Addressing mode to use
	 * @param size Size of data being loaded
	 * @param args Arguments to instruction (contains addressing info)
	 */
	public static void loadDataRegister(AddressingMode mode, Size size, int[] args) {
		// Quick exit if implied
		if (!mode.load) return;
		
		// What addressing mode are we using?
		int indirectAddr, directAddr, origDataAddr;
		switch (mode) {
			case ABSOLUTE:
				dataBank = CPU.dbr.getValue();
				dataAddr = Util.limitShort((args[1] << 8) + args[0]);
				break;
			
			case ABSOLUTE_INDEXED_X:
				dataBank = CPU.dbr.getValue();
				dataAddr = Util.limitShort(((args[1] << 8) + args[0]) + CPU.x.getValue());
				
				// Determine if index crossed page boundary
				origDataAddr = (args[1] << 8) + args[0];
				if (origDataAddr / PAGE_SIZE != dataAddr / PAGE_SIZE)
					indexCrossedPageBoundary = true;
				break;
			
			case ABSOLUTE_INDEXED_Y:
				dataBank = CPU.dbr.getValue();
				dataAddr = Util.limitShort(((args[1] << 8) + args[0]) + CPU.y.getValue());
				
				// Determine if index crossed page boundary
				origDataAddr = (args[1] << 8) + args[0];
				if (origDataAddr / PAGE_SIZE != dataAddr / PAGE_SIZE)
					indexCrossedPageBoundary = true;
				break;
				
			case ABSOLUTE_INDEXED_INDIRECT:
				indirectAddr = Util.limitShort(((args[1] << 8) + args[0]) + CPU.x.getValue());
				dataBank = CPU.pbr.getValue();
				dataAddr = Core.mem.read(Size.SHORT, CPU.pbr.getValue(), indirectAddr);
				break;
			
			case ABSOLUTE_LONG:
				dataBank = args[2];
				dataAddr = Util.limitShort((args[1] << 8) + args[0]);
				break;
			
			case ABSOLUTE_LONG_INDEXED_X:
				directAddr = (args[2] << 16) + (args[1] << 8) + args[0];
				directAddr += CPU.x.getValue();
				
				dataBank = (directAddr & 0xFF0000) >> 16;
				dataAddr = directAddr & 0xFFFF;
				break;
			
			case ABSOLUTE_INDIRECT:
				indirectAddr = Util.limitShort((args[1] << 8) + args[0]);
				dataBank = CPU.pbr.getValue();
				dataAddr = Core.mem.read(Size.SHORT, 0, indirectAddr);
				break;
				
			case ABSOLUTE_INDIRECT_LONG:
				indirectAddr = Util.limitShort((args[1] << 8) + args[0]);
				dataBank = Core.mem.read(Size.BYTE, 0, Util.limitShort(indirectAddr + 2));
				dataAddr = Core.mem.read(Size.SHORT, 0, indirectAddr);
				break;
			
			case DIRECT_PAGE:
				dataBank = 0;
				dataAddr = Util.limitShort(args[0] + CPU.dp.getValue());
				break;
			
			case DIRECT_PAGE_INDEXED_X:
				dataBank = 0;
				dataAddr = Util.limitShort(args[0] + CPU.dp.getValue() + CPU.x.getValue());
				break;
			
			case DIRECT_PAGE_INDEXED_Y:
				dataBank = 0;
				dataAddr = Util.limitShort(args[0] + CPU.dp.getValue() + CPU.y.getValue());
				break;
			
			case DIRECT_PAGE_INDIRECT:
				indirectAddr = Util.limitShort(CPU.dp.getValue() + args[0]);
				dataBank = CPU.dbr.getValue();
				dataAddr = Core.mem.read(Size.SHORT, 0, indirectAddr);
				break;
			
			case DIRECT_PAGE_INDIRECT_LONG:
				indirectAddr = Util.limitShort(CPU.dp.getValue() + args[0]);
				dataBank = Core.mem.read(Size.BYTE, 0, Util.limitShort(indirectAddr + 2));
				dataAddr = Core.mem.read(Size.SHORT, 0, indirectAddr);
				break;
			
			case DIRECT_PAGE_INDEXED_INDIRECT_X:
				indirectAddr = Util.limitShort(CPU.dp.getValue() + CPU.x.getValue() + args[0]);
				dataBank = CPU.dbr.getValue();
				dataAddr = Core.mem.read(Size.SHORT, 0, indirectAddr);
				break;
			
			case DIRECT_PAGE_INDEXED_INDIRECT_Y:
				indirectAddr = Util.limitShort(CPU.dp.getValue() + args[0]);
				
				// Very odd; 24-bit number plus 16-bit number
				directAddr = Core.mem.read(Size.SHORT, 0, indirectAddr);
				origDataAddr = directAddr;
				directAddr += (CPU.dbr.getValue() << 16);
				directAddr += CPU.y.getValue();
				
				dataBank = (directAddr & 0xFF0000) >> 16;
				dataAddr = directAddr & 0xFFFF;
				
				// Determine if index crossed page boundary
				if (origDataAddr / PAGE_SIZE != dataAddr / PAGE_SIZE)
					indexCrossedPageBoundary = true;
				break;
			
			case DIRECT_PAGE_INDEXED_INDIRECT_LONG_Y:
				indirectAddr = Util.limitShort(CPU.dp.getValue() + args[0]);
				
				// Very odd; 24-bit number plus 16-bit number
				directAddr = Core.mem.read(Size.SHORT, 0, indirectAddr);
				origDataAddr = directAddr;
				directAddr += (Core.mem.read(Size.BYTE, 0, Util.limitShort(indirectAddr + 2)) << 16);
				directAddr += CPU.y.getValue();
				
				dataBank = (directAddr & 0xFF0000) >> 16;
				dataAddr = directAddr & 0xFFFF;
				
				// Determine if index crossed page boundary
				if (origDataAddr / PAGE_SIZE != dataAddr / PAGE_SIZE)
					indexCrossedPageBoundary = true;
				break;
			
			case PROGRAM_COUNTER_RELATIVE:
				dataReg.setValue(Size.SHORT, Util.signExtendByte(args[0]));
				// Don't bother loading
				return;

			case PROGRAM_COUNTER_RELATIVE_LONG:
				dataReg.setValue(Size.SHORT, (args[1] << 8) + args[0]);
				// Don't bother loading
				return;
			
			case STACK_RELATIVE:
				dataBank = 0;
				dataAddr = Util.limitShort(CPU.sp.getValue() + args[0]);
				break;
			
			case STACK_RELATIVE_INDIRECT_INDEXED_Y:
				indirectAddr = Util.limitShort(CPU.sp.getValue() + args[0]);
				
				// Very odd; 24-bit number plus 16-bit number
				directAddr = Core.mem.read(Size.SHORT, 0, indirectAddr) + (CPU.dbr.getValue() << 16);
				directAddr += CPU.y.getValue();
				
				dataBank = (directAddr & 0xFF0000) >> 16;
				dataAddr = directAddr & 0xFFFF;
				break;
			case IMMEDIATE_MEMORY:
				if (CPU.status.isMemoryAccess()) {
					dataReg.setValue(size, args[0]);
				} else {
					dataReg.setValue(size, (args[1] << 8) + args[0]);
				}
				
				// Don't bother loading from an address
				return;
			case IMMEDIATE_INDEX:
				if (CPU.status.isIndexRegister()) {
					dataReg.setValue(size, args[0]);
				} else {
					dataReg.setValue(size, (args[1] << 8) + args[0]);
				}
				
				// Don't bother loading from an address
				return;
		}
		
		// Load from memory
		dataReg.setValue(size, Core.mem.read(size, dataBank, dataAddr));
	}
	
	/**
	 * Saves the contents of the data register back into memory where it was loaded from
	 */
	public static void saveDataReg() {
		Core.mem.write(dataReg.getSize(), dataBank, dataAddr, dataReg.getValue());
	}
	
	/**
	 * Pushes a value onto the stack and decrements the stack pointer
	 * 
	 * @param size Size of value to store
	 * @param value Value to store
	 */
	public static void stackPush(Size size, int value) {
		if (size.getRealSize() == Size.SHORT) {
			CPU.sp.subtract(2);
			Core.mem.write(Size.SHORT, 0, CPU.sp.getValue() + 1, value);
		} else {
			Core.mem.write(Size.BYTE, 0, CPU.sp.getValue(), value);
			CPU.sp.subtract(1);
		}
	}
	
	/**
	 * Pulls a value from the "top" of the stack and increments the stack pointer
	 * 
	 * @param size Size of value to pull
	 * @return Value at the top of the stack
	 */
	public static int stackPull(Size size) {
		if (size.getRealSize() == Size.SHORT) {
			CPU.sp.add(2);
			return Core.mem.read(Size.SHORT, 0, CPU.sp.getValue() - 1);
		} else {
			CPU.sp.add(1);
			return Core.mem.read(Size.BYTE, 0, CPU.sp.getValue());
		}
	}
	
	/**
	 * Grabs the value from the top of the stack without removing it
	 * 
	 * @param size Size of value to peek
	 * @return Value at the top of the stack
	 */
	public static int stackPeek(Size size) {
		return Core.mem.read(size.getRealSize(), 0, CPU.sp.getValue() + 1);
	}
	
	/**
	 * Initializes the CPU and registers during a reset vector interrupt
	 */
	public static void resetVectorInit() {
		CPU.sp.setValue(0x0100 + (CPU.sp.getValue() & 0xFF)); // Stack High = 01
		CPU.dp.setValue(0); // Direct Page = 0000;
		CPU.pbr.setValue(0); // Program Bank = 00;
		CPU.dbr.setValue(0); // Data Bank = 00;
		
		CPU.irqFlag = false;
		
		CPU.status.setMemoryAccess(true); // m = 1
		CPU.status.setIndexRegister(true); // x = 1
		CPU.status.setDecimalMode(false); // d = 0
		CPU.status.setIrqDisable(true); // i = 1
		
		CPU.emulationMode = true; // Emulation flag = 1
		
		// Follow reset vector
		int reset = Core.mem.read(Size.SHORT, 0, 0xFFFC);
		Log.debug(String.format("Reset Vector: 0x%04x\n", reset & 0xFFFF));
		CPU.pc.setValue(reset & 0xFFFF);
	}
	
	/**
	 * Logs an instruction to the debug log
	 * 
	 * @param opcode Opcode on instruction
	 * @param args Array of opcode arguments
	 * @param bank Bank where opcode is located
	 * @param addr Address where opcode is located
	 */
	public static void logInstruction(int opcode, int[] args, int bank, int addr) {
		Instruction inst = jmp[opcode];
		
		String[] strArgs = new String[args.length];
		if (args.length > 0) {
			for (int k = 0; k < args.length; k++) {
				strArgs[k] = Integer.toHexString(args[k]);
				if (strArgs[k].length()==1) {
					strArgs[k] = '0'+strArgs[k];
				}
			}
		}
		
		if (Settings.isTrue(Settings.CPU_ALT_DEBUG)) {
			String argsString="";
			boolean argsCap = true;
			
			// Special cases for different addressing modes
			switch(inst.addrMode) {
				case IMMEDIATE_MEMORY:
				case IMMEDIATE_INDEX:
					argsString = "#$";
					for (int i = strArgs.length-1;i>=0;i--) {
						argsString += strArgs[i];
					}
					break;
				
				case ACCUMULATOR:
					argsString = "A";
					break;
					
				case IMPLIED:
					if (strArgs.length >=1)
					{
						argsString = "#$";
						if (opcode == 0xF4 || opcode==0x62) argsString = "$"; // PEA doesn't have a hash
						for (int i = strArgs.length-1;i>=0;i--) {
							argsString += strArgs[i];
						}
						if (opcode == 0x62) { // PEA needs a special case here
							argsString += String.format("  [$%4X]", pc.getValue()+ ((args[1] << 8) + args[0]));
						}
					} else {
						argsString = " ";
					}
					break;
				
				case DIRECT_PAGE_INDEXED_INDIRECT_LONG_Y:
					int indirectAddr, directAddr;
					indirectAddr = Util.limitShort(CPU.dp.getValue() + args[0]);

					directAddr = Core.mem.get(Size.SHORT, 0, indirectAddr);
					directAddr += (Core.mem.get(Size.BYTE, 0, Util.limitShort(indirectAddr + 2)) << 16);
					directAddr += CPU.y.getValue();
					int dataBank = (directAddr & 0xFF0000) >> 16;
					int dataAddr = directAddr & 0xFFFF;
					
					argsString = "[$"+strArgs[0].toUpperCase()+"],y"+ String.format("[$%02X:%04X]", dataBank, dataAddr);
					argsCap = false;	
					break;
					
				case DIRECT_PAGE:
					argsString = String.format("$%02X    [$%02X:%04X]", args[0], 0, (CPU.dp.getValue() + args[0]));
					argsCap = false;
					break;
				case DIRECT_PAGE_INDEXED_X:
					argsString = String.format("$%02X,x  [$%02X:%04X]", args[0], 0, (CPU.dp.getValue() + args[0] + CPU.x.getValue()));
					argsCap = false;
					break;
				case DIRECT_PAGE_INDIRECT_LONG:
					int iaddr = Util.limitShort(CPU.dp.getValue() + args[0]);
					int datadpBank = Core.mem.get(Size.BYTE, 0, Util.limitShort(iaddr + 2));
					int datadpAddr = Core.mem.get(Size.SHORT, 0, iaddr);
					argsString = String.format("[$%02X]  [$%02X:%04X]",args[0],datadpBank,datadpAddr);
					break;
				case ABSOLUTE_LONG_INDEXED_X:{
					int tDataBank = args[2];
					int tDataAddr = Util.limitShort(((args[1] << 8) + args[0]) + CPU.x.getValue());
					argsString = "$"+strArgs[2].toUpperCase() + strArgs[1].toUpperCase() + strArgs[0].toUpperCase()+",x"+String.format("[$%02X:%04X]", tDataBank,tDataAddr);
					argsCap = false;
					break;
				}
				case ABSOLUTE_INDEXED_X:{
					int tDataBank = CPU.dbr.getValue();
					int tDataAddr = Util.limitShort(((args[1] << 8) + args[0]) + CPU.x.getValue());
					argsString = "$"+strArgs[1].toUpperCase() + strArgs[0].toUpperCase()+",x"+ String.format("[$%02X:%04X]", tDataBank, tDataAddr);
					argsCap = false;
					break;}
				case ABSOLUTE_INDEXED_Y:{
					int tDataBank = CPU.dbr.getValue();
					int tDataAddr = Util.limitShort(((args[1] << 8) + args[0]) + CPU.y.getValue());
					argsString = "$"+strArgs[1].toUpperCase() + strArgs[0].toUpperCase()+",y"+ String.format("[$%02X:%04X]", tDataBank, tDataAddr);
					argsCap = false;
					break;}
				case BLOCK_MOVE:
					argsString = String.format("%2x %2x", args[1], args[0]);
					break;
				case ABSOLUTE_INDEXED_INDIRECT:
					indirectAddr = Util.limitShort(((args[1] << 8) + args[0]) + CPU.x.getValue());
					int datajBank = CPU.pbr.getValue();
					int datajAddr = Core.mem.get(Size.SHORT, CPU.pbr.getValue(), indirectAddr);
					if (opcode == 0x7C) // Jump is funky
					{
						argsString = String.format("($%s%s,x)[$%2X:%4X]",strArgs[1].toUpperCase(),strArgs[0].toUpperCase(),datajBank,datajAddr);
						argsCap = false;
						break;
					}
				default:
					argsString = "$";
					if (strArgs.length>=2) {
						for (int i = strArgs.length-1;i>=0;i--) {
							argsString += strArgs[i];
						}
						for (int i= strArgs.length; i<3;i++)
							argsString += "  ";
						if (strArgs.length == 3)
							argsString += String.format("[$%s:%s%s]", strArgs[2], strArgs[1],strArgs[0]);
						else {
							if (inst.mnemonic.startsWith("J"))
								argsString += String.format("[$%02X:%s%s]", bank, strArgs[1], strArgs[0]);
							else
								argsString += String.format("[$%02X:%s%s]", CPU.dbr.getValue(), strArgs[1], strArgs[0]);
						}
					} else {
						
						int offset = Util.signExtendByte(Integer.parseInt(strArgs[0],16));
						argsString +=strArgs[0]+"    [$"+Integer.toHexString(Util.limitShort(pc.getValue()+offset))+"]";
					}
					break;
			}

			Log.instruction(String.format("$%02X/%04X %02X %-8S %S %-19s A:%04X X:%04X Y:%04X D:%04X DB:%02X S:%04X P:%s%8s", 
					bank, 
					addr, 
					opcode, 
					Util.implode(strArgs, " "),
					inst.mnemonic,
					(argsCap ? argsString.toUpperCase() : argsString),
					CPU.a.getValue(Size.SHORT),
					CPU.x.getValue(),
					CPU.y.getValue(),
					CPU.dp.getValue(),
					CPU.dbr.getValue(),
					CPU.sp.getValue(),
					(CPU.emulationMode?"E":"e"),
					CPU.status.toString()
				)
			);
			if (inst.mnemonic.equals("JSR")||inst.mnemonic.equals("RTS")) {
				Log.instruction("\n");
			}
		} else {
			Log.instruction(String.format("%-6x %02x:%04x  %02x   A:%04x X:%04x Y:%04x P:%8s  %-18s %s", 
					((bank * 0x8000) + addr - 0x8000),  
					bank, 
					addr,
					opcode, 
					CPU.a.getValue(Size.SHORT),
					CPU.x.getValue(),
					CPU.y.getValue(),
					CPU.status.toString(),
					Util.implode(strArgs, " "),
					inst.name
				)
			);
		}
	}
}
