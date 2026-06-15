package br.com.tonspersonalizados.dto.pedidos;

import jakarta.validation.constraints.NotBlank;

public class CancelamentoPedidoRequestDto {

    @NotBlank(message = "O motivo do cancelamento é obrigatório")
    private String motivo;

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
