package util;

public class Predictor1Bit implements Predictor{
	
	private boolean state;
	
	public Predictor1Bit(){
		state = false;
	}
	
	@Override
	public boolean executeBranch(){
		return state;
	}
	
	@Override
	public void updateState(boolean correctAction) {
		if (!correctAction){
			if(state)
				state = false;
			else
				state = true;
		}
	}
}
