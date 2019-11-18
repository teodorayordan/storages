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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import oop2.storages.Contract;
import oop2.storages.HibernateUtility;

public class AllContractsController implements Initializable {

	@FXML
	TableView<Contract> allContractsTable;

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

	@FXML
	DatePicker startDate;

	@FXML
	DatePicker endDate;

	SessionFactory factory = HibernateUtility.getSessionFactory();
	ObservableList<Contract> contractList;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		showContracts();

	}

	public void showContracts() {
		Session session = factory.getCurrentSession();
		session.beginTransaction();

		List<Contract> query = session.createQuery(
				"from Contract s where s.agent = '" + Singleton.getInstance().getAgent().getUser().getUserID() + "'")
				.list();
		contractList = FXCollections.observableArrayList(query);

		// tuka definirash vuv vsqka kolona kakvo ima kato towa v skobite e imeto na
		// promenlivata ot klasa na obekta
		storageColumn.setCellValueFactory(new PropertyValueFactory<Contract, String>("storage"));
		startDateColumn.setCellValueFactory(new PropertyValueFactory<Contract, String>("startDate"));
		endDateColumn.setCellValueFactory(new PropertyValueFactory<Contract, String>("endDate"));
		renterNameColumn.setCellValueFactory(new PropertyValueFactory<Contract, String>("renterName"));
		renterPinColumn.setCellValueFactory(new PropertyValueFactory<Contract, String>("renterPin"));
		priceColumn.setCellValueFactory(new PropertyValueFactory<Contract, String>("price"));

		allContractsTable.setItems(contractList);

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

		allContractsTable.getSelectionModel().getSelectedItem();

		// setvash tuka namerenite danni v sorted list i go setvash na table-a
		SortedList<Contract> sortedData = new SortedList<>(filteredData);
		sortedData.comparatorProperty().bind(allContractsTable.comparatorProperty());
		allContractsTable.setItems(sortedData);

		allContractsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				Singleton.getInstance().setContract(allContractsTable.getSelectionModel().getSelectedItem());
			}
		});

		session.getTransaction().commit();
	}

	public void showDateContract() {
		Session session = factory.getCurrentSession();
		session.beginTransaction();

		LocalDate sDate = startDate.getValue();
		LocalDate eDate = endDate.getValue();

		if (sDate != null && eDate != null) {
			System.out.println(sDate);
			System.out.println(eDate);

			List<Contract> query = session.createQuery(
					"from Contract s where (s.startDate between '"+ sDate +"' and '"+ eDate +"') and (s.agent = '"+ Singleton.getInstance().getAgent().getAgentID()+"')").list();

			ObservableList<Contract> dateContractList = FXCollections.observableArrayList(query);

			allContractsTable.setItems(dateContractList);
		}else
			allContractsTable.setItems(contractList);
		
		/*FilteredList<Contract> filteredData = new FilteredList<>(contractList, p -> true);
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

		allContractsTable.getSelectionModel().getSelectedItem();

		// setvash tuka namerenite danni v sorted list i go setvash na table-a
		SortedList<Contract> sortedData = new SortedList<>(filteredData);
		sortedData.comparatorProperty().bind(allContractsTable.comparatorProperty());
		allContractsTable.setItems(sortedData);*/

		session.getTransaction().commit();
	}
}
