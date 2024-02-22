package it.eremind.progetto_scuole.app_eventi.api.security;

import java.net.URL;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.fasterxml.uuid.impl.TimeBasedGenerator;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTClaimsSet.Builder;
import com.nimbusds.jwt.SignedJWT;

import it.orobicalab.hrv.web.dao.UserRepo;
import it.orobicalab.hrv.web.model.db.User;



public class SecMgr implements OAuth2TokenValidator<Jwt>, Converter<Jwt, AbstractAuthenticationToken> {

				
	static Logger logger = LoggerFactory.getLogger(SecMgr.class);		
	
	private RSAKey rsaJWK;
	private JWSAlgorithm algo;
	
	@Value("${auth.jwt.validity-secs:3600}")
	private int tokenValiditySecs;	
	@Value("${auth.jwt-refresh.validity-secs:7776000}")
	private int refreshTokenValiditySecs;	
	@Value("${auth.jwt.max-clock-skew-secs:60}")
	private int maxClockSkewSecs;			
	@Value("${auth.jwt.issuer-uri:https://eremind.it}")
	private String issuerUri;	
	@Value("${auth.max-auth-errors:5}")
	private int maxAuthErrors;	
	
	@Autowired
	private UserRepo userRepo;	

	@Autowired
	private PasswordEncoder passwordEncoder;			
	@Autowired
	private TimeBasedGenerator uuidGenerator;		
		
	private JwtDecoder jwtDecoder;
	
	
    @Value("${spring.profiles.active:}")
    private String activeProfiles;
    private String profile;
	
	
	public SecMgr(RSAKey rsaJWK) {	
		this.rsaJWK=rsaJWK;
		this.algo=(JWSAlgorithm) rsaJWK.getAlgorithm();
		if (this.algo==null) {
			this.algo=JWSAlgorithm.RS256;
		}
		this.profile=StringUtils.hasText(activeProfiles)?activeProfiles.split(";")[0]:"";
	}
	
	
	@PostConstruct
	void init() {
		System.out.println("SecMgr: init: tokenValiditySecs:"+tokenValiditySecs);
		logger.info("init: tokenValiditySecs:"+tokenValiditySecs+", refreshTokenValiditySecs="+this.refreshTokenValiditySecs);
	}
	
	
	public void setJwtDecoder(JwtDecoder jwtDecoder){
		this.jwtDecoder=jwtDecoder;
	}
	
	
	
	public SignedJWT generateClientToken(User user, String clientType) throws JOSEException {
		
		String logPrefix="generateClientToken: ";
		
		String userId=user.getIdUser();
		logger.debug(logPrefix+"userId="+userId+", clientType="+clientType);
		
		// Create HMAC signer
		RSASSASigner signer = new RSASSASigner(rsaJWK);
		
		GregorianCalendar cal=new GregorianCalendar();
		Date now=cal.getTime();
		long l=cal.getTimeInMillis();
		l+=tokenValiditySecs*1000;
		cal.setTimeInMillis(l);
		Date expDate=cal.getTime();
		
		String clientTokenId=uuidGenerator.generate().toString();
		
		Builder claimsBuilder = new JWTClaimsSet.Builder()
        .subject(userId)
        .issueTime(now)
        .expirationTime(expDate)
        .jwtID(clientTokenId)
        .issuer(issuerUri)
        .claim("client_type", clientType)
        .claim("refresh", "false");

		
		SignedJWT signedJWT = new SignedJWT(
				new JWSHeader.Builder(algo).keyID(rsaJWK.getKeyID()).build(), 
				claimsBuilder.build()
				);
		signedJWT.sign(signer);
		
		logger.debug(logPrefix+"userId="+userId+", issueTime="+now+", expirationTime="+expDate+", l="+l+", tokenValiditySecs="+this.tokenValiditySecs);

		return signedJWT;		
		
	}
	
	
	public SignedJWT generateRefreshToken(User user, String clientType) throws JOSEException {
		
		String logPrefix="generateRefreshToken: ";
		String userId=user.getIdUser();
		logger.debug(logPrefix+"userId="+userId+", clientType="+clientType);
		
		// Create HMAC signer
		RSASSASigner signer = new RSASSASigner(rsaJWK);
		
		GregorianCalendar cal=new GregorianCalendar();
		Date now=cal.getTime();
		long l=cal.getTimeInMillis();
		l+=this.refreshTokenValiditySecs*1000L;
		cal.setTimeInMillis(l);
		Date expDate=cal.getTime();
		
		String clientTokenId=uuidGenerator.generate().toString();
		
		Builder claimsBuilder = new JWTClaimsSet.Builder()
        .subject(userId)
        .issueTime(now)
        .expirationTime(expDate)
        .jwtID(clientTokenId)
        .issuer(issuerUri)
        .claim("client_type", clientType)
        .claim("refresh", "true");
		
		SignedJWT signedJWT = new SignedJWT(
				new JWSHeader.Builder(algo).keyID(rsaJWK.getKeyID()).build(), 
				claimsBuilder.build()
				);
		signedJWT.sign(signer);
		
		logger.debug(logPrefix+"userId="+userId+", issueTime="+now+", expirationTime="+expDate+", l="+l+", refreshTokenValiditySecs="+this.refreshTokenValiditySecs);
		
		return signedJWT;		
		
	}

	
	/**
	 * Valida e autentica il refresh token
	 * @param refreshToken
	 * @return
	 */
	public HrvAuthentication refreshAuth(String refreshToken){
		try{
			Jwt refreshJwt=this.jwtDecoder.decode(refreshToken);
			return this.convert(refreshJwt);
		}
		catch(Exception e){
			logger.error("refreshAuth: "+e.getMessage(),e);
			throw new InsufficientAuthenticationException("Invalid refresh token", e);
		}
	}
	
	
	@Override
	public OAuth2TokenValidatorResult validate(Jwt jwt) {
		
		String logPrefix="validate:";
		
		Instant expiresAt = jwt.getExpiresAt();
		Instant now=new Date().toInstant();
		long diff = ChronoUnit.SECONDS.between(now, expiresAt);
		logger.trace(logPrefix+"jwt.subject="+jwt.getSubject()+";jwt.issuer="+jwt.getIssuer()+";expiresAt="+expiresAt+";between="+diff);
		
		URL issuer = jwt.getIssuer();
		String jwtIssuerUri=null;
		if (issuer!=null) {
			jwtIssuerUri=issuer.toString();
		}

		List<OAuth2Error> el=new ArrayList<>();		
		if (!issuerUri.equals(jwtIssuerUri)) {
			OAuth2Error e=new OAuth2Error("invalid issuer:"+jwtIssuerUri);
			el.add(e);			
		}
		
		if (diff<-maxClockSkewSecs) {
			OAuth2Error e=new OAuth2Error("token expired");
			el.add(e);
		}
		
		return OAuth2TokenValidatorResult.failure(el);
	}
	

	@Override
	public HrvAuthentication convert(Jwt jwt) {
		
		String logPrefix="convert:";
		final String subject = jwt.getSubject();
		final String clType=jwt.getClaimAsString("client_type");
		final boolean refresh=jwt.getClaimAsBoolean("refresh");

		logger.trace(logPrefix+" jwt.subject="+subject+", jwt.clientType="+clType+", jwt.refresh="+refresh);
		
		User user=null;
		
		boolean isUserActive=false;
		user=loadUser(subject);
		if (user!=null) {		
			String status=user.getStatus();
			isUserActive=true;			
		}
		
		if (!isUserActive) {
			throw new AccessDeniedException("User not active:"+subject);
			
		}
		
		
		final List<SimpleGrantedAuthority> gaList=new ArrayList<SimpleGrantedAuthority>();
		if(refresh){
			final SimpleGrantedAuthority gaa=new SimpleGrantedAuthority("ROLE_REFRESH"); 
			gaList.add(gaa);
		}
		else{
			if (User.CLIENT_TYPE_APP.equals(clType)) {
				final SimpleGrantedAuthority gaa=new SimpleGrantedAuthority("ROLE_APP"); 
				gaList.add(gaa);
				if(!"prod".equals(profile)){
					// Consente all'app di inserire dei trades per testing
					final SimpleGrantedAuthority ta=new SimpleGrantedAuthority("ROLE_TWS"); 
					gaList.add(ta);
				}
			}
			else if (User.CLIENT_TYPE_TWS.equals(clType)) {
				final SimpleGrantedAuthority gaa=new SimpleGrantedAuthority("ROLE_TWS"); 
				gaList.add(gaa);
			}
			else{
				logger.warn(logPrefix+" Unexpected clientType="+clType);
				final SimpleGrantedAuthority ga=new SimpleGrantedAuthority("ROLE_CLIENT"); 
				gaList.add(ga);		
			}
		}
		//logger.debug(logPrefix+", profile="+profile+", gaList=", gaList);
		return new HrvAuthentication(jwt, gaList, user);
	
	}

	/**
	 * User login
	 * @param userId
	 * @param password
	 * @return
	 */
	@Transactional
	public User auth(String userId, String password) { 
		
		String logPrefix="auth:";
		logger.info(logPrefix+"userId="+userId);
		User user=null;

		try {
			
			user=loadUser(userId);
	
			boolean invalidCredentials=user==null;
			
			if (user!=null) {
				
				if( !passwordEncoder.matches(password, user.getPassword())) {
					logger.error(logPrefix+"invalid password. user="+userId);
					invalidCredentials=true;
				}
								
				String status=user.getStatus();					
				if ("BLOCKED".equals(status)) {
					logger.error(logPrefix+"status not active. userId="+userId+";status="+status);
					throw new LockedException("user LOCKED");
					//throw new DisabledException("user not enabled");
				}
				
				
				/*int loginErrs=Utils.int2Zero(client.getLoginErrs());
				if (invalidCredentials) {
					client.setLoginErrs(++loginErrs);
					if (loginErrs>maxAuthErrors) {
						logger.error(logPrefix+"client blocked.clientId="+clientId+";loginErrs="+loginErrs);
						client.setBlockDate(now);
						user.setStatus("BLOCKED");
						throw new LockedException("client LOCKED");
					}				
				}
				else {
					client.setLoginErrs(0);
				}*/
			
			}

			if (invalidCredentials) {
				throw new BadCredentialsException("Invalid Credentials");
			}			
					
	    }
		finally {		
			if (user!=null) {
				userRepo.update(user);
			}
		}

		return user;

	}
	
	@Transactional
	public User authTwsClient(String userId, String password) {
		User user = this.auth(userId, password);
		if(user.getTwsClientActivationDate()==null){
			logger.info("authTwsClient: tws client activation. userId="+userId);
			user.setTwsClientActivationDate(new Date());
			userRepo.update(user);
		}
		return user;
	}	
	
	@Transactional(readOnly=true)
	public User loadUser(String userId)  {
		
		String logPrefix="loadUser:";
		logger.trace(logPrefix+"userId="+userId);
		
		User user;
		try {
			user = userRepo.findByPk(userId);
			return user;
		} 
		catch(EmptyResultDataAccessException e) {
			logger.warn(logPrefix+"not found: userId="+userId);
			return null;		
	    }		
			
	}


}
