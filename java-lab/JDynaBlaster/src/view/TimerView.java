package view;

import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class TimerView extends Pane{
	private static TimerView singleton;
	private Text currentText;
	private TimerView() {
		this.setLayoutX(0);
		this.setLayoutY(15);
		currentText=new Text();
		currentText.setText("5:00");
		super.getChildren().add(currentText);
	}
	
	public static TimerView getIstance() {
		if(singleton==null)
			singleton=new TimerView();
		return singleton;
	}
	
	public void set(String s){
		currentText.setText(s);
	}
	
}
