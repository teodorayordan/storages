package validations;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import javafx.embed.swing.JFXPanel;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ValidationTest {

	@BeforeClass //izpulnqva se predi pochvaneto na testovete
	public static void initToolkit() throws InterruptedException {
		new JFXPanel(); // inicializirame JavaFx Runtime za da moje da polzvame elementite TextField i Label
	}

	@Test
	public void testTextAlphabetFirstCapital() {
		Label label = new Label();
		
		TextField textField = new TextField("Some Test");
		boolean test = Validation.textAlphabetFirstCapital(textField, label, "SomeText");
		assertTrue("Test Failed", test);
		
		TextField textField2 = new TextField("some Test");
		boolean test2 = Validation.textAlphabetFirstCapital(textField2, label, "SomeText");
		assertFalse("Test Failed2", test2);
		
		TextField textField3 = new TextField("Some");
		boolean test3 = Validation.textAlphabetFirstCapital(textField3, label, "SomeText");
		assertFalse("Test Failed3", test3);
		
		TextField textField4 = new TextField("123");
		boolean test4 = Validation.textAlphabetFirstCapital(textField4, label, "SomeText");
		assertFalse("Test Failed4", test4);
	}

	@Test
	public void testTextPin() {
		Label label = new Label();
		
		TextField textField = new TextField("1234567890");
		boolean test = Validation.textPin(textField, label, "SomeText");
		assertTrue("Test Failed", test);
		
		TextField textField2 = new TextField("1234567890a");
		boolean test2 = Validation.textPin(textField2, label, "SomeText");
		assertFalse("Test Failed2", test2);
		
		TextField textField3 = new TextField("1234567890123456");
		boolean test3 = Validation.textPin(textField3, label, "SomeText");
		assertFalse("Test Failed3", test3);
		
		TextField textField4 = new TextField("123");
		boolean test4 = Validation.textPin(textField4, label, "SomeText");
		assertFalse("Test Failed4", test4);
	}

	@Test
	public void testTextCommission() {
		Label label = new Label();
		
		TextField textField = new TextField("25.02");
		boolean test = Validation.textCommission(textField, label, "SomeText");
		assertTrue("Test Failed", test);
		
		TextField textField2 = new TextField("25");
		boolean test2 = Validation.textCommission(textField2, label, "SomeText");
		assertTrue("Test Failed2", test2);
		
		TextField textField3 = new TextField(".25");
		boolean test3 = Validation.textCommission(textField3, label, "SomeText");
		assertTrue("Test Failed3", test3);
		
		TextField textField4 = new TextField("25.");
		boolean test4 = Validation.textCommission(textField4, label, "SomeText");
		assertFalse("Test Failed4", test4);
		
		TextField textField5 = new TextField("25.02.02");
		boolean test5 = Validation.textCommission(textField5, label, "SomeText");
		assertFalse("Test Failed5", test5);
		
		TextField textField6 = new TextField("25.0a");
		boolean test6 = Validation.textCommission(textField6, label, "SomeText");
		assertFalse("Test Failed6", test6);
	}

	@Test
	public void testTextLetters() {
		Label label = new Label();
		
		TextField textField = new TextField("Justsometext");
		boolean test = Validation.textLetters(textField, label, "SomeText");
		assertTrue("Test Failed", test);
		
		TextField textField2 = new TextField("moretext");
		boolean test2 = Validation.textLetters(textField2, label, "SomeText");
		assertTrue("Test Failed2", test2);
		
		TextField textField3 = new TextField("Text3");
		boolean test3 = Validation.textLetters(textField3, label, "SomeText");
		assertFalse("Test Failed3", test3);
	}

	@Test
	public void testTextAddress() {
		Label label = new Label();
		
		TextField textField = new TextField("5th Street St. Peter B-vd");
		boolean test = Validation.textAddress(textField, label, "SomeText");
		assertTrue("Test Failed", test);
		
		TextField textField2 = new TextField("Some Other Street 16th");
		boolean test2 = Validation.textAddress(textField2, label, "SomeText");
		assertTrue("Test Failed2", test2);
	}

}
