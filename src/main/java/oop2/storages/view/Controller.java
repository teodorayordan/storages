package oop2.storages.view;

import java.net.URL;
import java.util.ResourceBundle;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.sun.xml.bind.v2.runtime.unmarshaller.XsiNilLoader.Single;

import javafx.application.Platform;
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
import javafx.stage.WindowEvent;
import oop2.storages.Agent;
import oop2.storages.HibernateUtility;
import oop2.storages.Owner;
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

	@FXML
	public void login(ActionEvent event) {
		// create session factory
		SessionFactory factory = HibernateUtility.getSessionFactory();

		// create session
		Session session = factory.getCurrentSession();
		session.beginTransaction();

		if (accountNameField.getText().equals("admin") && passwordField.getText().equals("admin"))
			try {
				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AdminPane.fxml"));
				Parent root1 = (Parent) fxmlLoader.load();
				Stage stage = new Stage();
				stage.setScene(new Scene(root1));
				stage.show();

				// hide login pane
				loginButton.getScene().getWindow().hide();

				stage.setOnCloseRequest((WindowEvent event1) -> {
					factory.close();
					Platform.exit();
				});

			} catch (Exception e) {
				e.printStackTrace();
			}
		else {
			String accountName = accountNameField.getText();
			String accountPassword = passwordField.getText();

			// start a transaction

			User loginResult = (User) session.createQuery("from User s where s.accountName='" + accountName
					+ "' and s.accountPassword='" + accountPassword + "'").uniqueResult();

			if (loginResult == null) {
				System.out.println("No account");
				label.setVisible(true);
			} else {
				Singleton.getInstance().setUser(loginResult);
				System.out.println("There is a account");

				// Proverka koe ot dvete e, ednoto shte e null
				Owner owner = session.get(Owner.class, loginResult.getUserID());
				Agent agent = session.get(Agent.class, loginResult.getUserID());

				if (owner != null && owner.getUser().getStatusLogin() == false) {
					System.out.println("Its an owner");
					System.out.println(owner);
					Singleton.getInstance().setOwner(owner);
					Singleton.getInstance().getUser().setStatusLogin(true); // setvame che e lognat
					session.update(Singleton.getInstance().getUser());

					try {
						Parent root = FXMLLoader.load(getClass().getResource("OwnerPane.fxml"));
						Stage stage = new Stage();
						stage.setScene(new Scene(root));
						stage.show();
						// hide login pane
						loginButton.getScene().getWindow().hide();

						stage.setOnCloseRequest((WindowEvent event1) -> {
							if (Singleton.getInstance().getUser() != null) {
								Session session1 = factory.getCurrentSession();
								session1.beginTransaction();
								Singleton.getInstance().getUser().setStatusLogin(false);
								session1.update(Singleton.getInstance().getUser());
								session1.getTransaction().commit();
							}
							factory.close();
							Platform.exit();
						});

					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				else if (agent != null && agent.getUser().getStatusLogin() == false) {
					System.out.println("Its an agent");
					System.out.println(agent);
					Singleton.getInstance().setAgent(agent);
					Singleton.getInstance().getUser().setStatusLogin(true);
					session.update(Singleton.getInstance().getUser());

					try {
						Parent root = FXMLLoader.load(getClass().getResource("AgentPane.fxml"));
						Stage stage = new Stage();
						stage.setScene(new Scene(root));
						stage.show();

						// hide login pane
						loginButton.getScene().getWindow().hide();

						stage.setOnCloseRequest((WindowEvent event1) -> {
							if (Singleton.getInstance().getUser() != null) {
								Session session1 = factory.getCurrentSession();
								session1.beginTransaction();
								Singleton.getInstance().getUser().setStatusLogin(false);
								session1.update(Singleton.getInstance().getUser());
								session1.getTransaction().commit();
							}
							factory.close();
							Platform.exit();
						});

					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					System.out.println("Contact an admin for help. It appears youre logged in.");
					session.getTransaction().commit();
				}

			}
			// commit transaction

		}
		// session.getTransaction().commit();
	}
}
