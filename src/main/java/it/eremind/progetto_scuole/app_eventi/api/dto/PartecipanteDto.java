package it.eremind.progetto_scuole.app_eventi.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.validation.constraints.NotEmpty;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data @NoArgsConstructor
public class PartecipanteDto {
	
    @NotEmpty
	String username;
	String nome;
	String cognome;

    Long idPartecipante;
    BigDecimal spesa; 
    LocalDateTime dataPagamento;

}
