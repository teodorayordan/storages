package oop2.storages.view;

import java.net.URL;
import java.util.ResourceBundle;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
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

	@FXML
	Button loginBtn;

	@FXML
	TextField accountName;

	@FXML
	PasswordField accountPassword;

	@FXML
	Label label;

	@FXML
	public void login(ActionEvent event) {
		// create session factory
		SessionFactory factory = HibernateUtility.getSessionFactory();

		// create session
		Session session = factory.getCurrentSession();
		session.beginTransaction();
		String accName = accountName.getText();
		String accPassword = accountPassword.getText();

		if (accName.equals("admin") && accPassword.equals("admin"))
			try {
				Parent root = FXMLLoader.load(getClass().getResource("AdminPane.fxml"));
				Stage stage = new Stage();
				stage.setTitle("Admin Profile");
				stage.setScene(new Scene(root));
				stage.show();

				// hide login pane
				loginBtn.getScene().getWindow().hide();

				stage.setOnCloseRequest((WindowEvent event1) -> {
					factory.close();
					Platform.exit();
				});

			} catch (Exception e) {
				e.printStackTrace();
			}
		else {

			// start a transaction

			User loginResult = (User) session.createQuery(
					"from User s where s.accountName='" + accName + "' and s.accountPassword='" + accPassword + "'")
					.uniqueResult();

			if (loginResult == null) {
				System.out.println("No account");
				label.setVisible(true);
				session.getTransaction().commit();
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
						stage.setTitle("Owner Profile");
						stage.show();
						// hide login pane
						loginBtn.getScene().getWindow().hide();

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
						loginBtn.getScene().getWindow().hide();

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

	public void keyPressed(KeyEvent event) {

		/*
		 * KeyCode key = event.getCode(); if(key == KeyCode.ENTER) {
		 * accountPassword.requestFocus(); }
		 */
		Control[] focusOrder = new Control[] { accountName, accountPassword };

		for (int i = 0; i < focusOrder.length - 1; i++) {
			Control nextControl = focusOrder[i + 1];
			focusOrder[i].addEventHandler(ActionEvent.ACTION, e -> nextControl.requestFocus());
		}
		if (accountPassword.isFocused()) {
			loginBtn.setDefaultButton(true);
		}
	}

}
