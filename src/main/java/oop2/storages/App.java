package oop2.storages;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
  
		// create session factory
		SessionFactory factory = new Configuration()
								.configure("hibernate.cfg.xml")
								.addAnnotatedClass(User.class)
								.buildSessionFactory();
		
		// create session
		Session session = factory.getCurrentSession();
		
		try {			
			
			// start a transaction
			session.beginTransaction();

			/*List<User> loginResult = session.createQuery("from User").list();
			
			loginResult = session.createQuery("from User").list();
			System.out.println(loginResult);*/
			
			/*Owner ownerTest = (Owner) session.createQuery("from Owner s where s.ownerID = 1").uniqueResult();
			System.out.println(ownerTest.getStorageList());
			
			Contract conTest = (Contract) session.createQuery("from Contract s where s.contractID = 1").uniqueResult();
			System.out.println(conTest);*/
			
			
			/*List<Storage> storageTest = session.createQuery("from Storage").list();
			System.out.println(storageTest);*/
			
			
			// commit transaction
			session.getTransaction().commit();
			
			System.out.println("Done!");
		}
		finally {
			factory.close();
		}
		
		Parent root = FXMLLoader.load(getClass().getResource("view/Login.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}