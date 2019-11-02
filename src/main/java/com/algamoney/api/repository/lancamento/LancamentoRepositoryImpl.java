package com.algamoney.api.repository.lancamento;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import com.algamoney.api.model.Categoria;
import com.algamoney.api.model.Categoria_;
import com.algamoney.api.model.Lancamento;
import com.algamoney.api.model.Lancamento_;
import com.algamoney.api.model.Pessoa_;
import com.algamoney.api.repository.filter.LancamentoFilter;
import com.algamoney.api.repository.projection.ResumoLancamento;

public class LancamentoRepositoryImpl implements LancamentoRepositoryQuery {

	@PersistenceContext
	private EntityManager em;

	@Override
	public Page<Lancamento> filtrar( LancamentoFilter lancamentoFilter, Pageable pageable ) {

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Lancamento> criteria = builder.createQuery( Lancamento.class );
		Root<Lancamento> root = criteria.from( Lancamento.class );

		// Criar as restrições
		Predicate[] predicates = criaRestricoes( lancamentoFilter, builder, root );
		criteria.where( predicates );

		TypedQuery<Lancamento> query = em.createQuery( criteria );
		adicionarRestricoesDePaginacao( query, pageable );

		return new PageImpl( query.getResultList(), pageable, total( lancamentoFilter ) );
	}

	@Override
	public Page<ResumoLancamento> resumir( LancamentoFilter lancamentoFilter, Pageable pageable ) {

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<ResumoLancamento> criteria = builder.createQuery( ResumoLancamento.class );
		Root<Lancamento> root = criteria.from( Lancamento.class );

		criteria.select( builder.construct( ResumoLancamento.class, 
			root.get( Lancamento_.ID ), root.get( Lancamento_.DESCRICAO ),
			root.get( Lancamento_.DATA_VENCIMENTO ), root.get( Lancamento_.DATA_PAGAMENTO ), 
			root.get( Lancamento_.VALOR ), root.get( Lancamento_.TIPO ),
			root.get( Lancamento_.CATEGORIA ).get( Categoria_.NOME ), 
			root.get( Lancamento_.PESSOA ).get( Pessoa_.NOME ) ) );

		Predicate[] predicates = criaRestricoes( lancamentoFilter, builder, root );
		criteria.where( predicates );

		TypedQuery<ResumoLancamento> query = em.createQuery( criteria );
		adicionarRestricoesDePaginacao( query, pageable );

		return new PageImpl( query.getResultList(), pageable, total( lancamentoFilter ) );
	}

	private Predicate[] criaRestricoes( LancamentoFilter lancamentoFilter, CriteriaBuilder builder, Root<Lancamento> root ) {

		List<Predicate> predicates = new ArrayList<>();

		if ( !StringUtils.isEmpty( lancamentoFilter.getDescricao() ) ) {

			predicates.add( builder.like( builder.lower( root.get( Lancamento_.DESCRICAO ) ), "%" + lancamentoFilter.getDescricao() + "%" ) );
		}
		if ( lancamentoFilter.getDataVencimentoDe() != null ) {

			predicates.add( builder.greaterThanOrEqualTo( root.get( Lancamento_.DATA_VENCIMENTO ), lancamentoFilter.getDataVencimentoDe() ) );
		}
		if ( lancamentoFilter.getDataVencimentoAte() != null ) {

			predicates.add( builder.lessThanOrEqualTo( root.get( Lancamento_.DATA_VENCIMENTO ), lancamentoFilter.getDataVencimentoAte() ) );
		}

		return predicates.toArray( new Predicate[ predicates.size() ] );
	}

	private void adicionarRestricoesDePaginacao( TypedQuery<?> query, Pageable pageable ) {

		int paginaAtual = pageable.getPageNumber();
		int totalRegistrosPorPagina = pageable.getPageSize();
		int primeiroRegistroDaPagina = paginaAtual * totalRegistrosPorPagina;

		query.setFirstResult( primeiroRegistroDaPagina );
		query.setMaxResults( totalRegistrosPorPagina );
	}

	private Long total( LancamentoFilter lancamentoFilter ) {

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = builder.createQuery( Long.class );
		Root<Lancamento> root = criteria.from( Lancamento.class );

		Predicate[] predicates = criaRestricoes( lancamentoFilter, builder, root );
		criteria.where( predicates );

		criteria.select( builder.count( root ) );

		return em.createQuery( criteria ).getSingleResult();
	}

}
