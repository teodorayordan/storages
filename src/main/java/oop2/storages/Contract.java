package oop2.storages;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "contract")
public class Contract {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_contract")
	private Integer contractID = null;

	@ManyToOne
	@JoinColumn(name = "id_storage_agent", nullable = false)
	private Agent agent;

	@ManyToOne
	@JoinColumn(name = "id_storage", nullable = false)
	private Storage storage;

	@Column(name = "contract_start_date")
	private Date startDate;

	@Column(name = "contract_end_date")
	private Date endDate;

	@Column(name = "price")
	private Double price = null;

	@Column(name = "renter_name")
	private String renterName = null;

	@Column(name = "renter_pin")
	private String renterPin = null;

	public Contract() {
		super();
	}

	public Contract(Agent agent, Storage storage, Date startDate, Date endDate, Double price, String renterName,
			String renterPin) {
		super();
		this.agent = agent;
		this.storage = storage;
		this.startDate = startDate;
		this.endDate = endDate;
		this.price = price;
		this.renterName = renterName;
		this.renterPin = renterPin;
	}

	@Override
	public String toString() {
		return "Contract [contractID=" + contractID + ", agent=" + agent + ", storage=" + storage + ", startDate="
				+ startDate + ", endDate=" + endDate + ", price=" + price + ", renterName=" + renterName
				+ ", renterPin=" + renterPin + "]";
	}

}
