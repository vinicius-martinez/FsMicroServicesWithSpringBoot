package br.com.impacta.fullstack.saldoextrato;

import brave.Span;
import brave.Tracer;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
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

    //@Value("${CREDITO_API_URL}")
    //private String CREDITO_API_URL;

    //@Value("${DEBITO_API_URL}")
    //private String DEBITO_API_URL;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private Tracer tracer;

    public SaldoExtrato get(){
        Span newSpan = tracer.nextSpan().name("saldoextrato").start();
        RestTemplate restTemplate = new RestTemplate();
        //Get Credito
        ServiceInstance serviceInstance = discoveryClient.getInstances("credito-v2").get(0);
        String creditoUrl = "http://" + serviceInstance.getHost() + ":" + serviceInstance.getPort() + "/api/v1/credito";
        ResponseEntity<Credito[]> creditoResponse = restTemplate.getForEntity(creditoUrl, Credito[].class);
        System.out.println("CREDITO_API_URL: " + creditoUrl);
        List<Credito> creditoList = Arrays.asList(creditoResponse.getBody());
        System.out.println("Creditos: " + creditoList);
        SaldoExtrato saldoExtrato = new SaldoExtrato();
        saldoExtrato.setCreditoList(creditoList);
        //Get Debito
        serviceInstance = discoveryClient.getInstances("debito-v2").get(0);
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
        newSpan.finish();
        return saldoExtrato;
    }

    @HystrixCommand(fallbackMethod = "fallback")
    public SaldoExtrato getBff(){
        RestTemplate restTemplate = new RestTemplate();
        //Get Credito
        ServiceInstance serviceInstance = discoveryClient.getInstances("credito-v2").get(0);
        String creditoUrl = "http://" + serviceInstance.getHost() + ":" + serviceInstance.getPort() + "/api/v1/credito";
        ResponseEntity<Credito[]> creditoResponse = restTemplate.getForEntity(creditoUrl, Credito[].class);
        System.out.println("CREDITO_API_URL: " + creditoUrl);
        List<Credito> creditoList = Arrays.asList(creditoResponse.getBody());
        System.out.println("Creditos: " + creditoList);
        //Get Debito
        serviceInstance = discoveryClient.getInstances("debito-v2").get(0);
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

    public SaldoExtrato fallback(){
        SaldoExtrato saldoExtratoFallBack = new SaldoExtrato();
        saldoExtratoFallBack.setSaldo(new BigDecimal(0));
        return saldoExtratoFallBack;
    }

}
