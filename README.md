# Microservices com Spring Boot

Neste repositório estarão disponíveis nosso *Workshop* de implementação de **Microservices** com [Spring Boot](https://spring.io/projects/spring-boot)

## Pré Requisitos

- [JDK/Open JDK 17 (no mínimo)](https://openjdk.java.net/install/)
- [Apache Maven 3.6.x](https://maven.apache.org/download.cgi)
- [IntelliJ Community](https://www.jetbrains.com/idea/download/#section=mac)
- [Docker Desktop Win/Mac 3.x](https://www.docker.com/products/docker-desktop)
- [jq](https://stedolan.github.io/jq/)

## Workshop

0. [Criação Credito API](#workshop-criacao-credito-api)
1. [Criação Debito API](#workshop-criacao-debito-api)
2. [Criação SaldoExtrato API](#workshop-criacao-saldoextrato-api)
3. [Criação SaldoExtrato BFF API](#workshop-criacao-saldoextrato-bff-api)
4. [Criação Config Server](#workshop-criacao-config-server)
5. [Criação Service Discovery Server](#workshop-service-discovery-server)
6. [Habilitar Circuit Breaker](#workshop-circuit-breaker)
7. [Habilitar Tracing](#workshop-tracing)
8. [Habilitar Monitoramento](#workshop-monitoramento)

## Implementação

### 0 - Criação Credito API <a name="workshop-criacao-credito-api">

* Acesse o [Spring Boot Initializer](https://start.spring.io/) e gere um projeto com as seguintes informações:

  ```
  Project: Maven
  Language: Java
  Spring Boot: 3.0.0
  Project Metadata
    Group: br.com.impacta.fullstack
    artifact: credito
    name: credito
    Package Name: br.com.impacta.fullstack.credito
    Packaging: jar
    Java: 17
  Dependencies:
    Spring Web
    Spring Boot Actuator
  ```

* Importe o projeto no *IntelliJ* e verifique se o mesmo está sendo executado com sucesso através do comando *mvn spring-boot:run*:

  ```
    .   ____          _            __ _ _
  /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
  ( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
  \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
  =========|_|==============|___/=/_/_/_/
  :: Spring Boot ::       (v3.0.0-SNAPSHOT)

  2022-02-13 16:11:47.280  INFO 15574 --- [           main] b.c.i.f.credito.CreditoApplication       : Starting CreditoApplication using Java 17.0.1 on marcfleury with PID 15574 (/Users/vinny/Desktop/FullStack/FsMicroServicesWithSpringBoot/source/credito/target/classes started by vinny in /Users/vinny/Desktop/FullStack/FsMicroServicesWithSpringBoot/source/credito)
  2022-02-13 16:11:47.282  INFO 15574 --- [           main] b.c.i.f.credito.CreditoApplication       : No active profile set, falling back to default profiles: default
  2022-02-13 16:11:48.079  INFO 15574 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port(s): 8081 (http)
  2022-02-13 16:11:48.087  INFO 15574 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
  2022-02-13 16:11:48.087  INFO 15574 --- [           main] org.apache.catalina.core.StandardEngine  : Starting Servlet engine: [Apache Tomcat/10.0.16]
  2022-02-13 16:11:48.149  INFO 15574 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
  2022-02-13 16:11:48.150  INFO 15574 --- [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 828 ms
  2022-02-13 16:11:48.574  INFO 15574 --- [           main] o.s.b.a.e.web.EndpointLinksResolver      : Exposing 13 endpoint(s) beneath base path '/actuator'
  2022-02-13 16:11:48.608  INFO 15574 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8081 (http) with context path ''
  2022-02-13 16:11:48.617  INFO 15574 --- [           main] b.c.i.f.credito.CreditoApplication       : Started CreditoApplication in 1.631 seconds (JVM running for 1.907)
  2022-02-13 16:11:56.681  INFO 15574 --- [nio-8081-exec-1] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring DispatcherServlet 'dispatcherServlet'
  2022-02-13 16:11:56.681  INFO 15574 --- [nio-8081-exec-1] o.s.web.servlet.DispatcherServlet        : Initializing Servlet 'dispatcherServlet'
  2022-02-13 16:11:56.682  INFO 15574 --- [nio-8081-exec-1] o.s.web.servlet.DispatcherServlet        : Completed initialization in 1 ms
  ```
    * o *output* deve variar variar ligeiramente do apresentando anteriormente observando as características do seu ambiente

* Criar classe **br.com.impacta.fullstack.credito.Credito**

  ```
  package br.com.impacta.fullstack.credito;

  import java.io.Serializable;
  import java.math.BigDecimal;

  public class Credito implements Serializable {

      private BigDecimal credito;

      public Credito() {
          super();
      }

      public Credito(BigDecimal credito){
          this.credito = credito;
      }

      public BigDecimal getCredito() {
          return credito;
      }

      public void setCredito(BigDecimal credito) {
          this.credito = credito;
      }

  }
  ```

* Criar classe **br.com.impacta.fullstack.credito.CreditoService**

  ```
  package br.com.impacta.fullstack.credito;

  import org.springframework.stereotype.Component;

  import java.math.BigDecimal;
  import java.math.BigInteger;
  import java.util.ArrayList;
  import java.util.List;
  import java.util.Random;

  @Component
  public class CreditoService {

      private static final BigDecimal minValue = new BigDecimal(BigInteger.ONE);
      private static final BigDecimal maxValue = new BigDecimal(BigInteger.TEN);

      public List<Credito> list(){
          int numberOfCredit = new Random().nextInt(10) + 1;
          List<Credito> creditoList = new ArrayList<Credito>(10);
          for (int i = 0; i < numberOfCredit; i++) {
              BigDecimal randomValue = minValue.add(new BigDecimal(Math.random()).multiply(maxValue.subtract(minValue))).setScale(1, BigDecimal.ROUND_HALF_UP);
              Credito credito = new Credito(randomValue);
              creditoList.add(credito);
          }
          System.out.println("creditoList: " + creditoList);
          return creditoList;
      }
  }
  ```

* Criar classe **br.com.impacta.fullstack.credito.CreditoController**

  ```
  package br.com.impacta.fullstack.credito;

  import org.springframework.web.bind.annotation.GetMapping;
  import org.springframework.web.bind.annotation.RequestMapping;
  import org.springframework.web.bind.annotation.RestController;

  import java.net.InetAddress;
  import java.net.UnknownHostException;
  import java.util.List;

  @RestController
  @RequestMapping("/api/v1/credito")
  public class CreditoController {

      private final CreditoService creditoService;

      public CreditoController(CreditoService creditoService) {
          this.creditoService = creditoService;
      }

      @GetMapping
      public List<Credito> list() throws UnknownHostException {
          System.out.println("Hostname: " + InetAddress.getLocalHost().getHostName());
          List<Credito> creditoList = creditoService.list();
          return creditoList;
      }

  }
  ```

* Ajustes na configuração de porta para evitar conflitos no arquivo **src/main/resources/application.properties**:

  ```
  server.port = 8081
  management.endpoints.web.exposure.include=*
  management.endpoints.jmx.exposure.include=*
  ```

* Para testar basta inicializar o serviço e invocar a *API* através do *endpoint /api/v1/credito*:

  ```
  mvn spring-boot:run

  http :8081/api/v1/credito
  HTTP/1.1 200
  Connection: keep-alive
  Content-Type: application/json
  Date: Sun, 13 Feb 2022 19:13:41 GMT
  Keep-Alive: timeout=60
  Transfer-Encoding: chunked

  [
      {
          "credito": 3.8
      },
      {
          "credito": 6.5
      },
      {
          "credito": 1.4
      },
      {
          "credito": 4.5
      },
      {
          "credito": 10.0
      },
      {
          "credito": 1.9
      },
      {
          "credito": 1.3
      }
  ]
  ```

* Finalmente para testar o *Actuator* invoque o *endpoint /actuator :*

  ```
  http :8081/actuator | jq
  {
    "_links": {
      "self": {
        "href": "http://localhost:8081/actuator",
        "templated": false
      },
      "beans": {
        "href": "http://localhost:8081/actuator/beans",
        "templated": false
      },
      "caches-cache": {
        "href": "http://localhost:8081/actuator/caches/{cache}",
        "templated": true
      },
      "caches": {
        "href": "http://localhost:8081/actuator/caches",
        "templated": false
      },
      "health": {
        "href": "http://localhost:8081/actuator/health",
        "templated": false
      },
      "health-path": {
        "href": "http://localhost:8081/actuator/health/{*path}",
        "templated": true
      },
      "info": {
        "href": "http://localhost:8081/actuator/info",
        "templated": false
      },
      "conditions": {
        "href": "http://localhost:8081/actuator/conditions",
        "templated": false
      },
      "configprops-prefix": {
        "href": "http://localhost:8081/actuator/configprops/{prefix}",
        "templated": true
      },
      "configprops": {
        "href": "http://localhost:8081/actuator/configprops",
        "templated": false
      },
      "env": {
        "href": "http://localhost:8081/actuator/env",
        "templated": false
      },
      "env-toMatch": {
        "href": "http://localhost:8081/actuator/env/{toMatch}",
        "templated": true
      },
      "loggers": {
        "href": "http://localhost:8081/actuator/loggers",
        "templated": false
      },
      "loggers-name": {
        "href": "http://localhost:8081/actuator/loggers/{name}",
        "templated": true
      },
      "heapdump": {
        "href": "http://localhost:8081/actuator/heapdump",
        "templated": false
      },
      "threaddump": {
        "href": "http://localhost:8081/actuator/threaddump",
        "templated": false
      },
      "metrics-requiredMetricName": {
        "href": "http://localhost:8081/actuator/metrics/{requiredMetricName}",
        "templated": true
      },
      "metrics": {
        "href": "http://localhost:8081/actuator/metrics",
        "templated": false
      },
      "scheduledtasks": {
        "href": "http://localhost:8081/actuator/scheduledtasks",
        "templated": false
      },
      "mappings": {
        "href": "http://localhost:8081/actuator/mappings",
        "templated": false
      }
    }
  }
  ```

### 1 - Criação Débito API <a name="workshop-criacao-debito-api">

  * Acesse o [Spring Boot Initializer](https://start.spring.io/) e gere um projeto com as seguintes informações:

    ```
    Project: Maven
    Language: Java
    Spring Boot: 3.0.0
    Project Metadata
      Group: br.com.impacta.fullstack
      artifact: debito
      name: debito
      Package Name: br.com.impacta.fullstack.debito
      Packaging: jar
      Java: 17
    Dependencies:
      Spring Web
      Spring Boot Actuator
    ```

  * Importe o projeto no *IntelliJ* e verifique se o mesmo está sendo executado com sucesso através do comando *mvn spring-boot:run*:

    ```
        .   ____          _            __ _ _
    /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
    ( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
    \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
    '  |____| .__|_| |_|_| |_\__, | / / / /
    =========|_|==============|___/=/_/_/_/
    :: Spring Boot ::       (v3.0.0-SNAPSHOT)

    2022-02-13 16:24:18.501  INFO 16316 --- [           main] b.c.i.f.debito.DebitoApplication         : Starting DebitoApplication using Java 17.0.1 on marcfleury with PID 16316 (/Users/vinny/Desktop/FullStack/FsMicroServicesWithSpringBoot/source/debito/target/classes started by vinny in /Users/vinny/Desktop/FullStack/FsMicroServicesWithSpringBoot/source/debito)
    2022-02-13 16:24:18.503  INFO 16316 --- [           main] b.c.i.f.debito.DebitoApplication         : No active profile set, falling back to default profiles: default
    2022-02-13 16:24:19.373  INFO 16316 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port(s): 8080 (http)
    2022-02-13 16:24:19.385  INFO 16316 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
    2022-02-13 16:24:19.386  INFO 16316 --- [           main] org.apache.catalina.core.StandardEngine  : Starting Servlet engine: [Apache Tomcat/10.0.16]
    2022-02-13 16:24:19.469  INFO 16316 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
    2022-02-13 16:24:19.472  INFO 16316 --- [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 916 ms
    2022-02-13 16:24:19.893  INFO 16316 --- [           main] o.s.b.a.e.web.EndpointLinksResolver      : Exposing 1 endpoint(s) beneath base path '/actuator'
    2022-02-13 16:24:19.939  INFO 16316 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
    2022-02-13 16:24:19.951  INFO 16316 --- [           main] b.c.i.f.debito.DebitoApplication         : Started DebitoApplication in 1.822 seconds (JVM running for 2.191)
    ```
      * o *output* deve variar variar ligeiramente do apresentando anteriormente observando as características do seu ambiente

* Criar classe **br.com.impacta.fullstack.debito.Debito**

  ```
  package br.com.impacta.fullstack.debito;

  import java.io.Serializable;
  import java.math.BigDecimal;

  public class Debito implements Serializable {

      private BigDecimal debito;

      public Debito() {
          super();
      }

      public Debito(BigDecimal debito) {
          this.debito = debito;
      }

      public BigDecimal getDebito() {
          return debito;
      }

      public void setDebito(BigDecimal debito) {
          this.debito = debito;
      }
  }
  ```

* Criar classe **br.com.impacta.fullstack.debito.DebitoService**

  ```
  package br.com.impacta.fullstack.debito;

  import org.springframework.stereotype.Component;

  import java.math.BigDecimal;
  import java.math.BigInteger;
  import java.util.ArrayList;
  import java.util.List;
  import java.util.Random;

  @Component
  public class DebitoService {

      private static final BigDecimal minValue = new BigDecimal(BigInteger.ONE);
      private static final BigDecimal maxValue = new BigDecimal(BigInteger.TEN);

      public List<Debito> list(){
          int numberOfDebit = new Random().nextInt(10) + 1;
          List<Debito> debitoList = new ArrayList<Debito>(10);
          for (int i = 0; i < numberOfDebit; i++) {
              BigDecimal randomValue = minValue.add(new BigDecimal(Math.random()).multiply(maxValue.subtract(minValue))).setScale(1, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("-1"));
              Debito debito = new Debito(randomValue);
              debitoList.add(debito);
          }
          System.out.println("debitoList: " + debitoList);
          return debitoList;
      }
  }
  ```

* Criar classe **br.com.impacta.fullstack.debito.DebitoController**

  ```
  package br.com.impacta.fullstack.debito;

  import org.springframework.web.bind.annotation.GetMapping;
  import org.springframework.web.bind.annotation.RequestMapping;
  import org.springframework.web.bind.annotation.RestController;

  import java.net.InetAddress;
  import java.net.UnknownHostException;
  import java.util.List;

  @RestController
  @RequestMapping("/api/v1/debito")
  public class DebitoController {

      private final DebitoService debitoService;

      public DebitoController(DebitoService debitoService) {
          this.debitoService = debitoService;
      }

      @GetMapping
      public List<Debito> list() throws UnknownHostException {
          System.out.println("Hostname: " + InetAddress.getLocalHost().getHostName());
          List<Debito> debitoList = debitoService.list();
          return debitoList;
      }
  }
  ```

* Ajustes na configuração de porta para evitar conflitos no arquivo **src/main/resources/application.properties**:

  ```
  server.port = 8082
  management.endpoints.web.exposure.include=*
  management.endpoints.jmx.exposure.include=*
  ```

* Para testar basta inicializar o serviço e invocar a *API* através de seu *endpoint /api/v1/debito*

  ```
  mvn spring:boot run

  -- outra aba do terminal/postman/httpie/curl/etc
  http :8082/api/v1/debito
  HTTP/1.1 200
  Connection: keep-alive
  Content-Type: application/json
  Date: Sun, 13 Feb 2022 19:30:01 GMT
  Keep-Alive: timeout=60
  Transfer-Encoding: chunked

  [
      {
          "debito": -5.4
      },
      {
          "debito": -3.2
      },
      {
          "debito": -5.3
      },
      {
          "debito": -6.6
      },
      {
          "debito": -3.2
      },
      {
          "debito": -8.9
      },
      {
          "debito": -1.3
      },
      {
          "debito": -8.8
      }
  ]
  ```

* Finalmente para testar o *Actuator* invoque o *endpoint /actuator :*

  ```
  http :8082/actuator | jq
  {
   "_links": {
     "self": {
       "href": "http://localhost:8082/actuator",
       "templated": false
     },
     "beans": {
       "href": "http://localhost:8082/actuator/beans",
       "templated": false
     },
     "caches-cache": {
       "href": "http://localhost:8082/actuator/caches/{cache}",
       "templated": true
     },
     "caches": {
       "href": "http://localhost:8082/actuator/caches",
       "templated": false
     },
     "health-path": {
       "href": "http://localhost:8082/actuator/health/{*path}",
       "templated": true
     },
     "health": {
       "href": "http://localhost:8082/actuator/health",
       "templated": false
     },
     "info": {
       "href": "http://localhost:8082/actuator/info",
       "templated": false
     },
     "conditions": {
       "href": "http://localhost:8082/actuator/conditions",
       "templated": false
     },
     "configprops": {
       "href": "http://localhost:8082/actuator/configprops",
       "templated": false
     },
     "configprops-prefix": {
       "href": "http://localhost:8082/actuator/configprops/{prefix}",
       "templated": true
     },
     "env": {
       "href": "http://localhost:8082/actuator/env",
       "templated": false
     },
     "env-toMatch": {
       "href": "http://localhost:8082/actuator/env/{toMatch}",
       "templated": true
     },
     "loggers": {
       "href": "http://localhost:8082/actuator/loggers",
       "templated": false
     },
     "loggers-name": {
       "href": "http://localhost:8082/actuator/loggers/{name}",
       "templated": true
     },
     "heapdump": {
       "href": "http://localhost:8082/actuator/heapdump",
       "templated": false
     },
     "threaddump": {
       "href": "http://localhost:8082/actuator/threaddump",
       "templated": false
     },
     "metrics-requiredMetricName": {
       "href": "http://localhost:8082/actuator/metrics/{requiredMetricName}",
       "templated": true
     },
     "metrics": {
       "href": "http://localhost:8082/actuator/metrics",
       "templated": false
     },
     "scheduledtasks": {
       "href": "http://localhost:8082/actuator/scheduledtasks",
       "templated": false
     },
     "mappings": {
       "href": "http://localhost:8082/actuator/mappings",
       "templated": false
     }
   }
  }
  ```

### 2 - Criação SaldoExtrato API <a name="workshop-criacao-saldoextrato-api">

* Acesse o [Spring Boot Initializer](https://start.spring.io/) e gere um projeto com as seguintes informações:

  ```
  Project: Maven
  Language: Java
  Spring Boot: 3.0.0
  Project Metadata
    Group: br.com.impacta.fullstack
    artifact: saldoextrato
    name: saldoextrato
    Package Name: br.com.impacta.fullstack.saldoextrato
    Packaging: jar
    Java: 17
  Dependencies:
    Spring Web
    Spring Boot Actuator
    Gateway
  ```

* Importe o projeto no *IntelliJ* e verifique se o mesmo está sendo executado com sucesso através do comando *mvn spring-boot:run*:

  ```
    .   ____          _            __ _ _
  /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
  ( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
  \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
  =========|_|==============|___/=/_/_/_/
  :: Spring Boot ::       (v3.0.0-SNAPSHOT)

  2022-02-13 16:45:07.584  INFO 17277 --- [           main] b.c.i.f.s.SaldoextratoApplication        : Starting SaldoextratoApplication using Java 17.0.1 on marcfleury with PID 17277 (/Users/vinny/Desktop/FullStack/FsMicroServicesWithSpringBoot/source/saldoextrato/target/classes started by vinny in /Users/vinny/Desktop/FullStack/FsMicroServicesWithSpringBoot/source/saldoextrato)
  2022-02-13 16:45:07.587  INFO 17277 --- [           main] b.c.i.f.s.SaldoextratoApplication        : No active profile set, falling back to default profiles: default
  2022-02-13 16:45:08.385  INFO 17277 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port(s): 8080 (http)
  2022-02-13 16:45:08.395  INFO 17277 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
  2022-02-13 16:45:08.395  INFO 17277 --- [           main] org.apache.catalina.core.StandardEngine  : Starting Servlet engine: [Apache Tomcat/10.0.16]
  2022-02-13 16:45:08.458  INFO 17277 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
  2022-02-13 16:45:08.460  INFO 17277 --- [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 816 ms
  2022-02-13 16:45:08.891  INFO 17277 --- [           main] o.s.b.a.e.web.EndpointLinksResolver      : Exposing 1 endpoint(s) beneath base path '/actuator'
  2022-02-13 16:45:08.941  INFO 17277 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
  2022-02-13 16:45:08.955  INFO 17277 --- [           main] b.c.i.f.s.SaldoextratoApplication        : Started SaldoextratoApplication in 1.707 seconds (JVM running for 1.98)
  ```
  * o *output* deve variar variar ligeiramente do apresentando anteriormente observando as características do seu ambiente

* Criar classe **br.com.impacta.fullstack.saldoextrato.Credito**

  ```
  package br.com.impacta.fullstack.saldoextrato;

  import java.io.Serializable;
  import java.math.BigDecimal;

  public class Credito implements Serializable {

      private BigDecimal credito;

      public Credito() {}

      public Credito(BigDecimal credito) {
          this.credito = credito;
      }

      public BigDecimal getCredito() {
          return credito;
      }

      public void setCredito(BigDecimal credito) {
          this.credito = credito;
      }

  }
  ```

* Criar classe **br.com.impacta.fullstack.saldoextrato.Debito**

  ```
  package br.com.impacta.fullstack.saldoextrato;

  import java.io.Serializable;
  import java.math.BigDecimal;

  public class Debito implements Serializable {

      private BigDecimal debito;

      public Debito() {}

      public Debito(BigDecimal debito) {
          this.debito = debito;
      }

      public BigDecimal getDebito() {
          return debito;
      }

      public void setDebito(BigDecimal debito) {
          this.debito = debito;
      }

  }
  ```

* Criar classe **br.com.impacta.fullstack.saldoextrato.SaldoExtrato**

  ```
  package br.com.impacta.fullstack.saldoextrato;

  import java.math.BigDecimal;
  import java.util.List;

  public class SaldoExtrato {

      private List<Credito> creditoList;
      private List<Debito> debitoList;
      private BigDecimal saldo;

      public List<Debito> getDebitoList() {
          return debitoList;
      }

      public void setDebitoList(List<Debito> debitoList) {
          this.debitoList = debitoList;
      }

      public List<Credito> getCreditoList() {
          return creditoList;
      }

      public void setCreditoList(List<Credito> creditoList) {
          this.creditoList = creditoList;
      }

      public BigDecimal getSaldo() {
          return saldo;
      }

      public void setSaldo(BigDecimal saldo) {
          this.saldo = saldo;
      }

  }
  ```

* Criar classe **br.com.impacta.fullstack.saldoextrato.SaldoExtratoService**

  ```
  package br.com.impacta.fullstack.saldoextrato;

  import org.springframework.beans.factory.annotation.Value;
  import org.springframework.http.ResponseEntity;
  import org.springframework.stereotype.Component;
  import org.springframework.web.client.RestTemplate;

  import java.math.BigDecimal;
  import java.util.Arrays;
  import java.util.List;

  @Component
  public class SaldoExtratoService {

      @Value(value = "${CREDITO_API_URL}")
      private String CREDITO_API_URL;

      @Value(value = "${DEBITO_API_URL}")
      private String DEBITO_API_URL;

      public SaldoExtrato get(){
          RestTemplate restTemplate = new RestTemplate();
          //Get Credito
          ResponseEntity<Credito[]> creditoResponse = restTemplate.getForEntity(CREDITO_API_URL, Credito[].class);
          System.out.println("CREDITO_API_URL: " + CREDITO_API_URL);
          List<Credito> creditoList = Arrays.asList(creditoResponse.getBody());
          System.out.println("Creditos: " + creditoList);
          SaldoExtrato saldoExtrato = new SaldoExtrato();
          saldoExtrato.setCreditoList(creditoList);
          BigDecimal creditoSum = creditoList.stream().map(Credito::getCredito).reduce(BigDecimal.ZERO, BigDecimal::add);
          //Get Debito
          ResponseEntity<Debito[]> debitoResponse = restTemplate.getForEntity(DEBITO_API_URL, Debito[].class);
          System.out.println("DEBITO_API_URL: " + DEBITO_API_URL);
          List<Debito> debitoList = Arrays.asList(debitoResponse.getBody());
          System.out.println("Debitos: " + debitoList);
          saldoExtrato.setDebitoList(debitoList);
          BigDecimal debitoSum = debitoList.stream().map(Debito::getDebito).reduce(BigDecimal.ZERO, BigDecimal::add);
          System.out.println("debitoSum: " + debitoSum);
          //Calcular saldo
          BigDecimal saldo = creditoSum.add(debitoSum);
          saldoExtrato.setSaldo(saldo);
          return saldoExtrato;
      }
  }
  ```

* Criar classe **br.com.impacta.fullstack.saldoextrato.SaldoExtratoController**

  ```
  package br.com.impacta.fullstack.saldoextrato;

  import org.springframework.web.bind.annotation.GetMapping;
  import org.springframework.web.bind.annotation.RequestMapping;
  import org.springframework.web.bind.annotation.RestController;

  import java.net.InetAddress;
  import java.net.UnknownHostException;

  @RestController
  @RequestMapping(("/api/v1/saldoextrato"))
  public class SaldoExtratoController {

      private final SaldoExtratoService saldoExtratoService;

      public SaldoExtratoController(SaldoExtratoService saldoExtratoService) {
          this.saldoExtratoService = saldoExtratoService;
      }

      @GetMapping
      public SaldoExtrato get() throws UnknownHostException {
          System.out.println("Hostname: " + InetAddress.getLocalHost().getHostName());
          SaldoExtrato saldoExtrato = saldoExtratoService.get();
          return saldoExtrato;
      }

  }
  ```

* Ajustes na configuração de porta no arquivo **src/main/resources/application.properties**:

  ```
  management.endpoints.web.exposure.include=*
  management.endpoints.jmx.exposure.include=*
  CREDITO_API_URL = ${CREDITO_URL:http://localhost:8081/api/v1/credito}
  DEBITO_API_URL = ${DEBITO_URL:http://localhost:8082/api/v1/debito}
  ```

* Para testar basta inicializar o serviço e invocar a *API* através de seu endpoint:

  ```
  mvn spring:boot run

  -- outra aba do terminal/postman/httpie/curl/etc
  http :8080/api/v1/saldoextrato

  HTTP/1.1 200
  Connection: keep-alive
  Content-Type: application/json
  Date: Sun, 13 Feb 2022 19:49:33 GMT
  Keep-Alive: timeout=60
  Transfer-Encoding: chunked

  {
      "creditoList": [
          {
              "credito": 7.2
          },
          {
              "credito": 5.4
          },
          {
              "credito": 8.7
          },
          {
              "credito": 5.2
          },
          {
              "credito": 1.6
          },
          {
              "credito": 3.3
          },
          {
              "credito": 3.3
          }
      ],
      "debitoList": [
          {
              "debito": -8.2
          }
      ],
      "saldo": 26.5
  }
  ```

### 3 - Criação SaldoExtrato BFF API <a name="workshop-criacao-saldoextrato-bff-api">

* Editar a classe **br.com.impacta.fullstack.saldoextrato.SaldoExtratoService** adicionando os métodos **getBff()** e **calculateSaldo**

  ```
  package br.com.impacta.fullstack.saldoextrato;

  import org.springframework.beans.factory.annotation.Value;
  import org.springframework.http.ResponseEntity;
  import org.springframework.stereotype.Component;
  import org.springframework.web.client.RestTemplate;

  import java.math.BigDecimal;
  import java.util.Arrays;
  import java.util.List;

  @Component
  public class SaldoExtratoService {

      @Value(value = "${CREDITO_API_URL}")
      private String CREDITO_API_URL;

      @Value(value = "${DEBITO_API_URL}")
      private String DEBITO_API_URL;

      public SaldoExtrato get(){
          RestTemplate restTemplate = new RestTemplate();
          //Get Credito
          ResponseEntity<Credito[]> creditoResponse = restTemplate.getForEntity(CREDITO_API_URL, Credito[].class);
          System.out.println("CREDITO_API_URL: " + CREDITO_API_URL);
          List<Credito> creditoList = Arrays.asList(creditoResponse.getBody());
          System.out.println("Creditos: " + creditoList);
          SaldoExtrato saldoExtrato = new SaldoExtrato();
          saldoExtrato.setCreditoList(creditoList);
          //Get Debito
          ResponseEntity<Debito[]> debitoResponse = restTemplate.getForEntity(DEBITO_API_URL, Debito[].class);
          System.out.println("DEBITO_API_URL: " + DEBITO_API_URL);
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
          ResponseEntity<Credito[]> creditoResponse = restTemplate.getForEntity(CREDITO_API_URL, Credito[].class);
          System.out.println("CREDITO_API_URL: " + CREDITO_API_URL);
          List<Credito> creditoList = Arrays.asList(creditoResponse.getBody());
          System.out.println("Creditos: " + creditoList);
          //Get Debito
          ResponseEntity<Debito[]> debitoResponse = restTemplate.getForEntity(DEBITO_API_URL, Debito[].class);
          System.out.println("DEBITO_API_URL: " + DEBITO_API_URL);
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
  ```

* Editar a classe **br.com.impacta.fullstack.saldoextrato.SaldoExtratoController** adicionando o método **getBff()**

  ```
  @GetMapping
  @RequestMapping("/mobile")
  public SaldoExtrato getBff() throws UnknownHostException {
      System.out.println("Hostname: " + InetAddress.getLocalHost().getHostName());
      SaldoExtrato saldoExtrato = saldoExtratoService.getBff();
      return saldoExtrato;
  }
  ```

* Adicione a propriedade *spring.jackson.default-property-inclusion* com valor *null* no arquivo **application.properties**

  ```
  spring.jackson.default-property-inclusion = non_null
  ```

* Para testar basta inicializar o serviço e invocar a *API* através de seu endpoint:

  ```
  mvn spring:boot run

  -- outra aba do terminal/postman/httpie/curl/etc
  http :8080/api/v1/saldoextrato/mobile
  HTTP/1.1 200
  Connection: keep-alive
  Content-Type: application/json
  Date: Sun, 13 Feb 2022 20:05:28 GMT
  Keep-Alive: timeout=60
  Transfer-Encoding: chunked

  {
      "saldo": 7.6
  }
  ```

### 4 - Criação Config Server <a name="workshop-criacao-config-server">

* Acesse o [Spring Boot Initializer](https://start.spring.io/) e gere um projeto com as seguintes informações:

  ```
  Project: Maven
  Language: Java
  Spring Boot: 2.4.2
  Project Metadata
    Group: br.com.impacta.fullstack
    artifact: configserver
    name: configserver
    Package Name: br.com.impacta.fullstack.configserver
    Packaging: jar
    Java: 17
  Dependencies:
    Config Server
  ```

* Inclua a *Annotation @EnableConfigServer* na classe **br.com.impacta.fullstack.configserver.ConfigServerApplication**

  ```
  package br.com.impacta.fullstack.configserver;

  import org.springframework.boot.SpringApplication;
  import org.springframework.boot.autoconfigure.SpringBootApplication;
  import org.springframework.cloud.config.server.EnableConfigServer;

  @EnableConfigServer
  @SpringBootApplication
  public class ConfigServerApplication {

  	public static void main(String[] args) {
  		SpringApplication.run(ConfigServerApplication.class, args);
  	}

  }
  ```

* Altere o arquivo **application.properties** adicionando o seguinte conteúdo:

  ```
  server.port = 8888
  spring.application.name=configserver
  spring.cloud.config.server.git.uri=https://github.com/vinicius-martinez/FsConfigServer
  spring.cloud.config.server.git.default-label=master
  ```

* Inicie o servico do *configserver* executando *mvn spring-boot:run*

* Verifique se as *properties* estão sendo retornadas com sucesso:

  ```
  -- outra aba do terminal/postman/httpie/curl/etc
  http :8888/saldoextrato/prod
  HTTP/1.1 200
  Connection: keep-alive
  Content-Type: application/json
  Date: Sun, 13 Feb 2022 20:48:48 GMT
  Keep-Alive: timeout=60
  Transfer-Encoding: chunked

  {
      "label": null,
      "name": "saldoextrato",
      "profiles": [
          "prod"
      ],
      "propertySources": [
          {
              "name": "https://github.com/vinicius-martinez/FsConfigServer/saldoextrato-prod.properties",
              "source": {
                  "CREDITO_API_URL": "http://credito:8081/api/v1/credito",
                  "DEBITO_API_URL": "http://debito:8082/api/v1/debito"
              }
          }
      ],
      "state": null,
      "version": "a7ee9ee2b497fb5a71a74d6a68218f2b8fa1683f"
  }

  -- outra aba do terminal/postman/httpie/curl/etc
  http :8888/saldoextrato/dev
  HTTP/1.1 200
  Connection: keep-alive
  Content-Type: application/json
  Date: Sun, 13 Feb 2022 20:48:56 GMT
  Keep-Alive: timeout=60
  Transfer-Encoding: chunked

  {
      "label": null,
      "name": "saldoextrato",
      "profiles": [
          "dev"
      ],
      "propertySources": [
          {
              "name": "https://github.com/vinicius-martinez/FsConfigServer/saldoextrato-dev.properties",
              "source": {
                  "CREDITO_API_URL": "http://localhost:8081/api/v1/credito",
                  "DEBITO_API_URL": "http://localhost:8082/api/v1/debito"
              }
          }
      ],
      "state": null,
      "version": "a7ee9ee2b497fb5a71a74d6a68218f2b8fa1683f"
  }
  ```

* No projeto **SaldoExtrato** modique o arquivo **pom.xml** adicionando as dependências para utilização do *Config Server*:

  ```
  <?xml version="1.0" encoding="UTF-8"?>
  <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
  	<modelVersion>4.0.0</modelVersion>
  	<parent>
  		<groupId>org.springframework.boot</groupId>
  		<artifactId>spring-boot-starter-parent</artifactId>
  		<version>3.0.0-SNAPSHOT</version>
  		<relativePath/> <!-- lookup parent from repository -->
  	</parent>
  	<groupId>br.com.impacta.fullstack</groupId>
  	<artifactId>saldoextrato</artifactId>
  	<version>0.0.1-SNAPSHOT</version>
  	<name>saldoextrato</name>
  	<description>Demo project for Spring Boot</description>
  	<properties>
  		<java.version>17</java.version>
  		<spring-cloud.version>2022.0.0-M1</spring-cloud.version>
  	</properties>
  	<dependencies>
  		<dependency>
  			<groupId>org.springframework.boot</groupId>
  			<artifactId>spring-boot-starter-actuator</artifactId>
  		</dependency>
  		<dependency>
  			<groupId>org.springframework.boot</groupId>
  			<artifactId>spring-boot-starter-web</artifactId>
  		</dependency>
  		<dependency>
  			<groupId>org.springframework.cloud</groupId>
  			<artifactId>spring-cloud-starter-config</artifactId>
  		</dependency>

  		<dependency>
  			<groupId>org.springframework.boot</groupId>
  			<artifactId>spring-boot-starter-test</artifactId>
  			<scope>test</scope>
  		</dependency>
  	</dependencies>
  	<dependencyManagement>
  		<dependencies>
  			<dependency>
  				<groupId>org.springframework.cloud</groupId>
  				<artifactId>spring-cloud-dependencies</artifactId>
  				<version>${spring-cloud.version}</version>
  				<type>pom</type>
  				<scope>import</scope>
  			</dependency>
  		</dependencies>
  	</dependencyManagement>

  	<build>
  		<plugins>
  			<plugin>
  				<groupId>org.springframework.boot</groupId>
  				<artifactId>spring-boot-maven-plugin</artifactId>
  			</plugin>
  		</plugins>
  	</build>
  	<repositories>
  		<repository>
  			<id>spring-milestones</id>
  			<name>Spring Milestones</name>
  			<url>https://repo.spring.io/milestone</url>
  			<snapshots>
  				<enabled>false</enabled>
  			</snapshots>
  		</repository>
  		<repository>
  			<id>spring-snapshots</id>
  			<name>Spring Snapshots</name>
  			<url>https://repo.spring.io/snapshot</url>
  			<releases>
  				<enabled>false</enabled>
  			</releases>
  		</repository>
  	</repositories>
  	<pluginRepositories>
  		<pluginRepository>
  			<id>spring-milestones</id>
  			<name>Spring Milestones</name>
  			<url>https://repo.spring.io/milestone</url>
  			<snapshots>
  				<enabled>false</enabled>
  			</snapshots>
  		</pluginRepository>
  		<pluginRepository>
  			<id>spring-snapshots</id>
  			<name>Spring Snapshots</name>
  			<url>https://repo.spring.io/snapshot</url>
  			<releases>
  				<enabled>false</enabled>
  			</releases>
  		</pluginRepository>
  	</pluginRepositories>

  </project>
  ```

  * Altere o arquivo **application.properties** da aplicação **SaldoExtrato** adicionando o seguinte conteúdo:

    ```
    spring.application.name=saldoextrato
    management.endpoints.web.exposure.include=*
    management.endpoints.jmx.exposure.include=*
    CREDITO_API_URL = ${CREDITO_URL:http://localhost:8081/api/v1/credito}
    DEBITO_API_URL = ${DEBITO_URL:http://localhost:8082/api/v1/debito}
    spring.jackson.default-property-inclusion = non_null
    spring.config.import=configserver:http://${CONFIG_HOST}:8888
    ```

* Para inicializar a aplicação **SaldoExtrato** corretamente, será necessário incluir duas informações adicionais: *host* do *Config Server* e *profile* da aplicação:

  ```
  export spring_profiles_active=dev;export CONFIG_HOST=localhost
  echo $spring_profiles_active;echo $CONFIG_HOST
  ```

* Verifique se a aplicação está funcionando adequadamente:

  ```
  mvn spring:boot run

  -- outra aba do terminal/postman/httpie/curl/etc
  http :8080/api/v1/saldoextrato/mobile
  HTTP/1.1 200
  Connection: keep-alive
  Content-Type: application/json
  Date: Sun, 13 Feb 2022 21:06:18 GMT
  Keep-Alive: timeout=60
  Transfer-Encoding: chunked

  {
      "saldo": 2.2
  }


  http :8080/api/v1/saldoextrato
  HTTP/1.1 200
  Connection: keep-alive
  Content-Type: application/json
  Date: Sun, 13 Feb 2022 21:06:22 GMT
  Keep-Alive: timeout=60
  Transfer-Encoding: chunked

  {
      "creditoList": [
          {
              "credito": 2.0
          },
          {
              "credito": 3.2
          },
          {
              "credito": 1.4
          },
          {
              "credito": 4.8
          },
          {
              "credito": 2.0
          },
          {
              "credito": 3.4
          },
          {
              "credito": 1.2
          }
      ],
      "debitoList": [
          {
              "debito": -7.9
          },
          {
              "debito": -2.0
          }
      ],
      "saldo": 8.1
  }
  ```

* Vamos inserir um erro na aplicação **SaldoExtrato**, modificando seu respectivo *profile* da aplicação:

  ```
  -- este comando precisa ser executado na mesma sessão do terminal onde a aplicação SaldoExtrato esta sendo executada
  export spring_profiles_active=prod

  mvn spring-boot:run
  ```

* Ao executar a chamada na aplicação **SaldoExtrato**, verificamos que um erro é retornado, e indo no terminal da aplicação, observa-se a referência ao *endpoint credito* alterado:

  ```
  http :8080/api/v1/saldoextrato/mobile
  HTTP/1.1 500
  Connection: close
  Content-Type: application/json
  Date: Sun, 13 Feb 2022 21:26:50 GMT
  Transfer-Encoding: chunked

  {
      "error": "Internal Server Error",
      "path": "/api/v1/saldoextrato/mobile",
      "status": 500,
      "timestamp": "2022-02-13T21:26:50.127+00:00"
  }

  -- na sessão do terminal da aplicação SaldoExtrato:
  2022-02-13 18:26:50.120 ERROR 21592 --- [nio-8080-exec-1] o.a.c.c.C.[.[.[/].[dispatcherServlet]    : Servlet.service() for servlet [dispatcherServlet] in context with path [] threw exception [Request processing failed; nested exception is org.springframework.web.client.ResourceAccessException: I/O error on GET request for "http://credito:8081/api/v1/credito": credito; nested exception is java.net.UnknownHostException: credito] with root cause

  java.net.UnknownHostException: credito
  ```

* Para desfazer o erro, basta interromper a execução da aplicação **

  ```
  -- este comando precisa ser executado na mesma sessão do terminal onde a aplicação SaldoExtrato esta sendo executada
  export spring_profiles_active=prod

  mvn spring-boot:run
  ```

### 5 - Criação Service Discovery Server <a name="workshop-service-discovery-server">

* Acesse o [Spring Boot Initializer](https://start.spring.io/) e gere um projeto com as seguintes informações:

  ```
  Project: Maven
  Language: Java
  Spring Boot: 2.4.2
  Project Metadata
    Group: br.com.impacta.fullstack
    artifact: servicediscovery
    name: servicediscovery
    Package Name: br.com.impacta.fullstack.servicediscovery
    Packaging: jar
    Java: 11
  Dependencies:
    Eureka Server
  ```
    ![Criação Service Discovery Server](images/workshop-criacao-servicediscovery-server/criacao-projeto-servicediscovery-server.png)

* Altere o arquivo **pom.xml** desse projeto adicionando a dependência *<dependency><groupId>org.glassfish.jaxb</groupId><artifactId>jaxb-runtime</artifactId></dependency>*:

  ```
  <dependency>
		<groupId>org.glassfish.jaxb</groupId>
		<artifactId>jaxb-runtime</artifactId>
	</dependency>
  ```

* Altere a classe **ServiceDiscoveryApplication** adicionando a *EnableEurekaServer:

  ```
  package br.com.impacta.fullstack.servicediscovery;

  import org.springframework.boot.SpringApplication;
  import org.springframework.boot.autoconfigure.SpringBootApplication;
  import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

  @SpringBootApplication
  @EnableEurekaServer
  public class ServiceDiscoveryApplication {

  	public static void main(String[] args) {
  		SpringApplication.run(ServiceDiscoveryApplication.class, args);
  	}

  }
  ```

* Altere o arquivo **application.properties** com o seguinte conteúdo:

  ```
  server.port=8761

  eureka.client.register-with-eureka=false
  eureka.client.fetch-registry=false

  logging.level.com.netflix.eureka=OFF
  logging.level.com.netflix.discovery=OFF
  ```

* Modifique o arquivo **pom.xml** projeto **Credito** adicionando o seguinte conteúdo:

  ```
  <?xml version="1.0" encoding="UTF-8"?>
  <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
  	<modelVersion>4.0.0</modelVersion>
  	<parent>
  		<groupId>org.springframework.boot</groupId>
  		<artifactId>spring-boot-starter-parent</artifactId>
  		<version>2.4.2</version>
  		<relativePath/> <!-- lookup parent from repository -->
  	</parent>
  	<groupId>br.com.impacta.fullstack</groupId>
  	<artifactId>credito</artifactId>
  	<version>0.0.2-SNAPSHOT</version>
  	<name>credito</name>
  	<description>Demo project for Spring Boot</description>

  	<properties>
  		<java.version>11</java.version>
  		<spring-cloud.version>2020.0.0</spring-cloud.version>
  	</properties>

  	<dependencies>
  		<dependency>
  			<groupId>org.springframework.boot</groupId>
  			<artifactId>spring-boot-starter-web</artifactId>
  		</dependency>
  		<dependency>
  			<groupId>org.springframework.cloud</groupId>
  			<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
  		</dependency>
  		<dependency>
  			<groupId>org.springframework.boot</groupId>
  			<artifactId>spring-boot-starter-test</artifactId>
  			<scope>test</scope>
  		</dependency>
  	</dependencies>

  	<dependencyManagement>
  		<dependencies>
  			<dependency>
  				<groupId>org.springframework.cloud</groupId>
  				<artifactId>spring-cloud-dependencies</artifactId>
  				<version>${spring-cloud.version}</version>
  				<type>pom</type>
  				<scope>import</scope>
  			</dependency>
  		</dependencies>
  	</dependencyManagement>

  	<build>
  		<plugins>
  			<plugin>
  				<groupId>org.springframework.boot</groupId>
  				<artifactId>spring-boot-maven-plugin</artifactId>
  			</plugin>
  		</plugins>
  	</build>

  </project>
  ```

* Modifique o arquivo **application.properties** projeto **Credito** adicionando o seguinte conteúdo:

  ```
  server.port = 8081
  spring.application.name=credito-v2
  eureka.client.serviceUrl.defaultZone = http://localhost:8761/eureka/
  ```

* Modifique o arquivo **pom.xml** projeto **Debito** adicionando o seguinte conteúdo:

  ```
  <?xml version="1.0" encoding="UTF-8"?>
  <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
  	<modelVersion>4.0.0</modelVersion>
  	<parent>
  		<groupId>org.springframework.boot</groupId>
  		<artifactId>spring-boot-starter-parent</artifactId>
  		<version>2.4.2</version>
  		<relativePath/> <!-- lookup parent from repository -->
  	</parent>
  	<groupId>br.com.impacta.fullstack</groupId>
  	<artifactId>debito</artifactId>
  	<version>0.0.1-SNAPSHOT</version>
  	<name>debito</name>
  	<description>Demo project for Spring Boot</description>

  	<properties>
  		<java.version>11</java.version>
  		<spring-cloud.version>2020.0.0</spring-cloud.version>
  	</properties>

  	<dependencies>
  		<dependency>
  			<groupId>org.springframework.boot</groupId>
  			<artifactId>spring-boot-starter-web</artifactId>
  		</dependency>
  		<dependency>
  			<groupId>org.springframework.cloud</groupId>
  			<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
  		</dependency>
  		<dependency>
  			<groupId>org.springframework.boot</groupId>
  			<artifactId>spring-boot-starter-test</artifactId>
  			<scope>test</scope>
  		</dependency>
  	</dependencies>

  	<dependencyManagement>
  		<dependencies>
  			<dependency>
  				<groupId>org.springframework.cloud</groupId>
  				<artifactId>spring-cloud-dependencies</artifactId>
  				<version>${spring-cloud.version}</version>
  				<type>pom</type>
  				<scope>import</scope>
  			</dependency>
  		</dependencies>
  	</dependencyManagement>

  	<build>
  		<plugins>
  			<plugin>
  				<groupId>org.springframework.boot</groupId>
  				<artifactId>spring-boot-maven-plugin</artifactId>
  			</plugin>
  		</plugins>
  	</build>

  </project>
  ```

* Modifique o arquivo **application.properties** projeto **Credito** adicionando o seguinte conteúdo:

  ```
  server.port = 8082
  spring.application.name=debito-v2
  eureka.client.serviceUrl.defaultZone = http://localhost:8761/eureka/
  ```

* Modifique o arquivo **pom.xml** projeto **SaldoExtrato** adicionando o seguinte conteúdo:

  ```
  <?xml version="1.0" encoding="UTF-8"?>
  <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
  	<modelVersion>4.0.0</modelVersion>
  	<parent>
  		<groupId>org.springframework.boot</groupId>
  		<artifactId>spring-boot-starter-parent</artifactId>
  		<version>2.4.2</version>
  		<relativePath/>
  	</parent>
  	<groupId>br.com.impacta.fullstack</groupId>
  	<artifactId>saldoextrato</artifactId>
  	<version>0.0.4-SNAPSHOT</version>
  	<name>saldoextrato</name>
  	<description>Demo project for Spring Boot</description>

  	<properties>
  		<java.version>11</java.version>
  		<spring.cloud-version>2020.0.0</spring.cloud-version>
  	</properties>

  	<dependencies>
  		<dependency>
  			<groupId>org.springframework.boot</groupId>
  			<artifactId>spring-boot-starter-web</artifactId>
  		</dependency>
  		<dependency>
  			<groupId>org.springframework.cloud</groupId>
  			<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
  		</dependency>
  		<!--
  		<dependency>
  			<groupId>org.springframework.cloud</groupId>
  			<artifactId>spring-cloud-starter-config</artifactId>
  		</dependency>-->
  		<dependency>
  			<groupId>org.springframework.cloud</groupId>
  			<artifactId>spring-cloud-starter-bootstrap</artifactId>
  		</dependency>
  		<dependency>
  			<groupId>org.springframework.boot</groupId>
  			<artifactId>spring-boot-starter-test</artifactId>
  			<scope>test</scope>
  		</dependency>
  	</dependencies>

  	<dependencyManagement>
  		<dependencies>
  			<dependency>
  				<groupId>org.springframework.cloud</groupId>
  				<artifactId>spring-cloud-dependencies</artifactId>
  				<version>${spring.cloud-version}</version>
  				<type>pom</type>
  				<scope>import</scope>
  			</dependency>
  		</dependencies>
  	</dependencyManagement>

  	<build>
  		<plugins>
  			<plugin>
  				<groupId>org.springframework.boot</groupId>
  				<artifactId>spring-boot-maven-plugin</artifactId>
  			</plugin>
  		</plugins>
  	</build>

  </project>
  ```

* Modifique o arquivo **application.properties** projeto **SaldoExtrato** adicionando o seguinte conteúdo:

  ```
  spring.jackson.default-property-inclusion = non_null
  eureka.client.serviceUrl.defaultZone = http://localhost:8761/eureka/
  spring.application.name=saldoextrato
  ```

* Remova/comente o conteúdo do arquivo **bootstrap.properties** no projeto **SaldoExtrato**:

  ```
  #spring.cloud.config.uri=http://${CONFIG_HOST}:8888
  #management.endpoints.web.exposure.include=refresh
  ```

* Altere o conteúdo da classe **br.com.impacta.fullstack.saldoextrato.SaldoExtratoService*** no projeto **SaldoExtrato**:

  ```
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

      //@Value("${CREDITO_API_URL}")
      //private String CREDITO_API_URL;

      //@Value("${DEBITO_API_URL}")
      //private String DEBITO_API_URL;

      @Autowired
      private DiscoveryClient discoveryClient;

      public SaldoExtrato get(){
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
          return saldoExtrato;
      }

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

  }
  ```

### 6 - Habilitar Circuit Breaker <a name="workshop-circuit-breaker">

* Alterar o arquivo **pom.xml** do projeto **SaldoExtrato**:

  ```
  <?xml version="1.0" encoding="UTF-8"?>
  <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
  	<modelVersion>4.0.0</modelVersion>
  	<parent>
  		<groupId>org.springframework.boot</groupId>
  		<artifactId>spring-boot-starter-parent</artifactId>
  		<version>2.3.7.RELEASE</version>
  		<!--version>2.4.2</version-->
  		<relativePath/>
  	</parent>
  	<groupId>br.com.impacta.fullstack</groupId>
  	<artifactId>saldoextrato</artifactId>
  	<version>0.0.5-SNAPSHOT</version>
  	<name>saldoextrato</name>
  	<description>Demo project for Spring Boot</description>

  	<properties>
  		<java.version>11</java.version>
  		<!--<spring.cloud-version>2020.0.0</spring.cloud-version-->
  		<spring.cloud-version>Hoxton.SR9</spring.cloud-version>
  	</properties>

  	<dependencies>
  		<dependency>
  			<groupId>org.springframework.boot</groupId>
  			<artifactId>spring-boot-starter-web</artifactId>
  		</dependency>
  		<dependency>
  			<groupId>org.springframework.boot</groupId>
  			<artifactId>spring-boot-starter-actuator</artifactId>
  		</dependency>
  		<dependency>
  			<groupId>org.springframework.cloud</groupId>
  			<artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
  		</dependency>
  		<dependency>
  			<groupId>org.springframework.cloud</groupId>
  			<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
  		</dependency>
  		<dependency>
  			<groupId>org.springframework.cloud</groupId>
  			<artifactId>spring-cloud-starter-config</artifactId>
  		</dependency>
  		<!--dependency>
  			<groupId>org.springframework.cloud</groupId>
  			<artifactId>spring-cloud-starter-bootstrap</artifactId>
  		</dependency-->
  		<dependency>
  			<groupId>org.springframework.boot</groupId>
  			<artifactId>spring-boot-starter-test</artifactId>
  			<scope>test</scope>
  		</dependency>
  	</dependencies>

  	<dependencyManagement>
  		<dependencies>
  			<dependency>
  				<groupId>org.springframework.cloud</groupId>
  				<artifactId>spring-cloud-dependencies</artifactId>
  				<version>${spring.cloud-version}</version>
  				<type>pom</type>
  				<scope>import</scope>
  			</dependency>
  		</dependencies>
  	</dependencyManagement>

  	<build>
  		<plugins>
  			<plugin>
  				<groupId>org.springframework.boot</groupId>
  				<artifactId>spring-boot-maven-plugin</artifactId>
  			</plugin>
  		</plugins>
  	</build>

  </project>
  ```

* Incluir endpoints gerenciamentodo *Actuator* no arquivo **application.properties**:

  ```
  # export spring_profiles_active=dev
  # export CONFIG_HOST=localhost
  spring.jackson.default-property-inclusion = non_null
  eureka.client.serviceUrl.defaultZone = http://localhost:8761/eureka/
  spring.application.name=saldoextrato
  management.endpoints.web.exposure.include=*
  ```

* Alterar a classe **br.com.impacta.fullstack.saldoextrato.SaldoExtratoApplicationController**

  ```
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
  ```

* Alterar a classe **br.com.impacta.fullstack.saldoextrato.SaldoExtratoService**

  ```
  package br.com.impacta.fullstack.saldoextrato;

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

      public SaldoExtrato get(){
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
  ```

### 7 - Habilitar Tracing <a name="workshop-tracing">

* Alterar o arquivo **pom.xml** do projeto **SaldoExtrato** com o seguinte conteúdo:

  ```
  <?xml version="1.0" encoding="UTF-8"?>
  <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
  	<modelVersion>4.0.0</modelVersion>
  	<parent>
  		<groupId>org.springframework.boot</groupId>
  		<artifactId>spring-boot-starter-parent</artifactId>
  		<version>2.3.7.RELEASE</version>
  		<!--version>2.4.2</version-->
  		<relativePath/>
  	</parent>
  	<groupId>br.com.impacta.fullstack</groupId>
  	<artifactId>saldoextrato</artifactId>
  	<version>0.0.6-SNAPSHOT</version>
  	<name>saldoextrato</name>
  	<description>Demo project for Spring Boot</description>

  	<properties>
  		<java.version>11</java.version>
  		<!--<spring.cloud-version>2020.0.0</spring.cloud-version-->
  		<spring.cloud-version>Hoxton.SR9</spring.cloud-version>
  	</properties>

  	<dependencies>
  		<dependency>
  			<groupId>org.springframework.boot</groupId>
  			<artifactId>spring-boot-starter-web</artifactId>
  		</dependency>
  		<dependency>
  			<groupId>org.springframework.boot</groupId>
  			<artifactId>spring-boot-starter-actuator</artifactId>
  		</dependency>
  		<dependency>
  			<groupId>org.springframework.cloud</groupId>
  			<artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
  		</dependency>
  		<dependency>
  			<groupId>org.springframework.cloud</groupId>
  			<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
  		</dependency>
  		<dependency>
  			<groupId>org.springframework.cloud</groupId>
  			<artifactId>spring-cloud-starter-config</artifactId>
  		</dependency>
  		<!--dependency>
  			<groupId>org.springframework.cloud</groupId>
  			<artifactId>spring-cloud-starter-bootstrap</artifactId>
  		</dependency-->
  		<dependency>
  			<groupId>org.springframework.cloud</groupId>
  			<artifactId>spring-cloud-starter-zipkin</artifactId>
  		</dependency>
  		<dependency>
  			<groupId>org.springframework.boot</groupId>
  			<artifactId>spring-boot-starter-test</artifactId>
  			<scope>test</scope>
  		</dependency>
  	</dependencies>

  	<dependencyManagement>
  		<dependencies>
  			<dependency>
  				<groupId>org.springframework.cloud</groupId>
  				<artifactId>spring-cloud-dependencies</artifactId>
  				<version>${spring.cloud-version}</version>
  				<type>pom</type>
  				<scope>import</scope>
  			</dependency>
  		</dependencies>
  	</dependencyManagement>

  	<build>
  		<plugins>
  			<plugin>
  				<groupId>org.springframework.boot</groupId>
  				<artifactId>spring-boot-maven-plugin</artifactId>
  			</plugin>
  		</plugins>
  	</build>

  </project>
  ```

* Modificar a classe **br.com.impacta.fullstack.saldoextrato.SaldoExtratoController** com o seguinte conteúdo:

  ```
  package br.com.impacta.fullstack.saldoextrato;

  import brave.Span;
  import brave.Tracer;
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
  import org.springframework.web.bind.annotation.GetMapping;
  import org.springframework.web.bind.annotation.RequestMapping;
  import org.springframework.web.bind.annotation.RestController;

  import java.net.InetAddress;
  import java.net.UnknownHostException;

  @RestController
  @RequestMapping("/api/v1/saldoextrato")
  @EnableCircuitBreaker
  public class SaldoExtratoController {

      private final SaldoExtratoService saldoExtratoService;

      @Autowired
      private Tracer tracer;

      public SaldoExtratoController(SaldoExtratoService saldoExtratoService) {
          this.saldoExtratoService = saldoExtratoService;
      }

      @GetMapping
      public SaldoExtrato get() throws UnknownHostException {
          Span newSpan = tracer.nextSpan().name("saldoextrato").start();
          System.out.println("Hostname: " + InetAddress.getLocalHost().getHostName());
          SaldoExtrato saldoExtrato = saldoExtratoService.get();
          newSpan.finish();
          return saldoExtrato;
      }

      @GetMapping
      @RequestMapping("/mobile")
      public SaldoExtrato getBff() throws UnknownHostException {
          System.out.println("Hostname: " + InetAddress.getLocalHost().getHostName());
          SaldoExtrato saldoExtrato = saldoExtratoService.getBff();
          return saldoExtrato;
      }

  }
  ```

* Modificar a classe **br.com.impacta.fullstack.saldoextrato.SaldoExtratoService** com o seguinte conteúdo:

  ```
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
  ```

* Inicialize o serviço do *Zipkin*:

  ```
  docker run -it \
    --name trace \
    -p 9411:9411 \
    openzipkin/zipkin
  ```

### 8 - Habilitar Monitoramento <a name="workshop-monitoramento">

* Inclua a extensão do *micrometer-registry-prometheus* no arquivo **pom.xml**

  ```
  <?xml version="1.0" encoding="UTF-8"?>
  <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
  	<modelVersion>4.0.0</modelVersion>
  	<parent>
  		<groupId>org.springframework.boot</groupId>
  		<artifactId>spring-boot-starter-parent</artifactId>
  		<version>2.3.7.RELEASE</version>
  		<!--version>2.4.2</version-->
  		<relativePath/>
  	</parent>
  	<groupId>br.com.impacta.fullstack</groupId>
  	<artifactId>saldoextrato</artifactId>
  	<version>0.0.7-SNAPSHOT</version>
  	<name>saldoextrato</name>
  	<description>Demo project for Spring Boot</description>

  	<properties>
  		<java.version>11</java.version>
  		<!--<spring.cloud-version>2020.0.0</spring.cloud-version-->
  		<spring.cloud-version>Hoxton.SR9</spring.cloud-version>
  	</properties>

  	<dependencies>
  		<dependency>
  			<groupId>org.springframework.boot</groupId>
  			<artifactId>spring-boot-starter-web</artifactId>
  		</dependency>
  		<dependency>
  			<groupId>org.springframework.boot</groupId>
  			<artifactId>spring-boot-starter-actuator</artifactId>
  		</dependency>
  		<dependency>
  			<groupId>org.springframework.cloud</groupId>
  			<artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
  		</dependency>
  		<dependency>
  			<groupId>org.springframework.cloud</groupId>
  			<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
  		</dependency>
  		<dependency>
  			<groupId>org.springframework.cloud</groupId>
  			<artifactId>spring-cloud-starter-config</artifactId>
  		</dependency>
  		<!--dependency>
  			<groupId>org.springframework.cloud</groupId>
  			<artifactId>spring-cloud-starter-bootstrap</artifactId>
  		</dependency-->
  		<dependency>
  			<groupId>org.springframework.cloud</groupId>
  			<artifactId>spring-cloud-starter-zipkin</artifactId>
  		</dependency>
  		<dependency>
  			<groupId>io.micrometer</groupId>
  			<artifactId>micrometer-registry-prometheus</artifactId>
  		</dependency>
  		<dependency>
  			<groupId>org.springframework.boot</groupId>
  			<artifactId>spring-boot-starter-test</artifactId>
  			<scope>test</scope>
  		</dependency>
  	</dependencies>

  	<dependencyManagement>
  		<dependencies>
  			<dependency>
  				<groupId>org.springframework.cloud</groupId>
  				<artifactId>spring-cloud-dependencies</artifactId>
  				<version>${spring.cloud-version}</version>
  				<type>pom</type>
  				<scope>import</scope>
  			</dependency>
  		</dependencies>
  	</dependencyManagement>

  	<build>
  		<plugins>
  			<plugin>
  				<groupId>org.springframework.boot</groupId>
  				<artifactId>spring-boot-maven-plugin</artifactId>
  			</plugin>
  		</plugins>
  	</build>

  </project>
  ```

* Inicialize o serviço do *Prometheus:*

  ```
  ./prometheus --config.file=prometheus.yml
  http://localhost:9090/
  brew services start grafana
  http://localhost:3000/
  ```

* Valide as informações no console gráfico: *http://localhost:9090*  
