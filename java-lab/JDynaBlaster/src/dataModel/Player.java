package dataModel;

import java.util.List;

import javafx.scene.image.Image;

public class Player extends Entity implements Cloneable, PlayerInterface{
	
	//strategy pattern
	private Movement<Player> movement;
	private CanAttack<Player> attack;
	private CollisionChecker checker;
	
	
	private GameModel cd;
	private AreaSolida solida;
	private boolean alive;
	private int vite=3;
	private FinalBlockChecker finalBlockChecker;
	private ItemChecker itemChecker;
	private boolean riscrivi;
	
	public Player(int x,int y,int larghezza,int altezza,boolean collide,String direzione,int blocco,GameModel cd) {
		super(x,y,larghezza,altezza,collide,direzione,blocco,cd);
		attack=new SingleBombAttack(this, cd);
		if(collide)
			checker=new CollisionCheckerPlayer();
		this.cd=cd;
		alive=true;
		riscrivi=false;
		solida=new AreaSolida(11,15);
		solida.setXY(x, y,5,16);
		finalBlockChecker=new FinalBlockChecker();
		movement=new MovementPlayer();
		itemChecker=new ItemChecker();
	}
	
	/**fa il get della bomba del giocatore*/
	public CanAttack<Player> getAttack() {
		return attack;
	}
	
	public void setVite(int i){
		vite=i;
		if(vite>0)
			alive=true;
	}
	
	/**ritorna la velocita del comportamento di movimento del giocatore*/
	public int getVelocita() {
		return movement.getSpeed();
	}
	
	public AreaSolida getPlayerSolid() {
		return solida;
		
	}
	
	/**permette di settare il metodo di attacco del player*/
	public void setAttack(CanAttack<Player> in) {
		attack=in;
	}
	
	public void setCollisioChecker(CollisionChecker<Player> in) {
		checker=in;
	}
	
	/**imposta il comportamento di movimento*/
	public void setMovement(Movement<Player> comportamento){
		movement=comportamento;
	}
	
	public Movement<Player> getMovement() {
		return movement;
	}
	
	public int getLives() {
		return vite;
	}
	
	/**il gicatore respawna, nella posizione di default, il timer viene azzerato, se le vite sono finite è game over*/
	public boolean revive() {
		
		boolean flag=false;
		
		if(vite>0) {
			
			riscrivi=false;
			
			cd.setDead(false);
			
			flag=true;
			
			solida.setXY(45, 120,5,16);
			
			this.setX(45);
			
			this.setY(120);
			
			this.setAlive();
			
			GameModel.Timer.setTimer(300);
			
		}
		
		return flag;
		
	}
	
	/**il giocatore muore, quindi viene scalata una vita dal salvataggio corrente e viene aggiornato il controller, il livello viene messo in pausa*/
	public void dead() {
		alive=false;
		if(!riscrivi) {
			riscrivi=true;
			vite--;
			cd.setLives(vite);
			if(vite<=0)
				cd.GameOver();
			else {
				cd.getCurrentSaves().decreaseLife();
				cd.getCurrentSaves().store();
				cd.setStato(6);
			}
		}
		cd.setDead(true);
		cd.updateController(this);
	}
	
	/**ritorna l attributo alive*/
	public boolean isAlive() {
		return alive;
	}
	
	/**imposta l attributo alive su true*/
	public void setAlive() {
		alive=true;
		riscrivi=false;
	}
	
	/**classe finalBlockChecker che controlla se il giocatore ha intersecato il blocco di fine livello*/
	public class FinalBlockChecker{
		public void check() {
			if(solida.interseca(cd.getFinal())) {
				List<Entity> s=cd.getEnemies();
				boolean flag=true;
				for(Entity i:s) {
					if(i instanceof Baloon && !((Baloon)i).isDead())
						flag=false;
				}
				if(flag)
					cd.win();
			}
				
		}
	}
	/**classe ItemChecker che controlla se il giocatore ha intersecato un item nel pannello*/
	public class ItemChecker{
		public ItemChecker() {}
		public void check() {
			if(cd.getSpeedItem().getAreaSolida().interseca(solida) && cd.getSpeedItem().getSpawned() && cd.getSpeedItem().getUsed() && movement instanceof MovementPlayer) {
				movement=new SuperMovementPlayer();
				cd.getSpeedItem().setSpawned(false);
				cd.updateController(cd.getSpeedItem());
				
			}
				
			if(cd.getSkullItem().getAreaSolida().interseca(solida)&& cd.getSkullItem().getSpawned() && cd.getSkullItem().getUsed() && attack instanceof SingleBombAttack && ((SingleBombAttack)attack).canChange()) {
				
				attack=new DoubleBombAttack(cd.getPlayer(),cd);
				cd.getSkullItem().setSpawned(false);
				cd.updateController(cd.getSkullItem());
				
			}
				
			if(cd.getFireItem().getAreaSolida().interseca(solida)&& cd.getFireItem().getSpawned() && cd.getFireItem().getUsed() && attack instanceof SingleBombAttack && ((SingleBombAttack)attack).canChange()) {
				
				attack=new FireAttackBomb(cd.getPlayer(), cd);
				cd.getFireItem().setSpawned(false);
				cd.updateController(cd.getFireItem());
				
			}

		}
	}
	
	/*muove il giocatore in una direzione precisa indicata nella stringa di direzione*/
	public void muoviPlayer(String direzione) {
		this.setDirezione(direzione);
		boolean flag=checker.collide(this, movement, cd, direzione);
		if(!flag) {
			movement.muovi(this, direzione);
			solida.setXY(this.getX(), this.getY(), 5, 16);
			itemChecker.check();
			finalBlockChecker.check();
		}
			
	}
	
			
	public Object clone() throws CloneNotSupportedException {
		//devo clonare soltanto la bomba per la sua posizione
		return super.clone();
			
	}

	@Override
	public void attack() {
		
		attack.attack();
			
	}
}

	

	
	

