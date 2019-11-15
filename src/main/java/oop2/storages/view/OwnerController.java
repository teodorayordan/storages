package oop2.storages.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

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
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
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
import oop2.storages.HibernateUtility;
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

	public void loadCreateStorage() {
		Session session = factory.getCurrentSession();
		session.beginTransaction();

		Query<Agent> agentQuery = session.createQuery("from Agent s ");
		agentList = FXCollections.observableArrayList(agentQuery.list());
		comboAgent.getItems().setAll(agentList);

		Query<StorageType> typeQuery = session.createQuery("from StorageType s ");
		typeList = FXCollections.observableArrayList(typeQuery.list());
		comboType.getItems().setAll(typeList);

		Query<Category> categoryQuery = session.createQuery("from Category s ");
		categoryList = FXCollections.observableArrayList(categoryQuery.list());
		comboCategory.getItems().setAll(categoryList);

		session.getTransaction().commit();
	}

	@FXML
	Button editPrBtn;

	@FXML
	Label nameText;

	@FXML
	Label accountNameText;

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

	public void createStorage(ActionEvent event) {
		Session session = factory.getCurrentSession();
		Owner owner = Singleton.getInstance().getOwner();
		Agent chosenAgent = (Agent) comboAgent.getValue();
		List<Agent> agentList = new ArrayList<>();
		agentList.add(chosenAgent);
		StorageType chosenType = (StorageType) comboType.getValue();
		Category chosenCategory = (Category) comboCategory.getValue();
		String storageAdr = storageAddressColumn.getText();
		String climateConditions = stClmConditions.getText();
		Double storageSze = Double.parseDouble(storageSize.getText().toString());

		Storage tempStorage = new Storage(owner, chosenType, chosenCategory, storageSze, climateConditions, storageAdr,
				agentList);

		System.out.println(tempStorage);

		storageList.add(tempStorage);

		// start a transaction
		session.beginTransaction();

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

	public void showStorage(ActionEvent event) {
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
	Button editStorageBtn;

	public void editStorage(ActionEvent event) {
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

	public void keyPressed(KeyEvent event) {

		Control[] focusOrder = new Control[] { comboAgent, comboType, comboCategory, storageAddress, stClmConditions,
				storageSize, crStorageBtn };

		for (int i = 0; i < focusOrder.length - 1; i++) {
			Control nextControl = focusOrder[i + 1];
			focusOrder[i].addEventHandler(ActionEvent.ACTION, e -> nextControl.requestFocus());
		}

	}

}
