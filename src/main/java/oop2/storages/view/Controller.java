package oop2.storages.view;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import oop2.storages.Agent;
import oop2.storages.Contract;
import oop2.storages.HibernateUtility;
import oop2.storages.Notification;
import oop2.storages.Owner;
import oop2.storages.Storage;
import oop2.storages.User;
import validations.Validation;

public class Controller implements Initializable {

	static SessionFactory factory = HibernateUtility.getSessionFactory();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		checkExpiredContract();
		soonExpiringContractNotification();
	}

	//proverka za iztekli dogovori i smqna na statusa na sklad i dogovor i suzdavane na suotvetni izvestiq
	public void checkExpiredContract() {
		Session session = factory.getCurrentSession();
		session.beginTransaction();

		Query<Contract> query = session.createQuery(
				"from Contract s " + "where s.endDate < '" + LocalDate.now() + "'" + " and s.contractStatus = '1' ");

		ObservableList<Contract> checkEndDateContract = FXCollections.observableArrayList(query.list());

		System.out.println(checkEndDateContract);
		for (Contract contract : checkEndDateContract) {
			Storage tempStorage = contract.getStorage();
			contract.getStorage().setStorageStatus(false);
			session.update(tempStorage);

			contract.setContractStatus(false);
			session.update(contract);

			// tuka slagame chasta za izvestiq sprqmo iztekul dovor ili kazano svobodna
			// zaqvka za otdavane
			ObservableList<Notification> notificationList = FXCollections.observableArrayList();

			for (Agent agent : contract.getStorage().getAgentList()) {
				Notification noti = new Notification(agent.getUser(),
						(LocalDate.now() + ": Storage " + tempStorage.getStorageAddress() + " is free for sale"));
				notificationList.add(noti);
			}

			List<Notification> notificationResult = session
					.createQuery("from Notification s where s.notificationStatus = 1").list();
			System.out.println(notificationResult);
			for (Notification notification : notificationList) {
				if (!notificationResult.contains(notification))
					System.out.println(notification);
				session.save(notification);
			}

		}

		session.getTransaction().commit();
	}

	//proverka za iztichashti dogovori v perioda dnes/utre i suzdavane na suotvetnite izvestiq
	public void soonExpiringContractNotification() {
		Session session = factory.getCurrentSession();
		session.beginTransaction();

		Query<Contract> query = session.createQuery("from Contract s where (s.endDate = '" + LocalDate.now().plusDays(1)
				+ "'" + " and s.contractStatus = '1') or (s.endDate = '" + LocalDate.now() + "'"
				+ " and s.contractStatus = '1')");

		ObservableList<Contract> checkSoonExpiringContract = FXCollections.observableArrayList(query.list());
		ObservableList<Notification> notificationList = FXCollections.observableArrayList();

		for (Contract contract : checkSoonExpiringContract) {
			for (Agent agent : contract.getStorage().getAgentList()) {
				Notification noti = new Notification(agent.getUser(), (LocalDate.now() + ": Expiring contract "
						+ contract.getStorage().getStorageAddress() + " on: " + contract.getEndDate()));
				notificationList.add(noti);
			}
		}

		for (Contract contract : checkSoonExpiringContract) {
			Notification noti = new Notification(contract.getStorage().getOwner().getUser(),
					(LocalDate.now() + ": Expiring contract " + contract.getStorage().getStorageAddress() + "on: "
							+ contract.getEndDate()));
			notificationList.add(noti);
		}

		for (Notification notification : notificationList) {
			List<Notification> notificationResult = session.createQuery(
					"from Notification s where s.notificationText = '" + notification.getNotificationText() + "'")
					.list();

			if (notificationResult.isEmpty()) {
				System.out.println(notification);
				session.save(notification);
			}
		}

		session.getTransaction().commit();
	}

	@FXML
	Button loginBtn;

	@FXML
	TextField accountName;

	@FXML
	PasswordField accountPassword;

	@FXML
	Label label;

	@FXML
	Label nameError;

	@FXML
	Label passwordError;

	//funkciq za login
	public void login() {
		boolean nameValid = Validation.textAlphabet(accountName, nameError, "Enter Valid Account!");
		boolean passValid = Validation.textAlphabet(accountPassword, passwordError, "Enter Valid Password!");

		label.setVisible(false);

		if (nameValid && passValid) {
			String accName = accountName.getText();
			String accPassword = accountPassword.getText();
			
			//proverka dali e admin i ako e se izobrazqva negoviq prozorec
			if (accName.equals("admin") && accPassword.equals("admin")) {
				try {
					Parent root = FXMLLoader.load(getClass().getResource("AdminPane.fxml"));
					Stage stage = new Stage();
					stage.setScene(new Scene(root));
					stage.setTitle("Admin Profile");
					stage.setResizable(false);
					stage.getIcons().add(new Image("/pictures/storage.png"));
					stage.show();

					loginBtn.getScene().getWindow().hide();

					stage.setOnCloseRequest((WindowEvent event1) -> {
						factory.close();
						Platform.exit();
					});

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				//proverka za sushtestvuvasht profil
				User loggedUser = profileCheck(accName, accPassword);

				if (loggedUser == null) {
					label.setVisible(true);
				} else {
					System.out.println("There is a account");

					Session session = factory.getCurrentSession();
					session.beginTransaction();

					Singleton.getInstance().setUser(loggedUser);

					Owner owner = session.get(Owner.class, loggedUser.getUserID());
					Agent agent = session.get(Agent.class, loggedUser.getUserID());

					//proverka dali e owner i ako e pokazvane na suotvetniq prozorec
					if (owner != null && owner.getUser().getStatusLogin() == false) {
						System.out.println("Its an owner");
						System.out.println(owner);
						Singleton.getInstance().setOwner(owner);
						Singleton.getInstance().getUser().setStatusLogin(true);
						session.merge(Singleton.getInstance().getUser()); 
						session.getTransaction().commit();
						try {
							Parent root = FXMLLoader.load(getClass().getResource("OwnerPane.fxml"));
							Stage stage = new Stage();
							stage.setScene(new Scene(root));
							stage.setTitle("Owner Profile: " + owner.getUser().getPersonName().toString());
							stage.setResizable(false);
							stage.getIcons().add(new Image("/pictures/storage.png"));
							stage.show();
							// hide login pane
							loginBtn.getScene().getWindow().hide();

							stage.setOnCloseRequest((WindowEvent event1) -> {
								if (Singleton.getInstance().getUser() != null) {
									Session session1 = factory.getCurrentSession();
									session1.beginTransaction();
									Singleton.getInstance().getUser().setStatusLogin(false);
									session1.update(Singleton.getInstance().getUser());
									session1.getTransaction().commit();
								}
								factory.close();
								Platform.exit();
							});

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					//proverka dali e agent i pokazvane na suotvetniq prozorec
					else if (agent != null && agent.getUser().getStatusLogin() == false) {
						System.out.println("Its an agent");
						System.out.println(agent);
						Singleton.getInstance().setAgent(agent);
						Singleton.getInstance().getUser().setStatusLogin(true);
						session.merge(Singleton.getInstance().getUser());
						session.getTransaction().commit();
						try {
							Parent root = FXMLLoader.load(getClass().getResource("AgentPane.fxml"));
							Stage stage = new Stage();
							stage.setScene(new Scene(root));
							stage.setTitle("Agent Profile: " + agent.getUser().getPersonName().toString());
							stage.setResizable(false);
							stage.getIcons().add(new Image("/pictures/storage.png"));
							stage.show();

							// hide login pane
							loginBtn.getScene().getWindow().hide();

							stage.setOnCloseRequest((WindowEvent event1) -> {
								if (Singleton.getInstance().getUser() != null) {
									Session session1 = factory.getCurrentSession();
									session1.beginTransaction();
									Singleton.getInstance().getUser().setStatusLogin(false);
									session1.update(Singleton.getInstance().getUser());
									session1.getTransaction().commit();
								}

								factory.close();
								Platform.exit();
							});

						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						System.out.println("Contact an admin for help. It appears youre logged in.");
						session.getTransaction().commit();
					}
				}
			}
		}
	}

	//funkciq za proverka na profil
	public static User profileCheck(String accName, String accPassword) {
		Session session = factory.getCurrentSession();
		session.beginTransaction();

		Query<User> query = session.createQuery(
				"from User s where s.accountName='" + accName + "' and s.accountPassword='" + accPassword + "'");

		User loginResult = query.uniqueResult();

		session.getTransaction().commit();
		;
		return loginResult;
	}

	public void keyPressed(KeyEvent event) {

		Control[] focusOrder = new Control[] { accountName, accountPassword };

		for (int i = 0; i < focusOrder.length - 1; i++) {
			Control nextControl = focusOrder[i + 1];
			focusOrder[i].addEventHandler(ActionEvent.ACTION, e -> nextControl.requestFocus());
		}
		if (accountPassword.isFocused()) {
			loginBtn.setDefaultButton(true);
		}
	}
}
