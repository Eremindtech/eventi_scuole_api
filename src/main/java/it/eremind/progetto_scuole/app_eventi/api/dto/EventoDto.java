package it.eremind.progetto_scuole.app_eventi.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import it.eremind.progetto_scuole.app_eventi.api.config.AppConfig;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor
public class EventoDto {

    Long id;
    @NotEmpty
    String nome;
    String descr;
    @Schema(type="string", example = AppConfig.DATE_TIME_SWAGGER) @NotEmpty
    LocalDateTime dataEv;
    @NotEmpty
    BigDecimal spesa;
    @NotNull
    UserDto creatore;
    @NotEmpty
    List<PartecipanteDto> partecipantiList;

}
