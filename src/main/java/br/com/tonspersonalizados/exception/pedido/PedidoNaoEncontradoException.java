package br.com.tonspersonalizados.exception.pedido;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class PedidoNaoEncontradoException extends ResponseStatusException {
    public PedidoNaoEncontradoException(String mensagem) {
        super(HttpStatus.NOT_FOUND, mensagem);
    }
}