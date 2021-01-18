package br.com.impacta.fullstack.debito;

import java.io.Serializable;
import java.math.BigDecimal;

public class Debito implements Serializable {

    private BigDecimal debito;

    public Debito(BigDecimal debito) {
        super();
        this.debito = debito;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    public BigDecimal getDebito() {
        return debito;
    }

    public void setDebito(BigDecimal debito) {
        this.debito = debito;
    }
}
