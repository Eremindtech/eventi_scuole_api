package it.eremind.progetto_scuole.app_eventi.api.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
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


    public EventoDto insertEvento(EventoDto ev) {
		Evento evDb = new Evento();
		BeanUtils.copyProperties(ev, evDb);
		User creatore=this.uRepo.findById(ev.getCreatore().getUsername())
			.orElseThrow(() -> new IllegalArgumentException("insertEvento: user creatore not found, username=" + ev.getCreatore().getUsername()));
		evDb.setCreatore(creatore);
		List<Partecipante> partDbL=new ArrayList<>();
		int npart=ev.getPartecipantiList().size();
		BigDecimal spc=BigDecimal.valueOf(ev.getSpesa().doubleValue()/npart);
		ev.getPartecipantiList().forEach(part->{
			User user=this.uRepo.findById(part.getUsername())
				.orElseThrow(() -> new IllegalArgumentException("insertEvento: user partecipante not found, username=" + ev.getCreatore().getUsername()));
			Partecipante partDb=new Partecipante();
			partDb.setUser(user);
			partDb.setSpesa(spc);
			partDbL.add(partDb);
		});
		evDb.setPartecipantiList(partDbL);
		evDb.setDataIns(LocalDateTime.now());
		evDb=this.evRepo.save(evDb);
		
		ev.setId(evDb.getId());
		log.debug("insertEvento: id="+ev.getId()+", creatore="+creatore.getUsername()+", spesa="+ev.getSpesa()
			+", numPartecipanti="+npart+", spesaProCapite="+spc);

		return ev;
	}





	
}
