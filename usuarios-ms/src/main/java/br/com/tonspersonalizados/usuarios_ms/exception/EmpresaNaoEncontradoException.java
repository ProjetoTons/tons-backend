package br.com.tonspersonalizados.usuarios_ms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EmpresaNaoEncontradoException extends RuntimeException {
    public EmpresaNaoEncontradoException(String message) {
        super(message);
    }
}
