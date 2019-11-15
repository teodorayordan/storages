package oop2.storages.view;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import oop2.storages.Storage;

public class StorageInfoController implements Initializable {

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

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		try {
			// да се добави валидация дали е селектирано
		

			ownerText.setText(Singleton.getInstance().getStorage().getOwner().getUser().getPersonName());
			agentText.setText(Singleton.getInstance().getStorage().getAgentList().toString());
			typeText.setText(Singleton.getInstance().getStorage().getStorageType().getTypeName());
			categoryText.setText(Singleton.getInstance().getStorage().getCategory().getCategoryName());
			addressText.setText(Singleton.getInstance().getStorage().getStorageAddress());
			sizeText.setText("" + Singleton.getInstance().getStorage().getStorageSize() + "");
			climateText.setText(Singleton.getInstance().getStorage().getClimateConditions());
			statusText.setText("" + Singleton.getInstance().getStorage().getStorageStatus() + "");

			// session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
