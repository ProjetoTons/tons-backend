package br.com.tonspersonalizados.exception.usuarios;

import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EnderecoNaoEncontradoException extends RuntimeException {
    public EnderecoNaoEncontradoException(String message) {
        super(message);
    }
}
