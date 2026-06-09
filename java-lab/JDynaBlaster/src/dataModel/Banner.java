package dataModel;

public class Banner {
	
	private long timer;
	private int lives;
	private int points;
	
	private static Banner banner;
	
	private Banner() {
		updateTimer();
		updateLives();
		updatePoints();
	}
	
	/**restituisce l'unica istanza del banner*/
	public static Banner getInstance() {
		if(banner==null)
			banner=new Banner();
		return banner;
	}
	
	/**aggiorna il timer*/
	public void updateTimer() {
		timer=GameModel.Timer.getTimer();
		GameModel.getInstance().updateController(this);
	}
	/**aggiorna le vite*/
	public void updateLives() {
		lives=GameModel.getInstance().getPlayer().getLives();
	}
	/**aggiorna i punti*/
	public void updatePoints() {
		GameModel.getInstance().updateController(this);
	}
	/**ritorna il timer*/
	public long getTimer() {
		return timer;
	}
	/**ritorna le vite il timer*/
	public int getLives() {
		return lives;
	}
	/**ritorna i punti*/
	public int getPoints() {
		return points;
	}
	
}
