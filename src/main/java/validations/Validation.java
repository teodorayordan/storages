package validations;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class Validation {

	// account name and account password
	public static boolean textAlphabet(TextField inputTextField, Label inputLabel, String validationText) {
		boolean isAlphabet = true;
		String validationString = null;

		if (!inputTextField.getText().matches("^[a-zA-Z0-9_-]{4,25}$")) {
			isAlphabet = false;
			validationString = validationText;

		}
		inputLabel.setText(validationString);

		return isAlphabet;

	}

	// user and renter first letter capital
	public static boolean textAlphabetFirstCapital(TextField inputTextField, Label inputLabel, String validationText) {
		boolean isAlphabet = true;
		String validationString = null;
  
		if (!inputTextField.getText().matches("[A-Z]{1}[a-z]+[ ]+[A-Z]{1}[a-z]{1,25}")) {
			isAlphabet = false;
			validationString = validationText;

		}
		inputLabel.setText(validationString);

		return isAlphabet;

	}

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
	
	//ne raboti
	//category and type
	public static boolean textLetters(TextField inputTextField, Label inputLabel, String validationText) {
		boolean isAlphabet = true;
		String validationString = null;

		if (!inputTextField.getText().matches("^[a-zA-Z ]{2,30}$")) {
			isAlphabet = false;
			validationString = validationText;

		}
		inputLabel.setText(validationString);
		return isAlphabet;

	}

	// address and climate conditions validation
	public static boolean textAddress(TextField inputTextField, Label inputLabel, String validationText) {
		boolean isNumeric = true;
		String validationString = null;

		if (!inputTextField.getText().matches("[A-Za-z0-9'\\.\\-\\s\\,]{2,25}")) {
			isNumeric = false;
			validationString = validationText;

		}
		inputLabel.setText(validationString);
		return isNumeric;

	}

}