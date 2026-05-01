package br.com.tonspersonalizados.dto.usuarios;

import jakarta.validation.constraints.NotBlank;

public class ResetSenhaRequestDto {

    @NotBlank
    private String token;

    @NotBlank
    private String novaSenha;

    public String getToken() {
        return token;
    }

    public String getNovaSenha() {
        return novaSenha;
    }
}
