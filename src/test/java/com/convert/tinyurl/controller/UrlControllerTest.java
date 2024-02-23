package com.convert.tinyurl.controller;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.*;

import com.convert.tinyurl.WebConfig;
import com.convert.tinyurl.dao.TokenRepository;
import com.convert.tinyurl.interceptor.JwtTokenInterceptor;
import com.convert.tinyurl.service.UrlService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.mockito.ArgumentMatchers.anyString;

@WebMvcTest(value = UrlController.class)
@Import(WebConfig.class)
public class UrlControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UrlService urlService;

    @MockBean
    private JwtTokenInterceptor jwtTokenInterceptor;
    
    @MockBean
    private TokenRepository tokenRepository;
 
    @Test
    public void shortenUrl_ReturnsShortUrl() throws Exception {
        String requestBody = "{\"longUrl\": \"https://example.com\", \"restrictedTimestamp\": \"2023-06-01 10:10:11\", \"domain\": \"intelli.io\", \"otp\": \"false\"}";
        String expectedShortUrl = "intelli.io/PK";
        Mockito.when(jwtTokenInterceptor.preHandle(ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.any(HttpServletResponse.class), ArgumentMatchers.any(Object.class)))
        .thenReturn(true);

        Mockito.when(urlService.shortenUrl("https://example.com", "2023-06-01 10:10:11", "intelli.io", null, false)).thenReturn(expectedShortUrl);
        mockMvc.perform(post("/tinyurl/v1/shorten")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(expectedShortUrl)));
    }

     @Test
     void testRedirectToLongUrlBody_ValidShortUrl_ReturnsFoundResponse() throws Exception {
         // Arrange
         String shortUrl = "intelli.io/Qk";
         String longUrl = "http://example.com";
         Mockito.when(jwtTokenInterceptor.preHandle(ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.any(HttpServletResponse.class), ArgumentMatchers.any(Object.class)))
         .thenReturn(true);
         Mockito.when(urlService.getLongUrl("Qk")).thenReturn(longUrl);

         // Act and Assert
         mockMvc.perform(get("/tinyurl/v1/longurl")
                 .contentType(MediaType.TEXT_PLAIN)
                 .content(shortUrl))
                 .andExpect(status().isFound())
                 .andExpect(redirectedUrl(longUrl));
     }

     @Test
     void testRedirectToLongUrlBody_InvalidShortUrl_ThrowsInvalidShortUrlException() throws Exception {
         // Arrange
         String shortUrl = "intelli.io";
         Mockito.when(jwtTokenInterceptor.preHandle(ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.any(HttpServletResponse.class), ArgumentMatchers.any(Object.class)))
         .thenReturn(true);
         // Act and Assert
         mockMvc.perform(get("/tinyurl/v1/longurl")
                 .contentType(MediaType.TEXT_PLAIN)
                 .content(shortUrl))
                 .andExpect(status().isBadRequest());
     }

     @Test
     void testRedirectToLongUrlBody_ShortUrlNotFound_ThrowsResourceNotFoundException() throws Exception {
         // Arrange
         String shortUrl = "intelli.io/PK";
         Mockito.when(jwtTokenInterceptor.preHandle(ArgumentMatchers.any(HttpServletRequest.class), ArgumentMatchers.any(HttpServletResponse.class), ArgumentMatchers.any(Object.class)))
         .thenReturn(true);
         Mockito.when(urlService.getLongUrl(anyString())).thenReturn(null);

         // Act and Assert
         mockMvc.perform(get("/tinyurl/v1/longurl")
                 .contentType(MediaType.TEXT_PLAIN)
                 .content(shortUrl))
                 .andExpect(status().isNotFound());
     }
   
}
