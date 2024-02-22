package it.eremind.progetto_scuole.app_eventi.api.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import it.eremind.progetto_scuole.app_eventi.api.entity.User;




@SuppressWarnings("serial")
public class ErmAuthentication extends JwtAuthenticationToken {
	
	private User user;
	
	public ErmAuthentication(Jwt jwt, Collection<? extends GrantedAuthority> authorities, User user) {
		super(jwt, authorities, user.getIdUser());
		this.user=user;
	}
	
	public User getUser() {
		return user;
	}
	
	
}
