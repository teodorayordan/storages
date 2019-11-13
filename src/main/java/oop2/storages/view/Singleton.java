package oop2.storages.view;

import oop2.storages.Agent;
import oop2.storages.Contract;
import oop2.storages.Owner;
import oop2.storages.Storage;
import oop2.storages.User;


public class Singleton {
	private static Singleton instance = new Singleton();

	public static Singleton getInstance() {
		return instance;
	}

	private User user;
	private Owner owner;
	private Agent agent;
	private Storage storage;
	private Contract contract;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Owner getOwner() {
		return owner;
	}

	public void setOwner(Owner owner) {
		this.owner = owner;
	}

	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	public Storage getStorage() {
		return storage;
	}

	public void setStorage(Storage storage) {
		this.storage = storage;
	}

	public Contract getContract() {
		return contract;
	}

	public void setContract(Contract contract) {
		this.contract = contract;
	}
	
	

}
