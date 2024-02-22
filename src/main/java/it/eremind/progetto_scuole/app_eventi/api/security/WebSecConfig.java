package it.eremind.progetto_scuole.app_eventi.api.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

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

import it.orobicalab.hrv.dto.ApiError;
import it.orobicalab.hrv.web.config.ConfigProps;
import it.orobicalab.hrv.web.util.Utils;


@Configuration
@EnableWebSecurity
public class WebSecConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private ObjectMapper objMapper;

	@Value("${springdoc.api-docs.path:/api-docs}")
	public static String API_DOCS_PATH="/api-docs";

	public static final String REG_PATH="/registration";
	public static final String AUTH_PATH="/auth";
	public static final String APP_PATH="/hrv-app";
	public static final String TWS_PATH="/tws-client";


	// use nimbus tool to create keyset (json-web-key-generator.jar)
	//private static final String JWK_SET_FILENAME="hrv-full-jwk-set.json";	
	
	private static final Logger logger = LoggerFactory.getLogger(WebSecConfig.class);


	
	private RSAKey rsaJWK;		
	//private JWKSet jwkSet;
	
	


	

	
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
	
	

	
	public WebSecConfig(ConfigProps configProps) {
		logger.debug("<init>: configProps="+configProps);

		String cfgDir=Utils.trim2Empty(configProps.getCfgDir());
		File fCfgDir=new File(cfgDir);
		if (!fCfgDir.isDirectory())
			throw new IllegalArgumentException("cfgDir not a Directory:"+cfgDir);
		
		String jwkSetFilename=Utils.trim2Empty(configProps.getJwkSetFilename());

		String jwkKeyID=Utils.trim2Empty(configProps.getJwkKeyID());
		File kpf=new File(fCfgDir, jwkSetFilename);
		FileInputStream fis=null;
		
		JWKSet jwkSet=null;
		
		
		logger.debug("loading jwkSetFile:"+kpf);
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

		//logger.debug("<init>:jwkSet="+jwkSet);
		
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
	
	
	
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		JwtDecoder jwtDecoder=jwtDecoder();
		
				
		http
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
        .csrf().disable()
		.authorizeRequests(authorizeRequests -> authorizeRequests				
				.mvcMatchers(AUTH_PATH+"/**", REG_PATH+"/**", API_DOCS_PATH+"/**").permitAll()
				.mvcMatchers(APP_PATH+"/**").hasAuthority("ROLE_APP")
				.mvcMatchers(TWS_PATH+"/**").hasAuthority("ROLE_TWS")
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

	
	/*
	//https://github.com/spring-projects/spring-security/issues/8092
	@Bean
	public JwtDecoder jwtDecoder(final OAuth2ResourceServerProperties properties, final ResourceLoader resourceLoader) throws Exception {
		// this workaround is needed because spring boot doesn't support loading jwks from classpath/file (only http/https)
		// using standard 'spring.security.oauth2.resourceserver.jwt.jwk-set-uri' configuration property
		// relevant issue: https://github.com/spring-projects/spring-security/issues/8092
		final String issuerUri = properties.getJwt().getIssuerUri();
		final String jwkSetUri = properties.getJwt().getJwkSetUri();
		final JWSAlgorithm jwsAlgorithm = JWSAlgorithm.parse(properties.getJwt().getJwsAlgorithm());

		final InputStream inputStream = resourceLoader.getResource(jwkSetUri).getInputStream();
		final JWKSet jwkSet = JWKSet.load(inputStream);
		final JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(jwkSet);
		final ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
		final JWSKeySelector<SecurityContext> jwsKeySelector = new JWSVerificationKeySelector<>(jwsAlgorithm, jwkSource);
		jwtProcessor.setJWSKeySelector(jwsKeySelector);

		// Spring Security validates the claim set independent from Nimbus
		// copied from 'org.springframework.security.oauth2.jwt.NimbusJwtDecoder.JwkSetUriJwtDecoderBuilder.processor'
		jwtProcessor.setJWTClaimsSetVerifier((claims, context) -> {
		});

		final OAuth2TokenValidator<Jwt> defaultValidator = JwtValidators.createDefaultWithIssuer(issuerUri);
		final OAuth2TokenValidator<Jwt> clientIdValidator = new JwtClaimValidator<String>(CLIENT_ID_CLAIM, value -> !value.trim().isEmpty());
		final OAuth2TokenValidator<Jwt> combinedValidator = new DelegatingOAuth2TokenValidator<>(defaultValidator, clientIdValidator);

		final NimbusJwtDecoder nimbusJwtDecoder = new NimbusJwtDecoder(jwtProcessor);
		nimbusJwtDecoder.setJwtValidator(combinedValidator);

		return nimbusJwtDecoder;
	}
	*/
	
	

}
