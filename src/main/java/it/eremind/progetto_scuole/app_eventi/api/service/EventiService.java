package it.eremind.progetto_scuole.app_eventi.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.eremind.progetto_scuole.app_eventi.api.dto.*;
import it.eremind.progetto_scuole.app_eventi.api.entity.*;
import it.eremind.progetto_scuole.app_eventi.api.mapper.BeansMapper;
import it.eremind.progetto_scuole.app_eventi.api.repository.*;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class EventiService {
	
	@Autowired
	private UserRepository uRepo;
	@Autowired EventiRepository evRepo;


	@Transactional(readOnly=true)
    public EventiDto findEventi() {
		EventiDto eventi=new EventiDto();
		List<Evento> dbL=this.evRepo.findAll();
		List<EventoDto> evL = BeansMapper.toDto(dbL);
		eventi.setEventiList(evL);
		log.debug("findEventi: eventiList.size="+evL.size());
		return eventi;
    }





	
}
