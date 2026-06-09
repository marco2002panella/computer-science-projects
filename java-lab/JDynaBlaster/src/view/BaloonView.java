package view;

import javafx.scene.image.Image;
import javafx.scene.text.Text;

public class BaloonView extends EntityView{
	private int counter=0;
	private boolean p;
	private int c=0;
	private final Text points=new Text("100");
	public BaloonView(int larghezza, int altezza, int x, int y, GamePanel input) {
		super(larghezza, altezza, x, y, input);
		p=false;
		loadImages();
	}

	/**disegna il baloon*/
	public void draw(int x, int y, boolean vivo,boolean none) {
		if(vivo) {
			super.setLayoutX(x-15);
			super.setLayoutY(y-15);
			if(c<12) {
				setImage(0);
			}else if (c>12 &&  c<25){
				setImage(1);
			}
			if(c>25)
				c=0;
			c++;
		}else if(!vivo && !p) {
			setImage(13);
			p=true;
			this.getChildren().add(points);
			super.setLayoutX(x+5);
			super.setLayoutY(y+5);
		} 
		if(none && !vivo && p) {
			this.getChildren().remove(points);
		}
	}
	
	private void loadImages(){
		up=new Image(getClass().getResourceAsStream("/resources/Baloon/1.png"));
		up1=new Image(getClass().getResourceAsStream("/resources/Baloon/2.png"));
	}


}
