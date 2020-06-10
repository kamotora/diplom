package com.diplom.work.controller;

import com.diplom.work.core.Client;
import com.diplom.work.core.Rule;
import com.diplom.work.core.json.view.Views;
import com.diplom.work.exceptions.ClientException;
import com.diplom.work.exceptions.ClientNotFound;
import com.diplom.work.exceptions.NumberParseException;
import com.diplom.work.svc.ClientService;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Controller
@Slf4j
@PreAuthorize("hasAuthority('Администратор') || hasAuthority('Пользователь')")
public class ClientController {

    private final ClientService clientService;
    private static final String CLIENT_ATTRIBUTE_NAME = "client";
    private static final String CLIENT_FORMPAGE_NAME = CLIENT_ATTRIBUTE_NAME;
    private static final String GOODMESSAGE_ATTRIBUTE_NAME = "goodMessage";
    private static final String BADMESSAGE_ATTRIBUTE_NAME = "badMessage";

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    /**
     * Страница с таблицей "Список  клиентов"
     * Данные получаются через api
     *
     * @see com.diplom.work.controller.api.ApiClientController
     */
    @GetMapping("/clients")
    public String list(Model model) {
        return "clients";
    }

    /**
     * Возврат всех клиентов в виде JSON
     *
     * @return все клиенты в виде JSON
     */
    @GetMapping(path = "/client/table", produces = {MediaType.APPLICATION_JSON_VALUE})
    @JsonView(Views.ForTable.class)
    public ResponseEntity<List<Client>> getAllClientsForTable() {
        return ResponseEntity.ok().body(clientService.getAll());
    }


    /**
     * Вывод формы для изменения клиента
     *
     * @return заполенная форма
     */
    @GetMapping("/client/{id}")
    public String getPageForEditClient(Model model, @PathVariable("id") Client client) {
        model.addAttribute(CLIENT_ATTRIBUTE_NAME, client);
        return CLIENT_FORMPAGE_NAME;
    }

    /**
     * Вывод формы для добавления клиента
     *
     * @return пустая форма
     */
    @GetMapping("/client")
    public String getPageForAddClient(Model model) {
        return getPageForEditClient(model, new Client());
    }

    /**
     * Страница для просмотра клиента
     *
     * @return страница
     */
    @GetMapping("/client/{id}/view")
    public String getViewPage(@PathVariable("id") Client client, Model model) {
        model.addAttribute(CLIENT_ATTRIBUTE_NAME, client);
        model.addAttribute("isView", "true");
        return CLIENT_FORMPAGE_NAME;
    }

    /**
     * Возврат всех правил для клиента
     *
     * @return всех правил в виде JSON
     */
    @GetMapping(path = "/client/{id}/rules", produces = {MediaType.APPLICATION_JSON_VALUE})
    @JsonView(Views.ForTable.class)
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
            model.addAttribute(GOODMESSAGE_ATTRIBUTE_NAME, "Сохранено");
        } catch (NumberParseException e) {
            model.addAttribute(BADMESSAGE_ATTRIBUTE_NAME, e.getMessage());
        } catch (Exception e) {
            model.addAttribute(BADMESSAGE_ATTRIBUTE_NAME, "Возникла неизвестная ошибка при сохранении :(");
        }
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
            model.addAttribute(GOODMESSAGE_ATTRIBUTE_NAME, "Удалено");
        } catch (ClientNotFound | ClientException exception) {
            model.addAttribute(BADMESSAGE_ATTRIBUTE_NAME, exception.getMessage());
        } catch (Exception exception) {
            log.error("Вызвано исключение при удалении клиента: {}", exception.getMessage());
            model.addAttribute(BADMESSAGE_ATTRIBUTE_NAME, "Не удалось удалить!");
        }
        return "fragments/messages :: messages";
    }
}
