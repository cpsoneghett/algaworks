package com.algamoney.api.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.algamoney.api.model.Usuario;
import com.algamoney.api.repository.UsuarioRepository;

@Service
public class AppUserDetailsService implements UserDetailsService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Override
	public UserDetails loadUserByUsername( String email ) throws UsernameNotFoundException {

		Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail( email );
		Usuario usuario = usuarioOptional.orElseThrow( () -> new UsernameNotFoundException( "Usuario e/ou senha nao encontrados." ) );

		return new UsuarioSistema( usuario, getPermissoes( usuario ) );
	}

	private Collection<? extends GrantedAuthority> getPermissoes( Usuario usuario ) {

		HashSet<SimpleGrantedAuthority> authorities = new HashSet<>();
		usuario.getPermissoes().forEach( p -> authorities.add( new SimpleGrantedAuthority( p.getDescricao().toUpperCase() ) ) );

		return authorities;
	}

}
