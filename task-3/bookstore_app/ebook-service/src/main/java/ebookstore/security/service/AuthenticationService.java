package ebookstore.security.service;

import ebookstore.exception.notfound.ClientLoginException;
import ebookstore.exception.notfound.ClientNotFoundException;
import ebookstore.model.Client;
import ebookstore.repository.ClientRepository;
import ebookstore.security.jwt.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Сервис занимается аутентификацией пользователя и выдачей токена
 */
@Service
public class AuthenticationService {

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthenticationService(ClientRepository clientRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * Метод проверяет логин и пароль пользователя и возвращает токен
     *
     * @param username - логин пользователя
     * @param password - пароль пользователя
     * @return - токен
     */
    @Transactional(readOnly = true)
    public String login(String username, String password) {
        Client client = clientRepository.findClientByUserName(username)
                .orElseThrow(() -> new ClientNotFoundException("Клиент не найден"));

        if (!passwordEncoder.matches(password, client.getPassword())) {
            throw new ClientLoginException("Неверный пароль");
        }

        return jwtService.generateToken(client);
    }
}
