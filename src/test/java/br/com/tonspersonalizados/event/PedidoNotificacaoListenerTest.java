package br.com.tonspersonalizados.event;

import br.com.tonspersonalizados.dto.notificacoes.NotificacaoDto;
import br.com.tonspersonalizados.entity.pedidos.Pedido;
import br.com.tonspersonalizados.entity.usuarios.Login;
import br.com.tonspersonalizados.entity.usuarios.Usuario;
import br.com.tonspersonalizados.service.notificacoes.NotificacaoService;
import br.com.tonspersonalizados.service.notificacoes.WhatsAppService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PedidoNotificacaoListener")
class PedidoNotificacaoListenerTest {

    @Mock private WhatsAppService whatsAppService;
    @Mock private NotificacaoService notificacaoService;
    @InjectMocks private PedidoNotificacaoListener listener;

    private EtapaAvancadaEvent criarEvento(String telefone, String email) {
        Usuario cliente = new Usuario();
        cliente.setNome("João");
        cliente.setTelefone(telefone);
        if (email != null) {
            Login login = new Login();
            login.setEmail(email);
            cliente.setLogin(login);
        }

        Pedido pedido = new Pedido();
        pedido.setNumPedido("PED-001");
        pedido.setUsuarioCliente(cliente);

        return new EtapaAvancadaEvent(pedido, "Produção", "Em andamento");
    }

    @Test
    @DisplayName("Deve enviar WhatsApp e email quando ambos disponíveis")
    void deveEnviarWhatsAppEEmail() {
        EtapaAvancadaEvent event = criarEvento("11999990000", "joao@email.com");

        listener.onEtapaAvancada(event);

        verify(whatsAppService).enviarMensagem(anyString(), anyString());
        verify(notificacaoService).enviarEmail(any(NotificacaoDto.class));
    }

    @Test
    @DisplayName("Não deve enviar WhatsApp quando telefone é null")
    void naoDeveEnviarWhatsAppSemTelefone() {
        EtapaAvancadaEvent event = criarEvento(null, "joao@email.com");

        listener.onEtapaAvancada(event);

        verify(whatsAppService, never()).enviarMensagem(anyString(), anyString());
        verify(notificacaoService).enviarEmail(any(NotificacaoDto.class));
    }

    @Test
    @DisplayName("Não deve enviar email quando login é null")
    void naoDeveEnviarEmailSemLogin() {
        EtapaAvancadaEvent event = criarEvento("11999990000", null);

        listener.onEtapaAvancada(event);

        verify(whatsAppService).enviarMensagem(anyString(), anyString());
        verify(notificacaoService, never()).enviarEmail(any());
    }

    @Test
    @DisplayName("Deve continuar fluxo mesmo quando WhatsApp falhar")
    void deveContinuarQuandoWhatsAppFalhar() {
        EtapaAvancadaEvent event = criarEvento("11999990000", "joao@email.com");
        doThrow(new RuntimeException("Falha")).when(whatsAppService).enviarMensagem(anyString(), anyString());

        listener.onEtapaAvancada(event);

        verify(notificacaoService).enviarEmail(any(NotificacaoDto.class));
    }

    @Test
    @DisplayName("Deve continuar fluxo mesmo quando email falhar")
    void deveContinuarQuandoEmailFalhar() {
        EtapaAvancadaEvent event = criarEvento("11999990000", "joao@email.com");
        doThrow(new RuntimeException("Falha")).when(notificacaoService).enviarEmail(any());

        listener.onEtapaAvancada(event);

        verify(whatsAppService).enviarMensagem(anyString(), anyString());
    }
}
