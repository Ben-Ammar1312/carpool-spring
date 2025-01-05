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
import gle.carpoolspring.model.RefreshToken;
import gle.carpoolspring.model.Role;
import gle.carpoolspring.model.User;
import gle.carpoolspring.repository.RoleRepository;
import gle.carpoolspring.repository.UserRepository;
import gle.carpoolspring.request.JwtResponse;
import gle.carpoolspring.request.LoginRequest;
import gle.carpoolspring.request.MessageResponse;
import gle.carpoolspring.request.SignupRequest;
import gle.carpoolspring.service.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Autowired
    private UserService userService;
    @Value("${app.security.secure-cookies}")
    private boolean secureCookies;

    /**
     * Display the registration form (Thymeleaf view)
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User()); // Binding directly to User
        return "register";
    }

    /**
     * Handle registration form submission (Thymeleaf)
     */
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                               BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("error", "Email is already in use!");
            return "register";
        }

        // Encode the password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Handle file upload
        if (user.getPhotoFile() != null && !user.getPhotoFile().isEmpty()) {
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
                return "register";
            }
        }

        // Assign default role
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        user.setRoles(Collections.singleton(userRole));

        // Save the user
        userRepository.save(user);

        // Initiate verification processes
        httpSession.setAttribute("userIdForSmsVerification", user.getIdUser());
        twilioVerifyService.sendVerificationCode(user.getTelephone());
        verificationService.sendVerificationEmail(user);

        redirectAttributes.addFlashAttribute("success", "Registration successful! Please check your email to verify your account and verify your number by entering the sent SMS.");
        return "redirect:/verify-sms";
    }

    /**
     * Display the login form (Thymeleaf view)
     */
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    /**
     * Handle login form submission (Thymeleaf)
     * Since we are using JWT for all authentication, adjust this method to issue JWTs
     */
    @PostMapping("/login")
    public String authenticateUserWeb(@RequestParam("username") String username,
                                      @RequestParam("password") String password,
                                      RedirectAttributes redirectAttributes,
                                      HttpServletResponse response) {
        try {
            log.debug("Attempting authentication for user: {}", username);
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetailsImp userDetails = (UserDetailsImp) authentication.getPrincipal();

            String jwt = jwtUtils.generateJwtToken(userDetails);
            log.debug("Generated JWT for user {}: {}", username, jwt);
            // Create HttpOnly cookie for JWT
            Cookie jwtCookie = new Cookie("JWT-TOKEN", jwt);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setSecure(secureCookies); // Set to true in production with HTTPS
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(24 * 60 * 60); // 1 day
            response.addCookie(jwtCookie);
            log.debug("JWT-TOKEN cookie set for user {}", username);
            var refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
            log.debug("Refresh token created for user {}: {}", username, refreshToken.getToken());

            // Redirect to the desired page after login
            return "redirect:/search";
        } catch (Exception e) {
            log.error("Authentication failed: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Invalid username or password.");
            return "redirect:/login";
        }
    }

    /**
     * Authenticate user via JSON request (API)
     * POST /api/auth/signin
     */
    @PostMapping("/api/auth/signin")
    @ResponseBody
    public ResponseEntity<?> authenticateUserJwt(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        try {
            log.debug("Attempting authentication for user: {}", loginRequest.getUsername());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetailsImp userDetails = (UserDetailsImp) authentication.getPrincipal();
            log.debug("Authenticated user: {}", userDetails.getUsername());

            // Generate JWT
            String jwt = jwtUtils.generateJwtToken(userDetails);
            log.debug("Generated JWT: {}", jwt);

            // Create refresh token
            var refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
            log.debug("Created RefreshToken: {}", refreshToken.getToken());
            // Create HttpOnly cookie for JWT
            Cookie jwtCookie = new Cookie("JWT-TOKEN", jwt);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setSecure(secureCookies); // Set to true in production with HTTPS
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(24 * 60 * 60); // 1 day
            response.addCookie(jwtCookie);
            log.debug("JWT-TOKEN cookie set for user {}", userDetails.getUsername());

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
        } catch (Exception e) {
            log.error("Authentication failed: {}", e.getMessage());
            return ResponseEntity.status(401).body(new MessageResponse("Error: Unauthorized"));
        }
    }

    /**
     * Register a new user via JSON request (API)
     * POST /api/auth/signup
     */
    @PostMapping("/api/auth/signup")
    @ResponseBody
    public ResponseEntity<?> registerUserJwt(@Valid @RequestBody SignupRequest signupRequest) {
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use"));
        }

        // Create new user's account
        User user = new User();
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));

        // Assign roles, default to ROLE_USER
        Set<Role> roles = userService.assignRoles(signupRequest.getRole());
        user.setRoles(roles);

        userRepository.save(user);

        // Initiate verification processes
        userService.initiateVerification(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully! Please verify your email/SMS."));
    }

    /**
     * Verify Email
     */
    @GetMapping("/verify")
    public String verifyEmail(@RequestParam("token") String token, Model model) {
        boolean isVerified = verificationService.verifyEmailToken(token);
        if (isVerified) {
            model.addAttribute("success", "Email verified successfully! Please verify your SMS to activate your account.");
            return "verify-sms";
        } else {
            model.addAttribute("error", "Invalid or expired verification token.");
            return "register";
        }
    }

    /**
     * Verify SMS
     */
    @PostMapping("/verify-sms")
    public String verifySms(@RequestParam("code") String code, Model model) {
        Integer userId = (Integer) httpSession.getAttribute("userIdForSmsVerification");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isVerified = verificationService.verifySms(user.getTelephone(), code);
        if (isVerified) {
            user.setEnabled(true);
            userRepository.save(user);
            model.addAttribute("success", "SMS verified successfully! You can now log in.");
            return "login";
        } else {
            model.addAttribute("error", "Invalid SMS verification code.");
            return "verify-sms";
        }
    }

    /**
     * Display Verify SMS Page
     */
    @GetMapping("/verify-sms")
    public String showVerifySmsPage(Model model) {
        return "verify-sms";
    }

    /**
     * Logout user by clearing the JWT cookie
     */
    @PostMapping("/logout")
    @ResponseBody
    public ResponseEntity<?> logoutUser(@CookieValue(value = "JWT-TOKEN", required = false) String token, HttpServletResponse response) {
        if (token != null && refreshTokenService.findByToken(token).isPresent()) {
            RefreshToken refreshToken = refreshTokenService.findByToken(token).orElseThrow();
            refreshTokenService.deleteByToken(refreshToken.getToken());
        }

        // Clear the JWT cookie
        Cookie jwtCookie = new Cookie("JWT-TOKEN", null);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true); // Set to true in production with HTTPS
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0);
        response.addCookie(jwtCookie);

        return ResponseEntity.ok(new MessageResponse("You've been signed out!"));
    }
}