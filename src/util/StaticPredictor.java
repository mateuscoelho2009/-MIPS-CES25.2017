package util;

public class StaticPredictor implements Predictor{

	@Override
	public boolean executeBranch() {
		return false;
	}

	@Override
	public void updateState(boolean correctAction) {
		return;
	}

}
