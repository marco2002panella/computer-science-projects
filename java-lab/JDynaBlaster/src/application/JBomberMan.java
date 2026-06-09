package application;

import controller.Controller;
import dataModel.GameModel;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.stage.Stage;
import view.GamePanel;
import view.GameView;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

/***/
public class JBomberMan extends Application {
	
	public void start(Stage primaryStage) {
		try {
			
			GameView gameView=GameView.getInstance();
			GameModel gameModel=GameModel.getInstance();
			Controller gameController=Controller.getInstance();
			gameController.start();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
