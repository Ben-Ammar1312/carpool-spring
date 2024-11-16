package gle.carpoolspring.service;

import com.twilio.Twilio;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import gle.carpoolspring.models.User;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwilioVerifyService {

    @Value("${twilio.accountSid}")
    private String accountSid;

    @Value("${twilio.authToken}")
    private String authToken;


    @Value("${twilio.verifyServiceSid}")
    private String verifyServiceSid;
    @PostConstruct
    public void initTwilio() {
        // Initialize Twilio in the constructor
        Twilio.init(accountSid, authToken);
    }

    /**
     * Send SMS verification code to the user's phone number.
     */
    public void sendVerificationCode(String phoneNumber) {
        Verification.creator(verifyServiceSid, phoneNumber, "sms").create();
    }

    /**
     * Check the SMS verification code sent to the user.
     */
    public boolean checkSmsVerification(User user, String code) {
        VerificationCheck verificationCheck = VerificationCheck.creator(verifyServiceSid,code)
                .setTo(user.getTelephone()) // Assuming user's phone number is stored in "telephone"
                .create();

        boolean isVerified = "approved".equals(verificationCheck.getStatus());
        if (isVerified) {
            user.setSmsVerified(true); // Update the user's SMS verification status
        }
        return isVerified;
    }
}