package com.diplom.work.controller;

import com.diplom.work.controller.ControllerUtils;
import com.diplom.work.core.Client;
import com.diplom.work.core.Days;
import com.diplom.work.core.Rule;
import com.diplom.work.core.Settings;
import com.diplom.work.core.json.NumberInfo;
import com.diplom.work.exceptions.ManagerIsNull;
import com.diplom.work.exceptions.SettingsNotFound;
import com.diplom.work.exceptions.TimeIncorrect;
import com.diplom.work.repo.SettingsRepository;
import com.diplom.work.svc.ClientService;
import com.diplom.work.svc.LogService;
import com.diplom.work.svc.RuleService;
import com.diplom.work.svc.SettingsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.sql.Time;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
public class ApiTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RuleService ruleService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private LogService logService;
    @Autowired
    private SettingsService settingsService;
    @Autowired
    private SettingsRepository settingsRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setUp() {
        Settings settings = new Settings();
        settings.setIsNeedCheckSign(true);
        settings.setClientID(ControllerUtils.randomString(32));
        settings.setClientKey(ControllerUtils.randomString(32));
        settingsService.save(settings);
    }

    @After
    public void tearDown() {
        settingsRepository.deleteAll();
    }

    public Settings testSettings() throws SettingsNotFound {
        Settings settings = settingsService.getSettings();
        assertNotNull(settings);
        assertNotNull(settings.getClientID());
        assertNotNull(settings.getClientKey());
        assertTrue(settings.getIsNeedCheckSign());
        return settings;
    }

    @Test
    public void addCallEventWithCorrectSign() throws Exception {
        String jsonBody = "{\"disconnect_reason\": \"4\",\"from_number\": \"sip:111@test@domain.com\",\"from_pin\": \"201\",\"is_record\": \"\",\"request_number\": \"sip:8912345678@123.45.67\",\"request_pin\": \"\",\"session_id\": \"1674-aFEQBQE\",\"state\": \"disconnected\",\"timestamp\": \"2020-05-12 10:46:56.725\",\"type\": \"outbound\"}";
        Settings settings = testSettings();
        HttpHeaders headers = ControllerUtils.getHeaders(jsonBody, settings.getClientID(), settings.getClientKey());
        assertNotNull(headers);
        assertNotNull(headers.get("X-Client-ID"));
        assertNotNull(headers.get("X-Client-Sign"));
        this.mockMvc.perform(post("/api/call_events")
                .headers(headers)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andDo(print()).andExpect(status().isOk());
        assertEquals(1, logService.findAllByOrderByTimestampAsc().size());
    }

    @Test
    public void addCallEventWithUnCorrectSign() throws Exception {
        String jsonBody = "{\"disconnect_reason\": \"4\",\"from_number\": \"sip:111@test@domain.com\",\"from_pin\": \"201\",\"is_record\": \"\",\"request_number\": \"sip:8912345678@123.45.67\",\"request_pin\": \"\",\"session_id\": \"1674-aFEQBQE\",\"state\": \"disconnected\",\"timestamp\": \"2020-05-12 10:46:56.725\",\"type\": \"outbound\"}";
        Settings settings = testSettings();
        HttpHeaders headers = new HttpHeaders();
        headers.put("X-Client-ID", Collections.singletonList(ControllerUtils.randomString(32)));
        headers.put("X-Client-Sign", Collections.singletonList(ControllerUtils.randomString(32)));
        this.mockMvc.perform(post("/api/call_events")
                .headers(headers)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andDo(print()).andExpect(status().isForbidden());
        assertEquals(0, logService.findAllByOrderByTimestampAsc().size());
    }

    @Test
    public void askRouteUnknownClientWithCorrectSign() throws Exception {
        final String CLIENT_NUMBER = "79812345678";
        NumberInfo numberInfo = new NumberInfo("test@domain.com", CLIENT_NUMBER, "89141495124");
        String jsonBody = objectMapper.writeValueAsString(numberInfo);
        Settings settings = testSettings();
        HttpHeaders headers = ControllerUtils.getHeaders(numberInfo, settings.getClientID(), settings.getClientKey());
        assertNotNull(headers);
        assertNotNull(headers.get("X-Client-ID"));
        assertNotNull(headers.get("X-Client-Sign"));
        this.mockMvc.perform(post("/api/get_number_info")
                .headers(headers)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andDo(print()).andExpect(status().isNotFound());
    }


    @Test
    public void askRouteKnownClientWithCorrectSign() throws Exception, TimeIncorrect, ManagerIsNull {
        final String CLIENT_NUMBER = "79812345678";
        final String MANAGER_NUMBER_FROM_RULE = "123";
        // Сохраняем правило с звонящим клиентом в БД
        Client client = new Client();
        client.setNumber(CLIENT_NUMBER);
        Client savedClient = clientService.save(client);
        Rule rule = new Rule();
        rule.getDays().addAll(Arrays.asList(Days.values()));
        rule.setTimeStartString(Time.valueOf(LocalTime.now().minusHours(1)).toString());
        rule.setTimeFinishString(Time.valueOf(LocalTime.now().plusHours(1)).toString());
        rule.setIsForAllClients(true);
        rule.setManagerNumber(MANAGER_NUMBER_FROM_RULE);
        rule.setName("test");
        rule.getClients().add(savedClient);
        Rule savedRule = ruleService.save(rule);
        assertFalse(savedRule.getClients().isEmpty());
        assertTrue(ruleService.getAll().contains(savedRule));
        assertTrue(ruleService.getAll().get(0).getClients().contains(savedClient));
        // Запрос
        NumberInfo numberInfo = new NumberInfo("test@domain.com", CLIENT_NUMBER, "89141495124");
        String jsonBody = objectMapper.writeValueAsString(numberInfo);
        Settings settings = testSettings();
        HttpHeaders headers = ControllerUtils.getHeaders(numberInfo, settings.getClientID(), settings.getClientKey());
        assertNotNull(headers);
        assertNotNull(headers.get("X-Client-ID"));
        assertNotNull(headers.get("X-Client-Sign"));
        MvcResult result = this.mockMvc.perform(post("/api/get_number_info")
                .headers(headers)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andDo(print()).andExpect(status().isOk())
                .andReturn();

        assertTrue(result.getResponse().getContentAsString().contains(MANAGER_NUMBER_FROM_RULE));
        assertTrue(result.getResponse().getContentAsString().contains(CLIENT_NUMBER));
    }
}
