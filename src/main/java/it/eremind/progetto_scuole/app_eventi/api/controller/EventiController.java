package it.eremind.progetto_scuole.app_eventi.api.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.eremind.progetto_scuole.app_eventi.api.service.EventiService;

import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@RestController
@RequestMapping(WebSecConfig.APP_PATH)
public class EventiController {
	

	protected Logger logger= LoggerFactory.getLogger(this.getClass().getName());	
	
	@Autowired
	private EventiService service;
	
	
	@GetMapping(value="/hr-devices/list", produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<HrMonitorDevicesDto> getHrMonitorDevices(HrvAuthentication auth){
		User au=auth.getUser();
		logger.debug("getHrMonitorDevices: idUser="+au.getIdUser());
		HrMonitorDevicesDto rsp=this.hrvService.getHrMonitorDevices(au);
		return ResponseEntity.ok(rsp);		
	}
	
	@PostMapping(value="/hr-devices/insert", produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<HrMonitorDeviceDto> insertNewHrMonitorDevice(HrvAuthentication auth, @Valid @RequestBody HrMonitorDeviceDto monitor){
		logger.debug("insertNewHrMonitorDevice: idUser="+monitor.getIdUser());
		this.checkUserGrants(auth, monitor);
		monitor=this.hrvService.insertNewHrMonitorDevice(monitor);
		return ResponseEntity.ok(monitor);		
	}
	
	@PostMapping(value="/hr-devices/delete", produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<HrMonitorDeviceDto> deleteHrMonitorDevice(HrvAuthentication auth, @Valid @RequestBody HrMonitorDeviceOperationDto req){
		logger.debug("deleteHrMonitorDevice: id="+req.getId());
		HrMonitorDevice monitor=this.hrvService.getHrMonitorDevice(req.getId());
		this.checkUserGrants(auth, monitor);
		this.hrvService.deleteHrMonitorDevice(monitor);
		return ResponseEntity.ok().build();		
	}
	

	
	/**
	 * Dati dello user autenticato
	 * @param auth
	 * @return
	 */
	@GetMapping(value="/user-data", produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<UserDto> getUserData(HrvAuthentication auth){
		User au=auth.getUser();
		logger.debug("getUserData: idUser="+au.getIdUser());
		UserDto user=this.hrvService.getUserData(au);
		return ResponseEntity.ok(user);		
	}
	
	@PostMapping(value="/user-data", produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<Void> updateUserData(HrvAuthentication auth, @Valid @RequestBody UserDto user){
		logger.debug("updateUserData: idUser="+user.getIdUser());
		this.checkUserGrants(auth, user);
		this.hrvService.updateUserData(user);
		return ResponseEntity.ok().build();		
	}
	

	


    
}
