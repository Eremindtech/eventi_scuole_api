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

import com.fasterxml.uuid.impl.TimeBasedGenerator;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTClaimsSet.Builder;

import it.eremind.progetto_scuole.app_eventi.api.entity.User;
import it.eremind.progetto_scuole.app_eventi.api.repository.UserRepository;

import com.nimbusds.jwt.SignedJWT;



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
	private UserRepository userRepo;	

	@Autowired
	private PasswordEncoder passwordEncoder;			
	@Autowired
	private TimeBasedGenerator uuidGenerator;		
		
	private JwtDecoder jwtDecoder;
	
	
	public SecMgr(RSAKey rsaJWK) {	
		this.rsaJWK=rsaJWK;
		this.algo=(JWSAlgorithm) rsaJWK.getAlgorithm();
		if (this.algo==null) {
			this.algo=JWSAlgorithm.RS256;
		}
	}
	
	
	@PostConstruct
	void init() {
		logger.info("init: tokenValiditySecs:"+tokenValiditySecs+", refreshTokenValiditySecs="+this.refreshTokenValiditySecs);
	}
	
	
	public void setJwtDecoder(JwtDecoder jwtDecoder){
		this.jwtDecoder=jwtDecoder;
	}
	
	
	
	public SignedJWT generateClientToken(User user) throws JOSEException {
		
		String logPrefix="generateClientToken: ";
		
		String userId=user.getUsername();
		logger.debug(logPrefix+"username="+userId);
		
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
        .claim("refresh", "false");

		
		SignedJWT signedJWT = new SignedJWT(
				new JWSHeader.Builder(algo).keyID(rsaJWK.getKeyID()).build(), 
				claimsBuilder.build()
				);
		signedJWT.sign(signer);
		
		logger.debug(logPrefix+"userId="+userId+", issueTime="+now+", expirationTime="+expDate+", l="+l+", tokenValiditySecs="+this.tokenValiditySecs);

		return signedJWT;		
		
	}
	
	
	public SignedJWT generateRefreshToken(User user) throws JOSEException {
		
		String logPrefix="generateRefreshToken: ";
		String userId=user.getUsername();
		logger.debug(logPrefix+"username="+userId);
		
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
	public ErmAuthentication refreshAuth(String refreshToken){
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
	public ErmAuthentication convert(Jwt jwt) {
		
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
			final SimpleGrantedAuthority gaa=new SimpleGrantedAuthority("ROLE_APP"); 
			gaList.add(gaa);
		}
		return new ErmAuthentication(jwt, gaList, user);
	
	}

	/**
	 * User login
	 * @param username
	 * @param password
	 * @return
	 */
	@Transactional
	public User auth(String username, String password) { 
		
		String logPrefix="auth:";
		logger.info(logPrefix+"username="+username);
		User user=null;

		try {
			
			user=loadUser(username);
	
			boolean invalidCredentials=user==null;
			
			if (user!=null) {
				
				if( !passwordEncoder.matches(password, user.getPassword())) {
					logger.error(logPrefix+"invalid password. username="+username);
					invalidCredentials=true;
				}
								
				String status=user.getStatus();					
				if ("BLOCKED".equals(status)) {
					logger.error(logPrefix+"status not active. username="+username+";status="+status);
					throw new LockedException("user LOCKED");
				}
			
			}

			if (invalidCredentials) {
				throw new BadCredentialsException("Invalid Credentials");
			}			
					
	    }
		finally {		
			if (user!=null) {
				userRepo.save(user);
			}
		}

		return user;

	}
	
	
	
	@Transactional(readOnly=true)
	public User loadUser(String username)  {
		
		String logPrefix="loadUser:";
		logger.trace(logPrefix+"username="+username);
		
		return userRepo.findById(username).orElse(null);	
			
	}


}
