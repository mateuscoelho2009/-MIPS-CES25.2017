package util;

public interface Predictor {
	public boolean executeBranch();
	public void updateState(boolean correctAction);

}
