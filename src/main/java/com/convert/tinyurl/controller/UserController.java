package com.convert.tinyurl.controller;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.convert.tinyurl.dao.TokenRepository;
import com.convert.tinyurl.model.Token;
import com.convert.tinyurl.model.User;
import com.convert.tinyurl.service.UserService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@RestController
@RequestMapping("tinyurl/v1")
public class UserController {
	
	@Autowired
	private UserService userService;
    
    @Autowired
    private TokenRepository tokenRepository;
    
    //get the jwt properties from application.properties
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpiration;

	@GetMapping("/hello")
	public String hello() {
		return "hello from spring boot";
	}

    @PostMapping("/login")
    public String login(@RequestBody User user) {
        if (userService.isValidUser(user.getUsername(), user.getPassword())) {
            LocalDateTime currentTime = LocalDateTime.now();
            LocalDateTime expirationTime = currentTime.plusMinutes(jwtExpiration);
            SecretKey secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());

            String token = Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(Date.from(expirationTime.atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(secretKey)
                .compact();
            
             // Save the token to the database
            Token tokenEntity = new Token();
            tokenEntity.setUsername(user.getUsername());
            tokenEntity.setTokenString(token);
            tokenRepository.save(tokenEntity);
            
            return token;
        } else {
            return "Invalid username or password";
        }
    }

}
