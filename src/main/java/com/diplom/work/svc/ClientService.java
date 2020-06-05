package com.diplom.work.svc;

import com.diplom.work.core.Client;
import com.diplom.work.core.Rule;
import com.diplom.work.exceptions.ClientException;
import com.diplom.work.exceptions.ClientNotFound;
import com.diplom.work.exceptions.NumberParseException;
import com.diplom.work.repo.ClientRepository;
import com.diplom.work.repo.RuleRepository;
import com.google.common.base.CharMatcher;
import lombok.NonNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.util.List;
import java.util.Set;

@Service
public class ClientService {
    private final ClientRepository clientRepository;
    private final RuleRepository ruleRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository, RuleRepository ruleRepository) {
        this.clientRepository = clientRepository;
        this.ruleRepository = ruleRepository;
    }

    public Set<Client> getAllByRule(@NonNull Rule rule) {
        return clientRepository.findAllByRulesContaining(rule);
    }

    /**
     * Поиск клиента по точному совпадению номера
     *
     * @return найденный клиент или null, если не найдено
     */
    public Client getFirstByNumberEquals(@NonNull String number) {
        return clientRepository.findFirstByNumber(number);
    }

    /**
     * Пытаемся найти по поступающему номеру
     * Если не нашли, пробуем оставить только цифры,
     * убрать код страны (т.к. 7 или 8 может быть, надеюсь с украины никто не позвонит) и пробуем ещё раз
     * <p>
     * Клиентов с одинаковым номером быть не должно (надеюсь)
     * </p>
     *
     * @return найденный клиент или null, если не найдено
     */
    public Client getFirstByNumberSubstr(@NonNull String number) {
        Client client = getFirstByNumberEquals(number);
        if (client == null) {
            client = clientRepository.findFirstByNumberContaining(getOnlyNumbers(number).substring(1));
        }
        return client;
    }

    public Client updateExistingClient(@NonNull Client client) {
        if (client.getId() == null || client.getId() == 0)
            return null;
        Client clientFromDb = clientRepository.getOne(client.getId());
        clientFromDb.setNumber(client.getNumber());
        clientFromDb.setName(client.getName());
        return clientRepository.save(clientFromDb);
    }

    public Client save(@NonNull Client client) throws NumberParseException {
        //Оставляем в номере только цифры
        client.setNumber(getOnlyNumbers(client.getNumber()));
        if (StringUtils.isEmptyOrWhitespace(client.getNumber()))
            throw new NumberParseException();

        // Если id есть, сохраняем так
        // Иначе пробуем поискать по номеру и обновить данные
        Client clientFromBd;
        if (client.getId() == null || client.getId() == 0) {
            // Вдруг клиент с таким номером уже есть
            clientFromBd = getFirstByNumberEquals(client.getNumber());
        } else
            clientFromBd = getById(client.getId());
        if (clientFromBd != null) {
            // Меняем список правил, в которых участвует клиент
            // Т.к. rule является "главным", меняем через него
            for (Rule rule : clientFromBd.getRules()) {
                rule.getClients().remove(clientFromBd);
                ruleRepository.save(rule);
            }
            for (Rule rule : client.getRules()) {
                rule.getClients().add(client);
                ruleRepository.save(rule);
            }
            BeanUtils.copyProperties(client, clientFromBd, "id");
            return clientRepository.save(clientFromBd);
        }
        return clientRepository.save(client);
    }

    public boolean deleteClient(Client client) throws ClientNotFound, ClientException {
        if (client == null || client.getId() == 0)
            throw new ClientNotFound();
        if (!client.getRules().isEmpty())
            throw new ClientException("Не удалось удалить. Данный клиент участвует в правилах. Перейдите в редактирование клиента и удалите его из всех правил. Затем можете попробовать удалить клиента ещё раз");
        clientRepository.save(client);
        clientRepository.delete(client);
        return true;

    }

    public boolean deleteClientById(Long id) throws ClientNotFound, ClientException {
        if (id == null || id == 0)
            throw new ClientNotFound();
        Client client = clientRepository.findById(id).orElseThrow(ClientNotFound::new);
        return deleteClient(client);
    }

    public Client getById(Long id) {
        return clientRepository.getOne(id);
    }

    public List<Client> getAll() {
        return clientRepository.findAll();
    }

    /**
     * Возвращает только числа из номера (удаляет плюсики и т.п.)
     *
     * @return номер
     */
    public String getOnlyNumbers(String number) {
        return CharMatcher.inRange('0', '9').retainFrom(number);
    }

}
