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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ownerID == null) ? 0 : ownerID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Owner other = (Owner) obj;
		if (ownerID == null) {
			if (other.ownerID != null)
				return false;
		} else if (!ownerID.equals(other.ownerID))
			return false;
		return true;
	}

	public Owner(User tempUser) {
		this.user = tempUser;
	}

	public Owner() {
		super();
	}

	public Integer getOwnerID() {
		return ownerID;
	}

	public void setOwnerID(Integer ownerID) {
		this.ownerID = ownerID;
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

	public void setStorageList(List<Storage> storageList) {
		this.storageList = storageList;
	}

}