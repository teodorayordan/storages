package oop2.storages.view;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import oop2.storages.HibernateUtility;

public class App extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {

		// create session factory
		SessionFactory factory = HibernateUtility.getSessionFactory();

		Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
		primaryStage.setScene(new Scene(root));
		primaryStage.show();

		primaryStage.setOnCloseRequest((WindowEvent event1) -> {
			if (Singleton.getInstance().getUser() != null) {
				Session session = factory.getCurrentSession();
				session.beginTransaction();
				Singleton.getInstance().getUser().setStatusLogin(false);
				session.update(Singleton.getInstance().getUser());
				session.getTransaction().commit();
			}
			factory.close();
			Platform.exit();
		});

	}

	public static void main(String[] args) {
		launch(args);
	}
}