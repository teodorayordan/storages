package oop2.storages.view;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.sun.xml.bind.v2.runtime.unmarshaller.XsiNilLoader.Single;

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
import javafx.stage.Stage;
import oop2.storages.Agent;
import oop2.storages.Contract;
import oop2.storages.Owner;
import oop2.storages.Storage;
import oop2.storages.User;

public class AgentController implements Initializable {

	SessionFactory factory = new Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(User.class)
			.addAnnotatedClass(Owner.class).addAnnotatedClass(Agent.class).addAnnotatedClass(Storage.class)
			.buildSessionFactory();

	Session session = factory.getCurrentSession();

	@FXML
	ListView<Storage> maintainedSt;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		session.beginTransaction();

		nameText.setText(Singleton.getInstance().getAgent().getUser().getPersonName());
		accountNameText.setText(Singleton.getInstance().getAgent().getUser().getAccountName());
		commissionText.setText("" + Singleton.getInstance().getAgent().getCommission() + "");
		ratingText.setText("" + Singleton.getInstance().getAgent().getRating() + "");

		List<Storage> storageList = new ArrayList<>();

		storageList = session.createQuery("from Storage s where id_storage_agent = '"
				+ Singleton.getInstance().getAgent().getUser().getUserID() + "'").list();

		storageCombo.getItems().clear();
		storageCombo.getItems().addAll(storageList);
		maintainedSt.getItems().addAll(storageList);

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
		Storage choosenStorage = storageCombo.getValue();
		String renterNme = renterName.getText();
		String renterPn = renterPin.getText();
		LocalDate endDate = dateContract.getValue();
		Double pricePerMonth = Double.parseDouble(priceContract.getText());

		Contract contract = new Contract(Singleton.getInstance().getAgent(), choosenStorage, null, endDate,
				pricePerMonth, renterNme, renterPn);
		// System.out.println(contract);
		session.save(contract);
		session.getTransaction().commit();
	}

	public void keyPressed(KeyEvent event) {

		Control[] focusOrder = new Control[] { renterName, renterPin, priceContract, crContractBtn };

		for (int i = 0; i < focusOrder.length - 1; i++) {
			Control nextControl = focusOrder[i + 1];
			focusOrder[i].addEventHandler(ActionEvent.ACTION, e -> nextControl.requestFocus());
		}

	}

	@FXML
	Button showStBtn;

	public void showStorage(ActionEvent event) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("ShowStorage.fxml"));
			Stage stage = new Stage();
			stage.setScene(new Scene(root));
			stage.setTitle("Storage Details");
			stage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
