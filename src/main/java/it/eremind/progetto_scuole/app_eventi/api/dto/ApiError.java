package it.eremind.progetto_scuole.app_eventi.api.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;


public class ApiError implements Serializable {

	private static final long serialVersionUID = 1L;


	public static final String MEASURES_ALREADY_SAVED = "error.measures.already-saved";
	public static final String INVALID_FIELDS = "error.validation.invalid-fields";
	public static final String GENERIC = "error.generic";
	public static final String ACCESS_DENIED ="error.access-denied";
	public static final String SESSION_NOT_PAUSED ="error.trade-session.not-paused";
	public static final String SESSION_ALREADY_PAUSED = "error.trade-session.already-paused";
	public static final String SESSION_ALREADY_STOPPED = "error.trade-session.already-stopped";
	public static final String SESSION_NOT_ACTIVE = "error.trade-session.not-active";
	public static final String SESSION_NOT_PLAYING = "error.trade-session.not-playing";
	public static final String INCORRECTLY_CLOSED_SESSION = "error.trade-session.incorrectly-closed";

	public static final String DUPLICATE_HR_DEVICE = "error.hr-monitor-device.insert.duplicate";

	// Auth - Login
	public static final String INVALID_CREDENTIALS = "error.auth.invalid-credentials";
	public static final String USER_LOCKED = "error.auth.user-locked";

	public static final String AUTH_ERROR = "error.auth.failed";







	protected String messageKey;
	protected String message;	
	protected List<ApiErrorDetail> errorDetails;
	
	@JsonIgnore
	protected int httpStatus;	
	

	public ApiError() {
	}
	public ApiError(String message) {
		this.message=message;
	}
	public ApiError(String messageKey, String message) {
		this.messageKey=messageKey;
		this.message=message;
	}
	public ApiError(String messageKey, String message, int httpStatus) {
		this.messageKey=messageKey;
		this.message=message;
		this.httpStatus=httpStatus;	
	}
	public ApiError(String messageKey, String message, int httpStatus, ApiErrorDetail err) {
		this.messageKey=messageKey;
		this.message=message;
		this.httpStatus=httpStatus;
		if(err!=null){
			this.errorDetails=new ArrayList<>(1);
			errorDetails.add(err);
		}			
	}

	
	@Override
	public String toString(){
		StringBuilder sb=new StringBuilder(1024);
		sb.append("{")
		.append("messageKey:").append(messageKey).append(";")
		.append("message:").append(message).append(";")
		.append("errors:")
		.append("[");
		if(this.errorDetails!=null){
			for (ApiErrorDetail err:this.errorDetails){
				sb.append(err.toString());
			}
		}
		sb.append("]")
		.append("}");
		return sb.toString();
	}


	/**
	 * @return the messageKey
	 */
	public String getMessageKey() {
		return messageKey;
	}

	/**
	 * @param messageKey the messageKey to set
	 */
	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the httpStatus
	 */
	public int getHttpStatus() {
		return httpStatus;
	}

	/**
	 * @param httpStatus the httpStatus to set
	 */
	public void setHttpStatus(int httpStatus) {
		this.httpStatus = httpStatus;
	}

	/**
	 * @return the errorDetails
	 */
	public List<ApiErrorDetail> getErrorDetails() {
		return errorDetails;
	}

	/**
	 * @param errorDetails the errorDetails to set
	 */
	public void setErrorDetails(List<ApiErrorDetail> errorDetails) {
		this.errorDetails = errorDetails;
	}	
	

	
	
}
