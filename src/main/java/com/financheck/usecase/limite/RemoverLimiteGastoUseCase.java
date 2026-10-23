package com.financheck.usecase.limite;

import com.financheck.domain.repository.LimiteGastoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RemoverLimiteGastoUseCase {

    private final LimiteGastoRepository limiteGastoRepository;

    public void execute(Long id) {
        limiteGastoRepository.remover(id);
    }
}
