package com.diplom.work.controller.api;

import com.diplom.work.core.Client;
import com.diplom.work.core.json.view.ClientViews;
import com.diplom.work.exceptions.ClientNotFound;
import com.diplom.work.svc.ClientService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/client/")
public class ApiClientController {

    private final ClientService clientService;

    @Autowired
    public ApiClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    /**
     * Возврат всех клиентов для в виде JSON
     *
     * @return всех клиентов в виде JSON
     */
    @GetMapping(path = "all", produces = {MediaType.APPLICATION_JSON_VALUE})
    @JsonView(ClientViews.forTable.class)
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
    @JsonView(ClientViews.forTable.class)
    public Client addClient(@RequestBody Client client) {
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
    @JsonView(ClientViews.forTable.class)
    public Client updateClient(@RequestBody Client client) {
        return clientService.updateExistingClient(client);
    }


    /**
     * Удаление информации о клиенте по его id
     *
     * @param client удаляемый клиент
     * @return Изменённый клиент
     * @see Client
     */
    @DeleteMapping(path = "{id}", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @JsonView(ClientViews.forTable.class)
    public boolean deleteClient(@PathVariable("id") Client client) throws ClientNotFound {
        return clientService.deleteClient(client);
    }
}
