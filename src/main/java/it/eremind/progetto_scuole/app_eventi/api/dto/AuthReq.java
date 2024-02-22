package it.eremind.progetto_scuole.app_eventi.api.dto;

import javax.validation.constraints.NotBlank;

/**
 * Corrisponde alla login user
 * @author Eri
 */
public class AuthReq {
	
	@NotBlank
	private String userId;
	@NotBlank
	private String password;
	
	@Override
	public String toString(){
		StringBuilder sb=new StringBuilder(512);
		sb.append("{")
		.append("userId:").append(userId).append(";")
		.append("password.length:").append(password.length()).append(";")
		.append("}");
		return sb.toString();
	}
	
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}


}
