package com.algamoney.api.service;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.algamoney.api.model.Pessoa;
import com.algamoney.api.repository.PessoaRepository;

@Service
public class PessoaService {

	@Autowired
	private PessoaRepository pessoaRepository;

	public Pessoa atualizarPessoa( Long id, Pessoa pessoa ) {

		Pessoa pessoaSalva = buscarPessoaPeloID( id );
		BeanUtils.copyProperties( pessoa, pessoaSalva, "id" );

		return pessoaRepository.save( pessoaSalva );
	}

	public Pessoa buscarPessoaPeloID( Long id ) {

		Optional<Pessoa> pessoaSalva = pessoaRepository.findById( id );

		if ( pessoaSalva.isEmpty() ) {
			throw new EmptyResultDataAccessException( 1 );
		}

		Pessoa p = pessoaSalva.get();
		return p;
	}

	public void atualizarPropriedadeAtivo( Long id, Boolean ativo ) {

		Pessoa pessoaSalva = buscarPessoaPeloID( id );
		pessoaSalva.setAtivo( ativo );
		pessoaRepository.save( pessoaSalva );
	}

}
