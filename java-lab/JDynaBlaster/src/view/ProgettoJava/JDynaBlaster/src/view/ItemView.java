package view;

import javafx.animation.AnimationTimer;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class ItemView extends ImageView{
	
	private Image pugno,teschio,fiamma;
	
	public ItemView(){
		super.setFitWidth(15);
		super.setFitHeight(15);
		loadImages();
	}
	
	/**carica le immagini*/
	public void loadImages() {
		pugno=new Image(getClass().getResourceAsStream("/resources/items/pugno.png"));
		teschio=new Image(getClass().getResourceAsStream("/resources/items/teschio.png"));
		fiamma=new Image(getClass().getResourceAsStream("/resources/items/fiamma.png"));
	}
	
	/**disegna l'item numero i in posizione posx,posy nel piano
	 * @param b */
	public void drawItem(int i, int posX, int posY, boolean used,boolean spawned) {
		setImage(null);
		if(used) {
			
			super.setX(posX);
			super.setY(posY);
			
			switch(i) {
			
			case 1: setImage(pugno); break;
			
			case 2: setImage(fiamma); break;
			
			case 3: setImage(teschio); break;
			
			}
			
		}else if(!used && spawned){
			Text text=new Text();
			text.setX(70);
			text.setY(350);
			text.setFont(new Font(40));
			GameView.getInstance().getGamePanel().addComponent(text);
			switch(i) {
			
			case 1: text.setText("   5 Secondi super Velocita!!!"); break;
			
			case 2: text.setText("10 Secondi bombe piu veloci!!!");break;
			
			case 3: text.setText("    10 Secondi doppie bombe!!!"); break;
			
			}
			new AnimationTimer() {
				long last=0;
				int counter=0;
				@Override
				public void handle(long arg0) {
					if(arg0-last>100000000l) {
						if(counter%2==1)
							text.setStyle("-fx-color:white");
						else
							text.setStyle("-fx-color:black");
						counter++;
						if(counter==16) {
							GameView.getInstance().getGamePanel().removeComponent(text);
							stop();
						}
							
						last=arg0;
					}
					
				}
				
			}.start();
			setImage(null);
		}
		
	}
}
