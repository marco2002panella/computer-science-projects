package view;

import javafx.scene.image.Image;
import javafx.scene.text.Text;

public class GhostView extends EntityView{
	
	private boolean p;
	private Text points=new Text("150");
	private int c=0;
	public GhostView(int larghezza, int altezza, int x, int y, GamePanel input) {
		super(larghezza, altezza, x, y, input);
		p=false;
		loadImages();
	}

	@Override
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
		}else if(!vivo && !none && !p) {
			setImage(13);
			p=true;
			this.getChildren().add(points);
			super.setLayoutX(x+5);
			super.setLayoutY(y+5);
			
		}else if(p && none){
			System.out.println("rimuovi");
			this.getChildren().remove(points);
		}
		
	}
	
	private void loadImages(){
		up=new Image(getClass().getResourceAsStream("/resources/ghost/1.png"));
		up1=new Image(getClass().getResourceAsStream("/resources/ghost/2.png"));
	}
	
}
