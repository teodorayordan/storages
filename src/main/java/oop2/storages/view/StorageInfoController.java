package oop2.storages.view;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import oop2.storages.Agent;

public class StorageInfoController implements Initializable {

	@FXML
	Label ownerText;

	@FXML
	TableView<Agent> agentsTable;

	@FXML
	TableColumn<Agent, String> nameColumn;

	@FXML
	TableColumn<Agent, String> commissionColumn;

	@FXML
	TableColumn<Agent, String> ratingColumn;

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

	ObservableList<Agent> agentsList;

	//populvane na poletata za informaciq na sklada
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		try {
			ownerText.setText(Singleton.getInstance().getStorage().getOwner().getUser().getPersonName());
			typeText.setText(Singleton.getInstance().getStorage().getStorageType().getTypeName());
			categoryText.setText(Singleton.getInstance().getStorage().getCategory().getCategoryName());
			addressText.setText(Singleton.getInstance().getStorage().getStorageAddress());
			sizeText.setText("" + Singleton.getInstance().getStorage().getStorageSize() + "");
			climateText.setText(Singleton.getInstance().getStorage().getClimateConditions());
			statusText.setText("" + Singleton.getInstance().getStorage().getStorageStatus() + "");

			agentsList = FXCollections.observableArrayList(Singleton.getInstance().getStorage().getAgentList());

			nameColumn.setCellValueFactory(new PropertyValueFactory<Agent, String>("personName"));
			commissionColumn.setCellValueFactory(new PropertyValueFactory<Agent, String>("commission"));
			ratingColumn.setCellValueFactory(new PropertyValueFactory<Agent, String>("rating"));

			agentsTable.setItems(agentsList);

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

}
