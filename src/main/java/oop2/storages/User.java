package oop2.storages;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;

@Entity
@Table(name = "user")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_user")
	private Integer userID = null;

	@Column(name = "account_name", unique = true)
	private String accountName;

	@Column(name = "account_password")
	private String accountPassword;

	@Column(name = "person_name")
	private String personName;

	@Column(name = "pin", unique = true)
	private String pin;

	@Column(name = "status_login", unique = true)
	private Boolean statusLogin;

	@OneToOne(targetEntity = Owner.class, mappedBy = "user")
	@Cascade(value = org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	private Owner owner;

	@OneToOne(targetEntity = Agent.class, mappedBy = "user")
	@Cascade(value = org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	private Agent agent;

	public User() {
		super();
	}

	public User(String accountName, String accountPassword, String personName, String pin) {
		super();
		this.accountName = accountName;
		this.accountPassword = accountPassword;
		this.personName = personName;
		this.pin = pin;
	}

	public String getAccountPassword() {
		return accountPassword;
	}

	public void setAccountPassword(String accountPassword) {
		this.accountPassword = accountPassword;
	}

	public String getPersonName() {
		return personName;
	}

	public void setPersonName(String personName) {
		this.personName = personName;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public Integer getUserID() {
		return userID;
	}

	public String getAccountName() {
		return accountName;
	}

	@Override
	public String toString() {
		return "User [userID=" + userID + ", accountName=" + accountName + ", accountPassword=" + accountPassword
				+ ", personName=" + personName + ", pin=" + pin + "]";
	}

}