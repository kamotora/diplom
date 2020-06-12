package com.diplom.work.controller;

import com.diplom.work.core.Log;
import com.diplom.work.repo.LogRepository;
import com.diplom.work.svc.LogService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@WithUserDetails(value = "admin", userDetailsServiceBeanName = "userService")
public class LogsControllerTest {
    @MockBean
    private LogService logService;
    @Autowired
    private LogRepository logRepository;
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void logsTableExistTest() throws Exception {
        this.mockMvc.perform(get("/logs"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("//*[@id='LogsTable']").exists());
    }

    @Test
    public void logsTableDataTableWork() throws Exception {
        this.mockMvc.perform(get("/logs/table"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk());
    }

    @Test
    public void logsTableDataTableWithFilterWork() throws Exception {
        String body = "{\"startDate\":\"2020-06-06\",\"finishDate\":\"2020-06-06\"}";
        this.mockMvc.perform(post("/logs/table")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body).with(csrf()))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk());
    }

    @Test
    public void logAddAndDelete() throws Exception {
        final Log log = logRepository.save(new Log());
        assertNotNull(log.getId());

        this.mockMvc.perform(delete("/log").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("[" + log.getId() + "]"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk());
    }

    @Test
    public void getLogInfoAndRecord() throws Exception {
        Log log = new Log();
        final String SESSION_ID = "session_id";
        log.setSession_id(SESSION_ID);
        log = logRepository.save(log);
        this.mockMvc.perform(get("/log/" + log.getId() + "/view"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk());
    }

    @Test
    public void getLogRecordIsBad() throws Exception {
        this.mockMvc.perform(get("/log/0/record"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getLogGraphicsStat() throws Exception {
        List<Log> logs = new ArrayList<>();
        String[] typesOfLog = {"incoming", "outbound", "internal"};
        for (String type : typesOfLog) {
            Log log = new Log();
            log.setTimestampInDateTimeFormat(LocalDateTime.now());
            log.setType(type);
            logs.add(log);
        }
        Mockito
                .doReturn(logs)
                .when(logService)
                .findAll(any(), any());
        MvcResult mvcResult = this.mockMvc.perform(post("/rest/logs/updateDataForGraphics"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertEquals(contentAsString, "[1,1,1]");
    }
}