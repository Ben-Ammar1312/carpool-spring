package gle.carpoolspring.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gle.carpoolspring.model.User;
import gle.carpoolspring.model.VerificationToken;
import gle.carpoolspring.repository.UserRepository;
import gle.carpoolspring.repository.VerificationTokenRepository;

@Service
public class VerificationService {

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TwilioVerifyService twilioVerifyService;

    /**
     * Send an email verification token to the user's email.
     */
    public void sendVerificationEmail(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(LocalDateTime.now().plusDays(1));
        tokenRepository.save(verificationToken);

        String verificationUrl = "http://localhost:8080/verify?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Email Verification");
        message.setText("Please verify your email by clicking the link: " + verificationUrl);
        mailSender.send(message);
    }

    /**
     * Verify the email verification token.
     */
    @Transactional
    public boolean verifyEmailToken(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token).orElse(null);
        if (verificationToken == null || verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return false; // Token is invalid or expired
        }
        User user = verificationToken.getUser();
        user.setEmailVerified(true); // Set emailVerified to true
        checkAndEnableUser(user); // Check if both verifications are complete
        userRepository.save(user); // Save the user
        tokenRepository.delete(verificationToken); // Remove the token after use
        return true;
    }

    /**
     * Send an SMS verification code using Twilio.
     */
    public void sendVerificationSms(User user) {
        twilioVerifyService.sendVerificationCode(user.getTelephone());
    }

    /**
     * Verify the SMS verification code using Twilio.
     */
    @Transactional
    public boolean verifySms(String phoneNumber, String code) {
        User user = userRepository.findByTelephone(phoneNumber)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isVerified = twilioVerifyService.checkSmsVerification(user, code);
        if (isVerified) {
            user.setSmsVerified(true); // Update SMS verification status
            checkAndEnableUser(user); // Check if both verifications are complete
            userRepository.save(user); // Save the user
        }
        return isVerified;
    }

    /**
     * Enable the user if both email and SMS verifications are complete.
     */
    private void checkAndEnableUser(User user) {
        if (user.isEmailVerified() && user.isSmsVerified()) {
            user.setEnabled(true); // Enable the user only if both verifications are true
        }
    }
}