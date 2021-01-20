package br.com.impacta.fullstack.debito;

import java.io.Serializable;
import java.math.BigDecimal;

public class Debito implements Serializable {

    private BigDecimal debito;

    public Debito(BigDecimal debito) {
        super();
        this.debito = debito;
    }

    public BigDecimal getDebito() {
        return debito;
    }

    public void setDebito(BigDecimal debito) {
        this.debito = debito;
    }
}
