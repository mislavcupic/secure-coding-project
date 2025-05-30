package hr.algebra.semregprojectbackend.filter;

import hr.algebra.semregprojectbackend.service.JwtService;
import hr.algebra.semregprojectbackend.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@AllArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsServiceImpl;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        log.debug("[JWT FILTER] Authorization header: {}", authHeader);
        log.debug("[JWT FILTER] Filter triggered for URI: {}", request.getRequestURI());

        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            log.debug("[JWT FILTER] Token extracted: {}", token);

            try {
                username = jwtService.extractUsername(token);
                log.debug("[JWT FILTER] Username extracted from token: {}", username);
            } catch (Exception e) {
                log.warn("[JWT FILTER] Failed to extract username from token", e);
            }
        } else {
            log.debug("[JWT FILTER] No Bearer token found in Authorization header");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);
            boolean isValid = false;
            try {
                isValid = jwtService.validateToken(token, userDetails);
                log.debug("[JWT FILTER] Token valid: {}", isValid);
            } catch (Exception e) {
                log.warn("[JWT FILTER] Token validation failed", e);
            }

            if (isValid) {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                log.debug("[JWT FILTER] Security context updated with authentication");
            } else {
                log.debug("[JWT FILTER] Token invalid or expired");
            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.equals("/auth/api/v1/login") || path.equals("/auth/api/v1/refreshToken");
    }
}
