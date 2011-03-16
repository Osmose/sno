

package edu.fit.cs.sno.test.cpu;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import edu.fit.cs.sno.snes.Core;
import edu.fit.cs.sno.snes.common.Size;
import edu.fit.cs.sno.snes.cpu.CPU;
import edu.fit.cs.sno.snes.cpu.instructions.StoreXToMemory;
import edu.fit.cs.sno.snes.mem.LoROMMemory;
import edu.fit.cs.sno.util.Settings;

public class StoreXToMemoryTest {
    @Before
    public void setUp() {
    	new StoreXToMemory();// For 100% coverage
    	Settings.init();
    	CPU.resetCPU();
        Core.mem = new LoROMMemory();
    }
    
    
    @Test 
    public void testStoreXAbsolute() {
        // Test operation
        Core.mem.set(Size.BYTE, 0x7E, 0x2112, 0);
        CPU.dbr.setValue(0x7E);
        CPU.x.setValue(5);
        CPU.doOp(0x8E, new int[] {0x12, 0x21});
        assertEquals(Core.mem.get(Size.BYTE, 0x7E, 0x2112), 5);
        
    }
    @Test
    public void testStoreXDPIndexedY() {

        // 8-bit memory access
        CPU.status.setMemoryAccess(true);
        
        // Test operation
        Core.mem.set(Size.BYTE, 0, 28, 0);
        CPU.dp.setValue(4);
        CPU.x.setValue(7);
        CPU.y.setValue(2);
        CPU.doOp(0x96, new int[] {22});
        assertEquals(Core.mem.get(Size.BYTE, 0, 28), 7);
        

        // 16-bit memory access
        CPU.status.setMemoryAccess(false);
        
        // Test 16-bit
        Core.mem.set(Size.SHORT, 0, 28, 0);
        CPU.dp.setValue(4);
        CPU.x.setValue(8);
        CPU.y.setValue(2);
        CPU.doOp(0x96, new int[] {22});
        assertEquals(Core.mem.get(Size.SHORT, 0, 28), 8);
    }
    @Test
    public void testStoreXDirectPage() {
     // 8-bit memory access
        CPU.status.setMemoryAccess(true);
        
        // Test operation
        Core.mem.set(Size.BYTE, 0, 26, 0);
        CPU.dp.setValue(4);
        CPU.x.setValue(9);
        CPU.doOp(0x86, new int[] {22});
        assertEquals(Core.mem.get(Size.BYTE, 0, 26), 9);
        
     // 16-bit memory access
        CPU.status.setMemoryAccess(false);
        
        // Test 16-bit
        Core.mem.set(Size.SHORT, 0, 26, 0x7EF0);
        CPU.dp.setValue(4);
        CPU.x.setValue(10);
        CPU.doOp(0x86, new int[] {22});
        assertEquals(Core.mem.get(Size.SHORT, 0, 26), 10);
    }
}
