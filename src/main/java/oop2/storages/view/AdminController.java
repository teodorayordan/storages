package oop2.storages.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.persistence.PostPersist;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import oop2.storages.Owner;
import oop2.storages.Storage;
import oop2.storages.StorageType;
import oop2.storages.User;
import oop2.storages.Agent;
import oop2.storages.Category;
import oop2.storages.HibernateUtility;

public class AdminController implements Initializable {

	SessionFactory factory = HibernateUtility.getSessionFactory();
	ObservableList<Category> categoryList;
	ObservableList<StorageType> typeList;
	

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		loadTypeList();
		loadCategoryList();
	}

	public void loadTypeList() {
		Session session = factory.getCurrentSession();
		session.beginTransaction();
		
		Query<StorageType> query = session.createQuery("from StorageType s");
		typeList = FXCollections.observableArrayList(query.list());
		stTypes.setItems(typeList);
		session.getTransaction().commit();
	}

	public void loadCategoryList() {
		Session session = factory.getCurrentSession();
		session.beginTransaction();
		
		Query<Category> query = session.createQuery("from Category s");
		categoryList = FXCollections.observableArrayList(query.list());
		stCategories.setItems(categoryList);
		session.getTransaction().commit();
	}

	@FXML
	Button crOwnBtn;

	@FXML
	TextField ownerAccountName;

	@FXML
	PasswordField ownerAccountPassword;

	@FXML
	TextField ownerName;

	@FXML
	TextField ownerPin;

	@FXML
	public void createOwner(ActionEvent event) {
		String account = ownerAccountName.getText();
		String password = ownerAccountPassword.getText();
		String name = ownerName.getText();
		String pin = ownerPin.getText();

		if (!account.isEmpty() && !password.isEmpty() && !name.isEmpty() && !pin.isEmpty()) {
			System.out.println(account + password + name + pin);

			User tempUser = new User(account, password, name, pin);

			Session session = factory.getCurrentSession();
			// start a transaction
			session.beginTransaction();

			User checkUser = (User) session
					.createQuery("from User s where s.accountName='" + account + "' OR s.pin='" + pin + "'")
					.uniqueResult();

			System.out.println(checkUser);

			if (checkUser == null) {
				session.save(tempUser);
				Owner tempOwner = new Owner(tempUser);
				session.save(tempOwner);
			} else {
				// ne znam dali raboti
				if (checkUser.getAccountName().equals(account))
					System.out.println("Account name Duplicate");
				else if (checkUser.getPin().equals(pin))
					System.out.println("PIN Duplicate");
				else
					System.out.println("Account name and PIN Duplicate");
			}

			// commit transaction
			session.getTransaction().commit();
		} else
			System.out.println("Dont leave empty fields");
	}

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

			User tempUser = new User(account, password, name, pin);

			Session session = factory.getCurrentSession();
			// start a transaction
			session.beginTransaction();

			User checkUser = (User) session
					.createQuery("from User s where s.accountName='" + account + "' OR s.pin='" + pin + "'")
					.uniqueResult();

			System.out.println(checkUser);

			if (checkUser == null) {
				session.save(tempUser);

				Agent tempAgent = new Agent(tempUser, Double.parseDouble(agentCommission.getText()));
				session.save(tempAgent);
			} else {
				// pak ne znam dali raboti
				if (checkUser.getAccountName().equals(account))
					System.out.println("Account name Duplicate");
				else if (checkUser.getPin().equals(pin))
					System.out.println("PIN Duplicate");
				else
					System.out.println("Account name and PIN Duplicate");
			}

			// commit transaction
			session.getTransaction().commit();
		} else
			System.out.println("Dont leave empty fields");

	}

	@FXML
	TextField storageCategory;

	@FXML
	ListView<Category> stCategories;

	@FXML
	Button crStCategoryBtn;

	@FXML
	Label categoryText;

	@FXML
	public void addCategory(ActionEvent event) {
		Session session = factory.getCurrentSession();
		session.beginTransaction();
		String category = storageCategory.getText();

		Category cat = (Category) session.createQuery("from Category s where s.categoryName='" + category + "'")
				.uniqueResult();

		if (cat == null) {
			Category tempCategory = new Category(category);
			categoryList.add(tempCategory);
			session.save(tempCategory);
		} else {
			categoryText.setVisible(true);

		}

		// commit transaction
		session.getTransaction().commit();
	}

	@FXML
	TextField storageType;

	@FXML
	ListView<StorageType> stTypes;

	@FXML
	Button crStTypeBtn;

	@FXML
	Label typeText;

	@FXML
	public void addType(ActionEvent event) {
		Session session = factory.getCurrentSession();
		session.beginTransaction();
		String type = storageType.getText();

		StorageType ty = (StorageType) session.createQuery("from StorageType s where s.typeName='" + type + "'")
				.uniqueResult();
		if (ty == null) {
			StorageType tempType = new StorageType(type);
			typeList.add(tempType);
			session.save(tempType);
		} else {
			typeText.setVisible(true);
		}

		session.getTransaction().commit();
	}

	@FXML
	Button crStorageBtn;

	@FXML
	TextField storageAddress;

	@FXML
	TextField stClmConditions;

	@FXML
	TextField storageSize;

	@FXML
	ComboBox<Owner> comboOwner;

	@FXML
	ComboBox<Agent> comboAgent;

	@FXML
	ComboBox<StorageType> comboType;

	@FXML
	ComboBox<Category> comboCategory;

	public void createStorage(ActionEvent event) {
		/*
		 * Session session = factory.getCurrentSession(); Owner chosenOwner = (Owner)
		 * comboOwner.getValue(); Agent chosenAgent = (Agent) comboAgent.getValue();
		 * StorageType chosenType = (StorageType) comboType.getValue(); Category
		 * chosenCategory = (Category) comboCategory.getValue(); String storageAdr =
		 * storageAddress.getText(); String climateConditions =
		 * stClmConditions.getText(); Double storageSze =
		 * Double.parseDouble(storageSize.getText().toString());
		 * 
		 * Storage tempStorage = new Storage(chosenOwner, chosenAgent, chosenType,
		 * chosenCategory, storageSze, climateConditions, storageAdr, false);
		 * 
		 * System.out.println(tempStorage);
		 * 
		 * // start a transaction session.beginTransaction();
		 * 
		 * session.save(tempStorage);
		 * 
		 * // commit transaction session.getTransaction().commit();
		 */
	}

	public void keyPressed(KeyEvent event) {

		Control[] focusOrder = new Control[] { ownerAccountName, ownerAccountPassword, ownerName, ownerPin,
				agentAccountName, agentAccountPassword, agentName, agentPin, agentCommission };

		for (int i = 0; i < focusOrder.length - 1; i++) {
			Control nextControl = focusOrder[i + 1];
			focusOrder[i].addEventHandler(ActionEvent.ACTION, e -> nextControl.requestFocus());
		}

		/*
		 * if (ownerPin.isFocused()) { crOwnBtn.setDefaultButton(true); } else if
		 * (agentCommission.isFocused()) { crAgBtn.setDefaultButton(true); }
		 */
	}
}
