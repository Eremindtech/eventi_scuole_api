package it.eremind.progetto_scuole.app_eventi.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import it.eremind.progetto_scuole.app_eventi.api.config.AppConfig;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor
public class EventoDto {

    Long id;
    String nome;
    String descr;
    @Schema(type="string", example = AppConfig.DATE_TIME_SWAGGER)
    LocalDateTime dataEv;
    BigDecimal spesa;
	LocalDateTime dataPagamento;
    UserDto creatore;
    List<UserDto> partecipantiList;

}
