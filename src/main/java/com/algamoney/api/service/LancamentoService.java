package com.algamoney.api.service;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.algamoney.api.model.Lancamento;
import com.algamoney.api.model.Pessoa;
import com.algamoney.api.repository.LancamentoRepository;
import com.algamoney.api.repository.PessoaRepository;
import com.algamoney.api.service.exception.PessoaInexistenteOuInativaException;

@Service
public class LancamentoService {

	@Autowired
	private PessoaRepository pessoaRepository;

	@Autowired
	private LancamentoRepository lancamentoRepository;

	public Lancamento salvar( @Valid Lancamento lancamento ) throws PessoaInexistenteOuInativaException {

		Optional<Pessoa> pessoa = pessoaRepository.findById( lancamento.getPessoa().getId() );

		if ( pessoa.isEmpty() || pessoa.get().isInativo() ) {
			throw new PessoaInexistenteOuInativaException();
		}

		return lancamentoRepository.save( lancamento );
	}

}
