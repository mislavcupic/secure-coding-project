package hr.algebra.semregprojectbackend.service;

import hr.algebra.semregprojectbackend.domain.UserInfo;
import hr.algebra.semregprojectbackend.domain.UserRole;
import hr.algebra.semregprojectbackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserDetailsServiceImplTest {

    private UserRepository userRepository;
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setup() {
        userRepository = mock(UserRepository.class);
        userDetailsService = new UserDetailsServiceImpl(userRepository);
    }

    @Test
    void loadUserByUsername_returnsUserDetails() {
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername("testuser");
        userInfo.setPassword("password");

        UserRole role = new UserRole();
        role.setName("USER");
        userInfo.setRoles(List.of(role));

        when(userRepository.findByUsername("testuser")).thenReturn(userInfo);

        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        assertThat(userDetails.getUsername()).isEqualTo("testuser");
        assertThat(userDetails.getPassword()).isEqualTo("password");
        assertThat(userDetails.getAuthorities()).isNotEmpty();
    }

    @Test
    void loadUserByUsername_throwsWhenUserNotFound() {
        when(userRepository.findByUsername("nouser")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("nouser");
        });
    }
}

