package hr.algebra.semregprojectbackend.service;


import hr.algebra.semregprojectbackend.domain.UserInfo;
import hr.algebra.semregprojectbackend.domain.UserRole;

import hr.algebra.semregprojectbackend.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

private final UserRepository repository;

    public UserDetailsServiceImpl(UserRepository repository) {
        this.repository = repository;
    }




    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserInfo user = this.repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Unknown user " + username));

        List<UserRole> userRoleList = user.getRoles();

        String[] roles = new String[userRoleList.size()];
        for (int i = 0; i < userRoleList.size(); i++) {
            roles[i] = userRoleList.get(i).getName();
        }

        return User.withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(roles)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }


}
