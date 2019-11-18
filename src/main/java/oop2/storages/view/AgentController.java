package oop2.storages.view;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import com.sun.xml.bind.v2.TODO;

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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import oop2.storages.Agent;
import oop2.storages.Category;
import oop2.storages.Contract;
import oop2.storages.HibernateUtility;
import oop2.storages.Owner;
import oop2.storages.Storage;
import oop2.storages.StorageType;
import oop2.storages.User;

public class AgentController implements Initializable {

	SessionFactory factory = HibernateUtility.getSessionFactory();

	ObservableList<Storage> storageList;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		showProfileInfo();
		showMaintainedStorages();
		loadCreateContract();

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
	TableColumn<Storage, String> storageStatusColumn;

	@FXML
	TextField searchStorage;

	public void showMaintainedStorages() {
		Session session = factory.getCurrentSession();
		session.beginTransaction();

		storageList = FXCollections.observableArrayList(Singleton.getInstance().getAgent().getStorageList());

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
		Session session = factory.getCurrentSession();
		session.beginTransaction();

		List<Storage> availableStorages = new ArrayList<>();
		for (Storage storage : storageList) {
			if (storage.getStorageStatus() == false)
				availableStorages.add(storage);
		}
		storageCombo.getItems().setAll(availableStorages);

		session.getTransaction().commit();
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

	public void editProfile(ActionEvent event) {

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

	public void createContract(ActionEvent event) {
		Session session = factory.getCurrentSession();
		session.beginTransaction();
		Storage choosenStorage = storageCombo.getValue();
		String renterNme = renterName.getText();
		String renterPn = renterPin.getText();
		LocalDate endDate = dateContract.getValue();
		Double pricePerMonth = Double.parseDouble(priceContract.getText());

		if (choosenStorage.getStorageStatus() == false) {
			Contract contract = new Contract(Singleton.getInstance().getAgent(), choosenStorage, null, endDate,
					pricePerMonth, renterNme, renterPn);
			Storage tempStorage = choosenStorage;
			tempStorage.setStorageStatus(true);
			System.out.println(tempStorage);
			session.update(tempStorage);
			session.save(contract);
		} else {
			System.out.println("Storage is already rented");
		}
		session.getTransaction().commit();
	}

	@FXML
	Button showStBtn;

	@FXML
	BorderPane borderMaintSt;

	AnchorPane anp = new AnchorPane();

	public void showStorage(ActionEvent event) {
		try {
			Storage tempStorage = storageTable.getSelectionModel().getSelectedItem();
			Singleton.getInstance().setStorage(tempStorage);
			anp.getChildren().clear();
			anp.getChildren().add(FXMLLoader.load(getClass().getResource("StorageInfo.fxml")));
			borderMaintSt.setRight(anp);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@FXML
	DatePicker startDate;

	@FXML
	DatePicker endDate;

	public void showAvailableStorages() {
		// TODO da se proveri dali raboti pravilno; update: da se pravqt oshte testove, TODO2 da se dobavi filtur
		Session session = factory.getCurrentSession();
		session.beginTransaction();

		LocalDate sDate = startDate.getValue();
		LocalDate eDate = endDate.getValue();

		if (sDate != null && eDate != null) {
			System.out.println(sDate);
			System.out.println(eDate);

			List<Storage> query = session.createQuery("select s.storage from Contract s where (s.startDate not between '"+ sDate +"' and '"+ eDate +"') and "
					+ "(s.endDate not between '"+ sDate +"' and '"+ eDate +"') and (s.agent = '"+ Singleton.getInstance().getAgent().getAgentID()+"')").list();

			//vzimane na spisuk sus skladove bez dogovor i proverka dali sme agent na tezi skladove
			List<Storage> noContractStorages = session.createQuery("from Storage s where s.storageID not in (select c.storage from Contract c)").list();
			for (Storage storage : noContractStorages) {
				if(storage.getAgentList().contains(Singleton.getInstance().getAgent())) {
					query.add(storage);
				}
			}

			ObservableList<Storage> dateStorageList = FXCollections.observableArrayList(query);
			System.out.println(dateStorageList);
			storageTable.setItems(dateStorageList);
		} else
			storageTable.setItems(storageList);

		session.getTransaction().commit();

		/*FilteredList<Storage> filteredData = new FilteredList<>(storageList, p -> true);
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
		storageTable.setItems(sortedData);*/

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

	public void showActiveContracts(ActionEvent event) {
		try {
			anpCont.getChildren().clear();
			anpCont.getChildren().add(FXMLLoader.load(getClass().getResource("ActiveContracts.fxml")));
			borderContracts.setRight(anpCont);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void showContractInfo(ActionEvent event) {
		try {
			anpCont2.getChildren().clear();
			anpCont2.getChildren().add(FXMLLoader.load(getClass().getResource("ContractInfo.fxml")));
			borderContracts.setRight(anpCont2);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void showAllContracts(ActionEvent event) {
		try {
			anpCont3.getChildren().clear();
			anpCont3.getChildren().add(FXMLLoader.load(getClass().getResource("AllContracts.fxml")));
			borderContracts.setRight(anpCont3);

		} catch (Exception e) {
			e.printStackTrace();
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
