package it.eremind.progetto_scuole.app_eventi.api.entity;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter @Setter @NoArgsConstructor
public class User {
	
	@Id
	private String username;
	private String password;
	private String email;
	private String nome;
	private String cognome;
	private String status;
	@Column(length=3)
	private Integer tentativiLogin;
	private LocalDateTime ultimoAccesso;
	private LocalDateTime cambioPwd;
	private String profilo;

}
