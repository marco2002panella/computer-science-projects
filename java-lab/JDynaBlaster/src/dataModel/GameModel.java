package dataModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.function.Function;

import controller.Controller;
import javafx.animation.AnimationTimer;
import javafx.beans.InvalidationListener;
import javafx.scene.image.Image;

public class GameModel extends Observable {
	
	private final int blocco;
	private int larghezzaPannello,altezzaPannello;
	
	private Player player;
	
	private Map[] maps;
	private int mappaCorrente;
	
	private SavesManager manager;
	private Saves salvataggioCorrente;
	
	/*informazioni relative allo stato del modello e del pannello di gioco*/
	private Status corrente;
	private boolean pause;
	private boolean dead;
	
	//blocchi solidi,entita,blocco finale del livello
	private List<AreaSolida> solidBlocks=new ArrayList<AreaSolida>();
	private LinkedList<Entity> enemies;
	private AreaSolida finalBlock;
	private FireItem fireItem;
	private SkullItem skullItem; 
	private SuperSpeedItem speedItem;
	private int xFinale;
	private int yFinale;
	private static GameModel game;
	private boolean finalB;
	
	//punteggio,timer,vite
	private Stats stats;
	private int punteggio;
	
	private GameModel(){
		//dimensione del mio pannello
		larghezzaPannello=800;
		altezzaPannello=800;
		blocco=40;
		
		//lo stato del mio gioco
		//6 ricomincia livello
		//5 prossimo livello
		//4 livello perso
		//3 livello da iniziare
		//2 livello vinto
		//1 livello iniziato
		//0 menu
		corrente=new Status(0);
		//creo due mappe
		maps=new Map[2];
		Map map1=new Map(larghezzaPannello,altezzaPannello-80,blocco,this,"/resources/maps/map1.txt");
		Map map2=new Map(larghezzaPannello,altezzaPannello-80,blocco,this,"/resources/maps/map2.txt");
		maps[0]=map1;
		maps[1]=map2;
		//creo gli item
		speedItem=new SuperSpeedItem(1/2d);
		skullItem=new SkullItem(1/3d);
		fireItem=new FireItem(1/4d);
		dead=false;
		//imposto il gioco in pausa
		pause=true;
		//creo il giocatore
		player=new Player(45,120,24,36,true,"destra",blocco,this);
		//creo le entita
		enemies=new LinkedList<>();
		//manager di salvataggi56
		manager=SavesManager.getInstance();
		//punteggio,vite,tempo rimasto
		stats=new Stats();
		punteggio=0;
		initLevel();
		//finito
	}
	
	/**ritorna il salvataggio corrente*/
	public Saves getCurrentSaves() {
		return salvataggioCorrente;
	}
	
	/**crea un nuovo salvataggio e lo imposta come corrente*/
	public void newGame(String name,int color) {
		dead=false;
		initLevel();
		manager.addSaves(name, color);
		salvataggioCorrente=manager.getLastInsert();
		mappaCorrente=0;
		setStato(3);
	}
	
	/**imposta lo stato*/
	public void setStato(int i) {
		corrente.setStato(i);
		if(i==0) {
			pause=true;
			player.getAttack().stop();
			updateController(player.getAttack());
		}
		
		if(i==3){
			maps[0].loadMatrice("/resources/maps/map1.txt");
			maps[1].loadMatrice("/resources/maps/map2.txt");
			player.setX(45);
			player.setY(110);
			player.setVite(salvataggioCorrente.getVite());
			player.setAlive();
			player.setDirezione("giu");
			pause=true;
			dead=false;
			finalB=false;
			setMappaCorrente(1);
			updateController(player);
			salvataggioCorrente.store();
			initLevel();
		}
			
	}
	
	/**imposta il timer del livello e reimposta gli item a inutilizzati*/
	public void initLevel(){
		Timer.setTimer(300);
		player.setAttack(new SingleBombAttack(player,this));
		player.setMovement(new MovementPlayer());
		speedItem.despawn();
		skullItem.despawn();
		fireItem.despawn();
		punteggio=0;
		updateController(speedItem);
		updateController(fireItem);
		updateController(skullItem);
		updateController(player.getAttack());
	}
	
	/**ritorna l istanza di GameModel*/
	public static GameModel getInstance() {
		if(game==null) {
			game=new GameModel();
		}
		
		return game;
	}
	
	/**ritorna l area del blocco finale del livello*/
	public AreaSolida getFinal() {
		return finalBlock;
	}
	
	/**ritorna un lista che rappresenta i salvataggi*/
	public ArrayList<String> getSaves(){
		return manager.getInstance().getSaves();
	}
	
	/**ritorna una lista che rappresenta i blocchi solidi */
	public ArrayList<AreaSolida> getSolids(){
		return (ArrayList<AreaSolida>) solidBlocks;
	}
	
	/**carica il salvataggio selezionato con le informazioni correnti attraverso il metodo statico getSave che
	 * permette di trovare un salvataggio da una stringa in input che è di formato salvataggio*/
	public void loadSave(String save){
		initLevel();
		dead=false;
		salvataggioCorrente=Saves.getSave(save);
		setStato(3);	
	}
	
	/**imposta le vite rimaste nelle statistiche*/
	public void setLives(int i) {
		stats.setLives(i);
	}
	
	/**imposta il punteggio nelle statistiche*/
	public void setPoints(int i) {
		punteggio=i;
	}
	
	/**ritorna il punteggio nelle statistiche*/
	public int getPoints(){
		return punteggio;
	}
	
	/**reinizializza tutte le impostazioni di default e aggiunge 1 alle partite perse*/
	public void GameOver() {
		salvataggioCorrente.increaseGameOver();
		salvataggioCorrente.setLivello(0);
		salvataggioCorrente.setVite(3);
		salvataggioCorrente.store();
	}
	
	/**imposta lo stato in vincita e aggiorna il controller che lo stato è quello di vittoria*/
	public void win() {
		if(!finalB) {
			finalB=true;
			if(mappaCorrente==0) {
				salvataggioCorrente.setLivello(1);
				setStato(5);
			}	
			else {
				salvataggioCorrente.increaseWin();
				salvataggioCorrente.setLivello(0);
				setStato(2);
			}
			salvataggioCorrente.store();
			updateController(corrente);
		}
	}
	
	/**reinizializza la posizione iniziale del playe rimposta lo stato sullo stato richiesto carica il 
	 * salvataggio in memoria e aggiorna il controller*/
	public void restart(int i){
		corrente.setStato(i);
		player.setX(45);
		player.setAlive();
		player.setY(110);
		player.getAttack().stop();
		salvataggioCorrente.store();
		updateController(corrente);
	}
	
	/**imposta la mappa corrente*/
	public void setLevel(int i) {
		setMappaCorrente(i);
		
	}
	
	public List<Entity> getEnemies(){
		return enemies;
	}
	
	/*crea oggetti solidi della mappa*/
	private void createSolids(Map map){
		
		if(solidBlocks.size()!=0)
			solidBlocks.removeAll(solidBlocks);
		
		int i,j;
		int[][] m=map.getMatrix();
		i=0;
		int l=0;
		int sp=0;
		while(i<m.length) {
			j=0;
			while(j<m[0].length) {
				if(m[i][j]==0){
					boolean spawn=Math.random()<0.2d;
					 if(l<10 && spawn && i>2){
							Entity a=enemies.get(l);
							l++;
							if(a.getClass().equals(Baloon.class)) {
								((Baloon)a).getBaloonSolid().setXY(j*40, i*40+80, 5, 5);
								((Baloon)a).setX(j*40+5);
								((Baloon)a).setY(i*40+80+5);
							}
							if(a.getClass().equals(Ghost.class)) {
								System.out.println("ghost");
								((Ghost)a).getGhostSolid().setXY(j*40, i*40+80, 5, 5);
								((Ghost)a).setX(j*40+5);
								((Ghost)a).setY(i*40+80+5);
							}
						}
					}
				if(m[i][j]==1 || m[i][j]==2) {
					
					AreaSolida a=new AreaSolida(blocco,blocco);
					solidBlocks.add(a);
					a.setXY(j*blocco,i*blocco+80,0,0);
					
				}
				else if(m[i][j]==6) {
					AreaSolida a,b;
					a=new AreaSolida(blocco,10);
					b=new AreaSolida(10,blocco);
					solidBlocks.add(b);
					solidBlocks.add(a);
					a.setXY(0,80,0,0);
					b.setXY(0,80,0,0);
				}
				else if(m[i][j]==7) {
					AreaSolida a=new AreaSolida(blocco,10);	
					solidBlocks.add(a);
					a.setXY(j*blocco,i*blocco+80,0,0);
					}
				else if(m[i][j]==8) {
					AreaSolida a,b;
					a=new AreaSolida(blocco,10);
					b=new AreaSolida(10,blocco);
					a.setXY(j*blocco,i*blocco+80,0,0);
					b.setXY(j*blocco+30,i*blocco+80,0,0);
					solidBlocks.add(b);
					solidBlocks.add(a);
					}
				else if(m[i][j]==9) {	
					AreaSolida a=new AreaSolida(blocco,blocco);
					solidBlocks.add(a);
					a.setXY(j*blocco+30,i*blocco+80,0,0);
					}
				else if(m[i][j]==10) {
					AreaSolida a,b;
					a=new AreaSolida(blocco,10);
					b=new AreaSolida(10,blocco);
					a.setXY(j*blocco,i*blocco+30+80,0,0);
					b.setXY(j*blocco+30,i*blocco+80,0,0);
					solidBlocks.add(b);
					solidBlocks.add(a);
				}
				else if(m[i][j]==11) {
					AreaSolida a=new AreaSolida(blocco,blocco);
					a.setXY(j*blocco,i*blocco+30+80,0,0);
					solidBlocks.add(a);
				}
				else if(m[i][j]==12) {
					AreaSolida a,b;
					a=new AreaSolida(blocco,10);
					b=new AreaSolida(10,blocco);
					solidBlocks.add(b);
					solidBlocks.add(a);
					a.setXY(j*blocco,i*blocco+30+80,0,0);
					b.setXY(j*blocco,i*blocco+80,0,0);
				}
				else if(m[i][j]==13) {
					AreaSolida a=new AreaSolida(10,blocco);
					solidBlocks.add(a);
					a.setXY(j*blocco,i*blocco+80,0,0);
				}			
				else if(m[i][j]==4) {
					AreaSolida a=new AreaSolida(blocco,blocco);
					solidBlocks.add(a);
					yFinale=i*blocco+80;
					xFinale=j*blocco;
					finalBlock=new AreaSolida(3,3);
					a.setXY(xFinale,yFinale,0,0);
					finalBlock.setXY(xFinale,yFinale,21,21);
				}
				
				j++;
			}
			i++;
		}
		
		updateController(map);
	}

	
	/**aggiunge un blocco solido*/
	public void addSolidBlocks(AreaSolida in){
		solidBlocks.add(in);
	}
	
	/**ritorna il fireItem*/
	public Item getFireItem() {
		return fireItem;
	}
	/**ritorna lo skullItem*/
	public Item getSkullItem() {
		return skullItem;
	}
	/**ritorna lo speedItem*/
	public Item getSpeedItem() {
		return speedItem;
	}
	
	/**rimuove un blocco solido*/
	public void removeSolidBlocks(AreaSolida in) {
		if(in!=null && corrente.getStato()==1) {
			solidBlocks.remove(in);
		}
			
		
	}
	/**ritorna la dimensione del blocco di un pannello*/
	public int getBlocco() {
		return blocco;
	}
	/**ritorna la mappa corrente*/
	public Map getMap() {
		return maps[mappaCorrente];
		
	}
	
	public int getMappaCorrente() {
		return mappaCorrente;
	}
	
	/**imposta la mappa corrente e ne crea i blocchi solidi*/
	public void setMappaCorrente(int i) {
		if(i>=0 && i<=1) {
			mappaCorrente=i;
			solidBlocks.removeAll(solidBlocks);
			createEnemies(i);
			createSolids(maps[mappaCorrente]);
			updateController(maps[mappaCorrente]);
		}
			
	}
	
	private void createEnemies(int i) {
		
		if(enemies.size()!=0)
			enemies.removeAll(enemies);
		
		if(i==0) {
			for(int s=0;s<10;s++) {
				Baloon t=new Baloon(0, 0, 24, 36, true, "giu", blocco, this,s);
				enemies.add(s, t);
			}
		}else if(i==1){
			for(int s=0;s<10;s++) {
				Ghost t=new Ghost(0, 0, 24, 36, true, "giu", blocco, this,s);
				enemies.add(s,t);
			}
		}
		
		Controller.getInstance().createEnemiesView(enemies);
	}
	
	/**ritorna il player*/
	public Player getPlayer() {
		return player;
		
	}
	/**ritorna la larghezza del pannello*/
	public int getLarghezza() {
		return larghezzaPannello;
	}
	/**ritorna l'altezza del pannello*/
	public int getAltezza() {
		return altezzaPannello;
	}
	
	/**ritorna lo stato corrente*/
	public int getStato() {
		return corrente.getStato();
	}
	
	
	/**aggiorna le condizioni della bomba e delle entita nemiche*/
	public void update(){	
		if((!pause && corrente.getStato()==1) || corrente.getStato()==6){
			updateTimer(System.nanoTime());
			player.getAttack().update();
			stats.setPoints(punteggio);
			for(Entity i:enemies) {
				if(i instanceof Baloon) {
					((Baloon)i).update();
				}else if(i instanceof Ghost) {
					((Ghost)i).update();
				}
			}
		}
	}
	
	/**aggiorna del timer*/
	public void updateTimer(long thisTime) {
		if(thisTime-Timer.lastTime>1000000000l) {
			Timer.decrease();
			stats.setTimer(Timer.getTimer());
			stats.setLives(player.getLives());
		}
	}
	
	/**il modello prima disegna tutto poi disegna soltanto ciò che è cambiato per migliorare il rendering*/
	public void updateController(Object in){
			setChanged();
			super.notifyObservers(in);
			clearChanged();
		
	}
	
	/**classe statica Timer, implementa runnable, non ha costruttore*/
	public static class Timer{
		
		private  static int timer;
		
		private static Timer singleton;
		
		private static long lastTime;
		
		public static  void setTimer(int i) {
			timer=i;
			lastTime=System.nanoTime();
		}
		
		private Timer() {
		}
		
		public static Timer getInstance(){
			if(singleton==null)
				singleton=new Timer();
			return singleton;
			
		}
		
		public  static int getTimer() {
			return timer;
		}
		
		private static void decrease() {
			timer--;
			lastTime=System.nanoTime();
		}
		
		public  static boolean isZero() {
			return timer==0;
		}
	
	}
	
	public void setDead(boolean i){
		dead=i;			
	}
	
	/**fa iniziare il livello
	 * @param
	 * @return */
	public void startLevel() {
		String r="";
		if(corrente.getStato()==3) {
			corrente.setStato(1);
		}
		
		if(corrente.getStato()==1 && player.isAlive()) {
			pause=false;
		}
			
		if(!player.isAlive()) {
			boolean flag=player.revive();
			if(flag)
				updateController(player);
			
		}
			
		
	}
	
	/**mette in pausa il livello
	 * @param
	 * @return*/
	public void pauseLevel(){
		if(corrente.getStato()==1) {
			pause=true;
		}
			
	}

	/**notifico il controller che deve aggiornare la view passando come parametro una copia dello stato del player
	 * @param direction:stringa che indica la direzione 
	 * @return */
	public void muoviPlayer(String direction){
		if(!pause && corrente.getStato()==1 && !dead) {
			player.muoviPlayer(direction);
			updateController(getPlayer());
		}
	}
	
	/**notifica al controller che è stato settato un timer della bomba quindi deve essere messa in video
	 * @param
	 * @return*/
	public void PiazzaBomba(){
		if(!pause && corrente.getStato()==1 && !dead){
			 player.attack();
		}
		
	}



}
