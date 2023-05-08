package br.senai.TestesUnit.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "veiculos")
public class Veiculo {
    @Id
    @NotBlank(message = "O preenchimento da placa do veículo é obrigatória!")
    String placa;
    @NotBlank(message = "O preenchimento do tipo do veículo é obrigatório!")
    String tipo;
    @NotBlank(message = "O preenchimento da cor do veículo é obrigatória!")
    String cor;
    @NotNull(message = "O preenchimento do ano de fabricação do veículo é obrigatório!")
    @Column(name = "ano_fabricacao")
    Integer anoFabricacao;
    @NotNull(message = "O preenchimento da quantidade de multas do veículo é obrigatória!")
    @Column(name = "qtd_multas")
    Integer qtdMultas;
}