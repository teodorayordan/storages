package oop2.storages;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "notification")
public class Notification {
	@Id
	@Column(name = "id_notification")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer notificationID = null;

	@ManyToOne
	@JoinColumn(name = "id_user", nullable = false)
	private User user;

	@Column(name = "notification_text")
	private String notificationText;

	@Column(name = "notification_status")
	private Boolean notificationStatus;

	public Notification() {

	}

	public Notification(User user, String notificationText) {
		this.user = user;
		this.notificationText = notificationText;
		this.notificationStatus = true;
	}

	@Override
	public String toString() {
		return "Notification [notificationID=" + notificationID + ", user=" + user + ", notificationText="
				+ notificationText + ", notificationStatus=" + notificationStatus + "]";
	}

	public Integer getNotificationID() {
		return notificationID;
	}

	public void setNotificationID(Integer notificationID) {
		this.notificationID = notificationID;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getNotificationText() {
		return notificationText;
	}

	public void setNotificationText(String notificationText) {
		this.notificationText = notificationText;
	}

	public Boolean getNotificationStatus() {
		return notificationStatus;
	}

	public void setNotificationStatus(Boolean notificationStatus) {
		this.notificationStatus = notificationStatus;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Notification other = (Notification) obj;
		if (notificationStatus == null) {
			if (other.notificationStatus != null)
				return false;
		} else if (!notificationStatus.equals(other.notificationStatus))
			return false;
		if (notificationText == null) {
			if (other.notificationText != null)
				return false;
		} else if (!notificationText.equals(other.notificationText))
			return false;
		return true;
	}
}