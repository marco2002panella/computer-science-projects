package dataModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javafx.scene.image.Image;

public class Bomb implements Cloneable,Explosive,Timer{
	
	private Image image1;
	private Image esplosione;
	private int x,y;
	private final boolean collide=true;
	private int timer;
	private boolean stop;
	private boolean esplosa;
	private boolean piazzabile;
	private boolean colpito;
	private GameModel cd;
	private int counter=0;
	private AreaSolida areaSolida;
	private boolean collision;
	private CollisionBomb exColl;
	private AreaSolida x1,x2,y1,y2;
	private int number;
	
	public Bomb(int x, int y,int larghezza, int altezza,GameModel cd){
		this.x=x+larghezza/2+2;
		this.y=y+altezza/2;
		this.timer=0;
		this.cd=cd;
		piazzabile=true;
		esplosa=false;
		collision=false;
		colpito=false;
		number=0;
		areaSolida=new AreaSolida(40,40);
		y1=new AreaSolida(10,40);
		x1=new AreaSolida(40,10);
		x2=new AreaSolida(40,10);
		y2=new AreaSolida(10,40);
		exColl=new CollisionBomb();
	}
	
	public void setEsplosa(boolean flag) {
		esplosa=flag;
	}

	public void setPiazzabile(boolean flag) {
		piazzabile=flag;
	}
	
	/**imposta la collisione*/
	public void setCollision(boolean flag) {
		collision=flag;
	}
	
	/**ritorna la collisione*/
	public boolean getCollision() {
		return collision;
	}
	
	/**ritorna il timer della bomba*/
	public int getTimer() {
		return timer;
	}
	
	/**imposta il numero della bomba*/
	public void setNumber(int n) {
		number=n;
	}
	/**ritorna il numero della bomba*/
	public int getNumber() {
		return number;
	}
	/**ritorna l'area di collisione della bomba*/
	public AreaSolida getSolida(){
		return areaSolida;
	}
	/**imposta l'area solida dell'esplosione a orizzontale a sinistra*/
	public void setAreaSolidax1(AreaSolida x1) {
		this.x1=x1;
	}
	/**imposta l'area solida dell'esplosione a orizzontale a destra*/
	public void setAreaSolidax2(AreaSolida x2) {
		this.x2=x2;
	}
	/**imposta l'area solida dell'esplosione a orizzontale sopra*/
	public void setAreaSoliday1(AreaSolida y1) {
		this.y1=y1;
	}
	/**imposta l'area solida dell'esplosione a orizzontale sotto*/
	public void setAreaSoliday2(AreaSolida y2) {
		this.y2=y2;
	}
	
	/**imposta l'area di collisione della bomba*/
	public void setAreaSolida(AreaSolida in){
		areaSolida=in;
	}
	
	/**imposta la posizione della bomba e la posizione delle aree solide:bomba e esplosioni
	 * @param x
	 * @param y
	 * @return void*/
	public void setPos(int x,int y){
		this.x=x;
		this.y=y;
		x1.setXY(x,y,-x1.getLatoX(),2);
		x2.setXY(x,y,2,2);
		y1.setXY(x,y,2,-y1.getLatoY());
		y2.setXY(x,y,2,2);
		areaSolida.setXY(x,y,-13,-13);
		collision=true;
	}
	
	/**ritorna la posizione x della bomba*/
	public int getPosX(){
		return x;
	}
	
	/**ritorna la posizione y della bomba*/
	public int getPosY(){
		return y;
	}
	/**fa il clone degli attributi della bomba*/
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
		
	}
	
	/**classe inner collisionBomb che implementa il controllo della collisione della esplosione*/
	public class CollisionBomb{
		
		private int counter1=0;
		private int counter2=0;
		
		private  CollisionBomb() {
		}
		
		boolean su=false,giu=false,destra=false,sinistra=false;
		
		/**controlla se l esploszione collide con i blocchi*/
		public void checkCollisionBlocks(){
			
			int m[][]=cd.getMap().getMatrix();
			
			int row,col;
			
			ArrayList<AreaSolida> solids=cd.getSolids();
			
			boolean interseca1=false;
			boolean interseca2=false;
			boolean interseca3=false;
			boolean interseca4=false;
			int i=0;
			
			while(i<solids.size()) {
		
				AreaSolida s=solids.get(i);
				
				row=cd.getMap().checkRow(s.getXY().getY()-80);
				
				col=cd.getMap().checkCol(s.getXY().getX());
				
				if(x1.interseca(s)) {
					interseca1=true;
					sinistra=true;
					if(m[row][col]==2||m[row][col]==4) {
						cd.removeSolidBlocks(s);
					}
					cd.getMap().changeRC(row, col);
				}
				
				if(x2.interseca(s)) {
					interseca2=true;
					destra=true;
					if(m[row][col]==2||m[row][col]==4) {
						cd.removeSolidBlocks(s);
					}
					cd.getMap().changeRC(row, col);
				}
				
				if(y1.interseca(s)) {
					interseca3=true;
					su=true;
					if(m[row][col]==2||m[row][col]==4) {
						cd.removeSolidBlocks(s);
					}
					cd.getMap().changeRC(row, col);
				}
				
				
				if(y2.interseca(s)) {
					interseca4=true;
					giu=true;
					if(m[row][col]==2||m[row][col]==4) {
						cd.removeSolidBlocks(s);
					}
					cd.getMap().changeRC(row, col);
				}
						
				i++;
			}
			
			i=0;
			
			x1.setXY(x,y,-x1.getLatoX()*2+5,2);
			x2.setXY(x,y,x1.getLatoX()+5,2);
			y1.setXY(x,y,2,-y1.getLatoY()*2+5);
			y2.setXY(x,y,2,y2.getLatoY()+5);
			while(i<solids.size() ) {
				
				AreaSolida s=solids.get(i);
				
				row=cd.getMap().checkRow(s.getXY().getY()-80);
				
				col=cd.getMap().checkCol(s.getXY().getX());
				
				if(x1.interseca(s) && !interseca1) {
					
					if(m[row][col]==2||m[row][col]==4) {
						cd.removeSolidBlocks(s);
					}
					cd.getMap().changeRC(row, col);
				}
				
				if(x2.interseca(s)&& !interseca2) {
					
					if(m[row][col]==2||m[row][col]==4) {
						cd.removeSolidBlocks(s);
					}
					cd.getMap().changeRC(row, col);
				}
				
				if(y1.interseca(s)&& !interseca3) {
					
					if(m[row][col]==2||m[row][col]==4) {
						cd.removeSolidBlocks(s);
					}
					cd.getMap().changeRC(row, col);
				}
				
				
				if(y2.interseca(s)&& !interseca4) {
					
					if(m[row][col]==2 ||m[row][col]==4) {
						cd.removeSolidBlocks(s);
					}
					cd.getMap().changeRC(row, col);
				}
				i++;
			}
			if(giu) {
				y2.setXY(x,y,2,2);
			}
			if(su) {
				y1.setXY(x,y,2,-y1.getLatoY());
		
			}
			if(destra) {
				x2.setXY(x,y,2,2);
			}
			if(sinistra) {
				x1.setXY(x,y,-x1.getLatoX(),2);
			}
			giu=false;
			su=false;
			destra=false;
			sinistra=false;
			}
		
		/**metodo che controlla se l esplosione collide con il giocatore*/
		public void checkCollisionPlayer() {
			if(x1.interseca(cd.getPlayer().getPlayerSolid()) || x2.interseca(cd.getPlayer().getPlayerSolid())|| x1.interseca(cd.getPlayer().getPlayerSolid()) 
					|| y1.interseca(cd.getPlayer().getPlayerSolid()) || y2.interseca(cd.getPlayer().getPlayerSolid()) || areaSolida.interseca(cd.getPlayer().getPlayerSolid()) ) {
				cd.getPlayer().dead();
			}
				
		}
		
		/**metodo che controlla se l esplosione collide con gli enemies*/
		public void checkCollisionEnemies() {
			List<Entity >s=cd.getEnemies();
			int i=0;
			int n=s.size();
			while(i<n) {
				Entity t=s.get(i);
				if(t instanceof Baloon && ((x2.interseca(((Baloon)t).getBaloonSolid())|| x1.interseca(((Baloon)t).getBaloonSolid()) 
						|| y1.interseca(((Baloon)t).getBaloonSolid()) || y2.interseca(((Baloon)t).getBaloonSolid()) || areaSolida.interseca(((Baloon)t).getBaloonSolid())))){
					((Baloon)t).dead();
				}
				if(t instanceof Ghost && ((x2.interseca(((Ghost)t).getGhostSolid())|| x1.interseca(((Ghost)t).getGhostSolid()) 
						|| y1.interseca(((Ghost)t).getGhostSolid()) || y2.interseca(((Ghost)t).getGhostSolid()) || areaSolida.interseca(((Ghost)t).getGhostSolid())))) {
					((Ghost)t).dead();
				}
				i++;
				}	
			}
		}
				
	
	/**controlla la collisione con i nemici*/
	public void checkCollisionEnemies() {
		exColl.checkCollisionEnemies();
	}
	
	/**controlla la collisione con il giocatore*/
	public void checkCollisionPlayer() {
		exColl.checkCollisionPlayer();
	}
	/**controlla la collisione con i blocchi*/
	public void checkCollisionBlocks() {
		exColl.checkCollisionBlocks();
	}
	
	/**decrementa il timer*/
	@Override
	public void decrease() {
		timer-=1;	
	}

	/**imposta il timer*/
	@Override
	public void setTimer(int i) {
		this.timer=i;
		piazzabile=false;
		esplosa=false;
		colpito=false;
	}
	/**controlla se il timer è zero*/
	@Override
	public boolean isZero() {
		return timer==0;
	}
	
	/**la bomba esplode e aggiorna il controller*/
	@Override
	public void explode(){
		esplosa=true;
	}
	/**ritorna se la bomba è piazzabile*/
	public boolean isPiazzable(){
		return piazzabile;
	}
	/**ritorna se la bomba è esplosa*/
	public boolean isExplosed() {
		return esplosa;
	}
	
}


	

