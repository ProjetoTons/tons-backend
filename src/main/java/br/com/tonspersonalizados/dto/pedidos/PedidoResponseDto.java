package br.com.tonspersonalizados.dto.pedidos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import br.com.tonspersonalizados.entity.usuarios.Endereco;

public class PedidoResponseDto {
    private Integer idPedido;
    private String numPedido;
    private String urlFotoArte;
    private String descricao;
    private String etapaPedido;
    private String status;
    private BigDecimal valorTotal;
    private LocalDateTime dataPedido;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFinalizacao;
    private String tipoEnvio;
    private String numNotaFiscal;
    private ClienteResumoDto cliente;
    private FuncionarioResumoDto responsavel;
    private Endereco endereco;
    private List<ItemPedidoResponseDto> itens;

    
    public PedidoResponseDto() {}


    // Getters e Setters
    public Integer getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Integer idPedido) {
        this.idPedido = idPedido;
    }

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

    public String getNumNotaFiscal() {
        return numNotaFiscal;
    }

    public void setNumNotaFiscal(String numNotaFiscal) {
        this.numNotaFiscal = numNotaFiscal;
    }

    public ClienteResumoDto getCliente() {
        return cliente;
    }

    public void setCliente(ClienteResumoDto cliente) {
        this.cliente = cliente;
    }

    public FuncionarioResumoDto getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(FuncionarioResumoDto responsavel) {
        this.responsavel = responsavel;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public List<ItemPedidoResponseDto> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedidoResponseDto> itens) {
        this.itens = itens;
    }
}