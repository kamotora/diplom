package com.diplom.work;

import com.diplom.work.controller.UserController;
import com.diplom.work.core.user.User;
import com.diplom.work.repo.UserRepository;
import com.diplom.work.svc.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.HashSet;

import static org.junit.Assert.assertNotNull;
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
public class UserControllerTest {
    @Autowired
    private UserController userController;
    @Autowired
    private UserRepository userRepository;
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

    @Test
    public void usersTableDataTableWork() throws Exception {
        this.mockMvc.perform(get("/users/table"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk());
    }

    @Test
    public void usersTableAddFormCheck() throws Exception {
        this.mockMvc.perform(get("/user"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("/html/body/main/div/form/button").exists())
                .andExpect(xpath("//*[@id='name']").exists());
    }

    @Test
    public void userSaveAndDelete() throws Exception {
        MockHttpServletRequestBuilder multipart = multipart("/user")
                .param("name", "fifth afs qwe")
                .param("username", "user")
                .param("password1", "qwerty")
                .param("password2", "qwerty")
                .param("role", "USER")
                .param("active", "true")
                .param("_active", "on")
                .with(csrf());
        this.mockMvc.perform(multipart);
        User user = userRepository.findByUsername("user");
        assertNotNull(user);

        this.mockMvc.perform(delete("/user").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("[" + user.getId() + "]"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk());
    }
}
