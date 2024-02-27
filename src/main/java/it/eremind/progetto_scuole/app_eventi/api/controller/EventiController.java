package it.eremind.progetto_scuole.app_eventi.api.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.eremind.progetto_scuole.app_eventi.api.config.AppConfig;
import it.eremind.progetto_scuole.app_eventi.api.dto.*;
import it.eremind.progetto_scuole.app_eventi.api.service.EventiService;
import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;



@RestController
@RequestMapping(AppConfig.APP_PATH)
@Slf4j
public class EventiController {
	

	@Autowired EventiService service;


	@GetMapping(produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<EventiDto> getListaEventi(){
		log.debug("getListaEventi");
		EventiDto rsp=this.service.findEventi();
		return ResponseEntity.ok(rsp);		
	}

	@PostMapping(value="/nuovo", produces={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<EventoDto> creaEvento(@Valid @RequestBody EventoDto ev){
		log.debug("creaEvento");
		ev=this.service.insertEvento(ev);
		return ResponseEntity.ok(ev);		
	}

    
}
