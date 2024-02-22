package it.eremind.progetto_scuole.app_eventi.api.config;


import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.TimeZone;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;
import com.zaxxer.hikari.HikariDataSource;


@Order(0)
@Configuration
public class AppConfig {

	
	private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);

	private HikariDataSource hds;	
	
	public final static String DATE_TIME_SWAGGER="2022-03-15 12:30:22";
	public static final String DATE_TIME_FORMAT="yyyy-MM-dd HH:mm:ss";

	
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
	
	@Primary
	@Bean
	public ObjectMapper objectMapper(){

		ObjectMapper mapper = new ObjectMapper();
	    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	    mapper.enable(SerializationFeature.INDENT_OUTPUT);
	    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	    //StdDateFormat is ISO8601 since jackson 2.9
	    mapper.setDateFormat(new SimpleDateFormat(DATE_TIME_FORMAT));
	    mapper.setTimeZone(TimeZone.getDefault());
	    //mapper.setDateFormat(new SimpleDateFormat(StdDateFormat.DATE_FORMAT_STR_ISO8601));
	    //mapper.findAndRegisterModules();
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
