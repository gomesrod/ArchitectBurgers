package com.example.gomesrodris.archburgers.domain.usecases;

import com.example.gomesrodris.archburgers.domain.entities.ItemCardapio;
import com.example.gomesrodris.archburgers.domain.entities.ItemPedido;
import com.example.gomesrodris.archburgers.domain.entities.Pagamento;
import com.example.gomesrodris.archburgers.domain.entities.Pedido;
import com.example.gomesrodris.archburgers.domain.exception.DomainArgumentException;
import com.example.gomesrodris.archburgers.domain.external.FormaPagamento;
import com.example.gomesrodris.archburgers.domain.external.FormaPagamentoRegistry;
import com.example.gomesrodris.archburgers.domain.repositories.ItemCardapioRepository;
import com.example.gomesrodris.archburgers.domain.repositories.PagamentoRepository;
import com.example.gomesrodris.archburgers.domain.repositories.PedidoRepository;
import com.example.gomesrodris.archburgers.domain.usecaseports.PagamentoUseCasesPort;
import com.example.gomesrodris.archburgers.domain.utils.Clock;
import com.example.gomesrodris.archburgers.domain.valueobjects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class PagamentoUseCasesTest {
    private PagamentoRepository pagamentoRepository;
    private PedidoRepository pedidoRepository;
    private ItemCardapioRepository itemCardapioRepository;
    private Clock clock;

    private FormaPagamento pagamentoExternoMock;

    private PagamentoUseCases pagamentoUseCases;

    @BeforeEach
    void setUp() {
        pagamentoRepository = mock(PagamentoRepository.class);
        pedidoRepository = mock(PedidoRepository.class);
        itemCardapioRepository = mock(ItemCardapioRepository.class);
        clock = mock(Clock.class);

        // Using real FormaPagamentoRegistry, with mocked external impl
        pagamentoExternoMock = mock(FormaPagamento.class);
        when(pagamentoExternoMock.id()).thenReturn(new IdFormaPagamento("OnlinePay"));
        when(pagamentoExternoMock.isIntegracaoExterna()).thenReturn(true);
        when(pagamentoExternoMock.descricao()).thenReturn("Pagamento Online empresa externa");

        FormaPagamentoRegistry formaPagamentoRegistry = new FormaPagamentoRegistry(List.of(
                pagamentoExternoMock
        ));

        pagamentoUseCases = new PagamentoUseCases(formaPagamentoRegistry,
                pagamentoRepository, pedidoRepository, itemCardapioRepository, clock);
    }

    @Test
    void validarFormaPagamento() {
        assertThat(pagamentoUseCases.validarFormaPagamento("OnlinePay")).isEqualTo(
                new IdFormaPagamento("OnlinePay")
        );
    }

    @Test
    void validarFormaPagamento_desconhecido() {
        var e = assertThrows(DomainArgumentException.class, () ->
                pagamentoUseCases.validarFormaPagamento("UnknownPay"));
        assertThat(e).hasMessage("Forma de pagamento desconhecida: UnknownPay");
    }

    @Test
    void iniciarPagamento_formaPagamentoInterna() {
        var pedido = Pedido.novoPedido(new IdCliente(25), null, List.of(
                        new ItemPedido(1,
                                new ItemCardapio(1000, TipoItemCardapio.LANCHE, "Hamburger", "Hamburger", new ValorMonetario("25.90"))
                        ),
                        new ItemPedido(2,
                                new ItemCardapio(1001, TipoItemCardapio.BEBIDA, "Refrigerante", "Refrigerante", new ValorMonetario("5.00"))
                        )
                ), "Lanche sem cebola", IdFormaPagamento.DINHEIRO, dateTimePedido)
                .withId(33);

        when(clock.localDateTime()).thenReturn(dateTimePagamentoInicio);

        var expectedPagamento = Pagamento.registroInicial(
                33, IdFormaPagamento.DINHEIRO,
                new ValorMonetario("30.90"),
                dateTimePagamentoInicio,
                null, null
        );

        when(pagamentoRepository.salvarPagamento(expectedPagamento)).thenReturn(
                expectedPagamento.withId(22));

        var pagamento = pagamentoUseCases.iniciarPagamento(pedido);

        assertThat(pagamento).isEqualTo(expectedPagamento.withId(22));
    }

    @Test
    void iniciarPagamento_formaPagamentoExterna() {
        var pedido = Pedido.novoPedido(new IdCliente(25), null, List.of(
                        new ItemPedido(1,
                                new ItemCardapio(1000, TipoItemCardapio.LANCHE, "Hamburger", "Hamburger", new ValorMonetario("25.90"))
                        ),
                        new ItemPedido(2,
                                new ItemCardapio(1001, TipoItemCardapio.BEBIDA, "Refrigerante", "Refrigerante", new ValorMonetario("5.00"))
                        )
                ), "Lanche sem cebola", new IdFormaPagamento("OnlinePay"), dateTimePedido)
                .withId(33);

        when(clock.localDateTime()).thenReturn(dateTimePagamentoInicio);

        when(pagamentoExternoMock.iniciarRegistroPagamento(pedido)).thenReturn(new FormaPagamento.InfoPagamentoExterno(
                "abc-def-ghi-jkl", "987654321"
        ));

        var expectedPagamento = Pagamento.registroInicial(
                33, new IdFormaPagamento("OnlinePay"),
                new ValorMonetario("30.90"),
                dateTimePagamentoInicio,
                "abc-def-ghi-jkl", "987654321"
        );

        when(pagamentoRepository.salvarPagamento(expectedPagamento)).thenReturn(
                expectedPagamento.withId(22));

        var pagamento = pagamentoUseCases.iniciarPagamento(pedido);

        assertThat(pagamento).isEqualTo(expectedPagamento.withId(22));
    }

    @Test
    void finalizarPagamento_notFound() {
        when(pagamentoRepository.findPagamentoByPedido(33)).thenReturn(null);

        var e = assertThrows(DomainArgumentException.class,
                () -> pagamentoUseCases.finalizarPagamento(33)
        );
        assertThat(e).hasMessageContaining("Pedido invalido=33");
    }

    @Test
    void finalizarPagamento() {
        when(pedidoRepository.getPedido(33)).thenReturn(Pedido.pedidoRecuperado(33, new IdCliente(25), null,
                List.of(), "", StatusPedido.PAGAMENTO,
                IdFormaPagamento.DINHEIRO, dateTimePedido));
        when(itemCardapioRepository.findByPedido(33)).thenReturn(List.of(
                new ItemPedido(1, new ItemCardapio(9, TipoItemCardapio.LANCHE,
                        "Hamburger Duplo", "", new ValorMonetario("30.90")))
        ));

        Pagamento savedPagamento = new Pagamento(44, 33, IdFormaPagamento.DINHEIRO,
                StatusPagamento.PENDENTE, new ValorMonetario("30.90"),
                dateTimePagamentoInicio, dateTimePagamentoInicio,
                null, null);

        when(pagamentoRepository.findPagamentoByPedido(33)).thenReturn(savedPagamento);

        when(clock.localDateTime()).thenReturn(dateTimePagamentoFinal);
        Pagamento expectedPagamentoFinalizado = savedPagamento.finalizar(dateTimePagamentoFinal);

        //
        var result = pagamentoUseCases.finalizarPagamento(33);

        var expectedPedidoPago = Pedido.pedidoRecuperado(33, new IdCliente(25), null,
                List.of(
                        new ItemPedido(1, new ItemCardapio(9, TipoItemCardapio.LANCHE,
                                "Hamburger Duplo", "", new ValorMonetario("30.90")))
                ), "", StatusPedido.RECEBIDO,
                IdFormaPagamento.DINHEIRO, dateTimePedido);

        assertThat(result).isEqualTo(expectedPedidoPago);

        verify(pagamentoRepository).updateStatus(expectedPagamentoFinalizado);
        verify(pedidoRepository).updateStatus(expectedPedidoPago);
    }

    @Test
    void listarFormasPagamento() {
        var list = pagamentoUseCases.listarFormasPagamento();
        assertThat(list).containsExactlyInAnyOrder(
                new PagamentoUseCasesPort.DescricaoFormaPagamento(IdFormaPagamento.DINHEIRO,
                        "Pagamento em dinheiro direto ao caixa"),
                new PagamentoUseCasesPort.DescricaoFormaPagamento(IdFormaPagamento.CARTAO_MAQUINA,
                        "Pagamento na máquina da loja"),
                new PagamentoUseCasesPort.DescricaoFormaPagamento(new IdFormaPagamento("OnlinePay"),
                        "Pagamento Online empresa externa")
        );
    }

    private final LocalDateTime dateTimePedido = LocalDateTime.of(2024, 5, 18, 15, 30);
    private final LocalDateTime dateTimePagamentoInicio = LocalDateTime.of(2024, 5, 18, 15, 31);
    private final LocalDateTime dateTimePagamentoFinal = LocalDateTime.of(2024, 5, 18, 15, 32);
}