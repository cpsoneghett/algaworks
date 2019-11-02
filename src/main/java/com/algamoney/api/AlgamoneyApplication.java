package com.algamoney.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.algamoney.api.config.property.AlgamoneyApiProperty;

@SpringBootApplication
@EnableConfigurationProperties( AlgamoneyApiProperty.class )
public class AlgamoneyApplication {

	public static void main( String[] args ) {
		SpringApplication.run( AlgamoneyApplication.class, args );
	}

}
