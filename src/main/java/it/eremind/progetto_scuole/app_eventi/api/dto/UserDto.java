package it.eremind.progetto_scuole.app_eventi.api.dto;

import javax.validation.constraints.NotEmpty;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data @NoArgsConstructor
public class UserDto {
	
	@NotEmpty
	String username;
	String nome;
	String cognome;


}
