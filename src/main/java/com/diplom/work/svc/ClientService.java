package com.diplom.work.svc;

import com.diplom.work.core.Client;
import com.diplom.work.core.Rule;
import com.diplom.work.exceptions.ClientNotFound;
import com.diplom.work.repo.ClientRepository;
import com.google.common.base.CharMatcher;
import lombok.NonNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

@Service
public class ClientService {
    private final ClientRepository clientRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Set<Client> getAllByRule(@NonNull Rule rule) {
        Set<Client> allByRulesContaining = clientRepository.findAllByRulesContaining(rule);
        return  allByRulesContaining;
    }

    //Клиентов с одинаковым номером быть не должно
    public Client getFirstByNumber(@NonNull String number) {
        return clientRepository.findFirstByNumber(number);
    }


    public Client save(@NonNull Client client) {
        //Оставляем в номере только цифры
        client.setNumber(CharMatcher.inRange('0', '9').retainFrom(client.getNumber()));
        //Если id есть, зачем искать в базе дубли
        if (client.getId() == null || client.getId() == 0) {
            Client findedClient = getFirstByNumber(client.getNumber());
            if (findedClient != null) {
                BeanUtils.copyProperties(client, findedClient, "id");
                return clientRepository.save(findedClient);
            }
        }
        return clientRepository.save(client);
    }

    public boolean deleteClient(Client client) throws ClientNotFound {
        if (client == null || client.getId() == 0)
            throw new ClientNotFound();
        client.setRules(new HashSet<>());
        clientRepository.save(client);
        clientRepository.delete(client);
        return true;

    }

    public boolean deleteClientById(Long id) throws ClientNotFound {
        if (id == null || id == 0)
            throw new ClientNotFound();
        Client client = clientRepository.findById(id).orElseThrow(ClientNotFound::new);
        return deleteClient(client);
    }

    public Client getById(Long id) {
        return clientRepository.getOne(id);
    }
}
