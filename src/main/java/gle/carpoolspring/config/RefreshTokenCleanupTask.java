package gle.carpoolspring.config;

import gle.carpoolspring.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class RefreshTokenCleanupTask {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    // Runs every day at 1 AM
    @Scheduled(cron = "0 0 1 * * ?")
    public void cleanUpExpiredRefreshTokens() {
        Instant now = Instant.now();
        refreshTokenRepository.deleteByExpiryDateBefore(now);
        System.out.println("Expired refresh tokens cleaned up at " + now);
    }
}