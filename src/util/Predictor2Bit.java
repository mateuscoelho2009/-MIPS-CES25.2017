package util;

public class Predictor2Bit implements Predictor{
	public enum STATE {S00, S01, S10, S11};
	
	private STATE state;
	
	public Predictor2Bit(){
		state = STATE.S00;
	}
	
	@Override
	public boolean executeBranch(){
		switch(state){
			case S00:
				return false;
			case S01:
				return false;
			case S10:
				return true;
			case S11:
				return true;
			default:
				return false;
		}
	}
	
	@Override
	public void updateState(boolean correctAction) {
		switch (state) {
		case S00:
			if (correctAction)
				state = STATE.S01;
			break;
		case S01:
			if (correctAction)
				state = STATE.S11;
			else
				state = STATE.S00;
			break;
		case S10:
			if (correctAction)
				state = STATE.S11;
			else
				state = STATE.S00;
			break;
		case S11:
			if(!correctAction)
				state = STATE.S10;
			break;
		}
	}

}
