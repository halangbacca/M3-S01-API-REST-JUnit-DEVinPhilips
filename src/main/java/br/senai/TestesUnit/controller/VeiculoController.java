package br.senai.TestesUnit.controller;

import br.senai.TestesUnit.model.Veiculo;
import br.senai.TestesUnit.records.MultaRecord;
import br.senai.TestesUnit.service.VeiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/veiculos")
public class VeiculoController {

    @Autowired
    private VeiculoService service;

    @PostMapping
    public ResponseEntity<Veiculo> cadastraVeiculo(@RequestBody @Validated Veiculo novoVeiculo, UriComponentsBuilder builder) {
        Veiculo veiculo = service.cadastraVeiculo(novoVeiculo);

        URI uri = builder.path("/api/veiculos/{placa}").buildAndExpand(veiculo.getPlaca()).toUri();

        return ResponseEntity.created(uri).body(veiculo);
    }

    @GetMapping
    public List<Veiculo> listaVeiculos() {
        return service.listaVeiculos();
    }

    @GetMapping("/{placa}")
    public Veiculo listaVeiculoPelaPlaca(@PathVariable String placa) {
        return service.listaVeiculoPelaPlaca(placa);
    }

    @DeleteMapping("/{placa}")
    public ResponseEntity<Void> deletaVeiculoPelaPlaca(@PathVariable String placa) {
        service.deletaVeiculoPelaPlaca(placa);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{placa}/multas")
    public ResponseEntity<Veiculo> adicionaMulta(@PathVariable String placa, @RequestBody @Validated MultaRecord multa) {
        return service.adicionaMulta(placa, multa);
    }

}