package view;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import dataModel.Map;
import javafx.animation.AnimationTimer;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class MapView extends Pane{
	

	private int col,row;
	private GamePanel cd;
	private int blocco;
	private Image images[];
	private int[][] clone;
	private boolean showed;
	
	//tengo una lista degli elementi della mappa per non cancellare giocatore bomba e entità che sono disegnati nei nodi figli della mappa
	private Collection childrens;
	
	public MapView(int blocco, GamePanel input){
		cd=input;
		images=new Image[14];
		this.blocco=input.getBlock();
		col=(int) (input.getScene().getWidth()/blocco);
		row=(int) ((input.getScene().getHeight()-80)/blocco);
		childrens=new ArrayList<Node>();
		getImages();
		showed=false;
	}
	
	public void getImages(){
		images[1]=new Image(getClass().getResourceAsStream("/resources/maps/muro.png"));
		images[2]=new Image(getClass().getResourceAsStream("/resources/maps/mattoni.png"));
		images[6]=new Image(getClass().getResourceAsStream("/resources/maps/angolo.png"));
		images[7]=new Image(getClass().getResourceAsStream("/resources/maps/lato.png"));
		images[8]=new Image(getClass().getResourceAsStream("/resources/maps/altezza.png"));
				
	}
	
	 private static Image loadImageFromFile(String filePath) throws IOException {
	        File file = new File(filePath);
	        FileInputStream inputStream = new FileInputStream(file);
	        InputStream input=inputStream;
	        return new Image(input);
	    }
	
	/**disegno la mappa e se cambiano i blocchi distruttibili fa una animazione
	 * @param map
	 * @return void*/
	public void drawMap(Map in) {
		int x=0,y=0;
		int counter=0;
		int[][] clone0;
		
		if(clone==null) {
			clone=in.getMatrix();
		}
		
		clone0=in.getMatrix();
		
		if(in!=null) {
			
			if(childrens.size()!=0) {
				super.getChildren().removeAll(childrens);
				childrens.removeAll(childrens);
			}
			
			//non voglio che la mia mappa sia modificata dalla view quindi ne faccio un clone
			
			while(x<row) {
				y=0;
				while(y<col) {
					if(clone0[x][y]==1) {
						TileView muro=new TileView(blocco,images[1],(x*blocco)+80,y*blocco);
						super.getChildren().add(muro);
						childrens.add(muro);
						
					}else if(clone[x][y]==6) {
						TileView angolo=new TileView(blocco,images[6],(x*blocco)+80,y*blocco);
						super.getChildren().add(angolo);
						childrens.add(angolo);
						
					}else if(clone[x][y]==7) {
						TileView lato=new TileView(blocco,images[7],(x*blocco)+80,y*blocco);
						super.getChildren().add(lato);
						childrens.add(lato);
						
					}else if(clone[x][y]==8) {
						TileView altezza=new TileView(blocco,images[6],(x*blocco)+80,y*blocco);
						altezza.setRotate(90);
						super.getChildren().add(altezza);
						childrens.add(altezza);
						
					}else if(clone[x][y]==9) {
						TileView altezza=new TileView(blocco,images[8],(x*blocco)+80,y*blocco);
						altezza.setRotate(180);
						super.getChildren().add(altezza);
						childrens.add(altezza);
						
					}else if(clone[x][y]==10) {
						TileView altezza=new TileView(blocco,images[6],(x*blocco)+80,y*blocco);
						altezza.setRotate(180);
						super.getChildren().add(altezza);
						childrens.add(altezza);
						
					}else if(clone[x][y]==11) {
						TileView altezza=new TileView(blocco,images[7],(x*blocco)+80,y*blocco);
						altezza.setRotate(180);
						super.getChildren().add(altezza);
						childrens.add(altezza);
						
					}else if(clone[x][y]==12) {
						TileView altezza=new TileView(blocco,images[6],(x*blocco)+80,y*blocco);
						altezza.setRotate(-90);
						super.getChildren().add(altezza);
						childrens.add(altezza);
						
					}else if(clone[x][y]==13) {
						TileView altezza=new TileView(blocco,images[8],(x*blocco)+80,y*blocco);
						super.getChildren().add(altezza);
						childrens.add(altezza);
						
					}if(clone0[x][y]==0){
						TileView mattoni=new TileView(blocco,images[2],(x*blocco)+80,y*blocco);
						if(clone[x][y]==2) { 
							
							new AnimationTimer(){
								
									long last=0;
									
									int counter=0;
									
									double x=mattoni.getX();
																
									public void handle(long arg0) {
										if(!getChildren().remove(mattoni))
											getChildren().add(mattoni);
										if(arg0-last>1000000l) {
											System.out.println("dentro");
											System.out.println(counter);
											switch(counter) {
											case 0: mattoni.setX(x+1); break;
											case 1: mattoni.setX(x); break;
											case 2: mattoni.setX(x-1); break;
											case 3:	mattoni.setX(x); break;
											case 4: mattoni.setX(x+1); break;
											case 5: mattoni.setX(x); break;
											case 6: mattoni.setX(x-1); break;
											case 7:	mattoni.setX(x); break;
											case 8: getChildren().remove(mattoni);
												stop();
												break;
											}
											counter++;
											last=arg0;
										}
										
									}
									
								}.start();
						}
					}
					else if(clone0[x][y]==2) {
						TileView mattoni=new TileView(blocco,images[2],(x*blocco)+80,y*blocco);
						super.getChildren().add(mattoni);
						childrens.add(mattoni);
						
					}else if(clone0[x][y]==4) {
						TileView mattoni=new TileView(blocco,images[2],(x*blocco)+80,y*blocco);
						super.getChildren().add(mattoni);
						childrens.add(mattoni);
					}
					else if(clone0[x][y]==5 ) {
						ImageView vortice=new ImageView();
						childrens.add(vortice);
						vortice.setX(y*blocco);
						vortice.setY(x*blocco+80);	
						vortice.setImage(new Image(getClass().getResourceAsStream("/resources/maps/vortice.png")));
						super.getChildren().add(vortice);
						new AnimationTimer() {
							int counter=4;
							long last=0;
							@Override
							public void handle(long arg0) {
								if(arg0-last>100000000l) {
									switch(counter) {
									case 1: vortice.setRotate(90); counter++; break;
									case 2: vortice.setRotate(180); counter++; break;
									case 3: vortice.setRotate(270); counter++; break;
									case 4: stop();break;
									}
									last=arg0;
								}
						
							}
							
						}.start();
					}
					y++;
				}
				
				x++;
			}
			clone=clone0;
		}
		
	}
	
	
	
}
