package validations;

import javafx.scene.control.TextField;

public class PinValidation extends TextField {

	public PinValidation() {
		this.setPromptText("Enter PIN");
	}

	@Override
	public void replaceText(int start, int end, String text) {
		if (text.matches("[\\\\d|\\\\s]{8}$") || text.isEmpty())
			super.replaceText(start, end, text);
	}

	@Override
	public void replaceSelection(String replacement) {
		// TODO Auto-generated method stub
		super.replaceSelection(replacement);
	}
}
