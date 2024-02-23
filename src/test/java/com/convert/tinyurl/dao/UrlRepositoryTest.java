package com.convert.tinyurl.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import com.convert.tinyurl.model.Url;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Transactional
@Rollback
public class UrlRepositoryTest {
    @Autowired
    private UrlRepository urlRepository;

    @Test
    public void saveUrl_Success() {
        // Create a Url object
        Url url = new Url();
        url.setLongUrl("https://www.example.com");

        // Save the Url object to the repository
        Url savedUrl = urlRepository.save(url);

        // Verify that the Url object is saved and assigned an ID
        Assertions.assertNotNull(savedUrl.getId());
    }
}
