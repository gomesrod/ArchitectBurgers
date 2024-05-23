package com.example.gomesrodris.archburgers.domain.serviceports;

import com.example.gomesrodris.archburgers.domain.entities.ItemCardapio;
import com.example.gomesrodris.archburgers.domain.valueobjects.TipoItemCardapio;

import java.util.List;

public interface CardapioServicesPort {

    List<ItemCardapio> listarItensCardapio(TipoItemCardapio filtroTipo);
}