package view;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import controller.Controller;
import javafx.animation.AnimationTimer;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class PlayerView extends EntityView{
	
	private final TranslateTransition animation=new TranslateTransition(Duration.seconds(0.0004),this);
	private long lastRender=0;
	private long updateRate=1000000000L/50;
	private int imagecounter=0;
	private int imagenum=0;
	
	public PlayerView(int larghezza,int altezza, int x, int y,GamePanel input) {
		super(larghezza,altezza, x, y,input);
		imagecounter=0;
		loadImagesWhite();
	}
	
	/**input: stringa direzione
	 * imposta l'immagine per l'animazione di movimento del player
	 * output:void
	 * */
	private void walkAnimation(String direction) {
		int image=0;
		switch(direction) {
			case "su": 
				this.setImage(imagenum);
				image=imagenum;
				break;
			case "giu": 
				setImage(imagenum+3);
				image=imagenum+3;
				break;
			case "sinistra":
				setImage(imagenum+6);
				image=imagenum+6;
				break;
			case "destra":
				setImage(imagenum+9);
				image=imagenum+9;
				break;		
		}
		if(imagecounter<10) {
			imagenum=0;
		}else if(imagecounter>10 && imagecounter<20) {
			imagenum=1;
		}else if (imagecounter>20 && imagecounter<30) {
			imagenum=0;
		}else if(imagecounter>30 && imagecounter<40) {
			imagenum=2;
		}else if(imagecounter>40)
			imagecounter=0;
		imagecounter++;	
	}
	
	/**crea una animazione per la vittoria del giocatore*/
	public void winAnimation(int i){
		new AnimationTimer(){
			int counter=1;
			long last=0;
			@Override
			public void handle(long arg0) {
				if(arg0-last>100000000l) {
					switch(counter) {
					case 1: setImage(0);counter++;break;
					case 2: setImage(3);counter++;break;
					case 3: setImage(6);counter++;break;
					case 4:	setImage(9);counter++;break;
					case 5: setImage(0);counter++;break;
					case 6: setImage(3);counter++;break;
					case 7: setImage(6);counter++;break;
					case 8: setImage(9);counter++;break;
					case 9: setImage(0);counter++;break;
					case 10: setImage(3);counter++;break;
					case 11: setImage(6);counter++;break;
					case 12:setImage(9);counter++;break;
					case 13: setImage(0);counter++;break;
					case 14: GameView.getInstance().setScene(WinView.getInstance().getScene(i)); counter++; break;
					case 15:stop();
					}
					last=arg0;
				}
			}
		}.start();
		
		
	}
	
	/**sposta il giocatore a cordinate x,y e imposta l'animazione alla direzione corrente*/
	public void translate(int x, int y,String direction,int speed,boolean alive,int lives) {
		
		if(alive) {
			walkAnimation(direction);
			this.setLayoutX(x);
			this.setLayoutY(y);	
			
		}else {
			deadAnimation(lives);
			this.setLayoutX(x);
			this.setLayoutY(y);
			
		}		
		
	}
	
	public void setcolor(int i){
		if(i==0) {
			loadImagesWhite();
		}
		if(i==1) {
			loadImagesRed();
		}
		if(i==2) {
			loadImagesBlack();
		}
		
	}

	
	/**crea una animazione per la morte del giocatore*/
	private void deadAnimation(int lives) {
		new AnimationTimer() {
			long lastTime=0;
			int counter=0;
			@Override
			public void handle(long arg0) {
				
				switch(counter) {
				
					case 0: setImage(12);
						break;
					case 1: setImage(13);
						break;
					case 2: setImage(14);
						break;
					case 3: setImage(15);
						break;
					case 8:
						if(lives>0) {
						GameView.getInstance().restart();
						Controller.getInstance().updateStato(6);
					}else {
						GameView.getInstance().gameOver();
						Controller.getInstance().updateStato(4);
					}
						
				}
				
				if(arg0-lastTime>=100000000){
					lastTime=arg0;
					counter++;
					
				if(counter==9)
					stop();
				}
				
			}
			
		}.start();
		
	}
	
	
	public void loadImagesRed() {
		down = new Image(getClass().getResourceAsStream("/resources/rb/down1.png"));
		down1 = new Image(getClass().getResourceAsStream("/resources/rb/down0.png"));
        down2 = new Image(getClass().getResourceAsStream("/resources/rb/down2.png"));
        up = new Image(getClass().getResourceAsStream("/resources/rb/up0.png"));
        up1 = new Image(getClass().getResourceAsStream("/resources/rb/up1.png"));
        up2 = new Image(getClass().getResourceAsStream("/resources/rb/up2.png"));
        left = new Image(getClass().getResourceAsStream("/resources/rb/left0.png"));
        left1 = new Image(getClass().getResourceAsStream("/resources/rb/left1.png"));
        left2 = new Image(getClass().getResourceAsStream("/resources/rb/left2.png"));
        right = new Image(getClass().getResourceAsStream("/resources/rb/right0.png"));
        right1 = new Image(getClass().getResourceAsStream("/resources/rb/right1.png"));
        right2= new Image(getClass().getResourceAsStream("/resources/rb/right2.png"));
        dead1 = new Image(getClass().getResourceAsStream("/resources/rb/dead1.png"));
        dead2 = new Image(getClass().getResourceAsStream("/resources/rb/dead2.png"));
        dead3 = new Image(getClass().getResourceAsStream("/resources/rb/dead3.png"));
        dead4 = new Image(getClass().getResourceAsStream("/resources/rb/dead4.png"));
        this.setImage(4);
	}
	public void loadImagesWhite() {
		down = new Image(getClass().getResourceAsStream("/resources/wb/wbdown1.png"));
		down1 = new Image(getClass().getResourceAsStream("/resources/wb/wbdown0.png"));
        down2 = new Image(getClass().getResourceAsStream("/resources/wb/wbdown2.png"));
        up = new Image(getClass().getResourceAsStream("/resources/wb/wbup0.png"));
        up1 = new Image(getClass().getResourceAsStream("/resources/wb/wbup1.png"));
        up2 = new Image(getClass().getResourceAsStream("/resources/wb/wbup2.png"));
        left = new Image(getClass().getResourceAsStream("/resources/wb/wbleft0.png"));
        left1 = new Image(getClass().getResourceAsStream("/resources/wb/wbleft1.png"));
        left2 = new Image(getClass().getResourceAsStream("/resources/wb/wbleft2.png"));
        right = new Image(getClass().getResourceAsStream("/resources/wb/wbright0.png"));
        right1 = new Image(getClass().getResourceAsStream("/resources/wb/wbright1.png"));
        right2= new Image(getClass().getResourceAsStream("/resources/wb/wbright2.png"));
        dead1 = new Image(getClass().getResourceAsStream("/resources/wb/dead1.png"));
        dead2 = new Image(getClass().getResourceAsStream("/resources/wb/dead2.png"));
        dead3 = new Image(getClass().getResourceAsStream("/resources/wb/dead3.png"));
        dead4 = new Image(getClass().getResourceAsStream("/resources/wb/dead4.png"));
        this.setImage(4);
        System.out.println("immagini caricate");
	}
	public void loadImagesBlack(){
		/*try {
			down = loadImageFromFile("file:///C:/Users/david/eclipse-workspace/JDynaBlaster/src/dataModel/playerSprites/RedBomber/BlackBomber/bbdown1.png");
			down1 = loadImageFromFile("file:///C:/Users/david/eclipse-workspace/JDynaBlaster/src/dataModel/playerSprites/RedBomber/BlackBomber/bbdown0.png");
	        down2 = loadImageFromFile("file:///C:/Users/david/eclipse-workspace/JDynaBlaster/src/dataModel/playerSprites/RedBomber/BlackBomber/bbdown2.png");
	        up = loadImageFromFile("file:///C:/Users/david/eclipse-workspace/JDynaBlaster/src/dataModel/playerSprites/RedBomber/BlackBomber/bbup0.png");
	        up1 = loadImageFromFile("file:///C:/Users/david/eclipse-workspace/JDynaBlaster/src/dataModel/playerSprites/RedBomber/BlackBomber/bbup1.png");
	        up2 = loadImageFromFile("file:///C:/Users/david/eclipse-workspace/JDynaBlaster/src/dataModel/playerSprites/RedBomber/BlackBomber/bbup2.png");
	        left = loadImageFromFile("file:///C:/Users/david/eclipse-workspace/JDynaBlaster/src/dataModel/playerSprites/RedBomber/BlackBomber/bbleft0.png");
	        left1 = loadImageFromFile("file:///C:/Users/david/eclipse-workspace/JDynaBlaster/src/dataModel/playerSprites/RedBomber/BlackBomber/bbleft1.png");
	        left2 = loadImageFromFile("file:///C:/Users/david/eclipse-workspace/JDynaBlaster/src/dataModel/playerSprites/RedBomber/BlackBomber/bbleft2.png");
	        right = loadImageFromFile("file:///C:/Users/david/eclipse-workspace/JDynaBlaster/src/dataModel/playerSprites/RedBomber/BlackBomber/bbright0.png");
	        right1 = loadImageFromFile("file:///C:/Users/david/eclipse-workspace/JDynaBlaster/src/dataModel/playerSprites/RedBomber/BlackBomber/bbright1.png");
	        right2 = loadImageFromFile("file:///C:/Users/david/eclipse-workspace/JDynaBlaster/src/dataModel/playerSprites/RedBomber/BlackBomber/bbright2.png");
	        dead1 = loadImageFromFile("file:///C:/Users/david/eclipse-workspace/JDynaBlaster/src/dataModel/playerSprites/RedBomber/BlackBomber/bbdea1.png");
	        dead2 = loadImageFromFile("file:///C:/Users/david/eclipse-workspace/JDynaBlaster/src/dataModel/playerSprites/RedBomber/BlackBomber/bbdead2.png");
	        dead3 = loadImageFromFile("file:///C:/Users/david/eclipse-workspace/JDynaBlaster/src/dataModel/playerSprites/RedBomber/BlackBomber/bbdead3.png");
	        dead4 = loadImageFromFile("file:///C:/Users/david/eclipse-workspace/JDynaBlaster/src/dataModel/playerSprites/RedBomber/BlackBomber/bbdead4.png");
			}catch(IOException e) {
				e.printStackTrace();
			}*/
		down = new Image(getClass().getResourceAsStream("/resources/bb/bbdown0.png"));
		down1 = new Image(getClass().getResourceAsStream("/resources/bb/bbdown1.png"));
        down2 = new Image(getClass().getResourceAsStream("/resources/bb/bbdown2.png"));
        up = new Image(getClass().getResourceAsStream("/resources/bb/bbup0.png"));
        up1 = new Image(getClass().getResourceAsStream("/resources/bb/bbup1.png"));
        up2 = new Image(getClass().getResourceAsStream("/resources/bb/bbup2.png"));
        left = new Image(getClass().getResourceAsStream("/resources/bb/bbleft0.png"));
        left1 = new Image(getClass().getResourceAsStream("/resources/bb/bbleft1.png"));
        left2 = new Image(getClass().getResourceAsStream("/resources/bb/bbleft2.png"));
        right = new Image(getClass().getResourceAsStream("/resources/bb/bbright0.png"));
        right1 = new Image(getClass().getResourceAsStream("/resources/bb/bbright1.png"));
        right2= new Image(getClass().getResourceAsStream("/resources/bb/bbright2.png"));
        dead1 = new Image(getClass().getResourceAsStream("/resources/bb/dead1.png"));
        dead2 = new Image(getClass().getResourceAsStream("/resources/bb/dead2.png"));
        dead3 = new Image(getClass().getResourceAsStream("/resources/bb/dead3.png"));
        dead4 = new Image(getClass().getResourceAsStream("/resources/bb/dead4.png"));
        this.setImage(5);
	}

	@Override
	public void draw(int x, int y, boolean vivo,boolean none) {}
	
	
	
	
			
}
	

