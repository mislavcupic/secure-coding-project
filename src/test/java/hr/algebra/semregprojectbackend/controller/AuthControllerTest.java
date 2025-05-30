package hr.algebra.semregprojectbackend.controller;

import hr.algebra.semregprojectbackend.domain.RefreshToken;
import hr.algebra.semregprojectbackend.dto.AuthRequestDTO;
import hr.algebra.semregprojectbackend.dto.JwtResponseDTO;
import hr.algebra.semregprojectbackend.service.JwtService;
import hr.algebra.semregprojectbackend.service.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

   @InjectMocks
   private AuthController authController;

   @Mock
   private AuthenticationManager authenticationManager;

   @Mock
   private JwtService jwtService;

   @Mock
   private RefreshTokenService refreshTokenService;

   @BeforeEach
   void setUp() {
      MockitoAnnotations.openMocks(this);
   }

   @Test
   void testAuthenticateAndGetToken_Success() {
      String username = "testUser";
      String password = "testPassword";
      String accessToken = "access-token";
      String refreshTokenStr = "refresh-token";

      AuthRequestDTO requestDTO = new AuthRequestDTO(username, password);

      RefreshToken mockRefreshToken = RefreshToken.builder()
              .token(refreshTokenStr)
              .build();

      // Mockiramo da autentifikacija vraća neki Authentication objekt
      when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
              .thenReturn(mock(UsernamePasswordAuthenticationToken.class));

      when(jwtService.generateToken(username)).thenReturn(accessToken);
      when(refreshTokenService.createRefreshToken(username)).thenReturn(mockRefreshToken);

      JwtResponseDTO response = authController.authenticateAndGetToken(requestDTO);

      assertNotNull(response);
      assertEquals(accessToken, response.getAccessToken());
      assertEquals(refreshTokenStr, response.getToken());
   }

   @Test
   void testAuthenticateAndGetToken_Failure() {
      AuthRequestDTO requestDTO = new AuthRequestDTO("invalidUser", "wrongPassword");

      when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
              .thenThrow(new BadCredentialsException("Invalid credentials"));

      BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
         authController.authenticateAndGetToken(requestDTO);
      });

      assertEquals("Invalid username or password", exception.getMessage());

      verify(jwtService, never()).generateToken(any());
      verify(refreshTokenService, never()).createRefreshToken(any());
   }

   @Test
   void testAuthenticateAndGetToken_NullUsername() {
      AuthRequestDTO requestDTO = new AuthRequestDTO(null, "password");

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
         authController.authenticateAndGetToken(requestDTO);
      });

      assertEquals("Username and password must be provided", exception.getMessage());

      verify(authenticationManager, never()).authenticate(any());
      verify(jwtService, never()).generateToken(any());
      verify(refreshTokenService, never()).createRefreshToken(any());
   }

   @Test
   void testAuthenticateAndGetToken_NullPassword() {
      AuthRequestDTO requestDTO = new AuthRequestDTO("username", null);

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
         authController.authenticateAndGetToken(requestDTO);
      });

      assertEquals("Username and password must be provided", exception.getMessage());

      verify(authenticationManager, never()).authenticate(any());
      verify(jwtService, never()).generateToken(any());
      verify(refreshTokenService, never()).createRefreshToken(any());
   }
}
