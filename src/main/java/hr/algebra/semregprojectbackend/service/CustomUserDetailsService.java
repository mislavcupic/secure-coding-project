package hr.algebra.semregprojectbackend.service;
import hr.algebra.semregprojectbackend.configuration.CustomUserDetails;
import hr.algebra.semregprojectbackend.domain.UserInfo;
import hr.algebra.semregprojectbackend.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository; // tvoj repo za UserInfo

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserInfo> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        UserInfo user = optionalUser.get();
        return new CustomUserDetails(user);
    }
}
