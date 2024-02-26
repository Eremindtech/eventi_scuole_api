package it.eremind.progetto_scuole.app_eventi.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.eremind.progetto_scuole.app_eventi.api.entity.Evento;


@Repository 
public interface EventiRepository extends JpaRepository<Evento, Long> { 

	
}
	

