package oop2.storages;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Table(name = "storage")
public class Storage {

	@Id
	@Column(name = "id_storage")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer storageID = null;

	@ManyToOne
	@JoinColumn(name = "id_owner", nullable = false)
	@NotFound(action = NotFoundAction.IGNORE)
	private Owner owner;

	/*@ManyToOne
	@JoinColumn(name = "id_storage_agent", nullable = false)
	@NotFound(action = NotFoundAction.IGNORE)
	private Agent agent;*/

	@ManyToOne
	@JoinColumn(name = "id_storage_type", nullable = false)
	private StorageType storageType;

	@ManyToOne
	@JoinColumn(name = "id_category", nullable = false)
	private Category category;

	@Column(name = "size")
	private Double storageSize = null;

	@Column(name = "climate_conditions")
	private String climateConditions;

	@Column(name = "address")
	private String storageAddress;

	@Column(name = "status")
	private Boolean storageStatus;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "chosen_agents", joinColumns = { @JoinColumn(name = "id_storage") }, inverseJoinColumns = {
			@JoinColumn(name = " id_storage_agent") })
	private List<Agent> agentList;

	
	
	public Storage(Owner owner, StorageType storageType, Category category, Double storageSize,
			String climateConditions, String storageAddress, List<Agent> agentList) {
		super();
		this.owner = owner;
		this.storageType = storageType;
		this.category = category;
		this.storageSize = storageSize;
		this.climateConditions = climateConditions;
		this.storageAddress = storageAddress;
		this.storageStatus = false;
		this.agentList = agentList;
	}

	/*public Storage(Owner owner, Agent agent, StorageType storageType, Category category, Double storageSize,
			String climateConditions, String storageAddress, Boolean storageStatus) {
		super();
		this.owner = owner;
		this.agent = agent;
		this.storageType = storageType;
		this.category = category;
		this.storageSize = storageSize;
		this.climateConditions = climateConditions;
		this.storageAddress = storageAddress;
		this.storageStatus = storageStatus;
	}*/

	public Storage() {

	}

	public Integer getStorageID() {
		return storageID;
	}

	public void setStorageID(Integer storageID) {
		this.storageID = storageID;
	}

	public Owner getOwner() {
		return owner;
	}

	public void setOwner(Owner owner) {
		this.owner = owner;
	}

	/*public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}*/

	public StorageType getStorageType() {
		return storageType;
	}

	public void setStorageType(StorageType storageType) {
		this.storageType = storageType;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Double getStorageSize() {
		return storageSize;
	}

	public void setStorageSize(Double storageSize) {
		this.storageSize = storageSize;
	}

	public String getClimateConditions() {
		return climateConditions;
	}

	public void setClimateConditions(String climateConditions) {
		this.climateConditions = climateConditions;
	}

	public String getStorageAddress() {
		return storageAddress;
	}

	public void setStorageAddress(String storageAddress) {
		this.storageAddress = storageAddress;
	}

	public Boolean getStorageStatus() {
		return storageStatus;
	}

	public void setStorageStatus(Boolean storageStatus) {
		this.storageStatus = storageStatus;
	}

	public List<Agent> getAgentList() {
		return agentList;
	}

	public void setAgentList(List<Agent> agentList) {
		this.agentList = agentList;
	}
	
	public void addAgent(Agent agent) {
		this.agentList.add(agent);
	}
	
	public void removeAgent(Agent agent) {
		this.agentList.remove(agent);
	}

	@Override
	public String toString() {
		return "Storage [storageID=" + storageID + ", owner=" + owner + ", storageType=" + storageType + ", category="
				+ category + ", storageSize=" + storageSize + ", climateConditions=" + climateConditions
				+ ", storageAddress=" + storageAddress + ", storageStatus=" + storageStatus + ", agentList=" + agentList
				+ "]";
	}

	/*@Override
	public String toString() {
		return "Storage [storageID=" + storageID + ", owner=" + owner + ", agent=" + agent + ", storageType="
				+ storageType + ", category=" + category + ", storageSize=" + storageSize + ", climateConditions="
				+ climateConditions + ", storageAddress=" + storageAddress + ", storageStatus=" + storageStatus + "]";
	}*/

	
}
