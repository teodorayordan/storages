package oop2.storages;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "storage_type")
public class StorageType {
	@Id
	@Column(name = "id_storage_type")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer typeID = null;

	@Column(name = "type_name")
	private String typeName;

	@Override
	public String toString() {
		return "StorageType [typeID=" + typeID + ", typeName=" + typeName + "]";
	}

	public StorageType(String typeName) {
		super();
		this.typeName = typeName;
	}

	public StorageType() {
		super();
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	
	

}
