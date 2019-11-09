package oop2.storages.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import oop2.storages.Agent;
import oop2.storages.Category;
import oop2.storages.Owner;
import oop2.storages.StorageType;
import oop2.storages.User;

public class AdminController implements Initializable {

	// create session factory
	SessionFactory factory = new Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(User.class)
			.buildSessionFactory();

	// create session
	Session session = factory.getCurrentSession();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		List<StorageType> types = new ArrayList<>();
		List<Category> categories = new ArrayList<>();

		// start a transaction
		session.beginTransaction();

		types = session.createQuery("from StorageType s").list();

		categories = session.createQuery("from Category s").list();

		stTypes.getItems().clear();
		stTypes.getItems().addAll(types);
		stCategories.getItems().clear();
		stCategories.getItems().addAll(categories);

	}

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

	@FXML
	Button crOwnBtn;

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
	Button crAgBtn;

	@FXML
	public void createAgent(ActionEvent event) {
		String account = agentAccountName.getText();
		String password = agentAccountPassword.getText();
		String name = agentName.getText();
		String pin = agentPin.getText();
		String commission = agentCommission.getText();

		if (!account.isEmpty() && !password.isEmpty() && !name.isEmpty() && !pin.isEmpty() && !commission.isEmpty()) {
			System.out.println(account + password + name + pin);

			/*
			 * // create session factory SessionFactory factory = new
			 * Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(User.class)
			 * .buildSessionFactory();
			 * 
			 * // create session Session session = factory.getCurrentSession();
			 */

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
					Agent tempAgent = new Agent(tempUser, Double.parseDouble(agentCommission.getText()));
					session.save(tempAgent);
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
	TextField storageCategory;

	@FXML
	ListView<Category> stCategories;

	@FXML
	Button crStCategoryBtn;

	@FXML
	public void addCategory(ActionEvent event) {
		String category = storageCategory.getText();
		Category tempCategory = new Category(category);
		session.save(tempCategory);
		// commit transaction
		session.getTransaction().commit();

	}

	//
	// Type Elements
	//
	@FXML
	TextField storageType;

	@FXML
	ListView<StorageType> stTypes;

	@FXML
	Button crStTypeBtn;

	@FXML
	public void addType(ActionEvent event) {
		String type = storageType.getText();
		StorageType tempType = new StorageType(type);
		session.save(tempType);
		// commit transaction
		session.getTransaction().commit();

	}

	public void keyPressed(KeyEvent event) {

		Control[] focusOrder = new Control[] { ownerAccountName, ownerAccountPassword, ownerName, ownerPin, crOwnBtn,
				agentAccountName, agentAccountPassword, agentName, agentPin, agentCommission, crAgBtn };

		for (int i = 0; i < focusOrder.length - 1; i++) {
			Control nextControl = focusOrder[i + 1];
			focusOrder[i].addEventHandler(ActionEvent.ACTION, e -> nextControl.requestFocus());
		}

	}

}
