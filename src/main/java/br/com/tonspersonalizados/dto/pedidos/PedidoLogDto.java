package br.com.tonspersonalizados.dto.pedidos;

import br.com.tonspersonalizados.entity.pedidos.Pedido;
import java.math.BigDecimal;

public class PedidoLogDto {
    private Integer id;
    private String numPedido;
    private String etapaPedido;
    private String status;
    private BigDecimal valorTotal;
    private String clienteNome;
    private String responsavelNome;

    public PedidoLogDto() {
    }

    public PedidoLogDto(Integer id, String numPedido, String etapaPedido, String status, BigDecimal valorTotal,
            String clienteNome, String responsavelNome) {
        this.id = id;
        this.numPedido = numPedido;
        this.etapaPedido = etapaPedido;
        this.status = status;
        this.valorTotal = valorTotal;
        this.clienteNome = clienteNome;
        this.responsavelNome = responsavelNome;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNumPedido() {
        return numPedido;
    }

    public void setNumPedido(String numPedido) {
        this.numPedido = numPedido;
    }

    public String getEtapaPedido() {
        return etapaPedido;
    }

    public void setEtapaPedido(String etapaPedido) {
        this.etapaPedido = etapaPedido;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getClienteNome() {
        return clienteNome;
    }

    public void setClienteNome(String clienteNome) {
        this.clienteNome = clienteNome;
    }

    public String getResponsavelNome() {
        return responsavelNome;
    }

    public void setResponsavelNome(String responsavelNome) {
        this.responsavelNome = responsavelNome;
    }

    public static PedidoLogDto from(Pedido p) {
        if (p == null)
            return null;
        return new PedidoLogDto(
                p.getId(),
                p.getNumPedido(),
                p.getEtapaPedido(),
                p.getStatus(),
                p.getValorTotal(),
                p.getUsuarioCliente() != null ? p.getUsuarioCliente().getNome() : null,
                p.getUsuarioResponsavel() != null ? p.getUsuarioResponsavel().getNome() : null);
    }
}
