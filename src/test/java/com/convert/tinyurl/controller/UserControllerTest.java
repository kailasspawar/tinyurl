package com.convert.tinyurl.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.convert.tinyurl.WebConfig;
import com.convert.tinyurl.dao.TokenRepository;
import com.convert.tinyurl.interceptor.JwtTokenInterceptor;
import com.convert.tinyurl.model.Token;
import com.convert.tinyurl.model.User;
import com.convert.tinyurl.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.hamcrest.Matchers.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UserController.class)
@Import(WebConfig.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtTokenInterceptor jwtTokenInterceptor;
 
    @MockBean
    private TokenRepository tokenRepository;

    @Test
    public void testLogin_ValidCredentials_ReturnsToken() throws Exception {
        // Arrange
        User user = new User("testUser", "password");
        String jwtSecret = "your-secret-key";
        int jwtExpiration = 30; // expiration in minutes
        
        when(jwtTokenInterceptor.preHandle(ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.any(HttpServletResponse.class), ArgumentMatchers.any(Object.class)))
        .thenReturn(true);
        when(userService.isValidUser(user.getUsername(), user.getPassword())).thenReturn(true);
        when(tokenRepository.save(any(Token.class))).thenReturn(new Token());

        // Act and Assert
        mockMvc.perform(post("/tinyurl/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user)))
        		.andExpect(status().isOk())
                .andExpect(content().string(not(nullValue())));
    }

    @Test
    public void testLogin_InvalidCredentials_ReturnsErrorMessage() throws Exception {
        // Arrange
        User user = new User("testUser", "invalidPassword");
        when(jwtTokenInterceptor.preHandle(ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.any(HttpServletResponse.class), ArgumentMatchers.any(Object.class)))
        .thenReturn(true);
        when(userService.isValidUser(user.getUsername(), user.getPassword())).thenReturn(false);

        // Act and Assert
        mockMvc.perform(post("/tinyurl/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user)))
                .andExpect(status().isOk())
                .andExpect(content().string("Invalid username or password"));
    }

    // Helper method to convert object to JSON string
    private String asJsonString(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
