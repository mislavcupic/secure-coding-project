package hr.algebra.semregprojectbackend.controller;

import hr.algebra.semregprojectbackend.domain.RefreshToken;
import hr.algebra.semregprojectbackend.dto.AuthRequestDTO;
import hr.algebra.semregprojectbackend.dto.JwtResponseDTO;
import hr.algebra.semregprojectbackend.dto.RefreshTokenRequestDTO;
import hr.algebra.semregprojectbackend.service.JwtService;
import hr.algebra.semregprojectbackend.service.RefreshTokenService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException; // <-- DODAJ OVO!
import org.springframework.security.authentication.BadCredentialsException; // <-- DODAJ OVO! (opcionalno, ali dobra praksa)
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("auth")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private AuthenticationManager authenticationManager;

    private JwtService jwtService;

    private RefreshTokenService refreshTokenService;

    @PostMapping("/api/v1/login")
    public JwtResponseDTO authenticateAndGetToken(@RequestBody AuthRequestDTO authRequestDTO) {
        if (authRequestDTO.getUsername() == null || authRequestDTO.getPassword() == null) {
            throw new IllegalArgumentException("Username and password must be provided");
        }

        try {
            // Pokušaj autentifikacije. Ako ne uspije, AuthenticationManager će baciti iznimku
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequestDTO.getUsername(), authRequestDTO.getPassword())
            );
        } catch (AuthenticationException e) {
            // Logiraj upozorenje i baci generičku BadCredentialsException
            logger.warn("Authentication failed for user {}: {}", authRequestDTO.getUsername(), e.getMessage());
            throw new BadCredentialsException("Invalid username or password");
        }

        // Ako je autentifikacija uspjela, generiraj tokene
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(authRequestDTO.getUsername());

        return JwtResponseDTO.builder()
                .accessToken(jwtService.generateToken(authRequestDTO.getUsername()))
                .token(refreshToken.getToken())
                .build();
    }


    @PostMapping("/api/v1/refreshToken")
    public JwtResponseDTO refreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO){
        return refreshTokenService.findByToken(refreshTokenRequestDTO.getToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUserInfo)
                .map(userInfo -> {
                    String accessToken = jwtService.generateToken(userInfo.getUsername());
                    return JwtResponseDTO.builder()
                            .accessToken(accessToken)
                            .token(refreshTokenRequestDTO.getToken()).build();
                }).orElseThrow(() ->new RuntimeException("Refresh Token is not in DB..!!"));
    }

    @PostMapping("/api/v1/logout")
    public void logout() {
        logger.info("logout called...");
    }
}