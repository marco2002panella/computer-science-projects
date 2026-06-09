package dataModel;

public class DoubleBombAttack implements CanAttack<Player>{
	
	private Bomb bomb1;
	private Bomb bomb2;
	private Player player;
	private GameModel cd;
	private long lastTime;
	private int seconds;
	private boolean finish;
	private int number;
	public DoubleBombAttack(Player in,GameModel cd){
		player=in;
		this.cd=cd;
		bomb1=new Bomb(player.getX(), player.getY()+player.getAltezza()/2-4, 40, 40, cd);
		bomb2=new Bomb(player.getX(), player.getY()+player.getAltezza()/2-4, 40, 40, cd);
		/**imposto il raggio esattamente la meta*/
		bomb1.setAreaSolida(new AreaSolida(30,30));
		bomb1.setAreaSolidax1(new AreaSolida(20,5));
		bomb1.setAreaSolidax2(new AreaSolida(20,5));
		bomb1.setAreaSoliday1(new AreaSolida(5,20));
		bomb1.setAreaSoliday2(new AreaSolida(5,20));
		bomb2.setAreaSolida(new AreaSolida(30,30));
		bomb2.setAreaSolidax1(new AreaSolida(20,5));
		bomb2.setAreaSolidax2(new AreaSolida(20,5));
		bomb2.setAreaSoliday1(new AreaSolida(5,20));
		bomb2.setAreaSoliday2(new AreaSolida(5,20));
		seconds=10;
		finish=false;
		lastTime=System.nanoTime();
	}
	
	@Override
	public void attack(){
		if(bomb1.isPiazzable()) {
			bomb1.setTimer(100);
			bomb1.setPos(player.getX(),player.getY()+player.getAltezza()/2-4);
			bomb1.setNumber(0);
			number=0;
			cd.updateController(this);
		}else if(bomb2.isPiazzable()){
			try {
				Thread.sleep(17);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			number=1;
			bomb1.setNumber(1);
			bomb2.setTimer(100);
			bomb2.setPos(player.getX(),player.getY()+player.getAltezza()/2-4);
			cd.updateController(this);
		}
	}
	
	public Bomb getBomb1(){
		return bomb1;
	}
	
	public Bomb getBomb2(){
		return bomb2;
	}
	
	/**aggiorna posizione e timer delle due bombe*/
	public void update(){
		
		if(System.nanoTime()-lastTime>seconds*1000000000l)
			finish=true;
		
		if(bomb1.getTimer()>=0) {
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
			number=0;
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
			number=0;
			cd.updateController(this);
			
		}
		
		if(bomb2.getTimer()>0) {
			bomb2.decrease();
		}
		
		if(!cd.getPlayer().getPlayerSolid().interseca(bomb2.getSolida()) && bomb2.getCollision() ){
			cd.addSolidBlocks(bomb2.getSolida());
			bomb2.setCollision(false);
		}
		
		if(bomb2.getTimer()==30) {
			cd.removeSolidBlocks(bomb2.getSolida());
			bomb2.explode();
			bomb2.checkCollisionBlocks();
			number=1;
			cd.updateController(this);
		}
		
		if(bomb2.getTimer()<30 && bomb2.getTimer()>0) {
			bomb2.checkCollisionPlayer();
			bomb2.checkCollisionEnemies();
		}
		
		else if(bomb2.getTimer()<=0) {
			bomb2.setPiazzabile(true);
			bomb2.setEsplosa(false);
			number=1;
			cd.removeSolidBlocks(bomb2.getSolida());
			cd.updateController(this);
		}
		
		if(finish && bomb1.getTimer()<=0 && bomb2.getTimer()<=0)
			cd.getPlayer().setAttack(new SingleBombAttack(cd.getPlayer(),cd));
	}
	
	/**ritorna il numero della bomba*/
	public int getNumber() {
		return number;
	}
	
	/**ritorna un booleano che indica se il mio attacco è ancora in corso o no*/
	public boolean canChange() {
		return finish;
	}
	
	@Override
	public void stop(){
		bomb1.setTimer(0);
		bomb2.setTimer(0);
	}
	

}
