package it.eremind.progetto_scuole.app_eventi.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.eremind.progetto_scuole.app_eventi.api.entity.Partecipante;


@Repository 
public interface PartecipantiRepository extends JpaRepository<Partecipante, Long> { 

	
}
	

