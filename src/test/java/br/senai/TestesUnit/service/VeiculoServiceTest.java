package br.senai.TestesUnit.service;

import br.senai.TestesUnit.model.Veiculo;
import br.senai.TestesUnit.records.MultaRecord;
import br.senai.TestesUnit.repository.VeiculoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class VeiculoServiceTest {

    @Mock
    private VeiculoRepository repository;

    @InjectMocks
    private VeiculoService service;

    @Test
    @DisplayName("Quando existe veículo com a placa informada, deve retornar o veículo")
    void listaVeiculoPelaPlaca_encontrado() {

        // Given
        String placa = "HHH-1111";

        Veiculo veiculo = new Veiculo();

        veiculo.setPlaca(placa);

        Mockito.when(repository.findById(Mockito.anyString()))
                .thenReturn(Optional.of(veiculo));

        // When
        Veiculo resultado = service.listaVeiculoPelaPlaca(placa);

        // Then
        assertNotNull(resultado);
        assertEquals(placa, resultado.getPlaca());
    }

    @Test
    @DisplayName("Quando não existe veículo com a placa informada, deve lançar uma exceção")
    void listaVeiculoPelaPlaca_naoEncontrado() {

        assertThrows(EntityNotFoundException.class, () -> service.listaVeiculoPelaPlaca("HHH-1111"));
    }

    @Test
    @DisplayName("Quando existem registros de veículos, deve retornar uma lista de veículos")
    void listaVeiculos_comRegistros() {

        // Given
        List<Veiculo> veiculos = List.of(
                new Veiculo("HHHH-1111", "Carro", "Preto", 2023, 1),
                new Veiculo("AAAA-2222", "Caminhão", "Cinza", 2022, 5)
        );

        Mockito.when(repository.findAll()).thenReturn(veiculos);

        // When
        List<Veiculo> lista = service.listaVeiculos();

        // Then
        assertNotNull(lista);
        assertFalse(lista.isEmpty());
        assertEquals(veiculos.size(), lista.size());
    }

    @Test
    @DisplayName("Quando não existem registros de veículos, deve retornar uma lista vazia")
    void listaVeiculos_semRegistros() {

        List<Veiculo> lista = service.listaVeiculos();

        assertNotNull(lista);
        assertTrue(lista.isEmpty());
    }

    @Test
    @DisplayName("Quando ocorrer uma tentativa de cadastrar um veículo sem placa duplicada, deve cadastrar o veículo")
    void cadastraVeiculo_naoDuplicado() {

        Veiculo veiculo = new Veiculo("AAAA-2222", "Carro", "Preto", 2023, 1);

        Mockito.when(repository.save(Mockito.any(Veiculo.class)))
                .thenReturn(veiculo);

        Veiculo resultado = service.cadastraVeiculo(veiculo);

        assertNotNull(resultado);
        assertEquals(veiculo.getPlaca(), resultado.getPlaca());
    }

    @Test
    @DisplayName("Quando ocorrer uma tentativa de cadastrar um veículo com placa duplicada, deve lançar uma exceção")
    void cadastraVeiculo_placaDuplicada() {

        Veiculo veiculo = new Veiculo("HHHH-1111", "Carro", "Preto", 2023, 1);

        Mockito.when(repository.findById(Mockito.anyString()))
                .thenReturn(Optional.of(veiculo));

        assertThrows(RuntimeException.class, () -> service.cadastraVeiculo(veiculo));
    }

    @Test
    @DisplayName("Quando tenta deletar um veículo não existente, deve lançar uma exceção")
    void deletaVeiculoPelaPlaca_naoEncontrado() {

        assertThrows(EntityNotFoundException.class, () -> service.deletaVeiculoPelaPlaca("HHH-1111"));
    }

    @Test
    @DisplayName("Quando tenta deletar um veículo que possui multas, deve lançar uma exceção")
    void deletaVeiculoPelaPlaca_possuiMultas() {
        Veiculo veiculo = new Veiculo("HHHH-1111", "Carro", "Preto", 2023, 1);

        Mockito.when(repository.findById(Mockito.anyString()))
                .thenReturn(Optional.of(veiculo));

        assertThrows(RuntimeException.class, () -> service.deletaVeiculoPelaPlaca("HHH-1111"));
    }

    @Test
    @DisplayName("Quando tenta deletar um veículo que não possui multas, deve deletar o veículo")
    void deletaVeiculoPelaPlaca_naoPossuiMultas() {
        Veiculo veiculo = new Veiculo("HHHH-1111", "Carro", "Preto", 2023, 0);

        Mockito.when(repository.findById(Mockito.anyString()))
                .thenReturn(Optional.of(veiculo));

        assertDoesNotThrow(() -> service.deletaVeiculoPelaPlaca("HHH-1111"));
    }

    @Test
    @DisplayName("Deve adicionar multa a um veículo existente")
    void adicionaMulta_existente() {
        Veiculo veiculo = new Veiculo("HHHH-1111", "Carro", "Preto", 2023, 0);

        Mockito.when(repository.findById(Mockito.anyString()))
                .thenReturn(Optional.of(veiculo));

        assertDoesNotThrow(() -> service.adicionaMulta("HHHH-1111", new MultaRecord(5)));
    }

    @Test
    @DisplayName("Quando tenta adicionar multa a um veículo não existente, deve lançar uma exceção")
    void adicionaMulta_naoEncontrado() {

        assertThrows(EntityNotFoundException.class, () -> service.adicionaMulta("HHH-1111", new MultaRecord(5)));
    }

}