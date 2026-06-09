package view;

import java.util.ArrayList;

import controller.Controller;
import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class GameViewManager{
	
	private ArrayList<GamePanel> game=new ArrayList<>();
	private Controller controller;
	
	public GameViewManager() {
		
	}
	
	public GamePanel getGamePanel(int i) {
		return game.get(i);
	}
}
