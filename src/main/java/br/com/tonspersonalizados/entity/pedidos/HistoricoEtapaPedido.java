package br.com.tonspersonalizados.entity.pedidos;

import br.com.tonspersonalizados.entity.usuarios.Usuario;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historico_etapa_pedido")
public class HistoricoEtapaPedido {

    @Id
    @Column(name = "fk_etapa_pedido")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // A qual pedido pertence
    @ManyToOne
    @JoinColumn(name = "fk_pedido", nullable = false)
    private Pedido pedido;

    // Usuário que registrou a mudança de etapa
    @ManyToOne
    @JoinColumn(name = "fk_usuario", nullable = false)
    private Usuario usuario;

    // Funcionário responsável por esta etapa
    @ManyToOne
    @JoinColumn(name = "fk_usuario_etapa_responsavel", nullable = false)
    private Usuario responsavelEtapa;

    @Column(name = "data_entrada", nullable = false)
    private LocalDateTime dataEntrada;

    @Column(name = "data_saida")
    private LocalDateTime dataSaida;

    @Column(name = "observacoes", length = 250)
    private String observacoes;

    @Column(name = "status_etapa", length = 45)
    private String statusEtapa;

    @Column(name = "etapa", length = 45)
    private String etapa;

    public HistoricoEtapaPedido() {}

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Usuario getResponsavelEtapa() { return responsavelEtapa; }
    public void setResponsavelEtapa(Usuario responsavelEtapa) { this.responsavelEtapa = responsavelEtapa; }

    public LocalDateTime getDataEntrada() { return dataEntrada; }
    public void setDataEntrada(LocalDateTime dataEntrada) { this.dataEntrada = dataEntrada; }

    public LocalDateTime getDataSaida() { return dataSaida; }
    public void setDataSaida(LocalDateTime dataSaida) { this.dataSaida = dataSaida; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public String getStatusEtapa() { return statusEtapa; }
    public void setStatusEtapa(String statusEtapa) { this.statusEtapa = statusEtapa; }

    public String getEtapa() { return etapa; }
    public void setEtapa(String etapa) { this.etapa = etapa; }
}