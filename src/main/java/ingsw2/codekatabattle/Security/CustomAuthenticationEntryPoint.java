package ingsw2.codekatabattle.Security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Custom implementation of AuthenticationEntryPoint for the Code Kata Battle application.
 * This class is used to handle authentication errors during the Spring Security process.
 * It customizes the response sent to clients when authentication fails, for instance, due to expired or invalid authentication credentials.
 */
@Component("CustomAuthenticationEntryPoint")
@AllArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper mapper;
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.TEXT_PLAIN_VALUE);
        response.setCharacterEncoding("UTF-8");
        String text = "Authentication Error: Your session has expired. " +
                "Please login to continue enjoying CKB";
        mapper.writeValue(response.getWriter(), text);
    }
}
