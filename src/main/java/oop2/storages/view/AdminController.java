package oop2.storages.view;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import oop2.storages.Owner;
import oop2.storages.User;

public class AdminController {

	//
	// Create Owner Elements
	//
	@FXML
	TextField ownerAccountName;

	@FXML
	PasswordField ownerAccountPassword;

	@FXML
	TextField ownerName;

	@FXML
	TextField ownerPin;

	// @FXML
	// Button createOwner;

	@FXML
	public void createOwner(ActionEvent event) {
		String account = ownerAccountName.getText();
		String password = ownerAccountPassword.getText();
		String name = ownerName.getText();
		String pin = ownerPin.getText();

		if (!account.isEmpty() && !password.isEmpty() && !name.isEmpty() && !pin.isEmpty()) {
			System.out.println(account + password + name + pin);

			// create session factory
			SessionFactory factory = new Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(User.class)
					.buildSessionFactory();

			// create session
			Session session = factory.getCurrentSession();

			try {
				// create a student object
				System.out.println("Creating new User object...");
				User tempUser = new User(account, password, name, pin);

				// start a transaction
				session.beginTransaction();

				User checkUser = new User();
				checkUser = (User) session
						.createQuery("from User s where s.accountName='" + account + "' OR s.pin='" + pin + "'")
						.uniqueResult();

				System.out.println(checkUser);

				if (checkUser == null) {
					// save the student object
					System.out.println("Saving the user...");
					session.save(tempUser);
					Owner tempOwner = new Owner(tempUser);
					session.save(tempOwner);
				} else {
					if (checkUser.getAccountName().equals(account))
						System.out.println("Account name Duplicate");
					else if (checkUser.getPin().equals(pin))
						System.out.println("PIN Duplicate");
					else
						System.out.println("Account name and PIN Duplicate");
				}

				// commit transaction
				session.getTransaction().commit();

				System.out.println("Done! Owner");
			}

			finally {
				factory.close();
			}
		} else
			System.out.println("Dont leave empty fields");

	}

	//
	// Create Agent Elements
	//
	@FXML
	TextField agentAccountName;

	@FXML
	PasswordField agentAccountPassword;

	@FXML
	TextField agentName;

	@FXML
	TextField agentPin;

	@FXML
	TextField agentCommission;

	@FXML
	public void createAgent(ActionEvent event) {
		String account = agentAccountName.getText();
		String password = agentAccountPassword.getText();
		String name = agentName.getText();
		String pin = agentPin.getText();
		String commission = agentCommission.getText();

		if (!account.isEmpty() && !password.isEmpty() && !name.isEmpty() && !pin.isEmpty() && !commission.isEmpty()) {
			System.out.println(account + password + name + pin);

			// create session factory
			SessionFactory factory = new Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(User.class)
					.buildSessionFactory();

			// create session
			Session session = factory.getCurrentSession();

			try {
				// create a student object
				System.out.println("Creating new User object...");
				User tempUser = new User(account, password, name, pin);

				// start a transaction
				session.beginTransaction();

				User checkUser = (User) session
						.createQuery("from User s where s.accountName='" + account + "' OR s.pin='" + pin + "'")
						.uniqueResult();

				System.out.println(checkUser);

				if (checkUser == null) {
					// save the student object
					System.out.println("Saving the user...");
					session.save(tempUser);
					//Agent tempAgent = newAtemp
				} else {
					if (checkUser.getAccountName().equals(account))
						System.out.println("Account name Duplicate");
					else if (checkUser.getPin().equals(pin))
						System.out.println("PIN Duplicate");
					else
						System.out.println("Account name and PIN Duplicate");
				}

				// commit transaction
				session.getTransaction().commit();

				System.out.println("Done! Agent");
			}

			finally {
				factory.close();
			}
		}

	}

	//
	// Category Elements
	//
	@FXML
	TextField categoryText;

	@FXML
	public void addCategory(ActionEvent event) {
		String category = categoryText.getText();
		System.out.println(category);
	}

	//
	// Type Elements
	//
	@FXML
	TextField typeText;

	@FXML
	public void addType(ActionEvent event) {
		String type = typeText.getText();
		System.out.println(type);
	}

	// @FXML
	// Button agentButton;

	/*
	 * @FXML public void ownerButton(ActionEvent event) { try {
	 * 
	 * Parent root = FXMLLoader.load(getClass().getResource("CreateOwner.fxml"));
	 * Stage stage = new Stage(); stage.setScene(new Scene(root)); stage.show();
	 * ownerButton.getScene().getWindow().hide();
	 * 
	 * 
	 * } catch (Exception e) { e.printStackTrace(); }
	 * 
	 * }
	 * 
	 * @FXML public void agentButton(ActionEvent event) { try {
	 * 
	 * Parent root = FXMLLoader.load(getClass().getResource("CreateAgent.fxml"));
	 * Stage stage = new Stage(); stage.setScene(new Scene(root)); stage.show();
	 * agentButton.getScene().getWindow().hide();
	 * 
	 * 
	 * } catch (Exception e) { e.printStackTrace(); } }
	 */
}
