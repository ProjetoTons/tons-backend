package br.com.tonspersonalizados.entity.pedidos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import br.com.tonspersonalizados.entity.usuarios.Endereco;
import br.com.tonspersonalizados.entity.usuarios.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @Column(name = "id_pedido")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "url_foto_arte", length = 512)
    private String urlFotoArte;

    @Column(name = "num_pedido", length = 45)
    private String numPedido;

    // Etapa ATUAL: "Design", "Produção", "Embalagem", "Logística"
    @Column(name = "etapa_pedido", length = 45)
    private String etapaPedido;

    // Status ATUAL: "Não iniciado", "Aguardando arte", "Personalizando", etc.
    @Column(name = "status", length = 50, nullable = false)
    private String status;

    @Column(name = "valor_total", precision = 10, scale = 2, nullable = false)
    private BigDecimal valorTotal;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "data_pedido", nullable = false)
    private LocalDateTime dataPedido;

    @Column(name = "data_finalizacao")
    private LocalDateTime dataFinalizacao;

    // FK referenciada por historico_etapa_pedido.fk_usuario
    @ManyToOne
    @JoinColumn(name = "fk_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "num_nota_fiscal", length = 45)
    private String numNotaFiscal;

    // "RETIRADA" ou "CORREIOS"
    @Column(name = "tipo_envio", length = 45)
    private String tipoEnvio;

    // Endereço de entrega
    @ManyToOne
    @JoinColumn(name = "fk_endereco", nullable = false)
    private Endereco endereco;

    // Cliente que fez o pedido
    @ManyToOne
    @JoinColumn(name = "fk_usuario_cliente", nullable = false)
    private Usuario usuarioCliente;

    // Funcionário responsável geral pelo pedido (null = aguardando alguém pegar)
    @ManyToOne
    @JoinColumn(name = "fk_usuario_responsavel")
    private Usuario usuarioResponsavel;

    // Itens do pedido — LAZY para não carregar em listagens
    @OneToMany(mappedBy = "pedido", fetch = FetchType.LAZY)
    private List<ItemPedido> itens;

    // Histórico de etapas — LAZY
    @OneToMany(mappedBy = "pedido", fetch = FetchType.LAZY)
    private List<HistoricoEtapaPedido> historicoEtapas;

    public Pedido() {}





    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getUrlFotoArte() { return urlFotoArte; }
    public void setUrlFotoArte(String urlFotoArte) { this.urlFotoArte = urlFotoArte; }

    public String getNumPedido() { return numPedido; }
    public void setNumPedido(String numPedido) { this.numPedido = numPedido; }

    public String getEtapaPedido() { return etapaPedido; }
    public void setEtapaPedido(String etapaPedido) { this.etapaPedido = etapaPedido; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public LocalDateTime getDataPedido() { return dataPedido; }
    public void setDataPedido(LocalDateTime dataPedido) { this.dataPedido = dataPedido; }

    public LocalDateTime getDataFinalizacao() { return dataFinalizacao; }
    public void setDataFinalizacao(LocalDateTime dataFinalizacao) { this.dataFinalizacao = dataFinalizacao; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getNumNotaFiscal() { return numNotaFiscal; }
    public void setNumNotaFiscal(String numNotaFiscal) { this.numNotaFiscal = numNotaFiscal; }

    public String getTipoEnvio() { return tipoEnvio; }
    public void setTipoEnvio(String tipoEnvio) { this.tipoEnvio = tipoEnvio; }

    public Endereco getEndereco() { return endereco; }
    public void setEndereco(Endereco endereco) { this.endereco = endereco; }

    public Usuario getUsuarioCliente() { return usuarioCliente; }
    public void setUsuarioCliente(Usuario usuarioCliente) { this.usuarioCliente = usuarioCliente; }

    public Usuario getUsuarioResponsavel() { return usuarioResponsavel; }
    public void setUsuarioResponsavel(Usuario usuarioResponsavel) { this.usuarioResponsavel = usuarioResponsavel; }

    public List<ItemPedido> getItens() { return itens; }
    public void setItens(List<ItemPedido> itens) { this.itens = itens; }

    public List<HistoricoEtapaPedido> getHistoricoEtapas() { return historicoEtapas; }
    public void setHistoricoEtapas(List<HistoricoEtapaPedido> historicoEtapas) { this.historicoEtapas = historicoEtapas; }
}