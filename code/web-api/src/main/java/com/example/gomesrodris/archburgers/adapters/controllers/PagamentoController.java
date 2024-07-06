package com.example.gomesrodris.archburgers.adapters.controllers;

import com.example.gomesrodris.archburgers.adapters.dbgateways.TransactionManager;
import com.example.gomesrodris.archburgers.adapters.dto.FormaPagamentoDto;
import com.example.gomesrodris.archburgers.adapters.dto.PedidoDto;
import com.example.gomesrodris.archburgers.apiutils.WebUtils;
import com.example.gomesrodris.archburgers.domain.entities.Pedido;
import com.example.gomesrodris.archburgers.domain.exception.DomainArgumentException;
import com.example.gomesrodris.archburgers.domain.usecaseports.PagamentoUseCasesPort;
import com.example.gomesrodris.archburgers.domain.valueobjects.FormaPagamento;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class PagamentoController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PagamentoController.class);

    private final PagamentoUseCasesPort pagamentoUseCases;
    private final TransactionManager transactionManager;

    public PagamentoController(PagamentoUseCasesPort pagamentoUseCases, TransactionManager transactionManager) {
        this.pagamentoUseCases = pagamentoUseCases;
        this.transactionManager = transactionManager;
    }

    // Refactor in progress , servicos de pagamento serao refeitos
//    @Operation(summary = "Lista opcoes de pagamento disponiveis")
//    @GetMapping("/pagamento/opcoes")
//    public List<FormaPagamentoDto> listFormasPagamento() {
//        return Arrays.stream(FormaPagamento.values()).map(
//                        formaPagamento -> new FormaPagamentoDto(formaPagamento.name(), formaPagamento.getDescricao()))
//                .toList();
//    }

    // Refactor in progress , servicos de pagamento serao refeitos
//    @Operation(summary = "Grava confirmação de pagamento para o pedido, movendo o mesmo para status RECEBIDO")
//    @PostMapping("/pagamento/confirmacao")
//    public ResponseEntity<PedidoDto> confirmacaoPagamento(@RequestBody ConfirmacaoPagamentoDto param) {
//        Pedido pedidoUpdated;
//        try {
//            if (param == null)
//                throw new DomainArgumentException("Request obj deve ser informado");
//
//            pedidoUpdated = transactionManager.runInTransaction(() -> pagamentoUseCases.confirmarPagamento(
//                    param.validarIdPedido(), param.validarFormaPagamento(), param.infoAdicional()
//            ));
//        } catch (DomainArgumentException ae) {
//            return WebUtils.errorResponse(HttpStatus.BAD_REQUEST, ae.getMessage());
//        } catch (Exception e) {
//            LOGGER.error("Ocorreu um erro ao salvar confirmacao de pagamento: {}", e, e);
//            return WebUtils.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro ao salvar confirmacao de pagamento");
//        }
//
//        return WebUtils.okResponse(PedidoDto.fromEntity(pedidoUpdated));
//    }
}
