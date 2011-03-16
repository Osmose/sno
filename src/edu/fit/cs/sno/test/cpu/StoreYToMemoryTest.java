

package edu.fit.cs.sno.test.cpu;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import edu.fit.cs.sno.snes.Core;
import edu.fit.cs.sno.snes.common.Size;
import edu.fit.cs.sno.snes.cpu.CPU;
import edu.fit.cs.sno.snes.cpu.instructions.StoreYToMemory;
import edu.fit.cs.sno.snes.mem.LoROMMemory;
import edu.fit.cs.sno.util.Settings;

public class StoreYToMemoryTest {
    @Before
    public void setUp() {
    	new StoreYToMemory();// For 100% coverage
    	Settings.init();
    	CPU.resetCPU();
        Core.mem = new LoROMMemory();
    }
    
    
    @Test 
    public void testStoreXAbsolute() {
        // Test operation
        Core.mem.set(Size.BYTE, 0x7E, 0x2112, 0);
        CPU.dbr.setValue(0x7E);
        CPU.y.setValue(5);
        CPU.doOp(0x8C, new int[] {0x12, 0x21});
        assertEquals(Core.mem.get(Size.BYTE, 0x7E, 0x2112), 5);
        
    }
    @Test
    public void testStoreXDPIndexedY() {

        // 8-bit memory access
        CPU.status.setMemoryAccess(true);
        
        // Test operation
        Core.mem.set(Size.BYTE, 0, 28, 0);
        CPU.dp.setValue(4);
        CPU.y.setValue(7);
        CPU.x.setValue(2);
        CPU.doOp(0x94, new int[] {22});
        assertEquals(Core.mem.get(Size.BYTE, 0, 28), 7);
        

        // 16-bit memory access
        CPU.status.setMemoryAccess(false);
        
        // Test 16-bit
        Core.mem.set(Size.SHORT, 0, 28, 0);
        CPU.dp.setValue(4);
        CPU.y.setValue(8);
        CPU.x.setValue(2);
        CPU.doOp(0x94, new int[] {22});
        assertEquals(Core.mem.get(Size.SHORT, 0, 28), 8);
    }
    @Test
    public void testStoreXDirectPage() {
     // 8-bit memory access
        CPU.status.setMemoryAccess(true);
        
        // Test operation
        Core.mem.set(Size.BYTE, 0, 26, 0);
        CPU.dp.setValue(4);
        CPU.y.setValue(9);
        CPU.doOp(0x84, new int[] {22});
        assertEquals(Core.mem.get(Size.BYTE, 0, 26), 9);
        
     // 16-bit memory access
        CPU.status.setMemoryAccess(false);
        
        // Test 16-bit
        Core.mem.set(Size.SHORT, 0, 26, 0x7EF0);
        CPU.dp.setValue(4);
        CPU.y.setValue(10);
        CPU.doOp(0x84, new int[] {22});
        assertEquals(Core.mem.get(Size.SHORT, 0, 26), 10);
    }
}
