package ebookstore.security.filter;

import ebookstore.model.Client;
import ebookstore.repository.ClientRepository;
import ebookstore.security.jwt.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Фильтр для проверки токена в запросах
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final ClientRepository clientRepository;
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter(JwtService jwtService, ClientRepository clientRepository) {
        this.jwtService = jwtService;
        this.clientRepository = clientRepository;
        log.info("JwtAuthenticationFilter bean created");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        log.info("Попали в фильтр security");

        try {
            String header = request.getHeader("Authorization");
            if (header == null || !header.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }
            String token = header.substring(7);
            String username = jwtService.extractUserName(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                Client client = clientRepository.findClientByUserName(username).orElse(null);
                if (client != null && jwtService.isTokenValid(token, client)) {
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(client, null,
                                    List.of(new SimpleGrantedAuthority(client.getRole().name())));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } else {
                    log.warn("Аутентификация не установлена: client null или невалидный токен");
                }
            }
        } catch (Exception e) {
            log.error("Ошибка jwt токена: {}", e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }
}
