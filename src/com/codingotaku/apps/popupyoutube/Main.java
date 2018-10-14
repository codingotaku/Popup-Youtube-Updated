package com.codingotaku.apps.popupyoutube;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class Main extends Application {
	@Override
	public void start(Stage stage) {
		try {
			stage.setTitle("Popup Youtube");
			stage.initStyle(StageStyle.TRANSPARENT);
			Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
			stage.setMinWidth(230);
			stage.setMinHeight(150);
			stage.setResizable(true);
			Scene scene = new Scene(root,600,500);
			stage.getIcons().add(new Image(getClass().getResourceAsStream("C.bmp")));
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			stage.setScene(scene);
			stage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
