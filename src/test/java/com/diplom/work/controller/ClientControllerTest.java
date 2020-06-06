package com.diplom.work.controller;

import com.diplom.work.core.Client;
import com.diplom.work.repo.ClientRepository;
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

import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@WithUserDetails(value = "admin", userDetailsServiceBeanName = "userService")
public class ClientControllerTest {
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void clientsTableExistTest() throws Exception {
        this.mockMvc.perform(get("/clients"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("//*[@id='clientsTable']").exists());
    }

    @Test
    public void clientsTableGetData() throws Exception {
        this.mockMvc.perform(get("/client/table"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk());
    }

    @Test
    public void clientDelete() throws Exception {
        Client client = new Client();
        client.setName("test");
        client.setNumber("123");
        client = clientRepository.save(client);
        assertNotNull(client.getId());
        assertTrue(clientRepository.existsById(client.getId()));

        this.mockMvc.perform(delete("/client").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("[" + client.getId() + "]"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk());
        assertFalse(clientRepository.existsById(client.getId()));
    }
}