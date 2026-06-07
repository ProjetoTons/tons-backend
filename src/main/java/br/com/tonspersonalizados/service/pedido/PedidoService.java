package br.com.tonspersonalizados.service.pedido;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.tonspersonalizados.dto.pedidos.CaracteristicasRequestDto;
import br.com.tonspersonalizados.dto.pedidos.ClienteResumoDto;
import br.com.tonspersonalizados.dto.pedidos.EtapaRequestDto;
import br.com.tonspersonalizados.dto.pedidos.FuncionarioResumoDto;
import br.com.tonspersonalizados.dto.pedidos.HistoricoEtapaResponseDto;
import br.com.tonspersonalizados.dto.pedidos.ItemPedidoRequestDto;
import br.com.tonspersonalizados.dto.pedidos.ItemPedidoResponseDto;
import br.com.tonspersonalizados.dto.pedidos.PedidoRequestDto;
import br.com.tonspersonalizados.dto.pedidos.PedidoResponseDto;
import br.com.tonspersonalizados.entity.pedidos.CaracteristicasItemPedido;
import br.com.tonspersonalizados.entity.pedidos.EtapaPedido;
import br.com.tonspersonalizados.entity.pedidos.HistoricoEtapaPedido;
import br.com.tonspersonalizados.entity.pedidos.ItemPedido;
import br.com.tonspersonalizados.entity.pedidos.Pedido;
import br.com.tonspersonalizados.entity.produtos.Produto;
import br.com.tonspersonalizados.entity.usuarios.Endereco;
import br.com.tonspersonalizados.entity.usuarios.Usuario;
import br.com.tonspersonalizados.event.EtapaAvancadaEvent;
import br.com.tonspersonalizados.exception.pedido.PedidoNaoEncontradoException;
import br.com.tonspersonalizados.exception.produto.ProdutoNaoEncontradoException;
import br.com.tonspersonalizados.exception.usuarios.EnderecoNaoEncontradoException;
import br.com.tonspersonalizados.exception.usuarios.UsuarioNaoEncontradoException;
import br.com.tonspersonalizados.repository.pedido.CaracteristicasItemPedidoRepository;
import br.com.tonspersonalizados.repository.pedido.HistoricoEtapaPedidoRepository;
import br.com.tonspersonalizados.repository.pedido.ItemPedidoRepository;
import br.com.tonspersonalizados.repository.pedido.PedidoRepository;
import br.com.tonspersonalizados.repository.produto.ProdutoRepository;
import br.com.tonspersonalizados.repository.usuarios.EnderecoRepository;
import br.com.tonspersonalizados.repository.usuarios.UsuarioRepository;
import br.com.tonspersonalizados.entity.AcaoLog;
import br.com.tonspersonalizados.service.LogSistemaService;
import br.com.tonspersonalizados.dto.pedidos.PedidoLogDto;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final CaracteristicasItemPedidoRepository caracteristicasRepository;
    private final HistoricoEtapaPedidoRepository historicoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProdutoRepository produtoRepository;
    private final EnderecoRepository enderecoRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final LogSistemaService logSistemaService;

    public PedidoService(PedidoRepository pedidoRepository,
                         ItemPedidoRepository itemPedidoRepository,
                         CaracteristicasItemPedidoRepository caracteristicasRepository,
                         HistoricoEtapaPedidoRepository historicoRepository,
                         UsuarioRepository usuarioRepository,
                         ProdutoRepository produtoRepository,
                         EnderecoRepository enderecoRepository,
                         ApplicationEventPublisher eventPublisher,
                         LogSistemaService logSistemaService) {
        this.pedidoRepository = pedidoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
        this.caracteristicasRepository = caracteristicasRepository;
        this.historicoRepository = historicoRepository;
        this.usuarioRepository = usuarioRepository;
        this.produtoRepository = produtoRepository;
        this.enderecoRepository = enderecoRepository;
        this.eventPublisher = eventPublisher;
        this.logSistemaService = logSistemaService;
    }


    // CRIAR PEDIDO
    @Transactional
    public PedidoResponseDto criarPedido(PedidoRequestDto request) {

        // 1. Validar pessoas
        Usuario cliente = usuarioRepository.findById(request.getIdUsuarioCliente())
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Cliente não encontrado"));

        Usuario responsavel = null;
        if (request.getIdUsuarioResponsavel() != null) {
            responsavel = usuarioRepository.findById(request.getIdUsuarioResponsavel())
                    .orElseThrow(() -> new UsuarioNaoEncontradoException("Funcionário não encontrado"));
        }

        // 2. Buscar endereço existente
        Endereco endereco = enderecoRepository.findById(request.getIdEndereco())
                .orElseThrow(() -> new EnderecoNaoEncontradoException("Endereço não encontrado"));

        // 3. Criar Pedido
        Pedido pedido = new Pedido();
        pedido.setNumPedido(request.getNumPedido());
        pedido.setUrlFotoArte(request.getUrlFotoArte());
        pedido.setDescricao(request.getDescricao());
        pedido.setEtapaPedido(request.getEtapaPedido());
        pedido.setStatus(request.getStatus());
        pedido.setValorTotal(request.getValorTotal());
        pedido.setDataPedido(request.getDataPedido());
        pedido.setDataInicio(request.getDataInicio());
        pedido.setDataFinalizacao(request.getDataFinalizacao());
        pedido.setTipoEnvio(request.getTipoEnvio());
        pedido.setEndereco(endereco);
        pedido.setUsuarioCliente(cliente);
        pedido.setUsuario(cliente);
        pedido.setUsuarioResponsavel(responsavel);

        pedido = pedidoRepository.save(pedido);

        // 4. Criar itens
        List<ItemPedido> itensSalvos = new ArrayList<>();
        for (ItemPedidoRequestDto itemDto : request.getItens()) {
            ItemPedido item = criarItemPedido(pedido, itemDto);
            itensSalvos.add(item);
        }

        logSistemaService.registrar(
                cliente.getId(), AcaoLog.CRIAR, "Pedido",
                pedido.getId().longValue(), "Novo pedido criado",
                null, PedidoLogDto.from(pedido));

        return montarPedidoResponse(pedido, itensSalvos);
    }


    // LISTAR PEDIDOS
    public List<PedidoResponseDto> listarTodos() {
        return pedidoRepository.findAllByOrderByDataPedidoDesc().stream()
                .map(pedido -> montarPedidoResponse(pedido, null))
                .collect(Collectors.toList());
    }


    // BUSCAR POR ID
    public PedidoResponseDto buscarPorId(Integer idPedido) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new PedidoNaoEncontradoException("Pedido não encontrado"));

        List<ItemPedido> itens = itemPedidoRepository.findByPedidoId(idPedido);
        return montarPedidoResponse(pedido, itens);
    }


    // AVANÇAR/VOLTAR ETAPA
    // 1. Valida combinação etapa+status via EtapaPedido enum
    // 2. Se etapa mudou → responsavel = null (aguardando alguém pegar)
    // 3. Atualiza pedido (estado atual)
    // 4. Insere em historico_etapa_pedido (log)
    // 5. Publica evento para notificações (Observer pattern)
    @Transactional
    public PedidoResponseDto avancarEtapa(Integer idPedido, EtapaRequestDto request) {
        // Validar combinação etapa+status
        EtapaPedido etapaEnum = EtapaPedido.fromLabel(request.getEtapa());
        if (!etapaEnum.isStatusValido(request.getStatus())) {
            throw new IllegalArgumentException(
                    "Status '" + request.getStatus() + "' não é válido para etapa '" + request.getEtapa() + "'");
        }

        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new PedidoNaoEncontradoException("Pedido não encontrado"));

        PedidoLogDto valorAnterior = PedidoLogDto.from(pedido);

        // Se a etapa principal mudou → responsável reseta (null)
        boolean etapaMudou = !request.getEtapa().equals(pedido.getEtapaPedido());

        Usuario responsavelEtapa = usuarioRepository.findById(request.getIdResponsavelEtapa())
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Responsável da etapa não encontrado"));

        // Atualizar estado atual do pedido
        pedido.setEtapaPedido(request.getEtapa());
        pedido.setStatus(request.getStatus());

        if (etapaMudou) {
            pedido.setUsuarioResponsavel(null);
        } else {
            pedido.setUsuarioResponsavel(responsavelEtapa);
        }

        pedidoRepository.save(pedido);

        // Registrar no histórico
        HistoricoEtapaPedido historico = new HistoricoEtapaPedido();
        historico.setPedido(pedido);
        historico.setUsuario(pedido.getUsuario());
        historico.setResponsavelEtapa(responsavelEtapa);

        historico.setEtapa(request.getEtapa());
        historico.setStatusEtapa(request.getStatus());
        historico.setDataEntrada(request.getDataEntrada());
        historico.setDataSaida(request.getDataSaida());
        historico.setObservacoes(request.getObservacoes());

        historicoRepository.save(historico);

        // Publicar evento — Observer notifica cliente via WhatsApp/email (somente mudança de etapa)
        if (etapaMudou) {
            eventPublisher.publishEvent(new EtapaAvancadaEvent(pedido, request.getEtapa(), request.getStatus()));
        }

        logSistemaService.registrar(
                responsavelEtapa.getId(), AcaoLog.ATUALIZAR, "Pedido",
                pedido.getId().longValue(), "Etapa do pedido alterada para " + request.getEtapa() + " - " + request.getStatus(),
                valorAnterior, PedidoLogDto.from(pedido));

        return montarPedidoResponse(pedido, null);
    }


    // ATRIBUIR RESPONSÁVEL
    @Transactional
    public PedidoResponseDto atribuirResponsavel(Integer idPedido, Long idResponsavel) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new PedidoNaoEncontradoException("Pedido não encontrado"));

        PedidoLogDto valorAnterior = PedidoLogDto.from(pedido);

        Usuario responsavel = usuarioRepository.findById(idResponsavel)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Funcionário não encontrado"));

        pedido.setUsuarioResponsavel(responsavel);
        pedidoRepository.save(pedido);

        logSistemaService.registrar(
                idResponsavel, AcaoLog.ATUALIZAR, "Pedido",
                pedido.getId().longValue(), "Responsável atribuído ao pedido: " + responsavel.getNome(),
                valorAnterior, PedidoLogDto.from(pedido));

        return montarPedidoResponse(pedido, null);
    }


    // LISTAR HISTÓRICO
    public List<HistoricoEtapaResponseDto> listarHistorico(Integer idPedido) {
        return historicoRepository.findByPedidoIdOrderByDataEntradaAsc(idPedido).stream().map(h -> {
            HistoricoEtapaResponseDto dto = new HistoricoEtapaResponseDto();
            dto.setEtapa(h.getEtapa());
            dto.setStatusEtapa(h.getStatusEtapa());
            dto.setDataEntrada(h.getDataEntrada());
            dto.setDataSaida(h.getDataSaida());
            dto.setObservacoes(h.getObservacoes());
            if (h.getResponsavelEtapa() != null) {
                dto.setNomeResponsavel(h.getResponsavelEtapa().getNome());
            }
            return dto;
        }).collect(Collectors.toList());
    }


    // MEUS PEDIDOS (em andamento — etapa != "Finalizado")
    public List<PedidoResponseDto> listarMeusPedidosEmAndamento(Integer idCliente) {
        return pedidoRepository
                .findByUsuarioClienteIdAndEtapaPedidoNotOrderByDataPedidoDesc(idCliente, EtapaPedido.FINALIZADO.getLabel())
                .stream()
                .map(pedido -> montarPedidoResponse(pedido, null))
                .collect(Collectors.toList());
    }

    // MEUS PEDIDOS (histórico — etapa = "Finalizado")
    public List<PedidoResponseDto> listarMeusPedidosFinalizados(Integer idCliente) {
        return pedidoRepository
                .findByUsuarioClienteIdAndEtapaPedidoOrderByDataFinalizacaoDesc(idCliente, EtapaPedido.FINALIZADO.getLabel())
                .stream()
                .map(pedido -> montarPedidoResponse(pedido, null))
                .collect(Collectors.toList());
    }


    // ATUALIZAR PEDIDO COMPLETO
    @Transactional
    public PedidoResponseDto atualizarPedido(Integer idPedido, PedidoRequestDto request) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new PedidoNaoEncontradoException("Pedido não encontrado"));

        PedidoLogDto valorAnterior = PedidoLogDto.from(pedido);

        // Validar pessoas
        Usuario cliente = usuarioRepository.findById(request.getIdUsuarioCliente())
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Cliente não encontrado"));

        Usuario responsavel = null;
        if (request.getIdUsuarioResponsavel() != null) {
            responsavel = usuarioRepository.findById(request.getIdUsuarioResponsavel())
                    .orElseThrow(() -> new UsuarioNaoEncontradoException("Funcionário não encontrado"));
        }

        Endereco endereco = enderecoRepository.findById(request.getIdEndereco())
                .orElseThrow(() -> new EnderecoNaoEncontradoException("Endereço não encontrado"));

        // Atualizar campos do pedido
        pedido.setNumPedido(request.getNumPedido());
        pedido.setUrlFotoArte(request.getUrlFotoArte());
        pedido.setDescricao(request.getDescricao());
        pedido.setEtapaPedido(request.getEtapaPedido());
        pedido.setStatus(request.getStatus());
        pedido.setValorTotal(request.getValorTotal());
        pedido.setDataPedido(request.getDataPedido());
        pedido.setDataInicio(request.getDataInicio());
        pedido.setDataFinalizacao(request.getDataFinalizacao());
        pedido.setTipoEnvio(request.getTipoEnvio());
        pedido.setEndereco(endereco);
        pedido.setUsuarioCliente(cliente);
        pedido.setUsuarioResponsavel(responsavel);

        pedido = pedidoRepository.save(pedido);

        // Remover itens antigos
        List<ItemPedido> itensAntigos = itemPedidoRepository.findByPedidoId(idPedido);
        for (ItemPedido itemAntigo : itensAntigos) {
            if (itemAntigo.getCaracteristicas() != null) {
                caracteristicasRepository.delete(itemAntigo.getCaracteristicas());
            }
        }
        itemPedidoRepository.deleteAll(itensAntigos);

        // Criar novos itens
        List<ItemPedido> itensSalvos = new ArrayList<>();
        for (ItemPedidoRequestDto itemDto : request.getItens()) {
            ItemPedido item = criarItemPedido(pedido, itemDto);
            itensSalvos.add(item);
        }

        Long autorId = (responsavel != null) ? responsavel.getId() : cliente.getId();

        logSistemaService.registrar(
                autorId, AcaoLog.ATUALIZAR, "Pedido",
                pedido.getId().longValue(), "Pedido atualizado",
                valorAnterior, PedidoLogDto.from(pedido));

        return montarPedidoResponse(pedido, itensSalvos);
    }


    // CANCELAR PEDIDO (soft delete)
    @Transactional
    public PedidoResponseDto cancelarPedido(Integer idPedido, String motivo) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new PedidoNaoEncontradoException("Pedido não encontrado"));

        if ("Cancelado".equals(pedido.getEtapaPedido())) {
            throw new IllegalStateException("Pedido já está cancelado");
        }

        PedidoLogDto valorAnterior = PedidoLogDto.from(pedido);

        // Mudar etapa para "Cancelado"
        pedido.setEtapaPedido("Cancelado");
        pedido.setStatus("Cancelado");

        // Concatenar motivo na descrição
        String descricaoAtual = pedido.getDescricao() != null ? pedido.getDescricao() : "";
        pedido.setDescricao(descricaoAtual + "\n\n[CANCELADO] " + motivo);

        pedidoRepository.save(pedido);

        Long autorId = pedido.getUsuarioCliente() != null ? pedido.getUsuarioCliente().getId() : null;

        logSistemaService.registrar(
                autorId, AcaoLog.ATUALIZAR, "Pedido",
                pedido.getId().longValue(), "Pedido cancelado. Motivo: " + motivo,
                valorAnterior, PedidoLogDto.from(pedido));

        return montarPedidoResponse(pedido, null);
    }


    // MÉTODOS AUXILIARES
    private ItemPedido criarItemPedido(Pedido pedido, ItemPedidoRequestDto dto) {
        Produto produto = produtoRepository.findById(dto.getIdProduto())
                .orElseThrow(() -> new ProdutoNaoEncontradoException("Produto não encontrado"));

        // Salvar características primeiro
        CaracteristicasItemPedido caract = new CaracteristicasItemPedido();
        caract.setDescricaoArte(dto.getCaracteristicas().getDescricaoArte());
        caract.setCorEstampa(dto.getCaracteristicas().getCorEstampa());
        caract.setCorMaterial(dto.getCaracteristicas().getCorMaterial());
        caract.setComposicao(dto.getCaracteristicas().getComposicao());
        caract.setTamanho(dto.getCaracteristicas().getTamanho());
        caract.setFornecedor(dto.getCaracteristicas().getFornecedor());
        caract = caracteristicasRepository.save(caract);

        // Criar item — @ManyToOne resolve as FKs automaticamente
        ItemPedido item = new ItemPedido();
        item.setPedido(pedido);           // JPA grava fk_pedido = pedido.getId()
        item.setProduto(produto);         // JPA grava fk_produto = produto.getId()
        item.setCaracteristicas(caract);  // JPA grava fk_caracteristicas = caract.getId()
        item.setQuantidade(dto.getQuantidade());
        item.setValorUnitario(dto.getValorUnitario());

        return itemPedidoRepository.save(item);
    }

    private PedidoResponseDto montarPedidoResponse(Pedido pedido, List<ItemPedido> itens) {
        PedidoResponseDto response = new PedidoResponseDto();
        response.setIdPedido(pedido.getId());
        response.setNumPedido(pedido.getNumPedido());
        response.setUrlFotoArte(pedido.getUrlFotoArte());
        response.setDescricao(pedido.getDescricao());
        response.setEtapaPedido(pedido.getEtapaPedido());
        response.setStatus(pedido.getStatus());
        response.setValorTotal(pedido.getValorTotal());
        response.setDataPedido(pedido.getDataPedido());
        response.setDataInicio(pedido.getDataInicio());
        response.setDataFinalizacao(pedido.getDataFinalizacao());
        response.setTipoEnvio(pedido.getTipoEnvio());
        response.setNumNotaFiscal(pedido.getNumNotaFiscal());

        if (pedido.getUsuarioCliente() != null) {
            ClienteResumoDto clienteResumo = new ClienteResumoDto();
            clienteResumo.setId(pedido.getUsuarioCliente().getId());
            clienteResumo.setNome(pedido.getUsuarioCliente().getNome());
            clienteResumo.setTelefone(pedido.getUsuarioCliente().getTelefone());
            if (pedido.getUsuarioCliente().getEmpresa() != null) {
                clienteResumo.setNomeEmpresa(pedido.getUsuarioCliente().getEmpresa().getNomeFantasia());
            }
            response.setCliente(clienteResumo);
        }

        if (pedido.getUsuarioResponsavel() != null) {
            FuncionarioResumoDto funcionarioResumo = new FuncionarioResumoDto();
            funcionarioResumo.setId(pedido.getUsuarioResponsavel().getId());
            funcionarioResumo.setNome(pedido.getUsuarioResponsavel().getNome());
            response.setResponsavel(funcionarioResumo);
        }

        if (pedido.getEndereco() != null) {
            response.setEndereco(pedido.getEndereco());
        }

        if (itens != null) {
            response.setItens(itens.stream().map(item -> {
                ItemPedidoResponseDto itemPedido = new ItemPedidoResponseDto();
                itemPedido.setIdProduto(item.getProduto().getId());
                itemPedido.setNomeProduto(item.getProduto().getNome());
                itemPedido.setQuantidade(item.getQuantidade());
                itemPedido.setValorUnitario(item.getValorUnitario());
                if (item.getCaracteristicas() != null) {
                    CaracteristicasRequestDto caracteristica = new CaracteristicasRequestDto();
                    caracteristica.setDescricaoArte(item.getCaracteristicas().getDescricaoArte());
                    caracteristica.setCorEstampa(item.getCaracteristicas().getCorEstampa());
                    caracteristica.setCorMaterial(item.getCaracteristicas().getCorMaterial());
                    caracteristica.setComposicao(item.getCaracteristicas().getComposicao());
                    caracteristica.setTamanho(item.getCaracteristicas().getTamanho());
                    caracteristica.setFornecedor(item.getCaracteristicas().getFornecedor());
                    itemPedido.setCaracteristicas(caracteristica);
                }
                return itemPedido;
            }).collect(Collectors.toList()));
        }

        return response;
    }
}