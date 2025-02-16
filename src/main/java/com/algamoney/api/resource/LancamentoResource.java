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
import com.algamoney.api.model.Lancamento;
import com.algamoney.api.repository.LancamentoRepository;
import com.algamoney.api.repository.filter.LancamentoFilter;
import com.algamoney.api.repository.projection.ResumoLancamento;
import com.algamoney.api.service.LancamentoService;
import com.algamoney.api.service.exception.PessoaInexistenteOuInativaException;

@RestController
@RequestMapping( "/lancamentos" )
public class LancamentoResource {

	@Autowired
	private LancamentoRepository lancamentoRepository;

	@Autowired
	private LancamentoService lancamentoService;

	@Autowired
	private ApplicationEventPublisher publisher;

	@GetMapping
	@PreAuthorize( "hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and #oauth2.hasScope('read')" )
	public Page<Lancamento> pesquisar( LancamentoFilter lancamentoFilter, Pageable pageable ) {

		return lancamentoRepository.filtrar( lancamentoFilter, pageable );
	}

	@GetMapping( params = "resumo" )
	@PreAuthorize( "hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and #oauth2.hasScope('read')" )
	public Page<ResumoLancamento> resumir( LancamentoFilter lancamentoFilter, Pageable pageable ) {

		return lancamentoRepository.resumir( lancamentoFilter, pageable );
	}

	@PostMapping
	@PreAuthorize( "hasAuthority('ROLE_CADASTRAR_LANCAMENTO') and #oauth2.hasScope('write')" )
	public ResponseEntity<Lancamento> criar( @Valid @RequestBody Lancamento lancamento, HttpServletResponse response ) throws PessoaInexistenteOuInativaException {

		Lancamento lancamentoSalva = lancamentoService.salvar( lancamento );
		publisher.publishEvent( new RecursoCriadoEvent( this, response, lancamentoSalva.getId() ) );

		return ResponseEntity.status( HttpStatus.CREATED ).body( lancamentoSalva );
	}

	@GetMapping( "/{id}" )
	@PreAuthorize( "hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and #oauth2.hasScope('read')" )
	public ResponseEntity<Lancamento> buscarPorId( @PathVariable Long id ) {

		Optional<Lancamento> lancamentoObtido = lancamentoRepository.findById( id );

		return !lancamentoObtido.isEmpty() ? ResponseEntity.ok( lancamentoObtido.get() ) : ResponseEntity.notFound().build();
	}

	@DeleteMapping( "/{id}" )
	@ResponseStatus( HttpStatus.NO_CONTENT )
	@PreAuthorize( "hasAuthority('ROLE_REMOVER_LANCAMENTO') and #oauth2.hasScope('write')" )
	public void remover( @PathVariable Long id ) {

		lancamentoRepository.deleteById( id );
	}

	@PutMapping( "/{id}" )
	@PreAuthorize( "hasAuthority('ROLE_CADASTRAR_LANCAMENTO') and #oauth2.hasScope('write')" )
	public ResponseEntity<Lancamento> atualizar( @PathVariable Long id, @Valid @RequestBody Lancamento lancamento ) throws PessoaInexistenteOuInativaException {

		try {
			Lancamento lancamentoSalvo = lancamentoService.atualizar( id, lancamento );
			return ResponseEntity.ok( lancamentoSalvo );
		} catch ( IllegalArgumentException e ) {
			return ResponseEntity.notFound().build();
		}
	}

}
