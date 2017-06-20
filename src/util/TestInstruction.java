package util;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestInstruction {

	@Test
	public void testAddAndAddI() {
		Instruction add = new Instruction("00000000001001110100100000100000");
		Instruction addi = new Instruction("00100000000000010000000000000011");
		
		assertEquals(add.getRS(), 9);
		assertEquals(add.getRT(), 1);
		assertEquals(add.getRD(), 7);
		
		assertEquals(addi.getRS(), 1);
		assertEquals(addi.getRT(), 0);
		assertEquals(addi.getImmediate(), 3);
	}

}
