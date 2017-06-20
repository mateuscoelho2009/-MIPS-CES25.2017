package util;

public class ULA {
	private int rs,rt,rd,target,immediate,ticker;
	private String shamt, funct;
	public ULA(){
		// Empty
	}
	public String set(Instruction inst){
		switch (inst.getType()) {
			case R:	artimethic(inst.getRs(), inst.getRt(), inst.getRd() inst.getShamt(), inst.getFunct());
					break;
			case I: immediate(inst.getMinemonico(), inst.getRS(), inst.getRt(), inst.getImmediate());
					break;
			case J: jump(inst.getTarget());
					break;
			default: System.out.println("Instrução Incorreta");
					break;
		}
	}
	private void arithmetic(int rs, int rt, int rd, String shamt, String funct){
			this.rs = rs; this.rt = rt; this.rd = rd;
			this.shamt = shamt; this.funct = funct;
			if(!srtcmp(funct,MUL))
				ticker = 3;
			else
				ticker = 1;
	}	
	private void immediate(String minemonico, int rs, int rt, int immediate){
		this.rs = rs; this.rt = rt; this.immediate = immediate;
		if(!srtcmp(minemonico,LW)||!srtcmp(minemonico,SW))
			ticker = 4;
		else
			ticker = 1;
	}
	private void tick(){
		switch (funct) {
		case ADD:	Arch.r.write(rd,Arch.r.read(rt)+Arch.r.read(rs));
					break;
		}
	}
}
