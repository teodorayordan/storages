package oop2.storages.view;

import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.controlsfx.control.Notifications;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import oop2.storages.Agent;
import oop2.storages.Contract;
import oop2.storages.HibernateUtility;
import oop2.storages.Notification;
import oop2.storages.Storage;
import validations.Validation;

public class AgentController implements Initializable {

	SessionFactory factory = HibernateUtility.getSessionFactory();

	ObservableList<Storage> storageList;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Logger logger = Logger.getLogger(AdminController.class);
		logger.info("Agent " + Singleton.getInstance().getAgent().getPersonName() + " Logged");

		showProfileInfo();
		showMaintainedStorages();
		loadCreateContract();
		showActiveContracts();
		onSelectMaintainedStoragesTab();
		showAllContracts();
		showNotifications();
	}

	public void showProfileInfo() {
		nameText.setText(Singleton.getInstance().getAgent().getUser().getPersonName());
		accountNameText.setText(Singleton.getInstance().getAgent().getUser().getAccountName());
		commissionText.setText(Singleton.getInstance().getAgent().getCommission().toString());
		ratingText.setText(Singleton.getInstance().getAgent().getRating().toString());
	}

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

	public void showMaintainedStorages() {
		Session session = factory.getCurrentSession();
		session.beginTransaction();

		// pravim go taka za da se relodva vseki put kato vlezem ontovo v taba ako nqkoi
		// ot chuzd profil e napravil neshto da se izobrazi
		Agent agent = (Agent) session
				.createQuery(
						"from Agent s where s.agentID = '" + Singleton.getInstance().getAgent().getAgentID() + "' ")
				.uniqueResult();
		storageList = FXCollections.observableArrayList(agent.getStorageList());

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

	public void loadCreateContract() {
		// TODO dobavi error label-i
		Session session = factory.getCurrentSession();
		session.beginTransaction();

		dateContract.setDayCellFactory(picker -> new DateCell() {
			public void updateItem(LocalDate date, boolean empty) {
				super.updateItem(date, empty);
				LocalDate today = LocalDate.now();
				setDisable(empty || date.compareTo(today) < 1);
			}
		});

		List<Storage> availableStorages = new ArrayList<>();
		for (Storage storage : storageList) {
			if (storage.getStorageStatus() == false)
				availableStorages.add(storage);
		}
		storageCombo.getItems().setAll(availableStorages);

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

	public void showNotifications() {
		Runnable task = new Runnable() {
			@Override
			public void run() {
				Session session = factory.getCurrentSession();
				session.beginTransaction();

				Query<Notification> notiQuery = session.createQuery("from Notification s where s.user = '"
						+ Singleton.getInstance().getAgent().getAgentID() + "' " + "and s.notificationStatus = 1");
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

	@FXML
	Button editPrBtn;

	@FXML
	Label nameText;

	@FXML
	Label accountNameText;

	@FXML
	Label commissionText;

	@FXML
	Label ratingText;

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
	Button crContractBtn;

	@FXML
	TextField renterName;

	@FXML
	TextField renterPin;

	@FXML
	ComboBox<Storage> storageCombo;

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

	public void createContract() {
		boolean storageValid = Validation.textCombo(storageCombo, chStorageError, "Select Storage!");
		boolean renterNameValid = Validation.textAlphabetFirstCapital(renterName, renterNameError,
				"Only Letters, First Capital! Max 25!");
		boolean renterPinValid = Validation.textPin(renterPin, renterPinError, "Only Numbers! Must be 10!");
		boolean endDateValid = Validation.textDate(dateContract, contrEndDateError, "Choose Date!");
		boolean dayPriceValid = Validation.textCommission(priceContract, stSinglePriceError, "Enter Valid Price!");

		if (storageValid && renterNameValid && renterPinValid && endDateValid && dayPriceValid) {

			Session session = factory.getCurrentSession();
			session.beginTransaction();
			Storage choosenStorage = storageCombo.getValue();
			String renterNme = renterName.getText();
			String renterPn = renterPin.getText();
			LocalDate endDate = dateContract.getValue();
			Double pricePerMonth = Double.parseDouble(priceContract.getText());

			if (choosenStorage.getStorageStatus() == false) {
				Contract contract = new Contract(Singleton.getInstance().getAgent(), choosenStorage, null,
						endDate.plusDays(1), pricePerMonth, renterNme, renterPn);

				Storage tempStorage = choosenStorage;
				tempStorage.setStorageStatus(true);

				// tuka dobavihme notification za otdaden sklad
				Notification noti = new Notification(choosenStorage.getOwner().getUser(),
						(LocalDate.now() + ": Storage " + tempStorage.getStorageAddress() + " is now rented"));

				System.out.println(noti);
				session.save(noti);

				System.out.println(tempStorage);
				System.out.println(tempStorage.getStorageStatus());

				changeRating(tempStorage, Singleton.getInstance().getAgent());

				session.merge(tempStorage);
				session.save(contract);
				storageList.remove(tempStorage);

				Logger logger = Logger.getLogger(AdminController.class);
				logger.info(Singleton.getInstance().getUser().getAccountName() + " created contract with ID: "
						+ contract.getContractID());

				storageCombo.setValue(null);
				renterName.clear();
				renterPin.clear();
				dateContract.setValue(null);
				priceContract.clear();

			} else {
				System.out.println("Storage is already rented");
			}

			session.getTransaction().commit();
		}
	}

	@FXML
	DatePicker startDate;

	@FXML
	DatePicker endDate;

	public void showAvailableStorages() {
		// TODO da se proveri dali raboti pravilno; update: da se pravqt oshte testove,
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
							+ eDate + "') and " + "(s.endDate not between '" + sDate + "' and '" + eDate
							+ "') and (s.agent = '" + Singleton.getInstance().getAgent().getAgentID() + "')")
					.list();

			// vzimane na spisuk sus skladove bez dogovor i proverka dali sme agent na tezi
			// skladove
			List<Storage> noContractStorages = session
					.createQuery("from Storage s where s.storageID not in (select c.storage from Contract c)").list();
			for (Storage storage : noContractStorages) {
				if (storage.getAgentList().contains(Singleton.getInstance().getAgent())) {
					query.add(storage);
				}
			}

			dateStorageList = FXCollections.observableArrayList(query);
			System.out.println(dateStorageList);
			storageTable.setItems(dateStorageList);
		} else
			storageTable.setItems(storageList);

		session.getTransaction().commit();

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
				}
				return false;
			});
		});

		SortedList<Storage> sortedData = new SortedList<>(filteredData);
		sortedData.comparatorProperty().bind(storageTable.comparatorProperty());
		storageTable.setItems(sortedData);

	}

	@FXML
	Button showStBtn;

	@FXML
	BorderPane borderMaintSt;

	AnchorPane anp = new AnchorPane();

	public void showStorage(ActionEvent event) {
		if (storageTable.getSelectionModel().getSelectedItem() != null) {
			try {

				Storage tempStorage = storageTable.getSelectionModel().getSelectedItem();
				Singleton.getInstance().setStorage(tempStorage);
				anp.getChildren().clear();
				anp.getChildren().add(FXMLLoader.load(getClass().getResource("StorageInfo.fxml")));
				borderMaintSt.setRight(anp);

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else
			System.out.println("No selection!");
	}

	@FXML
	Button activeContrBtn;

	@FXML
	Button allContrBtn;

	@FXML
	Button contractInfoBtn;

	@FXML
	BorderPane borderContracts;

	AnchorPane anpCont = new AnchorPane();

	AnchorPane anpCont2 = new AnchorPane();

	AnchorPane anpCont3 = new AnchorPane();

	public void showActiveContracts() {
		try {
			anpCont.getChildren().clear();
			anpCont.getChildren().add(FXMLLoader.load(getClass().getResource("ActiveContracts.fxml")));
			borderContracts.setRight(anpCont);
			Singleton.getInstance().setContract(null);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void showContractInfo() {
		if (Singleton.getInstance().getContract() != null) {
			try {
				anpCont2.getChildren().clear();
				anpCont2.getChildren().add(FXMLLoader.load(getClass().getResource("ContractInfo.fxml")));
				borderContracts.setRight(anpCont2);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void showAllContracts() {
		try {
			anpCont3.getChildren().clear();
			anpCont3.getChildren().add(FXMLLoader.load(getClass().getResource("AllContracts.fxml")));
			borderContracts.setRight(anpCont3);
			Singleton.getInstance().setContract(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	Tab maintainedStoragesTab;

	public void onSelectMaintainedStoragesTab() {
		maintainedStoragesTab.setOnSelectionChanged(e -> {
			showMaintainedStorages();
		});
	}

	// izhisleniq kolko rating da mahnem na drugite agenti
	public double calculateRating(int agentCount) {
		if (agentCount > 5)
			return 0.25;
		else if (agentCount < 3)
			return 0.5;
		else
			return 0.33;
	}

	// funkciq za promqna na ratinga na vsichki agenti obvurzani sus sklad
	public void changeRating(Storage storage, Agent agent) {
		Session session = factory.getCurrentSession();
		double rating = calculateRating(storage.getAgentList().size());

		agent.setRating(agent.getRating() + 0.5);
		if (agent.getRating() > 5) {
			agent.setRating(5);
			session.merge(agent);
		}

		for (Agent otherAgents : storage.getAgentList()) {
			if (otherAgents != agent) {
				otherAgents.setRating(otherAgents.getRating() - rating);
				if (otherAgents.getRating() < 0)
					otherAgents.setRating(0);

				session.merge(otherAgents);
			}
		}
	}

	public void keyPressed(KeyEvent event) {
		
		Control[] focusOrder = new Control[] { storageCombo, renterName, renterPin, dateContract, priceContract,
				crContractBtn };

		for (int i = 0; i < focusOrder.length - 1; i++) {
			Control nextControl = focusOrder[i + 1];
			focusOrder[i].addEventHandler(ActionEvent.ACTION, e -> nextControl.requestFocus());
		}
	}
}
