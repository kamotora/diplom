package com.diplom.work;

import com.diplom.work.controller.UserController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-dev.properties")
@WithUserDetails(value = "admin", userDetailsServiceBeanName = "userService")
public class UserControllerTest {
    @Autowired
    private UserController userController;

    @Autowired
    private MockMvc mockMvc;

    /**
     * Успешный логин
     */
    @Test
    public void mainPageTest() throws Exception {
        this.mockMvc.perform(get("/users"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("//*[@id='navbarCollapse']/span/a").string("admin"));
    }

    @Test
    public void usersTableExistTest() throws Exception {
        this.mockMvc.perform(get("/users"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("//*[@id='table']").exists());
    }

}
