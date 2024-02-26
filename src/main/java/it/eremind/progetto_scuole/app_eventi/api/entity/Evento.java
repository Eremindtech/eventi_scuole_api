package it.eremind.progetto_scuole.app_eventi.api.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter @Setter
public class Evento {
	
	@Id
	Long id;
    LocalDateTime dataIns;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="creatore")
	User creatore;

    String nome;
    String descr;
    LocalDateTime dataEv;
    BigDecimal spesa;
	LocalDateTime dataPagamento;

	@OneToMany(fetch = FetchType.LAZY, mappedBy="user")
    List<User> partecipantiList;

}
