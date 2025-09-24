package com.bank.cardmanager.exception;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class GlobalExceptionHandlerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    @WithMockUser(roles = "USER")
    void whenResourceNotFound_thenReturns404() throws Exception {
        mockMvc.perform(get("/cards/9999"))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.status").value(404))
               .andExpect(jsonPath("$.message").exists());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void whenValidationException_thenReturns400() throws Exception {
        mockMvc.perform(post("/cards")
               .contentType("application/json")
               .content("{}"))
               .andExpect(status().isBadRequest());
    }
}