package it.eremind.progetto_scuole.app_eventi.api.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class ResourcesService {

	public static final int NUM_HR_ZONES = 4;

	private Logger logger=LoggerFactory.getLogger(this.getClass().getName());
	
	@Value()
	private String jwkSetFilename="hrv-full-jwk-set.json";
	private String jwkKeyID="hrv-1";

}
