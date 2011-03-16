package edu.fit.cs.sno.test.cpu;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import edu.fit.cs.sno.snes.Core;
import edu.fit.cs.sno.snes.cpu.CPU;
import edu.fit.cs.sno.snes.cpu.CPUUtil;
import edu.fit.cs.sno.snes.cpu.instructions.FlagOps;
import edu.fit.cs.sno.snes.mem.LoROMMemory;
import edu.fit.cs.sno.util.Settings;

/**
 * Increment Flag Operations Instruction tests
 */
public class FlagOpsTest extends TestCase {

    @Before
    public void setUp() {
    	new FlagOps();// For 100% coverage
    	Settings.init();
    	CPU.resetCPU();
        Core.mem = new LoROMMemory();
        CPUUtil.clearFlags();
    }
    
    @Test
    public void testClearCarry() {
        CPU.status.setCarry(true);
        CPU.doOp(0x18, null);
        assertFalse(CPU.status.isCarry());
    }
    
    @Test
    public void testClearDecimalModeFlag() {
        CPU.status.setDecimalMode(true);
        CPU.doOp(0xD8, null);
        assertFalse(CPU.status.isDecimalMode());
    }
    
    @Test
    public void testClearInterruptDisableFlag() {
        CPU.status.setIrqDisable(true);
        CPU.doOp(0x58, null);
        assertFalse(CPU.status.isIrqDisable());
    }
    
    @Test
    public void testClearOverflowFlag() {
        CPU.status.setOverflow(true);
        CPU.doOp(0xB8, null);
        assertFalse(CPU.status.isOverflow());
    }
    
    @Test 
    public void testExchangeCarryEmulationFlag() {
        CPU.status.setCarry(true);
        CPU.doOp(0xFB, null);
        assertTrue(CPU.emulationMode);
        assertFalse(CPU.status.isCarry());
        
        CPU.doOp(0xFB, null);
        assertFalse(CPU.emulationMode);
        assertTrue(CPU.status.isCarry());
    }
    
    @Test
    public void testSetCarryFlag() {
        CPU.doOp(0x38, null);
        assertTrue(CPU.status.isCarry());
    }
    
    @Test
    public void testSetDecimalModeFlag() {
        CPU.doOp(0xF8, null);
        assertTrue(CPU.status.isDecimalMode());
    }
    
    @Test
    public void testSetInterruptDisableFlag() {
        CPU.doOp(0x78, null);
        assertTrue(CPU.status.isIrqDisable());
    }
    
    @Test
    public void testSetProcessorStatusBits() {
        CPU.doOp(0xE2, new int[] {0xFF});
        assertTrue(CPU.status.isCarry());
        assertTrue(CPU.status.isZero());
        assertTrue(CPU.status.isIrqDisable());
        assertTrue(CPU.status.isDecimalMode());
        assertTrue(CPU.status.isIndexRegister());
        assertTrue(CPU.status.isMemoryAccess());
        assertTrue(CPU.status.isNegative());
        assertTrue(CPU.status.isOverflow());
    }
    
    @Test
    public void testSetProcessorStatusBits2() {
        CPU.doOp(0xE2, new int[] {0x30});
        assertFalse(CPU.status.isCarry());
        assertFalse(CPU.status.isZero());
        assertFalse(CPU.status.isIrqDisable());
        assertFalse(CPU.status.isDecimalMode());
        assertTrue(CPU.status.isIndexRegister());
        assertTrue(CPU.status.isMemoryAccess());
        assertFalse(CPU.status.isNegative());
        assertFalse(CPU.status.isOverflow());
    }
    
    @Test
    public void testSetProcessorStatusBits3() {
        CPU.doOp(0xE2, new int[] {0x00});
        assertFalse(CPU.status.isCarry());
        assertFalse(CPU.status.isZero());
        assertFalse(CPU.status.isIrqDisable());
        assertFalse(CPU.status.isDecimalMode());
        assertFalse(CPU.status.isIndexRegister());
        assertFalse(CPU.status.isMemoryAccess());
        assertFalse(CPU.status.isNegative());
        assertFalse(CPU.status.isOverflow());
    }

    @Test
    public void testResetProcessorStatusBits() {
        CPU.doOp(0xE2, new int[] {0xFF});
        CPU.doOp(0xC2, new int[] {0xFF});
        assertFalse(CPU.status.isCarry());
        assertFalse(CPU.status.isZero());
        assertFalse(CPU.status.isIrqDisable());
        assertFalse(CPU.status.isDecimalMode());
        assertFalse(CPU.status.isIndexRegister());
        assertFalse(CPU.status.isMemoryAccess());
        assertFalse(CPU.status.isNegative());
        assertFalse(CPU.status.isOverflow());
    }
    
    @Test
    public void testResetProcessorStatusBits2() {
        CPU.doOp(0xE2, new int[] {0xFF});
        CPU.doOp(0xC2, new int[] {0x30});
        assertTrue(CPU.status.isCarry());
        assertTrue(CPU.status.isZero());
        assertTrue(CPU.status.isIrqDisable());
        assertTrue(CPU.status.isDecimalMode());
        assertFalse(CPU.status.isIndexRegister());
        assertFalse(CPU.status.isMemoryAccess());
        assertTrue(CPU.status.isNegative());
        assertTrue(CPU.status.isOverflow());
    }
    
    @Test
    public void testResetProcessorStatusBits3() {
        CPU.doOp(0xE2, new int[] {0xFF});
        CPU.doOp(0xC2, new int[] {0x00});
        assertTrue(CPU.status.isCarry());
        assertTrue(CPU.status.isZero());
        assertTrue(CPU.status.isIrqDisable());
        assertTrue(CPU.status.isDecimalMode());
        assertTrue(CPU.status.isIndexRegister());
        assertTrue(CPU.status.isMemoryAccess());
        assertTrue(CPU.status.isNegative());
        assertTrue(CPU.status.isOverflow());
    }
}