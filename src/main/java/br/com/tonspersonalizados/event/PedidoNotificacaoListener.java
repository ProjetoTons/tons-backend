package br.com.tonspersonalizados.event;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import br.com.tonspersonalizados.dto.notificacoes.NotificacaoDto;
import br.com.tonspersonalizados.entity.pedidos.Pedido;
import br.com.tonspersonalizados.entity.usuarios.Usuario;
import br.com.tonspersonalizados.service.notificacoes.NotificacaoService;
import br.com.tonspersonalizados.service.notificacoes.WhatsAppService;

@Component
public class PedidoNotificacaoListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PedidoNotificacaoListener.class);

    private final WhatsAppService whatsAppService;
    private final NotificacaoService notificacaoService;
    private final String frontendBaseUrl;

    public PedidoNotificacaoListener(WhatsAppService whatsAppService,
                                     NotificacaoService notificacaoService,
                                     @Value("${frontend.base-url}") String frontendBaseUrl) {
        this.whatsAppService = whatsAppService;
        this.notificacaoService = notificacaoService;
        this.frontendBaseUrl = frontendBaseUrl;
    }

    @Async
    @EventListener
    public void onEtapaAvancada(EtapaAvancadaEvent event) {
        Pedido pedido = event.getPedido();
        Usuario cliente = pedido.getUsuarioCliente();
        String numPedido = pedido.getNumPedido();

        String mensagem = "Olá " + cliente.getNome() + "! Seu pedido #" + numPedido
                + " avançou para: " + event.getEtapa() + ". Acompanhe pelo nosso sistema em: "
                + frontendBaseUrl + "/meus-pedidos";

        // WhatsApp
        try {
            if (cliente.getTelefone() != null) {
                whatsAppService.enviarMensagem(cliente.getTelefone(), mensagem);
            }
        } catch (Exception e) {
            LOGGER.error("Falha ao enfileirar notificação de e-mail do pedido {}", numPedido, e);
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
