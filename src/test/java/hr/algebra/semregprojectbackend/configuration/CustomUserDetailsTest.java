package hr.algebra.semregprojectbackend.configuration;

import hr.algebra.semregprojectbackend.domain.UserInfo;
import hr.algebra.semregprojectbackend.domain.UserRole;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomUserDetailsTest {

    @Test
    void testGetAuthorities_returnsCorrectRoles() {
        UserRole adminRole = new UserRole();
        adminRole.setName("ADMIN");
        UserRole userRole = new UserRole();
        userRole.setName("USER");

        UserInfo userInfo = new UserInfo();
        userInfo.setUsername("testuser");
        userInfo.setPassword("secret");
        userInfo.setRoles(List.of(adminRole, userRole));

        CustomUserDetails details = new CustomUserDetails(userInfo);

        var authorities = details.getAuthorities();

        assertEquals(2, authorities.size());
        assertTrue(authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        assertTrue(authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void testGetUsernameAndPassword() {
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername("username123");
        userInfo.setPassword("pass123");

        CustomUserDetails details = new CustomUserDetails(userInfo);

        assertEquals("username123", details.getUsername());
        assertEquals("pass123", details.getPassword());
    }

    @Test
    void testAccountNonExpiredNonLockedCredentialsNonExpiredEnabled() {
        UserInfo userInfo = new UserInfo();
        CustomUserDetails details = new CustomUserDetails(userInfo);

        assertTrue(details.isAccountNonExpired());
        assertTrue(details.isAccountNonLocked());
        assertTrue(details.isCredentialsNonExpired());
        assertTrue(details.isEnabled());
    }
}
