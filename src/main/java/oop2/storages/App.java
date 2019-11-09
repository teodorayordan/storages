package oop2.storages;

import org.hibernate.SessionFactory;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class App extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {

		// create session factory
		SessionFactory factory = HibernateUtility.getSessionFactory();

		Parent root = FXMLLoader.load(getClass().getResource("view/Login.fxml"));
		primaryStage.setScene(new Scene(root));
		primaryStage.show();

		primaryStage.setOnCloseRequest((WindowEvent event1) -> {
			factory.close();
			Platform.exit();
		});

	}

	public static void main(String[] args) {
		launch(args);
	}
}