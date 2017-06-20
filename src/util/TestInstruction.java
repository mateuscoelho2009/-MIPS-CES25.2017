package util;

import static org.junit.Assert.*;

import org.junit.Test;

import util.Instruction.INSTR_TYPE;

public class TestInstruction {

	@Test
	public void testAddAndAddI() {
		Instruction add = new Instruction("00000000001001110100100000100000");
		Instruction addi = new Instruction("00100000000000010000000000000011");
		
		assertEquals(add.getType(), INSTR_TYPE.R);
		assertEquals(add.getMnemonic(), Instruction.ADD);
		assertEquals(add.getRS(), 1);
		assertEquals(add.getRT(), 7);
		assertEquals(add.getRD(), 9);
		
		assertEquals(addi.getType(), INSTR_TYPE.I);
		assertEquals(addi.getMnemonic(), Instruction.ADDI);
		assertEquals(addi.getRS(), 0);
		assertEquals(addi.getRT(), 1);
		assertEquals(addi.getImmediate(), 3);
	}

}
