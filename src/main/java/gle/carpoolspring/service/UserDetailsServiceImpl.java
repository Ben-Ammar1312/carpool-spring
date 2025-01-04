package gle.carpoolspring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import gle.carpoolspring.model.User;
import gle.carpoolspring.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * If your authentication is by email, then 'username' param is actually the email.
     * If your authentication is by username, then 'username' param is actually the username.
     * Or, handle both (try email first, fallback to username).
     */
    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        // Try to find user by email, otherwise by username
        User user = userRepository.findByEmail(usernameOrEmail)
                .orElseGet(() ->
                        userRepository.findByUsername(usernameOrEmail)
                                .orElseThrow(() ->
                                        new UsernameNotFoundException("User not found with email/username: " + usernameOrEmail)
                                )
                );

        return UserDetailsImp.build(user);
    }
}