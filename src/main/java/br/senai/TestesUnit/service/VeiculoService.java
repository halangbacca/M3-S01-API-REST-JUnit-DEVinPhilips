package br.senai.TestesUnit.service;

import br.senai.TestesUnit.model.Veiculo;
import br.senai.TestesUnit.record.MultaRecord;
import br.senai.TestesUnit.repository.VeiculoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VeiculoService {

    @Autowired
    private VeiculoRepository repository;

    public Veiculo cadastraVeiculo(Veiculo veiculo) {
        repository.save(veiculo);
        return veiculo;
    }

    public List<Veiculo> listaVeiculos() {
        return repository.findAll();
    }

    public Veiculo listaVeiculoPelaPlaca(String placa) {
        return repository.findById(placa).orElseThrow(EntityNotFoundException::new);
    }

    public void deletaVeiculoPelaPlaca(String placa) {
        repository.findById(placa).orElseThrow(EntityNotFoundException::new);
        repository.deleteById(placa);
    }

    public ResponseEntity<Veiculo> adicionaMulta(String placa, MultaRecord multa) {
        Veiculo veiculo = repository.findById(placa).orElseThrow(EntityNotFoundException::new);
        veiculo.setQtdMultas(multa.qtdMultas());
        return ResponseEntity.ok(repository.save(veiculo));
    }

}
