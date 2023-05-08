package br.senai.TestesUnit.model;

import jakarta.persistence.*;
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
    private String placa;
    @NotBlank(message = "O preenchimento do tipo do veículo é obrigatório!")
    private String tipo;
    @NotBlank(message = "O preenchimento da cor do veículo é obrigatória!")
    private String cor;
    @NotNull(message = "O preenchimento do ano de fabricação do veículo é obrigatório!")
    @Column(name = "ano_fabricacao")
    private Integer anoFabricacao;
    @NotNull(message = "O preenchimento da quantidade de multas do veículo é obrigatória!")
    @Column(name = "qtd_multas")
    private Integer qtdMultas;
}