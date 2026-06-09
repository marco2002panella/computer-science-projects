package dataModel;

public class SingleBombAttack implements CanAttack<Player>{
	
	private Bomb bomb1;
	private Player player;
	private GameModel cd;
	
	public SingleBombAttack(Player in,GameModel cd){
		player=in;
		this.cd=cd;
		bomb1=new Bomb(player.getX(), player.getY()+player.getAltezza()/2-4, 40, 40, cd);
	}
	
	@Override
	public void attack(){
		if(bomb1.isPiazzable()) {
			bomb1.setTimer(125);
			bomb1.setPos(player.getX(),player.getY()+player.getAltezza()/2-4);
			cd.updateController(this);
		}
		
	}
	
	/**ritorna la bomba*/
	public Bomb getBomb() {
		return bomb1;
	}
	
	/**aggiorna posizione e timer delle due bombe*/
	public void update(){
		
		if(bomb1.getTimer()>0) {
			bomb1.decrease();
		}
		
		if(!cd.getPlayer().getPlayerSolid().interseca(bomb1.getSolida()) && bomb1.getCollision() ){
			cd.addSolidBlocks(bomb1.getSolida());
			bomb1.setCollision(false);
		}
		
		if(bomb1.getTimer()==30) {
			cd.removeSolidBlocks(bomb1.getSolida());
			bomb1.explode();
			bomb1.checkCollisionBlocks();
			cd.updateController(this);
		}
		
		if(bomb1.getTimer()<30 && bomb1.getTimer()>0) {
			bomb1.checkCollisionPlayer();
			bomb1.checkCollisionEnemies();
		}
		
		else if(bomb1.getTimer()<=0) {
			bomb1.setPiazzabile(true);
			bomb1.setEsplosa(false);
			cd.removeSolidBlocks(bomb1.getSolida());
			cd.updateController(this);
		}
		
	}
	
	/**ritorna un booleano che indica se il mio attacco è ancora in corso o no*/
	public boolean canChange(){
		return bomb1.getTimer()<=0;
	}
	
	@Override
	public void stop() {
		bomb1.setTimer(0);
		bomb1.setPiazzabile(true);
		bomb1.setEsplosa(false);
		GameModel.getInstance().updateController(this);
	}
}
