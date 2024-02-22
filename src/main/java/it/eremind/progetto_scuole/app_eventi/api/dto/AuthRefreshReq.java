package it.eremind.progetto_scuole.app_eventi.api.dto;

import javax.validation.constraints.NotBlank;

public class AuthRefreshReq {

	@NotBlank
	private String refreshToken;
	
	public String getRefreshToken() {
		return refreshToken;
	}
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

}
