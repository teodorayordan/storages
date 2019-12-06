package oop2.storages.view;

import java.net.URL;
import java.util.ResourceBundle;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import oop2.storages.Agent;
import oop2.storages.HibernateUtility;
import oop2.storages.User;
import validations.Validation;

public class EditProfileController implements Initializable {

	SessionFactory factory = HibernateUtility.getSessionFactory();

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

	@FXML
	Label newNameError;

	@FXML
	Label newPasswordError;

	@FXML
	Label newCommissionError;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Agent agent = Singleton.getInstance().getAgent();
		if (agent != null) {
			labelCommission.setVisible(true);
			newCommission.setVisible(true);
			commissionCheck.setVisible(true);
		}
	}

	//pri natiskane na suotveten checkbox da se pozvoli pisane v dadeniq textfield
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

	//funkciq koqto update-va suotvetno izbranite poleta
	public void editProfile() {
		User user = Singleton.getInstance().getUser();
		Session session = factory.getCurrentSession();
		session.beginTransaction();

		if (nameCheck.isSelected()) {
			boolean accNameValid = Validation.textAlphabetFirstCapital(newName, newNameError,
					"Only Letters, First Capital! Max 25!");
			if (accNameValid) {
				user.setPersonName(newName.getText());
			} else
				System.out.println("Empty fields");
		}

		if (passwordCheck.isSelected()) {
			boolean passValid = Validation.textAlphabet(newPassword, newPasswordError, "Enter Valid Password!");
			if (passValid) {
				user.setAccountPassword(newPassword.getText());
			} else
				System.out.println("Empty fields");
		}

		if (newCommission.isVisible()) {
			Agent agent = Singleton.getInstance().getAgent();
			if (commissionCheck.isSelected()) {
				boolean commValid = Validation.textCommission(newCommission, newCommissionError, "Enter Number!");
				if (commValid) {
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
