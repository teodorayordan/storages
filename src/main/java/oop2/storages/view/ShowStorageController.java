package oop2.storages.view;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class ShowStorageController implements Initializable {

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
	public void initialize(URL location, ResourceBundle resources) {
		ownerText.setText(Singleton.getInstance().getStorage().getOwner().getUser().getPersonName());
		agentText.setText(Singleton.getInstance().getStorage().getAgent().getUser().getPersonName());
		typeText.setText(Singleton.getInstance().getStorage().getStorageType().getTypeName());
		categoryText.setText(Singleton.getInstance().getStorage().getCategory().getCategoryName());
		addressText.setText(Singleton.getInstance().getStorage().getStorageAddress());
		sizeText.setText("" + Singleton.getInstance().getStorage().getStorageSize() + "");
		climateText.setText(Singleton.getInstance().getStorage().getClimateConditions());
		statusText.setText("" + Singleton.getInstance().getStorage().getStorageStatus() + "");

	}

}
