package br.senai.TestesUnit.controller;

import br.senai.TestesUnit.model.Veiculo;
import br.senai.TestesUnit.records.MultaRecord;
import br.senai.TestesUnit.service.VeiculoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest
class VeiculoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VeiculoService service;

    @Test
    @DisplayName("Quando não há veículos cadastrados, deve retornar uma lista vazia")
    void listaVeiculos_vazio() throws Exception {
        mockMvc.perform(get("/api/veiculos")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk()) // 200
                .andExpect(jsonPath("$", is(empty())));
    }

    @Test
    @DisplayName("Quando há veículos cadastrados, deve retornar uma lista contendo os veículos")
    void listaVeiculos() throws Exception {
        var veiculos = List.of(
                new Veiculo("HHHH-1111", "Carro", "Preto", 2023, 1),
                new Veiculo("AAAA-2222", "Caminhão", "Cinza", 2022, 5)
        );
        Mockito.when(service.listaVeiculos()).thenReturn(veiculos);
        mockMvc.perform(get("/api/veiculos")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk()) // 200
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].placa", is(veiculos.get(0).getPlaca())))
                .andExpect(jsonPath("$[1].placa", is(veiculos.get(1).getPlaca())));
    }

    @Test
    @DisplayName("Quando não há veículo cadastrado com a placa informada, deve lançar uma exceção")
    void listaVeiculoPorPlaca_naoEncontrado() throws Exception {
        Mockito.when(service.listaVeiculoPelaPlaca(Mockito.anyString())).thenThrow(EntityNotFoundException.class);
        mockMvc.perform(get("/api/veiculos/{placa}", "HHHH-1111")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound()); // 404
    }

    @Test
    @DisplayName("Quando há veículos cadastrados com a placa informada, deve retornar o veículo cadastrado")
    void listaVeiculoPorPlaca() throws Exception {
        Veiculo veiculo = new Veiculo("HHHH-1111", "Carro", "Preto", 2023, 1);
        Mockito.when(service.listaVeiculoPelaPlaca(Mockito.anyString())).thenReturn(veiculo);
        mockMvc.perform(get("/api/veiculos/{placa}", "HHHH-1111")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk()) // 200
                .andExpect(jsonPath("$.placa", is(veiculo.getPlaca())))
                .andExpect(jsonPath("$.tipo", is(veiculo.getTipo())));
    }

    @Test
    @DisplayName("Quando cadastrar um veículo com dados inválidos, deve retornar erros")
    void cadastrar_invalido() throws Exception {
        Veiculo request = new Veiculo();
        String requestJson = objectMapper.writeValueAsString(request); // Transforma o model/DTO em JSON
        mockMvc.perform(post("/api/veiculos")
                        .content(requestJson)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest()); // 400
    }

    @Test
    @DisplayName("Quando cadastrar um veículo com placa duplicada, deve lançar uma exceção")
    void cadastrar_placaDuplicada() throws Exception {
        Mockito.when(service.cadastraVeiculo(Mockito.any(Veiculo.class))).thenThrow(EntityExistsException.class);
        Veiculo request = new Veiculo("HHHH-1111", "Carro", "Preto", 2023, 1);
        String requestJson = objectMapper.writeValueAsString(request); // Transforma o model/DTO em JSON
        mockMvc.perform(post("/api/veiculos")
                        .content(requestJson)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isConflict()); // 409
    }

    @Test
    @DisplayName("Quando cadastrar um veículo com os dados corretos, deve salvar o veículo na base de dados")
    void cadastrar() throws Exception {
        Veiculo request = new Veiculo("HHHH-1111", "Carro", "Preto", 2023, 1);
        Mockito.when(service.cadastraVeiculo(Mockito.any(Veiculo.class))).thenReturn(request);
        String requestJson = objectMapper.writeValueAsString(request); // Transforma o model/DTO em JSON
        mockMvc.perform(post("/api/veiculos")
                        .content(requestJson)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated()); // 201
    }

    @Test
    @DisplayName("Quando tentar excluir veículo com placa não cadastrada, deve lançar exceção")
    void excluir_naoEncontrado() throws Exception {
        Mockito.doThrow(EntityNotFoundException.class).when(service).deletaVeiculoPelaPlaca(Mockito.anyString());
        mockMvc.perform(delete("/api/veiculos/{placa}", "ABC123")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound()); // 404
    }

    @Test
    @DisplayName("Quando tentar excluir veículo que possui multa, deve lançar exceção")
    void excluir_temMulta() throws Exception {
        Mockito.doThrow(EntityExistsException.class).when(service).deletaVeiculoPelaPlaca(Mockito.anyString());
        mockMvc.perform(delete("/api/veiculos/{placa}", "ABC123")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isConflict()); // 409
    }

    @Test
    @DisplayName("Quando o veículo não possuir multas, deve excluir")
    void excluir() throws Exception {
        mockMvc.perform(delete("/api/veiculos/{placa}", "ABC123")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNoContent()); // 204
    }

    @Test
    @DisplayName("Quando tentar inserir uma multa em um veículo com placa não cadastrada, deve lançar exceção")
    void multar_naoEncontrado() throws Exception {
        MultaRecord request = new MultaRecord(2);
        String requestJson = objectMapper.writeValueAsString(request); // Transforma o record em JSON
        Mockito.when(service.adicionaMulta(Mockito.anyString(), Mockito.any(MultaRecord.class))).thenThrow(EntityNotFoundException.class);
        mockMvc.perform(put("/api/veiculos/{placa}/multas", "ABC123")
                        .content(requestJson)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound()); // 404
    }

    @Test
    @DisplayName("Quando tentar inserir uma multa em um veículo com placa cadastrada, deve retornar sucesso")
    void multar() throws Exception {
        Veiculo veiculo = new Veiculo("HHHH-1111", "Carro", "Preto", 2023, 1);
        MultaRecord request = new MultaRecord(2);
        veiculo.setQtdMultas(2);
        final int qtdMultasOriginal = veiculo.getQtdMultas();
        String requestJson = objectMapper.writeValueAsString(request); // Transforma o record em JSON
        veiculo.setQtdMultas(veiculo.getQtdMultas() + 1);
        Mockito.when(service.adicionaMulta(Mockito.anyString(), Mockito.any(MultaRecord.class))).thenReturn(ResponseEntity.ok(veiculo));
        mockMvc.perform(put("/api/veiculos/{placa}/multas", veiculo.getPlaca())
                        .content(requestJson)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.placa", is(notNullValue())))
                .andExpect(jsonPath("$.qtdMultas", is(qtdMultasOriginal + 1)));
    }
}