package oop2.storages.view;

import java.net.URL;
import java.util.ResourceBundle;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import oop2.storages.Agent;
import oop2.storages.HibernateUtility;
import oop2.storages.User;

public class EditProfileController implements Initializable {

	SessionFactory factory = HibernateUtility.getSessionFactory();

	Session session = factory.getCurrentSession();
	
	@FXML
	Button editPrBtn;

	@FXML
	TextField newName;

	@FXML
	PasswordField newPassword;

	@FXML
	TextField newCommission;

	@FXML
	Label labelCommission;

	@FXML
	CheckBox nameCheck;

	@FXML
	CheckBox passwordCheck;

	@FXML
	CheckBox commissionCheck;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Agent agent = Singleton.getInstance().getAgent();
		if (agent != null) {
			labelCommission.setVisible(true);
			newCommission.setVisible(true);
			commissionCheck.setVisible(true);
		}
	}

	public void enableEdit() {
		if (nameCheck.isSelected()) {
			newName.setEditable(true);
		} else {
			newName.setEditable(false);
		}
		
		if (passwordCheck.isSelected()) {
			newPassword.setEditable(true);
		} else {
			newPassword.setEditable(false);
		}
		
		if (commissionCheck.isSelected()) {
			newCommission.setEditable(true);
		} else {
			newCommission.setEditable(false);
		}
	}

	public void editProfile(ActionEvent event) {
		User user = Singleton.getInstance().getUser();

		if (nameCheck.isSelected()) {
			if (!newName.getText().isEmpty()) {
				user.setPersonName(newName.getText());
			} else
				System.out.println("Empty fields");
		} else if (passwordCheck.isSelected()) {

			if (!newPassword.getText().isEmpty()) {
				user.setAccountPassword(newPassword.getText());
			} else
				System.out.println("Empty fields");
		}

		session.beginTransaction();

		if (newCommission.isVisible()) {
			Agent agent = Singleton.getInstance().getAgent();
			if (commissionCheck.isSelected()) {
				if (!newCommission.getText().isEmpty()) {
					agent.setCommission(Double.parseDouble(newCommission.getText()));
					session.update(user);
					System.out.println(agent);
					session.update(agent);
				} else
					System.out.println("Empty fields");
			}
		} else {
			System.out.println(user);
			session.update(user);
		}

		session.getTransaction().commit();
	}
}
