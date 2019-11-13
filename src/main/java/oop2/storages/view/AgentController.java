package oop2.storages.view;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

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
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import oop2.storages.Agent;
import oop2.storages.Contract;
import oop2.storages.HibernateUtility;
import oop2.storages.Owner;
import oop2.storages.Storage;
import oop2.storages.User;

public class AgentController implements Initializable {

	SessionFactory factory = HibernateUtility.getSessionFactory();

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		Session session = factory.getCurrentSession();
		nameText.setText(Singleton.getInstance().getAgent().getUser().getPersonName());
		accountNameText.setText(Singleton.getInstance().getAgent().getUser().getAccountName());
		commissionText.setText("" + Singleton.getInstance().getAgent().getCommission() + "");
		ratingText.setText("" + Singleton.getInstance().getAgent().getRating() + "");

		List<Storage> storageList = new ArrayList<>();
		// pozvolqva da se izbere samo skladove sus status 0 - svobodni za otdavane
		storageList = session.createQuery("from Storage s where id_storage_agent = '"
				+ Singleton.getInstance().getAgent().getUser().getUserID() + "' and status = 0").list();

		storageCombo.getItems().clear();
		storageCombo.getItems().addAll(storageList);
		maintainedSt.getItems().addAll(storageList);

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

		Contract contract = new Contract(Singleton.getInstance().getAgent(), choosenStorage, null, endDate,
				pricePerMonth, renterNme, renterPn);
		Storage tempStorage = choosenStorage;
		tempStorage.setStorageStatus(true);
		System.out.println(tempStorage);
		session.update(tempStorage);

		session.save(contract);
		session.getTransaction().commit();
	}

	@FXML
	ListView<Storage> maintainedSt;

	@FXML
	Button showStBtn;

	@FXML
	Label ownerText;

	@FXML
	Label agentText;

	@FXML
	Label typeText;

	@FXML
	Label categoryText;

	@FXML
	Label addressText;

	@FXML
	Label sizeText;

	@FXML
	Label climateText;

	@FXML
	Label statusText;

	@FXML
	AnchorPane showStorage;

	public void showStorage(ActionEvent event) {
		try {
			// да се добави валидация дали е селектирано
			showStorage.setVisible(true);

			Storage tempStorage = maintainedSt.getSelectionModel().getSelectedItem();
			Singleton.getInstance().setStorage(tempStorage);

			ownerText.setText(Singleton.getInstance().getStorage().getOwner().getUser().getPersonName());
			agentText.setText(Singleton.getInstance().getStorage().getAgent().getUser().getPersonName());
			typeText.setText(Singleton.getInstance().getStorage().getStorageType().getTypeName());
			categoryText.setText(Singleton.getInstance().getStorage().getCategory().getCategoryName());
			addressText.setText(Singleton.getInstance().getStorage().getStorageAddress());
			sizeText.setText("" + Singleton.getInstance().getStorage().getStorageSize() + "");
			climateText.setText(Singleton.getInstance().getStorage().getClimateConditions());
			statusText.setText("" + Singleton.getInstance().getStorage().getStorageStatus() + "");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@FXML
	ListView<Contract> activeContracts;

	@FXML
	Label activeConText;

	@FXML
	Button allContrBtn;

	@FXML
	Button contractInfoBtn;

	@FXML
	Label allConText;

	@FXML
	Label stDateText;

	@FXML
	Label endDateText;

	@FXML
	ListView<Contract> allContracts;

	@FXML
	DatePicker startDate;

	@FXML
	DatePicker endDate;

	public void showContracts(ActionEvent event) {
		activeContracts.setVisible(false);
		activeConText.setVisible(false);
		allConText.setVisible(true);
		stDateText.setVisible(true);
		endDateText.setVisible(true);
		allContracts.setVisible(true);
		startDate.setVisible(true);
		endDate.setVisible(true);
	}

	public void showContractInfo(ActionEvent event) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("ContractInfo.fxml"));
			Stage stage = new Stage();
			stage.setScene(new Scene(root));
			stage.setTitle("Contract Info");
			stage.show();
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
