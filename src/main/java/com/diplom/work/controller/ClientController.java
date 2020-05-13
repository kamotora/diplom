package com.diplom.work.controller;

import com.diplom.work.core.Client;
import com.diplom.work.core.Rule;
import com.diplom.work.core.json.view.ClientViews;
import com.diplom.work.exceptions.ClientNotFound;
import com.diplom.work.svc.ClientService;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Controller
public class ClientController {

    private final ClientService clientService;
    private final RulesController rulesController;

    @Autowired
    public ClientController(ClientService clientService, RulesController rulesController) {
        this.clientService = clientService;
        this.rulesController = rulesController;
    }

    /**
     * Возврат всех клиентов для таблицы в виде JSON (таблица на JS)
     *
     * @return всех клиентов в виде JSON
     */
    @GetMapping(path = "/rule/{id}/clients", produces = {MediaType.APPLICATION_JSON_VALUE})
    @JsonView(ClientViews.forTable.class)
    public ResponseEntity<Set<Client>> getUsersForTable(@PathVariable("id") Rule rule) {
        return ResponseEntity.ok(clientService.getAllByRule(rule));
    }

    @PostMapping(path = "/client", consumes = {MediaType.APPLICATION_JSON_VALUE}
            , produces = {MediaType.TEXT_PLAIN_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> addClient(@RequestBody Client client) {
        String jsonSavedClient = null;
        try {
            Client savedClient = clientService.save(client);
            rulesController.addClient(savedClient);
            jsonSavedClient = new ObjectMapper().writeValueAsString(savedClient);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(e.getMessage());
        }
        return ResponseEntity.ok(jsonSavedClient);
    }

    /**
     * Удаление клиентов по массиву IDs
     *
     * @param ids - массив с ID клиентов
     */
    @DeleteMapping(path = "/client", produces = {MediaType.TEXT_PLAIN_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> deleteUser(@RequestBody List<Long> ids) {
        try {
            for (Long id : ids) {
                Client client = clientService.getById(id);
                rulesController.removeClient(client);
                clientService.deleteClient(client);
            }
        } catch (ClientNotFound exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
        return ResponseEntity.ok().build();
    }

}
