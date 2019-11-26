package oop2.storages.view;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import oop2.storages.Agent;
import oop2.storages.HibernateUtility;
import oop2.storages.Notification;
import oop2.storages.Storage;
import oop2.storages.User;

public class EditStorageController implements Initializable {
	SessionFactory factory = HibernateUtility.getSessionFactory();

	ObservableList<Agent> allAgentList;
	ObservableList<Agent> currentAgentList;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		showAllAgents();
		showCurrentAgents();
		
	}

	public void showAllAgents() {
		// TODO v nachaloto ne premahva tekushti agenti sushto ne izobrazqva imena
		Session session = factory.getCurrentSession();
		session.beginTransaction();

		List<Agent> query = session.createQuery("from Agent s").list();
		List<Agent> buf = Singleton.getInstance().getStorage().getAgentList();
		query.removeAll(buf);
		allAgentList = FXCollections.observableArrayList(query);
		

		allNameColumn.setCellValueFactory(new PropertyValueFactory<Agent, String>("personName"));
		allCommissionColumn.setCellValueFactory(new PropertyValueFactory<Agent, String>("commission"));
		allRatingColumn.setCellValueFactory(new PropertyValueFactory<Agent, String>("rating"));

		allAgentsTable.setItems(allAgentList);

		session.getTransaction().commit();
	}

	public void showCurrentAgents() {
		Session session = factory.getCurrentSession();
		session.beginTransaction();

		currentAgentList = FXCollections.observableArrayList(Singleton.getInstance().getStorage().getAgentList());
		
		currentNameColumn.setCellValueFactory(new PropertyValueFactory<Agent, String>("personName"));
		currentCommissionColumn.setCellValueFactory(new PropertyValueFactory<Agent, String>("commission"));
		currentRatingColumn.setCellValueFactory(new PropertyValueFactory<Agent, String>("rating"));

		currentAgentsTable.setItems(currentAgentList);

		session.getTransaction().commit();
	}

	@FXML
	Button rmvAgentBtn;

	@FXML
	Button addAgentBtn;

	@FXML
	TableView<Agent> currentAgentsTable;

	@FXML
	TableColumn<Agent, String> currentNameColumn;

	@FXML
	TableColumn<Agent, String> currentCommissionColumn;

	@FXML
	TableColumn<Agent, String> currentRatingColumn;

	@FXML
	TableView<Agent> allAgentsTable;

	@FXML
	TableColumn<Agent, String> allNameColumn;

	@FXML
	TableColumn<Agent, String> allCommissionColumn;

	@FXML
	TableColumn<Agent, String> allRatingColumn;

	public void removeAgent() {
		Session session = factory.getCurrentSession();
		session.beginTransaction();
		Agent agent = currentAgentsTable.getSelectionModel().getSelectedItem();
		Singleton.getInstance().getStorage().removeAgent(agent);
		Storage tempStorage = Singleton.getInstance().getStorage();
		session.update(tempStorage);

		currentAgentList.remove(agent);
		allAgentList.add(agent);

		session.getTransaction().commit();
	}

	public void addAgent() {
		Session session = factory.getCurrentSession();
		session.beginTransaction();
		Agent agent = allAgentsTable.getSelectionModel().getSelectedItem();
		Singleton.getInstance().getStorage().addAgent(agent);
		Storage tempStorage = Singleton.getInstance().getStorage();
		session.update(tempStorage);

		currentAgentList.add(agent);
		allAgentList.remove(agent);
		
		//tuka dobavihme pri dobavqne na agent da mu se prashta izvestiq za svoboden sklad, ako e svoboden
		if(tempStorage.getStorageStatus() == false) {
			Notification noti = new Notification(agent.getUser(), (LocalDate.now() + ": Storage "+ tempStorage.getStorageAddress() +" is free for sale"));
	
			
			List<Notification> notificationResult = session.createQuery("from Notification s where s.notificationStatus = 1").list();
			System.out.println(notificationResult);
			if(!notificationResult.contains(noti)) {
				System.out.println(noti);
				session.save(noti);
			}
			
		}

		session.getTransaction().commit();
	}

	public void keyPressed(KeyEvent event) {

		/*
		 * Control[] focusOrder = new Control[] { editAgent, editType, editCategory,
		 * editSize, editClConditions };
		 * 
		 * for (int i = 0; i < focusOrder.length - 1; i++) { Control nextControl =
		 * focusOrder[i + 1]; focusOrder[i].addEventHandler(ActionEvent.ACTION, e ->
		 * nextControl.requestFocus()); }
		 */

	}

}
