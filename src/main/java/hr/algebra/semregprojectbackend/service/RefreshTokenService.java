package hr.algebra.semregprojectbackend.service;


import hr.algebra.semregprojectbackend.domain.RefreshToken;
import hr.algebra.semregprojectbackend.domain.UserInfo;
import hr.algebra.semregprojectbackend.exception.TokenExpiredException;
import hr.algebra.semregprojectbackend.repository.RefreshTokenRepository;
import hr.algebra.semregprojectbackend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    final
    RefreshTokenRepository refreshTokenRepository;

    final
    UserRepository userRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    public RefreshToken createRefreshToken(String username){
        UserInfo userInfo = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        RefreshToken refreshToken = RefreshToken.builder()
                .userInfo(userInfo)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(600000)) // 10 minuta
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token){
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token){
        if(token.getExpiryDate().compareTo(Instant.now())<0){
            refreshTokenRepository.delete(token);
            throw new TokenExpiredException();
        }
        return token;
    }

}