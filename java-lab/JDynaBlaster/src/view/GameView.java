package view;


import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Observable;

import controller.Controller;
import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GameView extends Observable{
	private static Stage game;
	private GamePanel one;
	private static int larghezza,altezza;
	private int blocco;
	private static GameView gameView;
	private Scene current;
	private MenuView menu;
	private static boolean running;
	private boolean inGame,inMenu,inContinue,inWin,inGameOver;
	/**crea la finestra di gioco e il pannello di gioco*/
	private GameView() {
		
		larghezza=800;
		altezza=800;
		blocco=40;
		one=new GamePanel(blocco,larghezza,altezza);
		menu=new MenuView(larghezza,altezza);
		game=new Stage();
		game.setY(100);
		game.setX(550);
		game.show();
		inMenu=false;
		inGame=false;
		inContinue=false;
		inGameOver=false;
		inWin=false;
		game.setResizable(false);
		game.setOnCloseRequest(event->stop());
		running=true;
		
	}
	
	private void stopAll() {
		if(inMenu)
			menu();
		if(inWin)
			inWin=false;
		if(inContinue)
			inContinue=false;
		if(inGameOver)
			inGameOver=false;
		if(inGame)
			game();
	}
	
	/**chiude il lo stage di gioco*/
	public static void stop() {
		game.close();
	}
	
	/**ritorna la scena corrente*/
	public Scene getCurrentScene() {
		return current;
	}
	
	/**metodo che mi permette di avere una sola istanza della view*/
	public static GameView getInstance() {
		if(gameView==null)
			gameView=new GameView();
		return gameView;
		
	}
	/**restituisce il gamepanel*/
	public GamePanel getGamePanel() {
		return one;
	}
	
	/**imposta la scena del gamepanel come scena dello stage*/
	public void game() {
		AudioManager.getInstance().Play(AudioMusic.getInstance());
		AudioManager.getInstance().stop(AudioMusicMenu.getInstance());
		game.setScene(one.getScene());
		Controller.getInstance().cycle();
		Controller.getInstance().updateStato(3);	
	}
	
	/**imposta la scena del menu*/
	public void menu() {
		Controller.getInstance().updateStato(0);
		Controller.getInstance().stop();
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(Controller.getInstance().getStato()==0 ||Controller.getInstance().getStato()==1) {
			menu=new MenuView(larghezza, altezza);
			resetBanner();
			game.setScene(menu.getScene());
			menu.show();
			AudioManager.getInstance().stopAll();
			AudioManager.getInstance().Play(AudioMusicMenu.getInstance());
		}
	}
	/**restituisce il menu*/
	public MenuView getMenu(){
		return menu;
	}
	
	/**imposta la scena al restart del livello quando muori*/
	public void restart(){
		Controller.getInstance().stop();
		AudioManager.getInstance().stopAll();
		AudioManager.getInstance().Play(AudioLoose.getInstance());
		if(Controller.getInstance().getStato()!=0)
			game.setScene(RestartView.getInstance().getScene());
		resetBanner();
	}
	
	/**toglie tutte le stastiche a video e imposta la scritta "press start!"*/
	public void resetBanner() {
		one.drawLives(null);
		one.drawTimer(null);
		one.drawPoints(null);
	}
	
	/**imposta la scena del livello vinto */
	public void win(int i) {
		Controller.getInstance().stop();
		AudioManager.getInstance().stopAll();
		AudioManager.getInstance().Play(AudioMusicWin.getInstance());
		if(Controller.getInstance().getStato()!=0)
			one.getPlayer().winAnimation(i);
		resetBanner();
	}
	
	/**imposta la scena passata in input*/
	public void setScene(Scene in) {
		game.setScene(in);
	}
	
	/**imposta la scena del game over*/
	public void gameOver() {
		Controller.getInstance().stop();
		AudioManager.getInstance().stopAll();
		AudioManager.getInstance().Play(AudioLoose.getInstance());
		if(Controller.getInstance().getStato()!=0)
			game.setScene(GameOverView.getInstance().getScene());
		resetBanner();
	}
		
	
}
