package com.grace.springsecuritypractice.notice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
class NoticeControllerTest {

    private MockMvc mockMvc;

    // 테스트 실행전 MockMvc 초기화
    @BeforeEach
    public void setup(@Autowired WebApplicationContext webApplicationContext){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity()) // spring security 적용
                .alwaysDo(print())
                .build();
    }

    // notice 페이지는 인증된 사람이면 모두 접근 가능
    @Test
    void getNotice_인증없음() throws Exception {
        mockMvc.perform(get("/notice"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

    }
    @Test
    @WithMockUser // 인증된 유저
    void getNotice_인증있음() throws Exception {
        mockMvc.perform(get("/notice"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"}, username = "admin", password = "admin")
    void postNotice_어드민권한() throws Exception {
        mockMvc.perform(
                        post("/notice").with(csrf())
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("title", "제목")
                                .param("content", "내용")
                ).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("notice"));
    }

    @Test
    void deleteNotice(){

    }
}