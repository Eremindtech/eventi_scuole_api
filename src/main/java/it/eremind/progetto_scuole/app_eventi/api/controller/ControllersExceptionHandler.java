package it.eremind.progetto_scuole.app_eventi.api.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import it.eremind.progetto_scuole.app_eventi.api.dto.ApiError;
import it.eremind.progetto_scuole.app_eventi.api.dto.ApiErrorDetail;
import it.eremind.progetto_scuole.app_eventi.api.exception.ApiException;
import it.eremind.progetto_scuole.app_eventi.api.security.HrvAuthentication;
import it.eremind.progetto_scuole.app_eventi.api.security.WebSecConfig.ADHandler;
import it.eremind.progetto_scuole.app_eventi.api.security.WebSecConfig.AuthFailureHandler;




/** 
 * Provides custom handling for controllers exceptions. 
 * @author Eri
 */
@ControllerAdvice
public class ControllersExceptionHandler {
	
	
	@Autowired 
	private AuthFailureHandler authFailureHandler;
	@Autowired
	public ADHandler accessDeniedHandler;
	
	
	private Logger logger=LoggerFactory.getLogger(this.getClass().getName());

	
	 
    /**
     * Spring boot default exception thrown in validation of user request parameters validation.
     * @param ex
     * @param request
     * @return
     */
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(value={ MethodArgumentNotValidException.class })
	public final ResponseEntity<ApiError> handleUserValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request, HrvAuthentication auth) {
	    
		HttpHeaders headers = new HttpHeaders();
	    HttpStatus status = HttpStatus.BAD_REQUEST;
	    
	    ApiError resBody=new ApiError();
	    resBody.setMessage("Invalid or missing request field values");
	    resBody.setMessageKey(ApiError.INVALID_FIELDS);
	    
	    List<ApiErrorDetail> errors = new ArrayList<ApiErrorDetail>();
	    resBody.setErrorDetails(errors);
	    
	    for(ObjectError error : ex.getBindingResult().getAllErrors()){
	    	ApiErrorDetail detail = new ApiErrorDetail();
	    	FieldError fe = (FieldError) error;
	    	detail.setFieldName(fe.getField());
	    	detail.setFieldValue(fe.getRejectedValue()==null?null:fe.getRejectedValue().toString());
	    	//detail.setMessage(msgSource.getMessage(error.getDefaultMessage(), null, request.getLocale()));
	    	detail.setMessage(error.getDefaultMessage());
	    	detail.setMessageKey(error.getDefaultMessage());
	    	errors.add(detail);
	    }
	    logger.error("handleUserValidationExceptions: userId="+auth.getUser().getIdUser()+", request validation error: "+resBody.toString());

	    return handleExceptionInternal(resBody, headers, status, request);
	}
	
	
    /**
     * Custom <code>ApiException</code> handling
     * @param ex
     * @param request
     * @return
     */
	@ExceptionHandler(value={ ApiException.class })
	public final ResponseEntity<ApiError> handleApiExceptions(ApiException ex, HttpServletRequest request, HrvAuthentication auth) {
	    
		HttpHeaders headers = new HttpHeaders();
	    
	    ApiError resBody=ex.getApiError();
	    if(resBody==null){
	    	resBody=new ApiError();
		    resBody.setMessage(ex.getMessage());
		    resBody.setMessageKey(ApiError.GENERIC);
		    resBody.setHttpStatus(500);
	    }
	    HttpStatus status = HttpStatus.valueOf(resBody.getHttpStatus()==0?500:resBody.getHttpStatus());

	    logger.error("handleApiExceptions: userId="+auth.getUser().getIdUser()+", httpStatus="+status+" resBody="+resBody.toString());
	    
	    return handleExceptionInternal(resBody, headers, status, request);
	}
	
	
	@ExceptionHandler(value={ AccessDeniedException.class })
	public void handleSpringSecurityAccessDeniedException(AccessDeniedException ex, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {  	
	    //logger.error("handleSpringSecurityAccessDeniedException: msg="+ex.getMessage());
	    this.accessDeniedHandler.handle(request, response, ex);
	}
	
	@ExceptionHandler(value={ AuthenticationException.class })
	public void handleSpringSecurityAuthenticationException(AuthenticationException ex, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {	
	    //logger.error("handleSpringSecurityAuthenticationException: msg="+ex.getMessage());
	    this.authFailureHandler.commence(request, response, ex);;
	}

	
    /**
     * Generic errors 
     * @param ex
     * @param request
     * @return
     */
	@ExceptionHandler(value={ Exception.class })
	public final ResponseEntity<ApiError> handleGenericException(Exception ex, HttpServletRequest request, HrvAuthentication auth) {
		
		HttpHeaders headers = new HttpHeaders();
	    HttpStatus status=HttpStatus.INTERNAL_SERVER_ERROR;
	    
	    ApiError resBody=new ApiError();
	    resBody.setMessage(ex.getMessage()==null?"Generic server error":ex.getMessage());
	    resBody.setMessageKey(ApiError.GENERIC);
	    
	    logger.error("handleGenericException: userId="+auth.getUser().getIdUser()+", msg="+ex.getMessage(), ex);
	    
	    return handleExceptionInternal(resBody, headers, status, request);
	}
	
	
	
    
    
    
    /** A single place to customize the response body of all Exception types. */
    protected ResponseEntity<ApiError> handleExceptionInternal(ApiError body, HttpHeaders headers, HttpStatus status, HttpServletRequest request) {
   	
        return new ResponseEntity<>(body, headers, status);
        
    }
    
    
    




}
