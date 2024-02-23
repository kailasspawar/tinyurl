package com.convert.tinyurl.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Url {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	@Column(unique=true)
	private String longUrl;

	@Column
	private String shortUrl;

	@Column(name = "restricted_timestamp")
    private LocalDateTime restrictedTimestamp;
	
	@Column(name = "password_protected")
	private String passwordProtected;

	@Column(nullable = false, columnDefinition = "boolean default false")
    private boolean otp;
	
	@Column(nullable = false, columnDefinition = "boolean default false")
    private boolean visited;

	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	public boolean isOtp() {
		return otp;
	}

	public void setOtp(boolean otp) {
		this.otp = otp;
	}

	public String getPasswordProtected() {
		return passwordProtected;
	}

	public void setPasswordProtected(String passwordProtected) {
		this.passwordProtected = passwordProtected;
	}

	public void setRestrictedTimestamp(LocalDateTime restrictedTimestamp) {
		this.restrictedTimestamp = restrictedTimestamp;
	}

	public LocalDateTime getRestrictedTimestamp() {
		return restrictedTimestamp;
	}

	public Url() {
		
	}
	
	public Url(String longUrl) {
		this.longUrl = longUrl;
	}

	public String getLongUrl() {
		return longUrl;
	}

	public void setLongUrl(String longUrl) {
		this.longUrl = longUrl;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public void setShortUrl(String shortUrl) {
		this.shortUrl = shortUrl;
	}

	public String getShortUrl() {
		return shortUrl;
	}
}
