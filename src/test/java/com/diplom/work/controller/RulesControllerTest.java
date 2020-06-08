package com.diplom.work.controller;

import com.diplom.work.core.Rule;
import com.diplom.work.repo.RuleRepository;
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
public class RulesControllerTest {
    @Autowired
    private RuleRepository ruleRepository;
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

    @Test
    public void ruleDelete() throws Exception {
        Rule rule = new Rule();
        rule.setName("test");
        rule.setIsSmart(true);
        rule.setIsForAllClients(true);
        rule.setTimeStartString("10:10");
        rule.setTimeFinishString("11:11");
        rule = ruleRepository.save(rule);
        assertNotNull(rule.getId());
        assertTrue(ruleRepository.existsById(rule.getId()));

        this.mockMvc.perform(delete("/rule").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("[" + rule.getId() + "]"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk());
        assertFalse(ruleRepository.existsById(rule.getId()));
    }
}