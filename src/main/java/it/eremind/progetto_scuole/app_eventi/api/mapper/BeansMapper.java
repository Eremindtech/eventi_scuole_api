package it.eremind.progetto_scuole.app_eventi.api.mapper;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import it.eremind.progetto_scuole.app_eventi.api.dto.EventoDto;
import it.eremind.progetto_scuole.app_eventi.api.dto.UserDto;
import it.eremind.progetto_scuole.app_eventi.api.entity.Evento;
import it.eremind.progetto_scuole.app_eventi.api.entity.User;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BeansMapper {

	
	public static List<EventoDto> toDto(List<Evento> dbL) {
		List<EventoDto> dtoL=new ArrayList<>();
		dbL.forEach(db->{
			EventoDto dto=new EventoDto();
			BeanUtils.copyProperties(db, dto);
			dto.setCreatore(toDto(db.getCreatore()));
			dto.setPartecipantiList(toDtoUserL(db.getPartecipantiList()));
			log.debug("toEventoDto: id="+dto.getId()+", nome="+dto.getNome()+", spesa="+dto.getSpesa()
				+", dataPagamento="+dto.getDataPagamento()+", partecipanti.size="+dto.getPartecipantiList().size());
			dtoL.add(dto);
		});
		return dtoL;
	}


	private static List<UserDto> toDtoUserL(List<User> dbL) {
		List<UserDto> dtoL=new ArrayList<>();
		dbL.forEach(db->{
			dtoL.add(toDto(db));
		});
		return dtoL;
	}


	private static UserDto toDto(User db) {
		UserDto dto=new UserDto();
		BeanUtils.copyProperties(db, dto);
		return dto;
	}
	


}
