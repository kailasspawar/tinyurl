package com.convert.tinyurl.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.convert.tinyurl.model.Url;

@Repository
public interface UrlRepository extends JpaRepository<Url, Integer> {
	Optional<Url> findById(Integer id);
}
