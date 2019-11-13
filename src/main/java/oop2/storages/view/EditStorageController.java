package oop2.storages.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import oop2.storages.Agent;
import oop2.storages.Category;
import oop2.storages.StorageType;

public class EditStorageController {
	
	@FXML
	Button editStorage;
	
	@FXML
	ComboBox<Agent> editAgent;
	
	@FXML
	ComboBox<StorageType> editType;
	
	@FXML
	ComboBox<Category> editCategory;
	
	@FXML
	TextField editSize;
	
	@FXML
	TextField editClConditions;
	
	public void editStorage(ActionEvent event) {
		
		
	}
	
	public void keyPressed(KeyEvent event) {

			Control[] focusOrder = new Control[] { editAgent, editType, editCategory, editSize, editClConditions };

			for (int i = 0; i < focusOrder.length - 1; i++) {
				Control nextControl = focusOrder[i + 1];
				focusOrder[i].addEventHandler(ActionEvent.ACTION, e -> nextControl.requestFocus());
			}

		}
	

}
