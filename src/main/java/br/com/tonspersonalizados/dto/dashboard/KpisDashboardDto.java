package br.com.tonspersonalizados.dto.dashboard;

import java.math.BigDecimal;

public class KpisDashboardDto {
    private BigDecimal totalValor;
    private int aguardandoArte;
    private int aguardandoRetirada;
    private int enviada;
    private BigDecimal metaSemanal;
    private int totalPedidos;

    public KpisDashboardDto(BigDecimal totalValor, int aguardandoArte,
                            int aguardandoRetirada, int enviada, BigDecimal metaSemanal, int totalPedidos) {
        this.totalValor = totalValor;
        this.aguardandoArte = aguardandoArte;
        this.aguardandoRetirada = aguardandoRetirada;
        this.enviada = enviada;
        this.metaSemanal = metaSemanal;
        this.totalPedidos = totalPedidos;
    }

    public BigDecimal getTotalValor() { return totalValor; }
    public void setTotalValor(BigDecimal totalValor) { this.totalValor = totalValor; }
    public int getAguardandoArte() { return aguardandoArte; }
    public void setAguardandoArte(int aguardandoArte) { this.aguardandoArte = aguardandoArte; }
    public int getAguardandoRetirada() { return aguardandoRetirada; }
    public void setAguardandoRetirada(int aguardandoRetirada) { this.aguardandoRetirada = aguardandoRetirada; }
    public int getEnviada() { return enviada; }
    public void setEnviada(int enviada) { this.enviada = enviada; }
    public BigDecimal getMetaSemanal() { return metaSemanal; }
    public void setMetaSemanal(BigDecimal metaSemanal) { this.metaSemanal = metaSemanal; }
    public int getTotalPedidos() { return totalPedidos; }
    public void setTotalPedidos(int totalPedidos) { this.totalPedidos = totalPedidos; }
}
