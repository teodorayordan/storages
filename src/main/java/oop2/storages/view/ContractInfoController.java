package oop2.storages.view;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class ContractInfoController implements Initializable {

	@FXML
	Label storageText;

	@FXML
	Label renterNameText;

	@FXML
	Label renterPinText;

	@FXML
	Label startDateText;

	@FXML
	Label endDateText;

	@FXML
	Label singlePriceText;

	@FXML
	Label fullPriceText;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		storageText.setText(Singleton.getInstance().getContract().getStorage().getStorageAddress());
		renterNameText.setText(Singleton.getInstance().getContract().getRenterName());
		renterPinText.setText(Singleton.getInstance().getContract().getRenterPin());
		startDateText.setText(Singleton.getInstance().getContract().getStartDate().toString());
		endDateText.setText(Singleton.getInstance().getContract().getEndDate().toString());
		singlePriceText.setText(Singleton.getInstance().getContract().getPrice().toString());

		// fullprice da dobavq

	}

}
