package gle.carpoolspring.service;

import gle.carpoolspring.model.RefreshToken;
import gle.carpoolspring.model.User;
import gle.carpoolspring.repository.RefreshTokenRepository;
import gle.carpoolspring.repository.UserRepository;
import gle.carpoolspring.exception.TokenRefreshException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    @Value("${refresh.token.expirationMs}")
    private Long refreshTokenDurationMs;
@Autowired
    private  RefreshTokenRepository refreshTokenRepository;
@Autowired
private  UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(RefreshTokenService.class);



    @Transactional
    public RefreshToken createRefreshToken(int userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Delete all existing refresh tokens for the user
        logger.debug("Attempting to delete existing refresh tokens for user {}", userId);
        refreshTokenRepository.deleteByUserId(userId);
        refreshTokenRepository.flush();
        logger.debug("Deleted existing refresh tokens for user {}", userId);
        Optional<RefreshToken> existingToken = refreshTokenRepository.findByUser(user);
        if (existingToken.isPresent()) {
            logger.warn("Existing refresh token still present for user {} after deletion!", userId);
        }
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());
        RefreshToken savedToken=refreshTokenRepository.save(refreshToken);
        logger.debug("Created new refresh token for user {}: {}", userId, savedToken.getToken());

        return savedToken;
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Transactional
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
        }

        return token;
    }

    @Transactional
    public int deleteByToken(String token) {
        Optional<RefreshToken> refreshTokenOpt = refreshTokenRepository.findByToken(token);
        if (refreshTokenOpt.isPresent()) {
            refreshTokenRepository.delete(refreshTokenOpt.get());
            logger.debug("Deleted refresh token: {}", token);
            return 1;
        }
        return 0;
    }
}
