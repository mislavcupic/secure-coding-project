package hr.algebra.semregprojectbackend.service;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    // Ovo je 256-bitni Base64URL key za test (32 bytes key)
    private final String testSecret = "h3mEj8mX4vEb9uyT3FYrRQVh8lWhu15cpkQ3JIdt2X0";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        jwtService.setSecret(testSecret);
    }

    @Test
    void testGenerateTokenAndExtractUsername() {
        String username = "testUser";
        String token = jwtService.generateToken(username);

        assertNotNull(token);

        String extractedUsername = jwtService.extractUsername(token);
        assertEquals(username, extractedUsername);
    }

    @Test
    void testTokenValidation_success() {
        String username = "validUser";
        String token = jwtService.generateToken(username);

        UserDetails userDetails = Mockito.mock(UserDetails.class);
        Mockito.when(userDetails.getUsername()).thenReturn(username);

        assertTrue(jwtService.validateToken(token, userDetails));
    }

    @Test
    void testTokenValidation_invalidUsername() {
        String token = jwtService.generateToken("someUser");

        UserDetails userDetails = Mockito.mock(UserDetails.class);
        Mockito.when(userDetails.getUsername()).thenReturn("otherUser");

        assertFalse(jwtService.validateToken(token, userDetails));
    }

    @Test
    void testTokenIsExpiredImmediately() {
        String username = "expiredUser";

        // Datum isteka je 1 minuta u prošlosti
        Date expiredDate = new Date(System.currentTimeMillis() - 60_000);
        String token = jwtService.generateTokenWithExpiration(username, expiredDate);

        UserDetails userDetails = Mockito.mock(UserDetails.class);
        Mockito.when(userDetails.getUsername()).thenReturn(username);

        boolean isValid;
        try {
            isValid = jwtService.validateToken(token, userDetails);
        } catch (ExpiredJwtException e) {
            isValid = false;
        }

        assertFalse(isValid);
    }

    @Test
    void testExtractExpiration() {
        String username = "expTest";
        Date expirationDate = new Date(System.currentTimeMillis() + 5000); // 5 sekundi od sad
        String token = jwtService.generateTokenWithExpiration(username, expirationDate);

        Date extractedExp = jwtService.extractExpiration(token);

        // Ekstraktirani expiration treba biti približno jednak našem expirationDate (do neke male razlike)
        long diff = Math.abs(extractedExp.getTime() - expirationDate.getTime());
        assertTrue(diff < 1000); // unutar 1 sekunde razlike
    }
}

