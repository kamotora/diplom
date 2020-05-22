package com.diplom.work.controller;

import com.diplom.work.core.Client;
import com.diplom.work.core.Rule;
import com.diplom.work.core.json.view.Views;
import com.diplom.work.exceptions.ClientException;
import com.diplom.work.exceptions.ClientNotFound;
import com.diplom.work.exceptions.NumberParseException;
import com.diplom.work.svc.ClientService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Controller
public class ClientController {

    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    /**
     * Страница с таблицей "Список  клиентов"
     * Данные получаются через api
     * @see com.diplom.work.controller.api.ApiClientController
     */
    @GetMapping("/clients")
    public String list(Model model) {
        return "clients";
    }

    /**
     * Вывод формы для изменения клиента
     *
     * @return заполенная форма
     */
    @GetMapping("/client/{id}")
    public String getPageForEditClient(Model model, @PathVariable("id") Client client) {
        model.addAttribute("client", client);
        return "client";
    }

    /**
     * Вывод формы для добавления клиента
     *
     * @return пустая форма
     */
    @GetMapping("/client")
    public String getPageForAddClient(Model model) {
        model.addAttribute("client", new Client());
        return "client";
    }

    /**
     * Страница для просмотра клиента
     *
     * @return страница
     */
    @GetMapping("/client/{id}/view")
    public String getViewPage(@PathVariable("id") Client client, Model model) {
        model.addAttribute("client", client);
        model.addAttribute("isView", "true");
        return "client";
    }

    /**
     * Возврат всех правил для клиента
     *
     * @return всех правил в виде JSON
     */
    @GetMapping(path = "/client/{id}/rules", produces = {MediaType.APPLICATION_JSON_VALUE})
    @JsonView(Views.forTable.class)
    public ResponseEntity<Set<Rule>> getRulesForClient(@PathVariable("id") Client client) {
        return ResponseEntity.ok(client.getRules());
    }


    /**
     * Сохранение клиента
     *
     * @return страница с заполенной формой и сообщение об ошибке/успехе
     */
    @PostMapping(value = "/client")
    public String saveClient(Model model, Client client) {
        try {
            client = clientService.save(client);
        } catch (NumberParseException e) {
            model.addAttribute("badMessage", e.getMessage());
        } catch (Exception e) {
            model.addAttribute("badMessage", "Возникла неизвестная ошибка при сохранении :(");
        }
        model.addAttribute("goodMessage", "Сохранено");
        return getPageForEditClient(model, client);
    }


    /**
     * Удаление клиентов по массиву IDs
     *
     * @param ids - массив с ID клиента
     * @return блок с сообщениями об успехе/ошибке для его вывода через jquery
     */
    @DeleteMapping("/client")
    public String deleteClient(Model model, @RequestBody List<Long> ids) {
        try {
            for (Long id : ids)
                clientService.deleteClientById(id);
            model.addAttribute("goodMessage", "Удалено");
        } catch (ClientNotFound | ClientException exception) {
            model.addAttribute("badMessage", exception.getMessage());
        } catch (Exception exception) {
            exception.printStackTrace(System.err);
            System.err.println(exception.getMessage());
            model.addAttribute("badMessage", "Не удалось удалить!");
        }
        return "fragments/messages :: messages";
    }
}
