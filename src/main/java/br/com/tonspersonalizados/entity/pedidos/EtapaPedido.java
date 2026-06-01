package br.com.tonspersonalizados.entity.pedidos;

import java.util.List;

public enum EtapaPedido {

    DESIGN("Design", List.of(
            "Não Iniciado",
            "Aguardando arte",
            "Criando Mockup",
            "Aguardando aprovação",
            "Impressão fotolito"
    )),

    PRODUCAO("Produção", List.of(
            "Não Iniciado",
            "Conferindo",
            "Personalizando"
    )),

    EMBALAGEM("Embalagem", List.of(
            "Não Iniciado",
            "Quality check",
            "Embalagem",
            "Medição",
            "Emitir etiqueta"
    )),

    LOGISTICA("Logística", List.of(
            "Enviado",
            "Aguardando retirada"
    )),

    FINALIZADO("Finalizado", List.of(
            "Concluído"
    ));

    private final String label;
    private final List<String> statusValidos;

    EtapaPedido(String label, List<String> statusValidos) {
        this.label = label;
        this.statusValidos = statusValidos;
    }

    public String getLabel() {
        return label;
    }

    public List<String> getStatusValidos() {
        return statusValidos;
    }

    public static EtapaPedido fromLabel(String label) {
        for (EtapaPedido etapa : values()) {
            if (etapa.label.equalsIgnoreCase(label)) {
                return etapa;
            }
        }
        throw new IllegalArgumentException("Etapa inválida: " + label);
    }

    public boolean isStatusValido(String status) {
        return statusValidos.stream()
                .anyMatch(s -> s.equalsIgnoreCase(status));
    }
}
