package gle.carpoolspring.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import gle.carpoolspring.jwt.JwtUtils;
import gle.carpoolspring.request.JwtResponse;
import gle.carpoolspring.request.LoginRequest;
import gle.carpoolspring.request.MessageResponse;
import gle.carpoolspring.request.SignupRequest;
import gle.carpoolspring.service.RefreshTokenService;
import gle.carpoolspring.service.TwilioVerifyService;
import gle.carpoolspring.service.UserDetailsImp;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import gle.carpoolspring.model.Role;
import gle.carpoolspring.model.User;
import gle.carpoolspring.repository.RoleRepository;
import gle.carpoolspring.repository.UserRepository;
import gle.carpoolspring.service.VerificationService;

import jakarta.validation.Valid;
@Slf4j
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

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RefreshTokenService refreshTokenService;

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

        httpSession.setAttribute("userIdForSmsVerification", user.getIdUser());
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

    @PostMapping("/api/auth/signup")
    @ResponseBody
    public ResponseEntity<?> registerUserJwt(@Valid @RequestBody SignupRequest signupRequest) {
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use"));
        }

        // If your merged User has a 'username' field:
        // user.setUsername(signupRequest.getUsername());
        User user = new User();
        user.setUsername(signupRequest.getUsername()); // optional if you want a username
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));


        // Assign requested roles, or default to ROLE_USER
        Set<Role> roles = assignRoles(signupRequest.getRole());
        user.setRoles(roles);

        userRepository.save(user);

        // (Optional) send verification
        if (user.getTelephone() != null) {
            twilioVerifyService.sendVerificationCode(user.getTelephone());
        }
        verificationService.sendVerificationEmail(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully! Please verify your email/SMS."));
    }

    /**
     * Authenticate user via JSON request (JWT style).
     * POST /api/auth/signin
     */
    @PostMapping("/api/auth/signin")
    @ResponseBody
    public ResponseEntity<?> authenticateUserJwt(@Valid @RequestBody LoginRequest loginRequest) {
        // Here we assume user logs in with email as the 'username'
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(), // or loginRequest.getEmail() if you rename
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Our custom userDetails
        UserDetailsImp userDetails = (UserDetailsImp) authentication.getPrincipal();

        // Generate JWT
        String jwt = jwtUtils.generateJwtToken(userDetails);

        // Create refresh token
        var refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        // Collect roles
        List<String> roles = userDetails.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                new JwtResponse(jwt, refreshToken.getToken(),
                        userDetails.getId(),
                        userDetails.getUsername(),
                        userDetails.getEmail(),
                        roles)
        );
    }

    // Helper method to map string roles -> Role entities
    private Set<Role> assignRoles(Set<String> strRoles) {
        Set<Role> roles = new HashSet<>();
        if (strRoles == null || strRoles.isEmpty()) {
            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Error: 'ROLE_USER' not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(roleStr -> {
                String roleName = "ROLE_" + roleStr.toUpperCase();
                Role foundRole = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new RuntimeException("Error: " + roleName + " not found."));
                roles.add(foundRole);
            });
        }
        return roles;
    }
}
