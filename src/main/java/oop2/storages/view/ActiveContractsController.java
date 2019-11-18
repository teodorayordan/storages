package oop2.storages.view;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import oop2.storages.Contract;
import oop2.storages.HibernateUtility;
import oop2.storages.Contract;

public class ActiveContractsController implements Initializable {

	@FXML
	TableView<Contract> activeContractsTable;

	@FXML
	TableColumn<Contract, String> storageColumn;

	@FXML
	TableColumn<Contract, String> startDateColumn;

	@FXML
	TableColumn<Contract, String> endDateColumn;

	@FXML
	TableColumn<Contract, String> renterNameColumn;

	@FXML
	TableColumn<Contract, String> renterPinColumn;

	@FXML
	TableColumn<Contract, String> priceColumn;

	@FXML
	TextField searchContract;

	SessionFactory factory = HibernateUtility.getSessionFactory();
	ObservableList<Contract> contractList;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		showContracts();
	}

	public void showContracts() {
		Session session = factory.getCurrentSession();
		session.beginTransaction();

		List<Contract> query = session.createQuery("from Contract s where "
				+ "s.agent = '"
				+ Singleton.getInstance().getAgent().getUser().getUserID() + "' and s.endDate >= '"+  LocalDate.now() +"'").list();
		contractList = FXCollections.observableArrayList(query);

		// tuka definirash vuv vsqka kolona kakvo ima kato towa v skobite e imeto na
		// promenlivata ot klasa na obekta
		storageColumn.setCellValueFactory(new PropertyValueFactory<Contract, String>("storage"));
		startDateColumn.setCellValueFactory(new PropertyValueFactory<Contract, String>("startDate"));
		endDateColumn.setCellValueFactory(new PropertyValueFactory<Contract, String>("endDate"));
		renterNameColumn.setCellValueFactory(new PropertyValueFactory<Contract, String>("renterName"));
		renterPinColumn.setCellValueFactory(new PropertyValueFactory<Contract, String>("renterPin"));
		priceColumn.setCellValueFactory(new PropertyValueFactory<Contract, String>("price"));

		activeContractsTable.setItems(contractList);

		FilteredList<Contract> filteredData = new FilteredList<>(contractList, p -> true);
		searchContract.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredData.setPredicate(contract -> {
				if (newValue == null || newValue.isEmpty()) {
					return true;
				}

				String lowerCaseFilter = newValue.toLowerCase();

				// kazvash koi poleta da tursi s teq if-ove
				// tuka ima .toString() toe da se vzeme stringa i se sravnavq s newValue toest
				// tova koeto napishesh
				if (contract.getStorage().getStorageAddress().toLowerCase().contains(lowerCaseFilter)) {
					return true;
				} else if (contract.getStartDate().toString().contains(newValue)) {
					return true;
				} else if (contract.getEndDate().toString().contains(newValue)) {
					return true;
				} else if (contract.getRenterName().toLowerCase().contains(lowerCaseFilter)) {
					return true;
				} else if (contract.getRenterPin().toLowerCase().contains(lowerCaseFilter)) {
					return true;
				} else if (contract.getPrice().toString().contains(newValue)) {
					return true;
				}
				return false;
			});
		});
		
		activeContractsTable.getSelectionModel().getSelectedItem();

		// setvash tuka namerenite danni v sorted list i go setvash na table-a
		SortedList<Contract> sortedData = new SortedList<>(filteredData);
		sortedData.comparatorProperty().bind(activeContractsTable.comparatorProperty());
		activeContractsTable.setItems(sortedData);

		activeContractsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
		    if (newSelection != null) {
		        Singleton.getInstance().setContract(activeContractsTable.getSelectionModel().getSelectedItem());
		    }
		});
	
		
		session.getTransaction().commit();
	}

}
