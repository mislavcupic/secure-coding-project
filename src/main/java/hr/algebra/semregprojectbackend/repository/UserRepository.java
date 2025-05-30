package hr.algebra.semregprojectbackend.repository;

import hr.algebra.semregprojectbackend.domain.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<UserInfo, Long> {
//    UserInfo findByUsername(String username);
 //   List<UserInfo> findByRoles_Name(String roleName);
    Optional<UserInfo> findByUsername(String username);

    Optional<UserInfo> findByUsernameAndRoles_Name(String username, String roleName);
}
