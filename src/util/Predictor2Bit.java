package util;

public class Predictor2Bit {
	public enum STATE {BRANCH1, BRANCH2, NOBRANCH1, NOBRANCH2};
	
	private STATE state;
	
	public Predictor2Bit(){
		state = STATE.NOBRANCH1;
	}
	
	public boolean executeBranch(){
		return state == STATE.BRANCH1 || state == STATE.BRANCH2;
	}
	
	public void updateState(boolean correctAction) {
		switch (state) {
		case BRANCH1:
			if (!correctAction)
				state = STATE.BRANCH2;
			break;
		case BRANCH2:
			state = correctAction ? STATE.BRANCH1 : STATE.NOBRANCH1;
			break;
		case NOBRANCH1:
			if (!correctAction)
				state = STATE.NOBRANCH2;
			break;
		case NOBRANCH2:
			state = correctAction ? STATE.NOBRANCH1 : STATE.BRANCH1;
			break;
		}
	}

}
