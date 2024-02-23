package it.eremind.progetto_scuole.app_eventi.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import it.eremind.progetto_scuole.app_eventi.api.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;




@Service
@Slf4j
public class EventiService {
	
	@Autowired
	private UserRepository uRepo;



	@Transactional(readOnly=true)
	public void getEventi() {
		log.debug("getEventi");
	}





	
}
