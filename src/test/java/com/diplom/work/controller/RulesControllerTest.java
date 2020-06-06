package com.diplom.work.controller;

import com.diplom.work.repo.LogRepository;
import com.diplom.work.svc.LogService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@WithUserDetails(value = "admin", userDetailsServiceBeanName = "userService")
public class RulesControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void rulesTableExistTest() throws Exception {
        this.mockMvc.perform(get("/rules"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("//*[@id='RulesTable']").exists());
    }

    @Test
    public void rulesTableGetData() throws Exception {
        this.mockMvc.perform(get("/rule/table"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk());
    }
}