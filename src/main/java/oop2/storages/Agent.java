package oop2.storages;

import java.io.Serializable;
import java.util.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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

	@OneToMany(mappedBy = "agent")
	private List<Storage> storageList;

	@Override
	public String toString() {
		return "Agent [user=" + user + ", commission=" + commission + ", rating=" + rating + "]";
	}

	public Agent() {
		super();
	}

	public Agent(User user, Double commission, Double rating) {
		super();
		this.user = user;
		this.commission = commission;
		this.rating = rating;
	}

}