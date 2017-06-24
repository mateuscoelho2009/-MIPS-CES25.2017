package util;

public class Predictor1Bit implements Predictor{
	public static enum STATE {BRANCH, NOBRANCH};
	
	private STATE state;
	
	public Predictor1Bit(){
		state = STATE.NOBRANCH;
	}
	
	@Override
	public boolean executeBranch(){
		return state == STATE.BRANCH;
	}
	
	@Override
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
