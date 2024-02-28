package it.eremind.progetto_scuole.app_eventi.api.config;


import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.TimeZone;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.util.UrlPathHelper;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;
import com.zaxxer.hikari.HikariDataSource;

import it.eremind.progetto_scuole.app_eventi.api.util.Utils;
import lombok.Getter;


@Order(0)
@Configuration
@Getter
public class AppConfig {

	
	private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);

	@Value("${springdoc.api-docs.path:/api-docs}")
	public String apiDocsPath;

	public static final String AUTH_PATH="/auth";
	public static final String APP_PATH="/eventi";

	public final static String DATE_TIME_SWAGGER="2022-03-15 12:30:22";
	public static final String DATE_TIME_FORMAT="yyyy-MM-dd HH:mm:ss";


	@Value("${cfgDir:cfg}")
	private String cfgDir;
	private String jwkSetFilename="hrv-full-jwk-set.json";
	private String jwkKeyID="hrv-1";



	@PostConstruct
	public void pc(){
		System.out.println("AppConfig-postContruct: cfgDir="+cfgDir);
		logger.info("pc: cfgDir="+cfgDir+", AUTH_PATH="+AUTH_PATH+", APP_PATH="+APP_PATH+", apiDocsPath="+apiDocsPath
			+", jwkSetFilename="+jwkSetFilename);
	}

	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
	
	@Primary
	@Bean
	public ObjectMapper objectMapper(){
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		// For java time dates serialization
		JavaTimeModule jtModule=new JavaTimeModule();
		jtModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ISO_LOCAL_DATE));
		jtModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ISO_LOCAL_DATE));
		jtModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
		jtModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
		mapper.registerModule(jtModule);
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	    return mapper;
	}
	
	
	@Bean
	public UrlPathHelper urlPathHelper(){
		return new UrlPathHelper();
	}
	
	@Bean
	public AntPathMatcher antPathMatcher(){
		return new AntPathMatcher();
	}
 
	@Bean
	public TimeBasedGenerator uuidGenerator() {
		return Generators.timeBasedGenerator();
	}	



	@Autowired
	private DataSource dataSource;
	
	@PreDestroy
	public void shutdown()  {
		
		logger.debug("Datasource shutdown:...");
		try { 
			HikariDataSource ds=(HikariDataSource) dataSource;
			ds.close(); 
		} catch (Exception e) {logger.error("hds.close():"+e.getMessage());}
		
		
		final ClassLoader cl = Thread.currentThread().getContextClassLoader();
		logger.debug("Jdbc drivers deregistration...:ctxCL="+cl.hashCode());
		final Enumeration<Driver> drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements()) {
			final Driver driver = drivers.nextElement();
			final ClassLoader dcl=driver.getClass().getClassLoader();
			logger.debug("Jdbc deregistration:found driver="+driver+";drvCL="+dcl.hashCode());
			if (dcl == cl) {
				try {
					logger.debug("Jdbc deregisterDriver:"+driver);
					DriverManager.deregisterDriver(driver);
					if (driver instanceof com.mysql.cj.jdbc.Driver) {
						logger.debug("Jdbc mysql checkedShutdown...");
						AbandonedConnectionCleanupThread.checkedShutdown();
					}
				} catch (SQLException e) {
					logger.error("Jdbc deregisterDriver error:"+driver+":"+e.getMessage(), e);
				}
			}
			else {
				logger.debug("Jdbc deregistration:ignoring driver="+driver+";not loaded by ctxCL"); 
			}
		}				
		
	} 
	
}
