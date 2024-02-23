package it.eremind.progetto_scuole.app_eventi.api.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyType;
import com.nimbusds.jose.jwk.RSAKey;

import it.eremind.progetto_scuole.app_eventi.api.config.AppConfig;
import it.eremind.progetto_scuole.app_eventi.api.dto.ApiError;
import it.eremind.progetto_scuole.app_eventi.api.util.Utils;



@Configuration
@EnableWebSecurity
public class WebSecConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private ObjectMapper objMapper;

	@Autowired AppConfig appConfig;

	private static final Logger logger = LoggerFactory.getLogger(WebSecConfig.class);

	private RSAKey rsaJWK;		

	

	@PostConstruct
	public void pc() {
		
		String cfgDir=Utils.trim2Empty(appConfig.getCfgDir());
		File fCfgDir=new File(cfgDir);
		if (!fCfgDir.isDirectory())
			throw new IllegalArgumentException("cfgDir not a Directory:"+cfgDir);
		
		String jwkSetFilename=Utils.trim2Empty(appConfig.getJwkSetFilename());
		String jwkKeyID=Utils.trim2Empty(appConfig.getJwkKeyID());
		File kpf=new File(fCfgDir, jwkSetFilename);
		FileInputStream fis=null;
		
		JWKSet jwkSet=null;
		
		
		logger.debug("pc: loading jwkSetFile:"+kpf);
		try {
			fis=new FileInputStream(kpf);
			jwkSet=JWKSet.load(fis);
		}
		catch (Exception e) {
			logger.error("Cannot load JWKSet:"+e.getMessage(), e);
			System.out.println("Cannot load JWKSet:"+e.getMessage());
			throw new IllegalArgumentException("WebSecConfig:cannot load JWKSet:"+e.getMessage());
		}
		finally {
			if (fis!=null) try {fis.close();} catch (Exception ex) {}
		}					
		
		List<JWK> jwkLst = jwkSet.getKeys();
		
		
		if (jwkLst!=null) {
			for (JWK jwk: jwkLst) {
				
				//logger.debug("<init>:jwk:keyID=="+jwk.getKeyID()+";private="+jwk.isPrivate()+";algo="+jwk.getAlgorithm());
				if (jwk.isPrivate() && KeyType.RSA.equals(jwk.getKeyType()) && (jwkKeyID.length()==0 || jwk.getKeyID().equals(jwkKeyID))) {
					rsaJWK=jwk.toRSAKey();
					break;
				}
				
			}
		}
		
		if (rsaJWK==null) {
			throw new IllegalArgumentException("WebSecConfig:no JWK RSA private key in keySet"+(jwkKeyID.length()==0 ? "" : " with keyID="+jwkKeyID));
		}
		
	}		
	
	
		
	@Bean
	public JwtDecoder jwtDecoder() throws JOSEException {
		logger.debug("jwtDecoder() ...");
		NimbusJwtDecoder jwtDecoder=NimbusJwtDecoder.withPublicKey(rsaJWK.toRSAPublicKey()).build();
		SecMgr secMgr=secMgr();
		secMgr.setJwtDecoder(jwtDecoder);
		jwtDecoder.setJwtValidator(secMgr);
		return jwtDecoder;
	}	
	
	@Bean
	public SecMgr secMgr() {
		logger.debug("secMgr() ...");
		return new SecMgr(rsaJWK);
	}
	
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		JwtDecoder jwtDecoder=jwtDecoder();
		
				
		http
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
        .csrf().disable()
		.authorizeRequests(authorizeRequests -> authorizeRequests				
				.mvcMatchers(AppConfig.AUTH_PATH+"/**", appConfig.getApiDocsPath()+"/**").permitAll()
				.mvcMatchers(AppConfig.APP_PATH+"/**").hasAuthority("ROLE_APP")
				.anyRequest().authenticated()
				)
		.oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer
				.jwt(jwt ->	jwt
						.decoder(jwtDecoder)
						.jwtAuthenticationConverter(secMgr()))
				.authenticationEntryPoint(authFailureHandler())
				.accessDeniedHandler(accessDeniedHandler())
				
		);
		
	}

	@Bean
	public AuthFailureHandler authFailureHandler(){
		return new AuthFailureHandler();
	}
	
	@Bean
	public ADHandler accessDeniedHandler(){
		return new ADHandler();
	}
	
	public class AuthFailureHandler implements AuthenticationEntryPoint {

		@Override
		public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e)
				throws IOException, ServletException {

			logger.debug("AuthFailureHandler - commence: "+e.getClass()+", " +e.getMessage());
			httpServletResponse.setContentType("application/json");
			httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			ApiError apiError=new ApiError(e.getMessage());
			if(e instanceof BadCredentialsException){
				apiError.setMessageKey(ApiError.INVALID_CREDENTIALS);
			}
			else if(e instanceof LockedException){
				apiError.setMessageKey(ApiError.USER_LOCKED);
			}
			else{
				apiError.setMessageKey(ApiError.AUTH_ERROR);
			}
					
			httpServletResponse.getOutputStream().println(objMapper.writeValueAsString(apiError));

		}
	}

	
	public class ADHandler implements AccessDeniedHandler {

		@Override
		public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
			String userId=request.getUserPrincipal()==null?null:request.getUserPrincipal().getName();
			logger.error("AccessDeniedHandler - handle: userId="+userId+", "+e.getClass()+", "+e.getMessage());
		    response.setContentType("application/json");
		    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			ApiError apiError=new ApiError(ApiError.ACCESS_DENIED, e.getMessage());
			response.getOutputStream().println(objMapper.writeValueAsString(apiError));		
			
		}
		
	}



}
