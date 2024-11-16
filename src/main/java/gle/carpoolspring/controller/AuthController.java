package gle.carpoolspring.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Collections;

import gle.carpoolspring.service.TwilioVerifyService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import gle.carpoolspring.models.Role;
import gle.carpoolspring.models.User;
import gle.carpoolspring.repository.RoleRepository;
import gle.carpoolspring.repository.UserRepository;
import gle.carpoolspring.service.VerificationService;

import jakarta.validation.Valid;

@Controller
public class AuthController {

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private VerificationService verificationService;
    @Autowired
    private TwilioVerifyService twilioVerifyService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User()); // Binding directly to User
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                               BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("error", "Email is already in use!");
            model.addAttribute("user", user);
            return "register";
        }

        // Encode the password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Handle file upload
        if (!user.getPhotoFile().isEmpty()) {
            String fileName = user.getPhotoFile().getOriginalFilename();
            String uploadDir = "user-photos/"; // Define where to store the file

            // Save the file locally (or in a specific directory on the server)
            try {
                Path filePath = Paths.get(uploadDir + fileName);
                Files.createDirectories(filePath.getParent());
                Files.copy(user.getPhotoFile().getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // Set the file path to the user entity
                user.setPhoto(uploadDir + fileName);
            } catch (IOException e) {
                model.addAttribute("error", "File upload failed!");
                model.addAttribute("user", user);
                return "register";
            }
        }

        // Assign default role
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        user.setRoles(Collections.singleton(userRole));

        // Save the user
        userRepository.save(user);

        httpSession.setAttribute("userIdForSmsVerification", user.getId_user());
        twilioVerifyService.sendVerificationCode(user.getTelephone());
        // Send verification email (if implemented)
        verificationService.sendVerificationEmail(user);

        redirectAttributes.addFlashAttribute("success", "Registration successful! Please check your email to verify your account. and verify your number by entering the sent sms");
        return "redirect:/verify-sms";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String showDashboard() {
        return "dashboard";
    }

    @GetMapping("/verify")
    public String verifyEmail(@RequestParam("token") String token, Model model) {
        boolean isVerified = verificationService.verifyEmailToken(token);
        if (isVerified) {
            model.addAttribute("success", "Email verified successfully! Please verify your SMS to activate your account.");
            return "verify-sms";
        } else {
            model.addAttribute("error", "Invalid or expired verification token.");
            model.addAttribute("user", new User());
            return "register";
        }
    }

    @PostMapping("/verify-sms")
    public String verifySms(@RequestParam("code") String code, Model model) {
        Integer userId = (Integer) httpSession.getAttribute("userIdForSmsVerification");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isVerified = verificationService.verifySms(user.getTelephone(), code);
        if (isVerified) {
            model.addAttribute("success", "SMS verified successfully! You can now log in.");
            return "login";
        } else {
            model.addAttribute("error", "Invalid SMS verification code.");
            return "verify-sms";
        }
    }

    @GetMapping("/verify-sms")
    public String showVerifySmsPage(Model model) {
        return "verify-sms";
    }
}