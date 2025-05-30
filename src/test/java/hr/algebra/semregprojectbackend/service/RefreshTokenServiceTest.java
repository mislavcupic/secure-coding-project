package hr.algebra.semregprojectbackend.service;

import hr.algebra.semregprojectbackend.domain.RefreshToken;
import hr.algebra.semregprojectbackend.domain.UserInfo;
import hr.algebra.semregprojectbackend.exception.TokenExpiredException;
import hr.algebra.semregprojectbackend.repository.RefreshTokenRepository;
import hr.algebra.semregprojectbackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RefreshTokenServiceTest {

    private RefreshTokenRepository refreshTokenRepository;
    private UserRepository userRepository;
    private RefreshTokenService refreshTokenService;

    @BeforeEach
    void setup() {
        refreshTokenRepository = Mockito.mock(RefreshTokenRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        refreshTokenService = new RefreshTokenService(refreshTokenRepository, userRepository);
    }

    @Test
    void createRefreshToken_returnsSavedToken() {
        UserInfo user = new UserInfo();
        user.setUsername("testuser");

        Mockito.when(userRepository.findByUsername("testuser")).thenReturn(user);

        RefreshToken token = RefreshToken.builder()
                .userInfo(user)
                .token("token123")
                .expiryDate(Instant.now().plusMillis(600000))
                .build();

        Mockito.when(refreshTokenRepository.save(Mockito.any())).thenReturn(token);

        RefreshToken created = refreshTokenService.createRefreshToken("testuser");

        assertThat(created).isNotNull();
        assertThat(created.getUserInfo().getUsername()).isEqualTo("testuser");
    }

    @Test
    void findByToken_returnsOptionalToken() {
        RefreshToken token = new RefreshToken();
        token.setToken("token123");

        Mockito.when(refreshTokenRepository.findByToken("token123"))
                .thenReturn(Optional.of(token));

        Optional<RefreshToken> found = refreshTokenService.findByToken("token123");

        assertThat(found).isPresent();
        assertThat(found.get().getToken()).isEqualTo("token123");
    }

    @Test
    void verifyExpiration_throwsExceptionWhenExpired() {
        RefreshToken expiredToken = RefreshToken.builder()
                .expiryDate(Instant.now().minusSeconds(10))
                .build();

        Mockito.doNothing().when(refreshTokenRepository).delete(expiredToken);

        assertThrows(TokenExpiredException.class, () -> {
            refreshTokenService.verifyExpiration(expiredToken);
        });

        Mockito.verify(refreshTokenRepository).delete(expiredToken);
    }

    @Test
    void verifyExpiration_returnsTokenWhenNotExpired() {
        RefreshToken validToken = RefreshToken.builder()
                .expiryDate(Instant.now().plusSeconds(1000))
                .build();

        RefreshToken result = refreshTokenService.verifyExpiration(validToken);

        assertThat(result).isEqualTo(validToken);
    }
}
