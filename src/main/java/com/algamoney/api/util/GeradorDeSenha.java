package com.algamoney.api.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GeradorDeSenha {

	public static void main( String[] args ) {

		String senhaTeste = "mobile";

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		System.out.println( encoder.encode( senhaTeste ) );
	}
}
