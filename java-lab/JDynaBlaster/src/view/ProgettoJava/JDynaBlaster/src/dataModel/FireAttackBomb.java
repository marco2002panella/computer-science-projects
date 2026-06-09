package dataModel;

public class FireAttackBomb implements CanAttack<Player>{
	private Bomb bomb1; 
	private Player player;
	private int counter;
	private GameModel cd;
	private int seconds;
	private long lastTime;
	private boolean finish;
	public FireAttackBomb(Player in,GameModel cd){
		player=in;
		counter=0;
		this.cd=cd;
		bomb1=new Bomb(player.getX(), player.getY()+player.getAltezza()/2-4, 40, 40, cd);
		seconds=10;
		lastTime=System.nanoTime();
		finish=false;
	}
	
	@Override
	public void attack(){
		if(bomb1.isPiazzable()){
			counter++;
			bomb1.setTimer(75);
			bomb1.setPos(player.getX(),player.getY()+player.getAltezza()/2-4);
			cd.updateController(this);
		}
	}
	
	/**imposta la durata dell'upgrade in secondi*/
	public void setTime(int seconds){
		this.seconds=seconds;
	}
	/**ritorna la durata dell'upgrade*/
	public int getTime() {
		return seconds;
	}
	
	/**ritorna la bomba*/
	public Bomb getBomb() {
		return bomb1;
	}
	
	/**aggiorna posizione e timer della bomba*/
	public void update(){
		if(System.nanoTime()-lastTime>seconds*1000000000l)
			finish=true;
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
			if(finish)
				cd.getPlayer().setAttack(new SingleBombAttack(cd.getPlayer(),cd));
		}
	}
	
	/**ritorna un booleano che indica se il mio attacco è ancora in corso o no*/
	public boolean canChange() {
		return bomb1.getTimer()==0 && finish;
	}

	@Override
	public void stop() {
		bomb1.setTimer(0);
	}
}
