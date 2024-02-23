package it.eremind.progetto_scuole.app_eventi.api.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import it.eremind.progetto_scuole.app_eventi.api.entity.User;


@Repository 
public interface UserRepository extends JpaRepository<User, String> { 
	
    @Query("UPDATE User u SET u.ultimoAccesso = ?1, u.tentativiLogin = ?2 WHERE u.username = ?3")
    @Modifying
    void updateLoginSucceeded(LocalDateTime loginDate, int loginFailures, String username);
    
    @Query("UPDATE User u SET u.ultimoAccesso = ?1, u.tentativiLogin = ?2, u.status=?3 WHERE u.username = ?4")
    @Modifying
    void updateLoginFailed(LocalDateTime loginDate, int loginFailures, String status, String username);    
    
	
}
	

