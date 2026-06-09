module JDynaBlaster {
	requires javafx.controls;
	requires javafx.graphics;
	requires java.desktop;
	requires javafx.base;
	requires javafx.media;
	requires javafx.web;
	
	opens application to javafx.graphics, javafx.fxml;
}
