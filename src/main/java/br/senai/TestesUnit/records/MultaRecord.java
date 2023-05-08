package br.senai.TestesUnit.records;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;

public record MultaRecord(
        @NotNull(message = "O preenchimento da quantidade de multas é obrigatória!")
        @Column(name = "qtd_multas")
        Integer qtdMultas
) {
}
