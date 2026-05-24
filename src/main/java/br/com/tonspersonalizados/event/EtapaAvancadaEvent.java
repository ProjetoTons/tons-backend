package br.com.tonspersonalizados.event;

import br.com.tonspersonalizados.entity.pedidos.Pedido;

public class EtapaAvancadaEvent {

    private final Pedido pedido;
    private final String etapa;
    private final String status;

    public EtapaAvancadaEvent(Pedido pedido, String etapa, String status) {
        this.pedido = pedido;
        this.etapa = etapa;
        this.status = status;
    }

    public Pedido getPedido() { return pedido; }
    public String getEtapa() { return etapa; }
    public String getStatus() { return status; }
}
