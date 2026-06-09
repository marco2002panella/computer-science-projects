package view;

import java.util.ArrayList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class TileView extends ImageView{
	
	public TileView(int blocco, Image in, int y,int x) {
		
		super.setImage(in);
		super.setFitWidth(blocco);
		super.setFitHeight(blocco);
		super.setX(x);
		super.setY(y);
	}
	
}
