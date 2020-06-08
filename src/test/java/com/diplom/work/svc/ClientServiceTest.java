package com.diplom.work.svc;

import com.diplom.work.core.Client;
import com.diplom.work.core.Rule;
import com.diplom.work.exceptions.*;
import com.diplom.work.repo.ClientRepository;
import com.diplom.work.repo.RuleRepository;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class ClientServiceTest {
    private final ClientRepository clientRepository = mock(ClientRepository.class);
    private final RuleRepository ruleRepository = mock(RuleRepository.class);
    private final ClientService clientService = new ClientService(clientRepository,ruleRepository);

    @Test
    public void save() {
        final Client client = new Client();
        final String TEL = "+7(912)-345-66-37";
        final String INCORRECT_TEL = "adfianoeivna   ejj";
        final String NAME = "Ivanov Иван ивановиCH";
        client.setName(NAME);
        client.setNumber(INCORRECT_TEL);
        Rule rule = new Rule();
        client.setRules(Set.of(rule));
        assertThrows(NumberParseException.class, () -> clientService.save(client));
        client.setNumber(TEL);
        try {
            clientService.save(client);
        } catch (NumberParseException exception) {
            exception.printStackTrace();
            fail(exception.getMessage());
        }
        Mockito.verify(clientRepository, Mockito.times(1)).save(client);
        // Правила можно добавить только для существующих клиентов
        Mockito.verify(ruleRepository, Mockito.times(0)).save(rule);
    }

    @Test
    public void deleteClient() {
        Client validClient = new Client();
        validClient.setId(1L);
        assertThrows(ClientNotFound.class, () -> clientService.deleteClientById(validClient.getId()));
        Mockito.doReturn(Optional.of(validClient))
                .when(clientRepository)
                .findById(validClient.getId());
        try {
            assertTrue(clientService.deleteClient(validClient));
        } catch (ClientNotFound | ClientException e) {
            fail(e.getMessage());
        }
        validClient.getRules().add(new Rule());
        assertThrows(ClientException.class, () -> clientService.deleteClient(validClient));
    }

}