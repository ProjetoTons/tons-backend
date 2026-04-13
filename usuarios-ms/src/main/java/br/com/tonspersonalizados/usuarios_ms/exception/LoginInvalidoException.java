package br.com.tonspersonalizados.usuarios_ms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class LoginInvalidoException extends RuntimeException {
    public LoginInvalidoException(String message) {

        super(message);
    }
}
