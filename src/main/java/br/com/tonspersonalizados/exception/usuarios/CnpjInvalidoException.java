package br.com.tonspersonalizados.exception.usuarios;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
public class CnpjInvalidoException extends RuntimeException {
    public CnpjInvalidoException(String message) {
        super(message);
    }
}
