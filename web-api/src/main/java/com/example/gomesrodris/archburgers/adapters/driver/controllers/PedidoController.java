package com.example.gomesrodris.archburgers.adapters.driver.controllers;

import com.example.gomesrodris.archburgers.adapters.driven.infra.TransactionManager;
import com.example.gomesrodris.archburgers.adapters.dto.PedidoDto;
import com.example.gomesrodris.archburgers.apiutils.WebUtils;
import com.example.gomesrodris.archburgers.domain.entities.Pedido;
import com.example.gomesrodris.archburgers.domain.services.PedidoServices;
import com.example.gomesrodris.archburgers.domain.utils.StringUtils;
import com.example.gomesrodris.archburgers.domain.valueobjects.StatusPedido;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PedidoController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PedidoController.class);

    private final PedidoServices pedidoServices;
    private final TransactionManager transactionManager;

    @Autowired
    public PedidoController(PedidoServices pedidoServices, TransactionManager transactionManager) {
        this.pedidoServices = pedidoServices;
        this.transactionManager = transactionManager;
    }

    @PostMapping(path = "/pedidos")
    public ResponseEntity<PedidoDto> criarPedido(@RequestBody PedidoServices.CriarPedidoParam param) {

        Pedido pedido;
        try {
            pedido = transactionManager.runInTransaction(() -> pedidoServices.criarPedido(param));
        } catch (IllegalArgumentException iae) {
            return WebUtils.errorResponse(HttpStatus.BAD_REQUEST, iae.getMessage());
        } catch (Exception e) {
            LOGGER.error("Ocorreu um erro ao criar pedido: {}", e, e);
            return WebUtils.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro ao criar pedido");
        }

        return WebUtils.okResponse(PedidoDto.fromEntity(pedido));
    }

    @GetMapping(path = "/pedidos")
    public ResponseEntity<List<PedidoDto>> listarPedidos(
            @RequestParam(value = "status", required = false) String filtroStatus,
            @RequestParam(value = "atraso", required = false) String filtroAtraso) {
        List<Pedido> result;
        try {
            StatusPedido parsedFiltroStatus = StringUtils.isEmpty(filtroStatus)
                    ? null : StatusPedido.valueOf(filtroStatus);

            boolean isFiltroAtraso = Boolean.parseBoolean(filtroAtraso);

            if (isFiltroAtraso) {
                result = pedidoServices.listarPedidosComAtraso();
            } else {
                result = pedidoServices.listarPedidosByStatus(parsedFiltroStatus);
            }

        } catch (IllegalArgumentException iae) {
            return WebUtils.errorResponse(HttpStatus.BAD_REQUEST, iae.getMessage());
        } catch (Exception e) {
            LOGGER.error("Ocorreu um erro ao listar pedidos: {}", e, e);
            return WebUtils.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro ao listar pedidos");
        }

        return WebUtils.okResponse(result.stream().map(PedidoDto::fromEntity).toList());
    }

    @PostMapping(path = "/pedidos/{idPedido}/validar")
    public ResponseEntity<PedidoDto> validarPedido(@PathVariable("idPedido") Integer idPedido) {
        Pedido pedido;
        try {
            pedido = transactionManager.runInTransaction(() -> pedidoServices.validarPedido(idPedido));
        } catch (IllegalArgumentException iae) {
            return WebUtils.errorResponse(HttpStatus.BAD_REQUEST, iae.getMessage());
        } catch (Exception e) {
            LOGGER.error("Ocorreu um erro ao atualizar pedido: {}", e, e);
            return WebUtils.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro ao atualizar pedido");
        }

        return WebUtils.okResponse(PedidoDto.fromEntity(pedido));
    }

    @PostMapping(path = "/pedidos/{idPedido}/cancelar")
    public ResponseEntity<PedidoDto> cancelarPedido(@PathVariable("idPedido") Integer idPedido) {
        Pedido pedido;
        try {
            pedido = transactionManager.runInTransaction(() -> pedidoServices.cancelarPedido(idPedido));
        } catch (IllegalArgumentException iae) {
            return WebUtils.errorResponse(HttpStatus.BAD_REQUEST, iae.getMessage());
        } catch (Exception e) {
            LOGGER.error("Ocorreu um erro ao atualizar pedido: {}", e, e);
            return WebUtils.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro ao atualizar pedido");
        }

        return WebUtils.okResponse(PedidoDto.fromEntity(pedido));
    }

    @PostMapping(path = "/pedidos/{idPedido}/setPronto")
    public ResponseEntity<PedidoDto> setPedidoPronto(@PathVariable("idPedido") Integer idPedido) {
        Pedido pedido;
        try {
            pedido = transactionManager.runInTransaction(() -> pedidoServices.setPronto(idPedido));
        } catch (IllegalArgumentException iae) {
            return WebUtils.errorResponse(HttpStatus.BAD_REQUEST, iae.getMessage());
        } catch (Exception e) {
            LOGGER.error("Ocorreu um erro ao atualizar pedido: {}", e, e);
            return WebUtils.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro ao atualizar pedido");
        }

        return WebUtils.okResponse(PedidoDto.fromEntity(pedido));
    }

    @PostMapping(path = "/pedidos/{idPedido}/finalizar")
    public ResponseEntity<PedidoDto> finalizarPedido(@PathVariable("idPedido") Integer idPedido) {
        Pedido pedido;
        try {
            pedido = transactionManager.runInTransaction(() -> pedidoServices.finalizarPedido(idPedido));
        } catch (IllegalArgumentException iae) {
            return WebUtils.errorResponse(HttpStatus.BAD_REQUEST, iae.getMessage());
        } catch (Exception e) {
            LOGGER.error("Ocorreu um erro ao atualizar pedido: {}", e, e);
            return WebUtils.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro ao atualizar pedido");
        }

        return WebUtils.okResponse(PedidoDto.fromEntity(pedido));
    }
}
