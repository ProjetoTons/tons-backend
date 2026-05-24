package br.com.tonspersonalizados.event;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import br.com.tonspersonalizados.dto.notificacoes.NotificacaoDto;
import br.com.tonspersonalizados.entity.pedidos.Pedido;
import br.com.tonspersonalizados.entity.usuarios.Usuario;
import br.com.tonspersonalizados.service.notificacoes.NotificacaoService;
import br.com.tonspersonalizados.service.notificacoes.WhatsAppService;

@Component
public class PedidoNotificacaoListener {

    private final WhatsAppService whatsAppService;
    private final NotificacaoService notificacaoService;

    public PedidoNotificacaoListener(WhatsAppService whatsAppService,
                                     NotificacaoService notificacaoService) {
        this.whatsAppService = whatsAppService;
        this.notificacaoService = notificacaoService;
    }

    @Async
    @EventListener
    public void onEtapaAvancada(EtapaAvancadaEvent event) {
        Pedido pedido = event.getPedido();
        Usuario cliente = pedido.getUsuarioCliente();
        String numPedido = pedido.getNumPedido();

        String mensagem = "Olá " + cliente.getNome() + "! Seu pedido #" + numPedido
                + " avançou para: " + event.getEtapa() + " - " + event.getStatus()
                + ". Acompanhe pelo nosso sistema!";

        // WhatsApp
        try {
            if (cliente.getTelefone() != null) {
                whatsAppService.enviarMensagem(cliente.getTelefone(), mensagem);
            }
        } catch (Exception e) {
            // Log do erro mas não interrompe o fluxo
        }

        // Email
        try {
            if (cliente.getLogin() != null && cliente.getLogin().getEmail() != null) {
                NotificacaoDto email = new NotificacaoDto();
                email.setDestinatario(cliente.getLogin().getEmail());
                email.setAssunto("Pedido #" + numPedido + " - Atualização de etapa");
                email.setCorpo(mensagem);
                notificacaoService.enviarEmail(email);
            }
        } catch (Exception e) {
            // Log do erro mas não interrompe o fluxo
        }
    }
}
