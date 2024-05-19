package com.example.gomesrodris.archburgers.domain.services;

import com.example.gomesrodris.archburgers.domain.entities.Pedido;
import com.example.gomesrodris.archburgers.domain.repositories.CarrinhoRepository;
import com.example.gomesrodris.archburgers.domain.repositories.ItemCardapioRepository;
import com.example.gomesrodris.archburgers.domain.repositories.PedidoRepository;
import com.example.gomesrodris.archburgers.domain.utils.Clock;
import com.example.gomesrodris.archburgers.domain.utils.StringUtils;
import com.example.gomesrodris.archburgers.domain.valueobjects.FormaPagamento;
import com.example.gomesrodris.archburgers.domain.valueobjects.InfoPagamento;
import com.example.gomesrodris.archburgers.domain.valueobjects.StatusPedido;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class PedidoServices {
    private final PedidoRepository pedidoRepository;
    private final CarrinhoRepository carrinhoRepository;
    private final ItemCardapioRepository itemCardapioRepository;
    private final Clock clock;

    public PedidoServices(PedidoRepository pedidoRepository, CarrinhoRepository carrinhoRepository, ItemCardapioRepository itemCardapioRepository, Clock clock) {
        this.pedidoRepository = pedidoRepository;
        this.carrinhoRepository = carrinhoRepository;
        this.itemCardapioRepository = itemCardapioRepository;
        this.clock = clock;
    }

    public Pedido criarPedido(CriarPedidoParam param) {
        if (param == null)
            throw new IllegalArgumentException("Parameter missing");
        if (param.idCarrinho == null)
            throw new IllegalArgumentException("idCarrinho missing");
        if (StringUtils.isEmpty(param.formaPagamento))
            throw new IllegalArgumentException("formaPagamento missing");

        var formaPagamento = FormaPagamento.fromName(param.formaPagamento);

        var carrinho = carrinhoRepository.getCarrinho(param.idCarrinho);
        if (carrinho == null) {
            throw new IllegalArgumentException("Invalid idCarrinho " + param.idCarrinho);
        }

        var itens = itemCardapioRepository.findByCarrinho(param.idCarrinho);

        var pedido = new Pedido(null,
                carrinho.idClienteIdentificado(), carrinho.nomeClienteNaoIdentificado(),
                itens, carrinho.observacoes(),
                StatusPedido.RECEBIDO, new InfoPagamento(formaPagamento),
                clock.localDateTime());

        Pedido saved = pedidoRepository.savePedido(pedido);

        carrinhoRepository.deleteCarrinho(carrinho);

        return saved;
    }

    public List<Pedido> listarPedidos(@Nullable StatusPedido filtroStatus) {
        if (filtroStatus == null) {
            throw new IllegalArgumentException("Obrigatório informar um filtro");
        }

        var pedidos = pedidoRepository.listPedidos(filtroStatus);
        return pedidos.stream().map(p -> {
            var itens = itemCardapioRepository.findByPedido(Objects.requireNonNull(p.id(), "Expected pedidos to have ID"));
            return p.withItens(itens);
        }).toList();
    }

    public record CriarPedidoParam(
            @Nullable Integer idCarrinho,
            @Nullable String formaPagamento
    ) {

    }
}