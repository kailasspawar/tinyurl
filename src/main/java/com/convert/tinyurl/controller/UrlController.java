package com.convert.tinyurl.controller;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.convert.tinyurl.exception.CustomAccessDeniedException;
import com.convert.tinyurl.service.UrlService;

@RestController
@RequestMapping("tinyurl/v1")
public class UrlController {
    
    @Autowired
    private UrlService urlService;

    @Value("${domain.values}")
    private String[] domainValues;

    @PostMapping("/shorten")
    /**
     * 
     * @param requestBody
     * restrictedTimestamp: 2023-05-23 10:11:11
     * @return
     */
    public String shortenUrl(@RequestBody Map<String, String> requestBody) {
        String longUrl = requestBody.get("longUrl");
        String  restrictedTimestamp = requestBody.get("restrictedTimestamp");
        String domain = requestBody.get("domain");
        String passordProtected = requestBody.get("passwordProtected");
        boolean otp = requestBody.get("otp") != null && "true".equals(requestBody.get("otp"));
        if (domain != null && domainValues != null && domainValues.length > 0 &&  (!Arrays.asList(domainValues).contains(domain))) {
            throw new CustomAccessDeniedException("Invalid domain");   
        }
        String shortUrl = urlService.shortenUrl(longUrl, restrictedTimestamp, domain, passordProtected, otp);
        return "Short URL: " + shortUrl;
    }
    
    @GetMapping("/longurl/{shortUrl}")
    public ResponseEntity<Void> redirectToLongUrl(@PathVariable("shortUrl") String shortUrl) {
        String longUrl = urlService.getLongUrl(shortUrl);
        if (longUrl != null) {
            return ResponseEntity.status(HttpStatus.FOUND).header("Location", longUrl).build();
        } else {
            throw new ResourceNotFoundException("Short URL not found");
        }
    }

    @GetMapping("/longurl")
    public ResponseEntity<Void> redirectToLongUrlBody(@RequestParam(value = "password_protected", required = false) String passwordProtected, @RequestBody String shortUrl) throws Exception {
        String[] urlParts = shortUrl.split("/");
        if (urlParts.length != 2) {
            throw new InvalidShortUrlException("Invalid short URL format");
        }
        String shortId = urlParts[1];
        //check if it is otp protected
        boolean isVisited = urlService.isOtpProtectedAndVisited(shortId);
        if (isVisited) {
            throw new CustomAccessDeniedException("This link is opt-protected and has already been visited.");
        }
        // Check if the password is matched
        boolean isPasswordProtectedMatched = urlService.isPasswordProtectedMatched(shortId, passwordProtected);
        if (passwordProtected != null && !isPasswordProtectedMatched) {
            throw new CustomAccessDeniedException("Access to this endpoint is restricted, password not matched");
        }
         // Retrieve the additional timestamp from the database
        LocalDateTime restrictedTimestamp = urlService.getRestrictedTimestamp(shortId);

        // Check if the current time is beyond the restricted timestamp
        LocalDateTime currentTime = LocalDateTime.now();
        if (restrictedTimestamp != null && currentTime.isAfter(restrictedTimestamp)) {
           throw new CustomAccessDeniedException("Access to this endpoint is restricted after the specified timestamp "+restrictedTimestamp);
       }

        String longUrl = urlService.getLongUrl(shortId);
        if (longUrl != null) {
            return ResponseEntity.status(HttpStatus.FOUND).header("Location", longUrl).build();
        } else {
            throw new ResourceNotFoundException("Short URL not found");
        }
    }

    @ExceptionHandler(InvalidShortUrlException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleInvalidShortUrlException(InvalidShortUrlException ex) {
        return ex.getMessage();
    }

    // Custom exception class for invalid short URL
    private static class InvalidShortUrlException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public InvalidShortUrlException(String message) {
            super(message);
        }
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ex.getMessage();
    }
    
    // Custom exception class for resource not found
    private static class ResourceNotFoundException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public ResourceNotFoundException(String message) {
            super(message);
        }
    }


    //check if given number is prime or not
    public static boolean isPrime(int n) {
        if (n <= 1) {
            return false;
        }
        //check from 2 to n-1
        for (int i = 2; i < n; i++) {
            if (n % i == 0) {
                return false; //not prime
            }
        }
        return true; //prime
    }
    //optimized solution to find prime number
    public static boolean isPrimeOptimized(int n) {
        if (n <= 1) {
            return false;
        }
        //check from 2 to n/2
        for (int i = 2; i <= n/2; i++) {
            if (n % i == 0) {
                return false; //not prime
            }
        }
        return true; //prime
    }
    //optimized solution to find prime number
    public static boolean isPrimeOptimized2(int n) {
        if (n <= 1) {
            return false;
        }
        //check from 2 to sqrt(n)
        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) {
                return false; //not prime
            }
        }
        return true; //prime
    }
}
