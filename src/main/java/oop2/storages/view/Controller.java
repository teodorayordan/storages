package oop2.storages.view;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cache.spi.support.AbstractReadWriteAccess.Item;
import org.hibernate.cfg.Configuration;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import oop2.storages.Agent;
import oop2.storages.Owner;
import oop2.storages.Storage;
import oop2.storages.User;

public class Controller implements Initializable {

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

	}

	//
	// Login
	@FXML
	Button loginButton;

	@FXML
	TextField accountNameField;

	@FXML
	PasswordField passwordField;

	@FXML
	Label label;
	//
	//

	@FXML
	public void login(ActionEvent event) {
		// create session factory
		SessionFactory factory = new Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(User.class)
				.addAnnotatedClass(Owner.class).addAnnotatedClass(Agent.class).addAnnotatedClass(Storage.class)
				.buildSessionFactory();

		// create session
		Session session = factory.getCurrentSession();

		if (accountNameField.getText().equals("admin") && passwordField.getText().equals("admin"))
			try {

				Parent root = FXMLLoader.load(getClass().getResource("AdminPane.fxml"));
				Stage stage = new Stage();
				stage.setScene(new Scene(root));
				stage.show();

				// hide login pane
				loginButton.getScene().getWindow().hide();

			} catch (Exception e) {
				e.printStackTrace();
			}
		else {
			String accountName = accountNameField.getText();
			String accountPassword = passwordField.getText();
			try {

				// start a transaction
				session.beginTransaction();

				User loginResult = (User) session.createQuery("from User s where s.accountName='" + accountName
						+ "' and s.accountPassword='" + accountPassword + "'").uniqueResult();

				// commit transaction
				// session.getTransaction().commit();

				System.out.println("Done!");

				if (loginResult == null) {
					System.out.println("No account");
					label.setVisible(true);
				} else {
					System.out.println("There is a account");

					// Proverka koe ot dvete e, ednoto shte e null
					Owner owner = session.get(Owner.class, loginResult.getUserID());
					Agent agent = session.get(Agent.class, loginResult.getUserID());

					if (owner != null) {
						System.out.println("Its an owner");
						System.out.println(owner);
					}

					else if (agent != null) {
						System.out.println("Its an agent");
						System.out.println(agent);
					}

					else {
						System.out.println("Contact an admin for help");
					}

					// commit transaction
					session.getTransaction().commit();
				}

			} finally {
				factory.close();
			}

		}
	}

}
