package it.eremind.progetto_scuole.app_eventi.api.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import it.eremind.progetto_scuole.app_eventi.api.config.AppConfig;
import it.eremind.progetto_scuole.app_eventi.api.dto.ApiError;
import it.eremind.progetto_scuole.app_eventi.api.dto.AuthRefreshReq;
import it.eremind.progetto_scuole.app_eventi.api.dto.AuthReq;
import it.eremind.progetto_scuole.app_eventi.api.dto.AuthRes;
import it.eremind.progetto_scuole.app_eventi.api.exception.ApiException;
import it.eremind.progetto_scuole.app_eventi.api.entity.User;
import it.eremind.progetto_scuole.app_eventi.api.security.ErmAuthentication;
import it.eremind.progetto_scuole.app_eventi.api.security.SecMgr;
import java.text.ParseException;
import java.util.Date;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@RestController
public class AuthController {
	

	protected Logger logger= LoggerFactory.getLogger(this.getClass().getName());	
	
	@Autowired
	private SecMgr secMgr;

		
		
	@RequestMapping(value = AppConfig.AUTH_PATH, method = RequestMethod.POST)
	public ResponseEntity<AuthRes> auth(@Valid @RequestBody AuthReq req)  {
		
		String logPrefix="auth:";						
		logger.debug(logPrefix+"req="+req.toString());

		String userId=req.getUsername();		
		User user=secMgr.auth(userId, req.getPassword());
		logger.debug(logPrefix+"user="+userId);
		
		String token=null;
		String refreshToken=null;
		long exp=0;
		try {
			final SignedJWT jwt=secMgr.generateClientToken(user);
			token = jwt.serialize();
			JWTClaimsSet claimSet=jwt.getJWTClaimsSet();
			if (claimSet!=null) {
				Date expDate=claimSet.getExpirationTime();
				if (expDate!=null) {
					exp=expDate.getTime();
				}
			}
			final SignedJWT refresJwt=secMgr.generateRefreshToken(user);
			refreshToken = refresJwt.serialize();
		} 
		catch (JOSEException|ParseException e) {
			logger.error(logPrefix+e.getMessage(), e);
			ApiError apiError=new ApiError(ApiError.GENERIC, "Internal server error");
			apiError.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			throw new ApiException(apiError);			
		}						

		final AuthRes res=new AuthRes(token, exp, refreshToken);
		return ResponseEntity.ok(res);
	
	}	
	
	@RequestMapping(value = AppConfig.AUTH_PATH+"/refresh", method = RequestMethod.POST)
	public ResponseEntity<AuthRes> authRefresh(@Valid @RequestBody AuthRefreshReq req) {
		
		String logpfx="authRefresh:";
		logger.debug(logpfx+", refreshToken="+req.getRefreshToken());
		ErmAuthentication auth=this.secMgr.refreshAuth(req.getRefreshToken());
		User user=auth.getUser();
		logger.debug(logpfx+", username="+user.getUsername());

		
		String token=null;
		String refreshToken=null;
		long exp=0;
		try {
			final SignedJWT jwt=secMgr.generateClientToken(user);
			token = jwt.serialize();
			JWTClaimsSet claimSet=jwt.getJWTClaimsSet();
			if (claimSet!=null) {
				Date expDate=claimSet.getExpirationTime();
				if (expDate!=null) {
					exp=expDate.getTime();
				}
			}
			final SignedJWT nRefreshJwt=secMgr.generateRefreshToken(user);
			refreshToken = nRefreshJwt.serialize();
		} 
		catch (JOSEException|ParseException e) {
			logger.error(logpfx+e.getMessage(), e);
			ApiError apiError=new ApiError(ApiError.GENERIC, "Internal server error");
			apiError.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			throw new ApiException(apiError);			
		}						

		final AuthRes res=new AuthRes(token, exp, refreshToken);
		return ResponseEntity.ok(res);
	
	}	

	
    
}
