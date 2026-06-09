package dataModel;

import java.util.ArrayList;

import javafx.scene.image.Image;

public class Entity {
	
	private int x,y;

	private String direzione;
	
	private boolean collide;
	
	private int row,col; //posizione del blocco
	
	private Image up,up1,up2, down,down1,down2, left,left1,left2, right,right1,right2;
	private Image dead1,dead2,dead3,dead4;
	
	private ArrayList<Image> images;
	
	private int latoX,latoY,blocco;
	
	private GameModel cd;
	
	private AreaSolida areaSolida;
	
	public Entity(int x,int y, int w,int h,boolean collide,String direzione,int blocco, GameModel cd){
		images=new ArrayList<>();
		
		this.cd=cd;
		
		this.x=x;
		
		this.y=y;
		
		this.direzione=direzione;
		
		latoX=w;
		
		latoY=h;
		
		this.blocco=blocco;
		
		

	}
	
	public GameModel getGameModel(){
		return cd;
	}
	/**imposta la direzione*/
	public void setDirezione(String d) {
		if(d.equals("su") ||d.equals("giu") ||d.equals("sinistra") ||d.equals("destra") )
			this.direzione=d;
	}
	/**imposta le cordinate nel punto x*/
	public void setX(int x) {
		this.x=x;
	}
	/**imposta le cordinate nel punto y*/
	public void setY(int y) {
		this.y=y;
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public String getDirezione() {
		return direzione;
	}
	
	public int getAltezza() {
		return latoY;
	}
	
	public int getLarghezza() {
		return latoX;
	}
	public int getCol() {
		return (int)(getX()/blocco);
	}
	public int getRow() {
		return (int)(getY()/blocco);
	}
	
	public int getBlock() {
		return blocco;
	}
	
}
