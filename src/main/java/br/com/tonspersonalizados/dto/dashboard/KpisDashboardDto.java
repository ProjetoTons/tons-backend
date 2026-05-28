package br.com.tonspersonalizados.dto.dashboard;

import java.math.BigDecimal;

public class KpisDashboardDto {
    private BigDecimal totalValor;
    private int aguardandoArte;
    private int enviadoAguardandoRetirada;
    private int totalPedidos;

    public KpisDashboardDto(BigDecimal totalValor, int aguardandoArte,
                            int enviadoAguardandoRetirada, int totalPedidos) {
        this.totalValor = totalValor;
        this.aguardandoArte = aguardandoArte;
        this.enviadoAguardandoRetirada = enviadoAguardandoRetirada;
        this.totalPedidos = totalPedidos;
    }

    public BigDecimal getTotalValor() { return totalValor; }
    public void setTotalValor(BigDecimal totalValor) { this.totalValor = totalValor; }
    public int getAguardandoArte() { return aguardandoArte; }
    public void setAguardandoArte(int aguardandoArte) { this.aguardandoArte = aguardandoArte; }
    public int getEnviadoAguardandoRetirada() { return enviadoAguardandoRetirada; }
    public void setEnviadoAguardandoRetirada(int enviadoAguardandoRetirada) { this.enviadoAguardandoRetirada = enviadoAguardandoRetirada; }
    public int getTotalPedidos() { return totalPedidos; }
    public void setTotalPedidos(int totalPedidos) { this.totalPedidos = totalPedidos; }
}
