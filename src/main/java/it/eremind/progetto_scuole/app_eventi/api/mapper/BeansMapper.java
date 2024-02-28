package it.eremind.progetto_scuole.app_eventi.api.mapper;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import it.eremind.progetto_scuole.app_eventi.api.dto.*;
import it.eremind.progetto_scuole.app_eventi.api.entity.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BeansMapper {

	
	public static List<EventoDto> toDto(List<Evento> dbL) {
		List<EventoDto> dtoL=new ArrayList<>();
		dbL.forEach(db->{
			EventoDto dto=new EventoDto();
			BeanUtils.copyProperties(db, dto);
			dto.setCreatore(toDto(db.getCreatore()));
			dto.setPartecipantiList(toDtoPartecipanteL(db.getPartecipantiList()));
			log.debug("toEventoDto: id="+dto.getId()+", nome="+dto.getNome()+", spesa="+dto.getSpesa()
				+", partecipanti.size="+dto.getPartecipantiList().size());
			dtoL.add(dto);
		});
		return dtoL;
	}


	public static List<UserDto> toDtoUserL(List<User> dbL) {
		List<UserDto> dtoL=new ArrayList<>();
		dbL.forEach(db->{
			dtoL.add(toDto(db));
		});
		return dtoL;
	}

	public static UserDto toDto(User db) {
		UserDto dto=new UserDto();
		BeanUtils.copyProperties(db, dto);
		return dto;
	}


	public static List<PartecipanteDto> toDtoPartecipanteL(List<Partecipante> dbL) {
		List<PartecipanteDto> dtoL=new ArrayList<>();
		dbL.forEach(db->{
			PartecipanteDto dto=new PartecipanteDto();
			User user=db.getUser();
			dto.setUsername(user.getUsername());
			dto.setNome(user.getNome());
			dto.setCognome(user.getCognome());
			dto.setDataPagamento(db.getDataPagamento());
			dto.setSpesa(db.getSpesa());
			dto.setIdPartecipante(db.getId());
			dtoL.add(dto);
			Evento ev=db.getEvento();
			log.debug("toDtoPartecipanteL: idEv="+ev.getId()+", nomeEv="+ev.getNome()+", idPart="+dto.getIdPartecipante()
				+", username="+dto.getUsername()+", spesa="+dto.getSpesa()+", dataPagamento="+dto.getDataPagamento());
		});
		return dtoL;
	}


}
