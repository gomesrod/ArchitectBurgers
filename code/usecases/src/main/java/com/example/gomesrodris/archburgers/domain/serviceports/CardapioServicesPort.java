package com.example.gomesrodris.archburgers.domain.serviceports;

import com.example.gomesrodris.archburgers.domain.entities.ItemCardapio;
import com.example.gomesrodris.archburgers.domain.valueobjects.TipoItemCardapio;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface CardapioServicesPort {

    List<ItemCardapio> listarItensCardapio(@Nullable TipoItemCardapio filtroTipo);

    ItemCardapio salvarItemCardapio(@NotNull ItemCardapio itemCardapio);

    void excluirItemCardapio(int idItemCardapio);
}