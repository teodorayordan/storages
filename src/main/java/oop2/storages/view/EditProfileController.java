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

	SessionFactory factory = HibernateUtility.getSessionFactory();

	// create session
	Session session = factory.getCurrentSession();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Agent agent = Singleton.getInstance().getAgent();
		if (agent != null) {
			labelCommission.setVisible(true);
			newCommission.setVisible(true);
			commissionCheck.setVisible(true);
		}

	}

	public void editProfile(ActionEvent event) {
		User user = Singleton.getInstance().getUser();
		newName.setEditable(false);
		newPassword.setEditable(false);
		newCommission.setEditable(false);

		if (nameCheck.selectedProperty() != null) {
			newName.setEditable(true);
			user.setPersonName(newName.getText());
		}
		if (passwordCheck.selectedProperty() != null) {
			newPassword.setEditable(true);
			user.setAccountPassword(newPassword.getText());
		}

		session.beginTransaction();

		if (newCommission.isVisible()) {
			Agent agent = Singleton.getInstance().getAgent();
			if (commissionCheck.selectedProperty() != null) {
				newCommission.setEditable(true);
				agent.setCommission(Double.parseDouble(newCommission.getText()));
				session.update(user);
				System.out.println(agent);
				session.update(agent);
			}
		} else
			System.out.println(user);

		// commit transaction
		session.getTransaction().commit();

	}

	public void keyPressed(KeyEvent event) {

		// да добавя още един - с if се проверява дали съдържа комисионна, че не работи
		// иначе
		if (newCommission.isVisible()) {
			Control[] focusOrder = new Control[] { newName, newPassword, newCommission, editPrBtn };

			for (int i = 0; i < focusOrder.length - 1; i++) {
				Control nextControl = focusOrder[i + 1];
				focusOrder[i].addEventHandler(ActionEvent.ACTION, e -> nextControl.requestFocus());
			}
		} else {
			Control[] focusOrder = new Control[] { newName, newPassword, editPrBtn };

			for (int i = 0; i < focusOrder.length - 1; i++) {
				Control nextControl = focusOrder[i + 1];
				focusOrder[i].addEventHandler(ActionEvent.ACTION, e -> nextControl.requestFocus());
			}
		}

	}

}
