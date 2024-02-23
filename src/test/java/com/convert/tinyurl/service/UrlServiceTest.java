package com.convert.tinyurl.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import com.convert.tinyurl.dao.UrlRepository;
import com.convert.tinyurl.model.Url;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {
    @Mock
    private UrlRepository urlRepository;

    private UrlService urlService;

//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.initMocks(this);
//        urlService = new UrlService(urlRepository);
//    }
    
	@BeforeEach
    void setUp() {
        urlService = new UrlService(urlRepository);
    }

    @Test
    void shortenUrl_ReturnsShortUrl() {
        String longUrl = "https://www.example.com";
        Integer id = 1;
        String expectedShortUrl = "intelli.io/B";
        String domain = "intelli.io";
        Url savedUrl = new Url();
        savedUrl.setId(id);
        Mockito.when(urlRepository.save(Mockito.any(Url.class))).thenReturn(savedUrl);

        String shortUrl = urlService.shortenUrl(longUrl, null, domain, null, false);

        Assertions.assertEquals(expectedShortUrl, shortUrl);
    }

    @Test
    void generateShortUrl_ReturnsShortUrl() {
        Integer id = 1;
        String expectedShortUrl = "intelli.io/B";

        String shortUrl = urlService.generateShortUrl(id, "intelli.io");

        Assertions.assertEquals(expectedShortUrl, shortUrl);
    }
}
