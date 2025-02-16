package com.algamoney.api.resource;

import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.algamoney.api.event.RecursoCriadoEvent;
import com.algamoney.api.model.Pessoa;
import com.algamoney.api.repository.PessoaRepository;
import com.algamoney.api.repository.filter.PessoaFilter;
import com.algamoney.api.service.PessoaService;

@RestController
@RequestMapping( "/pessoas" )
public class PessoaResource {

	@Autowired
	private PessoaRepository pessoaRepository;

	@Autowired
	private ApplicationEventPublisher publisher;

	@Autowired
	private PessoaService pessoaService;

	@GetMapping
	@PreAuthorize( "hasAuthority('ROLE_PESQUISAR_PESSOA') and #oauth2.hasScope('read')" )
	public Page<Pessoa> listar( PessoaFilter pessoaFilter, Pageable pageable ) {

		return pessoaRepository.filtrar( pessoaFilter, pageable );
	}

	@PostMapping
	@PreAuthorize( "hasAuthority('ROLE_CADASTRAR_PESSOA') and #oauth2.hasScope('read')" )
	public ResponseEntity<Pessoa> criar( @Valid @RequestBody Pessoa pessoa, HttpServletResponse response ) {

		Pessoa pessoaSalva = pessoaRepository.save( pessoa );
		publisher.publishEvent( new RecursoCriadoEvent( this, response, pessoaSalva.getId() ) );

		return ResponseEntity.status( HttpStatus.CREATED ).body( pessoaSalva );
	}

	@GetMapping( "/{id}" )
	@PreAuthorize( "hasAuthority('ROLE_PESQUISAR_PESSOA') and #oauth2.hasScope('read')" )
	public ResponseEntity<Pessoa> buscarPorId( @PathVariable Long id ) {

		Optional<Pessoa> pessoaObtida = pessoaRepository.findById( id );

		return !pessoaObtida.isEmpty() ? ResponseEntity.ok( pessoaObtida.get() ) : ResponseEntity.notFound().build();
	}

	@DeleteMapping( "/{id}" )
	@ResponseStatus( HttpStatus.NO_CONTENT )
	@PreAuthorize( "hasAuthority('ROLE_REMOVER_PESSOA') and #oauth2.hasScope('write')" )
	public void remover( @PathVariable Long id ) {

		pessoaRepository.deleteById( id );
	}

	@PutMapping( "/{id}" )
	@PreAuthorize( "hasAuthority('ROLE_CADASTRAR_PESSOA') and #oauth2.hasScope('write')" )
	public ResponseEntity<Pessoa> atualiza( @PathVariable Long id, @Valid @RequestBody Pessoa pessoa ) {

		Pessoa pessoaSalva = pessoaService.atualizarPessoa( id, pessoa );

		return ResponseEntity.ok( pessoaSalva );
	}

	@PutMapping( "/{id}/ativo" )
	@ResponseStatus( HttpStatus.NO_CONTENT )
	@PreAuthorize( "hasAuthority('ROLE_CADASTRAR_PESSOA') and #oauth2.hasScope('write')" )
	public void atualizarPropriedadeAtivo( @PathVariable Long id, @RequestBody Boolean ativo ) {

		pessoaService.atualizarPropriedadeAtivo( id, ativo );
	}
}
