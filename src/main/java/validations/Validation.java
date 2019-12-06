package validations;

import org.controlsfx.control.CheckComboBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class Validation {

	// raboti
	// account name and account password
	public static boolean textAlphabet(TextField inputTextField, Label inputLabel, String validationText) {
		boolean isAlphabet = true;
		String validationString = null;

		if (!inputTextField.getText().matches("^[a-zA-Z0-9_]{4,25}$")) {
			isAlphabet = false;
			validationString = validationText;

		}
		inputLabel.setText(validationString);

		return isAlphabet;

	}

	// raboti
	// user and renter first letter of both names capital
	public static boolean textAlphabetFirstCapital(TextField inputTextField, Label inputLabel, String validationText) {
		boolean isAlphabet = true;
		String validationString = null;

		if (!inputTextField.getText().matches("[A-Z][a-z]+[ ]+[A-Z][a-z]{1,25}")) {
			isAlphabet = false;
			validationString = validationText;

		}
		inputLabel.setText(validationString);

		return isAlphabet;

	}

	// raboti
	// user and renter pin validations
	public static boolean textPin(TextField inputTextField, Label inputLabel, String validationText) {
		boolean isNumeric = true;
		String validationString = null;

		if (!inputTextField.getText().matches("[0-9]{10}")) {
			isNumeric = false;
			validationString = validationText;

		}
		inputLabel.setText(validationString);
		return isNumeric;

	}

	// raboti
	// commission, size and price validation
	public static boolean textCommission(TextField inputTextField, Label inputLabel, String validationText) {
		boolean isNumeric = true;
		String validationString = null;

		if (!inputTextField.getText().matches("^[0-9]*\\.?[0-9]+$")) {
			isNumeric = false;
			validationString = validationText;

		}
		inputLabel.setText(validationString);
		return isNumeric;
	}

	// raboti
	// category and type
	public static boolean textLetters(TextField inputTextField, Label inputLabel, String validationText) {
		boolean isAlphabet = true;
		String validationString = null;

		if (!inputTextField.getText().matches("[A-Za-z ]{1,30}")) {
			isAlphabet = false;
			validationString = validationText;

		}
		inputLabel.setText(validationString);
		return isAlphabet;

	}

	// raboti
	// address and climate conditions validation
	public static boolean textAddress(TextField inputTextField, Label inputLabel, String validationText) {
		boolean isNumeric = true;
		String validationString = null;

		if (!inputTextField.getText().matches("[A-Za-z0-9'\"\\.\\-\\s\\,]{2,25}")) {
			isNumeric = false;
			validationString = validationText;

		}
		inputLabel.setText(validationString);
		return isNumeric;

	}

	// raboti
	// comboBox validation
	public static <T> boolean textCombo(ComboBox<T> comboField, Label inputLabel, String validationText) {
		boolean isSelected = true;
		String validationString = null;

		if (comboField.getSelectionModel().isEmpty()) {
			isSelected = false;
			validationString = validationText;
		}
		inputLabel.setText(validationString);
		return isSelected;
	}

	// raboti
	// checkComboBox validation
	public static <T> boolean textCheckCombo(CheckComboBox<T> comboField, Label inputLabel, String validationText) {
		boolean isSelected = true;
		String validationString = null;

		if (comboField.getCheckModel().isEmpty()) {
			isSelected = false;
			validationString = validationText;
		}
		inputLabel.setText(validationString);
		return isSelected;

	}
	
	//raboti
	// date validation
	public static boolean textDate(DatePicker dateField, Label inputLabel, String validationText) {
		boolean isData = true;
		String validationString = null;

		if (dateField.getValue() == null) {
			isData = false;
			validationString = validationText;
		}

		inputLabel.setText(validationString);
		return isData;

	}

}