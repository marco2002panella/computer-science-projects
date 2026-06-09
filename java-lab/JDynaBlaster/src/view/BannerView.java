package view;

import dataModel.GameModel.Timer;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class BannerView extends Pane{
	
	private static BannerView singleton;
	private BannerView(int w,int h){
		super.setLayoutX(0);
		super.setLayoutY(0);
		super.setWidth(w);
		super.setHeight(h);
		
		ImageView banner=new ImageView();
		
		super.getChildren().add(banner);
		
	}
	
	/**restituisce l unica istanza del banner presente*/
	public static BannerView getInstance(int w,int h) {
		if(singleton==null)
			singleton=new BannerView(w,h);
		return singleton;
	}
	
	
	
}
