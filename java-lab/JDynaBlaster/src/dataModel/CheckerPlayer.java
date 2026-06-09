package dataModel;

public class CheckerPlayer{
	boolean flag;
	public CheckerPlayer() {flag=false;}
	
	/**controlla se l'area solida di input interseca con l'area solida del player sul pannello*/
	public void check(AreaSolida solida){
		if(solida.interseca(GameModel.getInstance().getPlayer().getPlayerSolid()) && !flag && GameModel.getInstance().getPlayer().isAlive()) {
			GameModel.getInstance().getPlayer().dead();
			flag=true;
		}
	}
	
}
