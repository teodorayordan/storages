package oop2.storages.view;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.Notifications;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import oop2.storages.Agent;
import oop2.storages.Category;
import oop2.storages.Contract;
import oop2.storages.HibernateUtility;
import oop2.storages.Notification;
import oop2.storages.Owner;
import oop2.storages.Storage;
import oop2.storages.StorageType;
import validations.Validation;

public class OwnerController implements Initializable {

	SessionFactory factory = HibernateUtility.getSessionFactory();

	ObservableList<Agent> agentList;
	ObservableList<StorageType> typeList;
	ObservableList<Category> categoryList;
	ObservableList<Storage> storageList;
	ObservableList<Agent> allOwnerAgents;
	ObservableList<Contract> agentsContracts;

	@FXML
	TableView<Storage> storageTable;

	@FXML
	TableColumn<Storage, String> storageAddressColumn;

	@FXML
	TableColumn<Storage, String> storageCategoryColumn;

	@FXML
	TableColumn<Storage, String> storageTypeColumn;

	@FXML
	TextField searchStorage;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Logger logger = Logger.getLogger(AdminController.class);
		logger.info("Owner " + Singleton.getInstance().getOwner().getUser().getPersonName() + " Logged");

		showProfileInfo();
		showOwnedStorages();
		loadCreateStorage();
		onSelectOwnedStoragesTab();
		onSelectOwnerAgentsTab();
		showNotifications();
		loadOwnerAgents();
		showAvailableStorages();
	}

	public void showProfileInfo() {
		nameText.setText(Singleton.getInstance().getOwner().getUser().getPersonName());
		accountNameText.setText(Singleton.getInstance().getOwner().getUser().getAccountName());
	}

	public void showOwnedStorages() {
		Session session = factory.getCurrentSession();
		session.beginTransaction();

		List<Storage> query = session.createQuery(
				"from Storage s where id_owner = '" + Singleton.getInstance().getOwner().getUser().getUserID() + "'")
				.list();
		storageList = FXCollections.observableArrayList(query);

		// tuka definirame vuv vsqka kolona kakvo ima kato towa v skobite e imeto na
		// promenlivata ot klasa na obekta
		storageAddressColumn.setCellValueFactory(new PropertyValueFactory<Storage, String>("storageAddress"));
		storageCategoryColumn.setCellValueFactory(new PropertyValueFactory<Storage, String>("category"));
		storageTypeColumn.setCellValueFactory(new PropertyValueFactory<Storage, String>("storageType"));

		storageTable.setItems(storageList);

		FilteredList<Storage> filteredData = new FilteredList<>(storageList, p -> true);
		searchStorage.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredData.setPredicate(storage -> {
				if (newValue == null || newValue.isEmpty()) {
					return true;
				}

				String lowerCaseFilter = newValue.toLowerCase();

				if (storage.getStorageAddress().toLowerCase().contains(lowerCaseFilter)) {
					return true;
				} else if (storage.getCategory().getCategoryName().toLowerCase().contains(lowerCaseFilter)) {
					return true;
				} else if (storage.getStorageType().getTypeName().toLowerCase().contains(lowerCaseFilter)) {
					return true;
				}
				return false;
			});
		});

		SortedList<Storage> sortedData = new SortedList<>(filteredData);
		sortedData.comparatorProperty().bind(storageTable.comparatorProperty());
		storageTable.setItems(sortedData);

		session.getTransaction().commit();
	}

	@FXML
	GridPane gridPane;

	@FXML
	CheckComboBox<Agent> checkComboBox;

	public void loadCreateStorage() {
		Session session = factory.getCurrentSession();
		session.beginTransaction();

		Query<Agent> agentQuery = session.createQuery("from Agent s ");
		agentList = FXCollections.observableArrayList(agentQuery.list());
		checkComboBox = new CheckComboBox<Agent>(agentList);
		checkComboBox.setId("checkComboBox");
		gridPane.add(checkComboBox, 1, 0);

		Query<StorageType> typeQuery = session.createQuery("from StorageType s ");
		typeList = FXCollections.observableArrayList(typeQuery.list());
		comboType.getItems().setAll(typeList);

		Query<Category> categoryQuery = session.createQuery("from Category s ");
		categoryList = FXCollections.observableArrayList(categoryQuery.list());
		comboCategory.getItems().setAll(categoryList);

		session.getTransaction().commit();
	}

	public void showNotifications() {
		Runnable task = new Runnable() {
			@Override
			public void run() {
				Session session = factory.getCurrentSession();
				session.beginTransaction();

				Query<Notification> notiQuery = session.createQuery("from Notification s where s.user = '"
						+ Singleton.getInstance().getOwner().getOwnerID() + "' " + "and s.notificationStatus = 1");
				ObservableList<Notification> notifications = FXCollections.observableArrayList(notiQuery.list());

				System.out.println(notifications);

				for (Notification noti : notifications) {
					Platform.runLater(() -> {
						Notifications.create().hideAfter(javafx.util.Duration.minutes(60))
								.title(Singleton.getInstance().getUser().getAccountName() + " Reminder")
								.text(noti.getNotificationText()).showInformation();
					});
					Notification bufNoti = noti;
					bufNoti.setNotificationStatus(false);
					session.update(bufNoti);
				}

				session.getTransaction().commit();
			}
		};

		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleAtFixedRate(task, 3, 10, TimeUnit.SECONDS);
	}

	public void loadOwnerAgents() {
		Session session = factory.getCurrentSession();
		session.beginTransaction();

		List<Storage> query = session.createQuery(
				"from Storage s where id_owner = '" + Singleton.getInstance().getOwner().getUser().getUserID() + "'")
				.list();
		storageList = FXCollections.observableArrayList(query);

		allOwnerAgents = FXCollections.observableArrayList();

		ObservableSet<Agent> uniqueAgents = FXCollections.observableSet();

		for (Storage storage : storageList) {
			allOwnerAgents.addAll(storage.getAgentList());
		}

		agNameColumn.setCellValueFactory(new PropertyValueFactory<Agent, String>("personName"));
		agCommColumn.setCellValueFactory(new PropertyValueFactory<Agent, String>("commission"));
		agRatingColumn.setCellValueFactory(new PropertyValueFactory<Agent, String>("rating"));

		uniqueAgents.addAll(allOwnerAgents);
		agentInfoTable.setItems(FXCollections.observableArrayList(uniqueAgents));

		FilteredList<Agent> filteredData = new FilteredList<>(FXCollections.observableArrayList(uniqueAgents),
				p -> true);
		searchAgent.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredData.setPredicate(agent -> {
				if (newValue == null || newValue.isEmpty()) {
					return true;
				}

				String lowerCaseFilter = newValue.toLowerCase();

				if (agent.getPersonName().toLowerCase().contains(lowerCaseFilter)) {
					return true;
				} else if (agent.getCommission().toString().contains(lowerCaseFilter)) {
					return true;
				} else if (agent.getRating().toString().contains(lowerCaseFilter)) {
					return true;
				}
				return false;

			});
		});

		SortedList<Agent> sortedData = new SortedList<>(filteredData);
		sortedData.comparatorProperty().bind(agentInfoTable.comparatorProperty());
		agentInfoTable.setItems(sortedData);

		session.getTransaction().commit();
	}

	@FXML
	Button editPrBtn;

	@FXML
	Label nameText;

	@FXML
	Label accountNameText;

	public void editProfile() {

		try {
			Parent root = FXMLLoader.load(getClass().getResource("EditProfile.fxml"));
			Stage stage = new Stage();
			stage.setScene(new Scene(root));
			stage.setTitle("Edit Profile");
			stage.setResizable(false);
			stage.getIcons().add(new Image("/pictures/storage.png"));
			stage.show();

			stage.setOnCloseRequest((WindowEvent event1) -> {
				showProfileInfo();
			});

		} catch (Exception e) {
			e.printStackTrace();
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
	ComboBox<Agent> comboAgent;

	@FXML
	ComboBox<StorageType> comboType;

	@FXML
	ComboBox<Category> comboCategory;

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

	public void createStorage() {

		boolean addressValid = Validation.textAddress(storageAddress, stAddressError, "Enter Valid Address!");
		boolean clmCondValid = Validation.textAddress(stClmConditions, clmCondError, "Enter Valid Conditions!");
		boolean sizeValid = Validation.textCommission(storageSize, stSizeError, "Enter Valid Size!");
		boolean agentValid = Validation.textCheckCombo(checkComboBox, stAgentError, "Select Agent!");
		boolean typeValid = Validation.textCombo(comboType, stTypeError, "Select Type!");
		boolean categoryValid = Validation.textCombo(comboCategory, stCategoryError, "Select Category!");

		if (addressValid && clmCondValid && sizeValid && agentValid && typeValid && categoryValid) {
			Session session = factory.getCurrentSession();
			session.beginTransaction();

			Owner owner = Singleton.getInstance().getOwner();
			ObservableList<Agent> agentList = FXCollections
					.observableArrayList(checkComboBox.getCheckModel().getCheckedItems());
			StorageType chosenType = (StorageType) comboType.getValue();
			Category chosenCategory = (Category) comboCategory.getValue();
			String storageAdr = storageAddress.getText();
			String climateConditions = stClmConditions.getText();
			Double storageSze = Double.parseDouble(storageSize.getText().toString());

			System.out.println(storageAdr);

			Storage tempStorage = new Storage(owner, chosenType, chosenCategory, storageSze, climateConditions,
					storageAdr, agentList);

			System.out.println(tempStorage);

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

			storageList.add(tempStorage);
			allOwnerAgents.addAll(tempStorage.getAgentList());
			session.save(tempStorage);

			Logger logger = Logger.getLogger(AdminController.class);
			logger.info(Singleton.getInstance().getUser().getAccountName() + " created storage "
					+ tempStorage.getStorageID() + " " + tempStorage.getStorageAddress());

			checkComboBox.getCheckModel().clearChecks();
			comboType.setValue(null);
			comboCategory.setValue(null);
			storageAddress.clear();
			stClmConditions.clear();
			storageSize.clear();

			// commit transaction
			session.getTransaction().commit();
		}
	}

	@FXML
	DatePicker startDate;

	@FXML
	DatePicker endDate;

	public void showAvailableStorages() {
		// TODO testove
		Session session = factory.getCurrentSession();
		session.beginTransaction();

		LocalDate sDate = startDate.getValue();
		LocalDate eDate = endDate.getValue();

		ObservableList<Storage> dateStorageList = FXCollections.observableArrayList();

		if (sDate != null && eDate != null) {
			System.out.println(sDate);
			System.out.println(eDate);

			List<Storage> query = session.createQuery("select storage from Contract s where ((s.startDate between '"
					+ sDate + "' and '" + eDate + "') or" + " (s.endDate between '" + sDate + "' and '" + eDate
					+ "') or " + "((s.startDate < '" + sDate + "' and s.endDate > '" + eDate + "')))").list();

			dateStorageList = FXCollections.observableArrayList(query);

			for (Storage storage : storageList) {
				storage.setStatusByDate(false);
			}

			for (Storage storage : dateStorageList) {
				int index = storageList.indexOf(storage);
				if (index >= 0)
					storageList.get(index).setStatusByDate(true);
			}
		} else {

			for (Storage storage : storageList) {
				storage.setStatusByDate(null);
			}

			storageTable.setItems(storageList);
		}

		Platform.runLater(() -> {
			storageTable.setRowFactory(tv -> new TableRow<Storage>() {
				@Override
				public void updateItem(Storage item, boolean empty) {
					super.updateItem(item, empty);
					if (item == null) {
						setStyle("");
					} else if (item.getStatusByDate() == null) {
						setStyle("");
					} else if (item.getStatusByDate()) {
						setStyle("-fx-background-color: tomato;");
					} else {
						setStyle("-fx-background-color: palegreen;");
					}
				}
			});
		});

		FilteredList<Storage> filteredData = new FilteredList<>(storageList, p -> true);
		searchStorage.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredData.setPredicate(storage -> {
				if (newValue == null || newValue.isEmpty()) {
					return true;
				}

				String lowerCaseFilter = newValue.toLowerCase();

				if (storage.getStorageAddress().toLowerCase().contains(lowerCaseFilter)) {
					return true;
				} else if (storage.getCategory().getCategoryName().toLowerCase().contains(lowerCaseFilter)) {
					return true;
				} else if (storage.getStorageType().getTypeName().toLowerCase().contains(lowerCaseFilter)) {
					return true;
				} else if (storage.getStorageStatus().toString().contains(lowerCaseFilter)) {
					return true;
				}
				return false;
			});
		});

		storageTable.getSelectionModel().getSelectedItem();

		SortedList<Storage> sortedData = new SortedList<>(filteredData);
		sortedData.comparatorProperty().bind(storageTable.comparatorProperty());
		storageTable.setItems(sortedData);
		storageTable.refresh();

		session.getTransaction().commit();
	}

	@FXML
	Button editStorageBtn;

	public void editStorage() {
		if (storageTable.getSelectionModel().getSelectedItem() != null) {
			try {
				Storage tempStorage = storageTable.getSelectionModel().getSelectedItem();
				Singleton.getInstance().setStorage(tempStorage);
				anp2.getChildren().clear();
				anp2.getChildren().add(FXMLLoader.load(getClass().getResource("EditStorage.fxml")));
				borderOwnedSt.setRight(anp2);

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else
			System.out.println("No selection!");
	}

	@FXML
	Button showStBtn;

	@FXML
	BorderPane borderOwnedSt;

	AnchorPane anp = new AnchorPane();

	AnchorPane anp2 = new AnchorPane();

	SplitPane spl = new SplitPane();

	SplitPane spl2 = new SplitPane();

	public void showStorage() {
		if (storageTable.getSelectionModel().getSelectedItem() != null) {
			try {
				Storage tempStorage = storageTable.getSelectionModel().getSelectedItem();
				Singleton.getInstance().setStorage(tempStorage);
				anp.getChildren().clear();
				anp.getChildren().add(FXMLLoader.load(getClass().getResource("StorageInfo.fxml")));
				borderOwnedSt.setRight(anp);

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else
			System.out.println("No selection!");
	}

	@FXML
	TextField searchAgent;

	@FXML
	TableView<Agent> agentInfoTable;

	@FXML
	TableColumn<Agent, String> agNameColumn;

	@FXML
	TableColumn<Agent, String> agCommColumn;

	@FXML
	TableColumn<Agent, String> agRatingColumn;

	@FXML
	DatePicker conStDate;

	@FXML
	DatePicker conEndDate;

	@FXML
	TableView<Contract> contractsTable;

	@FXML
	TableColumn<Contract, String> storageColumn;

	@FXML
	TableColumn<Contract, String> stDateColumn;

	@FXML
	TableColumn<Contract, String> endDateColumn;

	@FXML
	TableColumn<Contract, String> renterNameColumn;

	@FXML
	Button contractInfoBtn;

	public void showContracts() {
		if (agentInfoTable.getSelectionModel().getSelectedItem() != null) {
			Session session = factory.getCurrentSession();
			session.beginTransaction();

			Agent tempAgent = agentInfoTable.getSelectionModel().getSelectedItem();
			Singleton.getInstance().setAgent(tempAgent);

			Query<Contract> contrQuery = session.createQuery("from Contract s where id_storage_agent = '"
					+ Singleton.getInstance().getAgent().getAgentID() + "' ");
			agentsContracts = FXCollections.observableArrayList(contrQuery.list());

			storageColumn.setCellValueFactory(new PropertyValueFactory<Contract, String>("storageAddress"));
			stDateColumn.setCellValueFactory(new PropertyValueFactory<Contract, String>("startDate"));
			endDateColumn.setCellValueFactory(new PropertyValueFactory<Contract, String>("endDate"));
			renterNameColumn.setCellValueFactory(new PropertyValueFactory<Contract, String>("renterName"));

			contractsTable.setItems(agentsContracts);

			session.getTransaction().commit();
		} else
			System.out.println("No selection!");
	}

	public void searchContracts() {
		Session session = factory.getCurrentSession();
		session.beginTransaction();

		ObservableList<Contract> dateContractList = FXCollections.observableArrayList();

		LocalDate sDate = conStDate.getValue();
		LocalDate eDate = conEndDate.getValue();

		if (sDate != null && eDate != null) {
			System.out.println(sDate);
			System.out.println(eDate);

			List<Contract> query = session.createQuery("from Contract s where ((s.startDate between '" + sDate
					+ "' and '" + eDate + "') or" + " (s.endDate between '" + sDate + "' and '" + eDate + "') or "
					+ "((s.startDate < '" + sDate + "' and s.endDate > '" + eDate + "'))) " + "and (s.agent = '"
					+ Singleton.getInstance().getAgent().getAgentID() + "')").list();

			dateContractList = FXCollections.observableArrayList(query);
			System.out.println(dateContractList);

			contractsTable.setItems(dateContractList);
		} else
			contractsTable.setItems(agentsContracts);

		session.getTransaction().commit();

	}

	public void showContractInfo() {
		if (contractsTable.getSelectionModel().getSelectedItem() != null) {
			try {
				Contract tempContract = contractsTable.getSelectionModel().getSelectedItem();
				Singleton.getInstance().setContract(tempContract);
				Parent root = FXMLLoader.load(getClass().getResource("ContractInfo.fxml"));
				Stage stage = new Stage();
				stage.setScene(new Scene(root));
				stage.setTitle("Contract Info");
				stage.setResizable(false);
				stage.getIcons().add(new Image("/pictures/storage.png"));
				stage.show();

				stage.setOnCloseRequest((WindowEvent event1) -> {
					showProfileInfo();
				});

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else
			System.out.println("No selection!");
	}

	@FXML
	Tab ownedStoragesTab;

	public void onSelectOwnedStoragesTab() {
		ownedStoragesTab.setOnSelectionChanged(e -> {
			showOwnedStorages();
		});
	}

	@FXML
	Tab checkAgentTab;

	public void onSelectOwnerAgentsTab() {
		checkAgentTab.setOnSelectionChanged(e -> {
			loadOwnerAgents();
		});
	}
}