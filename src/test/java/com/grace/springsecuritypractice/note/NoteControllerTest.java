package com.grace.springsecuritypractice.note;

import com.grace.springsecuritypractice.user.User;
import com.grace.springsecuritypractice.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@ActiveProfiles(profiles = "test")
class NoteControllerTest {

    private MockMvc mockMvc;
    @Autowired
    UserRepository userRepository;
    private User user;
    private User admin;

    @BeforeEach
    public void setUp(@Autowired WebApplicationContext applicationContext) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .alwaysDo(print())
                .build();
        user = userRepository.save(new User("user123", "user", "ROLE_USER"));
        admin = userRepository.save(new User("admin123", "admin", "ROLE_ADMIN"));
    }

    @Test
    void getNote_????????????() throws Exception {
        mockMvc.perform(get("/note"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithUserDetails(
            value = "user123", // userDetailsService??? ?????? ????????? ??? ?????? ??????
            userDetailsServiceBeanName = "userDetailsService", // UserDetailsService ???????????? Bean
            setupBefore = TestExecutionEvent.TEST_EXECUTION // ????????? ?????? ????????? ????????? ????????????.
    )
    void getNote_????????????() throws Exception {
        mockMvc.perform(
                        get("/note")
                ).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails(
            value = "admin123", // userDetailsService??? ?????? ????????? ??? ?????? ??????
            userDetailsServiceBeanName = "userDetailsService", // UserDetailsService ???????????? Bean
            setupBefore = TestExecutionEvent.TEST_EXECUTION // ????????? ?????? ????????? mock ?????? ??????
    )
    void getNote_?????????????????????() throws Exception {
        mockMvc.perform(get("/note"))
                .andExpect(status().isForbidden());

    }

}