package com.convert.tinyurl.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.convert.tinyurl.dao.UrlRepository;
import com.convert.tinyurl.exception.EndpointFailedException;
import com.convert.tinyurl.model.Url;
import com.convert.tinyurl.util.Base62Converter;
import com.convert.tinyurl.util.EncryptionUtility;

@Service
public class UrlService {
	
	@Autowired
	private UrlRepository urlRepository;
	private static final String URL_PREFIX = "intelli.io/";
	private static final String INVALID_URL = "Invalid short URL format";

	@Value("${encryption.key}")
	private String encryptionKey;

	public UrlService(UrlRepository urlRepository) {
		this.urlRepository = urlRepository;
	}

	public String shortenUrl(String longUrl, String restrictedTimestamp, String domain, String passordProtected, boolean otp) {
        // Generate a unique short URL, you can use a library like Apache Commons Codec or any other method you prefer
        try {
			Url url = new Url();
			url.setLongUrl(longUrl);
			if (restrictedTimestamp != null) {
				String formatPattern = "yyyy-MM-dd HH:mm:ss";
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatPattern);
				LocalDateTime restrictedDateTime = LocalDateTime.parse(restrictedTimestamp, formatter);
				url.setRestrictedTimestamp(restrictedDateTime);
			}
			if (passordProtected != null) {
				passordProtected = EncryptionUtility.encrypt(passordProtected, encryptionKey);
				url.setPasswordProtected(passordProtected);
			}
			url.setOtp(otp);
			Url savedUrl = urlRepository.save(url);
			//generate the short url using provided domain or default domain
			String shortUrl = generateShortUrl(savedUrl.getId(), domain);
			savedUrl.setShortUrl(shortUrl);
			urlRepository.save(savedUrl);

			return shortUrl;
		} catch (DateTimeParseException e) {
			// Handle invalid restrictedTimestamp format
			throw new IllegalArgumentException("Invalid restrictedTimestamp format. Expected format: yyyy-MM-dd HH:mm:ss", e);
		} catch (Exception e) {
			throw new EndpointFailedException("Failed to shorten URL");
		}
    }
	
	public String generateShortUrl(Integer id, String domain) {
		String base62ConvertedId = Base62Converter.encode(id);
		if (domain != null) {
			return domain + "/" + base62ConvertedId;
		}
		return URL_PREFIX + base62ConvertedId;
	}

	public String getLongUrl(String shortUrl) {
		try {
			int base62DecodedId = Base62Converter.decode(shortUrl);
			Optional<Url> urlOptional = urlRepository.findById(base62DecodedId);
	        if (urlOptional.isPresent()) {
	            Url url = urlOptional.get();
				//check if otp is enabled and not visited
				if (url.isOtp() && !url.isVisited()) {
					url.setVisited(true);
					urlRepository.save(url);
				}
	            return url.getLongUrl();
	        } else {
	            return null;
	        }
		} catch (NumberFormatException ex) {
            throw new InvalidShortUrlException(INVALID_URL);
        }
	}
	
	// Custom exception class for invalid short URL
    private static class InvalidShortUrlException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public InvalidShortUrlException(String message) {
            super(message);
        }
    }

    public LocalDateTime getRestrictedTimestamp(String shortId) {
        try {
			int base62DecodedId = Base62Converter.decode(shortId);
			Optional<Url> urlOptional = urlRepository.findById(base62DecodedId);
	        if (urlOptional.isPresent()) {
	            Url url = urlOptional.get();
	            return url.getRestrictedTimestamp();
	        } else {
	            return null;
	        }
		} catch (NumberFormatException ex) {
            throw new InvalidShortUrlException(INVALID_URL);
        }
    }

	public boolean isPasswordProtectedMatched(String shortId, String passwordProtected) throws Exception {
		try {
			int base62DecodedId = Base62Converter.decode(shortId);
			Optional<Url> urlOptional = urlRepository.findById(base62DecodedId);
	        if (urlOptional.isPresent()) {
	            Url url = urlOptional.get();
	            String passString = url.getPasswordProtected();
				if (passString == null) {
					return false;
				}
				passString = EncryptionUtility.decrypt(passString, encryptionKey);
				return passString.equals(passwordProtected);
	        } else {
	            return false;
	        }
		} catch (NumberFormatException ex) {
			throw new InvalidShortUrlException(INVALID_URL);
		}
	}

	public boolean isOtpProtectedAndVisited(String shortId) {
		try {
			int base62DecodedId = Base62Converter.decode(shortId);
			Optional<Url> urlOptional = urlRepository.findById(base62DecodedId);
	        if (urlOptional.isPresent()) {
	            Url url = urlOptional.get();
	            return url.isOtp() && url.isVisited(); //return true if url is otp protected and already visited
	        } else {
	            return false;
	        }
		} catch (NumberFormatException ex) {
			throw new InvalidShortUrlException(INVALID_URL);
		}
	}
}
