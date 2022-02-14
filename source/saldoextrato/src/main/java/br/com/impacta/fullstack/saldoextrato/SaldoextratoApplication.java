package br.com.impacta.fullstack.saldoextrato;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
@EnableAutoConfiguration
@EnableDiscoveryClient
public class SaldoextratoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SaldoextratoApplication.class, args);
	}

}
