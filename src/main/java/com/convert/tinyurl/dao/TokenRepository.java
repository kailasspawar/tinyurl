package com.convert.tinyurl.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.convert.tinyurl.model.Token;

@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {
    Token findByUsername(String username);
	Token findByTokenString(String token);
	Object save(Class<Token> class1);
}
