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

import org.springframework.util.StringUtils;

import com.algamoney.api.model.Lancamento;
import com.algamoney.api.model.Lancamento_;
import com.algamoney.api.repository.filter.LancamentoFilter;

public class LancamentoRepositoryImpl implements LancamentoRepositoryQuery {

	@PersistenceContext
	private EntityManager em;

	@Override
	public List<Lancamento> filtrar( LancamentoFilter lancamentoFilter ) {

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Lancamento> criteria = builder.createQuery( Lancamento.class );
		Root<Lancamento> root = criteria.from( Lancamento.class );

		// Criar as restrições
		Predicate[] predicates = criaRestricoes( lancamentoFilter, builder, root );
		criteria.where( predicates );

		TypedQuery<Lancamento> query = em.createQuery( criteria );

		return query.getResultList();
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

}
