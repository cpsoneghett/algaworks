package com.algamoney.api.resource;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.algamoney.api.event.RecursoCriadoEvent;
import com.algamoney.api.model.Categoria;
import com.algamoney.api.repository.CategoriaRepository;

@RestController
@RequestMapping( "/categorias" )
public class CategoriaResource {

	@Autowired
	private CategoriaRepository categoriaRepository;

	@Autowired
	private ApplicationEventPublisher publisher;

	@GetMapping
	@PreAuthorize( "hasAuthority('ROLE_PESQUISAR_CATEGORIA') and #oauth2.hasScope('read')" )
	public List<Categoria> listar() {
		return categoriaRepository.findAll();
	}

	@PostMapping
	@PreAuthorize( "hasAuthority('ROLE_CADASTRAR_CATEGORIA') and #oauth2.hasScope('write')" )
	public ResponseEntity<Categoria> criar( @Valid @RequestBody Categoria categoria, HttpServletResponse response ) {
		Categoria categoriaSalva = categoriaRepository.save( categoria );

		publisher.publishEvent( new RecursoCriadoEvent( this, response, categoriaSalva.getId() ) );

		return ResponseEntity.status( HttpStatus.CREATED ).body( categoriaSalva );
	}

	@GetMapping( "/{id}" )
	@PreAuthorize( "hasAuthority('ROLE_PESQUISAR_CATEGORIA') and #oauth2.hasScope('read')" )
	public ResponseEntity<Categoria> buscarPorId( @PathVariable Long id ) {

		Optional<Categoria> categoriaObtida = categoriaRepository.findById( id );

		return !categoriaObtida.isEmpty() ? ResponseEntity.ok( categoriaObtida.get() ) : ResponseEntity.notFound().build();
	}
}
