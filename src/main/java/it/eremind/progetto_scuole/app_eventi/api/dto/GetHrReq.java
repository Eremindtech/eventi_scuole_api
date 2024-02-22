package it.eremind.progetto_scuole.app_eventi.api.dto;

import java.util.Date;

import javax.validation.constraints.NotBlank;

public class GetHrReq implements IntervalReqI, UserOperationI {
	
	@NotBlank
	private String idUser;
	
	private Date ini;
	private Date end;
	
	
	public GetHrReq() {}
	public GetHrReq(String idUser) {
		this.idUser=idUser;
	}
	public GetHrReq(String idUser, Date ini, Date end) {
		this.idUser=idUser;
		this.ini=ini;
		this.end=end;
	}
	
	
	public Date getIni() {
		return ini;
	}
	public void setIni(Date ini) {
		this.ini = ini;
	}
	public Date getEnd() {
		return end;
	}
	public void setEnd(Date end) {
		this.end = end;
	}
	public String getIdUser() {
		return idUser;
	}
	public void setIdUser(String idUser) {
		this.idUser = idUser;
	}
	
	


}
