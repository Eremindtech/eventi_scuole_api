package it.eremind.progetto_scuole.app_eventi.api.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Corrisponde alla login user
 * @author Eri
 */
@Data @NoArgsConstructor
public class AuthReq {
	
	@NotBlank
	private String username;
	@NotBlank
	private String password;
	
}
