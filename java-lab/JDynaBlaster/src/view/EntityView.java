package view;

import java.util.ArrayList;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class EntityView extends Pane{
	
	protected Image up,up1,up2, down,down1,down2, left,left1,left2, right,right1,right2;
	protected Image dead1,dead2,dead3,dead4;
	private GamePanel cd;
	private ImageView container;
	
	public EntityView(int larghezza,int altezza,int x,int y, GamePanel input) {
		container=new ImageView();
		this.getChildren().add(container);
		container.setFitWidth(larghezza);
		container.setFitHeight(altezza);
		this.setLayoutX(x);
		this.setLayoutY(y);
		cd=input;
	}
	
	
	public Image getImage(int i) {
		Image image=null;
		switch(i) {
		case 0:image= up;
		break;
		case 1: image=up1;
		break;
		case 2:image= up2;
		break;
		case 3: image=down;
		break;
		case 4:image= down1;
		break;
		case 5: image=down2;
		break;
		case 6: image=left;
		break;
		case 7: image=left1;
		break;
		case 8: image=left2;
		break;
		case 9: image=right;
		break;
		case 10: image=right1;
		break;
		case 11: image=right2;
		break;
		case 12: image = dead1; 
		break;
		case 13: image = dead2; 
		break;
		case 14: image = dead3; 
		break;
		case 15: image = dead4; 
		break;
		
		}
		return image;
	}
	
	public void setImage(int i) {
		Image image=down;
		switch(i) {
			case 0:image= up;
			break;
			case 1: image=up1;
			break;
			case 2:image= up2;
			break;
			case 3: image=down;
			break;
			case 4:image= down1;
			break;
			case 5: image=down2;
			break;
			case 6: image=left;
			break;
			case 7: image=left1;
			break;
			case 8: image=left2;
			break;
			case 9: image=right;
			break;
			case 10: image=right1;
			break;
			case 11: image=right2;
			break;
			case 12: image=dead1;
			break;
			case 13: image=dead2;
			break;
			case 14: image=dead3;
			break;
			case 15: image=dead4;
			break;
		}
		
		container.setImage(image);
		
	}
	
	public GamePanel getGamePanel() {
		return cd;
	}


	public void draw(int x, int y, boolean b, boolean none) {	}
	
	
}
