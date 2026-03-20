package ebookstore.security.jwt;

import ebookstore.model.Client;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Сервис генерации токена
 */
@Service
public class JwtService {

    @Value("${jwt.secret_key}")
    private String SECRET_KEY;

    /**
     * Метод генерирует токен для клиента
     *
     * @param client - клиент
     * @return - сгенерированный токен
     */
    public String generateToken(Client client) {
        return Jwts.builder()
                .setSubject(client.getUsername())
                .claim("role", client.getRole())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                .compact();
    }

    /**
     * Метод получает имя пользователя из токена
     *
     * @param token - токен
     * @return - имя пользователя
     */
    public String extractUserName(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Метод проверяет валидность токена
     *
     * @param token  - токен
     * @param client - клиент(пользователь)
     */
    public boolean isTokenValid(String token, Client client) {
        String username = extractUserName(token);

        return username.equals(client.getUsername()) && !isTokenExpired(token);
    }

    /**
     * Метод проверяет не истёк ли токен
     *
     * @param token - токен
     */
    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();

        return expiration.before(new Date());
    }
}
