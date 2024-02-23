package it.eremind.progetto_scuole.app_eventi.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;


@SpringBootApplication
public class EventiApiApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(EventiApiApplication.class);
    }	
    
	public static void main(String[] args) {
		SpringApplication.run(EventiApiApplication.class, args);
	}
	


}
