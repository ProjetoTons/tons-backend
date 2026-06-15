package br.com.tonspersonalizados.dto.pedidos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class PedidoRequestDto {

    @NotBlank(message = "Número do pedido é obrigatório")
    @Size(max = 45)
    private String numPedido;

    @Size(max = 512)
    private String urlFotoArte;

    private String descricao;

    private String observacao;

    @NotBlank(message = "Etapa é obrigatória")
    @Size(max = 45)
    private String etapaPedido;

    @NotBlank(message = "Status é obrigatório")
    @Size(max = 50)
    private String status;

    @NotNull(message = "Valor total é obrigatório")
    @PositiveOrZero(message = "Valor total não pode ser negativo")
    private BigDecimal valorTotal;

    @NotNull(message = "Data do pedido é obrigatória")
    private LocalDateTime dataPedido;

    private LocalDateTime dataInicio;

    private LocalDateTime dataFinalizacao;

    @Size(max = 45)
    private String tipoEnvio;

    @NotNull(message = "Endereço é obrigatório")
    private Long idEndereco;

    @NotNull(message = "Cliente é obrigatório")
    private Long idUsuarioCliente;

    // Opcional na criação — fica null até alguém pegar o pedido
    private Long idUsuarioResponsavel;

    // Vendedor que registrou o pedido (opcional)
    private Long idUsuarioVendedor;

    @NotEmpty(message = "Pedido deve ter ao menos 1 item")
    @Valid
    private List<ItemPedidoRequestDto> itens;

    public PedidoRequestDto() {}



    // Getters e Setters
    public String getNumPedido() {
        return numPedido;
    }

    public void setNumPedido(String numPedido) {
        this.numPedido = numPedido;
    }

    public String getUrlFotoArte() {
        return urlFotoArte;
    }

    public void setUrlFotoArte(String urlFotoArte) {
        this.urlFotoArte = urlFotoArte;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
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

    public LocalDateTime getDataPedido() {
        return dataPedido;
    }

    public void setDataPedido(LocalDateTime dataPedido) {
        this.dataPedido = dataPedido;
    }

    public LocalDateTime getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDateTime dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDateTime getDataFinalizacao() {
        return dataFinalizacao;
    }

    public void setDataFinalizacao(LocalDateTime dataFinalizacao) {
        this.dataFinalizacao = dataFinalizacao;
    }

    public String getTipoEnvio() {
        return tipoEnvio;
    }

    public void setTipoEnvio(String tipoEnvio) {
        this.tipoEnvio = tipoEnvio;
    }

    public Long getIdEndereco() {
        return idEndereco;
    }

    public void setIdEndereco(Long idEndereco) {
        this.idEndereco = idEndereco;
    }

    public Long getIdUsuarioCliente() {
        return idUsuarioCliente;
    }

    public void setIdUsuarioCliente(Long idUsuarioCliente) {
        this.idUsuarioCliente = idUsuarioCliente;
    }

    public Long getIdUsuarioResponsavel() {
        return idUsuarioResponsavel;
    }

    public void setIdUsuarioResponsavel(Long idUsuarioResponsavel) {
        this.idUsuarioResponsavel = idUsuarioResponsavel;
    }

    public Long getIdUsuarioVendedor() {
        return idUsuarioVendedor;
    }

    public void setIdUsuarioVendedor(Long idUsuarioVendedor) {
        this.idUsuarioVendedor = idUsuarioVendedor;
    }

    public List<ItemPedidoRequestDto> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedidoRequestDto> itens) {
        this.itens = itens;
    }
}