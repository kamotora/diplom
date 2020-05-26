package com.diplom.work.controller.api;

import com.diplom.work.core.Client;
import com.diplom.work.core.json.view.Views;
import com.diplom.work.exceptions.ClientException;
import com.diplom.work.exceptions.ClientNotFound;
import com.diplom.work.exceptions.NumberParseException;
import com.diplom.work.svc.ClientService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CRUD для клиентов через json запросы (пока меняется только ФИО и номер)
 * (мб использовано для удалённого редактированния)
 */
@RestController
@RequestMapping("api/client")
public class ApiClientController {

    private final ClientService clientService;

    @Autowired
    public ApiClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    /**
     * Возврат всех клиентов в виде JSON
     *
     * @return все клиенты в виде JSON
     */
    @GetMapping(path = "/all", produces = {MediaType.APPLICATION_JSON_VALUE})
    @JsonView(Views.allClient.class)
    public List<Client> getAllClients() {
        return clientService.getAll();
    }

    /**
     * Добавление нового клиента
     *
     * @param client добавляемый клиент в виде json
     *               number - номер клиента (not null)
     *               name - ФИО клиента (может быть null)
     * @return Добавленный клиент с id
     * @see Client
     */
    @PostMapping(path = "", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @JsonView(Views.allClient.class)
    public Client addClient(@RequestBody Client client) throws NumberParseException {
        return clientService.save(client);
    }


    /**
     * Обновление информации о клиенте
     *
     * @param client изменённый клиент в виде json
     *               id - id клиента (not null)
     *               number - номер клиента (not null)
     *               name - ФИО клиента (может быть null)
     * @return Изменённый клиент
     * @see Client
     */
    @PutMapping(path = "", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @JsonView(Views.allClient.class)
    public Client updateClient(@RequestBody Client client) {
        return clientService.updateExistingClient(client);
    }


    /**
     * Удаление информации о клиенте по его id
     *
     * @param client удаляемый клиент
     * @return true - удалено, false - ошибка
     * @see Client
     */
    @DeleteMapping(path = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @JsonView(Views.allClient.class)
    public boolean deleteClient(@PathVariable("id") Client client) {
        try {
            return clientService.deleteClient(client);
        } catch (ClientNotFound | ClientException clientNotFound) {
            return false;
        }
    }

    /**
     * Удаление информации о клиентах по массиву id
     *
     * @param ids id удаляемых клиентов
     * @return true - удалено, false - ошибка
     * @see Client
     */
    @DeleteMapping(path = "", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @JsonView(Views.allClient.class)
    public boolean deleteClients(@RequestBody List<Long> ids) {
        for (Long id : ids) {
            try {
                clientService.deleteClientById(id);
            } catch (ClientNotFound | ClientException clientNotFound) {
                return false;
            }
        }
        return true;
    }
}
