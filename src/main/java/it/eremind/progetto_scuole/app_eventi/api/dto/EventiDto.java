package it.eremind.progetto_scuole.app_eventi.api.dto;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor
public class EventiDto {

    private List<EventoDto> eventiList;

}
