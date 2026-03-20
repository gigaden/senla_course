package ebookstore.security.controller;

import ebookstore.security.dto.LoginRequest;
import ebookstore.security.dto.LoginResponse;
import ebookstore.security.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер обрабатывает авторизацию
 * */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = authenticationService.login(request.username(), request.password());
        LoginResponse response = new LoginResponse(token);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
