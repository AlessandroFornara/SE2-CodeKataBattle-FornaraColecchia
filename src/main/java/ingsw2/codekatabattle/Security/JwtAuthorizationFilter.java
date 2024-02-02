package ingsw2.codekatabattle.Security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Custom filter for JWT authorization.
 * It is responsible for intercepting HTTP requests and validating JWT tokens to establish the user's identity and authorities.
 */
@Component("JwtAuthorizationFilter")
@AllArgsConstructor
@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            String accessToken = jwtUtil.resolveToken(request);
            if (accessToken == null ) {
                filterChain.doFilter(request, response);
                return;
            }
            //System.out.println("token : "+accessToken);
            Claims claims = jwtUtil.resolveClaims(request);

            if(claims != null & jwtUtil.validateClaims(claims)){
                String username = claims.getSubject();
                String role = (String) claims.get("role");

                List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
                Authentication authentication =
                        new UsernamePasswordAuthenticationToken(username,"",authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        }catch (Exception e){
            log.error("Authentication Error");
        }
        filterChain.doFilter(request, response);
    }

}
