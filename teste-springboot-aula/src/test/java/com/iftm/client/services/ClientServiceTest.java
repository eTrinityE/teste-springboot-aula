package com.iftm.client.services;

import com.iftm.client.dto.ClientDTO;
import com.iftm.client.entities.Client;
import com.iftm.client.repositories.ClientRepository;
import com.iftm.client.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.times;

@ExtendWith(SpringExtension.class)
public class ClientServiceTest {
    @InjectMocks
    ClientService service;

    @Mock
    ClientRepository repository;

    @Test
    @DisplayName("Testa metodo delete quando o id existir")
    public void testeDeleteIdExistente(){
        Long id = 1L;

        service.delete(id);

        Mockito.verify(repository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Testa metodo delete quando o id não existir")
    public void testeDeleteIdInexistente(){
        Long id = 129329323L;

        Mockito.doThrow(ResourceNotFoundException.class).when(repository).deleteById(id);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.delete(id));
    }

    @Test
    @DisplayName("Testa Metodo findAllPaged que deve retornar uma página com todos os clientes")
    public void testeFindAllPaged(){
        List<Client> clientes = new ArrayList<>(Arrays.asList(
                new Client(1L,"Joao", "123", 730D, Instant.now(), 3),
                new Client(2L,"Maria", "321", 1014D, Instant.now(), 1),
                new Client(3L,"Fulana", "007", 2030D, Instant.now(), 0)
        ));
        PageRequest pageRequest = PageRequest.of(0, clientes.size());
        Page<Client> page = new PageImpl<>(clientes);

        Mockito.when(repository.findAll(pageRequest)).thenReturn(page);
        Page<ClientDTO> resultado = service.findAllPaged(pageRequest);

        Assertions.assertEquals(clientes.size(), resultado.getSize());
        Assertions.assertEquals(clientes.get(0).getId(), resultado.getContent().get(0).getId());
        Assertions.assertEquals(clientes.get(1).getId(), resultado.getContent().get(1).getId());
        Assertions.assertEquals(clientes.get(2).getId(), resultado.getContent().get(2).getId());

    }

    @Test
    @DisplayName("Testa metodo findByIncome que deve retornar uma pagina com os clientes que tenham o income" +
            "maior que o passado como parametro")
    public void testeFindByIncomeGreaterThan(){
        List<Client> clientes = new ArrayList<>(Arrays.asList(
                new Client(2L,"Maria", "321", 5080D, Instant.now(), 1),
                new Client(3L,"Fulana", "007", 2030D, Instant.now(), 0)
        ));
        PageRequest pageRequest = PageRequest.of(0, clientes.size());
        Page<Client> page = new PageImpl<>(clientes);
        int tamanhoEsperado = 2;

        Mockito.when(repository.findByIncomeGreaterThan(1500D, pageRequest)).thenReturn(page);
        Page<ClientDTO> resultado = service.findByIncomeGreaterThan(pageRequest,1500D);

        Assertions.assertEquals(tamanhoEsperado, resultado.getContent().size());
        Assertions.assertTrue(resultado.getContent().get(0).getIncome()>1500);
        Assertions.assertTrue(resultado.getContent().get(1).getIncome()>1500);
    }


    @Test
    @DisplayName("Testa metodo findById quando id for existente")
    public void testeFindByIdExistente() {
        Long id = 1L;
        Client client = new Client(id,"Fulano da Silva", "123", 2000D, Instant.now(),1);

        Mockito.when(repository.findById(id)).thenReturn(Optional.of(client));
        ClientDTO resultado = service.findById(id);

        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(id, resultado.getId());
    }

    @Test
    @DisplayName("Testa metodo findById quando id for Inexistente")
    public void testeFindByIdInexistente() {
        Long id = 1L;

        Mockito.doThrow(ResourceNotFoundException.class).when(repository).findById(id);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findById(id));
    }

    @Test
    @DisplayName("Testa metodo update quando id for Existente")
    public void testeUpdateIdExistente() {
        Long id = 1L;
        Client client = new Client(id,"Fulano da Silva", "123", 2000D, Instant.now(),1);
        ClientDTO clientDto = new ClientDTO(id,"Fulano da Silva de souza", "123", 2500D, Instant.now(),2);

        Mockito.when(repository.getOne(id)).thenReturn(client);
        Mockito.when(repository.save(client)).thenReturn(client);
        ClientDTO resultado = service.update(id, clientDto);

        Assertions.assertEquals(ClientDTO.class, resultado.getClass());
        Assertions.assertEquals(clientDto.getId(), resultado.getId());
        Assertions.assertEquals(clientDto.getName(), resultado.getName());
        Assertions.assertEquals(clientDto.getIncome(), resultado.getIncome());
    }

    @Test
    @DisplayName("Testa metodo update quando id for Inexistente")
    public void testeUpdateIdInexistente() {
        Long id = 1121212L;

        Mockito.doThrow(ResourceNotFoundException.class).when(repository).getOne(id);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.update(id, new ClientDTO()));
    }

    @Test
    @DisplayName("Testa metodo insert que deve retornar um ClientDTO ao inserir um novo cliente")
    public void testeInsert(){
        ClientDTO clientASerInserido  = new ClientDTO(39L,"Fulano da Silva", "123", 2000D, Instant.now(),1);
        Client client = clientASerInserido.toEntity();

        Mockito.when(repository.save(client)).thenReturn(client);
        ClientDTO resultado = service.insert(clientASerInserido);

        Assertions.assertEquals(ClientDTO.class, resultado.getClass());
        Assertions.assertEquals(clientASerInserido.getId(), resultado.getId());
    }

}
