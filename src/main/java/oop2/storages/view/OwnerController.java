package oop2.storages.view;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Stream;

import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.Notifications;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import oop2.storages.Agent;
import oop2.storages.Category;
import oop2.storages.Contract;
import oop2.storages.HibernateUtility;
import oop2.storages.Notification;
import oop2.storages.Owner;
import oop2.storages.Storage;
import oop2.storages.StorageType;
import oop2.storages.User;

public class OwnerController implements Initializable {
	
	SessionFactory factory = HibernateUtility.getSessionFactory();

	ObservableList<Agent> agentList;
	ObservableList<StorageType> typeList;
	ObservableList<Category> categoryList;
	ObservableList<Storage> storageList;

	//TODO da se dobavi zaqvka za otdavane pri suzdavane na sklad kum agentite koito sa izbrani i pri redaktirane sushto
	
	
	// definirat se id na tablica i id na kolonite i
	@FXML
	TableView<Storage> storageTable;

	@FXML
	TableColumn<Storage, String> storageAddressColumn;

	@FXML
	TableColumn<Storage, String> storageCategoryColumn;

	@FXML
	TableColumn<Storage, String> storageTypeColumn;

	@FXML
	TableColumn<Storage, String> storageStatusColumn;

	@FXML
	TextField searchStorage;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		showProfileInfo();
		showOwnedStorages();
		loadCreateStorage();
		onSelectOwnedStoragesTab();
		showNotifications();
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

		// tuka definirash vuv vsqka kolona kakvo ima kato towa v skobite e imeto na
		// promenlivata ot klasa na obekta
		storageAddressColumn.setCellValueFactory(new PropertyValueFactory<Storage, String>("storageAddress"));
		storageCategoryColumn.setCellValueFactory(new PropertyValueFactory<Storage, String>("category"));
		storageTypeColumn.setCellValueFactory(new PropertyValueFactory<Storage, String>("storageType"));
		storageStatusColumn.setCellValueFactory(new PropertyValueFactory<Storage, String>("storageStatus"));

		storageTable.setItems(storageList);

		FilteredList<Storage> filteredData = new FilteredList<>(storageList, p -> true);
		searchStorage.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredData.setPredicate(storage -> {
				if (newValue == null || newValue.isEmpty()) {
					return true;
				}

				String lowerCaseFilter = newValue.toLowerCase();

				// kazvash koi poleta da tursi s teq if-ove
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

		// setvash tuka namerenite danni v sorted list i go setvash na table-a
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
		
		/*Query<Agent> agentQuery = session.createQuery("from Agent s ");
		agentList = FXCollections.observableArrayList(agentQuery.list());
		comboAgent.getItems().setAll(agentList);*/

		Query<StorageType> typeQuery = session.createQuery("from StorageType s ");
		typeList = FXCollections.observableArrayList(typeQuery.list());
		comboType.getItems().setAll(typeList);

		Query<Category> categoryQuery = session.createQuery("from Category s ");
		categoryList = FXCollections.observableArrayList(categoryQuery.list());
		comboCategory.getItems().setAll(categoryList);

		session.getTransaction().commit();
	}
	
	public void showNotifications(){
		Session session = factory.getCurrentSession();
		session.beginTransaction();
		
		Query<Notification> notiQuery = session.createQuery("from Notification s where s.user = '"+ Singleton.getInstance().getOwner().getOwnerID() +"' "
				+ "and s.notificationStatus = 1");
		ObservableList<Notification> notifications = FXCollections.observableArrayList(notiQuery.list());
		
		System.out.println(notifications);
		
		for (Notification noti : notifications) {
            Platform.runLater(() -> {
            	Notifications.create() .title("Task Reminder") .text(noti.getNotificationText()) .showWarning();
            });	
        	Notification bufNoti = noti;
        	bufNoti.setNotificationStatus(false);
        	session.update(bufNoti);
		}
		
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
		Session session = factory.getCurrentSession();
		session.beginTransaction();		
		
		Owner owner = Singleton.getInstance().getOwner();
		ObservableList<Agent> agentList = FXCollections.observableArrayList( checkComboBox.getCheckModel().getCheckedItems());
		StorageType chosenType = (StorageType) comboType.getValue();
		Category chosenCategory = (Category) comboCategory.getValue();
		String storageAdr = storageAddress.getText();
		String climateConditions = stClmConditions.getText();
		Double storageSze = Double.parseDouble(storageSize.getText().toString());

		System.out.println(storageAdr);
		
		Storage tempStorage = new Storage(owner, chosenType, chosenCategory, storageSze, climateConditions, storageAdr,
				agentList);

		System.out.println(tempStorage);
		
		//tuka dobavihme izvestie kogato se suzdade da se izprati izvestiq kum agentite
		ObservableList<Notification> notificationList = FXCollections.observableArrayList();
		
		for (Agent agent : agentList) {
			Notification noti = new Notification(agent.getUser(), (LocalDate.now() + ": Storage "+ tempStorage.getStorageAddress() +" is free for sale"));
			notificationList.add(noti);
		}
		
		List<Notification> notificationResult = session.createQuery("from Notification s where s.notificationStatus = 1").list();
		System.out.println(notificationResult);
		for (Notification notification : notificationList) {
			if(!notificationResult.contains(notification))
				System.out.println(notification);
				session.save(notification);
		}

		storageList.add(tempStorage);

		// start a transaction

		session.save(tempStorage);

		// commit transaction
		session.getTransaction().commit();

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
		try {
			Storage tempStorage = storageTable.getSelectionModel().getSelectedItem();
			Singleton.getInstance().setStorage(tempStorage);
			anp.getChildren().clear();
			anp.getChildren().add(FXMLLoader.load(getClass().getResource("StorageInfo.fxml")));
			borderOwnedSt.setRight(anp);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@FXML
	DatePicker startDate;

	@FXML
	DatePicker endDate;

	public void showAvailableStorages() {
		// TODO da se proveri dali raboti pravilno; update: da se pravqt oshte testove,
		// TODO2 da se dobavi filtur uj raboti
		Session session = factory.getCurrentSession();
		session.beginTransaction();

		LocalDate sDate = startDate.getValue();
		LocalDate eDate = endDate.getValue();
		
		ObservableList<Storage> dateStorageList = FXCollections.observableArrayList();

		if (sDate != null && eDate != null) {
			System.out.println(sDate);
			System.out.println(eDate);

			List<Storage> query = session
					.createQuery("select s.storage from Contract s where (s.startDate not between '" + sDate + "' and '"
							+ eDate + "') and " + "(s.endDate not between '" + sDate + "' and '" + eDate + "')")
					.list();

			List<Storage> noContractStorages = session
					.createQuery("from Storage s where s.storageID not in (select c.storage from Contract c) and "
							+ "(s.owner = '" + Singleton.getInstance().getOwner().getOwnerID() + "')")
					.list();

			for (Storage storage : query) {
				if (storage.getOwner() == Singleton.getInstance().getOwner()) {
					noContractStorages.add(storage);
				}
			}

			dateStorageList = FXCollections.observableArrayList(noContractStorages);
			System.out.println(dateStorageList);
			storageTable.setItems(dateStorageList);
		} else
			storageTable.setItems(storageList);
		
		FilteredList<Storage> filteredData = new FilteredList<>(dateStorageList, p -> true);
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
		System.out.println(sortedData);
		sortedData.comparatorProperty().bind(storageTable.comparatorProperty());
		storageTable.setItems(sortedData);

		session.getTransaction().commit();
	}

	@FXML
	Button editStorageBtn;

	public void editStorage() {
		try {
			Storage tempStorage = storageTable.getSelectionModel().getSelectedItem();
			Singleton.getInstance().setStorage(tempStorage);
			anp2.getChildren().clear();
			anp2.getChildren().add(FXMLLoader.load(getClass().getResource("EditStorage.fxml")));
			borderOwnedSt.setRight(anp2);

		} catch (Exception e) {
			e.printStackTrace();
		}
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

	public void showContractInfo() {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("ContractInfo.fxml"));
			Stage stage = new Stage();
			stage.setScene(new Scene(root));
			stage.setTitle("Contract Info");
			stage.show();

			stage.setOnCloseRequest((WindowEvent event1) -> {
				showProfileInfo();
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void showContracts() {
		
	}
	
	@FXML
	Tab ownedStoragesTab;
	
	public void onSelectOwnedStoragesTab() {
		ownedStoragesTab.setOnSelectionChanged(e -> {
			showOwnedStorages();
		});
	}

	public void keyPressed(KeyEvent event) {

		Control[] focusOrder = new Control[] { checkComboBox, comboType, comboCategory, storageAddress, stClmConditions,
				storageSize, crStorageBtn };

		for (int i = 0; i < focusOrder.length - 1; i++) {
			Control nextControl = focusOrder[i + 1];
			focusOrder[i].addEventHandler(ActionEvent.ACTION, e -> nextControl.requestFocus());
		}
	}
}
