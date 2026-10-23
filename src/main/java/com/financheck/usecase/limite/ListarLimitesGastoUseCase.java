package com.financheck.usecase.limite;

import com.financheck.domain.repository.LimiteGastoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListarLimitesGastoUseCase {

    private final LimiteGastoRepository limiteGastoRepository;
    private final CalcularGastoLimiteUseCase calcularGastoLimiteUseCase;

    public List<LimiteGastoResumo> execute(Long perfilId) {
        return limiteGastoRepository.buscarPorPerfil(perfilId).stream()
                .map(calcularGastoLimiteUseCase::execute)
                .toList();
    }
}
