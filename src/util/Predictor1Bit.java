package util;

public class Predictor1Bit {
	public static enum STATE {BRANCH, NOBRANCH};
	
	private STATE state;
	
	public Predictor1Bit(){
		state = STATE.NOBRANCH;
	}
	
	public boolean executeBranch(){
		return state == STATE.BRANCH;
	}
	
	public void updateState(boolean correctAction) {
		if (!correctAction){
			switch (state) {
			case NOBRANCH:
				state = STATE.BRANCH;
				break;
			case BRANCH:
				state = STATE.NOBRANCH;
				break;
			}
		}
	}
}
