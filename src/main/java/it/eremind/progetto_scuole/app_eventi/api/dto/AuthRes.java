package it.eremind.progetto_scuole.app_eventi.api.dto;

import java.util.Date;

public class AuthRes {
	
	protected String clientToken;
	protected long exp;
	protected Date expDateTime;	
	
	private String refreshToken;
	
	
	public AuthRes() {}
	public AuthRes(String token, long exp, String refreshToken) {
		this.clientToken=token;
		this.exp=exp;
		this.expDateTime=new Date();
		expDateTime.setTime(exp);
		this.refreshToken=refreshToken;
	}	
	
	
	public String getClientToken() {
		return clientToken;
	}
	public void setClientToken(String clientToken) {
		this.clientToken = clientToken;
	}

	public long getExp() {
		return exp;
	}
	public void setExp(long exp) {
		this.exp = exp;
	}
	public Date getExpDateTime() {
		return expDateTime;
	}
	public void setExpDateTime(Date expDateTime) {
		this.expDateTime = expDateTime;
	}
	public String getRefreshToken() {
		return refreshToken;
	}
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	

}
