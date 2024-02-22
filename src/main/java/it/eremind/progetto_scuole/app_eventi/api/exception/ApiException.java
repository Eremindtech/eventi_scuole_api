package it.eremind.progetto_scuole.app_eventi.api.exception;

import java.util.List;

import it.eremind.progetto_scuole.app_eventi.api.dto.ApiError;
import it.eremind.progetto_scuole.app_eventi.api.dto.ApiErrorDetail;

public class ApiException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	protected ApiError apiError;
	
	
	public ApiException(ApiError apiError) {
		super(apiError.getMessage());
		this.apiError=apiError;
				
	}

	public ApiException() {
		super();
		
	}

	public ApiException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public ApiException(String message) {
		super(message);
		
	}

	public ApiException(ApiError apiError, Exception pe) {
		super(apiError.getMessage(), pe);
		this.apiError=apiError;
	}

	public ApiError getApiError() {
		return apiError;
	}

	public void setApiError(ApiError apiError) {
		this.apiError = apiError;
	}
	
	public String getDebugMessage() {
		
		String msg=super.getMessage();
		if (this.apiError==null || this.apiError.getErrorDetails()==null || this.apiError.getErrorDetails().size()==0) {
			return msg;
		}
		
		StringBuilder sb=new StringBuilder(256);
		sb.append(this.apiError.getMessage());
		
		List<ApiErrorDetail> dtls=this.apiError.getErrorDetails();
		for (ApiErrorDetail dtl:dtls) {
			sb.append(";").append(dtl.getMessage());
		}
		
		return sb.toString();
		
		
	}
	
	
	
}