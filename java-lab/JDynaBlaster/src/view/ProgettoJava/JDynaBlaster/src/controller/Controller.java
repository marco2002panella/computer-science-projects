package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import dataModel.AreaSolida;
import dataModel.Baloon;
import dataModel.Bomb;
import dataModel.CanAttack;
import dataModel.Entity;
import dataModel.FireItem;
import dataModel.GameModel;
import dataModel.Ghost;
import dataModel.Map;
import dataModel.Player;
import dataModel.Saves;
import dataModel.SkullItem;
import dataModel.Stats;
import dataModel.Status;
import dataModel.SuperSpeedItem;
import javafx.animation.AnimationTimer;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import view.GamePanel;
import view.GameView;
import view.RestartView;
import view.WinView;



public class Controller  implements Observer {
	
	private GameModel model;
	private GameView view;
	private static final int DELAY_MILLISECONDS = 0; 
    private long lastKeyPressTime = 0;
    private boolean up,down,left,right,space,enter,pause,esc;
    private static Controller controller;
    private boolean running;
    private boolean init;
    private Controller(GameModel model, GameView view) {
		this.model=model;
		this.view=view;
		model.addObserver(this);	
	}
    
    /**metodo che implementa il pattern singleton, restituisce il controller*/
    public static Controller getInstance() {
    	if(controller==null && GameModel.getInstance()!=null && GameView.getInstance()!=null)
    		controller=new Controller(GameModel.getInstance(),GameView.getInstance());
    	return controller;
    }

    
	/*Il metodo della classe che carica tutti i key handler di gioco e contiene il loop stoppabile attraverso il pressing della crocetta del Thread*/
	public void start(){
		stop();
		init=true;
		view.menu();
		init();
		new AnimationTimer() {
			public long time=0;
			@Override
			public void handle(long arg0){
				if(running){
					if(arg0-time>1000000000l/75){
						updateModel();
						time=arg0;	
					}	
				}else if(view.getMenu()!=null && model.getStato()==0){
					if(arg0-time>1000000000l/75){
						view.getMenu().updateMenu();
						time=arg0;
					}
				}
			}
		}.start();
	}
	
	/**stoppa il controller dal ricevere input da tastiera e modificare il modello*/
	public void stop(){
		running=false;
	}
	public void cycle(){
		running=true;
		init=false;
	}
	
	public void init(){
		//crea la mappa visiva
		//view.game();
		view.getGamePanel().createMap();
		//crea il playerVisivo
		view.getGamePanel().createPlayerView(model.getPlayer().getLarghezza(),model.getPlayer().getAltezza(),model.getPlayer().getX(),model.getPlayer().getY());
		//imposta i metodi di questa classe per la gestione degli eventi da tastiera durante una partita
		view.getGamePanel().getScene().setOnKeyPressed(event->handleKeyPressed(event));
		view.getGamePanel().getScene().setOnKeyReleased(event->handleKeyReleased(event));
		
	}
	
	/**metodo che imposta se un tasto viene premuto durante un livello*/
	private void handleKeyPressed(KeyEvent event) {
        KeyCode keyCode = event.getCode();
        if(keyCode==KeyCode.D) {
        	right=true;
        }if(keyCode==KeyCode.S) {
        	down=true;
        }
         if(keyCode==KeyCode.A) {
        	left=true;
        }
         if(keyCode==KeyCode.W) {
        	up=true;
         }
         if(keyCode==KeyCode.SPACE) {
         	space=true;
          }
         if(keyCode==KeyCode.ENTER)
        	 enter=true;
         if(keyCode==KeyCode.P)
        	 pause=true;
         if(keyCode==keyCode.ESCAPE)
         	esc=true;
    }
	
	/**metodo che imposta se un tasto viene rilasciato durante un livello*/
	private void handleKeyReleased(KeyEvent event){
		KeyCode keyCode = event.getCode();
		if(keyCode==KeyCode.D) {
        	right=false;
        }  if(keyCode==KeyCode.S) {
        	down=false;
        }
        if(keyCode==KeyCode.A) {
        	left=false;
        }
        if(keyCode==KeyCode.W) {
        	up=false;
         }
        if(keyCode==KeyCode.SPACE) {
          	space=false;
           }
        if(keyCode==KeyCode.ENTER)
        	 enter=false;
        if(keyCode==KeyCode.P)
        	pause=false;
        if(keyCode==keyCode.ESCAPE)
        	esc=false;
	}
	
	/**update method del modello alla pressione dei tasti*/
	public void updateModel() {
		//posizione del giocatore
			if(up) {
			model.muoviPlayer("su");
		}
		 if(down) {
			model.muoviPlayer("giu");
		}
		 if(right) {
			model.muoviPlayer("destra");
		}
		 if(left) {
			model.muoviPlayer("sinistra");
		}
		if(space){
			model.PiazzaBomba();
			space=false;
			
		}
		if(pause) {
			model.pauseLevel();
			view.resetBanner();
			pause=false;
		}
		if(enter) {
			model.startLevel();
			model.muoviPlayer("fermo");
			enter=false;
		}
		if(esc) {
			if(model.getStato()==1) {
				model.restart(0);
				stop();
				esc=false;
			}
			
		}
		model.update();
	}
	
	/**ritorna lo stato del modello*/
	public int getStato() {
		return model.getStato();
	}
	
	/**crea le view dei nemici*/
	public void createEnemiesView(List<Entity> in){
		view.getGamePanel().createEnemiesView(in);
	}
	
	/**update method della view dopo l'aggiornamento del modello attraverso Observer Observable pattern*/
	@Override
	public void update(Observable o, Object arg) {
		drawPlayer(arg);
		drawBomb(arg);
		drawSolids(arg);
		drawMap(arg);
		drawOnStatus(arg);
		drawItem(arg);
		drawStats(arg);
		drawEnemies(arg);
	}
	
	public void drawEnemies(Object arg) {
		if(arg instanceof Entity)
			view.getGamePanel().drawEnemie((Entity)arg);
	}
	
	/**disegna il timer*/
	public void drawStats(Object o) {
		if(o instanceof Stats) {
			Stats s=(Stats)o;
			view.getGamePanel().drawTimer((Integer)s.getTimer());
			view.getGamePanel().drawLives((Integer)s.getLives());
			view.getGamePanel().drawPoints((Integer)s.getPoints());
		}
			
	}
	
	/**cambia lo stato della view in base allo stato del modello*/
	private void drawOnStatus(Object arg) {
		if(arg instanceof Status) {
			arg=(Status)arg;
		if(((Status) arg).getStato()==0) {
			view.menu();
		}
		
		if(((Status) arg).getStato()==5) {
			view.win(1);
		}
		if(((Status) arg).getStato()==2) {
			view.win(0);
		}
		
		}	
	}
	
	/**ordina alla playerView di disegnare il player*/
	private void drawPlayer(Object arg){
		if(arg instanceof Player) {
			Player arg0=(Player)arg;
			view.getGamePanel().drawPlayer(arg0.getX(),arg0.getY(),arg0.getDirezione(),arg0.getVelocita(),model.getPlayer().isAlive(),arg0.getLives());
		}
	}
	
	
	
	/**ordina alla mapView disegna la mappa*/
	private void drawMap(Object arg) {
		if(arg instanceof Map){
			System.out.println("disegno la mappa");
			view.getGamePanel().drawMap(model.getMap().clone());
		}
	}
	
	/**se l'oggetto passato è un oggetto di tipo area solida si puo vedere visivamente la solidità degli elementi del modello, il metodo
	 * serve per testare visivamente la collisione degli oggetti solidi nel piano*/
	private void drawSolids(Object arg) {
		if(arg instanceof AreaSolida) {
			ArrayList<AreaSolida> a=new ArrayList<AreaSolida>();
			a.add((AreaSolida) arg);
			view.getGamePanel().drawSolidArea(a);
		}
		
	}
	
	/**ordina alla bombView di disegnare*/
	private void drawBomb(Object arg) {
		if(arg instanceof CanAttack) {
			CanAttack<Player> arg0=(CanAttack<Player>)arg;
			view.getGamePanel().drawBomb(arg0);
		}
	}
	
	/**disegna gli item*/
	public void drawItem(Object o){
		view.getGamePanel().drawItem(o);
	}
	
	/**aggiorna lo stato del modello*/
	public void updateStato(int i) {
		model.setStato(i);
	}
	
	/**prende i salvataggi dal SavesManager attualmente in uso sul modello*/
	public ArrayList<String> getSaves() {
		ArrayList<String> t=new ArrayList<String>();
		ArrayList<String> s=model.getSaves();
		for(String a:s){
			t.add((String)a+"");
		}
		return t;
	}
	
	/**crea un nuovo salvataggio nel modello*/
	public void newSave(String nick, int i) {
		model.newGame(nick, i);
	}
	
	/**prende una stringa in input e chiama al metodo load save del modello*/
	public void Charge(String text) {
		model.loadSave(text);
	}
	
}
