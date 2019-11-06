package oop2.storages;

import java.io.Serializable;
import java.util.*;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "owner")
public class Owner implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_owner")
	private Integer ownerID = null;

	@MapsId
	@OneToOne(targetEntity = User.class, cascade = CascadeType.ALL)
	@JoinColumn(name = "id_owner")
	private User user;

	@OneToMany(mappedBy = "owner")
	private List<Storage> storageList;

	@Override
	public String toString() {
		return "Owner [ownerID=" + ownerID + ", user=" + user + "]";
	}

	public Owner(User tempUser) {
		this.user = tempUser;
	}

	public Owner() {
		super();
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<Storage> getStorageList() {
		return storageList;
	}

}