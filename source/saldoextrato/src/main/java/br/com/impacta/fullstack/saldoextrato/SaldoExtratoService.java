package br.com.impacta.fullstack.saldoextrato;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
public class SaldoExtratoService {

    @Autowired
    private DiscoveryClient discoveryClient;

    public SaldoExtrato get(){
        RestTemplate restTemplate = new RestTemplate();
        //Get Credito
        ServiceInstance serviceInstance = discoveryClient.getInstances("credito").get(0);
        String creditoUrl = "http://" + serviceInstance.getHost() + ":" + serviceInstance.getPort() + "/api/v1/credito";
        ResponseEntity<Credito[]> creditoResponse = restTemplate.getForEntity(creditoUrl, Credito[].class);
        System.out.println("CREDITO_API_URL: " + creditoUrl);
        List<Credito> creditoList = Arrays.asList(creditoResponse.getBody());
        System.out.println("Creditos: " + creditoList);
        SaldoExtrato saldoExtrato = new SaldoExtrato();
        saldoExtrato.setCreditoList(creditoList);
        //Get Debito
        serviceInstance = discoveryClient.getInstances("debito").get(0);
        String debitoUrl = "http://" + serviceInstance.getHost() + ":" + serviceInstance.getPort() + "/api/v1/debito";
        ResponseEntity<Debito[]> debitoResponse = restTemplate.getForEntity(debitoUrl, Debito[].class);
        System.out.println("DEBITO_API_URL: " + debitoUrl);
        List<Debito> debitoList = Arrays.asList(debitoResponse.getBody());
        System.out.println("Debitos: " + debitoList);
        saldoExtrato.setDebitoList(debitoList);
        //Calcular saldo
        BigDecimal saldo = calculateSaldo(creditoList, debitoList);
        saldoExtrato.setSaldo(saldo);
        System.out.println("saldo: " + saldo);
        return saldoExtrato;
    }

    public SaldoExtrato getBff(){
        RestTemplate restTemplate = new RestTemplate();
        //Get Credito
        ServiceInstance serviceInstance = discoveryClient.getInstances("credito").get(0);
        String creditoUrl = "http://" + serviceInstance.getHost() + ":" + serviceInstance.getPort() + "/api/v1/credito";
        ResponseEntity<Credito[]> creditoResponse = restTemplate.getForEntity(creditoUrl, Credito[].class);
        System.out.println("CREDITO_API_URL: " + creditoUrl);
        List<Credito> creditoList = Arrays.asList(creditoResponse.getBody());
        System.out.println("Creditos: " + creditoList);
        //Get Debito
        serviceInstance = discoveryClient.getInstances("debito").get(0);
        String debitoUrl = "http://" + serviceInstance.getHost() + ":" + serviceInstance.getPort() + "/api/v1/debito";
        ResponseEntity<Debito[]> debitoResponse = restTemplate.getForEntity(debitoUrl, Debito[].class);
        System.out.println("DEBITO_API_URL: " + debitoUrl);
        List<Debito> debitoList = Arrays.asList(debitoResponse.getBody());
        System.out.println("Debitos: " + debitoList);
        //Calcular saldo
        SaldoExtrato saldoExtrato = new SaldoExtrato();
        BigDecimal saldo = calculateSaldo(creditoList, debitoList);
        saldoExtrato.setSaldo(saldo);
        System.out.println("saldo: " + saldo);
        return saldoExtrato;
    }

    private BigDecimal calculateSaldo(List<Credito> creditoList, List<Debito> debitoList) {
        BigDecimal creditoSum = creditoList.stream().map(Credito::getCredito).reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.println("creditoSum: " + creditoSum);
        BigDecimal debitoSum = debitoList.stream().map(Debito::getDebito).reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.println("debitoSum: " + debitoSum);
        BigDecimal saldo = creditoSum.add(debitoSum);
        System.out.println("saldo: " + saldo);
        return saldo;
    }

}