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

import it.orobicalab.hrv.dto.ApiError;
import it.orobicalab.hrv.dto.AuthRefreshReq;
import it.orobicalab.hrv.dto.AuthReq;
import it.orobicalab.hrv.dto.AuthRes;
import it.orobicalab.hrv.web.exception.ApiException;
import it.orobicalab.hrv.web.model.db.User;
import it.orobicalab.hrv.web.security.HrvAuthentication;
import it.orobicalab.hrv.web.security.SecMgr;
import it.orobicalab.hrv.web.security.WebSecConfig;
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

		
		
	@RequestMapping(value = WebSecConfig.AUTH_PATH+"/app", method = RequestMethod.POST)
	public ResponseEntity<AuthRes> authApp(@Valid @RequestBody AuthReq req)  {
		
		String logPrefix="authApp:";						
		logger.debug(logPrefix+"req="+req.toString());

		String userId=req.getUserId();		
		User user=secMgr.auth(userId, req.getPassword());
		logger.debug(logPrefix+"user="+userId);
		
		String token=null;
		String refreshToken=null;
		long exp=0;
		try {
			final SignedJWT jwt=secMgr.generateClientToken(user, User.CLIENT_TYPE_APP);
			token = jwt.serialize();
			JWTClaimsSet claimSet=jwt.getJWTClaimsSet();
			if (claimSet!=null) {
				Date expDate=claimSet.getExpirationTime();
				if (expDate!=null) {
					exp=expDate.getTime();
				}
			}
			final SignedJWT refresJwt=secMgr.generateRefreshToken(user, User.CLIENT_TYPE_APP);
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
	
	@RequestMapping(value = WebSecConfig.AUTH_PATH+"/refresh/app", method = RequestMethod.POST)
	public ResponseEntity<AuthRes> authAppRefresh(@Valid @RequestBody AuthRefreshReq req) {
		
		String logpfx="authAppRefresh:";
		logger.debug(logpfx+", refreshToken="+req.getRefreshToken());
		HrvAuthentication auth=this.secMgr.refreshAuth(req.getRefreshToken());
		User user=auth.getUser();
		logger.debug(logpfx+", idUser="+user.getIdUser());

		
		String token=null;
		String refreshToken=null;
		long exp=0;
		try {
			final SignedJWT jwt=secMgr.generateClientToken(user, User.CLIENT_TYPE_APP);
			token = jwt.serialize();
			JWTClaimsSet claimSet=jwt.getJWTClaimsSet();
			if (claimSet!=null) {
				Date expDate=claimSet.getExpirationTime();
				if (expDate!=null) {
					exp=expDate.getTime();
				}
			}
			final SignedJWT nRefreshJwt=secMgr.generateRefreshToken(user, User.CLIENT_TYPE_APP);
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
	
	@RequestMapping(value = WebSecConfig.AUTH_PATH+"/tws-client", method = RequestMethod.POST)
	public ResponseEntity<AuthRes> authTwsClient(@Valid @RequestBody AuthReq req)  {
		
		String logPrefix="authTwsClient:";						
		logger.debug(logPrefix+"req="+req.toString());

		String userId=req.getUserId();		
		User user=secMgr.authTwsClient(userId, req.getPassword());
		logger.debug(logPrefix+" user="+userId);
		
		String token=null;
		String refreshToken=null;
		long exp=0;
		try {
			final SignedJWT jwt=secMgr.generateClientToken(user, User.CLIENT_TYPE_TWS);
			token = jwt.serialize();
			JWTClaimsSet claimSet=jwt.getJWTClaimsSet();
			if (claimSet!=null) {
				Date expDate=claimSet.getExpirationTime();
				if (expDate!=null) {
					exp=expDate.getTime();
				}
			}
			final SignedJWT refreshJwt=secMgr.generateRefreshToken(user, User.CLIENT_TYPE_TWS);
			refreshToken = refreshJwt.serialize();
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
	
    
}
