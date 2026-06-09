package view;

import controller.Controller;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class RestartView {
	private Scene scene;
	private Pane root;
	private static RestartView view;
	
	private RestartView() {
		root=new Pane();
		scene=new Scene(root,800,800);
		root.setStyle("-fx-background-color:White;");
		}
	
	public static RestartView getInstance() {
		if(view==null)
			view=new RestartView();
		return view;
	}
	
	public Scene getScene() {
		Text finish=new Text();
		finish.setText("You are dead, choose");
		
		root.getChildren().add(finish);
		finish.setLayoutX(200);
		finish.setLayoutY(350);
		finish.setFont(Font.font("Arial", FontWeight.BOLD, 30));
		finish.setFill(Color.WHITE);
        finish.setStroke(Color.BLACK); 
        finish.setStrokeWidth(2);
		Button back=new Button();
		Button contin=new Button();
		
		back.setText("back to menu");
		contin.setText("continue");
		
		back.setStyle("-fx-background-color:white;-fx-text-fill: #000000;-fx-font-size: 10px;");
		contin.setStyle("-fx-background-color:white;-fx-text-fill: #000000; -fx-font-size: 10px;");
		
		root.getChildren().add(contin);
		root.getChildren().add(back);
		back.setLayoutX(225);
		contin.setLayoutX(425);
		back.setLayoutY(400);
		contin.setLayoutY(400);
		back.setOnMouseEntered(event->{back.setText("-back to menu");});
		contin.setOnMouseEntered(event->{contin.setText("-continue");});
		back.setOnMouseExited(event->{back.setText("back to menu");});
		contin.setOnMouseExited(event->{contin.setText("continue");});
		
		back.setOnMouseClicked(event->{
			GameView.getInstance().menu();
			Controller.getInstance().updateStato(0);
		});
		contin.setOnMouseClicked(event->{
			GameView.getInstance().game();
			Controller.getInstance().updateStato(3);
		});
		return scene;
	}
}
