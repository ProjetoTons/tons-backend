package br.com.tonspersonalizados.usuarios_ms.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public class LoginRequestDto {

    @Email
    @NotNull
    private  String email;

    @NotNull
    private String senha;


    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }
}
