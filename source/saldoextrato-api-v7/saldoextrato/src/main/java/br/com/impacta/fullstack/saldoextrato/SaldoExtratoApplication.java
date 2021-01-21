package br.com.impacta.fullstack.saldoextrato;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;

@SpringBootApplication
@EnableCircuitBreaker
public class SaldoExtratoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SaldoExtratoApplication.class, args);
	}

}
