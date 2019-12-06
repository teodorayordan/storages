package oop2.storages.view;

import static org.junit.Assert.*;

import org.junit.Test;

import oop2.storages.User;

public class ControllerTest {

	@Test
	public void testProfileCheck() {
		String acc = "acc1";
		String pass = "acc1";
		User user = Controller.profileCheck(acc, pass);
		assertNotNull("Test", user);
		
		String acc2 = "admin";
		String pass2 = "admin";
		User user2 = Controller.profileCheck(acc2, pass2);
		assertNull("Test", user2);
	}

}
