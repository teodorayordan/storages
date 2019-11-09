package oop2.storages.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javafx.application.Platform;
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
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import oop2.storages.Agent;
import oop2.storages.Category;
import oop2.storages.Owner;
import oop2.storages.Storage;
import oop2.storages.StorageType;
import oop2.storages.User;

public class OwnerController implements Initializable {

	List<Agent> agentList = new ArrayList<>();
	List<StorageType> typeList = new ArrayList<>();
	List<Category> categoryList = new ArrayList<>();
	List<Storage> storageList = new ArrayList<>();

	SessionFactory factory = new Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(User.class)
			.addAnnotatedClass(Owner.class).addAnnotatedClass(Agent.class).addAnnotatedClass(Storage.class)
			.buildSessionFactory();

	Session session = factory.getCurrentSession();

	
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		session.beginTransaction();
		

		nameText.setText(Singleton.getInstance().getOwner().getUser().getPersonName());
		accountNameText.setText(Singleton.getInstance().getOwner().getUser().getAccountName());

		agentList = session.createQuery("from Agent s").list();

		categoryList = session.createQuery("from Category s").list();

		typeList = session.createQuery("from StorageType s").list();

		storageList = session.createQuery(
				"from Storage s where id_owner = '" + Singleton.getInstance().getOwner().getUser().getUserID() + "'")
				.list();

		// session.getTransaction().commit();

		comboAgent.getItems().clear();
		comboAgent.getItems().addAll(agentList);
		comboType.getItems().clear();
		comboType.getItems().addAll(typeList);
		comboCategory.getItems().clear();
		comboCategory.getItems().addAll(categoryList);
		ownedStorages.getItems().addAll(storageList);

	}

	@FXML
	Button editPrBtn;

	@FXML
	Label nameText;

	@FXML
	Label accountNameText;
	
	@FXML
	AnchorPane anchorPane;

	public void editProfile(ActionEvent event) {

		try {
			Parent root = FXMLLoader.load(getClass().getResource("EditProfile.fxml"));
			Stage stage = new Stage();
			stage.setScene(new Scene(root));
			stage.showAndWait();
			
			
			stage.setOnCloseRequest((WindowEvent event1) -> {
			    Platform.runLater(() -> {
					//anchorPane.getScene();
					//stage.setScene(anchorPane.getScene());
			    });
			});
			
		/*	Node node = anchorPane.getShape();

			PlatformHelper.run(() -> {
			  node.setLayoutX(editPrBtn.getScene().getWidth());
			  node.setLayoutY(editPrBtn.getScene().getHeight());
			});*/

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@FXML
	ListView<Storage> ownedStorages;

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

		Owner owner = Singleton.getInstance().getOwner();
		Agent chosenAgent = (Agent) comboAgent.getValue();
		StorageType chosenType = (StorageType) comboType.getValue();
		Category chosenCategory = (Category) comboCategory.getValue();
		String storageAdr = storageAddress.getText();
		String climateConditions = stClmConditions.getText();
		Double storageSze = Double.parseDouble(storageSize.getText().toString());

		Storage tempStorage = new Storage(owner, chosenAgent, chosenType, chosenCategory, storageSze, climateConditions,
				storageAdr, false);

		System.out.println(tempStorage);

		// start a transaction
		// session.beginTransaction();

		// save the student object
		session.save(tempStorage);

		// commit transaction
		session.getTransaction().commit();

	}
	
	@FXML
	Button showStBtn;

	public void showStorage(ActionEvent event) {

		try {
			Singleton.getInstance().setStorage(ownedStorages.getSelectionModel().getSelectedItem());

			Parent root = FXMLLoader.load(getClass().getResource("ShowStorage.fxml"));
			Stage stage = new Stage();
			stage.setScene(new Scene(root));
			stage.setTitle("Storage Details");
			stage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void keyPressed(KeyEvent event) {
		/*KeyCode key = event.getCode();
		if(key == KeyCode.ENTER) {
			stClmConditions.requestFocus();
		}*/

		Control[] focusOrder = new Control[] { storageAddress, stClmConditions, storageSize, crStorageBtn };

		for (int i = 0; i < focusOrder.length-1; i++) {
			Control nextControl = focusOrder[i + 1];
			focusOrder[i].addEventHandler(ActionEvent.ACTION, e -> nextControl.requestFocus());
		}
			
	}

}
