package oop2.storages.view;

import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.controlsfx.control.CheckComboBox;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import oop2.storages.Owner;
import oop2.storages.Storage;
import oop2.storages.StorageType;
import oop2.storages.User;
import validations.Validation;
import oop2.storages.Agent;
import oop2.storages.Category;
import oop2.storages.Contract;
import oop2.storages.HibernateUtility;
import oop2.storages.Notification;

public class AdminController implements Initializable {

	SessionFactory factory = HibernateUtility.getSessionFactory();
	ObservableList<Category> categoryList;
	ObservableList<StorageType> typeList;
	ObservableList<User> usersList;
	ObservableList<Owner> ownerList;
	ObservableList<Agent> agentList;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Logger logger = Logger.getLogger(AdminController.class);
		logger.info("Admin Logged");

		loadTypeList();
		loadCategoryList();
		loadUsersList();
		loadCreateStorage();
		loadCreateContract();
	}

	//zarejdave na spisuk s tipove sklad
	public void loadTypeList() {
		Session session = factory.getCurrentSession();
		session.beginTransaction();

		Query<StorageType> query = session.createQuery("from StorageType s");
		typeList = FXCollections.observableArrayList(query.list());
		stTypes.setItems(typeList);
		session.getTransaction().commit();
	}

	//zarejdane spisuk s kategorii skladove
	public void loadCategoryList() {
		Session session = factory.getCurrentSession();
		session.beginTransaction();

		Query<Category> query = session.createQuery("from Category s");
		categoryList = FXCollections.observableArrayList(query.list());
		stCategories.setItems(categoryList);
		session.getTransaction().commit();
	}

	@FXML
	GridPane gridPane;

	@FXML
	CheckComboBox<Agent> checkComboBox;

	//zarejdnae na poletata za suzdavane na sklad
	public void loadCreateStorage() {
		Session session = factory.getCurrentSession();
		session.beginTransaction();

		comboOwner.setItems(ownerList);

		Query<Agent> agentQuery = session.createQuery("from Agent s ");
		agentList = FXCollections.observableArrayList(agentQuery.list());
		checkComboBox = new CheckComboBox<Agent>(agentList);
		checkComboBox.setId("checkComboBox");
		gridPane.add(checkComboBox, 1, 2);

		// tuka sme napravili da e po edin red zashtoto veche sa napraveni querita v
		// loadCategory/Type
		comboCategory.setItems(categoryList);

		comboType.setItems(typeList);

		session.getTransaction().commit();
	}

	//zarejdane na poletata za suzdavane na dogovor
	public void loadCreateContract() {
		Session session = factory.getCurrentSession();
		session.beginTransaction();

		conAgentCombo.setItems(agentList);

		dateContract.setDayCellFactory(picker -> new DateCell() {
			public void updateItem(LocalDate date, boolean empty) {
				super.updateItem(date, empty);
				LocalDate today = LocalDate.now();
				setDisable(empty || date.compareTo(today) < 1);
			}
		});

		priceContract.textProperty().addListener((observable, oldValue, newValue) -> {
			if (dateContract.getValue() != null && !priceContract.getText().isEmpty()) {
				fullPrice.setText(""
						+ Double.parseDouble(newValue) * LocalDate.now().until(dateContract.getValue(), ChronoUnit.DAYS)
						+ "");
			} else
				fullPrice.clear();
		});

		dateContract.valueProperty().addListener((observable, oldValue, newValue) -> {
			System.out.println(priceContract.getText());
			if (dateContract.getValue() != null) {
				if (!priceContract.getText().isEmpty()) {
					fullPrice.setText("" + Double.parseDouble(priceContract.getText())
							* LocalDate.now().until(newValue, ChronoUnit.DAYS) + "");
				} else
					fullPrice.clear();
			}
		});

		session.getTransaction().commit();
	}

	@FXML
	TableView<User> usersTable;

	@FXML
	TableColumn<User, String> nameColumn;

	@FXML
	TableColumn<User, String> accNameColumn;

	@FXML
	TableColumn<User, String> pinColumn;

	@FXML
	TextField searchUser;

	@FXML
	RadioButton showAgRadio;

	@FXML
	RadioButton showOwnRadio;

	//nachalno nastroivane na taba s profili
	public void showProfiles() {

		if (showAgRadio.isSelected()) {
			usersList.clear();
			for (Agent agent : agentList) {
				usersList.add(agent.getUser());
			}
			usersTable.setItems(usersList);
		} else if (showOwnRadio.isSelected()) {
			usersList.clear();
			for (Owner owner : ownerList) {
				usersList.add(owner.getUser());
			}
			usersTable.setItems(usersList);
		}

		FilteredList<User> filteredData = new FilteredList<>(usersList, p -> true);
		searchUser.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredData.setPredicate(user -> {
				if (newValue == null || newValue.isEmpty()) {
					return true;
				}

				String lowerCaseFilter = newValue.toLowerCase();

				if (user.getPersonName().toLowerCase().contains(lowerCaseFilter)) {
					return true;
				} else if (user.getAccountName().toLowerCase().contains(lowerCaseFilter)) {
					return true;
				} else if (user.getPin().toLowerCase().contains(lowerCaseFilter)) {
					return true;
				}
				return false;

			});
		});

		SortedList<User> sortedData = new SortedList<>(filteredData);
		sortedData.comparatorProperty().bind(usersTable.comparatorProperty());
		searchUser.clear();
		usersTable.setItems(sortedData);

	}

	//zarejdane na taba s profili
	public void loadUsersList() {
		Session session = factory.getCurrentSession();
		session.beginTransaction();

		ToggleGroup toggleGroup = new ToggleGroup();
		showAgRadio.setToggleGroup(toggleGroup);
		showOwnRadio.setToggleGroup(toggleGroup);

		Query<User> query = session.createQuery("from User s");
		usersList = FXCollections.observableArrayList(query.list());

		Query<Owner> queryOwn = session.createQuery("from Owner s");
		ownerList = FXCollections.observableArrayList(queryOwn.list());

		Query<Agent> queryAg = session.createQuery("from Agent s");
		agentList = FXCollections.observableArrayList(queryAg.list());

		nameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("personName"));
		accNameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("accountName"));
		pinColumn.setCellValueFactory(new PropertyValueFactory<User, String>("pin"));

		usersTable.setItems(usersList);

		FilteredList<User> filteredData = new FilteredList<>(usersList, p -> true);
		searchUser.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredData.setPredicate(user -> {
				if (newValue == null || newValue.isEmpty()) {
					return true;
				}

				String lowerCaseFilter = newValue.toLowerCase();

				if (user.getPersonName().toLowerCase().contains(lowerCaseFilter)) {
					return true;
				} else if (user.getAccountName().toLowerCase().contains(lowerCaseFilter)) {
					return true;
				} else if (user.getPin().toLowerCase().contains(lowerCaseFilter)) {
					return true;
				}
				return false;

			});
		});

		SortedList<User> sortedData = new SortedList<>(filteredData);
		sortedData.comparatorProperty().bind(usersTable.comparatorProperty());
		usersTable.setItems(sortedData);

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
	Label ownAccNameError;

	@FXML
	Label ownAccPassError;

	@FXML
	Label ownerNameError;

	@FXML
	Label ownerPinError;

	//suzdavane na profil na sobstvenik
	public void createOwner() {
		String account = ownerAccountName.getText();
		String password = ownerAccountPassword.getText();
		String name = ownerName.getText();
		String pin = ownerPin.getText();

		boolean accNameValid = Validation.textAlphabet(ownerAccountName, ownAccNameError, "Enter Valid Account Name!");
		boolean passValid = Validation.textAlphabet(ownerAccountPassword, ownAccPassError, "Enter Valid Password!");
		boolean nameValid = Validation.textAlphabetFirstCapital(ownerName, ownerNameError,
				"Only Letters, First Capital! Max 25!");
		boolean pinValid = Validation.textPin(ownerPin, ownerPinError, "Only Numbers! Must Be 10!");

		if (accNameValid && passValid && nameValid && pinValid) {

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

				// dobavqne i zapisvane v log faila
				ownerList.add(tempOwner);
				usersList.add(tempUser);

				Logger logger = Logger.getLogger(AdminController.class);
				logger.info("Admin created owner " + tempOwner.getUser().getAccountName() + " "
						+ tempOwner.getUser().getAccountPassword());

				ownerAccountName.clear();
				ownerAccountPassword.clear();
				ownerName.clear();
				ownerPin.clear();
			} else {
				if (checkUser.getAccountName().equals(account)) {
					System.out.println("Account name Duplicate");
					ownAccNameError.setText("Account already exists");
				} else if (checkUser.getPin().equals(pin)) {
					System.out.println("PIN Duplicate");
					ownerPinError.setText("There's a person with this PIN");
				}
			}

			// commit transaction
			session.getTransaction().commit();

		} else {
			System.out.println("Don't leave empty fields!");
		}
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
	Label agAccNameError;

	@FXML
	Label agAccPassError;

	@FXML
	Label agNameError;

	@FXML
	Label agPinError;

	@FXML
	Label agCommissionError;

	@FXML
	Button crAgBtn;

	//suzdavane na profil na agent
	public void createAgent() {
		String account = agentAccountName.getText();
		String password = agentAccountPassword.getText();
		String name = agentName.getText();
		String pin = agentPin.getText();

		boolean accNameValid = Validation.textAlphabet(agentAccountName, agAccNameError, "Max 25!");
		boolean passValid = Validation.textAlphabet(agentAccountPassword, agAccPassError, "Max 25!");
		boolean nameValid = Validation.textAlphabetFirstCapital(agentName, agNameError,
				"Only Letters, First Capital, Max 25!");
		boolean pinValid = Validation.textPin(agentPin, agPinError, "Only Numbers, Must Be 10!");
		boolean commissionValid = Validation.textCommission(agentCommission, agCommissionError, "Enter Number!");

		if (accNameValid && passValid && nameValid && pinValid && commissionValid) {
			Double commission = Double.parseDouble(agentCommission.getText());
			System.out.println(account + password + name + pin);

			User tempUser = new User(account, password, name, pin);
			Agent tempAgent = new Agent(tempUser, commission);

			Session session = factory.getCurrentSession();
			session.beginTransaction();

			User checkUser = (User) session
					.createQuery("from User s where s.accountName='" + account + "' OR s.pin='" + pin + "'")
					.uniqueResult();

			System.out.println(checkUser);

			if (checkUser == null) {
				session.save(tempUser);
				session.save(tempAgent);

				// dobavqne v spisuka i zapisvane v log faila
				agentList.add(tempAgent);
				usersList.add(tempUser);

				Logger logger = Logger.getLogger(AdminController.class);
				logger.info("Admin created agent " + tempAgent.getUser().getAccountName() + " "
						+ tempAgent.getUser().getAccountPassword());

				agentAccountName.clear();
				agentAccountPassword.clear();
				agentName.clear();
				agentPin.clear();
				agentCommission.clear();
			} else {
				if (checkUser.getAccountName().equals(account)) {
					System.out.println("Account name Duplicate");
					agAccNameError.setText("Account already exists");
				} else if (checkUser.getPin().equals(pin)) {
					System.out.println("PIN Duplicate");
					agPinError.setText("There's a person with this PIN");
				}
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
	Label categoryError;

	//dobavane na kategoriq
	public void addCategory() {
		boolean categoryValid = Validation.textLetters(storageCategory, categoryError, "Only Letters, Max 30!");
		if (categoryValid) {
			Session session = factory.getCurrentSession();
			session.beginTransaction();
			String category = storageCategory.getText();

			Category cat = (Category) session.createQuery("from Category s where s.categoryName='" + category + "'")
					.uniqueResult();

			if (cat == null) {
				Category tempCategory = new Category(category);
				categoryList.add(tempCategory);
				session.save(tempCategory);

				// tuka dobavqme v log faila
				Logger logger = Logger.getLogger(AdminController.class);
				logger.info("Admin created category " + tempCategory.getCategoryName());

				storageCategory.clear();
			} else {
				categoryError.setText("Such category already exists!");
			}
			session.getTransaction().commit();
		}
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
	Label typeError;

	//dobavanq na tip sklad
	public void addType() {
		boolean typeValid = Validation.textLetters(storageType, typeError, "Only Letters, Max 30!");
		if (typeValid) {
			Session session = factory.getCurrentSession();
			session.beginTransaction();
			String type = storageType.getText();

			StorageType ty = (StorageType) session.createQuery("from StorageType s where s.typeName='" + type + "'")
					.uniqueResult();

			if (ty == null) {
				StorageType tempType = new StorageType(type);
				typeList.add(tempType);
				session.save(tempType);

				// tuka dobavqme v log faila
				Logger logger = Logger.getLogger(AdminController.class);
				logger.info("Admin created type " + tempType.getTypeName());

				storageType.clear();
			} else {
				typeError.setText("Such type already exists!");
			}

			session.getTransaction().commit();
		}
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

	@FXML
	Label stOwnerError;

	@FXML
	Label stAgentError;

	@FXML
	Label stTypeError;

	@FXML
	Label stCategoryError;

	@FXML
	Label stAddressError;

	@FXML
	Label clmCondError;

	@FXML
	Label stSizeError;

	//suzdavane na sklad
	public void createStorage() {
		boolean addressValid = Validation.textAddress(storageAddress, stAddressError, "Enter Valid Address!");
		boolean clmCondValid = Validation.textAddress(stClmConditions, clmCondError, "Enter Valid Conditions!");
		boolean sizeValid = Validation.textCommission(storageSize, stSizeError, "Enter Valid Size!");
		boolean ownerValid = Validation.textCombo(comboOwner, stOwnerError, "Select Owner!");
		boolean typeValid = Validation.textCombo(comboType, stTypeError, "Select Type!");
		boolean categoryValid = Validation.textCombo(comboCategory, stCategoryError, "Select Category!");

		if (addressValid && clmCondValid && sizeValid && ownerValid && typeValid && categoryValid) {
			Session session = factory.getCurrentSession();
			session.beginTransaction();

			Owner owner = comboOwner.getValue();
			ObservableList<Agent> agentList = FXCollections
					.observableArrayList(checkComboBox.getCheckModel().getCheckedItems());
			StorageType chosenType = (StorageType) comboType.getValue();
			Category chosenCategory = (Category) comboCategory.getValue();
			String storageAdr = storageAddress.getText();
			String climateConditions = stClmConditions.getText();
			Double storageSze = Double.parseDouble(storageSize.getText().toString());

			Storage tempStorage = new Storage(owner, chosenType, chosenCategory, storageSze, climateConditions,
					storageAdr, agentList);

			// tuka dobavihme izvestie kogato se suzdade da se izprati izvestiq kum agentite
			ObservableList<Notification> notificationList = FXCollections.observableArrayList();

			for (Agent agent : agentList) {
				Notification noti = new Notification(agent.getUser(),
						(LocalDate.now() + ": Storage " + tempStorage.getStorageAddress() + " is free for sale"));
				notificationList.add(noti);
			}

			List<Notification> notificationResult = session
					.createQuery("from Notification s where s.notificationStatus = 1").list();
			System.out.println(notificationResult);
			for (Notification notification : notificationList) {
				if (!notificationResult.contains(notification))
					System.out.println(notification);
				session.save(notification);
			}

			System.out.println(tempStorage);

			session.save(tempStorage);

			// tuka dobavqme v log faila
			Logger logger = Logger.getLogger(AdminController.class);
			logger.info("Admin created storage " + tempStorage.getStorageID() + " " + tempStorage.getStorageAddress());

			comboOwner.setValue(null);
			checkComboBox.getCheckModel().clearChecks();
			comboType.setValue(null);
			comboCategory.setValue(null);
			storageAddress.clear();
			stClmConditions.clear();
			storageSize.clear();

			session.getTransaction().commit();
		}
	}

	@FXML
	ComboBox<Agent> conAgentCombo;

	@FXML
	ComboBox<Storage> storageCombo;

	@FXML
	TextField renterName;

	@FXML
	TextField renterPin;

	@FXML
	DatePicker dateContract;

	@FXML
	TextField priceContract;

	@FXML
	TextField fullPrice;

	@FXML
	Label chStorageError;

	@FXML
	Label renterNameError;

	@FXML
	Label renterPinError;

	@FXML
	Label contrEndDateError;

	@FXML
	Label stSinglePriceError;

	@FXML
	Label conAgentError;

	//pri izbor na agent se zadava da izlizat negovite skladove v comboboxa za izbor na sklad
	public void selectAgent() {
		Session session = factory.getCurrentSession();
		session.beginTransaction();
		Agent chosenAgent = conAgentCombo.getValue();

		if (chosenAgent != null) {
			Agent agent = (Agent) session
					.createQuery("from Agent s where s.agentID = '" + chosenAgent.getAgentID() + "' ").uniqueResult();
			ObservableList<Storage> storageList = FXCollections.observableArrayList(agent.getStorageList());
			List<Storage> availableStorages = new ArrayList<>();
			for (Storage storage : storageList) {
				if (storage.getStorageStatus() == false)
					availableStorages.add(storage);
			}

			storageCombo.getItems().setAll(availableStorages);
		}

		session.getTransaction().commit();
	}

	//suzdavane na dogovor
	public void createContract() {
		boolean agentValid = Validation.textCombo(conAgentCombo, conAgentError, "Select Agent!");
		boolean storageValid = Validation.textCombo(storageCombo, chStorageError, "Select Storage!");
		boolean renterNameValid = Validation.textAlphabetFirstCapital(renterName, renterNameError,
				"Only Letters, First Capital! Max 25!");
		boolean renterPinValid = Validation.textPin(renterPin, renterPinError, "Only Numbers! Must be 10!");
		boolean endDateValid = Validation.textDate(dateContract, contrEndDateError, "Choose Date!");
		boolean dayPriceValid = Validation.textCommission(priceContract, stSinglePriceError, "Enter Valid Price!");

		if (agentValid && storageValid && renterNameValid && renterPinValid && endDateValid && dayPriceValid) {
			Session session = factory.getCurrentSession();
			session.beginTransaction();

			Agent chosenAgent = conAgentCombo.getValue();

			Storage choosenStorage = storageCombo.getValue();
			String renterNme = renterName.getText();
			String renterPn = renterPin.getText();
			LocalDate endDate = dateContract.getValue();
			Double pricePerMonth = Double.parseDouble(priceContract.getText());

			if (choosenStorage.getStorageStatus() == false) {
				Contract contract = new Contract(chosenAgent, choosenStorage, null, endDate.plusDays(1), pricePerMonth,
						renterNme, renterPn);

				Storage tempStorage = choosenStorage;
				tempStorage.setStorageStatus(true);

				// tuka dobavihme notification za otdaden sklad
				Notification noti = new Notification(choosenStorage.getOwner().getUser(),
						(LocalDate.now() + ": Storage " + tempStorage.getStorageAddress() + " is now rented"));

				System.out.println(noti);
				session.save(noti);

				System.out.println(tempStorage);
				System.out.println(tempStorage.getStorageStatus());
				session.update(tempStorage);
				session.save(contract);

				// tuka dobavqme v log faila
				Logger logger = Logger.getLogger(AdminController.class);
				logger.info("Admin created contract with ID: " + contract.getContractID());

				storageCombo.setValue(null);
				renterName.clear();
				renterPin.clear();
				dateContract.setValue(null);
				priceContract.clear();

			} else {
				System.out.println("Storage is already rented");
			}

			session.getTransaction().commit();
			// slagame go nakraq poneje shte izvika eventa i shte ima transaction already
			// active
			conAgentCombo.setValue(null);
		}
	}

	public void keyPressed(KeyEvent event) {

		Control[] focusOrder = new Control[] { ownerAccountName, ownerAccountPassword, ownerName, ownerPin,
				agentAccountName, agentAccountPassword, agentName, agentPin, agentCommission };

		for (int i = 0; i < focusOrder.length - 1; i++) {
			Control nextControl = focusOrder[i + 1];
			focusOrder[i].addEventHandler(ActionEvent.ACTION, e -> nextControl.requestFocus());
		}
	}
}
