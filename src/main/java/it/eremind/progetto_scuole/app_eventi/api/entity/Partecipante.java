package it.eremind.progetto_scuole.app_eventi.api.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Partecipante {

    @Id
    Long id;

    @ManyToOne
    @JoinColumn
    User user;
    
    @ManyToOne
    @JoinColumn
    Evento evento;
    
    BigDecimal spesa; 
    @Column(columnDefinition = "DATETIME")
    LocalDateTime dataPagamento;

}
