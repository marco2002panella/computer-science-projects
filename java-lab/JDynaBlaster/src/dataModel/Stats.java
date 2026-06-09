package dataModel;

public class Stats {
	
	private int points,lives,timer;
	
	public Stats() {
		timer=0;
		points=0;
		lives=0;
	}
	
	
	public void setLives(int i) {
		lives=i;
		GameModel.getInstance().updateController(this);
	}
	
	public void setPoints(int i) {
		points=i;
		GameModel.getInstance().updateController(this);
	}

	public void setTimer(int i) {
		timer=i;
		GameModel.getInstance().updateController(this);
	}
	
	public int getLives() {
		return lives;
	}
	
	public int getPoints() {
		return points;
	}

	public int getTimer() {
		return timer;
	}
	
}
