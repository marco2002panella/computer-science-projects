package view;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import controller.Controller;
import dataModel.Player;
import dataModel.SingleBombAttack;
import dataModel.SkullItem;
import dataModel.SuperSpeedItem;
import dataModel.AreaSolida;
import dataModel.Baloon;
import dataModel.CanAttack;
import dataModel.DoubleBombAttack;
import dataModel.Entity;
import dataModel.FireAttackBomb;
import dataModel.FireItem;
import dataModel.Ghost;
import dataModel.Item;
import dataModel.Map;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GamePanel extends Observable{
	
	private Scene scene;
	private Group root;
	private PlayerView player;
	private MapView map;
	private BombView bomb,bomb1;
	private ItemView itemView;
	private Text timer,lives,points,info;
	private int blocco;
	
	public GamePanel(int blocco,int larghezza, int altezza){
		
		root=new Group();
		scene=new Scene(root,larghezza,altezza);
		this.blocco=blocco;
		itemView=new ItemView();
		itemView.setImage(null);
		root.getChildren().add(itemView);
		timer=new Text();
		
		timer.setStyle(" -fx-font-size: 24px;\r\n"
				+ "    -fx-text-fill: white;\r\n"
				+ "    -fx-background-color: white;\r\n"
				+ "    -fx-padding: 10px;\r\n"
				+ "    -fx-border-color: black;\r\n"
				+ "    -fx-border-width: 2px;\r\n"
				+ "    -fx-border-radius: 5px;");
		root.getChildren().add(timer);
		
		info=new Text();
		info.setStyle(" -fx-font-size: 10px;\r\n"
				+ "    -fx-text-fill: white;\r\n"
				+ "    -fx-background-color: white;\r\n"
				+ "    -fx-padding: 10px;\r\n"
				+ "    -fx-border-color: black;\r\n"
				+ "    -fx-border-width: 2px;\r\n"
				+ "    -fx-border-radius: 5px;");
		root.getChildren().add(info);
		
		lives=new Text();
		lives.setStyle(" -fx-font-size: 24px;\r\n"
				+ "    -fx-text-fill: white;\r\n"
				+ "    -fx-background-color: white;\r\n"
				+ "    -fx-padding: 10px;\r\n"
				+ "    -fx-border-color: black;\r\n"
				+ "    -fx-border-width: 2px;\r\n"
				+ "    -fx-border-radius: 5px;");
		root.getChildren().add(lives);
		
		points=new Text();
		points.setStyle(" -fx-font-size: 24px;\r\n"
				+ "    -fx-text-fill: white;\r\n"
				+ "    -fx-background-color: white;\r\n"
				+ "    -fx-padding: 10px;\r\n"
				+ "    -fx-border-color: black;\r\n"
				+ "    -fx-border-width: 2px;\r\n"
				+ "    -fx-border-radius: 5px;");
		root.getChildren().add(points);
		
	}
	
	/**ritorna la scena del pannello di gioco visivo */
	public Scene getScene() {
		return scene;
	}
	/**ritorna la dimensione dei blocchi del pannello*/
	public int getBlock() {
		return blocco;
	}
	
	/**disegna il timer*/
	public void drawTimer(Integer l){
		if(l!=null) {
			Integer q=l/60;
			Integer r=l-(q*60);
			String s;
			if(r>=10)
				s="Time left "+q+":"+r;
			else
				s="Time left "+q+":0"+r;
			timer.setText(s);
			timer.setX(10);
			timer.setY(50);
		}else
			timer.setText("");
	}
	
	/**disegna il contatore delle vite*/
	public void drawLives(Integer i) {
		if(i!=null) {
			String s="lives left: "+i;
			lives.setText(s);
			lives.setX(300);
			lives.setY(50);
			info.setText("press p for pause/press esc for go back to menu");
			info.setX(250);
			info.setY(75);
		}else {
			lives.setText("Press start!");
			lives.setX(300);
			lives.setY(50);
			info.setText("");
		}
	}
	/**disegna il contatore dei punti*/
	public void drawPoints(Integer i) {
		if(i!=null) {
			String s="points:"+i;
			points.setText(s);
			points.setX(600);
			points.setY(50);
		}else
			points.setText("");
	}
	
	/**imposta il colore del giocatore*/
	public void setPlayerColor(int i){
		player.setcolor(i);
	}
	
	/**disegna un item, 0 speedItem,1 fireItem,2 skullItem*/
	public void drawItem(Object o){
		if(o instanceof SkullItem){
			itemView.drawItem(3,((SkullItem)o).getPosX(),((SkullItem)o).getPosY(),((SkullItem)o).getSpawned(),((SkullItem)o).getUsed());
		}
		if(o instanceof FireItem) {
			itemView.drawItem(2,((FireItem)o).getPosX(),((FireItem)o).getPosY(),((FireItem)o).getSpawned(),((FireItem)o).getUsed());
		}
		if(o instanceof SuperSpeedItem) {
			itemView.drawItem(1,((SuperSpeedItem)o).getPosX(),((SuperSpeedItem)o).getPosY(),((SuperSpeedItem)o).getSpawned(),((SuperSpeedItem)o).getUsed());
		}
		
	}
	/**aggiunge un componente grafico*/
	public void addComponent(Node e) {
		root.getChildren().add(e);
	}
	
	/**aggiunge un componente grafico*/
	public void removeComponent(Node e) {
		root.getChildren().remove(e);
	}
	
	/**cancella tutti gli item sullo schermo*/
	public void deleteItem(){
		itemView.setImage(null);
	}
	
	/**crea la playerView*/
	public void createPlayerView(int larghezza,int altezza, int x, int y) {
		player=new PlayerView(larghezza,altezza,x,y,this);
		bomb=new BombView();
		bomb1=new BombView();
		map.getChildren().add(player);
		map.getChildren().add(bomb);
		map.getChildren().add(bomb1);
	}
	
	/**crea la mapView*/
	public void createMap() {
		map=new MapView(blocco,this);
		root.getChildren().add(map);
		
	}
	
	/**ritorna la playerView*/
	public PlayerView getPlayer() {
		return player;
	}
	
	/**disegna la mappa*/
	public void drawMap(Map in){
		if(player!=null) {
			
			map.getChildren().remove(player);
			map.getChildren().remove(bomb);		
			map.getChildren().remove(bomb1);	
		}
		
		map.drawMap(in);
		
		if(player!=null) {
			map.getChildren().add(bomb1);
			map.getChildren().add(bomb);
			map.getChildren().add(player);
			
		}
		
	}
	
	
	/**disegna la bomba in posizione x,y, piazzabile,esplosa creano una animazione in base allo stato
	 * i è il tipo di bomba che bisogna disegnare */
	public void drawBomb(CanAttack<Player> attack) {
		if(bomb!=null) {
			if(attack instanceof SingleBombAttack) {
				SingleBombAttack s=(SingleBombAttack)attack;
				bomb.drawSingle(s.getBomb().getPosX(), s.getBomb().getPosY(), s.getBomb().isPiazzable(), s.getBomb().isExplosed());
			}else if(attack instanceof FireAttackBomb) {
				FireAttackBomb s=(FireAttackBomb)attack;
				bomb.drawFire(s.getBomb().getPosX(), s.getBomb().getPosY(), s.getBomb().isPiazzable(), s.getBomb().isExplosed());
			}else if(attack instanceof DoubleBombAttack) {
				DoubleBombAttack s=(DoubleBombAttack)attack;
				if(s.getNumber()==0)
					bomb1.drawSingle(s.getBomb1().getPosX(), s.getBomb1().getPosY(), s.getBomb1().isPiazzable(), s.getBomb1().isExplosed());
				else
					bomb.drawSingle(s.getBomb2().getPosX(), s.getBomb2().getPosY(), s.getBomb2().isPiazzable(), s.getBomb2().isExplosed());

			}
		}	
	}
	
	private LinkedList<EntityView> enemies=new LinkedList<>();
	public void createEnemiesView(List<Entity> en) {
		if(enemies.size()!=0) {
			root.getChildren().removeAll(enemies);
			enemies.removeAll(enemies);
		}
		
		for(Entity i: en) {
			if(i instanceof Baloon) {
				enemies.add(new BaloonView(60,60,0,0,this));
			}
			if(i instanceof Ghost) {
				enemies.add(new GhostView(60,60,0,0,this));
			}
		}
		
		root.getChildren().addAll(enemies);
	}
	
	public void drawEnemie(Entity in){
		if(in.getClass().equals(Baloon.class))
			enemies.get(((Baloon)in).getNumber()).draw(((Baloon)in).getX(), ((Baloon)in).getY(), !((Baloon)in).isDead(),((Baloon)in).getNone());
		if(in.getClass().equals(Ghost.class))
			enemies.get(((Ghost)in).getNumber()).draw(((Ghost)in).getX(), ((Ghost)in).getY(), !((Ghost)in).isDead(),((Ghost)in).getNone());
	}
	
	
	/**disegna il player nella posizione x,y del piano*/
	public void drawPlayer(int x,int y,String direction,int speed,boolean alive,int lives) {
		player.translate(x,y,direction,speed,alive,lives);
		
	}

	/**disegna una area solida*/
	public void drawSolidArea(ArrayList<AreaSolida> in) {
		Iterator<AreaSolida> a=in.iterator();
		while(a.hasNext()) {
			AreaSolida d=a.next();
			Rectangle b=new Rectangle(d.getXY0().getX()-d.getXY().getX(),d.getXY2().getY()-d.getXY().getY(),Color.GREEN);
			root.getChildren().add(b);
			b.setX(d.getXY().getX());
			b.setY(d.getXY().getY());
		}
	}
	
}
