package it.eremind.progetto_scuole.app_eventi.api.dto;

import java.io.Serializable;


public class ApiErrorDetail implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String fieldName;
	private String fieldValue;	
	private String messageKey;
	private String message;	
	
	
	@Override
	public String toString(){
		StringBuilder sb=new StringBuilder(1024);
		sb.append("{")
		.append("fieldName:").append(fieldName).append(";")
		.append("fieldValue:").append(fieldValue).append(";")
		.append("messageKey:").append(messageKey).append(";")
		.append("message:").append(message).append(";")
		.append("}");
		return sb.toString();
	}


	public String getMessageKey() {
		return messageKey;
	}
	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}		
	
	public String getFieldValue() {
		return fieldValue;
	}
	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	

}
