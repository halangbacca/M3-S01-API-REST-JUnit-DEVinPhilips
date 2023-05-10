package br.senai.TestesUnit.service;

import br.senai.TestesUnit.model.Veiculo;
import br.senai.TestesUnit.records.MultaRecord;
import br.senai.TestesUnit.repository.VeiculoRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;

@Service
public class VeiculoService {

    @Autowired
    private VeiculoRepository repository;

    public Veiculo cadastraVeiculo(Veiculo novoVeiculo) {
        Optional<Veiculo> veiculoAntigo = repository.findById(novoVeiculo.getPlaca());
        if (veiculoAntigo.isPresent()) {
            throw new EntityExistsException("Já existe um veículo cadastrado com essa placa!");
        }
        return repository.save(novoVeiculo);
    }

    public List<Veiculo> listaVeiculos() {
        return repository.findAll();
    }

    public Veiculo listaVeiculoPelaPlaca(String placa) {
        return repository.findById(placa).orElseThrow(EntityNotFoundException::new);
    }

    public void deletaVeiculoPelaPlaca(String placa) {
        Veiculo veiculo = repository.findById(placa).orElseThrow(EntityNotFoundException::new);
        if (veiculo.getQtdMultas() > 0) {
            throw new EntityExistsException("O veículo não pode ser excluído pois possui multas cadastradas!");
        }
        repository.deleteById(placa);
    }

    public ResponseEntity<Veiculo> adicionaMulta(String placa, MultaRecord multa) {
        Veiculo veiculo = repository.findById(placa).orElseThrow(EntityNotFoundException::new);
        veiculo.setQtdMultas(multa.qtdMultas());
        return ResponseEntity.ok(repository.save(veiculo));
    }

}
