package it.eremind.progetto_scuole.app_eventi.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.validation.constraints.NotEmpty;

import io.swagger.v3.oas.annotations.media.Schema;
import it.eremind.progetto_scuole.app_eventi.api.config.AppConfig;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data @NoArgsConstructor
public class PartecipanteDto {
	
    @NotEmpty
	String username;
	String nome;
	String cognome;

    Long idPartecipante;
    @Schema(example = AppConfig.CURRENCY_SWAGGER)
    BigDecimal spesa; 
    @Schema(type="string", example = AppConfig.DATE_TIME_SWAGGER)
    LocalDateTime dataPagamento;

}
