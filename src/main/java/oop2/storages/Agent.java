package oop2.storages;

import java.io.Serializable;
import java.util.*;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.MapsId;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.CascadeType;
import javax.persistence.Column;

@Entity
@Table(name = "storage_agent")
public class Agent implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_storage_agent")
	private Integer agentID = null;

	@MapsId
	@OneToOne(targetEntity = User.class, cascade = CascadeType.ALL)
	@JoinColumn(name = "id_storage_agent")
	private User user;

	@Column(name = "commission")
	private Double commission = null;

	@Column(name = "rating")
	private Double rating = null;

	@ManyToMany(mappedBy = "agentList", fetch = FetchType.EAGER)
	private List<Storage> storageList;

	@Override
	public String toString() {
		return "Agent [user=" + user + ", commission=" + commission + ", rating=" + rating + "]";
	}

	public Agent() {
		setRating();
	}

	public Agent(User user, Double commission) {
		super();
		this.user = user;
		this.commission = commission;
		setRating();
	}

	public Integer getAgentID() {
		return agentID;
	}

	public void setAgentID(Integer agentID) {
		this.agentID = agentID;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Double getCommission() {
		return commission;
	}

	public void setCommission(Double commission) {
		this.commission = commission;
	}

	public Double getRating() {
		return rating;
	}

	public void setRating() {
		this.rating = 5.0;
	}

	public List<Storage> getStorageList() {
		return storageList;
	}

	public void setStorageList(List<Storage> storageList) {
		this.storageList = storageList;
	}

}
