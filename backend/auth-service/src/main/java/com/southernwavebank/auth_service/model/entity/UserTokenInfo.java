package com.southernwavebank.auth_service.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTokenInfo {
	@Id
    private String emailId;

    private Integer tokenVersion;
}
