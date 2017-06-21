package util;

import util.Instruction.INSTR_TYPE;

public class EstacaoReserva {
	ULA ula;
	boolean busy;
	INSTR_TYPE Op;
	int Vj, Vk, Qj, Qk, address;
	
	public EstacaoReserva() {
		// TODO Auto-generated constructor stub
		ula = new ULA();
		busy = false;
		Op = INSTR_TYPE.UNDEFINED;
		Vj = -1;
		Vk = -1;
		Qj = -1;
		Qk = -1;
		address = -1;
	}
	
	public boolean isBusy() {
		return busy;
	}

	public void passInstruction(Instruction inst) {
		ula.set(inst);
		busy = true;
		
		checkDependencies();
	}
	
	boolean hasDependencies () {
		return (Qj == -1) || (Qk == -1);
	}
	
	void checkDependencies () {
		// TODO: Checar dependencias.
		if (!isBusy()) return;
		
		
	}

	public void tick() {
		if (hasDependencies()) {
			checkDependencies();
		}
		
		// TODO Auto-generated method stub
		if (isBusy() && !hasDependencies()) {
			busy = !ula.tick();
		}
	}
}
