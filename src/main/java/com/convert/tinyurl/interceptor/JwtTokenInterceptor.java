package com.convert.tinyurl.interceptor;

import java.io.IOException;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.convert.tinyurl.dao.TokenRepository;
import com.convert.tinyurl.model.Token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtTokenInterceptor implements HandlerInterceptor {

	@Autowired
    private TokenRepository tokenRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;
    
    public JwtTokenInterceptor(TokenRepository tokenRepository) {
		this.tokenRepository = tokenRepository;
	}

    private boolean handleResponse(HttpServletResponse response, String message) throws IOException{
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(message); // Set your custom error message here
        
        return false;
    }

	@Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String token = getTokenFromRequestHeader(request);
        try {
            Date expirationDate = getExpirationDateFromToken(token);

            // Retrieve the token from the database
            Token tokenEntity = tokenRepository.findByTokenString(token);

            if (tokenEntity == null) {
                return handleResponse(response, "Invalid token");
            }

            if (expirationDate.before(new Date())) {
                return handleResponse(response, "Token has expired");
            }
        
        } catch (Exception e) {
            return handleResponse(response, "Token has expired");
        }

        // Token is valid, allow the request to proceed
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        // No post-processing needed
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception ex) throws Exception {
        // No post-processing needed
    }

    private String getTokenFromRequestHeader(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        throw new UnauthorizedException("Authorization header token not passed...!!");
    }
    
    private Date getExpirationDateFromToken(String token) {
    	SecretKey secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
      
        Jws<Claims> claimsJws = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);

        Claims claims = claimsJws.getBody();
        return claims.getExpiration();
    }
    
    public class UnauthorizedException extends RuntimeException {

		private static final long serialVersionUID = 1L;

		public UnauthorizedException(String message) {
            super(message);
        }

        public UnauthorizedException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
