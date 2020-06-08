package com.diplom.work.controller;

import com.diplom.work.core.Client;
import com.diplom.work.core.Days;
import com.diplom.work.core.Rule;
import com.diplom.work.core.Settings;
import com.diplom.work.svc.SettingsService;
import com.diplom.work.svc.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.sql.Time;
import java.time.LocalTime;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
public class RestTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserService userService;
    @Autowired
    private SettingsService settingsService;

    @Test
    public void restForUnauthorized() throws Exception {
        this.mockMvc.perform(get("/rest/rule/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = "admin", userDetailsServiceBeanName = "userService")
    public void restForAuthorized() throws Exception {
        this.mockMvc.perform(get("/rest/client/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk());
    }

    @Test
    public void restWithToken() throws Exception {
        Settings settings = new Settings();
        settings.setIsTokensActivate(true);
        settingsService.save(settings);
        userService.createTokensIfNotExists();
        String token = userService.findByUsername("admin").getToken();
        this.mockMvc.perform(get("/rest/client/all")
                .header("X-AUTH-TOKEN", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails(value = "admin", userDetailsServiceBeanName = "userService")
    public void restAddClient() throws Exception {
        Client client = new Client();
        client.setName("Тестов тест");
        client.setNumber("123");
        Rule rule = new Rule();
        rule.setIsSmart(true);
        rule.setTimeStart(Time.valueOf(LocalTime.now()));
        rule.setTimeFinish(Time.valueOf(LocalTime.now()));
        rule.getDays().add(Days.Friday);
        client.getRules().add(rule);
        String s = new ObjectMapper().writeValueAsString(client);
        final MvcResult resultAddClient = this.mockMvc.perform(post("/rest/client")
                .contentType(MediaType.APPLICATION_JSON)
                .content(s))
                .andDo(print())
                .andExpect(status().isOk()).andReturn();
        MvcResult resultAllClients = this.mockMvc.perform(get("/rest/client/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk()).andReturn();
        final Client addedClient = new ObjectMapper().readValue(resultAddClient.getResponse().getContentAsString(), Client.class);
        assertTrue(resultAllClients.getResponse().getContentAsString().contains("\"id\":" + addedClient.getId()));
    }
}
