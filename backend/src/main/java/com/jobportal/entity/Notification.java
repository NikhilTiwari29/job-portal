package com.jobportal.entity;


import com.jobportal.dto.NotificationDTO;
import com.jobportal.dto.NotificationStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Notification {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long userId;
	private String message;
	private String action;
	private String route;
	private NotificationStatus status;
	private LocalDateTime timestamp;
	public NotificationDTO toDTO() {
		return new NotificationDTO (this.id, this.userId, this.message, this.action, this.route, this.status, this.timestamp);
	}
}
